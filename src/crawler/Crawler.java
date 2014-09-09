package crawler;

import java.lang.System;
import indexes.Index4WebMD;
import java.util.ArrayList;

/**
 * Crawl a set of WebMD exchange topics.
 *
 * @author whh8b
 * @version 0.1
 */
public class Crawler {
	public static void main(String args[]) {
		ArrayList<String> indexes = new ArrayList<String>();

		indexes.add("anxiety-and-panic-disorders");
		indexes.add("parenting");
		indexes.add("heart-disease");

		for (String index : indexes) {
			int i = 1;
			/*
			 * It takes about 40 pages to get 1000
			 * threads.
			 */
			for (i = 1; i<40; i++) {

				String indexURL = "http://exchanges.webmd.com/"
					+ index
					+ "-exchange/forum/index";
				
				if (i != 1) {
					indexURL += ("?pg=" + i);
				}
				System.out.println("IndexURL: " + indexURL);
				Index4WebMD webMDIndex = new Index4WebMD(index, indexURL);
				webMDIndex.parseIndex();
			}
		}
	}
}
