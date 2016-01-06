package com.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.Term;

/**
 * This class holds the document scores for the query term. Query is composed of multiple terms similar structure is depicted in storing the scores 
 * QueryScore contains many QueryTermScores object one for a term in query.
 * 
 * @author Anwar Shaikh
 *
 */
public class QueryTermScores {

	public Map<String, Double> documentIdToScoreMap;
	private String queryTermString;
	
	/**
	 * Creates QueryTermScores object for specified QueryTerm
	 * 
	 * @param queryTerm
	 * 	query term string
	 */
	public QueryTermScores(String queryTerm)
	{
		this.queryTermString = queryTerm;
		documentIdToScoreMap = new HashMap<String, Double>();
	}
	
	/**
	 * Adds the document score w.r.t to this term 
	 * 
	 * @param docNO
	 *  document number 
	 * @param score
	 * 	score of document w.r.t the term
	 */
	public void addDocumentScore(String docNO, double score)
	{
		documentIdToScoreMap.put(docNO, score);
	}
	
	/**
	 * This method returns the document score w.r.t to the term if it's relevant otherwise returns 0.
	 * 
	 * @param docNo
	 *  document number
	 * 
	 * @return
	 *  document score w.r.t to the term
	 */
	public double getDocumentScore(String docNo)
	{
		double documentScore = 0;
		if(documentIdToScoreMap.containsKey(docNo))
		{
			documentScore = documentIdToScoreMap.get(docNo);
		}
		return documentScore;
	}
}
