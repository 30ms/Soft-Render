package Math;


/**
 * M x N 矩阵
 *
 * @author Liuzhenbin
 * @date 2021-04-21 15:39
 **/
public class Matrix {

    private float[][] value;

    public Matrix(float[][] value) {
        this.value = value;
    }

    public Matrix(float[] values, int rows, int columns) {
        this.value = new float[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.value[i][j] = values[i * columns + j];
            }
        }
    }

    public float el(int row ,int col) {
        return this.value[row - 1][col - 1];
    }

    public int getRows() {
        return value.length;
    }

    public int getColumns() {
        return value[0].length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (float[] row : this.value) {
            for (float e : row) {
                sb.append(e).append(",");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 余子矩阵
     * @date 2021-04-21 18:00
     * @param row 行号
     * @param col 列号
     * @return Math.MatrixMxN
     **/
    public Matrix subMatrix(int row, int col){
        int rows = getRows();
        int columns = getColumns();
        if(row<1||row>rows||col<1||col>columns) throw new IllegalArgumentException("row or col error");
        float[] sub = new float[(rows - 1) * (columns - 1)];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            if(row == i+1) continue;
            for (int j = 0; j < columns; j++) {
                if (col == j + 1) continue;
                sub[index++] = value[i][j];
            }
        }
        return new Matrix(sub, rows - 1, columns - 1);
    }


    /**
     * 行列式
     * @date 2021-04-21 17:59
     * @return float
     **/
    public  float determinant(){
        int rows = getRows();
        int columns = getColumns();
        if(rows != columns) throw new RuntimeException("not a square matrix!");
        float det = 0;
        if(rows == 1) {
            det = el(1, 1);
        }
        else {
            for (int i = 1; i <= columns; i++) {
                Matrix subM = subMatrix(1, i);
                float subDet = subM.determinant();
                det = det + (float) Math.pow(-1, 1 + i) * el(1, i) * subDet;

            }
        }
        return det;
    }

    public Matrix add(Matrix l, Matrix r) {
        if(l.getRows() != r.getRows() || l.getColumns() != r.getColumns())
            throw new IllegalArgumentException("the num of row or column not equal! ");
        float[][] result = new float[getRows()][getColumns()];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; i++) {
                result[i][j] = l.el(i + 1, j + 1) + r.el(i + 1, j + 1);
            }
        }
        return new Matrix(result);
    }

    public static void main(String[] args) {
        Matrix matrix3x3 = new Matrix(new float[][]{{1,4,7},{3,0,5},{-1,9,11}});
        System.out.println(matrix3x3.determinant());
    }

}
