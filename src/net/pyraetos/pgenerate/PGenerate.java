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

import net.pyraetos.util.MatrixD;
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
	private int interpolation;
	
	public static final int NEAREST_NEIGHBOR = 0;
	public static final int BILINEAR = 1;
	public static final int BICUBIC = 2;
	
	private static final MatrixD matrixA;
	private static final MatrixD matrixC;

	static{
		matrixA = new MatrixD(4, 4);
		matrixA.setRow(0, 1d, 0d, 0d, 0d);
		matrixA.setRow(1, 0d, 0d, 1d, 0d);
		matrixA.setRow(2, -3d, 3d, -2d, -1d);
		matrixA.setRow(3, 2d, -2d, 1d, 1d);
		
		matrixC = new MatrixD(4, 4);
		matrixC.setColumn(0, 1d, 0d, 0d, 0d);
		matrixC.setColumn(1, 0d, 0d, 1d, 0d);
		matrixC.setColumn(2, -3d, 3d, -2d, -1d);
		matrixC.setColumn(3, 2d, -2d, 1d, 1d);
	}

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
		setInterpolation(BICUBIC);
	}
	
	public void setInterpolation(int interp){
		if(interp < 0 || interp > 2)
			return;
		interpolation = interp;
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
		for(int i = x - 1; i <= x + 1; i++){
			for(int j = y - 1; j <= y + 1; j++){
				value += noise(x, y, 4);
				value += noise(x, y, 3) / 2d;
				value += noise(x, y, 2) / 4d;
				value += noise(x, y, 1) / 8d;
				value += noise(x, y, 0) / 16d;
			}
		}
		setValue(x, y, value / 9d);
		//Good place to add mobs and rare objects
		/*if(Sys.chance(.0005d)){
			
		}
		*/
	}
	
	public double noise(int x, int y, int power){
		switch(interpolation){
		case NEAREST_NEIGHBOR: return nearestNeighbor(x, y, power);
		case BILINEAR: return bilinear(x, y, power);
		case BICUBIC: return bicubic(x, y, power);
		default: return 0d;
		}
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
	
	private double nearestNeighbor(int a, int b, int power){
		if(power < 0)
			return 0d;
		if(power == 0)
			return rawValue(a, b, 4, power);
		
		int x = a < 0 ? a >> power - 1 : a >> power;
		int y = b < 0 ? b >> power - 1 : b >> power;

		int w = 4;
		
		return rawValue(x, y, w, power);
	}
	
	private double bilinear(int a, int b, int power){
		if(power < 0)
			return 0d;
		if(power == 0)
			return rawValue(a, b, 4, power);
		
		int x = a < 0 ? a >> power - 1 : a >> power;
		int y = b < 0 ? b >> power - 1 : b >> power;
		int xFloor = x << power;
		int yFloor = y << power;
		
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
	
	private double bicubic(int a, int b, int power){
		if(power < 0)
			return 0d;
		if(power == 0)
			return rawValue(a, b, 4, power);
		
		int x0 = a < 0 ? a >> power - 1 : a >> power;
		int y0 = b < 0 ? b >> power - 1 : b >> power;
		int xFloor = x0 << power;
		int yFloor = y0 << power;
		int x1 = x0 + 1;
		int y1 = y0 + 1;
		int xn1 = x0 - 1;
		int yn1 = y0 - 1;
		int x2 = x1 + 1;
		int y2 = y1 + 1;
		
		double div = Math.pow(2d, power);
		double mappedX = ((double)(a - xFloor)) / div;
		double mappedY = ((double)(b - yFloor)) / div;
		
		int w = 4;
		
		//Obtain the 16 values we need
		//1. The function values
		double f00 = rawValue(x0, y0, w, power);
		double f01 = rawValue(x0, y1, w, power);
		double f10 = rawValue(x1, y0, w, power);
		double f11 = rawValue(x1, y1, w, power);

		//2. The x partial derivatives
		double fx00 = (rawValue(x1, y0, w, power) - rawValue(xn1, y0, w, power)) / 2d;
		double fx01 = (rawValue(x1, y1, w, power) - rawValue(xn1, y1, w, power)) / 2d;
		double fx10 = (rawValue(x2, y0, w, power) - rawValue(x0, y0, w, power)) / 2d;
		double fx11 = (rawValue(x2, y1, w, power) - rawValue(x0, y1, w, power)) / 2d;

		//3. The y partial derivatives
		double fy00 = (rawValue(x0, y1, w, power) - rawValue(x0, yn1, w, power)) / 2d;
		double fy01 = (rawValue(x0, y2, w, power) - rawValue(x0, y0, w, power)) / 2d;
		double fy10 = (rawValue(x1, y1, w, power) - rawValue(x1, yn1, w, power)) / 2d;
		double fy11 = (rawValue(x1, y2, w, power) - rawValue(x1, y0, w, power)) / 2d;
		
		//4. The cross derivatives
		double fxy00 = (fx01 - ((rawValue(x1, yn1, w, power) - rawValue(xn1, yn1, w, power)) / 2d)) / 2d;
		double fxy01 = (((rawValue(x1, y2, w, power) - rawValue(xn1, y2, w, power)) / 2d) - fx00) / 2d;
		double fxy10 = (fx11 - ((rawValue(x2, yn1, w, power) - rawValue(x0, yn1, w, power)) / 2d)) / 2d;
		double fxy11 = (((rawValue(x2, y2, w, power) - rawValue(x0, y2, w, power)) / 2d) - fx10) / 2d;		

		//Create the beta matrix
		MatrixD matrixB = new MatrixD(4, 4);
		matrixB.setRow(0, f00, f01, fy00, fy01);
		matrixB.setRow(1, f10, f11, fy10, fy11);
		matrixB.setRow(2, fx00, fx01, fxy00, fxy01);
		matrixB.setRow(3, fx10, fx11, fxy10, fxy11);
		
		//Perform the multiplication for the coefficient matrix
		MatrixD coeffMatrix = matrixA.multiply(matrixB.multiply(matrixC));
		
		//Create the vectors for our point
		MatrixD vecX = new MatrixD(4, 1);
		vecX.setRow(0, 1d, (double)mappedX, Math.pow((double)mappedX, 2d), Math.pow((double)mappedX, 3d));
	
		MatrixD vecY = new MatrixD(1, 4);
		vecY.setColumn(0, 1d, (double)mappedY, Math.pow((double)mappedY, 2d), Math.pow((double)mappedY, 3d));
		
		//Perform the final multiplication and extract the interpolated value
		MatrixD valueMatrix = vecX.multiply(coeffMatrix.multiply(vecY));
		
		double value = valueMatrix.get(0, 0);
		return value;
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