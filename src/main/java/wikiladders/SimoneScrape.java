package wikiladders;

import java.io.IOException;

import wikiladders.WikiNode;

/**
 * Get the HTML for Simone Weil's (the only great spirit of our time) Wikipedia
 * page and do something with it....
 */
public class SimoneScrape {

    public static void scrape() {

        try {
        	WikiNode node = new WikiNode("https://en.wikipedia.org/wiki/Simone_Weil");
        	for (WikiNode child : node.getChildren()) {
        		System.out.println(child.pageName + " -- " + child.displayTxt);
			} // for

        } catch (IOException e) {
            e.printStackTrace();
        } // try-catch

    } // scrape()

} // SimoneScrape