package pages;

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

/**
 * @author hongning
 * @version 0.1
 * @category Pages
 * code for base class of wrapper for parsing html files and extract threaded discussions to json format 
 *
 * modified to remove json serialization; it was moved
 * to Thread.
 *
 * modified to add getPosts()
 */

public abstract class PageBase {
	
	public PageBase() {
		m_posts = new ArrayList<Post>();
		m_dateFormatter = new SimpleDateFormat("yyyyMMdd-HH:mm:ss Z");//standard date format for this project
	}

	/**
	 * The URL of this page.
	 */
	String m_pageURL;
	/**
	 * The title of the thread on this page.
	 */
	String m_threadTitle;
	/**
	 * An array of Post-s
	 */
	ArrayList<Post> m_posts;
	
	/**
	 * date format helpers to normalize different date format across websites
	 */
	SimpleDateFormat m_dateParser, m_dateFormatter;

	/**
	 * The document being parsed.
	 */
	protected Document m_doc;

	public Document getDocument() {
		if (m_doc != null) {
			return m_doc;
		} else {
			return null;
		}
	}
	
	/**
	 * parse the given HTML and extract the discussion posts 
	 *
	 * @return A boolean (true or false) depending
	 * upon the success or failure of parsing the
	 * page at m_index.
	 */
	public boolean parseHTML(String name) {
		try {
			/*
			 * For testing purposes, make .XXXX a special
			 * syntax to indicate that m_index is a filename.
			 * Otherwise, assume that it's a URL.
			 */
			if (name.charAt(0) == '.') {
				return parseHTML(Jsoup.parse(new File(name), "UTF-8"));
			} else {
				return parseHTML(Jsoup.parse(utils.Utils.getHTML(name)));
			}
		} catch (IOException e) {
			System.err.format("[Error]Failed to parse %s!\n", name);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Return the posts parsed from this page.
	 *
	 * @return The posts parsed from this page.
	 */
	public ArrayList<Post> getPosts() {
		return m_posts;
	}

	/**
	 * parse the given Document and extract the discussion posts 
	 *
	 * @return A boolean (true or false) depending
	 * upon the success or failure of parsing the
	 * Document doc.
	 */
	abstract protected boolean parseHTML(Document doc); 
	abstract protected String extractReplyToID(String text);
	
	protected String parseDate(String date) throws ParseException {
		return m_dateFormatter.format(m_dateParser.parse(date));
	}
}
