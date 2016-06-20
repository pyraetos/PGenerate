package net.pyraetos.util;

public class MatrixF extends Matrix<Float> {
	
	public MatrixF(Float[][] matrix) {
		super(matrix);
	}
	
	public MatrixF(int w, int h){
		if(w <= 0 || h <= 0){
			System.err.println("Matrix cannot be 0 in any dimension!");
			System.exit(1);
		}
		matrix = new Float[w][h];
	}
	
	public MatrixF multiply(Matrix<Float> b){
		int aw = getWidth();
		int ah = getHeight();
		int bw = b.getWidth();
		int bh = b.getHeight();
		if(aw != bh)
			return null;
		MatrixF c = new MatrixF(bw, ah);
		for(int arow = 0; arow < ah; arow++){
			for(int bcol = 0; bcol < bw; bcol++){
				float val = 0f;
				for(int i = 0; i < aw; i++){
					val += matrix[i][arow] * b.matrix[bcol][i];
				}
				c.set(bcol, arow, val);
			}
		}
		return c;
	}

}
