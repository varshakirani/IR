/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  

package ir;

import java.io.Serializable;
import java.util.LinkedList;

public class PostingsEntry implements Comparable<PostingsEntry>, Serializable {
    
    public int docID;
    public double score;
    public int frequency;

    /**
     *  PostingsEntries are compared by their score (only relevant 
     *  in ranked retrieval).
     *
     *  The comparison is defined so that entries will be put in 
     *  descending order.
     */
    
    public int compareTo( PostingsEntry other ) {
	return Double.compare( other.score, score );
    }

    //
    //  YOUR CODE HERE
    //
    private LinkedList<Integer> positionsList = new LinkedList<Integer>(); 
    public LinkedList<Integer> getPositionsList() {
		return positionsList;
	}
    public void addPosition(int offset){
    	this.positionsList.add(offset);
    }
    public void setPostingsEntry(int docID,double score){
    	this.docID = docID;
    	this.score = score;
    }
    public void setScore(double score){
    	this.score = score;
    }
    public void setdocID(int docId){
    	this.docID = docId;
    }
    public int getdocID(){
    	return this.docID;
    }
    public double getScore(){
    	return this.score;
    }
    
    public int getFrequency(){
    	return positionsList.size();    //PositionList maintains the offsets of a particular term in that document
    }
    
    public void setFrequency(int frq){
    	frequency = frq;
    }
    public void printVariables(){
    	System.out.println(score+' '+docID);
    }

}

    
