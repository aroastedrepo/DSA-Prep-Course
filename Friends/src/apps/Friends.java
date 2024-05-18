package apps;

import structures.Queue;
import structures.Stack;

import java.util.*;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		// Check if the graph g is null or if either p1 or p2 is not present in the graph. 
		// If any of these conditions are true, return null as there can't be a shortest chain.
		if(g == null || !g.map.containsKey(p1) || !g.map.containsKey(p2)) {
			return null;
		}
		// Get indices of starting entry, p1
		int start = g.map.get(p1);
		// Get indices of ending entry, p2
		int end = g.map.get(p2);
		
		// Create a queue for BFS
		Queue<ArrayList<String>> queue = new Queue<>();
		// Create a set to keep track of visited vertices
		Set<Integer> visited = new HashSet<>();
		// Enqueue a new array list containing p1, the starting point of BFS
		queue.enqueue(new ArrayList<>(Arrays.asList(p1)));
		
		// Start a loop until queue is empty
		// If queue is empty, this means that all possible chains have been exhausted
		while (!queue.isEmpty()) {
			// Dequeue next chain to go through
			ArrayList<String> currChain = queue.dequeue();
			// Get last person in current chain
			String lastPerson = currChain.get(currChain.size()-1);
			// Find index of last person in current chain 
			int lastPersonIndex = g.map.get(lastPerson);
			
			// If last person in current chain is same as the end person, return current chain, which is shortest chain
			// In BFS, since we are exploring level by level, the first time we reach p2, it will the shortest path from p1 to p2.
			// BFS explores all possible paths of length k before moving to paths of length k+1, ensuring that the first time we reach p2, it will be via the shortest path.
			if(lastPersonIndex == end) {
				return currChain;
			}
			// Mark the last person as visited to avoid revisiting them
			visited.add(lastPersonIndex);
			
			// Iterate through friends of last person in current chain 
			for(Friend friend = g.members[lastPersonIndex].first; friend != null; friend = friend.next) {
				// If the friend has not been visited, iterate further in the graph
				if(!visited.contains(friend.fnum)) {
					// Create a new chain by adding the current friend to the current chain
					// Enqueue this chain so that it can be explored further 
					ArrayList<String> newChain = new ArrayList<>(currChain);
					newChain.add(g.members[friend.fnum].name);
					queue.enqueue(newChain);
				} 
			} // end of loop for exploring friends of last person in current chain 
		} // end of loop for exploring chains in queue 
		
		return null; // If no shortest chain found, return null 
	}
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		
		// If graph g is null, return null. Cannot be any cliques.
		if(g == null) {
			return null;
		}
		
		// Initialize ArrayList to store the cliques found 
		// Initialize HashSet to keep track of visited vertices.
		ArrayList<ArrayList<String>> cliques = new ArrayList<>();
		Set<Integer> visited = new HashSet<>();
		
		// Iterate through each entry in the graph 
		for(int i = 0; i < g.members.length; i++) {
			// If the person is a student, and belongs to the school mentioned, and has not been visited yet, start looking into clique w/ person. 
			if(g.members[i].student && g.members[i].school.equals(school) && !visited.contains(i)) {
				// Create ArrayList to store current clique
				ArrayList<String> clique = new ArrayList<>();
				// DFS from person to find all members of clique
				dfs(g, i, visited, clique);
				// Add clique to list of cliques found 
				cliques.add(clique);
			}
		}  // End of loop for exploring people in graph 
		
		// If no cliques found, return null
		if(cliques.isEmpty()) {
			return null;
		}
		
		// Return list of cliques found 
		return cliques;
		
	}
	/**
	 * Performs DFS to explore the graph starting from the specified person index.
	 * It adds the names of visited persons to the clique ArrayList, ensuring that only students from the same school are included in the clique.
	 * The visited set keeps track of visited vertices to avoid revisiting them.
	 * 
	 * @param g graph
	 * @param i index of curr person being visited
	 * @param visited, set to keep track of visited vertices
	 * @param clique, arraylist to store current clique being explored 
	 */
	private static void dfs(Graph g, int i, Set<Integer> visited, ArrayList<String> clique) {
		// Mark the current person index as visited
		visited.add(i);
		// Add the name of the current person to the clique
		clique.add(g.members[i].name);
		
		// Explore friends of the current person
		for(Friend friend = g.members[i].first; friend != null; friend = friend.next) {
			// Check if the friend hasn't been visited yet and belongs to the same school as the current person
			if(!visited.contains(friend.fnum) && g.members[friend.fnum].student && g.members[friend.fnum].school.equals(g.members[i].school)) {
				// Recursively explore the friend's connections
				dfs(g, friend.fnum, visited, clique);
			}
		}
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		// Check if graph is null, and return null. Cannot be any connectors if graph does not exist.
        if (g == null) {
            return null;
        }
     
        // Initialize data structures
        ArrayList<String> connectors = new ArrayList<>(); // ArrayList for all connectors
        int[] dfsnum = new int[g.members.length]; // Array to store DFS numbers
        int[] back = new int[g.members.length]; // Array to store back numbers
        boolean[] visited = new boolean[g.members.length]; // Array to keep track of visited vertices
        Set<Integer> connectorsSet = new HashSet<>(); // Set to store indices of connectors
        int dfsnumCounter = 0; // Counter for assigning DFS numbers

        // Perform DFS on each vertex
        for (int i = 0; i < g.members.length; i++) {
        	// Check if vertex has not been visited yet
            if (!visited[i]) {
            	// Start DFS from the current vertex
                dfs_connectors(g, i, i, visited, dfsnum, back, connectorsSet, dfsnumCounter);
            }
        }

        // Convert set of connectors to list
        connectors.addAll(g.map.keySet()); // Adds vertex names from graph's  map to connectors ArrayList. 
        // g.map contains a mapping of vertex names to corresponding indicies in graph
        // Adding all keys = all vertices seen as connectors at first
        
        // First, check if name from connectors ArrayList is contained in connectorsSet
        // If it does not exist in connectorsSet, this returns true, so element should be removed from connectors ArrayList
        // Retains vertex names only if they are identified as connectors during DFS
        connectors.removeIf(name -> !connectorsSet.contains(g.map.get(name)));
     
        // If no connectors are found, return null
        if (connectors.isEmpty()) {
            return null;
        }
        
        // Output connectors
        return connectors;
    }
	
	/**
	 * Performs a modified version of DFS to identify connectors.
	 * 
	 * @param g, graph
	 * @param curr, index of curr vertex being visited 
	 * @param parent, index of parent vertex in DFS traversal
	 * @param visited, array to keep track of visited vertices
	 * @param dfsnum, array to store DFS numbers assigned to vertices
	 * @param back, array to store back numbers assigned to vertices
	 * @param connectors, set to store indices of connectors found during DFS traversal
	 * @param dfsnumCounter, a counter for assigning DFS numbers
	 */
	private static void dfs_connectors(Graph g, int curr, int parent, boolean[] visited, int[] dfsnum, int[] back, Set<Integer> connectors, int dfsnumCounter) {
		// Mark the current vertex as visited
        visited[curr] = true;
        // Assign DFS number and back number to the current vertex
        dfsnum[curr] = back[curr] = ++dfsnumCounter;
        
        // Counter to keep track of children of the current vertex
        int children = 0;
        
        // Iterate through the neighbors of the current vertex
        for (Friend friend = g.members[curr].first; friend != null; friend = friend.next) {
            int next = friend.fnum; // Index of the next neighbor
            // Explore unvisited neighbors
            if (!visited[next]) {
            	// Increment children count
                children++;
                // Recursively explore the neighbor
                dfs_connectors(g, next, curr, visited, dfsnum, back, connectors, dfsnumCounter);
                // Update the back number of the current vertex
                // If next is an ancestor of curr, then there exists a back edge from curr to next, and back[curr] needs to capture the lowest DFS number reachable from curr through this back edge. 
                // If next is a descendant of curr, then back[curr] should still capture the lowest DFS number reachable from curr.
                back[curr] = Math.min(back[curr], back[next]);
                // Check if the current vertex, curr, is a connector
                // First part: if the DFS number of curr is less than or equal to the back number of its neighbor next, then next is not reachable from curr via a back edge. Ensure that curr is not root of DFS tree 
                // If both conditions agree, removing curr from DFS traversal would disconnect next or one of its descendants from DFS tree. This makes curr a connector.
                // Second part: Checks if curr is root of DFS tree, and if it has more than one children. If true, removing curr from DFS traversal would disconnect multiple subtrees rooted at its children.
                // Curr must be a connector in this case as well, because removing it would mean multiple subtrees in its children would be disconnected.
                if ((dfsnum[curr] <= back[next] && parent != curr) || (parent == curr && children > 1)) {
                    connectors.add(curr); // Add the current vertex to connectors set
                }
            } else if (next != parent) {
            	// Update the back number of the current vertex based on the visited neighbor
                back[curr] = Math.min(back[curr], dfsnum[next]);
            }
        }
    }
}

