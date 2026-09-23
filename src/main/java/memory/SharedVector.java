package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        this.vector = vector;
        this.orientation = orientation;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        readLock();
        if (vector != null && index >= 0 && index < vector.length) {
            return vector[index];
        }
        readUnlock();
        return 0.0;
    }

    public int length() {
        // TODO: return vector length
        if (vector != null) {
            return vector.length;
        }
        return 0;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        return orientation;
    }

    public void writeLock() {
        // TODO: acquire write lock
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        writeLock();
        try {
            if (orientation == VectorOrientation.COLUMN_MAJOR) {
                orientation = VectorOrientation.ROW_MAJOR;
            } else {
                orientation = VectorOrientation.COLUMN_MAJOR;
            }
        }finally{
            writeUnlock();
        }
    }
    public void add(SharedVector other) {
        // TODO: add two vectors
        writeLock();
        other.readLock();
        try {
            for (int i = 0; i < vector.length; i++) {
                vector[i] = vector[i] + other.get(i);
            }
        }finally{
            writeUnlock();
            other.readUnlock();
        }
    }

    public void negate() {
        // TODO: negate vector
        writeLock();
        try {
            for (int i = 0; i < vector.length; i++) {
                vector[i] = 0 - vector[i];
            }
        }finally{
            writeUnlock();
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        readLock();
        other.readLock();
        double sum = 0;
        try {
            for (int i = 0; i < vector.length; i++) {
                sum += vector[i] * other.get(i);
            }
        }finally{
            readUnlock();
            other.readUnlock();
        }
        return sum;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        writeLock();
        try {
            double[][] mat = matrix.readRowMajor(); 
            double[] newVec = new double[mat[0].length];
            for (int i = 0; i < mat[0].length; i++) {
                double sum = 0;
                for (int j = 0; j < mat.length; j++) {
                    sum += vector[j] * mat[j][i];
                }
                newVec[i] = sum;
            }
            vector = newVec;
        }finally{
            writeUnlock();
        }
    }
}
