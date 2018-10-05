package peakaboo.ui.swing.plugins;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;
import peakaboo.datasink.plugin.JavaDataSinkPlugin;
import peakaboo.datasource.plugin.JavaDataSourcePlugin;
import peakaboo.filter.plugins.JavaFilterPlugin;
import peakaboo.mapping.calibration.CalibrationReference;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;

public class PluginTreeRenderer extends DefaultTreeCellRenderer {
	
	
	private Color textColour;
	private Color selColour;
	JLabel widget;

	
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel,
            boolean expanded,
            boolean leaf, int row,
            boolean hasFocus) {
    	
    	
    	if (widget == null) {
    		widget = new JLabel();
    		widget.setBorder(Spacing.bSmall());
    		textColour = widget.getForeground();
    		selColour = new Color(UIManager.getColor("stratus-highlight-text").getRGB());
    		if (selColour == null) {
    			selColour = Color.WHITE;
    		}

    	}
    	
    	DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    	Object object = node.getUserObject();
    	if (object instanceof String) {
    		String string = object.toString();
    		widget.setIcon(StockIcon.PLACE_FOLDER.toImageIcon(IconSize.BUTTON));
    		widget.setText(string);
    	} else {
        	BoltPluginPrototype<? extends BoltPlugin> plugin = (BoltPluginPrototype<? extends BoltPlugin>)object;
        	widget.setText(plugin.getName());
        	widget.setIcon(getIcon(plugin));	
    	}
    	
    	if (sel) {
    		widget.setForeground(selColour);
    	} else {
    		widget.setForeground(textColour);
    	}
    	
    	return widget;
    	
    }
    
	private ImageIcon getIcon(BoltPluginPrototype<? extends BoltPlugin> plugin) {
		Class<? extends BoltPlugin> pluginBaseClass = plugin.getPluginClass();
		
		if (pluginBaseClass == JavaDataSourcePlugin.class) {
			return StockIcon.DOCUMENT_IMPORT.toImageIcon(IconSize.BUTTON);
		}
		
		if (pluginBaseClass == JavaDataSinkPlugin.class) {
			return StockIcon.DOCUMENT_EXPORT.toImageIcon(IconSize.BUTTON);
		}
		
		if (pluginBaseClass == JavaFilterPlugin.class) {
			return StockIcon.MISC_EXECUTABLE.toImageIcon(IconSize.BUTTON);
		}
		
		if (pluginBaseClass == CalibrationReference.class) {
			return IconFactory.getImageIcon("calibration", IconSize.BUTTON);
		}
		
		return null;
	}

}
