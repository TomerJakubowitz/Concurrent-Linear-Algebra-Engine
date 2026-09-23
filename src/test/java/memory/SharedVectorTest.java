package memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class SharedVectorTest {

    @Test
    void testNegate_PositiveNumbers_ShouldBecomeNegative() {
        SharedVector v = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        v.negate();
        assertEquals(-1.0, v.get(0));
        assertEquals(-2.0, v.get(1));
    }

    @Test
    void testNegate_NegativeNumbers_ShouldBecomePositive() {
        SharedVector v = new SharedVector(new double[]{-1.0, -5.0}, VectorOrientation.ROW_MAJOR);
        v.negate();
        assertEquals(1.0, v.get(0));
        assertEquals(5.0, v.get(1));
    }

    @Test
    void testNegate_ZeroVector_ShouldRemainZero() {
        SharedVector v = new SharedVector(new double[]{0.0, 0.0}, VectorOrientation.ROW_MAJOR);
        v.negate();
        assertEquals(0.0, v.get(0));
    }

    @Test
    void testAdd_StandardVectors_ShouldSumElementWise() {
        SharedVector v1 = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{3.0, 4.0}, VectorOrientation.ROW_MAJOR);
        v1.add(v2);
        assertEquals(4.0, v1.get(0));
        assertEquals(6.0, v1.get(1));
    }

    @Test
    void testAdd_WithNegativeValues_ShouldSumCorrectly() {
        SharedVector v1 = new SharedVector(new double[]{10.0, 20.0}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{-5.0, -25.0}, VectorOrientation.ROW_MAJOR);
        v1.add(v2);
        assertEquals(5.0, v1.get(0));
        assertEquals(-5.0, v1.get(1));
    }

    @Test
    void testTranspose_RowToColumn_ShouldChangeOrientation() {
        SharedVector v = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        v.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());
    }

    @Test
    void testTranspose_ColumnToRow_ShouldChangeOrientation() {
        SharedVector v = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.COLUMN_MAJOR);
        v.transpose();
        assertEquals(VectorOrientation.ROW_MAJOR, v.getOrientation());
    }

    @Test
    void testTranspose_DataShouldRemainUnchanged() {
        SharedVector v = new SharedVector(new double[]{5.5, 9.9}, VectorOrientation.ROW_MAJOR);
        v.transpose();
        assertEquals(5.5, v.get(0));
        assertEquals(9.9, v.get(1));
    }

    @Test
    void testTranspose_DoubleTranspose_ShouldReturnToOriginal() {
        SharedVector v = new SharedVector(new double[]{1.0}, VectorOrientation.ROW_MAJOR);
        v.transpose();
        v.transpose();
        assertEquals(VectorOrientation.ROW_MAJOR, v.getOrientation());
    }

    @Test
    void testDot_RowDotColumn_ShouldWork() {
        SharedVector row = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        SharedVector col = new SharedVector(new double[]{3.0, 4.0}, VectorOrientation.COLUMN_MAJOR);
        assertEquals(11.0, row.dot(col));
    }

    @Test
    void testDot_OrthogonalVectors_ShouldReturnZero() {
        SharedVector v1 = new SharedVector(new double[]{1.0, 0.0}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{0.0, 1.0}, VectorOrientation.COLUMN_MAJOR);
        assertEquals(0.0, v1.dot(v2));
    }

    @Test
    void testDot_WithNegativeValues_ShouldCalcCorrectly() {
        SharedVector v1 = new SharedVector(new double[]{1.0, -2.0}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{-1.0, -2.0}, VectorOrientation.COLUMN_MAJOR);
        assertEquals(3.0, v1.dot(v2));
    }

    @Test
    void testVecMatMul_StandardMultiplication_RowMajor() {
        SharedVector v = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        double[][] matrixData = {{3.0, 4.0}, {5.0, 6.0}};
        SharedMatrix matrix = new SharedMatrix(matrixData);
        v.vecMatMul(matrix);
        assertEquals(2, v.length());
        assertEquals(13.0, v.get(0));
        assertEquals(16.0, v.get(1));
    }

    @Test
    void testVecMatMul_WithResizing_VectorLengthShouldChange() {
        SharedVector v = new SharedVector(new double[]{1.0, 1.0}, VectorOrientation.ROW_MAJOR);
        double[][] matrixData = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        SharedMatrix matrix = new SharedMatrix(matrixData);
        v.vecMatMul(matrix);
        assertEquals(3, v.length());
        assertEquals(5.0, v.get(0));
        assertEquals(7.0, v.get(1));
        assertEquals(9.0, v.get(2));
    }

    @Test
    void testVecMatMul_ZeroMatrix_ShouldResultInZeroVector() {
        SharedVector v = new SharedVector(new double[]{10.0, 20.0}, VectorOrientation.ROW_MAJOR);
        double[][] zeroData = {{0.0, 0.0}, {0.0, 0.0}};
        SharedMatrix matrix = new SharedMatrix(zeroData);
        v.vecMatMul(matrix);
        assertEquals(0.0, v.get(0));
        assertEquals(0.0, v.get(1));
    }

    @Test
    void testVecMatMul_WithColumnMajorMatrix() {
        SharedVector v = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        double[][] rawData = {{1.0, 2.0}, {3.0, 4.0}};
        SharedMatrix matrix = new SharedMatrix();
        matrix.loadColumnMajor(rawData);
        v.vecMatMul(matrix);
        assertEquals(5.0, v.get(0));
        assertEquals(11.0, v.get(1));
    }
}
