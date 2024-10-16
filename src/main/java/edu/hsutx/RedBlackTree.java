package edu.hsutx;

/**
 * @author Todd Dole
 * @version 1.0
 * Starting Code for the CSCI-3323 Red-Black Tree assignment
 * Students must complete the TODOs and get the tests to pass
 */

/**
 * A Red-Black Tree that takes int key and String value for each node.
 * Follows the properties of a Red-Black Tree:
 * 1. Every node is either red or black.
 * 2. The root is always black.
 * 3. Every leaf (NIL node) is black.
 * 4. If a node is red, then both its children are black.
 * 5. For each node, all simple paths from the node to descendant leaves have the same number of black nodes.
 */
public class RedBlackTree<E> {
    Node root;
    int size;

    protected class Node {
        public String key; //seat row
        public E value;    //seat number
        public Node left;
        public Node right;
        public Node parent;
        public boolean color; // true = red, false = black

        public Node(String key, E value, Node parent, boolean color) {
            this.key = key;
            this.value = value;
            this.parent = parent;
            this.left = null;
            this.right = null;
            this.color = color;
        }

        // TODO - add comments as appropriate including a javadoc for each method
        public int getDepth() {
            // TODO - calculate the depth of the node and return an int value
            // Hint: follow parent pointers up to the root and count steps
            Node cur = this;
            int count = 1;

            while(cur.parent != null){
                cur = cur.parent;
                count++;
            }
            return count;
        }

        public int getBlackDepth() {
            // TODO - calculate the depth of the node counting only black nodes and return an int value
            Node cur = this;
            int count = 1;

            while(cur.parent != null){
                cur = cur.parent;
                if(cur.color == false)  //false = black
                    count++;
            }
            return count;
        }
    }

    public RedBlackTree() {
        root = null; // Start with an empty tree.  This is the one time we can have a null ptr instead of a null key node
        size = 0;
    }

    public void insert(String key, E value) {
        // TODO - Insert a new node into the tree with key and value
        // You must handle rebalancing the tree after inserting
        // 1. Insert the node as you would in a regular BST.
        // 2. Recolor and rotate to restore Red-Black Tree properties.
        // Make sure to add 1 to size if node is successfully added

        Node nodeSpot = find(key);
        Node n = new Node(key, value, null, true);  //node to insert

        if(root == null) {
            root = new Node(key, value, null, true);  //new node inserted is red
            root.left = null;
            root.right = null;
            size++;
        }else if(nodeSpot == null) {  // key wasn't found in tree, no duplicate keys
            Node p = null;  // above current - predecessor
            Node c = root;  //current

            while (c != null) {  //have to search for spot to insert
                p = c;
                if (key.compareTo(c.key) < 0)
                    c = c.left;
                else
                    c = c.right;
            }
            n.parent = p;  //setting n's parent to node above it
            if (key.compareTo(p.key) < 0)  //update parent's children
                p.left = n;
            else
                p.right = n;
            n.left = null;  //set new node's children to null
            n.right = null;
            size++;
        }
        fixInsertion(n); //this breaks everything
    }

    public void delete(String key) {
        // TODO - Implement deletion for a Red-Black Tree
        // Will need to handle three cases similar to the Binary Search Tree
        // 1. Node to be deleted has no children
        // 2. Node to be deleted has one child
        // 3. Node to be deleted has two children
        // Additionally, you must handle rebalancing after deletion to restore Red-Black Tree properties
        // make sure to subtract one from size if node is successfully added

        Node z = find(key); //deleteNode
        Node y;
        Node x;
        boolean yOgColor;

        if(z != null){  //node was found in tree
            y = z;
            yOgColor = y.color;
            if(z.left == null){             //Case 1 - z's L child is null
                x = z.right;
                transplant(z,z.right);
            }else if(z.right == null) {     //Case 2 - z's R child is null
                x = z.left;
                transplant(z, z.left);
            }else{                          //Case 3 - neither of z's children are null
                y = getMinimumKey(z.right);
                yOgColor = y.color;
                x = y.right;
                if(y.parent == z) {
                    if(x != null)
                        x.parent = y;
                }else{
                    transplant(y,x);
                    y.right = z.right;
                    y.right.parent = y;
                }
                transplant(z,y);
                y.left = z.left;
                y.left.parent = y;
                y.color = z.color;
            }
            size--;
            if(yOgColor == false)
                fixDeletion(x);
        }
    }

    //Helps move subtrees within a tree
    public void transplant(Node c, Node p){
        //p is one of c's child
        if(c != null) {
            if (c.parent == null) {  // if c is the root
                root = p;
            } else if (c == c.parent.left) {  // c is a L child in the tree
                c.parent.left = p;
            } else {  // c is a R child in the tree
                c.parent.right = p;
            }
            if(p != null){  //cannot access p.parent if p is null
                p.parent = c.parent;  //removes connection between c & p
            }
        }
    }

    public Node getMinimumKey(Node current){
        while(current.left != null){
            current = current.left;
        }
        return current;
    }

    private void fixInsertion(Node node) {
        // TODO - Implement the fix-up procedure after insertion
        // Ensure that Red-Black Tree properties are maintained (recoloring and rotations).
        // Hint: You will need to deal with red-red parent-child conflicts
        Node u;

        while (node.parent != null && node.parent.color == true) {  //while the color is red
            if (node.parent == node.parent.parent.left) {  //if parent is a L child
                u = node.parent.parent.right;   // u = node's uncle
                if (u != null && u.color == true) {  // case 1 - node's uncle is red
                    node.parent.color = false;
                    u.color = false;
                    node.parent.parent.color = true;
                    node = node.parent.parent;      //recursively move up tree
                } else {  //uncle is black
                    if (node == node.parent.right) {  //case 2 - node's unc = black(tri), node is a R child
                        node = node.parent;           // node takes place of parent
                        rotateLeft(node);             //rotate in opposite direction of node
                    } else {
                        //node is L child of parent
                        node.parent.color = false; //* case 3 starts here - node's unc = black(line)
                        node.parent.parent.color = true;
                        rotateRight(node.parent.parent);
                    }
                }
            } else if(node.parent == node.parent.parent.right){  //if parent is a R child
                u = node.parent.parent.left;
                if (u != null && u.color == true) {
                    node.parent.color = false;
                    u.color = false;
                    node.parent.parent.color = true;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                    } else {
                        node.parent.color = false; //color node black
                        node.parent.parent.color = true;  //color node red
                        rotateLeft(node.parent.parent);
                    }
                }
            }
        }
        root.color = false;  //case 0 - node is black
    }

    private void fixDeletion(Node node) {
        // TODO - Implement the fix-up procedure after deletion
        // Ensure that Red-Black Tree properties are maintained (recoloring and rotations).
        Node w; //node's sibling

        if(node == null)
            return;

        while(node != root && node.color == false){
            if(node == node.parent.left){
               w = node.parent.right;

               if(w.color == true){
                   w.color = false; //new color is black
                   node.parent.color = true;
                   rotateLeft(node.parent);
                   w = node.parent.right;
               }

               if((w.left == null || w.left.color == false) && (w.right == null || w.right.color == false) ){
                   //if w.left/right == null -> null node is a black node
                   w.color = true;
                   node = node.parent;
               }else{
                   if(w.right == null || w.right.color == false){
                       w.left.color = false;
                       w.color = true;
                       rotateRight(w);
                       w = node.parent.right;
                   }

                   w.color = node.parent.color;
                   node.parent.color = false;
                   w.right.color = false;
                   rotateLeft(node.parent);
                   node = root;
               }

            }else{
                w = node.parent.left;

                if(w.color == true){
                    w.color = false; //new color is black
                    node.parent.color = true;
                    rotateRight(node.parent);
                    w = node.parent.left;
                }

                if ( (w.right == null || w.right.color == false) && (w.left == null || w.left.color == false) ) {
                    w.color = true;
                    node = node.parent;
                } else {
                    if (w.left == null || w.left.color == false) {
                        if(w.right != null)
                            w.right.color = false;
                        w.color = true;
                        rotateLeft(w);
                        w = node.parent.left;
                    }

                    w.color = node.parent.color;
                    node.parent.color = false;
                    if(w.left != null)
                        w.left.color = false;
                    rotateRight(node.parent);
                    node = root;
                }
            }
        }
        node.color = false;
    }

    private void rotateLeft(Node node) {
        // TODO - Implement left rotation
        // Left rotation is used to restore balance after insertion or deletion
        Node y = node.right;
        node.right = y.left;  //y's left subtree into x's right subtree

        if(y.left != null)
            y.left.parent = node;
        y.parent = node.parent;

        if(node.parent == null){
            root = y;
        }else if(node == node.parent.left){
            node.parent.left = y;
        }else{
            node.parent.right = y;
        }

        y.left = node;     //node = L child of y
        node.parent = y;   //node's parent = y
    }

    private void rotateRight(Node node) {
        // TODO - Implement right rotation
        // Right rotation is used to restore balance after insertion or deletion
        Node y = node.left;
        node.left = y.right;  //y's right subtree into x's left subtree
        if(y.right != null)
            y.right.parent = node;
        y.parent = node.parent;
        if(node.parent == null){
            root = y;
        }else if(node == node.parent.right){
            node.parent.right = y;
        }else{
            node.parent.left = y;
        }

        y.right = node;   //node = R child of y
        node.parent = y;  //node's parent = y
    }

    Node find(String key) {
        // TODO - Search for the node with the given key
        // If the key exists in the tree, return the Node where it is located
        // Otherwise, return null
        Node n = root;

        while(n != null){
            if(key.compareTo(n.key) == 0) {
                return n;
            }else if(key.compareTo(n.key) < 0) {  //key is less
                n = n.left;
            }else if(key.compareTo(n.key) > 0) {  //key is greater
                n = n.right;
            }
        }
        return null;
    }

    public E getValue(String key) {
        // TODO - Use find() to locate the node with the given key and return its value
        // If the key does not exist, return null
        Node c = find(key);

        if(c != null && c.key.equals(key)) {
            return c.value;
        }
        return null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    // returns the depth of the node with key, or 0 if it doesn't exist
    public int getDepth(String key) {
        Node node = find(key);
        if (node != null) return node.getDepth();
        return 0;
    }

    // Helper methods to check the color of a node
    private boolean isRed(Node node) {
        return node != null && node.color == true; // Red is true
    }

    private boolean isBlack(Node node) {
        return node == null || node.color == false; // Black is false, and null nodes are black
    }
    public int getSize() {
        return size;
    }

    // Do not alter this method
    public boolean validateRedBlackTree() {
        // Rule 2: Root must be black
        if (root == null) {
            return true; // An empty tree is trivially a valid Red-Black Tree
        }
        if (isRed(root)) {
            return false; // Root must be black
        }

        // Start recursive check from the root
        return validateNode(root, 0, -1);
    }

    // Do not alter this method
    // Helper method to check if the current node maintains Red-Black properties
    private boolean validateNode(Node node, int blackCount, int expectedBlackCount) {
        // Rule 3: Null nodes (leaves) are black
        if (node == null) {
            if (expectedBlackCount == -1) {
                expectedBlackCount = blackCount; // Set the black count for the first path
            }
            return blackCount == expectedBlackCount; // Ensure every path has the same black count
        }

        // Rule 1: Node is either red or black (implicit since we use a boolean color field)

        // Rule 4: If a node is red, its children must be black
        if (isRed(node)) {
            if (isRed(node.left) || isRed(node.right)) {
                return false; // Red node cannot have red children
            }
        } else {
            blackCount++; // Increment black node count on this path
        }

        // Recurse on left and right subtrees, ensuring they maintain the Red-Black properties
        return validateNode(node.left, blackCount, expectedBlackCount) &&
                validateNode(node.right, blackCount, expectedBlackCount);
    }
}
