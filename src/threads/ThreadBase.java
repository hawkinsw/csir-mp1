package threads;

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
import structures.ThreadPage;

import utils.Utils;

/**
 * Represent a discussion thread that may span 
 * multiple pages.
 *
 * @author whh8b
 * @version 0.1
 * @category Thread
 */
public abstract class ThreadBase {
	
	public ThreadBase() {
		m_posts = new ArrayList<Post>();
		m_threadPages = new ArrayList<ThreadPage>();
	}

	/**
	 * The URL for this Thread
	 */
	String m_threadURL;
	/**
	 * The title of this Thread.
	 */
	String m_threadTitle;
	/**
	 * An array of Post-s which
	 * constitute this thread.
	 */
	ArrayList<Post> m_posts;

	ArrayList<ThreadPage> m_threadPages;
	
	/**
	 * Parse a discussion thread from its first page.
	 *
	 * @return A boolean (true or false) depending
	 * upon the success or failure of parsing the
	 * page at m_index.
	 */
	public boolean parseThreadStartPage(String name) {
		m_threadURL = name;
		try {
			/*
			 * For testing purposes, make .XXXX a special
			 * syntax to indicate that m_index is a filename.
			 * Otherwise, assume that it's a URL.
			 */
			if (name.charAt(0) == '.') {
				return parseThreadStartPage(Jsoup.parse(new File(name), "UTF-8"));
			} else {
				return parseThreadStartPage(Jsoup.parse(utils.Utils.getHTML(name)));
			}
		} catch (IOException e) {
			System.err.format("[Error]Failed to parse %s!\n", name);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Parse a discussion thread from its first page (as a
	 * Document).
	 *
	 * @return A boolean (true or false) depending
	 * upon the success or failure of parsing the
	 * Document doc.
	 */
	abstract protected boolean parseThreadStartPage(Document doc); 
	
	/**
	 * Serialize this Thread to a json structure;
	 * This implementation was written by Dr Wang.
	 *
	 * @param filename The filename for storage of the
	 * serialized Thread.
	 * @returns true or false depending on whether or
	 * not the serialization succeeded.
	 */
	public boolean save2Json(String filename) {
		JSONArray postlist = new JSONArray();
		for(Post p:m_posts){
			try {
				postlist.put(p.getJSON());
			} catch (JSONException e) {
				System.err.format("[Error]Failed to convert %s into json format!\n", p.getID());
				e.printStackTrace();
			}
		}		
		
		try {
			JSONObject thread = new JSONObject(); 
			
			thread.put("URL", m_threadURL);
			thread.put("title", m_threadTitle);
			thread.put("thread", postlist);
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
			writer.write(thread.toString());
			writer.close();
			
			return true;
		} catch (JSONException | IOException e) {
			System.err.format("[Error]Failed to save to %s!\n", filename);
			e.printStackTrace();
			return false;
		}
	}

	public boolean save2Html(String baseFilename) {
		for (ThreadPage p : m_threadPages) {
			Utils.writeHTMLToFile(p.getDocument().toString(),
				baseFilename + "-p" + p.getID() + ".html"
			);
		}
		return true;
	}
}
