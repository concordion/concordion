package org.concordion.internal;

import java.io.File;

import org.concordion.api.ConcordionResources.InsertType;

public class ResourceToCopy {
	protected String fileName;
	public InsertType insertType;
	
	public ResourceToCopy(String sourceFile, InsertType insertType) {
		this.fileName = sourceFile;
		this.insertType = insertType;
		
		if (fileName.startsWith(String.valueOf(File.separatorChar))) {
			fileName = fileName.substring(1);
		}				
		
		fileName = fileName.replace("\\", "/");
	}
	
	public String getResourceName() {
		return "/" + fileName;
	}
	
	public String getName() {
		return new File(fileName).getName();
	}
	
	public boolean isStyleSheet() {
		return fileName.endsWith(".css");
	}
	
	public boolean isScript() {
		return fileName.endsWith(".js");
	}
}