package edu.hsutx;


/**
 * CowboySeatTree is a Red-Black Tree that stores SeatAssignment objects as the values for the tree nodes.
 * It extends the generic RedBlackTree class and specializes it for the SeatAssignment value type.
 *
 * This class will also include additional methods to search for specific seat assignment blocks.
 */
public class CowboySeatTree extends RedBlackTree<SeatAssignment> {

    /**
     * Inserts a new SeatAssignment into the tree using the SeatAssignment's key as the search key.
     *
     * @param seatAssignment The SeatAssignment object to insert into the tree.
     */
    public void insert(SeatAssignment seatAssignment) {
        String key = seatAssignment.getKey();  // Get the key from SeatAssignment (for tree ordering)
        super.insert(key, seatAssignment);     // Use the RedBlackTree's insert method
    }

    /**
     * Deletes a SeatAssignment from the tree by its key.
     *
     * @param seatAssignment The SeatAssignment object to remove from the tree.
     */
    public void delete(SeatAssignment seatAssignment) {
        String key = seatAssignment.getKey();  // Get the key from SeatAssignment
        super.delete(key);                     // Use the RedBlackTree's delete method
    }

    /**
     * Finds a SeatAssignment in the tree by its key.
     *
     * @param key The key of the SeatAssignment to search for.
     * @return The SeatAssignment object if found, otherwise null.
     */
    public SeatAssignment getValue(String key) {
        return super.getValue(key);  // Use the RedBlackTree's getValue method to get the value (SeatAssignment)
    }

    /**
     * Finds the first unassigned block of seats with size >= threshold
     */

    private Node traverseTreeForUnassigned(Node n, String key) {
        if (n.key == null) return null;
        if (key.compareTo(n.key)<0) {
            Node bestLeftOption = null;
            if (n.left != null) bestLeftOption = traverseTreeForUnassigned(n.left, key);
            if (bestLeftOption != null) return bestLeftOption;

            // We didn't find a good node to the left, check this node
            if (n.value.getOwner() == null) return n;
        }

        return traverseTreeForUnassigned(n.right, key);
    }

    public SeatAssignment getUnassignedBlockOverThreshold(int threshold) {

        String searchKey = String.format("%05d", threshold) + "0000000";
        // Traverse the tree to find the first unassigned node with key > searchkey
        // a node is unassigned if node.value.getOwner() is null
        Node unassignedNode = traverseTreeForUnassigned(this.root, searchKey);

        if (unassignedNode == null) return null;
        else return unassignedNode.value;

    }

}