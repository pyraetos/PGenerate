package net.pyraetos.util;

import java.io.Serializable;
import java.util.Random;

@SuppressWarnings("serial")
public class NMMatrix implements Serializable{

	/*        m
	 *    _ _ _ _ _
	 *   |         |
	 *   |         |
	 * n |         |
	 *   |         |
	 *   |         |
	 *   |_ _ _ _ _|
	 */
	
	private int n;
	private int m;

	private float data[][];

	public static final int ZEROS = 0;
	public static final int ONES = 1;
	public static final int GAUSSIAN = 2;
	private static final Random R = new Random();
	
	public NMMatrix(int n, int m){
		this.n = n;
		this.m = m;
		data = new float[m][n];
	}
	
	public NMMatrix(int n, int m, int option){
		this.n = n;
		this.m = m;
		data = new float[m][n];
		for(int x = 0; x < m; x++){
			for(int y = 0; y < n; y++){
				switch(option){
				case(ONES): data[x][y] = 1; break;
				case(GAUSSIAN): data[x][y] = (float)R.nextGaussian(); break;
				default: data[x][y] = 0;
				}
			}
		}
	}
	
	public NMMatrix(String strData){
		String split[] = strData.split(";");
		this.n = split.length;
		this.m = split[0].split(",").length;
		set(strData);
		
	}
	
	public NMMatrix(NMMatrix existing){
		this(existing.n, existing.m);
		for(int mm = 0; mm < m; mm++){
			for(int nn = 0; nn < n; nn++){
				data[mm][nn] = existing.data[mm][nn];
			}
		}
	}
	
	public void set(String strData){
		String rows[] = strData.split(";");
		int givenN = rows.length;
		if(givenN != n){
			Sys.error();
			return;
		}
		float newData[][] = new float[m][n];
		int nn = 0;
		for(String rowStr : rows){
			String row[] = rowStr.split(",");
			int givenM = row.length;
			if(givenM != m){
				Sys.error();
				return;
			}
			int mm = 0;
			for(String elemStr : row){
				elemStr = elemStr.trim();
				float elem = Float.parseFloat(elemStr);
				newData[mm][nn] = elem;
				mm++;
			}
			nn++;
		}
		data = newData;
	}
	
	public void set(int x, int y, float f){
		if(x >= 0 && x < m)
			if(y >= 0 && y < n)
				data[x][y] = f;
	}
	
	public void setAll(float f){
		for(int i = 0; i < m; i++)
			for(int j = 0; j < n; j++)
				data[i][j] = f;
	}
	
	public float get(int x, int y){
		if(x >= 0 && x < m)
			if(y >= 0 && y < n)
				return data[x][y];
		return 0.0f;
	}
	
	public static NMMatrix multiply(NMMatrix a, NMMatrix b){
		//Check preconditions
		if(a.m != b.n){
			Sys.error();
			return null;
		}
		
		NMMatrix c = new NMMatrix(a.n, b.m);
		
		for(int bX = 0; bX < b.m; bX++){
			for(int aY = 0; aY < a.n; aY++){
				//Calculate c_aYbX
				float c_aYbX = 0.0f;
				for(int pos = 0; pos < a.m; pos++){
					c_aYbX += (a.get(pos, aY) * b.get(bX, pos));
				}
				c.set(bX, aY, c_aYbX);
			}
		}
		
		return c;
	}
	
	public static NMMatrix transpose(NMMatrix m){
		NMMatrix mT = new NMMatrix(m.m, m.n);
		for(int i = 0; i < m.m; i++){
			for(int j = 0; j < m.n; j++){
				mT.data[j][i] = m.data[i][j];
			}
		}
		return mT;
	}
	
	@Override
	public String toString(){
		String str = "";
		for(int y = 0; y < n; y++){
			for(int x = 0; x < m; x++){
				if(x == m - 1)
					str += Sys.round(data[x][y]) + ";\n";
				else
					str += Sys.round(data[x][y]) + ",";
			}
		}
		return str;
	}
}