package prefixTree;

import java.util.ArrayList;

/**
 * This class implements a PrefixTree.
 * 
 * @author Sesh Venugopal
 *
 */
public class PrefixTree {

	// prevent instantiation
	private PrefixTree() {
	}
	
	private static int getCommonLength(String str1, String str2) {
	    int commonLength = 0;
	    int minLength = Math.min(str1.length(), str2.length());
	    for (int i = 0; i < minLength; i++) {
	        if (str1.charAt(i) == str2.charAt(i)) {
	            commonLength++;
	        } else {
	            break;
	        }
	    }
	    return commonLength;
	}

	
	/**
	 * Recursively inserts a word into the trie.
	 * 
	 * @param node : PrefixTreeNode 
	 * @param word : word to insert into trie
	 * @param wordIndex : Index of word in array of words
	 * @param allWords : Input array of words (lowercase) to be inserted
	 */
	private static void insertWordRec(PrefixTreeNode node, String word, int wordIndex, String[] allWords) {
	    // Base case: If the word is empty, return
	    if(word.isEmpty()) {
	    	return;
	    }
	    // currChild and prevChild are initialized to traverse the children of the current node 
	    PrefixTreeNode currChild = node.firstChild;
	    PrefixTreeNode prevChild = null; 
	    // Traverse the children of the current node 
	    while(currChild != null) {
	    	// Find the prefix of the word associated with current child  
	    	String prefix = allWords[currChild.substr.wordIndex].substring(currChild.substr.startIndex, currChild.substr.endIndex+1);
	    	// Length of characters in common between prefix and word being inserted 
	    	int commonLength = getCommonLength(prefix, word);
	    	// If prefix and word share common characters 
	    	if(commonLength > 0) {
	    		// If prefix and word are the same String
	    		if(commonLength == prefix.length()) {
	    			// Recursively insert the remaining substring into the current child node
	    			insertWordRec(currChild, word.substring(commonLength), wordIndex, allWords);
	    			return; 
	    		}
	    		else {
	    			// Create a new child node to represent remaining portion of prefix 
	    			PrefixTreeNode newChild = new PrefixTreeNode(
		                    new Indices(
		                        currChild.substr.wordIndex,
		                        (short) (currChild.substr.startIndex + commonLength), // starts after common characters between prefix and word
		                        (short) currChild.substr.endIndex
		                    ),
		                    currChild.firstChild,
		                    currChild.sibling
		                );
	    			// Update current child node to only cover common prefix 
	    			currChild.substr.endIndex = (short) (currChild.substr.startIndex + commonLength - 1);
	    			// Assign the newChild to be the firstChild of the currChild
	    			// currChild represents the common prefix, and the remaining part of the prefix should follow it in the trie
	                currChild.firstChild = newChild;
	                // Attach new child node to represent the remaining part of the word
	                int remStartIndex = currChild.substr.startIndex + commonLength;
	                int remEndIndex = (short) (remStartIndex + word.length() - commonLength - 1);
	                Indices remainingSubstring = new Indices(
		                    wordIndex,
		                    (short) remStartIndex,
		                    (short) remEndIndex
		                );
	                //System.out.println("Remaining Substring");
	                //System.out.println(remainingSubstring);
	                PrefixTreeNode newRemainingChild = new PrefixTreeNode(remainingSubstring, null, null);
	                newChild.sibling = newRemainingChild;
	                return;
	    		}
	    	}
	    	// Move to the next node 
	    	prevChild = currChild;
	        currChild = currChild.sibling;
	    }
	    // If no common prefix is found, create a new child node for the entire word
	    Indices substring = new Indices(wordIndex, (short) 0, (short) (word.length() - 1));
	    PrefixTreeNode newChild = new PrefixTreeNode(substring, null, null);
	    
	    // if prevChild is not null, this means it is already a child of the parent node
	    // newChild should be added next to it in the list of children
	    if (prevChild != null) {
	        prevChild.sibling = newChild;
	    } else {
	    	// if prevChild is null, there are no previous child nodes before the current child node 
	    	// now, newChild is first child of parent node 
	        node.firstChild = newChild;
	    }
	}
	    
	/**
	 * Builds a PrefixTree(Prefixtionary-tree) by inserting all words in the input array, one at a
	 * time, in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!) The words
	 * in the input array are all lower case.
	 * 
	 * @param allWords
	 *            Input array of words (lowercase) to be inserted.
	 * @return 
	 * @return Root of PrefixTree with all words inserted from the input array
	 */
	
	public static PrefixTreeNode buildPrefixTree(String[] allWords) {
		/** COMPLETE THIS METHOD **/
		// Initialize root, which will have null for substring held at node, first child of this node and sibling of this node.
		PrefixTreeNode root = new PrefixTreeNode(null, null, null);
		// For every word in allWords, run insertWordRec function.
		for (int i = 0; i < allWords.length; i++) {
			insertWordRec(root, allWords[i], i, allWords);
		}
		return root;
	}

	/** 
	 * Traverses a PrefixTree structure to find all leaf nodes representing complete words starting with a given prefix.
	 * Goes through each node's children and siblings to generate an ArrayList of matching leaf nodes until it reaches end of prefix or trie.
	 * 
	 * @param root : Root of PrefixTree that stores all words to search on for completeWordList
	 * @param allWords : Array of words that have been inserted into the PrefixTree
	 * @param prefix : Prefix to be completed with words in PrefixTree
	 * @param results : ArrayList of PrefixTreeNodes that contains all words associated with a common prefix
	 * @return results 
	 */
	private static ArrayList<PrefixTreeNode> findAllPrefixRec(PrefixTreeNode root, String[] allWords, String prefix, ArrayList<PrefixTreeNode> results) {
		// Check if root node does not have any children and
		// Check if the word at the wordIndex of allWords starts with the given prefix
		if(root.firstChild == null && allWords[root.substr.wordIndex].startsWith(prefix)) {
			// Check if prefix string is not empty after removing leading and trailing whitespace chars 
			if(!(prefix.trim() == "")) {
				results.add(root);
			}
		}
		// Check if root has no children and no siblings
		// If this is the case, it means there are no words in the trie that match the given prefix 
		if(root.firstChild == null && root.sibling == null) {
			results = null;
		}
		// Apply recursive function on all nodes associated with firstChild of the root 
		if(root.firstChild != null) {
			findAllPrefixRec(root.firstChild, allWords, prefix, results);
		}
		// Apply recursive function on all nodes associated with the siblings of the root
		if(root.sibling != null) {
			findAllPrefixRec(root.sibling, allWords, prefix, results);
		}
		return results;
	}
	/**
	 * Given a PrefixTree, returns the "completeWordList" for the given prefix, i.e. all the
	 * leaf nodes in the PrefixTree whose words start with this prefix. For instance,
	 * if the PrefixTree had the words "bear", "bull", "stock", and "bell", the
	 * completeWordList for prefix "b" would be the leaf nodes that hold "bear",
	 * "bull", and "bell"; for prefix "be", the completeWordList would be the leaf nodes
	 * that hold "bear" and "bell", and for prefix "bell", completeWordList would be the
	 * leaf node that holds "bell". (The last example shows that an input prefix can
	 * be an entire word.) The order of returned leaf nodes DOES NOT MATTER. So, for
	 * prefix "be", the returned list of leaf nodes can be either hold [bear,bell]
	 * or [bell,bear].
	 *
	 * @param root
	 *            Root of PrefixTree that stores all words to search on for completeWordList
	 * @param allWords
	 *            Array of words that have been inserted into the PrefixTree
	 * @param prefix
	 *            Prefix to be completed with words in PrefixTree
	 * @return List of all leaf nodes in PrefixTree that hold words that start with the
	 *         prefix, order of leaf nodes does not matter. If there is no word in
	 *         the tree that has this prefix, null is returned.
	 */
	public static ArrayList<PrefixTreeNode> completeWordList(PrefixTreeNode root, String[] allWords, String prefix) {
		// Initialize results to store all of the words associated with a common prefix 
		ArrayList<PrefixTreeNode> results = new ArrayList<PrefixTreeNode>();
		// Apply recursive function to find all words associated with a prefix 
		results = findAllPrefixRec(root.firstChild, allWords, prefix, results);
		// If results is empty or null, return null
		if(results.isEmpty() || results == null) {
			results = null;
		}
		// Return results 
		return results;
	}
	
	

	public static void print(PrefixTreeNode root, String[] allWords) {
		System.out.println("\nPrefixTree\n");
		print(root, 1, allWords);
	}

	private static void print(PrefixTreeNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i = 0; i < indent - 1; i++) {
			System.out.print("    ");
		}

		if (root.substr != null) {
			String pre = words[root.substr.wordIndex].substring(0, root.substr.endIndex + 1);
			System.out.println("      " + pre);
		}

		for (int i = 0; i < indent - 1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}

		for (PrefixTreeNode ptr = root.firstChild; ptr != null; ptr = ptr.sibling) {
			for (int i = 0; i < indent - 1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent + 1, words);
		}
	}
}
