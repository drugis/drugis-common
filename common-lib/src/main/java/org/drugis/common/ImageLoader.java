package org.drugis.common;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class ImageLoader {
	
	static private Map<String, ImageIcon> icons;
	static private String imagePath;
	
	public static void setImagePath(String _imagePath) {
		imagePath = _imagePath;
		icons = new HashMap<String, ImageIcon>();
	}
	
	public static ImageIcon getIcon(String name) {
		if (icons.containsKey(name)) {
			return icons.get(name);
		} else {
		    java.net.URL imgURL = ImageLoader.class.getResource(deriveGfxPath(name));
		    if (imgURL == null) {
		    	System.err.println("Error loading image " + deriveGfxPath(name));
		    	return null;
		    }
		    ImageIcon icon = new ImageIcon(imgURL);
	        icons.put(name, icon);
	        return icon;
		}
	}

	public static Image getImage(String name) {
		return getIcon(name) == null ? null : getIcon(name).getImage();
	}
	
	private static String deriveGfxPath(String name) {
		return imagePath + name;
	}
}
