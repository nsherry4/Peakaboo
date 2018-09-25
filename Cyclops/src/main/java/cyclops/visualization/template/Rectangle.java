package cyclops.visualization.template;

import cyclops.visualization.Surface;

public class Rectangle implements SurfaceTemplate {

	private float x, y, width, height;
	
	public Rectangle(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void apply(Surface s) {
		s.moveTo(x, y);
		s.lineTo(x, (y + height));
		s.lineTo((x + width), (y + height));
		s.lineTo((x + width), y);
		s.lineTo(x, y);
	}

	
	
}
