package org.qcri.youtubescraping;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.api.services.youtube.YouTube.Videos;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

public class YoutubeURLParser implements Runnable {

	/**
	 * Youtube URL to scrape
	 */
	private String strURL;
	/**
	 * If true input URL column contain Video IDS instead of URLs
	 */
	private Boolean blnInputVideoIDs;
	/**
	 * Input line to prepend to the output lines
	 */
	private String strInputLine;

	/**
	 * Constructor for the YoutubeURLParser
	 * 
	 * @param strURL
	 *            Youtube URL to scrape
	 * @param strURL2
	 * @param blnInputVideoIDs
	 */
	public YoutubeURLParser(String strInputLine, String strURL,
			Boolean blnInputVideoIDs) {
		this.strInputLine = strInputLine;
		this.strURL = strURL;
		this.blnInputVideoIDs = blnInputVideoIDs;
	}
	
	
	public String getRealLocation(String link) throws Exception {
	    

	    final URL url = new URL(link);
	    final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	    urlConnection.setInstanceFollowRedirects(false);

	    return urlConnection.getHeaderField("location");
	    //assertEquals("http://stackoverflow.com/", location);
	    //String ur = urlConnection.getURL().toString();
	    //assertEquals(link, urlConnection.getURL().toString());
	}

	@Override
	public void run() {
		try {

			// Get video ID
//			String strVideoID = blnInputVideoIDs ? strURL : Utils
//					.extractVideoIDFromYoutubeURL(strURL);
			String strVideoID = null;
			if (strVideoID == null) {
				String d = getRealLocation(strURL);
				if(d==null)
					d = strURL;
				strVideoID = Utils.extractVideoIDFromYoutubeURLNaive(d);
			}
			
//			String strVideoID = blnInputVideoIDs ? strURL : Utils
//					.extractVideoIDFromYoutubeURL(strURL);
//			if (strVideoID == null) {
//				strVideoID = Utils.extractVideoIDFromYoutubeURLNaive(strURL);
//			}

			// Get query term from user.
			Videos.List objSearch = Program.objYouTube.videos().list("snippet");
			objSearch.setId(strVideoID);
			/*
			 * It is important to set your developer key from the Google
			 * Developer Console for non-authenticated requests (found under the
			 * API Access tab at this link: code.google.com/apis/). This is good
			 * practice and increased your quota.
			 */
			String strAPIKey = Program.objProperties.getProperty("api.key");
			objSearch.setKey(strAPIKey);
			/*
			 * This method reduces the info returned to only the fields we need
			 * and makes calls more efficient.
			 */
			objSearch.setFields("items(snippet/title,snippet/description,snippet/categoryId)");
			VideoListResponse objVideoListResponse = objSearch.execute();

			Video objVideo = objVideoListResponse.getItems().get(0);
			String strTitle = Utils.removeNewLineCharachters(objVideo
					.getSnippet().getTitle());
			String strDescription = Utils.removeNewLineCharachters(objVideo
					.getSnippet().getDescription());

			String strCategoryName = YoutubeCategories.getCategoryName(objVideo
					.getSnippet().getCategoryId());
			/*
			 * String strTags = ""; List<String> lstTags =
			 * objVideo.getSnippet().getTags(); if (lstTags != null) { for (int
			 * i = 0; i < lstTags.size(); i++) { if (i > 0) strTags += ",";
			 * strTags += Utils.removeNewLineCharachters(lstTags.get(i)); } }
			 * else { strTags = null; }
			 */

			// System.out.println(strInputLine + "\t" + strTitle + "\t" +
			// strDescription + "\t" + strCategoryName);
			// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
			// System.out.println(strInputLine + "\t" + strCategoryName);
//			Program.objPrintStream.println(strInputLine + "\t" + strTitle + "\t"
//					+ strDescription + "\t" + strCategoryName);
			Program.results.add(strInputLine + "\t" + strTitle + "\t"
					+ strDescription + "\t" + strCategoryName);

			//Program.incrementSemaphore();

		} catch (Exception ex) {
			// System.err.println("Exception while scraping Youtube URL(" +
			// strURL + "), message: " + ex.getMessage());
			Program.errors.add(strInputLine);
			System.err.println(strInputLine);
//			try {
//				Program.errorBw.write(strInputLine);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
}
