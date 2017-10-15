package org.qcri.youtubescraping;

import java.io.IOException;
import java.util.Hashtable;

import com.google.api.services.youtube.model.VideoCategory;
import com.google.api.services.youtube.YouTube.VideoCategories;
import com.google.api.services.youtube.model.VideoCategoryListResponse;

public class YoutubeCategories {

	private static Hashtable<String, String> tblYoutubeTags = new Hashtable<String, String>();

	public static String getCategoryName(String strCategoryId)
			throws IOException {
		String strCategoryName = null;

		if (tblYoutubeTags.containsKey(strCategoryId)) {
			strCategoryName = tblYoutubeTags.get(strCategoryId);
		} else {
			// Get query term from user.
			VideoCategories.List objSearch = Program.objYouTube
					.videoCategories().list("snippet");
			objSearch.setId(strCategoryId);
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
			objSearch.setFields("items(snippet/title)");
			VideoCategoryListResponse objVideoCategoryListResponse = objSearch
					.execute();

			VideoCategory objVideoCategory = objVideoCategoryListResponse
					.getItems().get(0);

			strCategoryName = objVideoCategory.getSnippet().getTitle();

			tblYoutubeTags.put(strCategoryId, strCategoryName);
		}

		return strCategoryName;
	}
}
