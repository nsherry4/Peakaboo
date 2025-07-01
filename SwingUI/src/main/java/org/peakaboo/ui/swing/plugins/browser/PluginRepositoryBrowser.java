package org.peakaboo.ui.swing.plugins.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.repository.PluginMetadata;
import org.peakaboo.framework.bolt.repository.PluginRepository;
import org.peakaboo.framework.bolt.repository.PluginRepositoryException;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.ComponentStrip;
import org.peakaboo.framework.stratus.components.stencil.StencilCellEditor;
import org.peakaboo.framework.stratus.components.stencil.StencilTableCellRenderer;
import org.peakaboo.ui.swing.plugins.PluginPanel.HeaderControlProvider;
import org.peakaboo.ui.swing.plugins.PluginsController;

public class PluginRepositoryBrowser extends JPanel implements HeaderControlProvider {
    private PluginTableModel pluginTableModel;
    private JTable pluginTable;

    private PluginsController controller;
    private ComponentStrip headerControls;
    private JComboBox<SortOrder> sortOrder;
    
    public enum SortOrder {
		NAME, KIND, SOURCE;

		@Override
		public String toString() {
			return switch (this) {
				case NAME -> "Sort by Name";
				case KIND -> "Sort by Kind";
				case SOURCE -> "Sort by Source";
			};
		}
	}
    
    public PluginRepositoryBrowser(PluginsController controller) {
        super(new BorderLayout());
        this.controller = controller;
        
        this.pluginTableModel = new PluginTableModel();
        this.pluginTable = new JTable(pluginTableModel);
        this.pluginTable.setTableHeader(null);
        this.pluginTable.setRowHeight(80);
        this.pluginTable.setPreferredScrollableViewportSize(new Dimension(600, 400));
        TableCellRenderer stencilRenderer = new StencilTableCellRenderer<>(new PluginRepositoryListItemStencil(controller, this::handleDownload, this::handleRemove, this::handleUpgrade), pluginTable);
        TableCellEditor stencilEditor = new StencilCellEditor<>(new PluginRepositoryListItemStencil(controller, this::handleDownload, this::handleRemove, this::handleUpgrade));
        pluginTable.getColumnModel().getColumn(0).setCellRenderer(stencilRenderer);
        pluginTable.getColumnModel().getColumn(0).setCellEditor(stencilEditor);
        pluginTable.setRowSelectionAllowed(false);
        pluginTable.setColumnSelectionAllowed(false);
        pluginTable.setShowGrid(false);
        pluginTable.setIntercellSpacing(new Dimension(0, 0));
        pluginTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scroller = Stratus.scrolled(pluginTable);
        this.add(scroller, BorderLayout.CENTER);
        
        // Refresh the plugin table and set up the controller listener so it updates automatically on changes
        refreshTable();
        this.controller.addListener(() -> refreshTable());
        
        // Set up the header sort controls
        sortOrder = new JComboBox<>(SortOrder.values());
        sortOrder.addActionListener(e -> sortTable());
        
        headerControls = new ComponentStrip(sortOrder);
        
    }
    
    // Refresh the plugin table by reloading and resorting the plugins from the repository
    // This method executes on the Event Dispatch Thread (EDT) to ensure the UI updates correctly
    // and without blocking the UI.
    private void refreshTable() {
    	javax.swing.SwingUtilities.invokeLater(() -> loadPlugins());
    }
    
    
    private void sortTable() {
    	SortOrder order = (SortOrder) sortOrder.getSelectedItem();
    	
    	switch (order) {
			case NAME:
				pluginTableModel.setPlugins(pluginTableModel.getPlugins().stream()
						.sorted((p1, p2) -> p1.name.compareToIgnoreCase(p2.name))
						.toList());
				break;
			case KIND:
				pluginTableModel.setPlugins(pluginTableModel.getPlugins().stream()
						.sorted((p1, p2) -> p1.category.compareToIgnoreCase(p2.category))
						.toList());
				break;
			case SOURCE:
				sortTableBySource();
				break;
		}
	}

	private void sortTableBySource() {
 			
		var sortedPlugins = pluginTableModel.getPlugins().stream()
				.sorted((p1, p2) -> {
					Optional<PluginRepository> repo1 = controller.getRepositoryByUrl(p1.repositoryUrl);
					Optional<PluginRepository> repo2 = controller.getRepositoryByUrl(p2.repositoryUrl);
					String name1 = "Unknown";
					String name2 = "Unknown";
					if (repo1.isPresent()) {
						name1 = repo1.get().getRepositoryName();
					}
					if (repo2.isPresent()) {
						name2 = repo2.get().getRepositoryName();
					}
					return name1.compareToIgnoreCase(name2);
				}).toList();
		pluginTableModel.setPlugins(sortedPlugins);
			
    }

	// Clear the current plugins and reload them, sorting them afterwards
    private void loadPlugins() {
        new javax.swing.SwingWorker<List<PluginMetadata>, Void>() {
        	
            private PluginRepositoryException exception;
        	
            @Override
            protected List<PluginMetadata> doInBackground() {
                try {
                    return controller.getRepository().listAvailablePlugins();
                } catch (PluginRepositoryException ex) {
                    // Pass exception to done()
                    this.exception = ex;
                    return List.of();
                }
            }
                        
            @Override
            protected void done() {
                try {
                    if (exception != null) {
                        throw exception;
                    }
                    List<PluginMetadata> plugins = get();
                    pluginTableModel.setPlugins(plugins);
                    sortTable();
                } catch (PluginRepositoryException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(PluginRepositoryBrowser.this, "Failed to load plugins: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (InterruptedException ex) {
					Thread.currentThread().interrupt(); // Restore interrupted status
					JOptionPane.showMessageDialog(PluginRepositoryBrowser.this, "Plugin loading was interrupted.", "Error", JOptionPane.ERROR_MESSAGE);
				}
            }
            
        }.execute();
    }

    private void handleDownload(PluginMetadata meta) {
        // Get the download stream from the repository
        InputStream downloadStream = controller.getRepository().downloadPlugin(meta);
        File tempFile = this.controller.download(downloadStream);
        if (tempFile == null) {
			JOptionPane.showMessageDialog(this, "Failed to download plugin: " + meta.name, "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
        this.controller.install(tempFile);
    }
    
    private void handleRemove(PluginMetadata meta) {
        var maybePlugin = DataSourceRegistry.system().getByUUID(meta.uuid);
        if (maybePlugin.isPresent()) {
        	PluginDescriptor<? extends BoltPlugin> plugin = maybePlugin.get();
        	this.controller.remove((PluginDescriptor<BoltPlugin>) plugin);
        } else {
        	JOptionPane.showMessageDialog(this, "Failed to remove plugins: " + meta.name, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleUpgrade(PluginMetadata meta) {
        var maybePlugin = DataSourceRegistry.system().getByUUID(meta.uuid);
        if (maybePlugin.isPresent()) {
        	PluginDescriptor<? extends BoltPlugin> plugin = maybePlugin.get();
        	this.controller.upgrade((PluginDescriptor<BoltPlugin>) plugin, meta, true);
        } else {
        	JOptionPane.showMessageDialog(this, "Failed to remove plugins: " + meta.name, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
       
    // Table model for plugins
    private static class PluginTableModel extends AbstractTableModel {
        private List<PluginMetadata> plugins = List.of();
        private final String[] columns = {"Plugin"};
        @Override public int getRowCount() { return plugins.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }
        @Override public Object getValueAt(int row, int col) {
            return plugins.get(row);
        }
        public void setPlugins(List<PluginMetadata> plugins) {
            this.plugins = plugins != null ? plugins : List.of();
            fireTableDataChanged();
        }
        public List<PluginMetadata> getPlugins() {
			return List.copyOf(plugins);
		}
        @Override public boolean isCellEditable(int row, int col) { return true; }
    }

	@Override
	public ComponentStrip getHeaderControls() {
		return headerControls;
	}
}