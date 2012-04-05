package org.drugis.common;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class ImageLoader {
	
	private Map<String, ImageIcon> d_icons;
	private String d_imagePath;
	
	public ImageLoader(String imagePath) { 
		d_imagePath = imagePath;
		d_icons = new HashMap<String, ImageIcon>();
	}
	
	public ImageIcon getIcon(String name) {
		if (d_icons.containsKey(name)) {
			return d_icons.get(name);
		} else {
		    java.net.URL imgURL = ImageLoader.class.getResource(deriveGfxPath(name));
		    if (imgURL == null) {
		    	System.err.println("Error loading image " + deriveGfxPath(name));
		    	return null;
		    }
		    ImageIcon icon = new ImageIcon(imgURL);
	        d_icons.put(name, icon);
	        return icon;
		}
	}

	public Image getImage(String name) {
		return getIcon(name) == null ? null : getIcon(name).getImage();
	}
	
	private String deriveGfxPath(String name) {
		return d_imagePath + name;
	}
}
