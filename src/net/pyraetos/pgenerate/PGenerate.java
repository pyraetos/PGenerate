package net.pyraetos.pgenerate;

import java.awt.Image;
import java.util.LinkedList;
import java.util.Random;

import net.pyraetos.util.Images;
import net.pyraetos.util.Point;
import net.pyraetos.util.Sys;

public abstract class PGenerate{

	private static LinkedList<LinkedList<Double>> tr = new LinkedList<LinkedList<Double>>();
	private static long seed = Sys.randomSeed();
	private static int offsetX;
	private static int offsetY;
	private static double s = 1.0d;
	public static final byte WATER = 0;
	public static final byte SAND = 1;
	public static final byte GRASS = 2;
	public static final byte TREE = 3;
	public static final byte NULL = -128;
	
	public static void setSeed(long seed){
		PGenerate.seed = seed;
	}
	
	public static long getSeed(){
		return seed;
	}

	public static void setEntropy(double s){
		PGenerate.s = s;
	}

	/**
	 * Generates a single tile using the Pyraetos algorithm.
	 * @author Pyraetos
	 */
	public static void pgenerate(int x, int y){
		double value = 0d;
		for(int i = x - 1; i <= x + 1; i++)
			for(int j = y - 1; j <= y + 1; j++)
				value += micro(i, j);
		setTileDouble(x, y, value / 9d);
		//Good place to add mobs and rare objects
		/*if(Sys.chance(.0005d)){
			
		}
		*/
	}
	
	private static double micro(int x, int y){
		Random random = new Random();
		double value = macro(x, y);
		for(int i = x - 4; i <= x + 4; i++){
			for(int j = y - 4; j <= y + 4; j++){
				random.setSeed(seed * 17717171L + i * 22222223L + j * 111181111L);
				double h = (x == i && y == j) ? 1 : Math.sqrt(Math.pow(x - i, 2) + Math.pow(y - j, 2));
				value += (random.nextGaussian() * s) / (3d * h);
			}
		}
		return value;
	}
	
	private static double macro(int a, int b){
		int x = a < 0 ? a / 16 - 1 : a / 16;
		int y = b < 0 ? b / 16 - 1 : b / 16;
		Random random = new Random();
		double value = 2d;
		for(int i = x - 4; i <= x + 4; i++){
			for(int j = y - 4; j <= y + 4; j++){
				random.setSeed(seed * 17717171L + i * 22222223L + j * 111181111L);
				double h = (x == i && y == j) ? 1 : Math.sqrt(Math.pow(x - i, 2) + Math.pow(y - j, 2));
				value += (random.nextGaussian() * s) / (3d * h);
			}
		}
		return value;
	}
	
	public static byte getTileByte(int x, int y){
		byte b = (byte)Math.floor(getTileDouble(x, y));
		if(b == NULL) return NULL;
		if(b >= TREE) return TREE;
		if(b <= WATER) return WATER;
		return b;
	}
	
	public static byte getTileByte(Point point){
		return getTileByte(point.getX(), point.getY());
	}

	public static double getTileDouble(int x, int y){
		try{
			return tr.get(x + offsetX).get(y + offsetY);
		}catch(Exception e){
			return NULL;
		}
	}

	public static void setTileByte(int x, int y, byte type){
		setTileDouble(x, y, (double)type);
	}
	
	public static void setTileDouble(int x, int y, double d){
		int dox = -x > offsetX ? -x - offsetX : 0;
		int doy = -y > offsetY ? -y - offsetY : 0;
		int xx = x + (offsetX += dox);
		int yy = y + (offsetY += doy);
		for(int i = 0; i < dox; i++){
			tr.addFirst(new LinkedList<Double>());
		}
		for(int i = 0; i < tr.size(); i++){
			for(int j = 0; j < doy; j++){
				tr.get(i).addFirst(null);
			}
		}
		while(xx >= tr.size()){
			tr.addLast(new LinkedList<Double>());
		}
		for(int i = 0; i < tr.size(); i++){
			while(yy >= tr.get(i).size()){
				tr.get(i).addLast(null);
			}
		}
		tr.get(xx).set(yy, d);
	}
	
	public static void setAdjacentTile(int x, int y, byte direction, byte type){
		switch(direction){
		case Sys.NORTH: setTileByte(x, y - 1, type); break;
		case Sys.WEST: setTileByte(x - 1, y, type); break;
		case Sys.SOUTH: setTileByte(x, y + 1, type); break;
		case Sys.EAST: setTileByte(x + 1, y, type); break;
		}
	}
	
	public static byte getAdjacentTile(int x, int y, byte direction){
		switch(direction){
		case Sys.NORTH: return getTileByte(x, y - 1);
		case Sys.WEST: return getTileByte(x - 1, y);
		case Sys.SOUTH: return getTileByte(x, y + 1);
		case Sys.EAST: return getTileByte(x + 1, y);
		}
		return 0;
	}
	
	public static Image imageFor(byte type){
		switch(type){
		case GRASS: return Images.retrieve("grass.png");
		case SAND: return Images.retrieve("sand.png");
		case WATER: return Images.retrieve("water.png");
		case TREE: return Images.retrieve("tree.png");
		case NULL: return Images.retrieve("null.png");
		default: return Images.retrieve("null.png");
		}
	}
	
	public static String describe(double x, double y){
		double tile = getTileDouble((int)x, (int)y);
		byte t = (byte)tile;
		String type = "NULL";
		if(t <= 0) type = "WATER"; else
		if(t == 1) type = "SAND"; else
		if(t == 2) type = "GRASS"; else
		if(t >= 3) type = "TREE";
		return Sys.round(tile) + " (" + type + ")";
	}

}	