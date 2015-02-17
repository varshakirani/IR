/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  


package ir;

import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.*;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


/**
 *   Processes a directory structure and indexes all PDF and text files.
 */
public class Indexer {
	public double tokenNo = 0;
	/** The index to be built up by this indexer. */
	public Index index;

	public BigIndex bigindex;
	/** The next docID to be generated. */
	private int lastDocID = 0;


	/* ----------------------------------------------- */


	/** Generates a new document identifier as an integer. */
	private int generateDocID() {
		return lastDocID++;
	}

	/** Generates a new document identifier based on the file name. */
	private int generateDocID( String s ) {
		return s.hashCode();
	}


	/* ----------------------------------------------- */

	

	/**
	 *  Initializes the index as a HashedIndex.
	 */
	public Indexer() {
		index = new HashedIndex();
//		bigindex = new LargeIndex();
	}
	public void setIndexer(boolean saveIndex){
		if(saveIndex){
			index = new LargeIndex();
		}
	}
	public Indexer(boolean saveIndex){
		if(saveIndex){
			index = new LargeIndex();
		}
	}


	/* ----------------------------------------------- */


	/**
	 *  Tokenizes and indexes the file @code{f}. If @code{f} is a directory,
	 *  all its files and subdirectories are recursively processed.
	 */
	public void processFiles( File f ) {
		// do not try to index fs that cannot be read
		if ( f.canRead() ) {
			if ( f.isDirectory() ) {
				String[] fs = f.list();
				// an IO error could occur
				if ( fs != null ) {
					for ( int i=0; i<fs.length; i++ ) {
						processFiles( new File( f, fs[i] ));
					}
				}
			} else {
				//System.err.println( "Indexing " + f.getPath() );
				// First register the document and get a docID
				int docID = generateDocID();
				index.docIDs.put( "" + docID, f.getPath() );
				try {
					//  Read the first few bytes of the file to see if it is 
					// likely to be a PDF 
					Reader reader = new FileReader( f );
					char[] buf = new char[4];
					reader.read( buf, 0, 4 );
					if ( buf[0] == '%' && buf[1]=='P' && buf[2]=='D' && buf[3]=='F' ) {
						// We assume this is a PDF file
						try {
							String contents = extractPDFContents( f );
							reader = new StringReader( contents );
						}
						catch ( IOException e ) {
							// Perhaps it wasn't a PDF file after all
							reader = new FileReader( f );
						}
					}
					else {
						// We hope this is ordinary text
						reader = new FileReader( f );
					}
					SimpleTokenizer tok = new SimpleTokenizer( reader );
					int offset = 0;
					while ( tok.hasMoreTokens() ) {
						String token = tok.nextToken();
						//			System.out.println(token+' '+docID+' '+offset );
						tokenNo++;
						insertIntoIndex( docID, token, offset++ );
					}
					index.docLengths.put( "" + docID, offset );
					/* To check getDictionary and getPostings
					Iterator<String> tokens = index.getDictionary();
					while(tokens.hasNext()){

						String token = tokens.next();
						PostingsList postingsList = index.getPostings(token);
						if(postingsList != null){
							//							System.out.println("Size: " + postingsList.size());
							//							System.out.println("token: "+ token+" Size: "+ postingsList.size());
						}
						else {
							System.out.println("no postings");
						}

					}
					 */
					index.setNoOfCollection(lastDocID);  //after reading all the files, last DocID will be the total number of collection
					reader.close();
				}
				catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}
	}


	/* ----------------------------------------------- */


	/**
	 *  Extracts the textual contents from a PDF file as one long string.
	 */
	public String extractPDFContents( File f ) throws IOException {
		FileInputStream fi = new FileInputStream( f );
		PDFParser parser = new PDFParser( fi );   
		parser.parse();   
		fi.close();
		COSDocument cd = parser.getDocument();   
		PDFTextStripper stripper = new PDFTextStripper();   
		String result = stripper.getText( new PDDocument( cd ));  
		cd.close();
		return result;
	}


	/* ----------------------------------------------- */


	/**
	 *  Indexes one token.
	 */
	public void insertIntoIndex( int docID, String token, int offset ) {
		index.insert( token, docID, offset );
		
//		bigindex.insert(token, docID, offset);
	}
	
	public void writeToDisk(){
		try {
			int noOfFiles = 100;
			String homeDir = "/home/varsha/KTH courses/Search Engine And Information Retrieval/Assignments/LAB1/IR";
			File mapFile = new File(homeDir+"/SavedIndex/"+"mapFile.txt");
			if(!mapFile.exists()){
				mapFile.createNewFile();
			}
			
			int noOfTokens = index.indexSize();
			Iterator<String> tokenIt = index.getDictionary();
			int tokensPerDoc =  (noOfTokens/noOfFiles);
			if(noOfTokens%noOfFiles != 0)
			{
				tokensPerDoc++;
			}
			int tokenIndex = 0;
			
			System.out.println("Total no of tokens:" + noOfTokens + "total no of files: " + noOfFiles);
			System.out.println("MaxTokensPerDoc: "+ tokensPerDoc);
//			for(int tokenIndex =0;tokenIndex<noOfTokens;tokenIndex++ ){
//				for(int t =0;t<tokensPerDoc;t++){
//					
//				}
//			}
			for(int fileNo =0;fileNo<noOfFiles;fileNo++){
				String fileName = fileNo+".txt";
				File file = new File(homeDir+"/SavedIndex/"+fileName);
				if(!file.exists()){
					System.out.println(file.getName()+ file.getPath());
					
					file.createNewFile();
					System.out.println("File created");
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				String content = "";
				for(int tokenNo = 0;tokenNo<tokensPerDoc;tokenNo++){
					if(tokenIndex < noOfTokens){
						if(tokenIt.hasNext()){
							String token = tokenIt.next();
							content = token + "\r\n" ;
							for(PostingsEntry entry: index.getPostings(token).getPostingsEntry()){
								content += " " + entry.docID + " ";
//								for(int pos:entry.getPositionsList()){
//									content += pos + " ";
//								}
								content += entry.getPositionsList().toString();
								content += "\r\n" ;
							}
							
						}
						
						tokenIndex++;
						fw.write(content);
					}
					else {
						break;
					}
					
				}
//				for(int tokenNo = 0;tokenNo<tokensPerDoc;tokenNo++){
//					if(tokenIndex < noOfTokens){
//						if(tokenIt.hasNext())
//						{
//							String token = tokenIt.next();
//							content +=  token +"\r\n";
//							for(PostingsEntry entry: index.getPostings(token).getPostingsEntry()){
//								content += entry.docID + " ";
//								for(Integer pos:entry.getPositionsList()){
//									content += pos + " ";
//								}
//								content += "\r\n";
//							}
//						}
//						tokenIndex++;
//					}
//					else {
//						break;
//					}
//				}
//				content = "Testing";
//				fw.write(content);
//				bw.write(content);
				fw.close();
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
}

