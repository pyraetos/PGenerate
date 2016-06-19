package net.pyraetos.util;

public class MatrixD extends Matrix<Double> {
	
	public MatrixD(Double[][] matrix) {
		super(matrix);
	}
	
	public MatrixD(int w, int h){
		if(w <= 0 || h <= 0){
			System.err.println("Matrix cannot be 0 in any dimension!");
			System.exit(1);
		}
		matrix = new Double[w][h];
	}
	
	public MatrixD multiply(Matrix<Double> b){
		int aw = getWidth();
		int ah = getHeight();
		int bw = b.getWidth();
		int bh = b.getHeight();
		if(aw != bh)
			return null;
		MatrixD c = new MatrixD(bw, ah);
		for(int arow = 0; arow < ah; arow++){
			for(int bcol = 0; bcol < bw; bcol++){
				double val = 0d;
				for(int i = 0; i < aw; i++){
					val += matrix[i][arow] * b.matrix[bcol][i];
				}
				c.set(bcol, arow, val);
			}
		}
		return c;
	}

}
