package threads;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.net.URL;
import java.net.MalformedURLException;

import java.lang.Thread;
import java.lang.InterruptedException;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import structures.Post;
import structures.ThreadPage;

import pages.Page4WebMD;

import utils.Utils;

public class Thread4WebMD extends ThreadBase {
	
	public Thread4WebMD() {
		super();
	}

	protected boolean parseThreadStartPage(Document doc) {
		int threadCount = -1;
		int parsedPages = 0;

		/*
		 * Assume that this is the starting page for a discussion.
		 * 0. Find the title for this discussion.
		 * 0.5. Create a "relative" URL to use as the default reply to id.
		 * 1. Generate a list of pages that relate to this thread.
		 * 2. One by one, 
		 *    a. download that page.
		 *    b. generate a Page4WebMd
		 *    c. send the current page to that wrapper.
		 *    d. take the posts from that wrapper.
		 *    e. merge the new posts with existing posts.
		 *    f. wait a set amount of time.
		 */
		System.out.println("Thread URL: " + m_threadURL);

		/*
		 * Step 0:
		 */
		Elements titleElements = doc.getElementsByClass("first_item_title_fmt");
		for (Element titleElement : titleElements) {
			if (!titleElement.text().equals("")) {
				m_threadTitle = titleElement.text();
				break;
			}
		}
		System.out.println("Thread title: " + m_threadTitle);

		/*
		 * Step 0.5:
		 */
		m_defaultReplyToID = m_threadURL;
		try {
			URL threadURL = new URL(m_threadURL);
			m_defaultReplyToID = threadURL.getPath();
		} catch (MalformedURLException e) {
		}

		/*
		 * Step 1:
		 * a. Use div class "pages" to find the links to
		 * each page of the discussion.
		 * b. Inside that div is an element whose onclick 
		 * value is "ctrs('srb-tpage_last')"
		 * c. Inside that element, the href has the format
		 * ?pg=X where X is total number of pages.
		 */
		Elements pagesElements = doc.getElementsByClass("pages");
		for (Element pageElement : pagesElements) {
			/*
			 * There are two of these per page. Take the first one.
			 */
			Elements clickToLastThreadPage = pageElement.getElementsByAttributeValue(
				"onclick", 
				"ctrs('srb-tpage_last');");
			if (!clickToLastThreadPage.isEmpty()) {
				/*
				 * attr() on a list calls attr on the first matching
				 * element in the list, so this is okay!
				 */
				String lastPageHref = clickToLastThreadPage.attr("href");
				String lastPage = lastPageHref.replaceAll("^?.+=", "");
				threadCount = Integer.parseInt(lastPage);
				break;
			}
		}
		if (threadCount == -1) {
			System.out.println("Cannot determine the thread count. " + 
				"Assuming the thread is on one page.");
			threadCount = 0;
		}

		/*
		 * Step 2
		 */
		do {
			Page4WebMD page = new Page4WebMD();
			String pageURL = m_threadURL;

			if (parsedPages != 0) {
				pageURL += "?pg=" + (parsedPages + 1);
			}
			System.out.println("page-ing " + (parsedPages + 1) + " page.");
			/*
			 * Step 2 a, b and c.
			 */
			page.parseHTML(pageURL);

			/*
			 * Step 2 d
			 */
			ArrayList<Post> pagePosts = page.getPosts();

			/*
			 * Step 2 e
			 */
			for (Post p : pagePosts) {
				if (!m_posts.contains(p)) {
					/*
					 * We may need to reset the reply to id.
					 */
					if (p.getReplyToID() == null) {
						p.setReplyToID(m_defaultReplyToID);
					}
					m_posts.add(p);
				} else {
					System.out.println("Skipping duplicate post.");
				}
			}

			m_threadPages.add(new ThreadPage(parsedPages, page.getDocument()));
			parsedPages++;

			/*
			 * Step 2 f
			 */
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		} while (parsedPages < threadCount);

		System.out.println("Thread had " + m_posts.size() + " posts.");
		return true;
	}

	private String m_defaultReplyToID;

	/**
	 * Faux unit test for Thread4WebMD
	 */
	public static void main(String args[]) {
		Thread4WebMD thread = new Thread4WebMD();
		//thread.parseThreadStartPage("./data/HTML/WebMD/APD/2657");
		thread.parseThreadStartPage("http://forums.webmd.com/3/anxiety-and-panic-disorders-exchange/forum/2657");
		thread.save2Json("./data/json/WebMD/anxiety-and-panic-disorders/2657.json");
		thread.save2Html("./data/HTML/WebMD/anxiety-and-panic-disorders/" + Utils.getComputingID() + "-" + "2657");
	}
}
