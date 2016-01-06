package com.searcher;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;

import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.trec.TrecTopicsReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;

import com.constants.Constants;
import com.result.QueryScore;

/**
 * This class is solution for Task-2
 * @author Anwar Shaikh
 *
 */
public class SearchTRECTTopics {

	/**
	 * This function process top 1000 results for all the short and long trec queries and writes result to the file.
	 * 
	 * @param similarity
	 *  similarity algoritm to be used
	 * @param algorithmName
	 * 	algorithm name which used to devise the output file name
	 * 
	 * @throws IOException
	 * @throws ParseException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static void processTop1KResults(Similarity similarity, String algorithmName) throws IOException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		// 1. Read the queries from trec topics
		TrecTopicsReader trecTopicReader = new TrecTopicsReader();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(Constants.TREC_TOPIC_FILE_PATH));
		QualityQuery[] qualityQueries = trecTopicReader.readQueries(bufferedReader);

		// 2. Get calculate query score each document 
		for(int queryIndex=0; queryIndex < qualityQueries.length; queryIndex++)
		{
			QualityQuery qualityQuery = qualityQueries[queryIndex];
			String queryID = qualityQuery.getQueryID();

			// 2.a Process for "Title" query (Short Query)
			// Braces to end the heavy variable scope (titleQueryScore)
			{
				String titleQuery = qualityQuery.getValue(Constants.TREC_TOPIC_QUERY_TITLE);
				String cleanedTitleQuery = cleanTitleQueryString(titleQuery);
				QueryScore titleQueryScore = EasySearch.getQueryRelevanceScores(cleanedTitleQuery, queryID, similarity);
				String OutputFilePath = Constants.TREC_TOPIC_OUTPUT_DIR + "/" + algorithmName + "ShortQuery" + ".txt";
				titleQueryScore.writeTop1KResults(OutputFilePath);

			}


			// 2.b Process for "Description" query (Long Query)
			// Braces to end the heavy variable scope (descQueryScore)
			{
				String descQuery = qualityQuery.getValue(Constants.TREC_TOPIC_QUERY_DESC);				
				String cleanedDescQuery = cleanDescQueryString(descQuery);
				QueryScore descQueryScore = EasySearch.getQueryRelevanceScores(cleanedDescQuery, queryID, similarity);
				String OutputFilePath = Constants.TREC_TOPIC_OUTPUT_DIR + "/" + algorithmName + "LongQuery" + ".txt";
				descQueryScore.writeTop1KResults(OutputFilePath);

			}


		}
		System.out.println("All the querie from TREC 51-100 executed successfully.");
	}

	/**
	 * This function removes un-necessary content from the title query string. For example, query string contains 
	 * "Topic: [queryText]" removes "Topic:" from the query and return.
	 * 
	 * @param queryString
	 *  Query String to be cleaned
	 * @return
	 * 	Cleaned title query string
	 */
	public static String cleanTitleQueryString(String queryString)
	{
		String cleanedQuery = null;

		int colonIndex = queryString.indexOf(":");
		cleanedQuery = queryString.substring(colonIndex+1, queryString.length());

		return cleanedQuery;
	}
	
	/**
	 * This function removes un-necessary content from the description query string.
	 * 
	 * @param queryString
	 *  Query String to be cleaned
	 * @return
	 * 	Cleaned description query string
	 */
	public static String cleanDescQueryString(String queryString)
	{
		String cleanedQuery = null;

		int smryIndex = queryString.indexOf("<smry>");
		if(smryIndex != -1 )
		{
			cleanedQuery = queryString.substring(0, smryIndex);
		}

		return cleanedQuery;
	}

	/**
	 * Main function to execute Task-2.
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			Similarity defaultSimilarity = new DefaultSimilarity();
			processTop1KResults(defaultSimilarity, "MYRANK1");
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error While Reading topics");
			e.printStackTrace();
		} 
		catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Error While processing queries");
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println(cleanQueryString("toic: jdskfj qery stirng. djsfak dlfja"));
	}

}
