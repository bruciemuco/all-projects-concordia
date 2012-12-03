package parser;
import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

/*
 * COMP6791 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
 * 
 */

public class HtmlToText {

	// convert html file to plain text
	public static String text(String filename) {
		File input = new File(filename);
		Document doc;
		try {
			doc = Jsoup.parse(input, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

        FormattingVisitor formatter = new FormattingVisitor();
        NodeTraversor traversor = new NodeTraversor(formatter);
        traversor.traverse(doc); 

        return formatter.toString();
	}
	
    // the formatting rules, implemented in a breadth-first DOM traverse
    private static class FormattingVisitor implements NodeVisitor {
        private StringBuilder accum = new StringBuilder();
        private boolean isA = false;
        private boolean isP = false;
        private boolean isFooter = false;

		@Override
		public void head(Node node, int depth) {
			String tagName = node.nodeName();
			if (tagName.equals("meta") && node.hasAttr("name")) {
				if (node.attr("name").equals("keywords")
						|| node.attr("name").equals("description")) {
					accum.append(node.attr("content"));
					accum.append(" ");
				}
				return;
			}

			if (tagName.equals("p")) {
				isP = true;
				return;
			}
			if (tagName.equals("a")) {
				if (!isP) {
					isA = true;
				}
				return;
			}

			String idAttr = node.attr("id");
			String classAttr = node.attr("class");
			if (idAttr.startsWith("footer") || classAttr.startsWith("footer")) {
				isFooter = true;
				return;
			}

			if (!isFooter && !isA && node instanceof TextNode) {
	           	accum.append(((TextNode) node).text()); 
			}
		}

		@Override
		public void tail(Node node, int depth) {
			String tagName = node.nodeName();
			
			if (tagName.equals("p")) {
				isP = false;
				return;
			}
			if (tagName.equals("a")) {
				isA = false;
				return;
			}			
		}
		
        public String toString() {
            return accum.toString();
        }
    }
}
