package org.drugis.common.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.UIManager;

import org.apache.commons.lang.StringEscapeUtils;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

public class GUIHelper {

	public static void initializeLookAndFeel() {
		try {
			String osName = System.getProperty("os.name");
			
			if (osName.startsWith("Windows")) {
				UIManager.setLookAndFeel(new WindowsLookAndFeel());
			} else  if (osName.startsWith("Mac")) {
				// do nothing, use the Mac Aqua L&f
			} else {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				} catch (Exception e) {
					UIManager.setLookAndFeel(new PlasticLookAndFeel());
				}
			}
		} catch (Exception e) {
			// Likely the Looks library is not in the class path; ignore.
		}
	}
	
	private static final Pattern wrapRE = Pattern.compile(".{0,79}(?:\\S(?:-| |$)|$)");
	
	private static String[] makeParts(String str) {
		if (str != null && str != "") {
		    List<String> list = new LinkedList<String>();
		    Matcher m = wrapRE.matcher(str);
		    while (m.find()) list.add(m.group());
		    return list.toArray(new String[]{});
		}
		return new String[] {};
	}
	
	public static String wordWrap(String input, boolean surround) {
		String[] arr = makeParts(StringEscapeUtils.escapeHtml(input));
		String resStr = "";
		for (String s : arr) {
			if (s.length() < 1) {
				continue;
			}
			if(!resStr.equals("")) {
				resStr = resStr + "<br>";
			}
			resStr += s;
		}
		
		if (surround) {
			return "<html>" + resStr + "</html>";
		}
		return resStr;
	}

	/**
	 * Center window on screen.
	 * @param window to center
	 */
	public static void centerWindow(Window window) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension fsize = window.getSize();
		int xLoc = (int) ((screenSize.getWidth() / 2) - (fsize.getWidth() / 2));
		int yLoc = (int) ((screenSize.getHeight() / 2) - (fsize.getHeight() / 2));
		window.setLocation(new Point(xLoc, yLoc));
	}

	/**
	 * Center window on another window.
	 * @param window to center
	 */
	public static void centerWindow(Window window, Window parent) {
		Point parentLocation = parent.getLocation();
		Dimension parentDim = parent.getSize();
		Dimension fsize = window.getSize();
		int xLoc = (int) parentLocation.getX() + (int) ((parentDim.getWidth() / 2) - (fsize.getWidth() / 2));
		int yLoc = (int) parentLocation.getY() + (int) ((parentDim.getHeight() / 2) - (fsize.getHeight() / 2));
		window.setLocation(new Point(xLoc, yLoc));
	}
	
	public static String createToolTip(String text) {
		if (text != null && text.trim().length() > 0) {
			return wordWrap(text, true);
		}
		return null;
	}	
}
