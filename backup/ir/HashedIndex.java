/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 *   Additions: Hedvig Kjellström, 2012-14
 */  


package ir;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;


/**
 *   Implements an inverted index as a Hashtable from words to PostingsLists.
 */
public class HashedIndex implements Index {

	/** The index as a hashtable. */
	private HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();


	/**
	 *  Inserts this token in the index.
	 */
	public void insert( String token, int docID, int offset ) {
		//
		//  YOUR CODE HERE
		//
		// if already token is present, only docID has to appended to the token
		double score = 0;
		PostingsList postingsList = index.get(token);
		if(postingsList != null){
			//			System.out.println("not new token");
			
			//Should check whether docID is already present in the postingsList
			if(postingsList.isDocPresent(docID)){
				return;
			}
			else{
				postingsList.updateList(docID, score);
			}
		}
		else {
			postingsList = new PostingsList(docID,score);
		}

		index.put(token,postingsList);
		//		System.out.println("token: "+token+" list size: "+ index.get(token).size());
	}

	/**
	 * Sorts the indexs according to tokens 
	 * */
	public void sortDict(){
		Map<String, PostingsList> map = new TreeMap <String, PostingsList>(index);
		//		SortedSet<String> keys = new TreeSet<String>(index.keySet());
		//		index = map;
	}
	/**
	 *  Returns all the words in the index.
	 */
	public Iterator<String> getDictionary() {
		// 
		//  REPLACE THE STATEMENT BELOW WITH YOUR CODE
		//
		if (index.keySet() != null){
			return index.keySet().iterator();
		}
		return null;
	}


	/**
	 *  Returns the postings for a specific term, or null
	 *  if the term is not in the index.
	 */
	public PostingsList getPostings( String token ) {
		// 
		//  REPLACE THE STATEMENT BELOW WITH YOUR CODE
		//
		PostingsList postingsList = index.get(token);
		if(postingsList != null){
			System.out.println("token: "+token+" list size: "+ index.get(token).size());
			return postingsList;
		}
		return null;
	}


	/**
	 *  Searches the index for postings matching the query.
	 */
	public PostingsList search( Query query, int queryType, int rankingType, int structureType ) {
		// 
		//  REPLACE THE STATEMENT BELOW WITH YOUR CODE
		//
		//QueryType = 0 Intersection Query
		//QueryType = 1 Phrase Query
		//QueryType = 2 Ranked Retrieval
		System.out.println("QueryType: " + queryType  );
		switch(queryType){
		case 0: return intersectionSearch(query);

		default:return null;
		}
		//		return null;
	}

	public PostingsList intersectionSearch(Query query){
		// first implementing basic query search
		PostingsList finalList = null;
		for (int i =0;i<query.terms.size();i++){
			String token = query.terms.get(i);
			finalList = getPostings(token);
		}
		return finalList;
	}

	/**
	 *  No need for cleanup in a HashedIndex.
	 */
	public void cleanup() {
	}
}
