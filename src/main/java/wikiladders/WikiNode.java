package wikiladders;

import java.io.IOException;
import java.util.Iterator;
import java.lang.NullPointerException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Represents a wikipedia page as a node in the wikipedia graph.
 */
public class WikiNode {

    private static final String SITE_PREFIX = "https://en.wikipedia.org";
    private static final String WIKI_PREFIX = "/wiki/";
    
    /**
     * 
     * @param link a traversable internal wikipedia link
     * @return The title of the article linked by link, or null if the link isn't traversable.
     */
    private static String wikiPageName(Element link) {
    	String url = link.attr("href");
		if (url.indexOf(WIKI_PREFIX) == 0) {		// wikipedia internal link
			int endIdx = url.indexOf('#');	// '#' begins subsecion title if present
			endIdx = (endIdx == -1) ? url.length() : endIdx;
			return url.substring(WIKI_PREFIX.length(), endIdx);
		} // if
    	return null;
	} // wikiPageName()
    
    /**
     * An iterable container which holds the children of a WikiNode.
     * Note that children which backtrack are not allowed.
     */
    private static class NodeChildren implements Iterable<WikiNode> {
    	
    	private Elements links;
    	private final WikiNode node;
    	
        public NodeChildren(final WikiNode node) throws IOException {
           Document doc = Jsoup.connect(SITE_PREFIX + WIKI_PREFIX + node.pageName).get();
           links = doc.select("a[href]");
           this.node = node;
        } // ctor

        /**
         * Returns an anonymous iterator over the parent node's children.
         */
        public Iterator<WikiNode> iterator() {
        	return new Iterator<WikiNode>() {
        		
        		private Iterator<Element> linkIterator = links.iterator();
            	private String nextPageName = null, nextDisplayTxt = null;

            	/**
            	 * Advance linkIterator, and set nextPageName and nextDisplayTxt to the 
            	 * appropriate values, or to null if the next link is non-traversable
            	 * or backtracks.
            	 */
            	private void advanceLink() {
            		Element nextLink = linkIterator.next();
            		nextPageName = wikiPageName(nextLink);
            		nextDisplayTxt = nextLink.text();
            		
            		// prevent backtracking
            		if (nextPageName != null) {
	            		for (WikiNode ancestor = node; ancestor != null; ancestor = ancestor.parent) {
	            			if (nextPageName.equals(ancestor.pageName)) { 
	            				nextPageName = null; 
	            				break;
	            			} // if
	            		} // for
            		} // if not null
            	} // advanceLink()
            	
            	/**
            	 * Postcondition: if return value is true, nextLink holds the first in-game 
            	 * traversable link which has not yet been returned by next() and does not
            	 * backtrack.
            	 */
                public boolean hasNext() {
                	while (linkIterator.hasNext() && nextPageName == null) { advanceLink(); }
                    return linkIterator.hasNext();
                } // hasNext()

                public WikiNode next() throws NullPointerException {
                	// this check also achieves an important postcondition
                    if (!hasNext()) {
						throw new NullPointerException("iteration beyond last child node.");
					} // if
                    WikiNode nextChild = new WikiNode(nextPageName, nextDisplayTxt, node);
                    advanceLink();
                    return nextChild;
                } // next()

            }; // anonymous iterator body
        } // iterator()
        
    } // NodeChildren
    
    /**
     * Create a child node.
     * 
     * @param pageName
     * @param displayTxt
     */
    private WikiNode(final String pageName, final String displayTxt, final WikiNode parent) {
    	this.pageName = pageName;
    	this.displayTxt = displayTxt;
    	this.parent = parent;
    } // child ctor
    

    /** suffix identifying this page in url */
    public final String pageName;
    
    /** 
     *  Text of link used to access this page, null if this node has no parent. 
     */
    public final String displayTxt;
    
    /**
     * Parent node, null if this node has no parent.
     */
    public final WikiNode parent;
    
    /**
     * Create a WikiNode with no parent from a web URL string.
     * @param url
     */
    public WikiNode(final String url) throws IllegalArgumentException {
        if (url.indexOf(SITE_PREFIX + WIKI_PREFIX) == 0) {
        	int endIdx = url.indexOf('#');	// '#' begins subsection title if present
			endIdx = (endIdx == -1) ? url.length() : endIdx;
			pageName = url.substring((SITE_PREFIX + WIKI_PREFIX).length(), endIdx);
			displayTxt = null;
			parent = null;
        } else {
        	throw new IllegalArgumentException("Provided url is not a wikipedia link:" + url);
        } // if-else
    } // ctor

    /**
     * @return an iterable containing the article titles of children of this node.
     * @throws IOException 
     */
    public NodeChildren getChildren() throws IOException {
        return new NodeChildren(this);
    } // getChildren()
    
    /**
     * 
     * @param other
     * @return whether this and other refer to the same webpage.
     */
    public boolean isSamePageAs(WikiNode other) {
    	return pageName.compareTo(other.pageName) == 0;
    } // equals()

} // WikiNode