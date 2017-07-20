package net.pyraetos.pgenerate;

import java.awt.Color;
import java.util.Random;

import net.pyraetos.util.Sys;

public class ScorpionMap extends MultiLayerMap {

	public static final Color OCP_BROWN = new Color(121, 84, 60);
	public static final Color OCP_LIGHT_GREEN = new Color(143, 143, 95);
	public static final Color OCP_GREEN = new Color(69, 107, 79);
	public static final Color OCP_DARK_BROWN = new Color(56, 39, 54);
	public static final Color OCP_GRAY = new Color(170, 172, 170);
	public static final Random r = new Random();
	
	public ScorpionMap(int width, int height) {
		super(width, height, 4);
	}

	private Color getBrownGradient(int x, int y){
		double pB = Math.sin(.008*(((double)y) + r.nextDouble()))/2f +.5f;
		int bR = (int)(121f * pB);
		int bG = (int)(84f * pB);
		int bB = (int)(60f * pB);
		double pG = 1d-pB;
		int gR = (int)(176f * pG);
		int gG = (int)(162f * pG);
		int gB = (int)(153f * pG);
		return new Color(bR+gR,bG+gG,bB+gB);
	}
	
	@Override
	protected Color colorForLayer(int layer, int i, int j){
		switch(layer){
		case(0): return getBrownGradient(j, i);
		case(1): if(pg.getValue(i, j) > .75) return OCP_LIGHT_GREEN;
		case(2): if(pg.getValue(i, j) > 1) return OCP_GREEN;		
		case(3): if(pg.getValue(i, j) > 1.35) return OCP_DARK_BROWN;		
		case(4): if(pg.getValue(i, j) > 2) return OCP_GRAY;			
		}
		return null;
	}
	
}
