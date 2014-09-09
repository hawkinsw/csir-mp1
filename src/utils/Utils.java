package utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * A Utils class that does utilitarian things.
 *
 * @author whh8b
 * @version 0.1
 */
public class Utils {
	/**
	 * Get the HTML contents from a url.
	 *
	 * @param urlString The URL of the page to fetch
	 * @return The HTML contents of the page at urlString.
	 */
	public static String getHTML(String urlString) {
		URL url = null;
		HttpURLConnection connection = null;
		String line = null, output = new String("");

		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			return "";
		}

		try {
			connection = (HttpURLConnection)(url.openConnection());

			BufferedReader connectionReader = new BufferedReader(
				new InputStreamReader(connection.getInputStream())
			);

			do {
				line = connectionReader.readLine();
				output += line + "\n";
			} while (line != null);
			connectionReader.close();
			connection.disconnect();
		} catch (IOException e) {
			System.out.println("Oops: " + e.toString());
			return "";
		}
		return output;
	}
}
