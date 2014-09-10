/**
 * 
 */
package structures;

import json.JSONException;
import json.JSONObject;

import org.jsoup.nodes.Document;

/**
 * @author whh8b
 * @version 0.1
 * @category data structure
 * data structure for to associate an index with a thread
 * page.
 */
public class ThreadPage {
	int m_ID;
	Document m_doc;

	public ThreadPage(int id, Document d) {
		m_ID = id;
		m_doc = d;
	}

	public int getID() {
		return m_ID;
	}

	public Document getDocument() {
		return m_doc;
	}
}
