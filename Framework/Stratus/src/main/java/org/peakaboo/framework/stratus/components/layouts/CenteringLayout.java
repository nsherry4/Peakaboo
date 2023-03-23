package org.peakaboo.framework.stratus.components.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * Centres a single component without expanding it like BorderLayout would.
 * 
 * @author NAS
 *
 */
public class CenteringLayout implements LayoutManager {

	private Component child(Container container) {
		var children = container.getComponents();
		if (children.length == 0) return null;
		return children[0];
	}
	
	@Override
	public void addLayoutComponent(String name, Component component) {
		//NOOP
	}
	
	@Override
	public void removeLayoutComponent(Component component) {
		//NOOP
	}

	@Override
	public Dimension minimumLayoutSize(Container container) {
		var component = child(container);
		if (component == null) return new Dimension(0,0);
		return component.getMinimumSize();
	}

	@Override
	public Dimension preferredLayoutSize(Container container) {
		var component = child(container);
		if (component == null) return new Dimension(0,0);
		return component.getPreferredSize();
	}

	@Override
	public void layoutContainer(Container container) {
		
		var children = container.getComponents();
		if (children.length == 0) return;
		var child = children[0];
		
		var csize = container.getSize();
		int cw = csize.width;
		int ch = csize.height;
		
		var psize = child.getPreferredSize();
		int pw = psize.width;
		int ph = psize.height;
		
		var msize = child.getMinimumSize();
		int mw = msize.width;
		int mh = msize.height;
		
		
		int x, w;
		if (cw >= pw) {
			w = pw;
			x = (cw - pw) / 2;
		} else if (cw < pw && cw >= mw) {
			w = cw;
			x = 0;
		} else {
			w = mw;
			x = 0;
		}
		
		
		int y, h;
		if (ch >= ph) {
			h = ph;
			y = (ch - ph) / 2;
		} else if (ch < ph && ch >= mh) {
			h = ch;
			y = 0;
		} else {
			h = mh;
			y = 0;
		}
		
		
		child.setBounds(x, y, w, h);
		
	}



}
