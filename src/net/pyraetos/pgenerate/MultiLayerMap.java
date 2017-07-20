package net.pyraetos.pgenerate;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import net.pyraetos.util.Sys;

public abstract class MultiLayerMap extends BufferedImage{
	
	protected PGenerate pg;
	protected int width;
	protected int height;
	protected int[] pixels;
	protected static AtomicInteger done = new AtomicInteger(0);
	
	protected int side = 1024;
	protected int inc = 64;
	protected int layercutoff;
	
	/**
	 * Not tested with width != height
	 * @param width
	 * @param height
	 */
	public MultiLayerMap(int width, int height, int toplayer){
		super(width, height, BufferedImage.TYPE_INT_RGB);
		layercutoff = toplayer;
		if(width == height){
			side = width;
			inc = width / 16;
		}
		this.pg = null;
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
		layer(0);
		setRGB(0, 0, width, height, pixels, 0, width);
	}

	public void save(){
		try {
			File file = new File("map.png");
			if(!file.exists())
				file.createNewFile();
			ImageIO.write(this, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save(String path){
		try {
			File file = new File(path);
			if(!file.exists())
				file.createNewFile();
			ImageIO.write(this, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class DivideNConquer implements Runnable{
		private int n;
		DivideNConquer(int start){
			this.n = start;
		}
		public void run(){
			for(int i = n; i < n + inc; i++){
				for(int j = 0; j < side; j++){
					pg.generate(i, j);
				}
			}
			done.incrementAndGet();
		}
	}
	
	protected void layer(int layer){
		if(layer > layercutoff) return;
		System.out.println("Starting layer " + layer);
		pg = new PGenerate(width, height);
		pg.setInterpolation(PGenerate.BICUBIC);
		done.set(0);
		for(int n = 0; n < side; n += inc){
			Sys.thread(new DivideNConquer(n));
		}
		while(done.get() != side / inc){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color c = colorForLayer(layer, i, j);
				if(c != null)
					pixels[i * height + j] = c.getRGB();
			}
		}
		layer(++layer);
	}

	protected abstract Color colorForLayer(int layer, int i, int j);
}
