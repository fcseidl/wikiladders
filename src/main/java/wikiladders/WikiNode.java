package wikiladders;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
     * @param url a web link
     * @return The title of the article if url belongs to a wikipedia article else null
     */
    private static String WikiArticleTitle(String url) {
		if (url.indexOf(WIKI_PREFIX) == 0) {		// wikipedia internal link
			int endIdx = url.indexOf('#');	// '#' begins subsecion title if present
			endIdx = (endIdx == -1) ? url.length() : endIdx;
			return url.substring(WIKI_PREFIX.length(), endIdx);
		} // if
    	return null; // not another wikipedia link
	} // WikiArticleTitle()

    /**
     * An iterable container which holds the children of a WikiNode.
     * Children are returned by their title strings so that each 
     * article may be checked against the list of previously visited pages
     * to avoid backtracking.
     */
    private class NodeChildren implements Iterable<String> {
    	
    	private Elements links;
    	
        public NodeChildren(WikiNode parent) throws IOException {
           Document doc = Jsoup.connect(SITE_PREFIX + WIKI_PREFIX + parent.articleTitle).get();
           links = doc.select("a[href]");
        } // ctor

        /**
         * Returns an anonymous iterator over the parent node's children.
         */
        public Iterator<String> iterator() {
        	return new Iterator<String>() {
        		
        		private Iterator<Element> linkIterator = links.iterator();
            	private String nextTitle = null;

                public boolean hasNext() {
                	String url;
                	while (linkIterator.hasNext() && nextTitle == null) {
                		url = linkIterator.next().attr("href");
                		nextTitle = WikiArticleTitle(url);
                	} // while
                    return linkIterator.hasNext();
                } // hasNext()

                public String next() throws NoSuchElementException {
                    if (!hasNext()) {
						throw new NoSuchElementException("iteration beyond last child node.");
					} // if
                    String result = nextTitle;
                    String url;
                    url = linkIterator.next().attr("href");
            		nextTitle = WikiArticleTitle(url);
            		return result;
                } // next()

            }; // anonymous iterator body
        } // iterator()
        
    } // NodeChildren

    public final String articleTitle;

    public WikiNode(final String articleTitle) {
        this.articleTitle = articleTitle;
    } // ctor

    /**
     * @return an iterable containing the article titles of children of this node.
     * @throws IOException 
     */
    public NodeChildren getChildren() throws IOException {
        return new NodeChildren(this);
    } // getChildren()

} // WikiNode