package net.pyraetos.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Box;

public abstract class Sys{
	
	public static final byte NORTH = 0;
	public static final byte SOUTH = 1;
	public static final byte EAST = 2;
	public static final byte WEST = 3;
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("########.##");
	private static final DecimalFormat DECIMAL_FORMAT1 = new DecimalFormat("########.#");
	private static final Random RANDOM = new Random();
	public static final float PI = (float)Math.PI;

	public static void thread(Runnable r){
		new Thread(r).start();
	}

	public static float sin(float theta){
		return (float)Math.sin(theta);
	}
	
	public static float asin(float theta){
		return (float)Math.asin(theta);
	}
	
	public static float cos(float theta){
		return (float)Math.cos(theta);
	}
	
	public static float acos(float theta){
		return (float)Math.acos(theta);
	}
	
	public static float tan(float theta){
		return (float)Math.tan(theta);
	}
	
	public static String[] tokenize(String string, String separator){
		String[] split = string.split(separator);
		List<String> list = new ArrayList<String>();
		for(String s : split)
			if(s.length() > 0)
				list.add(s);
		return list.toArray(new String[list.size()]);
	}
	
	public static float toRadians(float angle){
		return (angle * (float)Math.PI) / 180f;
	}
	
	public static float toDegrees(float theta){
		return (theta * 180f) / (float)Math.PI;
	}
	
	public static float simplifyAngled(float angle){
		while(angle < 0) angle += 360;
		while(angle > 360f) angle -= 360f;
		return angle;
	}
	
	public static float simplifyAngler(float theta){
		while(theta < 0) theta += (2f * (float)Math.PI);
		while(theta > (2f * (float)Math.PI)) theta -= (2f * (float)Math.PI);
		return theta;
	}
	
	public static boolean betweenSimplified(float theta, float min, float max){
		theta = simplifyAngler((float)theta);
		min = simplifyAngler((float)min);
		max = simplifyAngler((float)max);
		if(min > max)
			return theta >= min || theta <= max;
		return theta <= max && theta >= min;
	}
	
	public static boolean between(float theta, float min, float max){
		if(min > max)
			return theta >= min || theta <= max;
		return theta <= max && theta >= min;
	}
	
	//Modified for OpenGL space
	public static float direction(float x, float y){
		return simplifyAngler((float)Math.atan2(y, x) - PI / 2f);
	}
	
	public static float distance(float x, float y){
		return (float)Math.round(Math.sqrt(x*x + y*y));
	}
	
	public static boolean equal(Object...objects){
		Object o = null;
		for(Object object : objects){
			if(o != null)
			if(!object.equals(o)) return false;
			o = object;
		}
		return true;
	}
	
	public static long time(){
		return System.currentTimeMillis();
	}

	public static void timer(long begin){
		Sys.debug(time() - begin);
	}
	
	public static String load(String filename){
		File file = new File(filename);
		String s = "";
		if(!file.exists())
			return s;
		try{
			FileReader reader = new FileReader(filename);
			int i = reader.read();
			while(i != -1){
				s += (char)i;
				i = reader.read();
			}
			reader.close();
			return s;
		}catch(IOException e){
			return s;
		}
	}
	
	public static void sleep(long time){
		if(time < 0)
			return;
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static Component space(){
		return Box.createRigidArea(new Dimension(10, 10));
	}
	
	public static Component space(int scaleFactor){
		return Box.createRigidArea(new Dimension(10 * scaleFactor, 10 * scaleFactor));
	}
	
	public static int toInt(byte[] buf){
		ByteBuffer bb = ByteBuffer.wrap(buf);
		return bb.getInt();
	}
	
	public static byte[] toBytes(int i){
		return ByteBuffer.allocate(4).putInt(i).array();
	}
	
	public static Color randomColor(){
		int red = RANDOM.nextInt(256);
		int green = RANDOM.nextInt(256);
		int blue = RANDOM.nextInt(256);
		return new Color(red, green, blue);
	}
	
	public static Vector randomColorV(){
		Color c = randomColor();
		float[] cf = c.getRGBColorComponents(new float[3]);
		return new Vector(cf[0], cf[1], cf[2]);
	}
	
	public static InputStream getURLStream(String url){
		try{
			URL u = new URL(url);
			return u.openStream();
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static <E> Set<E> concurrentSet(Class<E> type){
		return Collections.newSetFromMap(new ConcurrentHashMap<E, Boolean>());
	}
	
	public static Set<Integer> concurrentSetInteger(){
		return Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
	}
	
	public static Set<String> concurrentSetString(){
		return Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	}
	
	public static int dualRandom(int one, int two, int three, int four){
		if(chance(.5))
			return RANDOM.nextInt(two - one) + one;
		return RANDOM.nextInt(four - three) + three;
	}
	
	public static void debug(Object o){
		System.out.println(o);
	}
	
	public static void debug(int i){
		System.out.println(i);
	}
	
	public static void debug(double d){
		System.out.println(d);
	}
	
	public static void debug(boolean b){
		System.out.println(b);
	}
	
	public static void debug(String s){
		System.out.println(s);
	}
	
	public static void debug(long l){
		System.out.println(l);
	}
	
	public static void debug(){
		System.out.println("debug");
	}

	public static void error(String s){
		System.err.println(s);
	}
	
	public static void error(Object o){
		System.err.println(o);
	}
	
	public static void error(){
		System.err.println("Error!");
	}
	
	public static boolean chance(double chance){
		return chance(chance, new Random());
	}
	
	public static boolean chance(double chance, Random r){
		double random = r.nextDouble();
		return random <= chance;
	}
	
	public static int distanceFrom(int x0, int y0, int x1, int y1){
		return (int)Math.round(Math.sqrt(Math.pow(Math.max(x0, x1) - Math.min(x0, x1), 2) + Math.pow(Math.max(y0, y1) - Math.min(y0, y1), 2)));
	}
	
	public static float distanceFrom(Vector a, Vector b){
		return (float)Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) + Math.pow(a.z - b.z, 2));
	}
	
	public static long randomSeed(){
		return Math.round(System.currentTimeMillis() * (1 - (2*RANDOM.nextDouble())));
	}
	
	public static int amean(int...nums){
		int mean = 0;
		for(int n : nums) mean += n;
		mean /= nums.length;
		return mean;
	}
	
	public static int gmean(int...nums){
		int mean = 1;
		for(int n : nums) mean *= n;
		mean = (int)Math.round(Math.pow(mean, 1d / ((float)nums.length)));
		return mean;
	}
	
	public static void histogram(int bars, int num_bins, int...nums){
		Arrays.sort(nums);
		float maxVal = nums[nums.length-1];
		int bins[] = new int[num_bins+1];
		for(int i = 0; i < bins.length; i++) bins[i] = 0;
		for(int num : nums){
			int bin = (int)Math.floor(((float)num / (float)maxVal) * num_bins);
			bins[bin]++;
		}
		int bin_i = 0;
		for(int bin : bins){
			int barsForYou = (int)(((float)bars) * (((float)bin) / (float)nums.length));
			System.out.print((int)((float)maxVal / (float)num_bins)*bin_i + "|");
			for(int i = 0; i < barsForYou; i++)
				System.out.print("-");
			System.out.println();
			bin_i++;
		}
	}
	
	public static void histogram(int bars, int num_bins, float...nums){
		Arrays.sort(nums);
		float maxVal = nums[nums.length-1];
		int bins[] = new int[num_bins+1];
		for(int i = 0; i < bins.length; i++) bins[i] = 0;
		for(float num : nums){
			int bin = (int)Math.floor((num / maxVal) * num_bins);
			bins[bin]++;
		}
		int bin_i = 0;
		for(int bin : bins){
			int barsForYou = (int)(((float)bars) * (((float)bin) / (float)nums.length));
			System.out.print((maxVal / (float)num_bins)*bin_i + "|");
			for(int i = 0; i < barsForYou; i++)
				System.out.print("-");
			System.out.println();
			bin_i++;
		}
	}
	
	public static int hmean(int...nums){
		float mean = 0;
		for(int n : nums) mean += (1d / (float)n);
		mean = ((float)nums.length) / mean;
		return Math.round(mean);
	}
	
	public static double round(double d){
		return Double.parseDouble(DECIMAL_FORMAT.format(d));
	}
	
	public static double round1(double d){
		return Double.parseDouble(DECIMAL_FORMAT1.format(d));
	}
}
