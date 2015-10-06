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
	}

	// TODO: Do we need to be able to turn this off?  There is a possibility that will delete something already added
	//		 Actually, could probably replace all of this with something that deletes all links where href does not start with "http" 
	//		 and do something similar for JavaScript where all scripts where src does not start with "http" 
	private void removeExistingStyling(Element head) {
		Elements links = head.getChildElements("link");
				
		for (int i = links.size() - 1; i >= 0; i--) {
			Element link = links.get(i);
		
			String href = link.getAttributeValue("href");
			
			if (href == null) {
				continue;
			}
			
			href = href.toLowerCase();
			
			// Remove any links to concordion.css created by developers
			if (href.contains("/concordion.css") || href.equals("concordion.css")) {
				head.removeChild(link);
				continue;
			}

			// Remove any links to custom css/js created by developers
			for (ResourceToCopy source : sourceFiles) {
				if (source.fileName.endsWith(".css") || source.fileName.endsWith(".js")) {
					if (href.contains("/" + source.getName().toLowerCase()) || href.equals(source.getName().toLowerCase())) {
						head.removeChild(link);
						break;
					}
				}
			}
		}
    }
}

