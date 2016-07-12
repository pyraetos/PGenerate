package net.pyraetos.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Matrix implements Serializable{

	public float v00; public float v01; public float v02; public float v03;
	public float v10; public float v11; public float v12; public float v13;
	public float v20; public float v21; public float v22; public float v23;
	public float v30; public float v31; public float v32; public float v33;
	
	public Matrix(){}
	
	public Matrix(float...values){
		if(values.length == 16){
			v00 = values[0]; v01 = values[1]; v02 = values[2]; v03 = values[3];
			v10 = values[4]; v11 = values[5]; v12 = values[6]; v13 = values[7];
			v20 = values[8]; v21 = values[9]; v22 = values[10]; v23 = values[11];
			v30 = values[12]; v31 = values[13]; v32 = values[14]; v33 = values[15];
		}
	}
	
	public Matrix(Matrix m){
		v00 = m.v00; v01 = m.v01; v02 = m.v02; v03 = m.v03;
		v10 = m.v10; v11 = m.v11; v12 = m.v12; v13 = m.v13;
		v20 = m.v20; v21 = m.v21; v22 = m.v22; v23 = m.v23;
		v30 = m.v30; v31 = m.v31; v32 = m.v32; v33 = m.v33;
	}
	
	/**
	 * For immutable multiply, use static version
	 */
	public void multiply(Matrix b){
		float n00 = v00*b.v00 + v10*b.v01 + v20*b.v02 + v30*b.v03;
		float n01 = v01*b.v00 + v11*b.v01 + v21*b.v02 + v31*b.v03;
		float n02 = v02*b.v00 + v12*b.v01 + v22*b.v02 + v32*b.v03;
		float n03 = v03*b.v00 + v13*b.v01 + v23*b.v02 + v33*b.v03;
		
		float n10 = v00*b.v10 + v10*b.v11 + v20*b.v12 + v30*b.v13;
		float n11 = v01*b.v10 + v11*b.v11 + v21*b.v12 + v31*b.v13;
		float n12 = v02*b.v10 + v12*b.v11 + v22*b.v12 + v32*b.v13;
		float n13 = v03*b.v10 + v13*b.v11 + v23*b.v12 + v33*b.v13;
		
		float n20 = v00*b.v20 + v10*b.v21 + v20*b.v22 + v30*b.v23;
		float n21 = v01*b.v20 + v11*b.v21 + v21*b.v22 + v31*b.v23;
		float n22 = v02*b.v20 + v12*b.v21 + v22*b.v22 + v32*b.v23;
		float n23 = v03*b.v20 + v13*b.v21 + v23*b.v22 + v33*b.v23;
		
		float n30 = v00*b.v30 + v10*b.v31 + v20*b.v32 + v30*b.v33;
		float n31 = v01*b.v30 + v11*b.v31 + v21*b.v32 + v31*b.v33;
		float n32 = v02*b.v30 + v12*b.v31 + v22*b.v32 + v32*b.v33;
		float n33 = v03*b.v30 + v13*b.v31 + v23*b.v32 + v33*b.v33;
		
		v00 = n00; v01 = n01; v02 = n02; v03 = n03;
		v10 = n10; v11 = n11; v12 = n12; v13 = n13;
		v20 = n20; v21 = n21; v22 = n22; v23 = n23;
		v30 = n30; v31 = n31; v32 = n32; v33 = n33;
	}

	
	public static void multiply(Matrix a, Matrix b, Matrix c){
		float cv00 = a.v00*b.v00 + a.v01*b.v10 + a.v02*b.v20 + a.v03*b.v30;
		float cv01 = a.v00*b.v01 + a.v01*b.v11 + a.v02*b.v21 + a.v03*b.v31;
		float cv02 = a.v00*b.v02 + a.v01*b.v12 + a.v02*b.v22 + a.v03*b.v32;
		float cv03 = a.v00*b.v03 + a.v01*b.v13 + a.v02*b.v23 + a.v03*b.v33;
		
		float cv10 = a.v10*b.v00 + a.v11*b.v10 + a.v12*b.v20 + a.v13*b.v30;
		float cv11 = a.v10*b.v01 + a.v11*b.v11 + a.v12*b.v21 + a.v13*b.v31;
		float cv12 = a.v10*b.v02 + a.v11*b.v12 + a.v12*b.v22 + a.v13*b.v32;
		float cv13 = a.v10*b.v03 + a.v11*b.v13 + a.v12*b.v23 + a.v13*b.v33;
		
		float cv20 = a.v20*b.v00 + a.v21*b.v10 + a.v22*b.v20 + a.v23*b.v30;
		float cv21 = a.v20*b.v01 + a.v21*b.v11 + a.v22*b.v21 + a.v23*b.v31;
		float cv22 = a.v20*b.v02 + a.v21*b.v12 + a.v22*b.v22 + a.v23*b.v32;
		float cv23 = a.v20*b.v03 + a.v21*b.v13 + a.v22*b.v23 + a.v23*b.v33;

		float cv30 = a.v30*b.v00 + a.v31*b.v10 + a.v32*b.v20 + a.v33*b.v30;
		float cv31 = a.v30*b.v01 + a.v31*b.v11 + a.v32*b.v21 + a.v33*b.v31;
		float cv32 = a.v30*b.v02 + a.v31*b.v12 + a.v32*b.v22 + a.v33*b.v32;
		float cv33 = a.v30*b.v03 + a.v31*b.v13 + a.v32*b.v23 + a.v33*b.v33;

		c.v00 = cv00;
		c.v01 = cv01;
		c.v02 = cv02;
		c.v03 = cv03;
		c.v10 = cv10;
		c.v11 = cv11;
		c.v12 = cv12;
		c.v13 = cv13;
		c.v20 = cv20;
		c.v21 = cv21;
		c.v22 = cv22;
		c.v23 = cv23;
		c.v30 = cv30;
		c.v31 = cv31;
		c.v32 = cv32;
		c.v33 = cv33;
	}
	
	//Returns w
	public static float multiply(Matrix a, Vector b, float w, Vector c){
		float x = a.v00 * b.x + a.v01 * b.y + a.v02 * b.z + a.v03 * w;
		float y = a.v10 * b.x + a.v11 * b.y + a.v12 * b.z + a.v13 * w;
		float z = a.v20 * b.x + a.v21 * b.y + a.v22 * b.z + a.v23 * w;
		float ww = a.v30 * b.x + a.v31 * b.y + a.v32 * b.z + a.v33 * w;
		c.setX(x);
		c.setY(y);
		c.setZ(z);
		return ww;
	}
	
	public void transpose(){
		float oldv10 = v10; v10 = v01; v01 = oldv10;
		float oldv20 = v20; v20 = v02; v02 = oldv20;
		float oldv30 = v30; v30 = v03; v03 = oldv30;
		float oldv21 = v21; v21 = v12; v12 = oldv21;
		float oldv31 = v31; v31 = v13; v13 = oldv31;
		float oldv32 = v32; v32 = v23; v23 = oldv32;
	}
	
	@Override
	public String toString(){
		return v00 + " " + v01 + " " + v02 + " " + v03 + "\n"
				+ v10 + " " + v11 + " " + v12 + " " + v13 + "\n"
				+ v20 + " " + v21 + " " + v22 + " " + v23 + "\n"
				+ v30 + " " + v31 + " " + v32 + " " + v33 + "\n";
	}

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(v00);
		result = prime * result + Float.floatToIntBits(v01);
		result = prime * result + Float.floatToIntBits(v02);
		result = prime * result + Float.floatToIntBits(v03);
		result = prime * result + Float.floatToIntBits(v10);
		result = prime * result + Float.floatToIntBits(v11);
		result = prime * result + Float.floatToIntBits(v12);
		result = prime * result + Float.floatToIntBits(v13);
		result = prime * result + Float.floatToIntBits(v20);
		result = prime * result + Float.floatToIntBits(v21);
		result = prime * result + Float.floatToIntBits(v22);
		result = prime * result + Float.floatToIntBits(v23);
		result = prime * result + Float.floatToIntBits(v30);
		result = prime * result + Float.floatToIntBits(v31);
		result = prime * result + Float.floatToIntBits(v32);
		result = prime * result + Float.floatToIntBits(v33);
		return result;
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Matrix other = (Matrix)obj;
		if(Float.floatToIntBits(v00) != Float.floatToIntBits(other.v00))
			return false;
		if(Float.floatToIntBits(v01) != Float.floatToIntBits(other.v01))
			return false;
		if(Float.floatToIntBits(v02) != Float.floatToIntBits(other.v02))
			return false;
		if(Float.floatToIntBits(v03) != Float.floatToIntBits(other.v03))
			return false;
		if(Float.floatToIntBits(v10) != Float.floatToIntBits(other.v10))
			return false;
		if(Float.floatToIntBits(v11) != Float.floatToIntBits(other.v11))
			return false;
		if(Float.floatToIntBits(v12) != Float.floatToIntBits(other.v12))
			return false;
		if(Float.floatToIntBits(v13) != Float.floatToIntBits(other.v13))
			return false;
		if(Float.floatToIntBits(v20) != Float.floatToIntBits(other.v20))
			return false;
		if(Float.floatToIntBits(v21) != Float.floatToIntBits(other.v21))
			return false;
		if(Float.floatToIntBits(v22) != Float.floatToIntBits(other.v22))
			return false;
		if(Float.floatToIntBits(v23) != Float.floatToIntBits(other.v23))
			return false;
		if(Float.floatToIntBits(v30) != Float.floatToIntBits(other.v30))
			return false;
		if(Float.floatToIntBits(v31) != Float.floatToIntBits(other.v31))
			return false;
		if(Float.floatToIntBits(v32) != Float.floatToIntBits(other.v32))
			return false;
		if(Float.floatToIntBits(v33) != Float.floatToIntBits(other.v33))
			return false;
		return true;
	}

}