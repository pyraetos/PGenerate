package net.pyraetos.pgenerate;

import java.awt.Color;

public class TerrainMap extends PixelMap {

	public TerrainMap(PGenerate pg) {
		super(pg);
	}
	

	@Override
	public Color valueToColor(double value) {
		if(value == 1000) return Color.MAGENTA;
		if(value <= -0.5) return Color.BLUE;
		if(value < -0.25) return new Color(255, 250, 205);
		if(value < 1.25) return new Color(76, 187, 23);
		if(value < 2.25)	return new 	Color(0, 100, 0);
		if(value >= 2.25) return Color.BLUE;
		return null;
	}
}
