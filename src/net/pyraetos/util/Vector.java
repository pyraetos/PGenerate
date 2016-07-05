package net.pyraetos.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Vector implements Serializable{

	protected float x;
	protected float y;
	protected float z;
	
	public Vector(Vector v){
		this(v.getX(), v.getY(), v.getZ());
	}
	
	public Vector(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public float getZ(){
		return z;
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}
	
	public void setZ(float z){
		this.z = z;
	}
	
	public void multiply(float scalar){
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}
	
	public static void multiply(Vector base, float scalar, Vector dest){
		dest.setX(base.x * scalar);
		dest.setY(base.y * scalar);
		dest.setZ(base.z * scalar);
	}
	
	public static float multiply(Vector a, float wa, Vector b, float wb){
		return a.x * b.x + a.y * b.y + a.z * b.z + wa * wb;
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
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
		Vector other = (Vector)obj;
		if(Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if(Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if(Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}
	
	@Override
	public String toString(){
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
