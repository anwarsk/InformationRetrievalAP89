package com.result;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.constants.Constants;

/**
 * This Consumer class for the processing the sorted QueryScore. Which writes the QueryScores to the file encapsulated in the FileWriter object.
 * 
 * @author Anwar Shaikh
 *
 */
public class ScoreConsumer implements Consumer<Map.Entry<String, Double>> {


	private FileWriter fileWriter;
	private String queryID;
	private int documentRank;
	
	/**
	 * This function creates the ScoreConsumer with specified FileWriter for query with specified ID.
	 * 
	 * @param fileWriter
	 * 	FileWriter object to write results
	 * @param queryID
	 *  QueryID which is being processed by this ScoreConsumer
	 */
	public ScoreConsumer(FileWriter fileWriter, String queryID) {
		// TODO Auto-generated constructor stub
		this.fileWriter = fileWriter;
		this.queryID = queryID;
		this.documentRank = 1;
	}

	/**
	 * This is overridden method from java.util.function.Consumer class. Which writers the specified entry to the file.
	 */
	@Override
	public void accept(Entry<String, Double> entry) {
		// TODO Auto-generated method stub
		try
		{
			fileWriter.append(this.queryID);
			fileWriter.append(Constants.OUTPUT_FILE_DELIMITER + "Q0");
			fileWriter.append(Constants.OUTPUT_FILE_DELIMITER + entry.getKey());
			fileWriter.append(Constants.OUTPUT_FILE_DELIMITER + documentRank);
			fileWriter.append(Constants.OUTPUT_FILE_DELIMITER + entry.getValue());
			fileWriter.append(Constants.OUTPUT_FILE_DELIMITER + "run-1 \n");
			documentRank++;
		}
		catch(IOException e)
		{
			System.out.println("Unable to write- " + entry.getKey());
			e.printStackTrace();
		}
	}

}
