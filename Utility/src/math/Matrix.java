package math;


/**
 * M x N 矩阵
 *
 * @author Liuzhenbin
 * @date 2021-04-21 15:39
 **/
public class Matrix {

    private final float[] items;
    private final int rowNum;
    private final int columnNum;

    public Matrix(float[] items, int rowNum, int columnNum) {
        if(items.length != rowNum * columnNum)
            throw new IllegalArgumentException("items length not equal to rowNum multiply colNum");
        this.items = items;
        this.rowNum = rowNum;
        this.columnNum = columnNum;
    }

    public float item(int row , int col) {
        if(row<1||row> rowNum()||col<1||col> columnNum())
            throw new IllegalArgumentException("row or col out of range");
        return this.items[(row - 1) * columnNum + col-1];
    }

    public Vector rowVector(int row) {
        float[] re = new float[columnNum];
        for (int i = (row - 1) * columnNum, j = 0; j < columnNum; i++,j++) {
            re[j] = this.items[i];
        }
        return new Vector(re);
    }

    public Vector columnVector(int column) {
        int rows = rowNum();
        float[] result = new float[rows];
        for (int i = 1; i <= rows; i++) {
            result[i - 1] = item(i, column);
        }
        return new Vector(result);
    }
    public int rowNum() {
        return rowNum;
    }

    public int columnNum() {
        return columnNum;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < rowNum; r++) {
            sb.append("|");
            for (int c = 0; c < columnNum; c++) {
                sb.append(this.items[r * columnNum + c]);
                if(c!=columnNum-1)
                    sb.append(",");
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    public static Matrix add(Matrix l, Matrix r) {
        if (l.rowNum() != r.rowNum() || l.columnNum() != r.columnNum())
            throw new IllegalArgumentException("rowNum or columnNum is not equal! ");
        int rowLength = l.rowNum;
        int columnLength = l.columnNum;
        float[] result = new float[rowLength * columnLength];
        for (int i = 0; i < rowLength; i++) {
            for (int j = 0; j < columnLength; j++) {
                result[i * columnLength + j] = l.item(i + 1, j + 1) + r.item(i + 1, j + 1);
            }
        }
        return new Matrix(result, rowLength, columnLength);
    }

    public static Matrix multiply(Matrix l,Matrix r) {
        int l_rowLength = l.rowNum();
        int r_columnLength = r.columnNum();
        if(l_rowLength != r_columnLength)
            throw new IllegalArgumentException("left matrix rowNum is not equal to right matrix columnNum");
        float[] result = new float[l_rowLength * r_columnLength];
        for (int i = 0; i < l_rowLength; i++) {
            Vector lRowVector = l.rowVector(i+1);
            for (int j = 0; j < r_columnLength; j++) {
                Vector rColumnVector = r.columnVector(j+1);
                result[i*r_columnLength+j] = Vector.dotProduct(lRowVector, rColumnVector);
            }
        }
        return new Matrix(result, l_rowLength, r_columnLength);
    }

    public Matrix scale(float scale) {
        int rowL = rowNum();
        int columnL = columnNum();
        float[] result = new float[rowL*columnL];
        for (int i = 0; i < rowL; i++) {
            for (int j = 0; j < columnL; j++) {
                result[i * columnNum() + j] = scale * item(i + 1, j + 1);
            }
        }
        return new Matrix(result, rowL, columnL);
    }

    /**
     * 余子矩阵
     * @date 2021-04-21 18:00
     * @param row 行号
     * @param col 列号
     * @return Math.MatrixMxN
     **/
    public Matrix subMatrix(int row, int col){
        int rowL = rowNum();
        int columnL = columnNum();
        if(row<1||row>rowL||col<1||col>columnL) throw new IllegalArgumentException("row or col out of range");
        float[] sub = new float[(rowL - 1) * (columnL - 1)];
        int index = 0;
        for (int i = 0; i < rowL; i++) {
            if(row == i+1) continue;
            for (int j = 0; j < columnL; j++) {
                if (col == j + 1) continue;
                sub[index++] = item(i + 1, j + 1);
            }
        }
        return new Matrix(sub, rowL-1, columnL-1);
    }

    /**
     * 余子式
     * @date 2021-04-22 8:59
     * @param row 行号
     * @param column 列号
     * @return float
     **/
    public float minor(int row,int column) {
        return  subMatrix(row, column).determinant();
    }

    /**
     * 代数余子式
     * @date 2021-04-22 8:59
     * @param row 行号
     * @param column 列号
     * @return float
     **/
    public float cofactor(int row,int column) {
        return (float) Math.pow(-1, row + column) * minor(row, column);
    }

    /**
     * 行列式
     * @date 2021-04-21 17:59
     * @return float
     **/
    public  float determinant(){
        int rows = rowNum();
        int columns = columnNum();
        if(rows != columns) throw new IllegalArgumentException("not a square matrix!");
        float det = 0;
        if(rows == 1) {
            det = item(1, 1);
        }
        else {
            for (int i = 0; i < columns; i++) {
                det = det + item(1, i+1) * cofactor(1, i + 1);
            }
        }
        return det;
    }

    /**
     * 转置矩阵
     * @date 2021-04-22 12:09
     * @param
     * @return Math.Matrix
     **/
    public Matrix transpose() {
        int rowL = columnNum();
        int columnL = rowNum();
        float[] result = new float[rowL * columnL];
        for (int i = 0; i < rowL; i++) {
            for (int j = 0; j < columnL; j++) {
                result[i * columnL + j] = item(j + 1, i + 1);
            }
        }
        return new Matrix(result, rowL, columnL);
    }

    /**
     * 伴随矩阵
     * @date 2021-04-22 12:08
     * @return Math.Matrix
     **/
    public Matrix adjugate() {
        int rowL = rowNum();
        int columnL = columnNum();
        float[] value = new float[rowL*columnL];
        for (int i = 0; i < rowL; i++) {
            for (int j = 0; j < columnL; j++) {
                value[i * columnNum() + j] = cofactor(i+1, j+1);
            }
        }
        //代数余子式矩阵的转置矩阵
        return new Matrix(value, rowL, columnL).transpose();
    }

    /**
     * 逆矩阵
     * @date 2021-04-22 12:08
     * @return Math.Matrix
     **/
    public Matrix inverse() {
        float det = determinant();
        Matrix adj = adjugate();
        return adj.scale(1f / det);
    }
}
