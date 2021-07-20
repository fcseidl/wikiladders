package wikiladders;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Short driver program for wikiladders package.
 * 
 * @author fcseidl
 *
 */
public class Driver {

	public static void main(String[] args) {
		//SimoneScrape.scrape();
		
		
		WikiNode source, dest;
		IterativeDeepening ids;
		ArrayList<String> path;
		
		source = new WikiNode("https://en.wikipedia.org/wiki/Gelato");
		//dest = new WikiNode("https://en.wikipedia.org/wiki/Albert_Camus");
		dest = new WikiNode("https://en.wikipedia.org/wiki/Martin_Luther_King_Jr.");
		
		ids = new IterativeDeepening(source, dest);
		
		try {
			path = ids.search();
			for (String displayTxt : path) {
				System.out.println(displayTxt);
			} // for
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} // try-catch
		
		
		
	} // main()

} // Driver
