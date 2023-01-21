package org.peakaboo.framework.cyclops.visualization.descriptor;

public abstract class AbstractSurfaceDescriptor implements SurfaceDescriptor {

	private String title, desc, ext;
	
	public AbstractSurfaceDescriptor(String title, String desc, String ext) {
		this.title = title;
		this.desc = desc;
		this.ext = ext;
	}

	@Override
	public String title() {
		return title;
	}

	@Override
	public String description() {
		return desc;
	}

	@Override
	public String extension() {
		return ext;
	}

}
