/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2012
 */  
package pageRank;
import ir.PostingsEntry;

import java.util.*;
import java.io.*;

public class PageRank{

	/**  
	 *   Maximal number of documents. We're assuming here that we
	 *   don't have more docs than we can keep in main memory.
	 */
	final static int MAX_NUMBER_OF_DOCS = 2000000;

	/**
	 *   Mapping from document names to document numbers.
	 */
	Hashtable<String,Integer> docNumber = new Hashtable<String,Integer>();

	/**
	 *   Mapping from document numbers to document names
	 */
	String[] docName = new String[MAX_NUMBER_OF_DOCS];

	/**  
	 *   A memory-efficient representation of the transition matrix.
	 *   The outlinks are represented as a Hashtable, whose keys are 
	 *   the numbers of the documents linked from.<p>
	 *
	 *   The value corresponding to key i is a Hashtable whose keys are 
	 *   all the numbers of documents j that i links to.<p>
	 *
	 *   If there are no outlinks from i, then the value corresponding 
	 *   key i is null.
	 */
	Hashtable<Integer,Hashtable<Integer,Boolean>> link = new Hashtable<Integer,Hashtable<Integer,Boolean>>();

	/**
	 *   The number of outlinks from each node.
	 */
	int[] out = new int[MAX_NUMBER_OF_DOCS];

	/**
	 *   The number of documents with no outlinks.
	 */
	int numberOfSinks = 0;

	/**
	 *   The probability that the surfer will be bored, stop
	 *   following links, and take a random jump somewhere.
	 */
	final static double BORED = 0.15;

	/**
	 *   Convergence criterion: Transition probabilities do not 
	 *   change more that EPSILON from one iteration to another.
	 */
	final static double EPSILON = 0.0001;

	/**
	 *   Never do more than this number of iterations regardless
	 *   of whether the transistion probabilities converge or not.
	 */
	final static int MAX_NUMBER_OF_ITERATIONS = 1000;


	/* --------------------------------------------- */


	public PageRank( String filename ) {

		int noOfDocs = readDocs( filename );
//				computePagerank( noOfDocs );
//				computeMC1(noOfDocs,noOfDocs * 200);
//				computeMC2(noOfDocs, 200);
//				computeMC3(noOfDocs, 20, 100);
//		computeMC4(noOfDocs, 200);
		computeMC5(noOfDocs, 200);
	}


	/* --------------------------------------------- */


	/**
	 *   Reads the documents and creates the docs table. When this method 
	 *   finishes executing then the @code{out} vector of outlinks is 
	 *   initialised for each doc, and the @code{p} matrix is filled with
	 *   zeroes (that indicate direct links) and NO_LINK (if there is no
	 *   direct link. <p>
	 *
	 *   @return the number of documents read.
	 */
	int readDocs( String filename ) {
		int fileIndex = 0;
		try {
			System.err.print( "Reading file... " );
			BufferedReader in = new BufferedReader( new FileReader( filename ));
			String line;
			while ((line = in.readLine()) != null && fileIndex<MAX_NUMBER_OF_DOCS ) {
				int index = line.indexOf( ";" );
				String title = line.substring( 0, index );
				Integer fromdoc = docNumber.get( title );
				//  Have we seen this document before?
				if ( fromdoc == null ) {	
					// This is a previously unseen doc, so add it to the table.
					fromdoc = fileIndex++;
					docNumber.put( title, fromdoc );
					docName[fromdoc] = title;
				}
				// Check all outlinks.
				StringTokenizer tok = new StringTokenizer( line.substring(index+1), "," );
				while ( tok.hasMoreTokens() && fileIndex<MAX_NUMBER_OF_DOCS ) {
					String otherTitle = tok.nextToken();
					Integer otherDoc = docNumber.get( otherTitle );
					if ( otherDoc == null ) {
						// This is a previousy unseen doc, so add it to the table.
						otherDoc = fileIndex++;
						docNumber.put( otherTitle, otherDoc );
						docName[otherDoc] = otherTitle;
					}
					// Set the probability to 0 for now, to indicate that there is
					// a link from fromdoc to otherDoc.
					if ( link.get(fromdoc) == null ) {
						link.put(fromdoc, new Hashtable<Integer,Boolean>());
					}
					if ( link.get(fromdoc).get(otherDoc) == null ) {
						link.get(fromdoc).put( otherDoc, true );
						out[fromdoc]++;
					}
				}
			}
			if ( fileIndex >= MAX_NUMBER_OF_DOCS ) {
				System.err.print( "stopped reading since documents table is full. " );
			}
			else {
				System.err.print( "done. " );
			}
			// Compute the number of sinks.
			for ( int i=0; i<fileIndex; i++ ) {
				if ( out[i] == 0 )
					numberOfSinks++;
			}
		}
		catch ( FileNotFoundException e ) {
			System.err.println( "File " + filename + " not found!" );
		}
		catch ( IOException e ) {
			System.err.println( "Error reading file " + filename );
		}
		System.err.println( "Read " + fileIndex + " number of documents" );
		return fileIndex;
	}


	/* --------------------------------------------- */


	boolean isBored(){
		Random rand = new Random();
		double boredProb = rand.nextDouble();
		if(boredProb < BORED){
			return true;
		}
		else{
			return false;
		}
	}

	int selectNextPage(Set<Integer> possiblePages,int numberOfDocs){
		int size = possiblePages.size();
		int item = new Random().nextInt(size);
		Object[] pagesArr = possiblePages.toArray();
		return (Integer) pagesArr[item];
	}

	void printSortedPageRankDesc(double[] pageVisited,int numberOfDocs,int bottomK){
		final double[] ranked = new double[numberOfDocs];
		Integer[] indices = new Integer[numberOfDocs];
		for (int i = 0; i < numberOfDocs; i++) {
			ranked[i] = pageVisited[i];
			indices[i] = i;
		}
		Arrays.sort(indices, new Comparator<Integer>() {
			@Override
			public int compare(final Integer o1, final Integer o2) {
				return Double.compare(ranked[o1], ranked[o2]);
			}
		});
		for (int i = 0; i < bottomK; i++) {
			int idx = indices[indices.length - i - 1];
			System.out.println((i + 1) + ": " + docName[idx] + " "
					+ ranked[idx]);
		}
	}
	void printSortedPageRank(double[] pageVisited,int numberOfDocs,int topK){
		final double[] ranked = new double[numberOfDocs];
		Integer[] indices = new Integer[numberOfDocs];
		for (int i = 0; i < numberOfDocs; i++) {
			ranked[i] = pageVisited[i];
			indices[i] = i;
		}
		Arrays.sort(indices, new Comparator<Integer>() {
			@Override
			public int compare(final Integer o1, final Integer o2) {
				return Double.compare(ranked[o1], ranked[o2]);
			}
		});
		for (int i = 0; i < topK; i++) {
			int idx = indices[indices.length - i - 1];
			System.out.println((i + 1) + ": " + docName[idx] + " "
					+ ranked[idx]);
		}
	}
	void computeMC5(int numberOfDocs,int m){
		double[] pageVisited = new double[numberOfDocs];

		int N = m * numberOfDocs;
		
		int totalNumberOfVisits = 0;
		for(int t = 0;t<N;t++){
			Random rand = new Random();
			int start = rand.nextInt(((numberOfDocs-1) - 0) + 1);
			int i = start;
			boolean danglingNode = false;
			boolean boredFlag = false;
			while(!danglingNode && !boredFlag){
				//				for(int l = 0;l<lengthT;l++){
				totalNumberOfVisits += 1;
				int next;
				if(out[i] == 0){
					pageVisited[i] += 1;
					pageVisited[i] += pageVisited[i] / (totalNumberOfVisits);
					next = rand.nextInt(((numberOfDocs-1) - 0) + 1);
					danglingNode = true;
				}
				else{
					pageVisited[i] += 1;
					pageVisited[i] += pageVisited[i] / (totalNumberOfVisits);
					Set possiblePages = link.get(i).keySet();
					next = selectNextPage(possiblePages,numberOfDocs);
				}
				boredFlag = isBored();
				i = next;
			}
		}

		for(int i=0;i<numberOfDocs;i++){
			pageVisited[i] = pageVisited[i]/N;
		}
		printSortedPageRank(pageVisited, numberOfDocs, 50);
	}
	void computeMC4(int numberOfDocs,int m){
		double[] pageVisited = new double[numberOfDocs];

		int N = m * numberOfDocs;
		Random rand = new Random();
		int start = rand.nextInt(((numberOfDocs-1) - 0) + 1);
		int totalNumberOfVisits = 0;
		for(int t = 0;t<numberOfDocs;t++){
			start = t;
			for(int v = 0;v<m;v++){
				int i = start;
				boolean danglingNode = false;
				boolean boredFlag = false;
				while(!danglingNode && !boredFlag){
					//				for(int l = 0;l<lengthT;l++){
					totalNumberOfVisits += 1;
					int next;
					if(out[i] == 0){
						pageVisited[i] += 1;
						pageVisited[i] += pageVisited[i] / (totalNumberOfVisits);
						next = rand.nextInt(((numberOfDocs-1) - 0) + 1);
						danglingNode = true;
					}
					else{
						pageVisited[i] += 1;
						pageVisited[i] += pageVisited[i] / (totalNumberOfVisits);
						Set possiblePages = link.get(i).keySet();
						next = selectNextPage(possiblePages,numberOfDocs);
					}
					boredFlag = isBored();
					i = next;
				}
			}	
		}

		for(int i=0;i<numberOfDocs;i++){
			pageVisited[i] = pageVisited[i]/N;
		}
		printSortedPageRank(pageVisited, numberOfDocs, 50);

	}
	void computeMC3(int numberOfDocs,int m,int lengthT){
		double[] pageVisited = new double[numberOfDocs];

		int N = m * numberOfDocs;
		Random rand = new Random();
		int start = rand.nextInt(((numberOfDocs-1) - 0) + 1);
		int totalNumberOfVisits = 0;
		for(int t = 0;t<numberOfDocs;t++){
			start = t;
			for(int v = 0;v<m;v++){
				int i = start;

				for(int l = 0;l<lengthT;l++){
					totalNumberOfVisits += 1;
					int next;
					if(out[i] == 0){
						pageVisited[i] += 1;
						pageVisited[i] += pageVisited[i] / (totalNumberOfVisits);
						next = rand.nextInt(((numberOfDocs-1) - 0) + 1);
					}
					else{
						pageVisited[i] += 1;
						pageVisited[i] += pageVisited[i] / (totalNumberOfVisits);
						Set possiblePages = link.get(i).keySet();
						next = selectNextPage(possiblePages,numberOfDocs);
					}

					i = next;
				}
			}	
		}

		for(int i=0;i<numberOfDocs;i++){
			pageVisited[i] = pageVisited[i]/N;
		}
		printSortedPageRank(pageVisited, numberOfDocs, 50);

	}
	void computeMC2(int numberOfDocs,int m){
		double[] pageVisited = new double[numberOfDocs];

		int N = m * numberOfDocs;
		int mCount = 0;
		Random rand = new Random();
		int start = rand.nextInt(((numberOfDocs-1) - 0) + 1);
		for(int t = 0;t<numberOfDocs;t++){
			start = t;
			for(int v = 0;v<m;v++){
				int i = start;
				boolean boredFlag = false;
				while(!boredFlag){
					int next;
					if(out[i] == 0){
						next = rand.nextInt(((numberOfDocs-1) - 0) + 1);
					}
					else{
						Set possiblePages = link.get(i).keySet();
						next = selectNextPage(possiblePages,numberOfDocs);
					}

					boredFlag = isBored();
					if(boredFlag){
						pageVisited[i] += 1;
					}
					i = next;
				}
			}	
		}

		for(int i=0;i<numberOfDocs;i++){
			pageVisited[i] = pageVisited[i]/N;
		}
		printSortedPageRank(pageVisited, numberOfDocs, 50);
	}
	void computeMC1(int numberOfDocs ,int N){
		double[] pageVisited = new double[numberOfDocs];
		for(int t = 0;t<N;t++){
			//simulation		
			Random rand = new Random();
			int start = rand.nextInt(((numberOfDocs-1) - 0) + 1);
			int i = start;
			boolean boredFlag = false;
			while(!boredFlag){
				int next;
				if(out[i] == 0){
					next = rand.nextInt(((numberOfDocs-1) - 0) + 1);
				}
				else{
					Set possiblePages = link.get(i).keySet();
					next = selectNextPage(possiblePages,numberOfDocs);
				}

				boredFlag = isBored();
				if(boredFlag){
					pageVisited[i] += 1;
				}
				i = next;
			}

		}

		for(int i=0;i<numberOfDocs;i++){
			pageVisited[i] = pageVisited[i]/N;
		}
		final double[] ranked = new double[numberOfDocs];
		Integer[] indices = new Integer[numberOfDocs];
		for (int i = 0; i < numberOfDocs; i++) {
			ranked[i] = pageVisited[i];
			indices[i] = i;
		}
		Arrays.sort(indices, new Comparator<Integer>() {
			@Override
			public int compare(final Integer o1, final Integer o2) {
				return Double.compare(ranked[o1], ranked[o2]);
			}
		});
		for (int i = 0; i < 50; i++) {
			int idx = indices[indices.length - i - 1];
			System.out.println((i + 1) + ": " + docName[idx] + " "
					+ ranked[idx]);
		}

	}
	/*
	 *   Computes the pagerank of each document.
	 */
	void computePagerank( int numberOfDocs ) {
		//
		//   YOUR CODE HERE
		//
		System.out.println("Testing the run");

		System.out.println(numberOfDocs);

		double[] pi = new double[numberOfDocs];
		double[] x = new double[numberOfDocs ];
		pi[0] = 1.0;
		double P =0;

		/** Power Iteration **/
		int index = 1;
		double dist = 1;
		while (dist > EPSILON){
			System.out.println("Iteration: "+ index++);
			//Matrix Multiplication
			x = pi.clone();
			double[] res = new double[numberOfDocs ];
			for(int j=0;j<numberOfDocs;j++){

				for(int i = 0;i<numberOfDocs;i++){
					if(out[i] == 0){
						res[j] += x[i]*(1/(double)(numberOfDocs));
					}

					else {
						if(link.get(i).containsKey(j)){
							res[j] += x[i] *((BORED/(double)numberOfDocs) + (1-BORED)/(double)out[i]);
						}
						else{
							res[j] += x[i] *(BORED/(double)numberOfDocs);
						}
					}
				}
			}
			pi = res.clone();
			dist = manhattanDistance(x, pi, numberOfDocs);
		}


		final double[] ranked = new double[numberOfDocs];
		Integer[] indices = new Integer[numberOfDocs];
		for (int i = 0; i < numberOfDocs; i++) {
			ranked[i] = pi[i];
			indices[i] = i;
		}
		Arrays.sort(indices, new Comparator<Integer>() {
			@Override
			public int compare(final Integer o1, final Integer o2) {
				return Double.compare(ranked[o1], ranked[o2]);
			}
		});
		for (int i = 0; i < 50; i++) {
			int idx = indices[indices.length - i - 1];
			System.out.println((i + 1) + ": " + docName[idx] + " "
					+ ranked[idx]);
		}



		String homeDir = "/home/varsha/KTH courses/Search Engine And Information Retrieval/Assignments/LAB1/IR/src/pagerank";
		try{
			File file= new File(homeDir+"sortedPageRank.txt");
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(),true)));
			File file1 = new File(homeDir+"pageRankCompleteList1.txt");
			PrintWriter writer1 = new PrintWriter(new BufferedWriter(new FileWriter(file1.getAbsolutePath(),true)));
			for (int i = 0; i < 50; i++) {
				int idx = indices[indices.length - i - 1];
				System.out.println((i + 1) + ": " + docName[idx] + " "
						+ ranked[idx]);
				writer.println((i + 1) + ": " + docName[idx] + " "
						+ ranked[idx]);
			}
			writer.close();
			
			
			//for combination of assignment
			for(int i =0;i<numberOfDocs;i++){
				int idx = indices[indices.length - i - 1];
				writer1.println( docName[i] + ":"	+ pi[i]);
			}
			writer1.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}


	}



	public double manhattanDistance(double[] x1,double[] x2,int numOfDocs){
		double res = 0.0;
		for(int i=0;i<numOfDocs;i++){
			res += Math.abs(x1[i]-x2[i]);
		}
		System.out.println("inside manhattanDiatance: "+res);
		return res;
	}

	public double euclideanDistance(double[] x1,double[] x2,int numOfDocs){
		double res = 0.0;
		for(int i=0;i<=numOfDocs;i++){
			res += Math.pow((x1[i]-x2[i]),2);
		}
		res = Math.pow(res, (1/2));
		return res;
	}
	/* --------------------------------------------- */


	public static void main( String[] args ) {
		args = new String[1];
		args[0] = "/home/varsha/davisWiki/linksDavis.txt";
		if ( args.length != 1 ) {
			System.err.println( "Please give the name of the link file" );
		}
		else {
			new PageRank( args[0] );
		}
	}
}
