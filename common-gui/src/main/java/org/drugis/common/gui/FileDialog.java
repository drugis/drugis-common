package org.drugis.common.gui;

import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public abstract class FileDialog {
	
	public static class Filter extends FileFilter
	{
		private final String d_description;
		private final List<String> d_extension;

		public Filter(String[] extension, String description) {
			d_extension = Arrays.asList(extension);
			d_description = description;
			
		}
		
		@Override
		public String getDescription() {
			return d_description;
		}
			
		private String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
	            ext = s.substring(i+1).toLowerCase();
	        }
	        return ext;
	    }
	    
		public String getPresentExtension() {
			return d_extension.get(0);
		}
		
		@Override
		public boolean accept(File f) {
	        if (f.isDirectory()) {
	            return true;
	        }
	        
	        String extension = getExtension(f);
	        if (extension != null) {
	        	if (d_extension.contains(extension)) {
	            	return true;
	        	} else {
	                return false;
	            }
	        }
	        return false;
		}
	}

	static File d_currentDirectory = null;
	protected JFileChooser d_fileChooser;
	private boolean d_lastSuccess = true;
    
    public static String fixExtension(String absPath, String ext) {
    	if (ext == null || ext.equals("")) {
    		return absPath;
    	}
    	if (absPath.toLowerCase().substring(absPath.lastIndexOf('.') + 1, absPath.length()).equals(ext)) {
    		return absPath;
    	}
    	return absPath + "." + ext;
    }
    
	protected void setLastSuccess(boolean lastSuccess) {
		d_lastSuccess = lastSuccess;
	}

    public FileDialog(Component frame, String extension, String description){
    	this(frame, new String [] {extension}, new String [] {description});
    }
	
	public FileDialog(Component frame, String [] extension, String [] description) {
		this(frame, wrapExtensions(extension), description);
	}
	
	private static String[][] wrapExtensions(String[] extension) {
		int m = extension.length;
		String [][] output = new String[m][1];
		for (int i = 0; i < m ; ++i) {
			output[i][0] = extension[i];
		}
		return output;
	}

	public FileDialog(Component frame, String [][] extension, String [] description) {
		
		d_fileChooser = new JFileChooser();
		Filter defaultFilter = null;
		for(int i=0; i< extension.length; i++) {
			Filter filter = new Filter(extension[i], description[i]);
			d_fileChooser.addChoosableFileFilter(filter);
			if (i == 0) {
				defaultFilter = filter;
			}
		}
		d_fileChooser.setFileFilter(defaultFilter);
		if (d_currentDirectory != null)
			d_fileChooser.setCurrentDirectory(d_currentDirectory);
	}
	
	protected void handleFileDialogResult(Component frame, int returnVal, String message) {
		d_currentDirectory = d_fileChooser.getCurrentDirectory();
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String path = getPath();
			try {
				doAction(path, getExtension());
			} catch (Exception e1) {
				d_lastSuccess = false;
				JOptionPane.showMessageDialog(frame, message + "\n" +
						d_fileChooser.getSelectedFile().getAbsolutePath());
				e1.printStackTrace();
			}
		}
	}

	protected abstract String getPath();
	
	public boolean getLastSuccess() {
		return d_lastSuccess;
	}

	protected String getExtension() {
		if(d_fileChooser.getFileFilter() instanceof Filter) {
			return ((Filter) d_fileChooser.getFileFilter()).getPresentExtension();
		} else {
			return "";
		}
	}

	public abstract void doAction(String path, String extension);
	
}
