package net.pyraetos.pgenerate;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.pyraetos.util.Matrix;
import net.pyraetos.util.Point;
import net.pyraetos.util.Sys;
import net.pyraetos.util.Tuple3;
import net.pyraetos.util.Vector;

public class PGenerate{

	/*
	 * Todo:
	 * 
	 * 1. fix bicubic for new matrices
	 * 2. extend coordinate system to negative numbers
	 * 3. implement region based double layer data structure
	 * 
	 */
	
	private float tr[][];
	private int width;
	private int height;
	private long seed;
	private int seedUpper;
	private int seedLower;
	private int offsetX;
	private int offsetY;
	private float s;
	private int w;
	private Map<Tuple3<Integer, Integer, Integer>, Float> gaussianMap;
	private Map<Tuple3<Integer, Integer, Integer>, Float> rawValueMap;
	private Map<Point, Float> generatedMap;
	private int interpolation;
	
	public static final int NEAREST_NEIGHBOR = 0;
	public static final int BILINEAR = 1;
	public static final int BICUBIC = 2;
	
	private static final Matrix matrixA;
	private static final Matrix matrixC;

	static{
		matrixA = new Matrix(1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, -3f, 3f, -2f, -1f, 2f, -2f, 1f, 1f);
		matrixC = new Matrix(1f, 0f, -3f, 2f, 0f, 0f, 3f, -2f, 0f, 1f, -2f, 1f, 0f, 0f, -1f, 1f);
	}

	public PGenerate(int width, int height){
		this(width, height, Sys.randomSeed());
	}
	
	public PGenerate(int width, int height, long seed){
		this.width = width;
		this.height = height;
		tr = new float[width][height];
		setSeed(seed);
		offsetX = offsetY = 0;
		s = 1f;
		w = 4;
		rawValueMap = new ConcurrentHashMap<Tuple3<Integer, Integer, Integer>, Float>();
		gaussianMap = new ConcurrentHashMap<Tuple3<Integer, Integer, Integer>, Float>();
		generatedMap = new ConcurrentHashMap<Point, Float>();
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

	public void setEntropy(float s){
		this.s = s;
	}
	
	public double getEntropy(){
		return s;
	}
	
	public float getValue(int x, int y){
		try{
			return tr[x + offsetX][y + offsetY];
		}catch(Exception e){
			return 0.0f;
		}
	}

	public void setValue(int x, int y, float f){
		tr[x][y] = f;
	}
	
	public void generate(int x, int y){
		float value = 0f;
		for(int i = x - 1; i <= x + 1; i++){
			for(int j = y - 1; j <= y + 1; j++){
				Point point = new Point(i, j);
				if(!generatedMap.containsKey(point)){
					float pointValue = 0f;
					pointValue += noise(i, j, 4);
					pointValue += noise(i, j, 3) / 2f;
					pointValue += noise(i, j, 2) / 4f;
					pointValue += noise(i, j, 1) / 8f;
					pointValue += noise(i, j, 0) / 16f;
					generatedMap.put(point, pointValue);
				}
				value += generatedMap.get(point);
			}
		}
		setValue(x, y, value / 9f);
		//Good place to add mobs and rare objects
		/*if(Sys.chance(.0005d)){
			
		}*/
	}
	
	public float noise(int x, int y, int power){
		if(power < 0)
			return 0f;
		if(power == 0)
			return rawValue(x, y, power);
		switch(interpolation){
		case NEAREST_NEIGHBOR: return nearestNeighbor(x, y, power);
		case BILINEAR: return bilinear(x, y, power);
		case BICUBIC: return bicubic(x, y, power);
		default: return 0f;
		}
	}
	
	private byte[] getMaskedSeed(int x, int y, int power){
		x ^= seedUpper ^ power;
		y ^= seedLower ^ power;
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
	
	private float rawValue(int x, int y, int power){
		Tuple3<Integer, Integer, Integer> tup = new Tuple3<Integer, Integer, Integer>(x, y, power);
		if(rawValueMap.containsKey(tup))
			return rawValueMap.get(tup);
		float value = 0f;
		for(int i = x - w; i <= x + w; i++){
			for(int j = y - w; j <= y + w; j++){
				int dx = x - i;
				int dy = y - j;
				float h = (x == i && y == j) ? 1f : (float)Math.sqrt(dx * dx + dy * dy);
				Tuple3<Integer, Integer, Integer> gtup = new Tuple3<Integer, Integer, Integer>(i, j, power);
				if(!gaussianMap.containsKey(gtup)){
					Random random = new SecureRandom(getMaskedSeed(i, j, power));
					float rawValue = (float) random.nextGaussian();
					gaussianMap.put(gtup, rawValue);
				}
				value += (gaussianMap.get(gtup) * s) / (4f * h);
			}
		}
		rawValueMap.put(tup, value);
		return value;
	}
	
	private float nearestNeighbor(int a, int b, int power){
		int x = a < 0 ? a >> power - 1 : a >> power;
		int y = b < 0 ? b >> power - 1 : b >> power;
		
		return rawValue(x, y, power);
	}
	
	private float bilinear(int a, int b, int power){
		int x = a < 0 ? a >> power - 1 : a >> power;
		int y = b < 0 ? b >> power - 1 : b >> power;
		int xFloor = x << power;
		int yFloor = y << power;
		
		float div = (float)Math.pow(2d, power);
		float prop_left = ((float)(a - xFloor)) / div;
		float prop_up = ((float)(b - yFloor)) / div;
		
		float wnw = (1 - prop_left) * (1 - prop_up);
		float wsw = (1 - prop_left) * prop_up;
		float wne = prop_left * (1 - prop_up);
		float wse = prop_left * prop_up;

		float base = 0f;
		
		float vnw = rawValue(x, y, power);
		float vsw = rawValue(x, y + 1, power);
		float vne = rawValue(x + 1, y, power);
		float vse = rawValue(x + 1, y + 1, power);
		
		base += wnw * vnw;
		base += wsw * vsw;
		base += wne * vne;
		base += wse * vse;
		
		return base;
	}
	
	private float bicubic(int a, int b, int power){
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
		
		float div = (float)Math.pow(2d, power);
		float mappedX = ((float)(a - xFloor)) / div;
		float mappedY = ((float)(b - yFloor)) / div;
		
		//Obtain the 16 values we need
		//1. The function values
		float f00 = rawValue(x0, y0, power);
		float f01 = rawValue(x0, y1, power);
		float f10 = rawValue(x1, y0, power);
		float f11 = rawValue(x1, y1, power);

		//2. The x partial derivatives
		float fx00 = (rawValue(x1, y0, power) - rawValue(xn1, y0, power)) / 2f;
		float fx01 = (rawValue(x1, y1, power) - rawValue(xn1, y1, power)) / 2f;
		float fx10 = (rawValue(x2, y0, power) - rawValue(x0, y0, power)) / 2f;
		float fx11 = (rawValue(x2, y1, power) - rawValue(x0, y1, power)) / 2f;

		//3. The y partial derivatives
		float fy00 = (rawValue(x0, y1, power) - rawValue(x0, yn1, power)) / 2f;
		float fy01 = (rawValue(x0, y2, power) - rawValue(x0, y0, power)) / 2f;
		float fy10 = (rawValue(x1, y1, power) - rawValue(x1, yn1, power)) / 2f;
		float fy11 = (rawValue(x1, y2, power) - rawValue(x1, y0, power)) / 2f;
		
		//4. The cross derivatives
		float fxy00 = (fx01 - ((rawValue(x1, yn1, power) - rawValue(xn1, yn1, power)) / 2f)) / 2f;
		float fxy01 = (((rawValue(x1, y2, power) - rawValue(xn1, y2, power)) / 2f) - fx00) / 2f;
		float fxy10 = (fx11 - ((rawValue(x2, yn1, power) - rawValue(x0, yn1, power)) / 2f)) / 2f;
		float fxy11 = (((rawValue(x2, y2, power) - rawValue(x0, y2, power)) / 2f) - fx10) / 2f;		

		//Create the beta matrix
		Matrix matrixB = new Matrix(f00, f01, fy00, fy01, f10, f11, fy10, fy11, fx00, fx01, fxy00, fxy01, fx10, fx11, fxy10, fxy11);
		
		//Perform the multiplication for the coefficient matrix
		Matrix coeffMatrix = new Matrix();
		Matrix.multiply(matrixC, matrixB, coeffMatrix);
		Matrix.multiply(coeffMatrix, matrixA, coeffMatrix);
		
		//Create the vectors for our point
		Vector vecX = new Vector(1f, (float)mappedX, (float)Math.pow((double)mappedX, 2f));
		float wa = (float)Math.pow((double)mappedX, 3f);
	
		Vector vecY = new Vector(1f, (float)mappedY, (float)Math.pow((double)mappedY, 2f));
		float wb = (float)Math.pow((double)mappedY, 3f);
		
		//Perform the final multiplications and obtain the interpolated value
		Vector c = new Vector(0f, 0f, 0f);
		float wc = Matrix.multiply(coeffMatrix, vecY, wb, c);
		float interpValue = Vector.multiply(vecX, wa, c, wc);
		return interpValue;
	}
}	