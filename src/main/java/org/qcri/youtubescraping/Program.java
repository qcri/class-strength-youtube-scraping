package org.qcri.youtubescraping;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.google.api.client.json.JsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

public class Program {
	/**
	 * Thread context class loader
	 */
	public static final ClassLoader CONTEXT_CLASS_LOADER = Thread
			.currentThread().getContextClassLoader();

	/**
	 * Global instance properties filename.
	 * */
	private static String PROPERTIES_FILENAME = "youtube.properties";
	

	/**
	 * Global instance of the HTTP transport.
	 * */
	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	/**
	 * Global instance of the JSON factory.
	 * */
	public static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/**
	 * Input URLs file path (TSV)
	 */
	private static String strInputFilePath;

	/**
	 * Global instance of Youtube object to make all API requests.
	 * */
	public static YouTube objYouTube;

	/**
	 * Global properties object
	 */
	public static Properties objProperties;

	/**
	 * Semaphore for threads
	 */
	public static Integer intSemaphore = 0;

	/**
	 * The column to find the URL to parse in the input TSV file
	 */
	private static Integer intURLColumn = 0;
	/**
	 * If true input URL column contain Video IDS instead of URLs
	 */
	private static Boolean blnInputVideoIDs = true;

	/**
	 * Output stream in UTF8
	 */
	static PrintStream objPrintStream = null;
	static ArrayList<String> results = null;
	static ArrayList<String> errors = null;
	static BufferedReader br;
    static BufferedWriter resultBw = null; 
    static BufferedWriter errorBw = null;

	/**
	 * Main Method
	 * 
	 * @param args
	 *            Command line arguments
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {
		Program.objPrintStream = new PrintStream(System.out, true, "UTF-8");
		Program.results = new ArrayList<>();
		Program.errors = new ArrayList<>();

		if (args.length != 2) {
			// System.err.println("Arguments: inputFilePath URLColumn ThreadsCount");
			System.err.println("Arguments: inputFilePath URLColumn");
			System.exit(-1);
		} else {
			strInputFilePath = args[0];
			intURLColumn = Integer.parseInt(args[1]);
			// intSemaphore = Integer.parseInt(args[2]);
		}

		// Open input file to read URLs
		BufferedReader objBufferedReader = new BufferedReader(new FileReader(
				strInputFilePath));

		// Read the developer key from youtube.properties
		objProperties = new Properties();
		try {
			objProperties.load(Program.CONTEXT_CLASS_LOADER
					.getResourceAsStream(PROPERTIES_FILENAME));

		} catch (IOException e) {
			System.err.println("There was an error reading "
					+ PROPERTIES_FILENAME + ": " + e.getCause() + " : "
					+ e.getMessage());
			System.exit(1);
		}

		/*
		 * The YouTube object is used to make all API requests. The last
		 * argument is required, but because we don't need anything initialized
		 * when the HttpRequest is initialized, we override the interface and
		 * provide a no-op function.
		 */
		objYouTube = new YouTube.Builder(Program.HTTP_TRANSPORT,
				Program.JSON_FACTORY, new HttpRequestInitializer() {
					public void initialize(HttpRequest request)
							throws IOException {
					}
				}).setApplicationName("class-strength").build();
		resultBw = openFileForWriting("/home/disooqi/qcri/class-strength-offline/ru.out.00");
		 errorBw = openFileForWriting("/home/disooqi/qcri/class-strength-offline/ru.er.00");
		// Start Threadsf
		String strInputLine = null;
		int count = 0;
		while ((strInputLine = objBufferedReader.readLine()) != null) {
			count++;
			if(count < 610000){
				continue;
			}
			String strURL = strInputLine.split("\t")[intURLColumn].trim();

			if (strURL != null && strURL.length() > 0) {
				if (Utils.isYoutubeURL(strURL) || blnInputVideoIDs) {
					Runnable objRunnable = new YoutubeURLParser(
							strInputLine.trim(), strURL, blnInputVideoIDs);
					Thread objYoutubeURLParserThread = new Thread(objRunnable);
					objYoutubeURLParserThread.start();

					intSemaphore++;
					if (intSemaphore == 300) {
						objYoutubeURLParserThread.join();
						Thread.sleep(5000);
						for(String res: results){
							resultBw.write(res);
							resultBw.newLine();
						}
						for(String er: errors){
							errorBw.write(er);
							errorBw.newLine();
						}
						
						System.out.println("========================================================================================================================");
						System.out.println("======================================================   Break   =======================================================");
						System.out.println("========================================================================================================================");
						
						Program.results.clear();
						Program.errors.clear();
						intSemaphore = 0;
					}else{
						//System.out.println("==================");
					}
					
				} else {
					System.err.println("Input URL is not Youtube one: "
							+ strURL);
				}
			}
		}
		System.out.println(Program.errors.size());
		
		resultBw.close();
        errorBw.close();

		// Close input file
		objBufferedReader.close();
	}

	public static synchronized void incrementSemaphore() {
		Program.intSemaphore++;
	}

	public static synchronized void decrementSemaphore() {
		Program.intSemaphore--;
	}
	
    public static BufferedReader openFileForReading(String filename) throws FileNotFoundException {
        BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
        return sr;
    }

    public static BufferedWriter openFileForWriting(String filename) throws FileNotFoundException {
        BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
        return sw;
    }
}
