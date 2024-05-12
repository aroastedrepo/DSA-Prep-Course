# DSA-Prep-Course

Contains assignments from Rutgers DSA (CS112) course. I'm redoing these for a good refresher on DSA. Feel free to follow along (my code is commented heavily for greater understanding).

### Project 3: Trie

This project involves building a tree structure called PrefixTree for a dictionary of English words, and use the PrefixTree to generate completeWordList(list of words starting with the given prefix) for string searches.

In this project, a main challenge area was understanding the recursion pattern. For example, in insertWordRec, the recursion occurs if prefix and word are same string, because we want to break down how similar they are in each pass, and how many characters they share in common. A good approach for recursion is understanding what requires repetitive multiple passes that are completed with latter parts of a function.

### Project 4: Toy Search Engine

This project involves finding the occurrences of keywords in several documents, and organizing them by the frequency of appearance. The Occurrence of each keyword is held in the Occurrence Object, which contains the document where a keyword occurs, and the frequency of that keyword in the document.

In this project, a main challenge area was knowing how to separate keyword frequencies per document. I had to go back and revise my updateDocumentFrequency method, as I was often combining keyword frequencies, instead of separating them per document first, and then combining. The binary search implementation for insertLastOccurrence was interesting, given that items are in descending order. In trad binary search, you compare elements to target value and adjust the search according to them. Here, as list is in descending order, we reverse comparisons and adjustments (if fre of occurrence at midpoint is less than frequency of occurrence that is being inserted, decrement high index to move towards beginning of list, else increment low index to move towards end of list). Calculation of midpoint in my implementation was the same because I adjusted low and high indices in latter parts.
