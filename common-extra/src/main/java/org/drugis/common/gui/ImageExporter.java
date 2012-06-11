package org.drugis.common.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.jfree.chart.JFreeChart;
import org.jgraph.JGraph;

public class ImageExporter {
	
	public static void writeImage(Component frame, final JComponent p, final int width, final int height) {
		String [] extensions = {"png"};
		String [] descriptions = {"PNG files"};
		FileSaveDialog dialog = new FileSaveDialog(frame, extensions, descriptions) {
			@Override
			public void doAction(String path, String extension) {
				if (extension.equals("png"))
					writePNG(path, p, width, height);
				else
					throw new IllegalArgumentException("Unknown extension " + extension);
			}
		};
		dialog.saveActions();
	}
	
	public interface DrawCommand<T, C extends Graphics2D> {
		public void draw(T toDraw, C canvas, Dimension dim);
	}
	
	protected static <T> void writePNG(String path, DrawCommand<T, Graphics2D> drawer, T toDraw, Dimension dim) {
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage bufferedImage = config.createCompatibleImage(dim.width, dim.height, Transparency.OPAQUE);

		Graphics2D canvas = bufferedImage.createGraphics();
		canvas.setBackground(Color.WHITE);
		canvas.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		canvas.setColor(Color.BLACK);
		drawer.draw(toDraw, canvas, dim);
		
		writePNG(path, bufferedImage);
	}
	

	protected static void writePNG(String path, JComponent p, int width, int height) {
		writePNG(path,  new DrawCommand<JComponent, Graphics2D>() {
			public void draw(JComponent toDraw, Graphics2D canvas, Dimension dim) {
				toDraw.paint(canvas);
			}
		}, p, new Dimension(width, height));
	}


	public static void writeImage(Component frame, final JGraph p, final int width, final int height) {
		String [] extensions = {"png"};
		String [] descriptions = {"PNG files"};
		FileSaveDialog dialog = new FileSaveDialog(frame, extensions, descriptions) {
			@Override
			public void doAction(String path, String extension) {
				if (extension.equals("png"))
					writePNG(path, p, width, height);
				else
					throw new IllegalArgumentException("Unknown extension " + extension);
			}
		};
		dialog.saveActions();
	}

	public static void writeImage(Component frame, final JFreeChart p, final int width, final int height) {
		String [] extensions = {"png"};
		String [] descriptions = {"PNG files"};
		FileSaveDialog dialog = new FileSaveDialog(frame, extensions, descriptions) {
			@Override
			public void doAction(String path, String extension) {
				if (extension.equals("png"))
					writePNG(path, p, width, height);
				else
					throw new IllegalArgumentException("Unknown extension " + extension);
			}
		};
		dialog.saveActions();
	}

	protected static void writePNG(String path, JFreeChart chart, int width, int height) {
		writePNG(path,  new DrawCommand<JFreeChart, Graphics2D>() {
			public void draw(JFreeChart toDraw, Graphics2D canvas, Dimension dim) {
				toDraw.draw(canvas, new Rectangle(dim.width, dim.height));
			}
		}, chart, new Dimension(width, height));
	}
	
	private static void writePNG(String filename, BufferedImage b) {
		try {
			ImageIO.write(b, "png", new File(filename));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
