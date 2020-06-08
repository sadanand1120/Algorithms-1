/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    /* we will NOT actually make the two virtual sites, rather we will just imagine that
     * there are those two sites, and accordingly update the status at root of site*/
    private static final byte ONLY_CLOSED = (byte) 1;
    private static final byte ONLY_OPEN = (byte) 2;
    private static final byte OPEN_AND_TOP_CONNECTED = (byte) 6;
    private static final byte OPEN_AND_BOTTOM_CONNECTED = (byte) 10;

    private byte[][] statusOfSites;  /* maintains the status of all sites. Also, as the status of each site changes we
    update this change in the status of the root of WHOLE TREE to which particular site belongs. So the VALUE AT THE ROOT of each tree
    is not ONLY DUE THE STATUS OF THE ROOT, but it is the whole cumulative effect of all sites' statuses in that tree. For example, say
    if one site of a tree is OPEN_AND_BOTTOM_CONNECTED, so the whole tree would be bottom connected. So we update the status of root as being
    currentRootStatus | OPEN_AND_BOTT_CONNECTED. Here, comes the importance of bitwise operation! */
    private final int gridsize;
    private final WeightedQuickUnionUF obj;
    private int countOpenSites;  // java automatically initializes to 0
    private boolean percolationStarted; // java automatically initializes to false

    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("n should be > 0");
        gridsize = n;
        obj = new WeightedQuickUnionUF(n * n);
        statusOfSites = new byte[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                statusOfSites[i][j]
                        = ONLY_CLOSED; // initially, no site is open. Also, initially no tree is there, so it is itself's root.
            }
        }
    }

    public void open(int row, int col) {
        boolean outOfBounds = validate(row, col);
        if (outOfBounds) throw new IllegalArgumentException("Given arguments are invalid!");
        if (!isOpen(row, col)) {
            countOpenSites++;
            /* now this has just been opened. It is currently not part of any tree (so root == itself). So first we will make it open and update its status
             * and after making its unions with its open neighbours, we will add its status contribution to the overall tree  */
            int x = row - 1;
            int y = col - 1;
            if (x == 0) statusOfSites[x][y] = (byte) (statusOfSites[x][y] | OPEN_AND_TOP_CONNECTED);
            if (x == gridsize - 1)
                statusOfSites[x][y] = (byte) (statusOfSites[x][y] | OPEN_AND_BOTTOM_CONNECTED);
            if (x > 0 && x < (gridsize - 1))
                statusOfSites[x][y] = (byte) (statusOfSites[x][y] | ONLY_OPEN);
            unionNeighbour(row, col, row, col + 1);
            unionNeighbour(row, col, row, col - 1);
            unionNeighbour(row, col, row + 1, col);
            unionNeighbour(row, col, row - 1, col);
            /* after making unions, THE ONLY POSSIBLE TREE THAT *MAY* HAVE NOW STARTED PERCOLATING DUE TO THIS CELL's
            opening and unioning, CAN ONLY BE THE NEW TREE TO WHICH THIS NODE NOW BELONGS */
            byte rootStatus = statusAtRootOfSite(row, col);
            if (((rootStatus & OPEN_AND_TOP_CONNECTED) == OPEN_AND_TOP_CONNECTED) && (
                    (rootStatus & OPEN_AND_BOTTOM_CONNECTED) == OPEN_AND_BOTTOM_CONNECTED))
                percolationStarted = true;
        }
    }

    public boolean isOpen(int r, int c) {
        boolean outOfBounds = validate(r, c);
        if (outOfBounds) throw new IllegalArgumentException("Given arguments are invalid!");
        return (statusOfSites[r - 1][c - 1] & ONLY_OPEN) == ONLY_OPEN;  // note: & is bitwise AND
    }

    public boolean isFull(int r, int c) {
        boolean outOfBounds = validate(r, c);
        if (outOfBounds) throw new IllegalArgumentException("Given arguments are invalid!");
        return (statusAtRootOfSite(r, c) & OPEN_AND_TOP_CONNECTED) == OPEN_AND_TOP_CONNECTED;
    }
    /* isOpen only depends on that individual cell, ie, whether it is open or not.
     * Whereas isFull depends on its connections with other cells, ie, the status of the tree to which it belongs */

    public boolean percolates() {
        return percolationStarted;
    }

    public int numberOfOpenSites() {
        return countOpenSites;
    }

    private void unionNeighbour(int r, int c, int rN, int cN) {
        boolean outOfBounds = validate(rN, cN);
        if (!outOfBounds && isOpen(rN, cN)) {
            int index = rowcolTo1D(r, c);
            int indexN = rowcolTo1D(rN, cN);
            int root = obj.find(index);
            int rootN = obj.find(indexN);
            byte presentStatusRoot = statusAtRootOfSite(r, c);
            byte presentStatusRootN = statusAtRootOfSite(rN, cN);
            obj.union(index, indexN);  /* since we don't know in this union process which one of the two roots has been retained and which root has been attached to the other root
            so we will update the status of both the original roots (one of which is now the new root of the combined tree). Doing this is no harm, it is just storing
            information at one more location, thats it! */
            statusOfSites[oneDTo2Drow(root) - 1][oneDTo2Dcol(root) - 1] = (byte) (presentStatusRoot
                    | presentStatusRootN);
            statusOfSites[oneDTo2Drow(rootN) - 1][oneDTo2Dcol(rootN) - 1] = (byte) (
                    presentStatusRoot
                            | presentStatusRootN);
        }
    }

    private byte statusAtRootOfSite(int r, int c) {
        int index = rowcolTo1D(r, c);
        int root = obj.find(index);
        int root2Drow = oneDTo2Drow(root);
        int root2Dcol = oneDTo2Dcol(root);
        return statusOfSites[root2Drow - 1][root2Dcol - 1];
    }

    private int rowcolTo1D(int r, int c) {
        return ((r - 1) * gridsize + c - 1);
    }

    private int oneDTo2Dcol(int a) {
        return ((a % gridsize) + 1);
    }

    private int oneDTo2Drow(int a) {
        return (a / gridsize + 1);
    }

    private boolean validate(int r, int c) {
        return ((r < 1 || r > gridsize) || (c < 1 || c > gridsize));
    }

    public static void main(String[] args) {
        byte a = (byte) 6;
        byte b = (byte) 10;
        System.out.println(a | b);
    }
}
