package net.pyraetos.pgenerate;

import java.util.concurrent.atomic.AtomicInteger;

import net.pyraetos.util.Sys;

public class Test {

	public static int side = 256;
	public static int inc = 256;
	
	public static AtomicInteger done = new AtomicInteger(0);
	public static PGenerate pg;
	
	private static class DivNConq implements Runnable{
		
		int start;
		
		DivNConq(int start){
			this.start = start;
		}
		
		@Override
		public void run(){
			for(int i = start; i < start + inc; i++){
				for(int j = 0; j < side; j++){
					pg.generate(i, j);
				}
			}
			done.incrementAndGet();
		}
	}
	
	public static void main(String[] args) {
		pg = new PGenerate(side, side, 25346);
		long start = System.currentTimeMillis();
		for(int n = 0; n < side; n += inc){
			Sys.thread(new DivNConq(n));
		}
		while(done.get() != side / inc){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long time = System.currentTimeMillis() - start;
		float seconds = (float) (((double)time)/1000d);
		Sys.debug("It took " + seconds + "s to complete.");
		pg.createAndSaveHeightmap();
	}

}
