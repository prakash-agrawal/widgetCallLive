package com.agilecrm.image;

import java.io.InputStream;
import java.net.URL;

public class ImageClass {

	
	public InputStream getImageAsStream(String name){
		return getClass().getResourceAsStream(name);
	}
	
	
	public URL getImageAsUrl(String name){
		return getClass().getResource(name);
	}
	
	
}
