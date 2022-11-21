package org.peakaboo.framework.stratus.api.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * TreeModel which takes a list of items and a key function and groups the items
 * by key.
 * 
 * @author NAS
 */
public class GroupedListTreeModel<T> extends DefaultTreeModel {
	
	private List<T> items;
	private DefaultMutableTreeNode rootNode;
	private Function<T, String> groupBy;
	
	public GroupedListTreeModel(List<T> items, Function<T, String> groupBy) {
		super(new DefaultMutableTreeNode());
		this.items = items;
		this.groupBy = groupBy;
		build();
	}
	
	private void build() {
		rootNode = new DefaultMutableTreeNode();
		
		//Discover group names
		Set<String> groups = new HashSet<>();
		for (T item : items) {
			groups.add(this.groupBy.apply(item));
		}
		
		//build 2-level tree
		for (String group : groups) {
			DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group);
			rootNode.add(groupNode);
			
			for (T item : items) {
				if (group.equals(this.groupBy.apply(item))) {
					DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(item);
					groupNode.add(itemNode);
				}
			}
			
		}
		
		super.setRoot(rootNode);
		
	}
	
	public T getItem(TreePath path) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		try {
			return (T) node.getUserObject();
		} catch (ClassCastException e) {
			return null;
		}
		
	}
	



}
