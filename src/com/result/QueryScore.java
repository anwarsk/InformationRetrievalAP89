package com.result;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * This class is responsible for maintaining all the micro results generated for 
 * each term in the query. Query is composed of multiple terms similar structure is depicted in storing the scores 
 * QueryScore contains many QueryTermScores object one for a term in query.
 * 
 * @author Anwar Shaikh
 *
 */
public class QueryScore {

	public List<QueryTermScores> queryTermScoresList; 
	public Set<String> relevantDocumentIDs;
	private String queryID;
	
	/**
	 * Creates object QueryScore for query with specified ID
	 * @param queryID
	 *  Query ID
	 */
	public QueryScore(String queryID)
	{
		this.queryID = queryID;
		queryTermScoresList = new ArrayList<QueryTermScores>();
		relevantDocumentIDs = new HashSet<String>();
	}
	
	/**
	 * Add the QueryTermScores which holds the document score for each term of query.
	 * 
	 * @param queryTermScores
	 *  Object of QueryTermScore class
	 */
	public void addQueryTermScores(QueryTermScores queryTermScores)
	{
		queryTermScoresList.add(queryTermScores);
	}
	
	/**
	 * Calculate and returns the document score for specified document.
	 * @param docNo
	 * 	document number
	 * @return
	 * 	score of document w.r.t to the query
	 */
	public double getDocumentScore(String docNo)
	{
		double documentScoreForQuery = 0;
		
		for(QueryTermScores queryTermScores : queryTermScoresList)
		{
			documentScoreForQuery += queryTermScores.getDocumentScore(docNo);
		}
		
		return documentScoreForQuery;
	}
	
	/**
	 * Add the specified document number to the relevant document number for the query.
	 * 
	 * @param docNO
	 * 	document number
	 */
	public void addRelevantDocument(String docNO)
	{
		relevantDocumentIDs.add(docNO);
	}


	/**
	 * Returns the map containing document id to it's score w.r.t query.
	 * @return
	 *  map containing document id to it's score w.r.t query
	 */
	public Map<String, Double> getDocumentIdToScoreMap()
	{
		Map<String, Double> documentScoreMap = new HashMap<String, Double>();
		
		for(String docNo : relevantDocumentIDs)
		{
			double score = this.getDocumentScore(docNo);
			documentScoreMap.put(docNo, score);
		}
		
		return documentScoreMap;
	}
	
	/**
	 * This functions writes the top 1000 result document for the query in 'treceval' format to the specified output file.
	 * 
	 * @param outputFilePath
	 *  output file path
	 *  
	 * @throws IOException
	 */
	public void writeTop1KResults(String outputFilePath) throws IOException {

		Map<String, Double> documentIdToScoreMap = this.getDocumentIdToScoreMap();
		
		File outputFile = new File(outputFilePath);
		//outputFile.delete();
		outputFile.getParentFile().mkdirs();
		if(outputFile.exists() == false)
		{
			outputFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(outputFile, true);
		
		ScoreConsumer scoreConsumer = new ScoreConsumer(fileWriter, this.queryID);
		
		/* Sort DocumentID to score array on score in reverse order and limit size to 1000 to get top 1K documents
		 * Here I used java Consumer<T> class as ScoreConsumer which writes the each document to the file.
		 */
		documentIdToScoreMap.entrySet().stream()
		.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
		.limit(1000).forEachOrdered(scoreConsumer);
		
		fileWriter.flush();
		fileWriter.close();
	}

	
}
