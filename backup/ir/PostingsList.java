/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  

package ir;

import java.util.LinkedList;
import java.io.Serializable;

/**
 *   A list of postings for a given word.
 */
public class PostingsList implements Serializable {

	/** The postings list as a linked list. */
	private LinkedList<PostingsEntry> list = new LinkedList<PostingsEntry>();
	
	//Copy Constructor//
	public PostingsList(PostingsList PosList){
		this.list = new LinkedList<PostingsEntry>(PosList.getPostingsEntry());
	
	}
	
	
	//	private double score = 0;
	
	
	
	/**  Number of postings in this list  */
	public int size() {
		return list.size();
		
	}

	public int getDocumentFrequency(){
		return list.size();
	}
	/**  Returns the ith posting */
	public PostingsEntry get( int i ) {
		return list.get( i );
	}
	
	//
	//  YOUR CODE HERE
	//

	public LinkedList<PostingsEntry> getPostingsEntry(){
		return list;
	}

	
	

	//constructor of postingslist... to pass docID and score to postingsEntry

	public void updateList(int docID, double score,int offset){
		PostingsEntry posEntry = new PostingsEntry();
		posEntry.setPostingsEntry(docID,score);	
//		System.out.println("from Entries: docID=" + posEntry.getdocID()+" score= "+posEntry.getScore());
		posEntry.addPosition(offset);
		list.add(posEntry);
	}
	public PostingsList(){
		//do nothing
	}
	public PostingsList(int docID, double score, int offset){

		updateList(docID,score,offset);

	}

	//    // these values are used as temp.. to add it into postingsEntry
	//    private int docID;
	//    private double score;
	//    
	public boolean isDocPresent(int docID,int offset){
		for(PostingsEntry entry: list){
			if(docID == entry.getdocID()){

				//increase the score (number of hits) 
				//TO DO Make this hits according to docIDs... now it is global  --- check whether it is correct

//				double score = entry.getScore();
//				score++;
//				entry.setScore(score);
				entry.addPosition(offset);
				
				return true;
			}
		}
		return false;
	}
	
	
	
	public void printDocID(){
		for(PostingsEntry entry: list){
			System.out.print(entry.getdocID() + " ");
		}
		System.out.println("");
	}
	
	public void addPostingEntry(int docID, int offset){
		PostingsEntry entry = new PostingsEntry();
		// TODO update score as well
		entry.setdocID(docID);
		entry.addPosition(offset);
		list.add(entry);
	}
	
	
	

}



