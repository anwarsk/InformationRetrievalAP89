package com.searcher;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import com.calculator.Calculator;
import com.constants.Constants;
import com.result.QueryScore;
import com.result.QueryTermScores;

/**
 * This is a solution class for Task-1
 * @author Anwar Shaikh
 *
 */
public class EasySearch {

	/**
	 * This method returns the QueryScore object which contains- 
	 * (1) Set of relevance document IDs to the query
	 * (2) List of QueryTermScore for each Term in the query
	 * 
	 * @param queryString 
	 *  Query String 
	 * @param queryID
	 *  Query ID
	 * @param similarity
	 *  Similarity algorithm to be used for processing
	 *
	 * @return
	 *  Resultant Query Score Object
	 * @throws IOException
	 * @throws ParseException
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static QueryScore getQueryRelevanceScores(String queryString, String queryID, Similarity similarity) throws IOException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{

			IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(Constants.INDEX_DIR_PATH)));
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			StandardAnalyzer analyzer = new StandardAnalyzer();
			indexSearcher.setSimilarity(similarity);
			
			/* Task-1 : [1] Parsing query using analyzer */
			QueryParser queryParser = new QueryParser("TEXT", analyzer); 
			Query query = queryParser.parse(QueryParserUtil.escape(queryString));
			Set<Term> queryTerms = new HashSet<Term>();
			query.createWeight(indexSearcher,false).extractTerms(queryTerms);

			int corpusDocumentCount = indexReader.maxDoc();
			QueryScore queryScore = new QueryScore(queryID);


			List<LeafReaderContext> leafReaderContexts = indexReader.getContext().reader().leaves();
			
			/* Task-1: [2] Calculating relevance score each term in query */
			for(Term queryTerm: queryTerms)
			{
				int documentFrequencyForTerm = indexReader.docFreq(queryTerm);
				
				QueryTermScores queryTermScore = new QueryTermScores(queryTerm.text());
				
				for(LeafReaderContext leafReaderContext : leafReaderContexts)
				{
					PostingsEnum posting = MultiFields.getTermDocsEnum(leafReaderContext.reader(),"TEXT", new BytesRef(queryTerm.text()));
					if(posting != null)
					{
						while(posting.nextDoc() != PostingsEnum.NO_MORE_DOCS)
						{
							int termFrequencyInDocument = posting.freq();
							int documentID = posting.docID() + leafReaderContext.docBase;
							String docNO = indexSearcher.doc(documentID).get("DOCNO");
							long nonNormalizedDocLength = leafReaderContext.reader().getNormValues("TEXT").get(posting.docID()); 
							
							double normalizedDocumentLength = (float) similarity.getClass().getMethod("decodeNormValue", long.class).invoke(similarity, nonNormalizedDocLength);
							
							double documentLength = 1 / (normalizedDocumentLength * normalizedDocumentLength);

							double relevanceScoreForTerm = Calculator.calculateTFIDFScoreForTerm(termFrequencyInDocument, documentLength, documentFrequencyForTerm, corpusDocumentCount);
							queryTermScore.addDocumentScore(docNO, relevanceScoreForTerm);
							queryScore.addRelevantDocument(docNO);
							//System.out.println("Relevance Score for Term: '" + queryTerm.text() + "' for document: "+ docNO +" = " + relevanceScoreForTerm);
						}
					}

				}
				
				// Store the scores calculated for each term */
				queryScore.addQueryTermScores(queryTermScore);
			}
			indexReader.close();
			return queryScore;
	}

	/**
	 * Main function to execute Task-1.
	 * @param args
	 */
	public static void main(String[] args) {
		try
		{
			String queryString = "police";
			String queryID = "1";
			Similarity defaultSimilarity = new DefaultSimilarity();
			
			QueryScore queryScore = getQueryRelevanceScores(queryString, queryID, defaultSimilarity);
			
			/* Task-1 [3]: Calculating relevance score for query w.r.t. to documents */
			for(String docNo : queryScore.relevantDocumentIDs)
			{
				System.out.println("DocumentID: " + docNo + "     Score:" + queryScore.getDocumentScore(docNo));
			}
			
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error while reading index directroy.");
			e.printStackTrace();
		} 
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
