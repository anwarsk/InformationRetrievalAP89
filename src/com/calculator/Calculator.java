package com.calculator;

/**
 * This class is responsible for calculating/performing all the mathematical operations.
 * @author Anwar Shaikh
 *
 */
public class Calculator {
	
	/**
	 * This function calculates the TF-IDF Score for a term, provided with its specifications.
	 * 
	 * @param termCountInDoc 
	 * 	number of term occurrences in the document 
	 * @param docLength
	 *  length of the document
	 * @param termDocCount
	 * 	number of documents in the corpus containing the term
	 * @param totalDocCount
	 * 	total number of documents in the corpus
	 * @return
	 */
	public static double calculateTFIDFScoreForTerm(double termCountInDoc, double docLength, 
			double termDocCount, double totalDocCount)

	{
		double tFScore = (termCountInDoc/docLength);
		
		double iDFScore = Math.log(1+(totalDocCount/termDocCount));
		
		double score =  tFScore * iDFScore;

		return score;
	}
}
