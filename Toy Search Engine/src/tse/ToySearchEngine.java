package tse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class ToySearchEngine {
	
	/**
	 * This is a hash table of all keys. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keysIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keysIndex and noiseWords hash tables.
	 */
	public ToySearchEngine() {
		keysIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of key occurrences
	 * in the document. Uses the getKey method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keys in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String, Occurrence> loadKeysFromDocument(String docFile) throws FileNotFoundException {
	    // Initialize a HashMap to store the occurrences of keywords in the doc
	    HashMap<String, Occurrence> keysInDoc = new HashMap<>();
	    
	    // Open and read through docFile provided
	    try (BufferedReader reader = new BufferedReader(new FileReader(docFile))) {
	        String line;
	        // Read each line in the document
	        while ((line = reader.readLine()) != null) {
	            // Split the line into words
	            String[] words = line.split("\\s+");
	            // Process each word
	            for (String word : words) {
	                // Get the keyword for the word
	                String keyword = getKey(word);
	                // Check if the keyword is not a noise word
	                if (!noiseWords.contains(keyword)) {
	                    // If the keyword already exists in the HashMap, update its occurrence frequency
	                    if (keysInDoc.containsKey(keyword)) {
	                        Occurrence occurrence = keysInDoc.get(keyword);
	                        occurrence.frequency++;
	                    } else { 
	                    	// Else, add a new occurrence to the HashMap
	                        Occurrence occurrence = new Occurrence(docFile, 1);
	                        keysInDoc.put(keyword, occurrence);
	                    }
	                }
	            }
	        }
	    } 
	    // If exception, print stack trace
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    // Return the HashMap containing occurrences of keywords in the doc
	    return keysInDoc;
	}
	/**
	 * Merges the keys for a single document into the master keysIndex
	 * hash table. For each key, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same key's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeys(HashMap<String, Occurrence> kws) {
	    // Iterate over each entry (keyword and occurrence) in the provided HashMap
	    for (Map.Entry<String, Occurrence> entry : kws.entrySet()) {
	        // Extract the keyword and occurrence from the entry
	        String keyword = entry.getKey(); 
	        Occurrence occ = entry.getValue(); 
	        
	        // Check if the keyword already exists in the keysIndex
	        if (keysIndex.containsKey(keyword)) {
	            // If the keyword exists, get the list of occurrences associated with it
	            ArrayList<Occurrence> occList = keysIndex.get(keyword);
	            boolean found = false;
	            // Iterate over existing occurrences to find a match with the provided occurrence
	            for (Occurrence existingOcc : occList) {
	                // If an occurrence with the same doc is found, update its frequency
	                if (existingOcc.document.equals(occ.document)) {
	                    existingOcc.frequency += occ.frequency; // Update frequency
	                    found = true;
	                    break;
	                }
	            }
	            // If no matching occurrence is found, add the provided occurrence to the list
	            // and reorder occurrences based on frequency
	            if (!found) {
	                occList.add(occ); // Add occurrence from a different doc
	                insertLastOccurrence(occList); // Reorder occurrences
	            }
	        } else {
	            // If the keyword does not exist in keysIndex, create a new list and add the occurrence
	            ArrayList<Occurrence> occList = new ArrayList<>();
	            occList.add(occ);
	            keysIndex.put(keyword, occList);
	        }
	    }
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * Note: No other punctuation characters will appear in grading testcases
	 * 
	 * @param word Candidate word
	 * @return Key (word without trailing punctuation, LOWER CASE)
	 */
	public String getKey(String word) {
	    // Remove trailing punctuation characters from the word
	    word = word.replaceAll("[.,?:;!]$", "");
	    
	    // Check if the word contains only alphabetic characters
	    if (!word.matches("[a-zA-Z]+")) {
	        return null; // Return null if the word contains non-alphabetic characters
	    }
	    
	    // Convert the word to lowercase
	    word = word.toLowerCase();
	    
	    // Check if the word is a noise word
	    if (noiseWords.contains(word)) {
	        return null; // Return null if the word is a noise word
	    }
	    
	    // Return the processed word as the keyword
	    return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
	    // Check if the list contains only one element
	    if(occs.size() == 1) {
	        return null; // If there's only one element, no insertion is needed, so return null
	    }
	    
	    // Get the last occurrence from the list
	    Occurrence last = occs.get(occs.size() - 1);
	    int freqInsert = last.frequency; // Frequency of the last occurrence to be inserted
	    int low = 0; // Index of the first element in the list
	    int high = occs.size() - 2; // Index of the second to last element in the list
	    
	    ArrayList<Integer> midPoint = new ArrayList<>(); // List to store midpoints during binary search
	    
	    // Perform binary search on the list
	    while(low <= high) {
	        int mid = (low + high) / 2; // Calculate the midpoint
	        midPoint.add(mid); // Add the midpoint to the list
	        
	        Occurrence midOcc = occs.get(mid); // Get the occurrence at the midpoint
	        int midFreq = midOcc.frequency; // Frequency of the occurrence at the midpoint
	        
	        // Compare frequencies to determine the position for insertion
	        if(midFreq == freqInsert) {
	            occs.add(mid + 1, last); // Insert the last occurrence after the occurrence at the midpoint
	            return midPoint; // Return the list of mid points
	        } else if (midFreq < freqInsert) {
	            high = mid - 1; // Adjust the high index for the upper half
	        } else {
	            low = mid + 1; // Adjust the low index for the lower half
	        }
	    }
	    
	    occs.add(low, last); // Insert the last occurrence at the calculated index
	    return midPoint; // Return the list of mid points
	}
	
	/**
	 * This method indexes all words found in all the input documents. When this
	 * method is done, the keysIndex hash table will be filled with all keys,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void buildIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all words
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeysFromDocument(docFile);
			mergeKeys(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
	    // Create a map to store document frequencies
	    HashMap<String, Integer> documentFreq = new HashMap<>();

	    // Update document frequencies for both keywords
	    updateDocFreq(documentFreq, kw1, kw2);

	    // Sort document frequencies in descending order
	    ArrayList<Map.Entry<String, Integer>> sortedFreq = new ArrayList<>(documentFreq.entrySet());
	    sortedFreq.sort((entry1, entry2) -> {
	        int freqComparison = entry2.getValue().compareTo(entry1.getValue());
	        if (freqComparison == 0) {
	            // If frequencies are equal, use lexicographical order for document names
	            return entry1.getKey().compareTo(entry2.getKey());
	        }
	        return freqComparison;
	    });

	    // Create a list to store the top 5 documents
	    ArrayList<String> top5Documents = new ArrayList<>();

	    // Add top 5 documents to the result list
	    int count = 0;
	    for (Map.Entry<String, Integer> entry : sortedFreq) {
	        top5Documents.add(entry.getKey());
	        count++;
	        if (count == 5) {
	            break; // Limit reached
	        }
	    }

	    // Return the top 5 documents
	    
	    return top5Documents.isEmpty() ? null : top5Documents;
	}
	/**
	 * updateDocFreq gets occurrences of kw1 and kw2 from keysIndex, and uses these to adjust the frequencies 
	 * of both keywords in documentFrequencies, a hashmap that contains the document name, and the frequency of the 
	 * keyword in that document.
	 * 
	 * @param documentFrequencies Hashmap of document name, and frequency of a specified keyword in the document
	 * @param kw1 keyword1, a string keyword1, that we are searching for
	 * @param kw2 keyword2, a string keyword2, that we are searching for
	 */
	private void updateDocFreq(HashMap<String, Integer> documentFrequencies, String kw1, String kw2) {
	    // Get occurrences for both keywords
		// If there are no occurrences, an empty ArrayList is returned, as per getOrDefault
	    ArrayList<Occurrence> occurrencesKw1 = keysIndex.getOrDefault(kw1, new ArrayList<>());
	    ArrayList<Occurrence> occurrencesKw2 = keysIndex.getOrDefault(kw2, new ArrayList<>());
	    
	    //System.out.println("Occ of " + kw1 + ": " + occurrencesKw1);
	    //System.out.println("Occ of " + kw2 + ": " + occurrencesKw2);

	    // Update document frequencies based on frequency of kw1
	    for (Occurrence occurrence : occurrencesKw1) {
	        String document = occurrence.document;
	        int frequency = occurrence.frequency;

	        // Update document frequency by adding the frequency of the occurrence
	        documentFrequencies.put(document, documentFrequencies.getOrDefault(document, 0) + frequency);
	    }
	    
	    // Update document frequencies based on frequency of kw2
	    for (Occurrence occurrence : occurrencesKw2) {
	        String document = occurrence.document;
	        int frequency = occurrence.frequency;

	        // Update document frequency by adding the frequency of the occurrence
	        documentFrequencies.put(document, documentFrequencies.getOrDefault(document, 0) + frequency);
	        
	        //System.out.println("Document Freq: " + documentFrequencies);
	    }
	}
}
