package indexes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import structures.Post;

import utils.Utils;

/**
 * Represent an index of discussions.
 * @author whh8b
 * @version 0.1
 * @category Index
 */
public abstract class IndexBase {
	
	public IndexBase(String indexTitle, String index) {
		m_index = index;
		m_title = indexTitle;
		m_discussionBaseURL = getDiscussionBaseURL();
		m_discussions = new ArrayList<Integer>();
	}

	/**
	 * An integer list of discussion IDs under
	 * this Index.
	 */
	ArrayList<Integer> m_discussions;
	/**
	 * A string for the index URL.
	 */
	String m_index;
	/**
	 * A string for the base URL of all discussions.
	 */
	String m_discussionBaseURL;
	/**
	 * The title for this index.
	 */
	String m_title;

	/**
	 * Get the base URL for any discussions
	 * in this index.
	 *
	 * @return a string for the base URL of any
	 *  discussions referenced in this index.
	 */
	abstract String getDiscussionBaseURL();

	/**
	 * Parse an index file from its Document
	 * representation.
	 *
	 * @param title The title of the index being parsed.
	 * @param doc A Document of the index page.
	 * @return A boolean (true or false) depending
	 * upon the success or failure of parsing the
	 * Document doc.
	 */
	abstract protected boolean parseIndex(String title, Document doc); 

	/**
	 * Parse an index file from its source.
	 *
	 * @return A boolean (true or false) depending
	 * upon the success or failure of parsing the
	 * page at m_index.
	 */
	public boolean parseIndex() {
		try {
			/*
			 * For testing purposes, make .XXXX a special
			 * syntax to indicate that m_index is a filename.
			 * Otherwise, assume that it's a URL.
			 */
			if (m_index.charAt(0) == '.') {
				return parseIndex(m_title, Jsoup.parse(new File(m_index), "UTF-8"));
			} else {
				return parseIndex(m_title, Jsoup.parse(utils.Utils.getHTML(m_index)));
			}
		} catch (IOException e) {
			System.err.format("[Error]Failed to parse %s!\n", m_index);
			e.printStackTrace();
			return false;
		}
	}
}
