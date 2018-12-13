package net.pyraetos.pgenerate;

import java.util.concurrent.atomic.AtomicInteger;

import net.pyraetos.util.Sys;

public abstract class Example {

	public static int side = 1024;
	public static int inc = 32;
	
	public static AtomicInteger done = new AtomicInteger(0);
	public static PGenerate pg;
	
	private static class DivideNConquer implements Runnable{
		
		int start;
		
		DivideNConquer(int start){
			this.start = start;
		}
		
		@Override
		public void run(){
			for(int i = start; i < start + inc; i++){
				for(int j = 0; j < side; j++){
					pg.generate(i, j);
				}
				done.addAndGet(side);
			}
		}
	}
	
	public static void main(String[] args) {
		pixelMapExample();
	}
	
	public static void pixelMapExample(){
		pg = new PGenerate(side, side, 2);
		pg.setInterpolation(PGenerate.BICUBIC);
		long start = System.currentTimeMillis();
		int total = side*side;
		Sys.thread(()->{
			try {
				while(done.get() != total) {
					Sys.sleep(1000);
					Sys.debug(Sys.round((((double)done.get())/((double)total))*100d)+"%");
				}
				long time = System.currentTimeMillis() - start;
				float seconds = (float) (((double)time)/1000d);
				Sys.debug("It took " + seconds + "s to complete.");
			}catch(Exception e) {}
		});
		for(int n = 0; n < side; n += inc){
			Sys.thread(new DivideNConquer(n));
		}
		while(done.get() != total){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		new HeightMap(pg).save();
	}

}
