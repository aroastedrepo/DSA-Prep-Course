# DSA-Prep-Course

Contains assignments from Rutgers DSA (CS112) course. I'm redoing these for a good refresher on DSA. Feel free to follow along (my code is commented heavily for greater understanding).

### Project 3: Trie

This project involves building a tree structure called PrefixTree for a dictionary of English words, and use the PrefixTree to generate completeWordList(list of words starting with the given prefix) for string searches.

In this project, a main challenge area was understanding the recursion pattern. For example, in insertWordRec, the recursion occurs if prefix and word are same string, because we want to break down how similar they are in each pass, and how many characters they share in common. A good approach for recursion is understanding what requires repetitive multiple passes that are completed with latter parts of a function.

### Project 4: Toy Search Engine

This project involves finding the occurrences of keywords in several documents, and organizing them by the frequency of appearance. The Occurrence of each keyword is held in the Occurrence Object, which contains the document where a keyword occurs, and the frequency of that keyword in the document.

In this project, a main challenge area was knowing how to separate keyword frequencies per document. I had to revise my updateDocumentFrequency method, as I was often combining keyword frequencies, instead of separating them per document first, and then combining. The binary search implementation for insertLastOccurrence was interesting, given that items are in descending order. In trad binary search, you compare elements to target value and adjust the search according to them. Here, as list is in descending order, we reverse comparisons and adjustments (if fre of occurrence at midpoint is less than frequency of occurrence that is being inserted, decrement high index to move towards beginning of list, else increment low index to move towards end of list). Calculation of midpoint in my implementation was the same because I adjusted low and high indices in latter parts.

### Project 5: Friends

This project involves finding the shortest path between two nodes in a graph, cliques based on a particular attribute in a graph, and connectors (a node that connects another node such that the other node would not be able to reach anyone else).

For the shortest path, the ideal algorithm is BFS because it is well suited for finding the shortest path. This is due to the fact that it explores all nodes at the present "depth" (or distance from the start node) before moving on to nodes at the next depth level. DFS explores as far down a branch as possible, so it may not find the shortest path in the first shot. Dijkstra's is unnecessarily complex for unweighted graphs.

For cliques in a graph, DFS is ideal as it explores all nodes connected to a starting node. This property allows DFS to effectively traverse an entire connected component (clique) before moving on to another component. DFS can be repeatedly applied starting from unvisited nodes to ensure that all separate cliques within the graph are identified. Each time DFS is initiated from an unvisited node, it will mark all nodes in that component as visited. While BFS can also find connected components, DFS is preferred for its straightforward implementation.

For connectors, I used a modified version of DFS, because an out-of-box implementation does not provide the order in which nodes are first visited, and the earliest reachable vertex. Our modified DFS works as follows:

- Initialize all parameter values.
- Visit each vertex, and assign all values upon visting.
- For each unvisited neighbor, recursively perform DFS and update back values.
- Upon returning from recursion, update the back value of the current vertex based on its children's back values.
- If curr is not the root (parent != curr) and dfsnum[curr] <= back[next], it means curr is a connector because the only way to reach next or its descendants from the rest of the graph is through curr.
- If curr is the root of the DFS tree (parent == curr) and has more than one child, it is also a connector, as its removal would disconnect the graph.

In this project, the main challenge area was finding connectors. I didn't understand the algorithm at first, and needed to go through each example on paper myself. However, this exercise proved to be a rigorous refresher of graphs, especially DFS and BFS algorithms.
