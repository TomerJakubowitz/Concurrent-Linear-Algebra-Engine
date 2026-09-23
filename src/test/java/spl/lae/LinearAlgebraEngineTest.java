package spl.lae;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import parser.ComputationNode;
import parser.ComputationNodeType;

public class LinearAlgebraEngineTest {

    @Test
    void testRun_NegateUnaryOperation() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(1);
        double[][] data = {{1.0, -2.0}};
        ComputationNode leaf = new ComputationNode(data);
        ComputationNode root = new ComputationNode(ComputationNodeType.NEGATE, java.util.List.of(leaf));
        ComputationNode result = engine.run(root);
        double[][] out = result.getMatrix();
        assertEquals(-1.0, out[0][0]);
        assertEquals(2.0, out[0][1]);
    }

    @Test
    void testRun_TransposeUnaryOperation() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(1);
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        ComputationNode leaf = new ComputationNode(data);
        ComputationNode root = new ComputationNode(ComputationNodeType.TRANSPOSE, java.util.List.of(leaf));
        ComputationNode result = engine.run(root);
        double[][] out = result.getMatrix();
        assertEquals(1.0, out[0][0]);
        assertEquals(3.0, out[0][1]);
        assertEquals(2.0, out[1][0]);
        assertEquals(4.0, out[1][1]);
    }

    @Test
    void testRun_AddBinaryOperation() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        ComputationNode left = new ComputationNode(new double[][]{{1.0, 2.0}});
        ComputationNode right = new ComputationNode(new double[][]{{3.0, 4.0}});
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, java.util.List.of(left, right));
        ComputationNode result = engine.run(root);
        double[][] out = result.getMatrix();
        assertEquals(4.0, out[0][0]);
        assertEquals(6.0, out[0][1]);
    }

    @Test
    void testRun_MultiplyBinaryOperation() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        ComputationNode left = new ComputationNode(new double[][]{{1.0, 2.0}});
        ComputationNode right = new ComputationNode(new double[][]{{3.0, 4.0}, {5.0, 6.0}});
        ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, java.util.List.of(left, right));
        ComputationNode result = engine.run(root);
        double[][] out = result.getMatrix();
        assertEquals(13.0, out[0][0]);
        assertEquals(16.0, out[0][1]);
    }

    @Test
    void testAdd_DimensionMismatch_ShouldThrow() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(1);
        ComputationNode left = new ComputationNode(new double[][]{{1.0}});
        ComputationNode right = new ComputationNode(new double[][]{{1.0, 2.0}});
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, java.util.List.of(left, right));
        assertThrows(IllegalArgumentException.class, () -> engine.run(root));
    }

    @Test
    void testRun_ChainedOperations_AddThenNegate() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        ComputationNode a = new ComputationNode(new double[][]{{1.0, 2.0}});
        ComputationNode b = new ComputationNode(new double[][]{{3.0, 4.0}});
        ComputationNode add = new ComputationNode(ComputationNodeType.ADD, java.util.List.of(a, b));
        ComputationNode root = new ComputationNode(ComputationNodeType.NEGATE, java.util.List.of(add));
        ComputationNode result = engine.run(root);
        double[][] out = result.getMatrix();
        assertEquals(-4.0, out[0][0]);
        assertEquals(-6.0, out[0][1]);
    }

    @Test
    void testRun_ExceptionStillShutsDownExecutor() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(1);
        ComputationNode left = new ComputationNode(new double[][]{{1.0}});
        ComputationNode right = new ComputationNode(new double[][]{{1.0, 2.0}});
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, java.util.List.of(left, right));
        assertThrows(IllegalArgumentException.class, () -> engine.run(root));
        String report = engine.getWorkerReport();
        assertNotNull(report);
    }

    @Test
    void testLoadAndCompute_UnaryWithIllegalType_ShouldThrow() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(1);
        ComputationNode leaf = new ComputationNode(new double[][]{{1.0}});
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, java.util.List.of(leaf));
        assertThrows(IllegalArgumentException.class, () -> engine.run(root));
    }

    @Test
    void testRun_AssociativeAdd_ThreeMatrices() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        ComputationNode a = new ComputationNode(new double[][]{{1.0}});
        ComputationNode b = new ComputationNode(new double[][]{{2.0}});
        ComputationNode c = new ComputationNode(new double[][]{{3.0}});
        java.util.List<ComputationNode> children = new java.util.ArrayList<>();
        children.add(a);
        children.add(b);
        children.add(c);
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, children); 
        ComputationNode result = engine.run(root);
        double[][] out = result.getMatrix();
        assertEquals(6.0, out[0][0]);
    }

    @Test
    void testRun_ComplexTree_AddThenMultiply() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        double[][] dataA = {{1.0, 2.0}};
        double[][] dataB = {{3.0, 4.0}};
        double[][] dataC = {{2.0}, {1.0}};
        ComputationNode a = new ComputationNode(dataA);
        ComputationNode b = new ComputationNode(dataB);
        ComputationNode c = new ComputationNode(dataC);
        ComputationNode addNode = new ComputationNode(ComputationNodeType.ADD, java.util.List.of(a, b));
        ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, java.util.List.of(addNode, c));
        ComputationNode result = engine.run(root);
        double[][] out = result.getMatrix();
        assertEquals(14.0, out[0][0]);
    }

    @Test
    void testMultiply_DimensionMismatch_ShouldThrow() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(1);
        ComputationNode left = new ComputationNode(new double[][]{{1.0, 2.0}});
        ComputationNode right = new ComputationNode(new double[][]{{3.0, 4.0}});
        ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, java.util.List.of(left, right));
        assertThrows(IllegalArgumentException.class, () -> engine.run(root));
    }

    @Test
    void testMultiply_NonSquareMatrices_Valid() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        double[][] dataLeft = {{1.0, 2.0}};
        double[][] dataRight = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        ComputationNode left = new ComputationNode(dataLeft);
        ComputationNode right = new ComputationNode(dataRight);
        ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, java.util.List.of(left, right));
        ComputationNode result = engine.run(root);
        double[][] out = result.getMatrix();
        assertEquals(1, out.length);
        assertEquals(3, out[0].length);
        assertEquals(9.0, out[0][0]);
        assertEquals(15.0, out[0][2]);
    }

    @Test
    void testBinaryOp_WithUnaryNodeStructure_ShouldThrow() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(1);
        ComputationNode leaf = new ComputationNode(new double[][]{{1.0}});
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, java.util.List.of(leaf));
        Exception e = assertThrows(IllegalArgumentException.class, () -> engine.run(root));
        assertTrue(e.getMessage().contains("unary operation"));
    }

    @Test
    void testUnaryOp_WithBinaryNodeStructure_ShouldThrow() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(1);
        ComputationNode leaf1 = new ComputationNode(new double[][]{{1.0}});
        ComputationNode leaf2 = new ComputationNode(new double[][]{{2.0}});
        ComputationNode root = new ComputationNode(ComputationNodeType.NEGATE, java.util.List.of(leaf1, leaf2));
        Exception e = assertThrows(IllegalArgumentException.class, () -> engine.run(root));
        assertTrue(e.getMessage().contains("two opperands"));
    }

    @Test
    void testTranspose_NonSquare_ShouldSwapDimensions() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(1);
        double[][] data = {{1.0}, {2.0}};
        ComputationNode leaf = new ComputationNode(data);
        ComputationNode root = new ComputationNode(ComputationNodeType.TRANSPOSE, java.util.List.of(leaf));
        ComputationNode result = engine.run(root);
        double[][] out = result.getMatrix();
        assertEquals(1, out.length);
        assertEquals(2, out[0].length);
        assertEquals(1.0, out[0][0]);
        assertEquals(2.0, out[0][1]);
    }
}
