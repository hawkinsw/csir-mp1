package indexes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.lang.Thread;
import java.lang.InterruptedException;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import structures.Post;
import threads.Thread4WebMD;

/**
 * @author whh8b
 * @version 0.1
 * @category Index
 */

public class Index4WebMD extends IndexBase {

	public Index4WebMD(String title, String index) {
		super(title, index);
	}

	String getDiscussionBaseURL() {
		return m_index
			.replaceAll("exchanges", "forums")
			.replaceAll("com/", "com/3/")
			.replaceAll("/index.*$", "");
	}

	protected boolean parseIndex(String title, Document doc) {
		Elements thread_fmts= doc.body().getElementsByClass("thread_fmt");

		for (Element thread_fmt : thread_fmts) {
			Elements as = thread_fmt.getElementsByAttribute("href");
			for (Element a : as) {
				String href = a.attr("href");
				String discussionId = href.replaceAll("^.*/forum/(\\d+)$", "$1");
				String discussionURL = m_discussionBaseURL + "/" + discussionId;
				System.out.println("Parsing discussion at url: "+discussionURL+"...");

				Thread4WebMD thread = new Thread4WebMD();
				thread.parseThreadStartPage(discussionURL);
				thread.save2Json("./data/json/WebMD/"+ title+"/"+discussionId+".json");

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					/* eh.
					 */
				}

			}
		}
		return true;
	}

	/**
	 * Faux unit testing code for Index4WebMD.
	 */
	public static void main(String args[]) {
		Index4WebMD indexes = new Index4WebMD(
			"APD", 
			"http://exchanges.webmd.com/anxiety-and-panic-disorders-exchange/forum/index");
		indexes.parseIndex();
	}
}
