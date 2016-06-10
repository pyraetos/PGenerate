package net.pyraetos.util;

public class Matrix<T>{

	private T[][] matrix;
	
	public Matrix(T[][] matrix){
		if(matrix.length == 0 || matrix[0].length == 0){
			System.err.println("Matrix cannot be 0 in any dimension!");
			System.exit(1);
		}
		this.matrix = matrix;
	}
	
	public T get(int x, int y){
		try{
			return matrix[x][y];
		}catch(Exception e){
			return null;
		}
	}
	
	public void set(int x, int y, T t){
		try{
			matrix[x][y] = t;
		}catch(Exception e){
			return;
		}
	}
	
	public int getWidth(){
		return matrix.length;
	}
	
	public int getHeight(){
		return matrix[0].length;
	}
	
	@Override
	public String toString(){
		String string = "";
		for(int y = 0; y < getWidth(); y++){
			for(int x = 0; x < getHeight(); x++){
				string += get(x, y) + " ";
			}
			string += "\n";
		}
		return string;
	}

}