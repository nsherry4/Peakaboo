package org.peakaboo.ui.swing.plugins.browser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.stencil.StencilCellEditor;
import org.peakaboo.framework.stratus.components.stencil.StencilTableCellRenderer;
import org.peakaboo.ui.swing.plugins.PluginsController;

public class PluginRepositoryBrowser extends JPanel {
    private PluginRepository repository;
    private PluginTableModel pluginTableModel;
    private JTable pluginTable;

    private PluginsController controller;
    
    public PluginRepositoryBrowser(PluginsController controller, PluginRepository repository) {
        super(new BorderLayout());
        this.controller = controller;
        this.repository = repository;
        
        this.pluginTableModel = new PluginTableModel();
        this.pluginTable = new JTable(pluginTableModel);
        this.pluginTable.setTableHeader(null);
        this.pluginTable.setRowHeight(80);
        this.pluginTable.setPreferredScrollableViewportSize(new Dimension(600, 400));
        TableCellRenderer stencilRenderer = new StencilTableCellRenderer<>(new PluginRepositoryListItemStencil(this::handleDownload, this::handleRemove, this::handleUpgrade), pluginTable);
        TableCellEditor stencilEditor = new StencilCellEditor<>(new PluginRepositoryListItemStencil(this::handleDownload, this::handleRemove, this::handleUpgrade));
        pluginTable.getColumnModel().getColumn(0).setCellRenderer(stencilRenderer);
        pluginTable.getColumnModel().getColumn(0).setCellEditor(stencilEditor);
        pluginTable.setRowSelectionAllowed(false);
        pluginTable.setColumnSelectionAllowed(false);
        pluginTable.setShowGrid(false);
        pluginTable.setIntercellSpacing(new Dimension(0, 0));
        pluginTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scroller = Stratus.scrolled(pluginTable);
        this.add(scroller, BorderLayout.CENTER);
        loadPlugins();
        
        this.controller.addListener(() -> {
        	// Submit this repaint on the Swing main thread
        	javax.swing.SwingUtilities.invokeLater(() -> {
                loadPlugins();
			});

        });
    }

    private void loadPlugins() {
        new javax.swing.SwingWorker<List<PluginMetadata>, Void>() {
        	
            private PluginRepositoryException exception;
        	
            @Override
            protected List<PluginMetadata> doInBackground() {
                try {
                    return repository.listAvailablePlugins();
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
        InputStream downloadStream = repository.downloadPlugin(meta);
        this.controller.downloadPluginFile(downloadStream);
    }
    
    private void handleRemove(PluginMetadata meta) {
        // Get the registry
        var reg = DataSourceRegistry.system();
        var maybePlugin = reg.getByUUID(meta.uuid);
        if (maybePlugin.isPresent()) {
        	PluginDescriptor<? extends BoltPlugin> plugin = maybePlugin.get();
        	this.controller.remove((PluginDescriptor<BoltPlugin>) plugin);
        } else {
        	JOptionPane.showMessageDialog(this, "Failed to remove plugins: " + meta.name, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleUpgrade(PluginMetadata meta) {
    	// TODO
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
        @Override public boolean isCellEditable(int row, int col) { return true; }
    }
}