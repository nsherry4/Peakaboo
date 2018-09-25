package cyclops.visualization.template;

import cyclops.visualization.Surface;

public class RoundedRectangle implements SurfaceTemplate {

	private float x, y, width, height, xradius, yradius;
	
	public RoundedRectangle(float x, float y, float width, float height, float xradius, float yradius) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.xradius = Math.min(xradius, width/2f);
		this.yradius = Math.min(yradius, height/2f);
	}
	
	@Override
	public void apply(Surface s) {
		
		//top left
		s.moveTo(x, y+yradius);
		s.arcTo(x, y, xradius*2, yradius*2, 180, -90);
		
		//top right
		s.lineTo(x+width-xradius, y);
		s.arcTo(x+width-xradius*2, y, xradius*2, yradius*2, 90, -90);
		
		//bottom right
		s.lineTo(x+width, y+height-yradius);
		s.arcTo(x+width-xradius*2, y+height-yradius*2, xradius*2, yradius*2, 0, -90);
		
		//bottom left
		s.lineTo(x+xradius, y+height);
		s.arcTo(x, y+height-yradius*2, xradius*2, yradius*2, 270, -90);
		
		//join back to top left
		s.lineTo(x, y+yradius);
	}

	
}
