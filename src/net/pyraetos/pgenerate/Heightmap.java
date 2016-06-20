package net.pyraetos.pgenerate;

import java.awt.Color;

public class Heightmap extends PixelMap {

	public Heightmap(PGenerate pg) {
		super(pg);
	}
	

	@Override
	public Color valueToColor(double value){
		if(value <= -3) return Color.BLACK;
		if(value >= 3) return Color.WHITE;
		int c = (int)Math.round((value + 3) * (255d / 6d));
		return new Color(c, c, c);
	}
}
