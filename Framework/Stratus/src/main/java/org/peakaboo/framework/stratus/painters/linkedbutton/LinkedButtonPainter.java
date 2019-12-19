package org.peakaboo.framework.stratus.painters.linkedbutton;

import java.awt.Container;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.controls.ButtonLinker;
import org.peakaboo.framework.stratus.painters.AbstractButtonPainter;
import org.peakaboo.framework.stratus.painters.ButtonPainter;
import org.peakaboo.framework.stratus.theme.Theme;

public class LinkedButtonPainter extends ButtonPainter {

    public LinkedButtonPainter(Theme theme, ButtonState... buttonStates) {
    	this(theme, 1, buttonStates);
    }
	
	public LinkedButtonPainter(Theme theme, int margin, ButtonState... buttonStates) {
		super(theme, margin, buttonStates);
	}

	private boolean isLinked(JComponent object) {
		Container container = object.getParent();
		return container instanceof ButtonLinker;
	}
	
	private boolean isFirst(JComponent object) {
		Container container = object.getParent();
		if (!isLinked(object)) {
			return false;
		}
		
		if (container.getComponentCount() <= 1) {
			return false;
		}
		
		return container.getComponent(0) == object;
		
	}
	
	private boolean isLast(JComponent object) {
		Container container = object.getParent();
		if (!isLinked(object)) {
			return false;
		}
		
		if (container.getComponentCount() <= 1) {
			return false;
		}
		
		return container.getComponent(container.getComponentCount()-1) == object;
		
	}
	

	private float getXOffset(JComponent object, float pad) {
		if (isFirst(object)) {
    		return 0;
    	}
    	if (isLast(object)) {
    		return -pad;
    	}
    	
    	return -pad;
	}
	
	private float getWOffset(JComponent object, float pad) {
		if (isFirst(object)) {
    		return pad;
    	}
    	if (isLast(object)) {
    		return pad;
    	}
    	
    	return pad*2;
	}
	
	@Override
	protected Shape fillShape(JComponent object, float width, float height, float pad) {
    	if (!isLinked(object)) {
    		return super.fillShape(object, width, height, pad);
    	}
    	
    	Area area;
    	float p = pad+1;
    	float x = p+getXOffset(object, pad);
    	float w = width-p*2+getWOffset(object, pad);
    	float h = height - p*2;
    	float y = p;
    	
		if (isFirst(object)) {
			area = new Area(new RoundRectangle2D.Float(x, y, w, h, radius, radius));
	    	area.add(new Area(new Rectangle2D.Float(x+radius, y, w-radius+1, h)));
		} else if (isLast(object)) {
			area = new Area(new RoundRectangle2D.Float(x, y, w, h, radius, radius));
	    	area.add(new Area(new Rectangle2D.Float(x, y, w-radius, h)));
		} else {
			area = new Area(new Rectangle2D.Float(x, y, w+1, h));
		}
		return area;

    	
    	
    }
    
	@Override
    protected Shape borderShape(JComponent object, float width, float height, float pad) {
		if (!isLinked(object)) {
			return super.borderShape(object, width, height, pad);
		}
		
		Area area;
		float x = pad+getXOffset(object, pad);
		float w = width-pad*2+getWOffset(object, pad);
		float h = height-pad*2;
		float y = pad;
		
		if (isFirst(object)) {
			area = new Area(new RoundRectangle2D.Float(x, y, w, h, radius, radius));
			area.add(new Area(new Rectangle2D.Float(x+radius, y, w-radius, h)));
		} else if (isLast(object)) {
			area = new Area(new RoundRectangle2D.Float(x, y, w, h, radius, radius));
			area.add(new Area(new Rectangle2D.Float(x, y, w-radius, h)));
		} else {
			area = new Area(new Rectangle2D.Float(x, y, w, h));
		}
		return area;

		

    }
    
    @Override
    protected Shape shadowShape(JComponent object, float width, float height, float pad) {
    	if (!isLinked(object)) {
    		return super.shadowShape(object, width, height, pad);
    	}
    	
    	GeneralPath path = new GeneralPath();
    	float y = (int)(height-(pad)*2);

    	if (isFirst(object)) {
    		path.moveTo(pad+2, y);
        	path.lineTo(width-(pad+1), y);
    	} else if (isLast(object)) {
    		path.moveTo(2, y);
        	path.lineTo(width-(pad+1)*2, y);
    	} else {
        	path.moveTo(pad+1, y);
        	path.lineTo(width-(pad)*2, y);
    	}
    	

    	return path;
    }
    
    @Override
    protected Shape bevelShape(JComponent object, float width, float height, float pad) {  	
    	if (!isLinked(object)) {
    		return super.bevelShape(object, width, height, pad);
    	}
    	
    	GeneralPath path = new GeneralPath();
    	if (isFirst(object)) {
    		path.moveTo(pad+2, pad+1);
    		path.lineTo(width-(pad-1)*2, pad+1);
    	} else if (isLast(object)) {
    		path.moveTo(pad, pad+1);
    		path.lineTo(width-(pad+1)*2, pad+1);
    	} else {
    		path.moveTo(pad, pad+1);
    		path.lineTo(width-(pad-1)*2, pad+1);
    	}
    	return path;
    }
    
    @Override
    protected Shape dashShape(JComponent object, float width, float height, float pad) {
    	if (!isLinked(object)) {
    		return super.dashShape(object, width, height, pad);
    	}
    	
    	float x = pad;
    	float w = width-pad*2-1;
    	Area area;
    	
    	if (isFirst(object)) {
    		area = new Area(new RoundRectangle2D.Float(x, pad, w, height-pad*2-1, radius, radius));
			area.add(new Area(new Rectangle2D.Float(x+radius, pad, w-radius, height-pad*2-1)));
    		return area;
    	} else if (isLast(object)) {
    		area = new Area(new RoundRectangle2D.Float(x, pad, w, height-pad*2-1, radius, radius));
			area.add(new Area(new Rectangle2D.Float(x, pad, w-radius, height-pad*2-1)));
    		return area;
    	} else {
    		return new Rectangle2D.Float(x, pad, w, height-pad*2-1);
    	}
    	
    }
	
}
