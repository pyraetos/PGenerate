package net.pyraetos.pgenerate;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import net.pyraetos.util.Images;
import net.pyraetos.util.Point;
import net.pyraetos.util.Sys;

public class PGenerate{

	/*
	 * Todo:
	 * 
	 * 1. extend coordinate system to negative numbers
	 * 2. implement region based double layer data structure
	 * 3. improve algorithm, increase scalability for large areas
	 * 
	 */
	
	private double tr[][];
	private int width;
	private int height;
	private long seed;
	private int offsetX;
	private int offsetY;
	private double s;
	
	public static final byte WATER = 0;
	public static final byte SAND = 1;
	public static final byte GRASS = 2;
	public static final byte TREE = 3;
	public static final byte NULL = -128;
	
	public PGenerate(int width, int height){
		this.width = width;
		this.height = height;
		tr = new double[width][height];
		seed = Sys.randomSeed();
		offsetX = offsetY = 0;
		s = 1d;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public void setSeed(long seed){
		this.seed = seed;
	}
	
	public long getSeed(){
		return seed;
	}

	public void setEntropy(double s){
		this.s = s;
	}
	
	public double getEntropy(){
		return s;
	}

	public void createAndSaveHeightmap(){
		BufferedImage image = createHeightmap();
		try {
			File file = new File("heightmap.png");
			if(!file.exists())
				file.createNewFile();
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage createHeightmap(){
		int[] pixels = new int[width * height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				pixels[i * height + j] = getTileColor(i, j).getRGB();
			}
		}

		BufferedImage pixelImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);    
		pixelImage.setRGB(0, 0, width, height, pixels, 0, width);
		
		return pixelImage;
	}

	public void generate(int x, int y){
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
	
	private double micro(int x, int y){
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
	
	private  double macro(int a, int b){
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
	
	public byte getTileByte(int x, int y){
		byte b = (byte)Math.floor(getTileDouble(x, y));
		if(b == NULL) return NULL;
		if(b >= TREE) return TREE;
		if(b <= WATER) return WATER;
		return b;
	}
	
	public byte getTileByte(Point point){
		return getTileByte(point.getX(), point.getY());
	}

	public double getTileDouble(int x, int y){
		try{
			return tr[x + offsetX][ y + offsetY];
		}catch(Exception e){
			return NULL;
		}
	}
	
	private Color getTileColor(int x, int y){
		double d = getTileDouble(x, y) + 1;
		if(d <= 0) return Color.BLACK;
		if(d >= 5) return Color.WHITE;
		int c = (int)Math.round(d * (255d / 5d));
		return new Color(c, c, c);
	}

	public void setTileByte(int x, int y, byte type){
		setTileDouble(x, y, (double)type);
	}

	public void setTileDouble(int x, int y, double d){
		tr[x][y] = d;
	}

	public void setAdjacentTile(int x, int y, byte direction, byte type){
		switch(direction){
		case Sys.NORTH: setTileByte(x, y - 1, type); break;
		case Sys.WEST: setTileByte(x - 1, y, type); break;
		case Sys.SOUTH: setTileByte(x, y + 1, type); break;
		case Sys.EAST: setTileByte(x + 1, y, type); break;
		}
	}
	
	public byte getAdjacentTile(int x, int y, byte direction){
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
	
	public String describe(int x, int y){
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