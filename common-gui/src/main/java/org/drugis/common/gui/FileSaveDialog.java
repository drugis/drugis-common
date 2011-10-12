package org.drugis.common.gui;

import java.awt.Component;

abstract public class FileSaveDialog extends FileDialog {
	
	public FileSaveDialog(Component frame, String extension, String description){
    	this(frame, new String [] {extension}, new String [] {description});
    }

	public FileSaveDialog(Component frame, String [] extension, String [] description) {
		super(frame, extension, description);
	}

	public void saveActions() {
		String message = "Couldn't save file ";
		
		int returnValue = d_fileChooser.showSaveDialog(d_frame);
		handleFileDialogResult(returnValue, message);
	}

	@Override
	protected String getPath() {
		return fixExtension(d_fileChooser.getSelectedFile().getAbsolutePath(),getExtension());
	}
}