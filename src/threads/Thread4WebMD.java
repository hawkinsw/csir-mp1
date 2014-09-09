package threads;

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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import structures.Post;

import pages.Page4WebMD;

public class Thread4WebMD extends ThreadBase {
	
	public Thread4WebMD() {
		super();
	}

	protected boolean parseThreadStartPage(Document doc) {
		int threadCount = -1;
		int parsedPages = 0;

		/*
		 * Assume that this is the starting page for a discussion.
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
					m_posts.add(p);
				} else {
					System.out.println("Skipping duplicate post.");
				}
			}
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

	/**
	 * Faux unit test for Thread4WebMD
	 */
	public static void main(String args[]) {
		Thread4WebMD thread = new Thread4WebMD();
		thread.parseThreadStartPage("./data/HTML/WebMD/APD/2658");
	}
}
