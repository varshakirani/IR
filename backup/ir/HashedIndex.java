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
import java.util.LinkedList;
import java.util.ListIterator;
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
			//			System.out.println("token: "+token+" list size: "+ index.get(token).size());
			System.out.println("for token: "+token);
			postingsList.printDocID();
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
	}

	public PostingsList intersectionSearch(Query query){
		System.out.println("inside intersection function");
		Query sortedQuery = sortByIncreasedFrequency(query);
		LinkedList<String> terms = sortedQuery.terms;
		PostingsList result = getPostings(sortedQuery.terms.poll());
		terms = sortedQuery.terms;
		int termSize = 0;
		int resultSize = 0;
		if(terms != null){
			termSize = terms.size();
		}
		if(result !=null){
			resultSize = result.size();
		}
		while(termSize != 0 && resultSize != 0){
			result = intersect(result,getPostings(sortedQuery.terms.poll()));
			terms = sortedQuery.terms;
		}
		return result;
	}

	public PostingsList intersect(PostingsList p1, PostingsList p2){
		PostingsList answer = new PostingsList();
		LinkedList<PostingsEntry> p1List = p1.getPostingsEntry();
		LinkedList<PostingsEntry> p2List = p2.getPostingsEntry();
		ListIterator<PostingsEntry> p1It = p1List.listIterator();
		ListIterator<PostingsEntry> p2It = p2List.listIterator();
		
		
		while(p1It.hasNext()  && p2It.hasNext()){
		int p1docID = p1It.next().getdocID();
		int p2docID = p2It.next().getdocID();
		p1It.previous();
		p2It.previous();
			if(p1docID == p2docID ){
				answer.addPostingEntry(p1docID);
				p1It.next();
				p2It.next();
			}
			else if(p1docID < p2docID){
				p1It.next();
			}
			else {
				p2It.next();
			}

		}
		if(answer.size() != 0){
			return answer;	
		}
		
		return null;
	}
    public Query sortByIncreasedFrequency(Query query){
    	Query sortedQuery = query.copy();
    	int n = query.terms.size();
    	//doing bubble sort
    	boolean swapped = true;
    	while(swapped == true){
    		swapped = false;
    		for(int i=1;i<n;i++){
    			System.out.println("Before Swap, I-1 token: "+sortedQuery.terms.get(i-1)+" "+"I token: "+sortedQuery.terms.get(i));
    			PostingsList i_1term = index.get(sortedQuery.terms.get(i-1));
    			PostingsList iterm = index.get(sortedQuery.terms.get(i));
    			int i_1termSize = 0;
    			int itermSize = 0;
    			if(i_1term != null){
    				 i_1termSize = index.get(sortedQuery.terms.get(i-1)).size();
    			}
    			
    			if(iterm != null)
    			{
    				itermSize = index.get(sortedQuery.terms.get(i)).size();
    			}
    			
    			if(i_1termSize > itermSize ){
    				//swap i-1 and i
    				String tmpToken = sortedQuery.terms.get(i-1);
    				Double tmpWeight = sortedQuery.weights.get(i-1);
    				sortedQuery.terms.add(i-1,sortedQuery.terms.get(i));
    				sortedQuery.weights.add(i-1,sortedQuery.weights.get(i));
    				sortedQuery.terms.add(i,tmpToken);
    				sortedQuery.weights.add(i,tmpWeight);
    				System.out.println("I-1 token: "+sortedQuery.terms.get(i-1)+" "+"I token: "+sortedQuery.terms.get(i));
    				swapped = true;
    			}
    		}
    	}
    	return sortedQuery;
    }
	/**
	 *  No need for cleanup in a HashedIndex.
	 */
	public void cleanup() {
	}
}
