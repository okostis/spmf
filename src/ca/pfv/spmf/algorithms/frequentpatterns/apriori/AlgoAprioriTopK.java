package ca.pfv.spmf.algorithms.frequentpatterns.apriori;

/* This file is copyright (c) 2008-2013 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import ca.pfv.spmf.algorithms.ArraysAlgos;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import ca.pfv.spmf.tools.MemoryLogger;

/**
 * This is a top-k implementation of the SPMF version of Apriori algorithm that
 * uses binary search to check if subsets of a candidate are frequent and other
 * optimizations. <br/>
 * <br/>
 * 
 * The apriori algorithm is described in : <br/>
 * <br/>
 * 
 * Agrawal R, Srikant R. "Fast Algorithms for Mining Association Rules", VLDB.
 * Sep 12-15 1994, Chile, 487-99, <br/>
 * <br/>
 * 
 * The versinon of the Apriori algorithm finds is modified to find the
 * top-kfrequents itemsets and their support in a transaction database provided
 * by the user. <br/>
 * <br/>
 * 
 * This is an optimized version that saves the result to a file or keep it into
 * memory if no output path is provided by the user to the runAlgorithm()
 * method.
 * 
 * @see Itemset
 * @see Itemsets
 * @author Philippe Fournier-Viger
 */
public class AlgoAprioriTopK {

	// ======================================
	/** the number of patterns to find "n" */
	protected int n;

	/** priority queue to store the top-n patterns */
	PriorityQueue<Itemset> nItemsets;
	// ======================================

	/** the current level k in the breadth-first search */
	protected int k;

	/** total number of candidates */
	protected int totalCandidateCount = 0;

	/** number of candidates generated during last execution */
	protected long startTimestamp; //

	/** start time of last execution */
	protected long endTimestamp; //

	/** end time of last execution */
	private int itemsetCount; //

	/** itemset found during last execution */
	private int databaseSize;

	/** the minimum support set by the user */
	private int minsupRelative;

	/**
	 * A memory representation of the database. Each position in the list represents
	 * a transaction
	 */
	private List<int[]> database = null;

	/**
	 * The patterns that are found (if the user wants to keep them into memory)
	 */
	protected Itemsets patterns = null;

	/** object to write the output file (if the user wants to write to a file) */
	BufferedWriter writer = null;

	/** maximum pattern length */
	private int maxPatternLength = 10000;

	/**
	 * Default constructor
	 */
	public AlgoAprioriTopK() {

	}

	/**
	 * Method to run the algorithm
	 * 
	 * @param kValue the number of patterns to find (i.e. the top-k patterns)
	 * @param input  the path of an input file
	 * @return the top k itemsets
	 * @throws IOException exception if error while writting or reading the
	 *                     input/output file
	 */
	public PriorityQueue<Itemset> runAlgorithm(int kValue, String input, String output) throws IOException {

		// if the user wants to keep the result into memory
		if (output == null) {
			writer = null;
			patterns = new Itemsets("TOP-K ITEMSETS");
		} else { // if the user wants to save the result to a file
			patterns = null;
			writer = new BufferedWriter(new FileWriter(output));
		}

		// ======================================
		this.n = kValue;

		// Initialize the priority queue to store the top K patterns
		nItemsets = new PriorityQueue<>(Comparator.comparingInt(Itemset::getAbsoluteSupport));

		// Set the internal minsup value to 1
		minsupRelative = 1;
		// ======================================

		// record the start time
		startTimestamp = System.currentTimeMillis();

		// set the number of itemset found to zero
		itemsetCount = 0;
		// set the number of candidate found to zero
		totalCandidateCount = 0;
		// reset the utility for checking the memory usage
		MemoryLogger.getInstance().reset();

		// READ THE INPUT FILE
		// variable to count the number of transactions
		databaseSize = 0;
		// Map to count the support of each item
		// Key: item Value : support
		Map<Integer, Integer> mapItemCount = new HashMap<Integer, Integer>(); // to count the support of each item

		database = new ArrayList<int[]>(); // the database in memory (intially empty)

		// scan the database to load it into memory and count the support of each single
		// item at the same time
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line;
		// for each line (transactions) until the end of the file
		while (((line = reader.readLine()) != null)) {
			// if the line is a comment, is empty or is a
			// kind of metadata
			if (line.isEmpty() == true || line.charAt(0) == '#' || line.charAt(0) == '%' || line.charAt(0) == '@') {
				continue;
			}
			// split the line according to spaces
			String[] lineSplited = line.split(" ");

			// create an array of int to store the items in this transaction
			int transaction[] = new int[lineSplited.length];

			// for each item in this line (transaction)
			for (int i = 0; i < lineSplited.length; i++) {
				// transform this item from a string to an integer
				Integer item = Integer.parseInt(lineSplited[i]);
				// store the item in the memory representation of the database
				transaction[i] = item;
				// increase the support count
				Integer count = mapItemCount.get(item);
				if (count == null) {
					mapItemCount.put(item, 1);
				} else {
					mapItemCount.put(item, ++count);
				}
			}
			// add the transaction to the database
			database.add(transaction);
			// increase the number of transaction
			databaseSize++;
		}
		// close the input file
		reader.close();

		// we start looking for itemset of size 1
		k = 1;

		// We add all frequent items to the set of candidate of size 1
		// ====================================== OPTIMIZATION ===========
		// Find the highest support among the single items
		int itemCount = mapItemCount.size();
		if (itemCount >= n) {
			int itemSupports[] = new int[mapItemCount.size()];
			int index = 0;
			for (Entry<Integer, Integer> entry : mapItemCount.entrySet()) {
				itemSupports[index++] = entry.getValue();
			}
			Arrays.sort(itemSupports);
//			System.out.println(Arrays.toString(itemSupports));
			minsupRelative = itemSupports[itemSupports.length - n];
		}
		// ==============================================================

		List<Integer> frequent1 = new ArrayList<Integer>();
		for (Entry<Integer, Integer> entry : mapItemCount.entrySet()) {
			// ======================================
			int support = entry.getValue();
			if (entry.getValue() >= minsupRelative) {
				frequent1.add(entry.getKey());
				saveItemsetToQueue(new Itemset(entry.getKey(), support), support);
			}
			// ======================================
		}

		mapItemCount = null;

		// We sort the list of candidates by lexical order
		// (Apriori need to use a total order otherwise it does not work)
		Collections.sort(frequent1, new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
		});

		// If no frequent item, the algorithm stops!
		if (frequent1.size() == 0 || maxPatternLength <= 1) {

			// record end time
			endTimestamp = System.currentTimeMillis();
			// check the memory usage
			MemoryLogger.getInstance().checkMemory();

			// close the output file if we used it
			if (writer != null) {
				writer.close();
			}
			return nItemsets;
		}

		// add the frequent items of size 1 to the total number of candidates
		totalCandidateCount += frequent1.size();

		// Now we will perform a loop to find all frequent itemsets of size > 1
		// starting from size k = 2.
		// The loop will stop when no candidates can be generated.
		List<Itemset> level = null;
		k = 2;
		do {
			// we check the memory usage
			MemoryLogger.getInstance().checkMemory();

			// Generate candidates of size K
			List<Itemset> candidatesK;

			// if we are at level k=2, we use an optimization to generate candidates
			if (k == 2) {
				candidatesK = generateCandidate2(frequent1);
			} else {
				// otherwise we use the regular way to generate candidates
				candidatesK = generateCandidateSizeK(level);
			}

			// we add the number of candidates generated to the total
			totalCandidateCount += candidatesK.size();

			// We scan the database one time to calculate the support
			// of each candidates and keep those with higher support.
			// For each transaction:
			for (int[] transaction : database) {
				// NEW OPTIMIZATION 2013: Skip transactions shorter than k!
				if (transaction.length < k) {
//					System.out.println("test");
					continue;
				}
				// END OF NEW OPTIMIZATION

				// for each candidate:
				loopCand: for (Itemset candidate : candidatesK) {
					// a variable that will be use to check if
					// all items of candidate are in this transaction
					int pos = 0;
					// for each item in this transaction
					for (int item : transaction) {
						// if the item correspond to the current item of candidate
						if (item == candidate.itemset[pos]) {
							// we will try to find the next item of candidate next
							pos++;
							// if we found all items of candidate in this transaction
							if (pos == candidate.itemset.length) {
								// we increase the support of this candidate
								candidate.support++;
								continue loopCand;
							}
							// Because of lexical order, we don't need to
							// continue scanning the transaction if the current item
							// is larger than the one that we search in candidate.
						} else if (item > candidate.itemset[pos]) {
							continue loopCand;
						}

					}
				}
			}

			// We build the level k+1 with all the candidates that have
			// a support higher than the minsup threshold.
			level = new ArrayList<Itemset>();
			if (k < maxPatternLength + 1) {
				for (Itemset candidate : candidatesK) {
					// if the support is > minsup
					if (candidate.getAbsoluteSupport() >= minsupRelative) {
						// add the candidate
						level.add(candidate);

						// ======================================
						// the itemset is frequent so save it into results
						saveItemsetToQueue(candidate, candidate.getAbsoluteSupport());
						// ======================================
					}
				}
			}
			// we will generate larger itemsets next.
			k++;
		} while (level.isEmpty() == false);

		// record end time
		endTimestamp = System.currentTimeMillis();
		// check the memory usage
		MemoryLogger.getInstance().checkMemory();

		// close the output file if the result was saved to a file.
		if (writer != null) {
			// ======================================
			Iterator<Itemset> iter = nItemsets.iterator();
			while (iter.hasNext()) {
				saveItemsetToFile(iter.next());
			}
			// ======================================
			writer.close();
		}

		// ======================================
		return nItemsets;
		// ======================================
	}

	/**
	 * Return the number of transactions in the last database read by the algorithm.
	 * 
	 * @return the number of transactions.
	 */
	public int getDatabaseSize() {
		return databaseSize;
	}

	/**
	 * This method generates candidates itemsets of size 2 based on itemsets of size
	 * 1.
	 * 
	 * @param frequent1 the list of frequent itemsets of size 1.
	 * @return a List of Itemset that are the candidates of size 2.
	 */
	private List<Itemset> generateCandidate2(List<Integer> frequent1) {
		List<Itemset> candidates = new ArrayList<Itemset>();

		// For each itemset I1 and I2 of level k-1
		for (int i = 0; i < frequent1.size(); i++) {
			Integer item1 = frequent1.get(i);

			for (int j = i + 1; j < frequent1.size(); j++) {
				Integer item2 = frequent1.get(j);

				// Create a new candidate by combining itemset1 and itemset2
				candidates.add(new Itemset(new int[] { item1, item2 }));
			}
		}
		return candidates;
	}

	/**
	 * Method to generate itemsets of size k from frequent itemsets of size K-1.
	 * 
	 * @param levelK_1 frequent itemsets of size k-1
	 * @return itemsets of size k
	 */
	protected List<Itemset> generateCandidateSizeK(List<Itemset> levelK_1) {
		// create a variable to store candidates
		List<Itemset> candidates = new ArrayList<Itemset>();

		// For each itemset I1 and I2 of level k-1
		loop1: for (int i = 0; i < levelK_1.size(); i++) {
			int[] itemset1 = levelK_1.get(i).itemset;
			loop2: for (int j = i + 1; j < levelK_1.size(); j++) {
				int[] itemset2 = levelK_1.get(j).itemset;

				// we compare items of itemset1 and itemset2.
				// If they have all the same k-1 items and the last item of
				// itemset1 is smaller than
				// the last item of itemset2, we will combine them to generate a
				// candidate
				for (int k = 0; k < itemset1.length; k++) {
					// if they are the last items
					if (k == itemset1.length - 1) {
						// the one from itemset1 should be smaller (lexical
						// order)
						// and different from the one of itemset2
						if (itemset1[k] >= itemset2[k]) {
							continue loop1;
						}
					}
					// if they are not the last items, and
					else if (itemset1[k] < itemset2[k]) {
						continue loop2; // we continue searching
					} else if (itemset1[k] > itemset2[k]) {
						continue loop1; // we stop searching: because of lexical
										// order
					}
				}

				// Create a new candidate by combining itemset1 and itemset2
				int newItemset[] = new int[itemset1.length + 1];
				System.arraycopy(itemset1, 0, newItemset, 0, itemset1.length);
				newItemset[itemset1.length] = itemset2[itemset2.length - 1];

				// The candidate is tested to see if its subsets of size k-1 are
				// included in
				// level k-1 (they are frequent).
				if (allSubsetsOfSizeK_1AreFrequent(newItemset, levelK_1)) {
					candidates.add(new Itemset(newItemset));
				}
			}
		}
		return candidates; // return the set of candidates
	}

	/**
	 * Method to check if all the subsets of size k-1 of a candidate of size k are
	 * frequent
	 * 
	 * @param candidate a candidate itemset of size k
	 * @param levelK_1  the frequent itemsets of size k-1
	 * @return true if all the subsets are frequet
	 */
	protected boolean allSubsetsOfSizeK_1AreFrequent(int[] candidate, List<Itemset> levelK_1) {
		// generate all subsets by always each item from the candidate, one by one
		for (int posRemoved = 0; posRemoved < candidate.length; posRemoved++) {

			// perform a binary search to check if the subset appears in level k-1.
			int first = 0;
			int last = levelK_1.size() - 1;

			// variable to remember if we found the subset
			boolean found = false;
			// the binary search
			while (first <= last) {
				int middle = (first + last) >> 1; // >>1 means to divide by 2

				int comparison = ArraysAlgos.sameAs(levelK_1.get(middle).getItems(), candidate, posRemoved);
				if (comparison < 0) {
					first = middle + 1; // the itemset compared is larger than the subset according to the lexical order
				} else if (comparison > 0) {
					last = middle - 1; // the itemset compared is smaller than the subset is smaller according to the
										// lexical order
				} else {
					found = true; // we have found it so we stop
					break;
				}
			}

			if (found == false) { // if we did not find it, that means that candidate is not a frequent itemset
									// because
				// at least one of its subsets does not appear in level k-1.
				return false;
			}
		}
		return true;
	}

	// ======================================
	// ======================================
	/**
	 * Method to save an itemset to the queue of top-n patterns
	 * 
	 * @param itemset an itemset
	 * @param support the support of the itemset
	 */
	private void saveItemsetToQueue(Itemset itemset, int support) {
		nItemsets.add(itemset);
		if (nItemsets.size() > n) {
			if (support > this.minsupRelative) {
				Itemset lower;
				do {
					lower = nItemsets.peek();
					if (lower == null) {
						break; // / IMPORTANT
					}
					nItemsets.remove(lower);
				} while (nItemsets.size() > n);
				this.minsupRelative = nItemsets.peek().getAbsoluteSupport();
//				System.out.println(nItemsets);
//				System.out.println(minsupRelative);
			}
		}
	}

	/**
	 * Save an itemset to file
	 * 
	 * @param itemset the itemset
	 * @throws IOException if an error occur for writing the file
	 */
	void saveItemsetToFile(Itemset itemset) throws IOException {
		itemsetCount++;

		// if the result should be saved to a file
		if (writer != null) {
			writer.write(itemset.toString() + " #SUP: " + itemset.getAbsoluteSupport());
			writer.newLine();
		} // otherwise the result is kept into memory
		else {
			patterns.addItemset(itemset, itemset.size());
		}
	}
	// ======================================
	// ======================================

	/**
	 * Print statistics about the algorithm execution to System.out.
	 */
	public void printStats() {
		System.out.println("=============  APRIORI(top-k) - STATS =============");
		System.out.println(" Candidates count : " + totalCandidateCount);
		System.out.println(" The algorithm stopped at size " + (k - 1));
		System.out.println(" Intermal minsup: " + minsupRelative);
		System.out.println(" Frequent itemsets count : " + nItemsets.size());
		System.out.println(" Maximum memory usage : " + MemoryLogger.getInstance().getMaxMemory() + " mb");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
		System.out.println("===================================================");
	}

	/**
	 * Set the maximum pattern length
	 * 
	 * @param length the maximum length
	 */
	public void setMaximumPatternLength(int length) {
		maxPatternLength = length;
	}
}
