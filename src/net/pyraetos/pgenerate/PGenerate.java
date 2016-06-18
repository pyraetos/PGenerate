package net.pyraetos.pgenerate;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import net.pyraetos.util.Point;
import net.pyraetos.util.Sys;
import net.pyraetos.util.Tuple3;

public class PGenerate{

	/*
	 * Todo:
	 * 
	 * 1. extend coordinate system to negative numbers
	 * 2. implement region based double layer data structure
	 * 
	 */
	
	private double tr[][];
	private int width;
	private int height;
	private long seed;
	private int seedUpper;
	private int seedLower;
	private int offsetX;
	private int offsetY;
	private double s;
	private Map<Point, Double> gaussianMap;
	private Map<Tuple3<Integer, Integer, Integer>, Double> rawValueMap;
	
	public static final double NULL = 0.0d;
	
	public PGenerate(int width, int height){
		this(width, height, Sys.randomSeed());
	}
	
	public PGenerate(int width, int height, long seed){
		this.width = width;
		this.height = height;
		tr = new double[width][height];
		setSeed(seed);
		offsetX = offsetY = 0;
		s = 1d;
		rawValueMap = new ConcurrentHashMap<Tuple3<Integer, Integer, Integer>, Double>();
		gaussianMap = new ConcurrentHashMap<Point, Double>();
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public void setSeed(long seed){
		this.seed = seed;
		this.seedUpper = (int)(seed >> 32);
		this.seedLower = (int)seed;
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
			for(int j = y - 1; j <= y + 1; j++){
				value += noise(x, y, 4);
				value += noise(x, y, 3) / 2d;
				value += noise(x, y, 2) / 4d;
				value += noise(x, y, 1) / 8d;
				value += noise(x, y, 0) / 16d;
			}
		setValue(x, y, value / 9d);
		//Good place to add mobs and rare objects
		/*if(Sys.chance(.0005d)){
			
		}
		*/
	}
	
	private byte[] getMaskedSeed(int x, int y, int power){
		x ^= power == 0 ? seedUpper : seedUpper ^ power;
		y ^= power == 0 ? seedLower : seedLower ^ power;
		byte[] b = new byte[8];
		b[0] = (byte)(x >> 24);
		b[1] = (byte)(x >> 16);
		b[2] = (byte)(x >> 8);
		b[3] = (byte)x;
		b[4] = (byte)(y >> 24);
		b[5] = (byte)(y >> 16);
		b[6] = (byte)(y >> 8);
		b[7] = (byte)y;
		return b;
	}
	
	private double rawValue(int x, int y, int w, int power){
		Tuple3<Integer, Integer, Integer> tup = new Tuple3<Integer, Integer, Integer>(x, y, power);
		if(rawValueMap.containsKey(tup))
			return rawValueMap.get(tup);
		double value = 0d;
		for(int i = x - w; i <= x + w; i++){
			for(int j = y - w; j <= y + w; j++){
				int dx = x - i;
				int dy = y - j;
				double h = (x == i && y == j) ? 1 : Math.sqrt(dx * dx + dy * dy);
				Point point = new Point(i, j);
				if(!gaussianMap.containsKey(point)){
					Random random = new SecureRandom(getMaskedSeed(i, j, power));
					double rawValue = random.nextGaussian();
					gaussianMap.put(point, rawValue);
				}
				value += (gaussianMap.get(point) * s) / (4d * h);
			}
		}
		rawValueMap.put(tup, value);
		return value;
	}
	
	private double noise(int a, int b, int power){
		if(power < 0)
			return 0d;
		if(power == 0)
			return rawValue(a, b, 4, power);
		
		int range = power;
		int x = a < 0 ? a >> range - 1 : a >> range;
		int y = b < 0 ? b >> range - 1 : b >> range;
		int xFloor = x << range;
		int yFloor = y << range;
		
		double div = Math.pow(2d, power);
		double prop_left = ((double)(a - xFloor)) / div;
		double prop_up = ((double)(b - yFloor)) / div;
		
		double wnw = (1 - prop_left) * (1 - prop_up);
		double wsw = (1 - prop_left) * prop_up;
		double wne = prop_left * (1 - prop_up);
		double wse = prop_left * prop_up;
		
		int w = 4;
		double base = 0d;
		
		double vnw = rawValue(x, y, w, power);
		double vsw = rawValue(x, y + 1, w, power);
		double vne = rawValue(x + 1, y, w, power);
		double vse = rawValue(x + 1, y + 1, w, power);
		
		base += wnw * vnw;
		base += wsw * vsw;
		base += wne * vne;
		base += wse * vse;
		
		return base;
	}

	public double getValue(int x, int y){
		try{
			return tr[x + offsetX][y + offsetY];
		}catch(Exception e){
			return 0.0d;
		}
	}
	
	private Color getTileColor(int x, int y){
		double d = getValue(x, y);
		if(d <= -3) return Color.BLACK;
		if(d >= 3) return Color.WHITE;
		int c = (int)Math.round((d + 3) * (255d / 6d));
		return new Color(c, c, c);
	}

	public void setValue(int x, int y, double d){
		tr[x][y] = d;
	}
}	