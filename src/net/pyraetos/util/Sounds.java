package net.pyraetos.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public abstract class Sounds{
	
	private static Map<String, Clip> loadedSounds = new HashMap<String, Clip>();
	private static String prefix;
	private static String file;

	static{
		try{
			fromLocal();
		}catch(Exception e){}
		file = null;
		Sys.thread(new Runnable(){
			public void run(){
				while(true){
					if(file != null){
						try{
							if(!loadedSounds.containsKey(file)){
								URL url = new URL(prefix + file);
								Clip clip = AudioSystem.getClip();
								AudioInputStream in = AudioSystem.getAudioInputStream(url);
								clip.open(in);
								in.close();
								loadedSounds.put(file, clip);
							}
							Clip clip = loadedSounds.get(file);
							if(clip.isActive())
								clip.stop();
							clip.setFramePosition(0);
							clip.start();
						}catch(Exception e){
							Sys.debug("Path: " + prefix + file);
							e.printStackTrace();
						}
						file = null;
					}
					Sys.sleep(10);
				}
			}
		});
	}

	public static void fromPyraetosNet(){
		fromURL("http://www.pyraetos.net/sounds/");
	}
	
	public static void fromLocal(){
		fromURL("file:///" + System.getProperty("user.dir").replace("\\", "/"));
	}
	
	public static void fromLocalSounds(){
		fromURL("file:///" + System.getProperty("user.dir").replace("\\", "/") + "/sounds/");
	}
	
	public static void fromURL(String url){
		prefix = url + (url.endsWith("/") ? "" : "/");
	}

	public static void play(String file){
		Sounds.file = file;
	}
}
