package net.pyraetos.util;

public class Matrix<T>{

	protected T[][] matrix;
	
	protected Matrix(){}
	
	public Matrix(T[][] matrix){
		if(matrix.length == 0 || matrix[0].length == 0){
			System.err.println("Matrix cannot be 0 in any dimension!");
			System.exit(1);
		}
		this.matrix = matrix;
	}
	
	public void transpose(){
		if(getWidth() != getHeight())
			return;
		for(int x = 0; x < getWidth(); x++){
			for(int y = 0; y < x; y++){
				T temp = matrix[x][y];
				matrix[x][y] = matrix[y][x];
				matrix[y][x] = temp;
			}
		}
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
	
	@SuppressWarnings("unchecked")
	public void setRow(int row, T... values){
		if(values.length != getWidth())
			return;
		for(int i = 0; i < values.length; i++){
			matrix[i][row] = values[i];
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setColumn(int col, T... values){
		if(values.length != getHeight())
			return;
		for(int i = 0; i < values.length; i++){
			matrix[col][i] = values[i];
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
		for(int y = 0; y < getHeight(); y++){
			for(int x = 0; x < getWidth(); x++){
				string += get(x, y) + " ";
			}
			string += "\n";
		}
		return string;
	}

}