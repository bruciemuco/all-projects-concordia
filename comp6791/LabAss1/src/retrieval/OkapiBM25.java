/*
 * COMP6791 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
 * 
 */

package retrieval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import utils.ByteArrayWrapper;

public class OkapiBM25 {
	// docId, the length of the doc
	public static HashMap<Long, Long> mapLenOfDocs = new HashMap<Long, Long>();

	// average length of all docs
	public static double avgDocLen = 0; 


	// calculate the average length of all docs
	public static void calcAvgDocLen() {
		double sum = 0.0;

		if (mapLenOfDocs.size() < 1) {
			return;
		}
		
		for (Long v : mapLenOfDocs.values()) {
			sum += v.longValue();
		}		
		avgDocLen = sum / mapLenOfDocs.size();
	}
	
	// map: <term, map<docID, tf>>
	public static HashMap<ByteArrayWrapper, HashMap<Long, Long>> mapTF = 
			new HashMap<ByteArrayWrapper, HashMap<Long, Long>>();
	
	// store all tf in memory
	public static int calcTF(String tk, long docID) {
		ByteArrayWrapper term = new ByteArrayWrapper(tk.getBytes());
		HashMap<Long, Long> mapTFDocs = mapTF.get(term);
		
		if (mapTFDocs == null) {
			mapTFDocs = new HashMap<Long, Long>();
			mapTFDocs.put(docID, (long) 1);
			mapTF.put(term, mapTFDocs);
			return 0;
		}
		
		Long tf = mapTFDocs.get(docID);
		if (tf == null) {
			mapTFDocs.put(docID, (long) 1);
		} else {
			mapTFDocs.put(docID, tf.longValue() + 1);
		}
		
		return 0;
	}
	
	private static long getTF(String tk, long docID) {
		ByteArrayWrapper term = new ByteArrayWrapper(tk.getBytes());
		HashMap<Long, Long> mapTFDocs = mapTF.get(term);
		
		if (mapTFDocs == null) {
			return 0;
		}
		
		Long tf = mapTFDocs.get(docID);
		if (tf == null) {
			return 0;
		}
		return tf.longValue();
	}

	public static double getScore(HashMap<String, Integer> termDocFreq, long docID) {
		double ret = 0;
		long N = mapLenOfDocs.size();
		long docLen = mapLenOfDocs.get(docID);
		double k = 1.5;
		double b = 0.75;
		
		for (String term : termDocFreq.keySet()) {
			int docFreq = termDocFreq.get(term);
			double idf = Math.log((N - docFreq + 0.5) / (docFreq + 0.5));
			long tf = getTF(term, docID);
			
			ret += idf * (tf * (k + 1)) / (tf + k * (1 - b + b * (docLen / avgDocLen)));
//			System.out.println(ret + ":" + N + "," + docFreq + "," 
//					+ docLen + "," + idf + "," + tf);
		}
		
		
		return ret;
	}
	
	public static HashMap<Long, Double> getScoredDocIDs(HashMap<String, Integer> termDocFreq, 
			ArrayList<Long> docIDs) {
		HashMap<Long, Double> mapScoredDocIDs = new HashMap<Long, Double>();
        
		for (Long docID : docIDs) {
			double score = getScore(termDocFreq, docID.longValue());
			mapScoredDocIDs.put(docID, score);
		}
		
		return (HashMap<Long, Double>) sortByValue(mapScoredDocIDs);
	}

	// the following code is copied from:
	// http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
