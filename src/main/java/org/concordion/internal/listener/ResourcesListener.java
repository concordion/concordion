package org.concordion.internal.listener;

import java.util.ArrayList;
import java.util.List;
import org.concordion.api.listener.DocumentParsingListener;
import org.concordion.internal.listener.ResourcesFactory.ResourceToCopy;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

/**
 * Adds custom CSS to the generated specifications
 * @author sumnera
 */
public class ResourcesListener implements DocumentParsingListener {
	private List<ResourceToCopy> sourceFiles = new ArrayList<ResourceToCopy>();
		
	public ResourcesListener(List<ResourceToCopy> sourceFiles) {
		this.sourceFiles = sourceFiles;
	}

	@Override
	public void beforeParsing(Document document) {
		Element head = document.getRootElement().getFirstChildElement("head");

		removeExistingStyling(head);
		removeExistingScripts(head);
	}

	private void removeExistingStyling(Element head) {
		Elements links = head.getChildElements("link");
				
		for (int i = links.size() - 1; i >= 0; i--) {
			Element link = links.get(i);
		
			String href = link.getAttributeValue("href");
			
			if (href == null || href.isEmpty()) {
				continue;
			}
			
			href = href.toLowerCase();
			
			// Remove any links to concordion.css created by developers - which seems to be a common trend on projects I've worked on, although 
			// the link never seems to point to the correct location of concordion.css!
			if (href.contains("/concordion.css") || href.equals("concordion.css")) {
				head.removeChild(link);
				continue;
			}

			// Remove any links to custom css created by developers
			for (ResourceToCopy source : sourceFiles) {
				if (source.isStyleSheet()) {
					if (href.contains("/" + source.getName().toLowerCase()) || href.equals(source.getName().toLowerCase())) {
						head.removeChild(link);
						break;
					}
				}
			}
		}
    }
	
	private void removeExistingScripts(Element head) {
		Elements scripts = head.getChildElements("script");
				
		for (int i = scripts.size() - 1; i >= 0; i--) {
			Element script = scripts.get(i);
		
			String src = script.getAttributeValue("src");
			
			if (src == null || src.isEmpty()) {
				continue;
			}
			
			src = src.toLowerCase();
			
			// Remove any links to custom js created by developers
			for (ResourceToCopy source : sourceFiles) {
				if (source.isScript()) {
					if (src.contains("/" + source.getName().toLowerCase()) || src.equals(source.getName().toLowerCase())) {
						head.removeChild(script);
						break;
					}
				}
			}
		}
    }
}

