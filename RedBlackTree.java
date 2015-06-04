/**
 * CS 241: Data Structures and Algorithms II
 * Professor: Edwin Rodr&iacute;guez
 *
 * Programming Assignment #2
 *
 * Simple code of a self balancing Red-Black tree
 * 
 * NOTE:All cases of removing from the tree are increased
 * by 1 due to the way the tree is coded.
 * 
 * Jacob Romero
 * 
 */

//Imports for functionality later on
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Jacob Romero
 *
 * All functions are implementations of a Red-Black tree, used to keep
 * a balanced tree to take advantage of of the log(n) search time.
 *
 * @param <K>
 * A comparable type used for the keys in the tree.
 * 
 * @param <V>
 * Any type V that will be the value of tree nodes.
 */
public class RedBlackTree<K extends Comparable<K>, V>// implements Tree<K, V> {
{
	public Node root = null;

	/**
	 * 
	 * Add the key, value pair into the tree, then balance it.
	 * 
	 * @param key
	 * Key used to compare the the tree nodes to find the insert position.
	 * 
	 * @param value
	 * Value the Node will hold.
	 */
	public void add(K key, V value){
		Node addNode = null;
		
		//if tree is empty make new node the root.
		if(root == null){
			root = new Node(key, value);
			
			//add null leaves
			root.leftChild = addLeaf(root);
			root.rightChild = addLeaf(root);
			
			addNode = root;
		}
		else{
			//find the insertion position
			Node addPos = addPos(key, root);
			
			//if the key is less or equal it belongs in the left sub-tree
			if(key.compareTo(addPos.key) <= 0){
				addNode = addPos.leftChild = new Node(key, value, Colors.red, addPos);
				addNode.leftChild = addLeaf(addNode);
				addNode.rightChild = addLeaf(addNode);
			}
			//otherwise belongs in the right sub-tree
			else if(key.compareTo(addPos.key) > 0){
				addNode = addPos.rightChild = new Node(key, value, Colors.red, addPos);
				addNode.leftChild = addLeaf(addNode);
				addNode.rightChild = addLeaf(addNode);
			}
			
			//add null leaves
			addNode.leftChild = addLeaf(addNode);
			addNode.rightChild = addLeaf(addNode);
		}
		
		//go through balance cases
		addCase1(addNode);
	}
	
	/**
	 * Finds the appropriate position to add the key from the add(Key, Value) method.
	 * 
	 * @param key
	 * Key used to search for insertion position.
	 * 
	 * @param current
	 * The node, that you are comparing to.
	 * 
	 * @return
	 * returns the appropriate position to add the node.
	 */
	private Node addPos(K key, Node current){
		if(current.rightChild != null && current.rightChild.key != null && key.compareTo(current.key) > 0)
			return addPos(key, current.rightChild);
		else if(current.leftChild != null && current.leftChild.key != null && key.compareTo(current.key) <= 0)
			return addPos(key, current.leftChild);
		
		return current;
	}
	
	/**
	 * Case 1 of addition to a Red-Black tree
	 * the parent is null, so the node is the root, color it black for
	 * 2nd RBT invariant.
	 * 
	 * @param node
	 * The node that was added to the tree.
	 */
	private void addCase1(Node node){
		//if node is root color black
		if(node.parent == null){
			node.color = Colors.black;
		}
		//otherwise check for case 2 of adding to a RBT
		else{
			addCase2(node);
		}
	}
	
	/**
	 * Case 2 of addition to a RBT
	 * where if the nodes parent is black then RBT invariant 4 is maintained.
	 * 
	 * @param node
	 * Node that is being added to the tree.
	 */
	private void addCase2(Node node){
		if(node.parent.color == Colors.black){
			return;
		}
		//if invariant 4 is broken fix with case3
		else{
			addCase3(node);
		}
	}
	
	/**
	 * Case 3 of addition to a RBT
	 * where if the uncle of the addition node is color red, along with add node's parent
	 * in which case we color parent, and uncle black, and grand parent red to restore
	 * invariant 4.
	 * 
	 * @param node
	 * Node that is being added to the tree.
	 */
	private void addCase3(Node node){
		Node uncle = getUncle(node);
		
		if(uncle != null && uncle.key != null && uncle.color == Colors.red){
			node.parent.color = Colors.black;
			uncle.color = Colors.black;
			
			//get grandparent
			Node grandParent = getGrandParent(node);
			//re-color grandparent
			grandParent.color = Colors.red;
			
			//check to see if invariants are maintained on the grand parent
			addCase1(grandParent);
		}
		//Check for case 4 if case 3 is not valid
		else{
			addCase4(node);
		}
	}
	
	/**
	 * Case 4 of adding to a RBT
	 * if the node we are adding is an internal node, then we
	 * rotate to make it external, and set up for case 5.
	 * 
	 * @param node
	 * The node we are adding to the RBT.
	 */
	private void addCase4(Node node){
		Node grandParent = getGrandParent(node);

		//if node we are adding is a left side internal node rotate left to make it an external node
		if(node == node.parent.rightChild && node.parent == grandParent.leftChild){
			leftRotate(node.parent);
			
			node = node.leftChild;	
		}
		//other wise it is external node on right side so right rotate
		else if(node == node.parent.leftChild && node.parent == grandParent.rightChild){			
			rightRotate(node.parent);

			node = node.rightChild;
		}
		
		//always go to case 5.
		addCase5(node);
	}
	
	/**
	 * Case 5 of adding to a RBT
	 * the node we are adding has a parent that is red, and its uncle is black,
	 * so we rotate the grandparent to balance the sub-tree.
	 * 
	 * @param node
	 * The node we are adding to the RBT.
	 */
	private void addCase5(Node node){
		Node grandParent = getGrandParent(node);

		node.parent.color = Colors.black;
		grandParent.color = Colors.red;

		//perform grandparent rotation to balance tree
		if(node == node.parent.leftChild){
			rightRotate(grandParent);
		}
		else{
			leftRotate(grandParent);
		}
	}
//END ADDING TO TREE METHODS
	
	/**
	 * Remove the node with the specified key from the tree.
	 * 
	 * @param key
	 * the key we want to find and remove from the tree.
	 * 
	 * @return
	 * returns value of the node with the specified key that was removed.
	 */
	public V remove(K key) {
		//if tree is empty return nothing
		if(root == null){
			return null;
		}
		//otherwise check if the node we want is root, otherwise return null
		else if(root.leftChild.key == null && root.rightChild.key == null){
			if(key.compareTo(root.key) == 0)
				return root.value;
			else
				return null;
		}
		else{
			//find removal position
			Node removeNode = removePos(key, root);
			
			if(removeNode == null){
				return null;
			}
			
			Node fixNode = removeNode;
			
			//value being returned
			V returnVal = removeNode.value;
			Node pred;
			
			//if the node we are removing is not a leaf we need to replace with in-order predecessor
			if(removeNode.leftChild.key != null && removeNode.rightChild.key != null){
				//copy predecessor values to the remove node
				pred = getPred(removeNode.leftChild);
				fixNode = pred;
				removeNode.key = pred.key;
				removeNode.value = pred.value;
				
				//make the predecessor a leaf
				pred.leftChild = null;
				pred.rightChild = null;
				pred.key = null;
				pred.value = null;
				pred.color = Colors.black;
			}
			//otherwise for the next to cases, we simply move the child up to remove nodes parent
			else if(removeNode.leftChild.key != null){
				removeNode.key = removeNode.leftChild.key;
				removeNode.value = removeNode.leftChild.value;
				removeNode.color = removeNode.leftChild.color;
				removeNode.rightChild = removeNode.leftChild.rightChild;
				removeNode.leftChild = removeNode.leftChild.leftChild;
			}
			else if(removeNode.rightChild.key != null){
				removeNode.key = removeNode.rightChild.key;
				removeNode.value = removeNode.rightChild.value;
				removeNode.color = removeNode.rightChild.color;
				removeNode.rightChild = removeNode.rightChild.rightChild;
				removeNode.leftChild = removeNode.rightChild.leftChild;
			}
			//lastly if the remove node is a leaf, remove values, and make it a null leaf
			else{
				//if the leave we are removing is red, then the RBT properties are maintained, nothing to do.
				if(removeNode.color == Colors.red)
					fixNode = root;
				
				removeNode.key = null;
				removeNode.value = null;
				removeNode.leftChild = null;
				removeNode.rightChild = null;
				removeNode.color = Colors.black;
			}
			
			//go through case checks
			removeCase1(fixNode);
			
			return returnVal;
		}
	}
	
	/**
	 * Case 1 of removing from RBT
	 * if the remove node's parent isn't null, i.e. isn't the root
	 * the go to case to otherwise make sure the root is black.
	 * 
	 * @param node
	 * The node being removed from the RBT.
	 */
	private void removeCase1(Node node){
		if(node.parent != null){
			//go to case 2 if the node isn't the root
			removeCase2(node);
		}
		else
			node.color = Colors.black;
	}
	
	/**
	 * Case 2 of removing from a RBT
	 * if the sibling is red then we rotate to restore the invariant
	 * 5.
	 * 
	 * @param node
	 * The node being removed from the RBT.
	 */
	private void removeCase2(Node node){
		Node sibling = getSibling(node);
		
		if(sibling.color == Colors.red){
			node.parent.color = Colors.red;
			sibling.color = Colors.black;
			
			if(node == node.parent.leftChild){
				leftRotate(node.parent);
			}
			else{
				rightRotate(node.parent);
			}
		}
		//always go to case 3
		removeCase3(node);
	}
	
	/**
	 * Case 3 of removing from a RBT
	 * If the all nodes around the node are black simply re-color the sibling node
	 *  
	 * @param node
	 * The node being removed from the RBT.
	 */
	private void removeCase3(Node node){
		Node sibling = getSibling(node);

		if(node.parent.color == Colors.black && sibling.color == Colors.black && sibling.leftChild.color == Colors.black && sibling.rightChild.color == Colors.black){
			sibling.color = Colors.red;
			removeCase1(node.parent);
		}
		else{
			removeCase4(node);
		}
	}
	
	/**
	 * Case 4 of removing from a RBT
	 * If the sibling of the removal node is black, exchange the color of the parent
	 * and the sibling.
	 * 
	 * @param node
	 * The node being removed from the RBT.
	 */
	private void removeCase4(Node node){
		Node sibling = getSibling(node);

		if(node.parent.color == Colors.red && sibling.color == Colors.black && sibling.leftChild.color == Colors.black && sibling.rightChild.color == Colors.black){
			sibling.color = Colors.red;
			node.parent.color = Colors.black;
		}
		else{
			removeCase5(node);
		}
	}
	
	/**
	 * Case5 of removing from a RBT
	 * If the sibling of the remove node is black
	 * rotate the parent.
	 *
	 * @param node
	 * The node being removed from the RBT.
	 */
	private void removeCase5(Node node){
		Node sibling = getSibling(node);
		
		//check sibling's color
		if(sibling.color == Colors.black){
			//rotate based on the location of the node
			if(node == node.parent.leftChild && sibling.rightChild.color == Colors.black && sibling.leftChild.color == Colors.red){
				sibling.color = Colors.red;
				sibling.leftChild.color = Colors.black;
				rightRotate(sibling);
			}
			else if(node == node.parent.rightChild && sibling.leftChild.color == Colors.black && sibling.rightChild.color == Colors.red){
				sibling.color = Colors.red;
				sibling.rightChild.color = Colors.black;
				leftRotate(sibling);
			}
		}
		removeCase6(node);
	}
	
	/**
	 * Case 6 of removing from RBT
	 * If remove node's sibling is black and
	 * the child of the sibling is red
	 * we rotate the parent of the remove node
	 * 
	 * @param node
	 * The node being removed from the RBT.
	 */
	private void removeCase6(Node node){
		Node sibling = getSibling(node);
		
		sibling.color = node.parent.color;
		node.parent.color = Colors.black;
		
		if(node == node.parent.leftChild){
			sibling.rightChild.color = Colors.black;
			leftRotate(node.parent);
		}
		else{
			sibling.leftChild.color = Colors.black;
			rightRotate(node.parent);
		}
	}
	
	/**
	 * Get the removal position of the passed in key.
	 * 
	 * @param key
	 * Key that is to be removed from the tree
	 * @param current
	 * The node we are searching from
	 * @return
	 * Returns the position to remove the node from
	 */
	private Node removePos(K key, Node current){
		if(current.rightChild.key != null && key.compareTo(current.key) > 0){
			return removePos(key, current.rightChild);
		}
		else if(current.leftChild.key != null && key.compareTo(current.key) < 0){
			return removePos(key, current.leftChild);
		}
		else if(current.leftChild.key == null && current.rightChild.key == null && current.key != key){
			return null;
		}
		return current;
	}
	
	/**
	 * Get the in-order predecessor of the node we are removing
	 * to replace that node.
	 * 
	 * @param node
	 * Node that we are getting the predecessor from
	 * @return
	 * returns the predecessor
	 */
	private Node getPred(Node node){
		if(node.rightChild.key != null)
			return getPred(node.rightChild);
		return node;
	}
	
//END OF REMOVAL METHODS
	
	/**
	 * Left rotates the sub-tree of the node being
	 * passed in.
	 * 
	 * @param node
	 * Node that will be rotated.
	 * @return
	 * returns the new sub-tree.
	 */
	private Node leftRotate(Node node){
		//create temporary fields for replacement later
		Node tmpChild = node.rightChild.leftChild;	
		Node newRoot = node.rightChild;
		
		//add new data to the new root of the sub-tree
		newRoot.leftChild = node;
		newRoot.leftChild.rightChild = tmpChild;
		
		//change parent of the now newRoot
		newRoot.parent = newRoot.leftChild.parent;
		newRoot.rightChild.parent = newRoot;
		
		//replace the old parent's link with the newRoot
		if(newRoot.parent == null){
			root = newRoot;
			newRoot.leftChild.parent = newRoot; 
		}
		else if(newRoot.parent.leftChild == newRoot.leftChild){
			newRoot.parent.leftChild = newRoot;
		}
		else{
			newRoot.parent.rightChild = newRoot;
		}
		
		return newRoot;
	}

	/**
	 * Right rotates the sub-tree of the node being
	 * passed in.
	 * 
	 * @param node
	 * Node that will be rotated.
	 * @return
	 * returns the new sub-tree.
	 */
	private Node rightRotate(Node node){
		//create temporary fields for replacement later
		Node tmpChild = node.leftChild.rightChild;		
		Node newRoot = node.leftChild;
		
		//add new data to the new root of the sub-tree
		newRoot.rightChild = node;
		newRoot.rightChild.leftChild = tmpChild;
		//change parent of the now newRoot
		newRoot.parent = newRoot.rightChild.parent;
		newRoot.rightChild.parent = newRoot;
		
		//replace the parents link with the newRoot
		if(newRoot.parent == null){
			root = newRoot;
			newRoot.rightChild.parent = newRoot; 
		}
		else if(newRoot.parent.leftChild == newRoot.rightChild){
			newRoot.parent.leftChild = newRoot;
		}
		else{
			newRoot.parent.rightChild = newRoot;
		}
		
		return newRoot;
	}
	
	/**
	 * Returns the sibling of the node that is passed in.
	 * @param node
	 * Node we are finding the sibling from.
	 * @return
	 * Returns the sibling of the node.
	 */
	private Node getSibling(Node node){
		if(node == node.parent.leftChild){
			return node.parent.rightChild;
		}
		else{
			return node.parent.leftChild;
		}
	}
	
	/**
	 * Returns the grandparent of the node that is passed in.
	 * @param node
	 * Node we are finding the grandparent from.
	 * @return
	 * Returns the grandparent of the node.
	 */
	private Node getGrandParent(Node node){
		if(node != null && node.parent != null)
			return node.parent.parent;
		else
			return null;
	}
	
	/**
	 * Returns the uncle of the node that is passed in.
	 * @param node
	 * Node we are finding the uncle from.
	 * @return
	 * Returns the uncle of the node.
	 */
	private Node getUncle(Node node){
		Node grandParent = getGrandParent(node);
		
		if(grandParent == null)
			return null;
		
		if(node.parent == grandParent.leftChild)
			return grandParent.rightChild;
		else
			return grandParent.leftChild;
	}
	
	//@Override
	/**
	 * Returns, but doesn't remove the node with the
	 * key that is passed in to the method.
	 * @param key
	 * The key used to search the tree for the node
	 * @return
	 * Returns the value of the key with the node that is passed
	 */
	public V lookup(K key) {
		if(root == null)
			return null;
		else
			return find(key, root);
	}
	
	/**
	 * method that does the heavy lifting of finding the node based on a key
	 * @param key
	 * Key we want to find with the node that owns that key
	 * @param current
	 * Node we want to start searching from
	 * @return
	 * Returns value at the node with the key we wanted to find
	 */
	private V find(K key, Node current){
		if(current.key == key)
			return current.value;
		else if(current.key.compareTo(key) > 0)
			find(key, current.rightChild);
		else
			find(key, current.leftChild);
		return null;
	}
	
	/**
	 * Add a null black leaf to the passed
	 * in node.
	 * @param parent
	 * The node we want to add to the null leaf to
	 * @return
	 * returns the node that is created
	 */
	private Node addLeaf(Node parent){
		//simply create new node and return it
		return new Node(null, null, parent);
	}
	
	/**
	 * Print the tree in its pyramid structure
	 * @return
	 * return a string of the pyramid structure of the tree
	 */
	public String toPrettyString() {
        int maxLevel = maxLevel(root);

        return printLevel(Collections.singletonList(root), 1, maxLevel);
    }
	
	/**
	 * Helper method for toPrettyString(), does the heavy lifting
	 * by recursively calling itself to print the tree level by level.
	 * 
	 * @param nodes
	 * A list of type Node that contains the nodes on that level.
	 * @param level
	 * The current level we are working on.
	 * @param maxLevel
	 * The depth of the tree.
	 * @return
	 * A string print out of the current level is concatenated.
	 */
    private String printLevel(List<Node> nodes, int level, int maxLevel) {
    	String str = "";
    	
        if (nodes.isEmpty() || isAllNull(nodes))
            return "";

        List<Node> newNodes = new ArrayList<Node>();
        
        int floor = maxLevel - level;
        int firstSpaces = (int) Math.pow(2, (floor));
        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

        str = str.concat(printWhitespaces(firstSpaces));
        
        //put all child nodes into a new list
        for (Node node : nodes){
            if (node != null){
                str = str.concat(node.value + " - " + node.color);
                newNodes.add(node.leftChild);
                newNodes.add(node.rightChild);
            } 
            //if the node is null, then print some white space to allow for gaps in the tree
            else{
                newNodes.add(null);
                newNodes.add(null);
                str = str.concat("\t  ");
            }
            
            //print white space between nodes
            str = str.concat(printWhitespaces(betweenSpaces));
        }   
        
        //white space to separate levels of the tree
        str = str.concat("\n\n\n");
        
        //print next level of the tree
        str = str.concat(printLevel(newNodes, level + 1, maxLevel));
        
        return str;
    }
    
    /**
     * Helper method to printLevel() that prints whitespace and returns it as a string.
     * 
     * @param count
     * The number of spaces we want to make a string out of.
     * @return
     * The string of whitespace to be returned. 
     */
    private String printWhitespaces(int count) {
    	String str = "";
    	
        for (int i = 0; i < count * 3; i++)
        	str = str.concat(" ");
        
        return str;
    }

    /**
     * Checks if any elements in the list are not null
     * returns true if at least 1 is not, false otherwise.
     * 
     * @param list
     * List that is being check for any non-null elements.
     * @return
     * returns true if at least 1 is not, false otherwise.
     */
    private boolean isAllNull(List<Node> list) {
    	//check if at least one element in the list is not null
        for (Node node : list) {
            if (node != null)
                return false;
        }

        return true;
    }
	
	/**
	 * Finds the largest depth of the tree using 
	 * Depth first traversal
	 * @return
	 * returns number of levels the tree has
	 */
	public int maxLevel(Node node) {
        if (node == null)
            return 0;

        return Math.max(maxLevel(node.leftChild), maxLevel(node.rightChild)) + 1;
    }

	/**
	 * Enum for the two colors a node can possible have
	 * @author Jacob Romero
	 *
	 */
	private enum Colors{
		black,
		red;
	}
	
	/**
	 * Inner class for tree nodes.
	 * 
	 * Fields include:
	 * 	-color
	 * 	-leftChild
	 *	-rightChild
	 *	-parent
	 *	-key
	 *	-value
	 *
	 * @author Jacob Romero
	 *
	 */
	class Node{
		Colors color = Colors.red;
		Node leftChild;
		Node rightChild;
		Node parent = null;
		K key;
		V value;
		
		//constructors for creating nodes
		
		/**
		 * Standard constructor for creating nodes
		 * @param addKey
		 * Key the node will have.
		 * @param addValue
		 * Value the node will have.
		 */
		public Node(K addKey, V addValue) {
			key = addKey;
			value = addValue;
		}
		
		/**
		 * Constructor allowing you to specify the color of the node.
		 * @param addKey
		 * Key the node will have.
		 * @param addValue
		 * Value the node will have.
		 * @param addColor
		 * Color the node will be.
		 */
		@SuppressWarnings("unused")
		private Node(K addKey, V addValue, Colors addColor) {
			key = addKey;
			value = addValue;
			color = addColor;
		}
		
		/**
		 * Constructor that allows you to specify the parent of the node
		 * being created.
		 * @param addKey
		 * Key the node will have.
		 * @param addValue
		 * Value the node will have.
		 * @param addParent
		 * Parent of the node.
		 */
		private Node(K addKey, V addValue, Node addParent) {
			key = addKey;
			value = addValue;
			color = Colors.black;
			parent = addParent;
		}
		
		/**
		 * Node that allows you to specify the color and the parent of the
		 * node being created.
		 * @param addKey
		 * Key the node will have.
		 * @param addVal
		 * Value the node will have.
		 * @param addCol
		 * Color the node will be.
		 * @param addParent
		 * Parent of the node.
		 */
		private Node(K addKey, V addVal, Colors addCol, Node addParent) {
			key = addKey;
			value = addVal;
			color = addCol;
			parent = addParent;
		}
	}
	
}
