package net.pyraetos.pgenerate;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class PixelMap extends BufferedImage{
	
	public PixelMap(PGenerate pg){
		super(pg.getWidth(), pg.getHeight(), BufferedImage.TYPE_INT_RGB);
		int width = getWidth();
		int height = getHeight();
		int[] pixels = new int[width * height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				pixels[i * height + j] = valueToColor(pg.getValue(i, j)).getRGB();
			}
		}
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
	
	public abstract Color valueToColor(double value);
}
