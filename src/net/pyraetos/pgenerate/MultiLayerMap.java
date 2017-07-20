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
	
	public static final int SIDE = 1024;
	public static final int INC = 64;
	
	public MultiLayerMap(int width, int height){
		super(width, height, BufferedImage.TYPE_INT_RGB);
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
			for(int i = n; i < n + INC; i++){
				for(int j = 0; j < SIDE; j++){
					pg.generate(i, j);
				}
			}
			done.incrementAndGet();
		}
	}
	
	protected void layer(){
		pg = new PGenerate(width, height);
		pg.setInterpolation(PGenerate.BICUBIC);
		done.set(0);
		for(int n = 0; n < SIDE; n += INC){
			Sys.thread(new DivideNConquer(n));
		}
		while(done.get() != SIDE / INC){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public abstract void layer(int layer);
}
