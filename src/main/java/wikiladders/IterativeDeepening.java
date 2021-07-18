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
	
	private final WikiNode source, dest;
	
	private int currentDepthLimit;
	
	/**
	 * Inorder search up to currentDepthLimit for path to destination node.
	 * 
	 * @param node
	 * @return A child node whose ancestors are the path from source to dest, 
	 * or null if one can't be found.
	 */
	private WikiNode depthFirstSearch(WikiNode node, int depth) throws IOException {
		//System.out.println("node " + node.pageName + " at depth " + depth);
		//System.out.println(dest.pageName);
		
		// base cases
		if (node.isSamePageAs(dest)) { return node; }
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
	 * 
	 * @return ArrayList<String> with the display text of each link to be clicked 
	 * along the shortest path between the two pages.
	 * @throws IllegalStateException if maxDepthLimit is exceeded.
	 */
	public ArrayList<String> search() throws IllegalStateException, IOException {
		while (currentDepthLimit <= maxDepthLimit) {
			System.out.println("DFS with depth limit " + currentDepthLimit);
			
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
	} // search
	
	
	
} // IterativeDeepening
