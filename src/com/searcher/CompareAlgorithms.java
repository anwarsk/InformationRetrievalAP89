package com.searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.trec.TrecTopicsReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

import com.constants.Constants;

/**
 * This class is solution for Task-3. Comparing the different similarity algorithms.
 * @author Anwar Shaikh
 *
 */
public class CompareAlgorithms {

	
	/**
	 * This function writes the top 1000 documents in the specified TopDocs object to the OutputFileSpecified accoriding to trec_eval format.
	 * 
	 * @param topDocs
	 * 	TopDocs object to process
	 * @param indexSearcher
	 * 	IndexSearcher object
	 * @param queryID
	 * 	ID of query being processed
	 * @param outputFilePath
	 *  Output file path to write results
	 *  
	 * @throws IOException
	 */
	public static void writeTopDocs(TopDocs topDocs, IndexSearcher indexSearcher, String queryID, String outputFilePath) throws IOException
	{
		File outputFile = new File(outputFilePath);
		//outputFile.delete();
		outputFile.getParentFile().mkdirs();
		if(outputFile.exists() == false)
		{
			outputFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(outputFile, true);
		
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		
		for(int docIndex = 0; docIndex < scoreDocs.length; docIndex++)
		{
			ScoreDoc scoreDoc = scoreDocs[docIndex];
			String docNo = indexSearcher.doc(scoreDoc.doc).get("DOCNO");
			
			fileWriter.append(queryID);
			fileWriter.append(Constants.OUTPUT_FILE_DELIMITER + "Q0");
			fileWriter.append(Constants.OUTPUT_FILE_DELIMITER + docNo);
			fileWriter.append(Constants.OUTPUT_FILE_DELIMITER + (docIndex+1));
			fileWriter.append(Constants.OUTPUT_FILE_DELIMITER + scoreDoc.score);
			fileWriter.append(Constants.OUTPUT_FILE_DELIMITER + "run-1 \n");	
		}
		
		fileWriter.flush();
		fileWriter.close();
	}
	
	
	/**
	 * This function uses the similarity object specified and retrieves the Top 1000 docs for each query for Trec 51-1000 test set. 
	 * Further writes th results in trec_eval file format.
	 * 
	 * @param similarity
	 * 	Object of similarity to be used
	 * @param algorithmName
	 * 	algorithm name
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void processTop1KResults(Similarity similarity, String algorithmName) throws IOException, ParseException
	{
		// 1. Read the queries from trec topics
		TrecTopicsReader trecTopicReader = new TrecTopicsReader();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(Constants.TREC_TOPIC_FILE_PATH));
		QualityQuery[] qualityQueries = trecTopicReader.readQueries(bufferedReader);

		
		//2.Create searcher
		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(Constants.INDEX_DIR_PATH)));
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		StandardAnalyzer analyzer = new StandardAnalyzer();
		indexSearcher.setSimilarity(similarity);
		
		/* Task-1 : [1] Parsing query using analyzer */
		QueryParser queryParser = new QueryParser("TEXT", analyzer); 
		
		
		for(int queryIndex=0; queryIndex < qualityQueries.length; queryIndex++)
		{
			QualityQuery qualityQuery = qualityQueries[queryIndex];
			String queryID = qualityQuery.getQueryID();

			// Braces to end the heavy variable scope (titleQueryScore)
			{
				String titleStringQuery = qualityQuery.getValue(Constants.TREC_TOPIC_QUERY_TITLE);
				String cleanedTitleQuery = SearchTRECTTopics.cleanTitleQueryString(titleStringQuery);
				Query titleQuery = queryParser.parse(QueryParserUtil.escape(cleanedTitleQuery));
				TopDocs topDocs = indexSearcher.search(titleQuery, 1000);
				String outputFilePath = Constants.TREC_TOPIC_OUTPUT_DIR + "/" + algorithmName + "ShortQuery" + ".txt";
				writeTopDocs(topDocs, indexSearcher, queryID, outputFilePath);

			}


			// Braces to end the heavy variable scope (descQueryScore)
			{
				String descStringQuery = qualityQuery.getValue(Constants.TREC_TOPIC_QUERY_DESC);
				String cleanedDescQuery = SearchTRECTTopics.cleanDescQueryString(descStringQuery);
				Query descQuery = queryParser.parse(QueryParserUtil.escape(cleanedDescQuery));
				TopDocs topDocs = indexSearcher.search(descQuery, 1000);
				String outputFilePath = Constants.TREC_TOPIC_OUTPUT_DIR + "/" + algorithmName + "LongQuery" + ".txt";
				writeTopDocs(topDocs, indexSearcher, queryID, outputFilePath);

			}


		}
		System.out.println("All the querie from TREC 51-100 executed successfully For-" + algorithmName);
	}
	
	/**
	 * Main Function To execute Task-3
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try 
		{

			processTop1KResults(new DefaultSimilarity(), "DEFAULT_VECTOR");
			processTop1KResults(new BM25Similarity(), "BM25");
			processTop1KResults(new LMDirichletSimilarity(), "LMDirichlet");
			processTop1KResults(new LMJelinekMercerSimilarity((float) 0.7), "LMJelinek");
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
