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
		// if already token is present, only docID has to appended to the token
//		double score = 0;
		PostingsList postingsList = index.get(token);
		if(postingsList != null){
			//Should check whether docID is already present in the postingsList, 
			//if it is present then update the positionList  in the postingsEntry
			PostingsEntry entry = postingsList.getPostingsEntry().getLast();
			if(entry.getdocID() == docID){
				entry.addPosition(offset);
				return;
			}
//			if(postingsList.isDocPresent(docID,offset)){
//				return;
//			}
			else{
				postingsList.updateList(docID, 0,offset);
				
			}
		}
		else {
			postingsList = new PostingsList(docID,0,offset);
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
//			System.out.println("for token: "+token);
			postingsList.printDocID();
			return postingsList;
		}
		return null;
	}


	/**
	 *  Searches the index for postings matching the query.
	 */
	public PostingsList search( Query query, int queryType, int rankingType, int structureType ) {
		//QueryType = 0 Intersection Query
		//QueryType = 1 Phrase Query
		//QueryType = 2 Ranked Retrieval

		Query sortedQuery = query;
		if(queryType == 0){
			sortedQuery = sortByIncreasedFrequency(query);
		}
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
			System.out.print("sortedQuery: ");
			//printing the query list
			int sortedQuerySize = 0;
			if(sortedQuery != null){
				sortedQuerySize = sortedQuery.size();
			}
			for(int j =0;j<sortedQuerySize;j++)
			{
				System.out.print(" " + sortedQuery.terms.get(j));
			}
			
			// calling intersection function for different querytypes
			if(queryType == 0){
				result = intersect(result,getPostings(sortedQuery.terms.poll()));	
			}
			else if(queryType == 1){
				result = phraseIntersect(result,getPostings(sortedQuery.terms.poll()));
			}
			terms = sortedQuery.terms;
			if(terms != null){
				termSize = terms.size();
			} else termSize = 0;
			if(result !=null){
				resultSize = result.size();
			} else resultSize = 0;
		}

		
		return result;
//		
//		switch(queryType){
//		case 0: return intersectionSearch(query);
//		case 1: return phraseSearch(query);
//		default:return null;
//		}
	}
	public PostingsList phraseSearch(Query query){
		System.out.println("Inside phraseSearch function");
		PostingsList result = null;
		LinkedList<String> terms = query.terms;
		PostingsList p1 = getPostings(terms.get(0));
		PostingsList p2 = getPostings(terms.get(1));
		
		
		result = phraseIntersect(p1,p2);
		return result;
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
			System.out.print("sortedQuery: ");
			//printing the query list
			int sortedQuerySize = 0;
			if(sortedQuery != null){
				sortedQuerySize = sortedQuery.size();
			}
			for(int j =0;j<sortedQuerySize;j++)
			{
				System.out.print(" " + sortedQuery.terms.get(j));
			}
			result = intersect(result,getPostings(sortedQuery.terms.poll()));
			terms = sortedQuery.terms;
			if(terms != null){
				termSize = terms.size();
			} else termSize = 0;
			if(result !=null){
				resultSize = result.size();
			} else resultSize = 0;
		}
		return result;
	}

	public PostingsList phraseIntersect(PostingsList p1,PostingsList p2){
		PostingsList answer = new PostingsList();
		System.out.println(p1.getPostingsEntry());
		System.out.println(p2.getPostingsEntry());
		
		ListIterator<PostingsEntry> p1It = p1.getPostingsEntry().listIterator();
		ListIterator<PostingsEntry> p2It = p2.getPostingsEntry().listIterator();
		
		while(p1It.hasNext() && p2It.hasNext()){
			int p1docID = p1It.next().getdocID();
			int p2docID = p2It.next().getdocID();
			p1It.previous();
			p2It.previous();
			if(p1docID == p2docID){
				//should check whether 2 words occur one after the other. 
				//If so, then add postion of second word in the answer
				
				ListIterator<Integer> posList1 = p1It.next().getPositionsList().listIterator();
				ListIterator<Integer> posList2 = p2It.next().getPositionsList().listIterator();
				p1It.previous();
				p2It.previous();
				
				System.out.println("\n\nDocID: "+p2docID+"\nFirst word positions:");
				while(posList1.hasNext()){
					System.out.print(posList1.next() + " ");
				}
				posList1 = p1It.next().getPositionsList().listIterator();
				System.out.println("\nSecond word positions:");
				while(posList2.hasNext()){
					System.out.print(posList2.next() + " ");
				}
				posList2 = p2It.next().getPositionsList().listIterator();
				p1It.previous();
				p2It.previous();
				while(posList1.hasNext()){
					int posP1 = posList1.next();
					posList1.previous();
					posList2 = p2It.next().getPositionsList().listIterator();
					p2It.previous();
					while(posList2.hasNext()){
						int posP2 = posList2.next();
						
						if(posP2 == posP1+1){
							if(answer.isDocPresent(p2docID, posP2)){
								continue;
							}
							else{
								answer.updateList(p2docID, 0, posP2);
							}
						}
					}
					posList1.next();
				}
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
		
		return answer;
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
				answer.addPostingEntry(p1docID,0);
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
    			System.out.println("i = " + i);
    			//printing the query list
    			for(int j =0;j<sortedQuery.size();j++)
    			{
    				System.out.print(" " + sortedQuery.terms.get(j));
    			}
    			
    			System.out.println("\n Before Swap, I-1 token: "+sortedQuery.terms.get(i-1)+" "+"I token: "+sortedQuery.terms.get(i));
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
    				String ith_tmpToken = sortedQuery.terms.get(i);
    				Double ith_tmpWeight = sortedQuery.weights.get(i);
    				sortedQuery.terms.set(i-1,ith_tmpToken);
    				sortedQuery.weights.set(i-1,ith_tmpWeight);
    				sortedQuery.terms.set(i,tmpToken);
    				sortedQuery.weights.set(i,tmpWeight);
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
