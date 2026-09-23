package spl.lae;

import java.util.List;

import memory.SharedMatrix;
import memory.SharedVector;
import parser.ComputationNode;
import parser.ComputationNodeType;
import scheduling.TiredExecutor;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        try {
            ComputationNode curr = computationRoot.findResolvable();
            while (curr != null) {
                loadAndCompute(curr);
                curr = computationRoot.findResolvable();
            }
            return computationRoot;
        } finally {
            try {
                executor.shutdown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        if (node.getChildren().size() == 1) {
            leftMatrix.loadRowMajor(node.getChildren().get(0).getMatrix());
            if (node.getNodeType() == ComputationNodeType.NEGATE) {
                List<Runnable> tasks = createNegateTasks();
                executor.submitAll(tasks);
            } else if (node.getNodeType() == ComputationNodeType.TRANSPOSE) {
                List<Runnable> tasks = createTransposeTasks();
                executor.submitAll(tasks);
            } else {
                throw new IllegalArgumentException("Illegal operation: unary operation must have only one opperand");
            }
            node.resolve(leftMatrix.readRowMajor());
        } else {
            node.associativeNesting();
            ComputationNode curr = node.findResolvable();
            while (curr != null) {
                leftMatrix.loadRowMajor(curr.getChildren().get(0).getMatrix());
                rightMatrix.loadRowMajor(curr.getChildren().get(1).getMatrix());
                if (curr.getNodeType() == ComputationNodeType.ADD) {
                    List<Runnable> tasks = createAddTasks();
                    executor.submitAll(tasks);
                } else if (curr.getNodeType() == ComputationNodeType.MULTIPLY) {
                    List<Runnable> tasks = createMultiplyTasks();
                    executor.submitAll(tasks);
                } else {
                    throw new IllegalArgumentException("Illegal operation: can not perform unary opertion on two opperands");
                }
                curr.resolve(leftMatrix.readRowMajor());
                curr = node.findResolvable();
            }
        }
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        if (leftMatrix.length() != rightMatrix.length()) {
            throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
        }
        for (int i = 0; i < leftMatrix.length(); i++) {
            if (leftMatrix.get(i).length() != rightMatrix.get(i).length()) {
                throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
            }
        }
        Runnable[] tasks = new Runnable[leftMatrix.length()];
        for (int i = 0; i < tasks.length; i++) {
            SharedVector vec1 = leftMatrix.get(i);
            SharedVector vec2 = rightMatrix.get(i);
            tasks[i] = () -> vec1.add(vec2);
        }
        return java.util.Arrays.asList(tasks);
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        for (int i = 0; i < leftMatrix.length(); i++) {
            if (leftMatrix.get(i).length() != rightMatrix.length()) {
                throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
            }
        }
        Runnable[] tasks = new Runnable[leftMatrix.length()];
        for (int i = 0; i < tasks.length; i++) {
            SharedVector vec1 = leftMatrix.get(i);
            tasks[i] = () -> vec1.vecMatMul(rightMatrix);
        }
        return java.util.Arrays.asList(tasks);
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        Runnable[] tasks = new Runnable[leftMatrix.length()];
        for (int i = 0; i < tasks.length; i++) {
            SharedVector vec1 = leftMatrix.get(i);
            tasks[i] = () -> vec1.negate();
        }
        return java.util.Arrays.asList(tasks);
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        Runnable[] tasks = new Runnable[leftMatrix.length()];
        for (int i = 0; i < tasks.length; i++) {
            SharedVector vec1 = leftMatrix.get(i);
            tasks[i] = () -> vec1.transpose();
        }
        return java.util.Arrays.asList(tasks);
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}
