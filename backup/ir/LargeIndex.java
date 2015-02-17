package ir;
import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;

public class LargeIndex implements Index {

	public int tokenNo = 0;
	public int fileIndex = 0;
	private HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();
	private Map<String,String> mappings = new HashMap<String,String>();
	String homeDir = "/home/varsha/KTH courses/Search Engine And Information Retrieval/Assignments/LAB1/IR";
	/** Index as hashTable which stores inverted index on to files**/
	Map<String,String> tmpList = new HashMap<String,String>();
	File mapFile = new File(homeDir+"/SavedIndex/"+"mapFile.txt");
//	int FileCapacity = 0;
	int noOfCollection = 0;
	public void setNoOfCollection(int num){
		noOfCollection = num;
	}
	public int getNoOfCollection(){
		return noOfCollection;
	}
	public void insert(String token,int docID,int offset) {
		System.out.println("docId: "+docID+"token:" +token);
		if(tokenNo <1000){
			tmpList.put(token, ","+docID+","+offset);
//			System.out.println(tmpList.get(token));
			tokenNo++;
		}
		else{
//			System.out.println(tmpList.get("describe"));
			tokenNo = 0;
			Iterator it = tmpList.entrySet().iterator();
			File file = null;
//			Iterator<String> keysIt = tmpList.keySet().iterator();
//			while(keysIt.hasNext()){
//				System.out.println(keysIt.next());
//			}
			try{
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry)it.next();
//					String tmpToken = pairs.getKey().toString();
//					System.out.println(tmpToken);
					String tkn = (String)pairs.getKey();
					String tknVal = (String)pairs.getValue();
					String tokenFile = mappings.get(tkn);
					if(tokenFile != null){
						file = new File(homeDir+"/SavedIndex/"+tokenFile);
					}
					else if(tokenFile == null){
						file = new File(homeDir+"/SavedIndex/"+fileIndex+".txt");
						if(!file.exists()){
							file.createNewFile();
						}
						mappings.put(tkn,fileIndex+".txt");
					}
//					it.remove(); // avoids a ConcurrentModificationException
					PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(),true)));
					writer.println(tkn+tknVal);
					writer.close();
				}
				fileIndex++;

			}
			catch(IOException e){
				e.printStackTrace();
			}


			tmpList.clear();

			tmpList.put(token, ","+docID+","+offset);
		}}

	public void writeIntoMapping(){
		try{
			if(!mapFile.exists()){
				mapFile.createNewFile();
			}	
			Iterator it = mappings.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry pairs = (Map.Entry)it.next();
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(mapFile.getAbsolutePath(),true)));
				writer.println((String)pairs.getKey()+","+(String)pairs.getValue());
				writer.close();
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}

	}
	public void insert2(String token,int docID,int offset) {
		System.out.println("docId: "+docID);
		if(tokenNo < 1000){
			String tokenFile = mappings.get(token);
			File file = null;
			try{
				if(tokenFile != null){
					file = new File(homeDir+"/SavedIndex/"+tokenFile);
				}
				else if(tokenFile == null){
					file = new File(homeDir+"/SavedIndex/"+fileIndex+".txt");
					if(!file.exists()){
						file.createNewFile();
					}
					mappings.put(token, fileIndex+".txt");
				}
				//				FileWriter fw = new FileWriter(file.getAbsolutePath(),true);
				//				BufferedWriter bufferedWriter = new BufferedWriter(fw);
				//				PrintWriter writer = new PrintWriter(bufferedWriter);
				// 
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(),true)));
				writer.println(token + ","+docID+","+offset);
				//				fw.close();
				writer.close();

			}
			catch(IOException e){
				e.printStackTrace();
			}



			tokenNo++;
		}
		else{
			tokenNo = 0;
			fileIndex++;
			insert(token, docID, offset);
		}
	}
	public void insert1(String token, int docID, int offset) {
		// TODO Auto-generated method stub
		System.out.println("inside insert method in class largeIndex" +"docId: "+docID);

		if(tokenNo < 1000)
		{
			//			String tokenFile = getFile(token);
			String tokenFile = mappings.get(token);
			try{
				File file = null;
				if(tokenFile != null)
				{
					file = new File(tokenFile);
				}
				if(file == null){
					file = new File(homeDir+"/SavedIndex/"+fileIndex+".txt");
					if(!file.exists()){
						file.createNewFile();
					}
				}
				if(tokenFile == null)
				{
					File mapFile = new File(homeDir+"/SavedIndex/"+"mapFile.txt");
					if(!mapFile.exists()){
						mapFile.createNewFile();
					}
					FileWriter mapfw = new FileWriter(mapFile.getAbsolutePath(),true);
					BufferedWriter bufferedWriter = new BufferedWriter(mapfw);
					PrintWriter writer = new PrintWriter(bufferedWriter);

					writer.println(token +","+fileIndex+".txt");
					mapfw.close();
					writer.close();
				}
				FileWriter fw = new FileWriter(file.getAbsolutePath(),true);
				BufferedWriter bufferedWriter = new BufferedWriter(fw);
				PrintWriter writer = new PrintWriter(bufferedWriter);

				writer.println(token + ","+docID+","+offset);
				fw.close();
				writer.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
			tokenNo++;
		}

		else {
			tokenNo =0;
			fileIndex++;
			insert(token, docID, offset);
		}
	}

	public String getFile(String token){
		String returnFilePath = null;
		File mapFile = new File(homeDir+"/SavedIndex/"+"mapFile.txt");
		try{
			BufferedReader br = new BufferedReader(new FileReader(mapFile));
			String line;
			while((line = br.readLine()) != null ){
				if(line.contains(token)){
					String[] parts = line.split(",");
					if(parts[0].equals(token)){
						returnFilePath = homeDir+"/SavedIndex/"+parts[1];
						return returnFilePath;	
					}
					
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return returnFilePath;

	}
	@Override
	public void sortDict() {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<String> getDictionary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PostingsList getPostings(String token) {
		PostingsList postingsList = index.get(token);
		if(postingsList != null){
			//			System.out.println("token: "+token+" list size: "+ index.get(token).size());
//			System.out.println("for token: "+token);
			postingsList.printDocID();
			return postingsList;
		}
		return null;
	}

	public void textToPostingsList(String token){
		try{
			if(!mapFile.exists()){
				System.out.println("Wrong run");
				return;
			}
			else{
				String fN = null;
				fN = getFile(token);
				File fileName = null;
				if(fN != null)
				{
					fileName = new File(fN);
				}
				try{
					if(fileName.exists())
					{FileReader fr = new FileReader(fileName);
					BufferedReader br = new BufferedReader(fr);
					String line;
					
					int docID = 0;
					int offset = 0;
					
					while((line = br.readLine()) != null ){
						boolean addEntry = true;
						if(line.contains(token)){
							String[] parts = line.split(",");
							if(parts[0].equals(token)){
								PostingsList postingsList = index.get(token);
								docID = Integer.parseInt(parts[1]);
								offset = Integer.parseInt(parts[2]);
								if(postingsList != null){
									for(PostingsEntry entry:postingsList.getPostingsEntry()){
										if(entry.getdocID() == docID){
											entry.addPosition(offset);
											addEntry = false;
											break;
										}
									}
									if(addEntry){
										postingsList.updateList(docID, 0,offset);
									}
								}
								else {
									postingsList = new PostingsList(docID, 0,offset);
									index.put(token,postingsList);
								}
										
							}
						
						}
					}
					}
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
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
	@Override
	public PostingsList search(Query query, int queryType, int rankingType,
			int structureType) {
		// TODO Auto-generated method stub
		Query sortedQuery = query;
		LinkedList<String> terms = sortedQuery.terms;
		for(String term:terms){
			if(index.get(term) == null)
			{
				textToPostingsList(term);
			}
		}
		if(queryType == 0){
			sortedQuery = sortByIncreasedFrequency(query);
		}
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
				
				PostingsList p2 = getPostings(sortedQuery.terms.poll());
				result = phraseIntersect(result,p2);
			}
			terms = sortedQuery.terms;
			if(terms != null){
				termSize = terms.size();
			} else termSize = 0;
			if(result !=null){
				resultSize = result.size();
			} else resultSize = 0;
		}

		System.out.println("Inside search method using disk storage");
		return result;
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public int indexSize() {
		// TODO Auto-generated method stub
		return 0;
	}

}
