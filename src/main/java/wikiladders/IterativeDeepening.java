package wikiladders;

import java.io.IOException;
import java.util.ArrayList;



/**
 * Class to perform iterative deepening search to find Wikipedia shortest paths.
 * Constructor takes a source and destination WikiNode. The search() function 
 * returns an ArrayList<String> with the display text of each link to be clicked 
 * along the shortest path between the two pages.
 * 
 * @author fcseidl
 *
 */
public class IterativeDeepening {

	private static final int DEFAULT_MAX_DEPTH_LIMIT = 5;

	// lots of spaces to overwrite previous path after carriage return in console
	private static final String CONSOLE_BUFFER = "                                                                                                            ";

	// how long to allow named in console before using ellipsis
	private static final int CONSOLE_ELLIPSIS_THRES = 20;

	private final WikiNode source, dest;
	
	private int currentDepthLimit;
	private String consolePath;
	
	/**
	 * Inorder search up to currentDepthLimit for path to destination node.
	 * 
	 * @param node
	 * @return A child node whose ancestors are the path from source to dest, 
	 * or null if one can't be found.
	 */
	private WikiNode depthFirstSearch(WikiNode node, int depth) throws IOException {
		consolePath = "";
		String displayTxt;
		for (WikiNode ancestor = node; ancestor.parent != null; ancestor = ancestor.parent) {
			// loop over non-root ancestry
			if (ancestor.displayTxt.length() > CONSOLE_ELLIPSIS_THRES) {
				displayTxt = ancestor.displayTxt.substring(0, CONSOLE_ELLIPSIS_THRES - 3) + "...";
			} else {
				displayTxt = ancestor.displayTxt;
			}
			consolePath = " -> " + displayTxt + consolePath;
		} // for
		System.out.print(consolePath + CONSOLE_BUFFER + "\r");
		
		// base cases
		if (node.isSamePageAs(dest)) { 
			System.out.println(consolePath);
			return node; 
		} // if done
		if (depth == currentDepthLimit) { return null; }
		
		// recursive case
		WikiNode end;
		for (WikiNode child : node.getChildren()) {
			end = depthFirstSearch(child, depth + 1);
			if (end != null) { return end; }
		} // for link
		
		return null;
	} // depthFirstSearch()
	
	
	public final int maxDepthLimit;
	
	public IterativeDeepening(WikiNode source, WikiNode dest) {
		this(source, dest, DEFAULT_MAX_DEPTH_LIMIT);
	} // ctor
	
	public IterativeDeepening(WikiNode source, WikiNode dest, int maxDepthLimit) {
		this.source = source;
		this.dest = dest;
		currentDepthLimit = 0;
		this.maxDepthLimit = maxDepthLimit;
	} // maxDepthLimit ctor
	
	/**
	 * @return ArrayList<String> with the display text of each link to be clicked 
	 * along the shortest path between the source and dest pages.
	 * @throws IllegalStateException if maxDepthLimit is exceeded.
	 */
	public ArrayList<String> search() throws IllegalStateException, IOException {
		while (currentDepthLimit <= maxDepthLimit) {
			System.out.println("Searching, depth limit " + currentDepthLimit + CONSOLE_BUFFER);
			WikiNode end = depthFirstSearch(source, 0);
			if (end != null) {
				ArrayList<String> result = new ArrayList<String>();
				for (WikiNode ancestor = end; ancestor.displayTxt != null; ancestor = ancestor.parent) {
					result.add(0, ancestor.displayTxt);
				} // for
				return result;
			} // if
			++currentDepthLimit;
		} // while
		
		// we've passed maxDepthLimit
		throw new IllegalStateException("IDS max depth of " + maxDepthLimit + " exceeded.");
	} // search()
	
} // IterativeDeepening
