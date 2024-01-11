package org.peakaboo.framework.stratus.components.ui.colour;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.function.Function;

import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.framework.stratus.components.ui.colour.ColourView.Settings;

public class ColourStencil<T> extends Stencil<T> {

	private Function<T, Color> colourMapper;
	private ColourStencilView view;

	public ColourStencil(Function<T, Color> colourMapper) {
		this(Settings.defaults(), colourMapper);
	}
	
	public ColourStencil(Settings settings, Function<T, Color> colourMapper) {
		this.view = new ColourStencilView(Color.BLACK, settings);
		this.colourMapper = colourMapper;
		this.setLayout(new BorderLayout());
		this.add(this.view, BorderLayout.CENTER);
	}
	
	@Override
	protected void onSetValue(T value, boolean selected) {
		this.view.setColour(colourMapper.apply(value));
	}
	
	public static class ColourStencilView extends ColourView {
		
		public ColourStencilView(Color colour) {
			super(colour);
		}
		
		public ColourStencilView(Color colour, Settings settings) {
			super(colour, settings);
		}

		void setColour(Color colour) {
			this.colour = colour;
		}

		@Override
		protected void onMouseClick() {
			System.out.println("Ouch!");
		}
		
	}
	
}
