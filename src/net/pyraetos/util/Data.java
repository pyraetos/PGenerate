package net.pyraetos.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URL;

public abstract class Data{

	public static void download(String url, String target){
		try{
			BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
			File file = new File(target);
			if(file.exists())
				file.delete();
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			int datum;
			while((datum = in.read()) != -1){
				out.write(datum);
			}
			out.flush();
			out.close();
			in.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public static String load(String dir){
		String result = "";
		try{
			File file = new File(dir);
			if(!file.exists())
				return result;
			BufferedReader in = new BufferedReader(new FileReader(file));
			result = in.readLine();
			String temp = in.readLine();
			while(temp != null){
				result += "\n" + temp;
				temp = in.readLine();
			}
			in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

}
