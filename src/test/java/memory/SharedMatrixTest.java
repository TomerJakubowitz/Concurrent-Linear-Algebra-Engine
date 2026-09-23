package memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class SharedMatrixTest {
    
    @Test
    void testLoadRowMajor_ShouldUpdateDataAndOrientation() {
        SharedMatrix m = new SharedMatrix();
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        m.loadRowMajor(data);
        assertEquals(2, m.length());
        assertEquals(VectorOrientation.ROW_MAJOR, m.getOrientation());
        assertEquals(1.0, m.get(0).get(0));
    }
    @Test
    void testLoadColumnMajor_ShouldUpdateDataAndOrientation() {
        SharedMatrix m = new SharedMatrix();
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        m.loadColumnMajor(data);
        assertEquals(2, m.length());
        assertEquals(VectorOrientation.COLUMN_MAJOR, m.getOrientation());
        assertEquals(1.0, m.get(0).get(0));
        assertEquals(2.0, m.get(0).get(1));
    }
    @Test
    void testReadRowMajor_FromRowMajorStorage() {
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        SharedMatrix m = new SharedMatrix(data);
        double[][] result = m.readRowMajor();
        assertEquals(1.0, result[0][0]);
        assertEquals(2.0, result[0][1]);
        assertEquals(3.0, result[1][0]);
        assertEquals(4.0, result[1][1]);
    }
    @Test
    void testReadRowMajor_FromColumnMajorStorage_ShouldTransposeBack() {
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        SharedMatrix m = new SharedMatrix();
        m.loadColumnMajor(data);
        double[][] result = m.readRowMajor();
        assertEquals(1.0, result[0][0]);
        assertEquals(3.0, result[0][1]);
        assertEquals(2.0, result[1][0]);
        assertEquals(4.0, result[1][1]);
    }
    @Test
    void testReadRowMajor_NonSquareMatrix_RowMajor() {
        double[][] data = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        SharedMatrix m = new SharedMatrix(data);
        double[][] result = m.readRowMajor();
        assertEquals(2, result.length);
        assertEquals(3, result[0].length);
        assertEquals(6.0, result[1][2]);
    }
    @Test
    void testReadRowMajor_NonSquareMatrix_ColumnMajor() {
        double[][] data = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        SharedMatrix m = new SharedMatrix();
        m.loadColumnMajor(data);
        double[][] result = m.readRowMajor();
        assertEquals(3, result.length);
        assertEquals(2, result[0].length);
        assertEquals(2.0, result[1][0]);
    }
    @Test
    void testReadRowMajor_EmptyMatrix() {
        SharedMatrix m = new SharedMatrix();
        double[][] result = m.readRowMajor();
        assertEquals(0, result.length);
    }
    @Test
    void testLoadRowMajor_ShouldOverridePreviousData() {
        SharedMatrix m = new SharedMatrix(new double[][]{{1.0}});
        double[][] newData = {{2.0, 3.0}};
        m.loadRowMajor(newData);
        double[][] result = m.readRowMajor();
        assertEquals(2.0, result[0][0]);
        assertEquals(3.0, result[0][1]);
    }
    @Test
    void testGet_ShouldReturnCorrectRowVector() {
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        SharedMatrix m = new SharedMatrix(data);
        SharedVector row = m.get(1);
        assertEquals(3.0, row.get(0));
        assertEquals(4.0, row.get(1));
        assertEquals(VectorOrientation.ROW_MAJOR, row.getOrientation());
    }
}
