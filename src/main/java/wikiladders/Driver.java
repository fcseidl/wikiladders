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
		
		source = new WikiNode("https://en.wikipedia.org/wiki/Kyoto");
		dest = new WikiNode("https://en.wikipedia.org/wiki/Japan");
		
		ids = new IterativeDeepening(source, dest);
		
		try {
			path = ids.search();
			for (String displayTxt : path) {
				System.out.println(displayTxt);
			} // for
			System.out.println("Used " + WikiNode.getTotalNodesExpanded() + " page connections.");
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} // try-catch
		
		
		
	} // main()

} // Driver
