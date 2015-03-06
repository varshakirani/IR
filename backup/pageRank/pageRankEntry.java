package pageRank;

import ir.PostingsEntry;

import java.io.Serializable;

public class pageRankEntry implements Comparable<pageRankEntry>, Serializable{

	public int docID;
	public double pageRank;
	
	public void setParameters(int docID,double pageRank){
		this.docID = docID;
		this.pageRank = pageRank;
	}
	
	public int getdocID(){
		return docID;
	}
	public double getpageRank(){
		return pageRank;
	}

	@Override
	public int compareTo(pageRankEntry other) {
		// TODO Auto-generated method stub
		return Double.compare( other.pageRank, pageRank );
		
	}
}
