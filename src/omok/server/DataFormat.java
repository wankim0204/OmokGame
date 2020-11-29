package omok.server;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class DataFormat implements Serializable{
	private String fileName;
	private ImageIcon icon;
	public DataFormat(String fileName, ImageIcon icon) {
		this.fileName = fileName;
		this.icon = icon;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ImageIcon getIcon() {
		return icon;
	}
	
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
	
}
