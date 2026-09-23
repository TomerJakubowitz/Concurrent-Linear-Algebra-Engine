package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            SharedVector vector = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
            vectors[i] = vector;
        }
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            SharedVector vector = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
            vectors[i] = vector;
        }
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            double[] curr = new double[matrix[i].length];
            for (int j = 0; j < matrix[i].length; j++) {
                curr[j] = matrix[i][j];
            }
            SharedVector vector = new SharedVector(curr, VectorOrientation.COLUMN_MAJOR);
            vectors[i] = vector;
        }
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        acquireAllVectorReadLocks(vectors);
        try {
             if (vectors != null && vectors.length != 0) {
            double[][] toReturn;
            if (vectors[0].getOrientation() == VectorOrientation.ROW_MAJOR) {
                toReturn = new double[vectors.length][vectors[0].length()];
                for (int i = 0; i < vectors.length; i++) {
                    for(int j = 0; j < vectors[i].length(); j++){
                        toReturn[i][j] = vectors[i].get(j);
                    }
                }
            }else{
                toReturn = new double[vectors[0].length()][vectors.length];
                for (int i = 0; i < vectors.length; i++) {
                    for(int j = 0; j < vectors[i].length(); j++){
                        toReturn[j][i] = vectors[i].get(j);
                    }
                }
            }
            return toReturn;
            } else {
                return new double[0][0];
            }
        } finally {
            releaseAllVectorReadLocks(vectors);
        }
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        if(vectors != null && index >= 0 && index < vectors.length){
             return vectors[index];
        }
        return null;
    }

    public int length() {
        // TODO: return number of stored vectors
        if(vectors != null){
             return vectors.length;
        }
        return 0;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        if(vectors != null){
             return vectors[0].getOrientation();
        }
        return null;
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for(SharedVector v: vecs){
            v.readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for(SharedVector v: vecs){
            v.readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for(SharedVector v: vecs){
            v.writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        for(SharedVector v: vecs){
            v.writeUnlock();
        }
    }
}
