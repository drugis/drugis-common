package org.drugis.common.gui;

import java.awt.Color;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.lang.StringEscapeUtils;
import org.drugis.common.ImageLoader;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.event.TaskFailedEvent;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

public class GUIHelper {

	public static class ErrorDialogExceptionHandler {
		public void handle(Throwable e) {
			e.printStackTrace();
			ErrorDialog.showDialog(e, "Unexpected error.");
		}
	}

	public static final ImageLoader IMAGELOADER = new ImageLoader("/org/drugis/common/gui/");

	public static final Color COLOR_WARNING = new Color(255, 214, 159);

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
	 * Center window on another window.
	 * @param window to center
	 * @deprecated Should use {@link Window#setLocationByPlatform(boolean)}
	 */
	@Deprecated
	public static void centerWindow(Window window, Window parent) {
		window.setLocationByPlatform(true);
	}

	public static String createToolTip(String text) {
		if (text != null && text.trim().length() > 0) {
			return wordWrap(text, true);
		}
		return null;
	}

	/**
	 * @param String to be transformed into human readable format
	 * @return human-readable string
	 * @see <a href="http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java">How do I convert CamelCase into human-readable names in Java</a>
	 */
	public static String humanize(String s) {
		return s.replaceAll(
				String.format("%s|%s|%s",
						"(?<=[A-Z])(?=[A-Z][a-z])",
						"(?<=[^A-Z])(?=[A-Z])",
						"(?<=[A-Za-z])(?=[^A-Za-z])"
						),
						" "
				);
	}

	private static void addTaskFailureListener() {
		ThreadHandler.getInstance().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getPropertyName().equals(ThreadHandler.PROPERTY_FAILED_TASK)) {
					final TaskFailedEvent taskEvent = (TaskFailedEvent) event.getNewValue();

					Runnable r = new Runnable() {
						public void run() {
							Throwable cause = taskEvent.getCause();
							ErrorDialog.showDialog(cause, taskEvent.getSource() + " failed");
						}
					};
					SwingUtilities.invokeLater(r);
				}
			}
		});
	}

	public static void startApplicationWithErrorHandler(Runnable main, String helpText) {
		System.setProperty("sun.awt.exception.handler", ErrorDialogExceptionHandler.class.getName());
		ErrorDialog.setHelpText(helpText);
		ThreadGroup threadGroup = new ThreadGroup("ExceptionGroup") {
			public void uncaughtException(Thread t, Throwable e) {
				e.printStackTrace();
				ErrorDialog.showDialog(e, "Unexpected error.");
			}
		};

		addTaskFailureListener();

		Thread mainThread = new Thread(threadGroup, main, "Main thread");
		mainThread.start();
	}

}
