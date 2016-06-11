package net.pyraetos.pgenerate;

import java.util.concurrent.atomic.AtomicInteger;

import net.pyraetos.util.Sys;

public class Test {

	public static int side = 2000;
	public static int inc = 100;
	
	public static AtomicInteger done = new AtomicInteger(0);
	
	private static class DivNConq implements Runnable{
		
		int start;
		
		DivNConq(int start){
			this.start = start;
		}
		
		@Override
		public void run(){
			for(int i = start; i < start + inc; i++){
				for(int j = 0; j < side; j++){
					PGenerate.generate(i, j);
				}
			}
			done.incrementAndGet();
		}
	}
	
	public static void main(String[] args) {
		PGenerate.setSeed(14);
		long start = System.currentTimeMillis();
		for(int n = 0; n < side; n += inc){
			Sys.thread(new DivNConq(n));
		}
		while(done.get() != side / inc){
			Sys.debug(done.get());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		/*for(int i = 0; i < 500; i++){
			for(int j = 0; j < 500; j++){
				PGenerate.generate(i, j);
			}
		}*/
		long time = System.currentTimeMillis() - start;
		float seconds = (float) (((double)time)/1000d);
		Sys.debug("It took " + seconds + "s to complete.");
		PGenerate.createAndSaveHeightmap();
	}

}
