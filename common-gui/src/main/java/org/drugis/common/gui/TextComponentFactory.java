package org.drugis.common.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.drugis.common.BrowserLaunch;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.util.DefaultUnitConverter;

public class TextComponentFactory {

	public static JScrollPane createTextArea(ValueModel model, boolean editable) {
		return createTextArea(model, editable, true);
	}

	public static JScrollPane createTextArea(ValueModel model, boolean editable, boolean commitOnFocusLost) {
		JTextArea area = BasicComponentFactory.createTextArea(model, commitOnFocusLost);
		dontStealTabKey(area);
		area.setEditable(editable);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		if (!editable) {
			area.setUI(new javax.swing.plaf.basic.BasicTextAreaUI());
		}

		JScrollPane pane = new JScrollPane(area);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.setPreferredSize(new Dimension(
				DefaultUnitConverter.getInstance().dialogUnitXAsPixel(200, area),
				DefaultUnitConverter.getInstance().dialogUnitYAsPixel(50, area)));
		return pane;
	}


	private static void dontStealTabKey(final JTextArea area) {
		area.setFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				new JLabel().getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		area.setFocusTraversalKeys(
				KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				new JLabel().getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
	}

	public static JComponent createTextPane(String html, boolean scrollable) {
		JTextPane area = new JTextPane();
		area.setContentType("text/html");
		area.setText(html);
		area.setCaretPosition(0);
		area.setEditable(false);
		addHyperlinkListener(area);

		if (!scrollable) {
			area.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
					BorderFactory.createEmptyBorder(4,4,4,4)));
			return area;
		}
		return putTextPaneInScrollPane(area);
	}


	public static JComponent putTextPaneInScrollPane(JTextPane area) {
		JScrollPane pane = new JScrollPane(area);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.setPreferredSize(defaultTextPaneDimension(area));

		pane.setWheelScrollingEnabled(true);
		pane.getVerticalScrollBar().setValue(0);

		return pane;
	}

	public static JTextPane createTextPaneWithHyperlinks(String str, Color bg, boolean opaque) {
		JTextPane pane = new JTextPane();
		pane.setBackground(bg);
		pane.setContentType("text/html");
		pane.setText(str);
		pane.setEditable(false);
		pane.setOpaque(opaque);
		addHyperlinkListener(pane);
		return pane;
	}

	private static void addHyperlinkListener(JTextPane pane) {
		pane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
					BrowserLaunch.openURL(e.getURL().toExternalForm());
				}
			}
		});
	}

	public static Dimension defaultTextPaneDimension(JTextPane area) {
		return textPaneDimension(area, 230, 50);
	}

	public static Dimension textPaneDimension(JTextPane area, int dluX, int dluY) {
		return new Dimension(
				DefaultUnitConverter.getInstance().dialogUnitXAsPixel(dluX, area),
				DefaultUnitConverter.getInstance().dialogUnitYAsPixel(dluY, area));
	}

}
