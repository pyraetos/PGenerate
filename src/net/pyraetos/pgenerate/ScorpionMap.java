package net.pyraetos.pgenerate;

import java.awt.Color;

import net.pyraetos.util.Sys;

public class ScorpionMap extends MultiLayerMap {

	public static final Color OCP_BROWN = new Color(121, 84, 60);
	public static final Color OCP_LIGHT_GREEN = new Color(143, 143, 95);
	public static final Color OCP_GREEN = new Color(69, 107, 79);
	public static final Color OCP_DARK_BROWN = new Color(56, 39, 54);
	public static final Color OCP_GRAY = new Color(170, 172, 170);
	
	public ScorpionMap(int width, int height) {
		super(width, height);
	}

	private int getBrownGradient(int x, int y){
		double pB = Math.sin(.008*y)/2f +.5f;
		int bR = (int)(121f * pB);
		int bG = (int)(84f * pB);
		int bB = (int)(60f * pB);
		double pG = 1d-pB;
		int gR = (int)(206f * pG);
		int gG = (int)(182f * pG);
		int gB = (int)(173f * pG);
		return new Color(bR+gR,bG+gG,bB+gB).getRGB();
	}
	
	@Override
	public void layer(int layer){
		if(layer > 4) return;
		layer();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				switch(layer){
				case(0): pixels[i * height + j] = getBrownGradient(j, i); break;
				case(1): if(pg.getValue(i, j) > 1.) pixels[i * height + j] = OCP_LIGHT_GREEN.getRGB(); break;
				case(2): if(pg.getValue(i, j) > 1.25) pixels[i * height + j] = OCP_GREEN.getRGB(); break;			
				case(3): if(pg.getValue(i, j) > 1.5) pixels[i * height + j] = OCP_DARK_BROWN.getRGB(); break;			
				case(4): if(pg.getValue(i, j) > 2) pixels[i * height + j] = OCP_GRAY.getRGB(); break;			
				}
			}
		}
		layer(++layer);
	}
}
