package org.peakaboo.ui.swing.plugins.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.logging.Level;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.ExtensionPointRegistry;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.bolt.repository.IssuePluginRepository;
import org.peakaboo.framework.bolt.repository.PluginMetadata;
import org.peakaboo.framework.bolt.repository.PluginRepositoryException;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.ComponentStrip;
import org.peakaboo.framework.stratus.components.stencil.StencilCellEditor;
import org.peakaboo.framework.stratus.components.stencil.StencilTableCellRenderer;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.plugins.PluginPanel.HeaderControlProvider;
import org.peakaboo.ui.swing.plugins.PluginsController;

public class PluginRepositoryBrowser extends JPanel implements HeaderControlProvider {
    private PluginTableModel pluginTableModel;
    private JTable pluginTable;

    private PluginsController controller;
    private ComponentStrip headerControls;
    private JComboBox<SortOrder> sortOrder;

    // Track plugins that are currently being downloaded/installed
    private java.util.Set<String> pluginsInProgress = new java.util.HashSet<>();
        
    public enum SortOrder {
		NAME, KIND, SOURCE, INSTALLED;

		@Override
		public String toString() {
			return switch (this) {
				case NAME -> "Sort by Name";
				case KIND -> "Sort by Kind";
				case SOURCE -> "Sort by Source";
				case INSTALLED -> "Sort by Installed";
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
        TableCellRenderer stencilRenderer = new StencilTableCellRenderer<>(
        		new PluginRepositoryListItemStencil(
        				controller,
        				this::handleInstall,
        				this::handleRemove,
        				this::handleUpgrade,
        				this::handleIssue,
        				this::isPluginInProgress
        			), pluginTable);
        TableCellEditor stencilEditor = new StencilCellEditor<>(
        		new PluginRepositoryListItemStencil(
        				controller,
        				this::handleInstall,
        				this::handleRemove,
        				this::handleUpgrade,
        				this::handleIssue,
        				this::isPluginInProgress
        			));
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
        
        // Don't show the sort order control yet
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
    	
    	List<PluginMetadata> sorted = pluginTableModel.getPlugins();
    	ExtensionPointRegistry reg = Tier.provider().getExtensionPoints();
    	
    	// Sort by name first so that items of the same rank are always in the same order
		sorted = sorted.stream()
				.sorted((p1, p2) -> p1.name.compareToIgnoreCase(p2.name))
				.toList();
    	
    	switch (order) {
			case NAME:
				sorted = sorted.stream()
						.sorted((p1, p2) -> p1.name.compareToIgnoreCase(p2.name))
						.toList();
				break;
			case KIND:
				sorted = sorted.stream()
						.sorted((p1, p2) -> p1.category.compareToIgnoreCase(p2.category))
						.toList();
				break;
			case SOURCE:
				sorted = sorted.stream()
						.sorted((p1, p2) -> {
							String name1 = p1.sourceRepository().getRepositoryName();
							String name2 = p2.sourceRepository().getRepositoryName();
							return name1.compareToIgnoreCase(name2);
						}).toList();
				break;
				
			case INSTALLED:
				sorted = sorted.stream()
						.sorted((p1, p2) -> -Boolean.compare(
								reg.getByUUID(p1.uuid).isPresent(), 
								reg.getByUUID(p2.uuid).isPresent()
							))
						.toList();
		}
    	
    	sorted = sorted.stream().sorted(
    			(p1, p2) -> -Boolean.compare(hasNotification(p1), hasNotification(p2))
    		).toList();
    	
    	pluginTableModel.setPlugins(sorted);
    	
	}

    public static boolean hasNotification(PluginMetadata p) {
    	ExtensionPointRegistry reg = Tier.provider().getExtensionPoints();
    	return 	p.getUpgradeTarget(reg).isPresent() || 
    			p.category.equals(IssuePluginRepository.CATEGORY);
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
                    Set<String> interfaceNames = Tier.provider().getExtensionPoints().getInterfaceNames();
                    interfaceNames.add(IssuePluginRepository.CATEGORY); // Custom type for issues, rather than plugins
                    // Filter out plugins that do not implement an interface that we support
                    List<PluginMetadata> filteredPlugins = new ArrayList<>();
                    for (PluginMetadata plugin : plugins) {
                    	if (interfaceNames.contains(plugin.category)) {
                    		filteredPlugins.add(plugin);
                    	} else {
							OneLog.log(Level.WARNING, "Plugin " + plugin.name + " is for unsupported catagory " + plugin.category);
                    	}
                    }
                    plugins = filteredPlugins;
                    pluginTableModel.setPlugins(plugins);
                    sortTable();
                } catch (PluginRepositoryException | ExecutionException ex) {
                	controller.showError("Failed to load plugins: " + ex.getMessage());
                } catch (InterruptedException ex) {
					Thread.currentThread().interrupt(); // Restore interrupted status
					controller.showError("Plugin loading was interrupted.");
				}
            }
            
        }.execute();
    }

    private void handleInstall(PluginMetadata meta) {
    	// Mark as in progress and refresh the UI
    	pluginsInProgress.add(meta.uuid);
    	refreshTableDisplay();

    	// Download in background to avoid blocking the UI
		new javax.swing.SwingWorker<java.io.File, Void>() {
			private Exception exception;

			@Override
			protected java.io.File doInBackground() {
				try {
					return meta.download().orElseThrow(() ->
						new NoSuchElementException("Failed to download plugin: " + meta.name));
				} catch (Exception ex) {
					this.exception = ex;
					return null;
				}
			}

			@Override
			protected void done() {
				try {
					if (exception != null) {
						throw exception;
					}
					java.io.File pluginFile = get();
					if (pluginFile != null) {
						controller.install(pluginFile, true);
					}
				} catch (NoSuchElementException ex) {
					controller.showError("Install Error", "Failed to download plugin: " + meta.name);
				} catch (Exception ex) {
					controller.showError("Install Error", "Failed to install plugin: " + ex.getMessage());
				} finally {
					// Mark as complete and refresh the UI
					pluginsInProgress.remove(meta.uuid);
					refreshTableDisplay();
				}
			}
		}.execute();
    }
    
    private void handleRemove(PluginMetadata meta) {
    	var maybePlugin = Tier.provider().getExtensionPoints().getByUUID(meta.uuid);
        if (maybePlugin.isPresent()) {
        	PluginDescriptor<? extends BoltPlugin> plugin = maybePlugin.get();
        	this.controller.remove((PluginDescriptor<BoltPlugin>) plugin, true);
        } else {
        	controller.showError("Failed to remove plugin: " + meta.name);
        }
    }
    
    private void handleUpgrade(PluginMetadata meta) {
        var maybePlugin = Tier.provider().getExtensionPoints().getByUUID(meta.uuid);
        if (maybePlugin.isPresent()) {
        	PluginDescriptor<? extends BoltPlugin> plugin = maybePlugin.get();

        	// Mark as in progress and refresh the UI
        	pluginsInProgress.add(meta.uuid);
        	refreshTableDisplay();

        	// Download in background to avoid blocking the UI
        	new javax.swing.SwingWorker<java.io.File, Void>() {
        		private Exception exception;

        		@Override
        		protected java.io.File doInBackground() {
        			try {
        				return meta.download().orElseThrow(() ->
        					new NoSuchElementException("Failed to download plugin upgrade: " + meta.name));
        			} catch (Exception ex) {
        				this.exception = ex;
        				return null;
        			}
        		}

        		@Override
        		protected void done() {
        			try {
        				if (exception != null) {
        					throw exception;
        				}
        				java.io.File pluginFile = get();
        				if (pluginFile != null) {
        					// Perform the upgrade with the downloaded file
        					controller.upgradeFromFile((PluginDescriptor<BoltPlugin>) plugin, meta, pluginFile, true);
        				}
        			} catch (NoSuchElementException ex) {
        				controller.showError("Upgrade Error", "Failed to download upgrade: " + meta.name);
        			} catch (Exception ex) {
        				controller.showError("Upgrade Error", "Failed to upgrade plugin: " + ex.getMessage());
        			} finally {
        				// Mark as complete and refresh the UI
        				pluginsInProgress.remove(meta.uuid);
        				refreshTableDisplay();
        			}
        		}
        	}.execute();
        } else {
        	controller.showError("Upgrade Error", "Failed to find plugin: " + meta.name);
        }
    }
    
    private void handleIssue(BoltIssue<? extends BoltPlugin> issue) {
    	controller.fixIssue(issue);
    }

    private boolean isPluginInProgress(PluginMetadata meta) {
    	return pluginsInProgress.contains(meta.uuid);
    }

    // Refresh just the table display without reloading data
    private void refreshTableDisplay() {
    	pluginTableModel.fireTableDataChanged();
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