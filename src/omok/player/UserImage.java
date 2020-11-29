package omok.player;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import common.image.ImageUtil;

public class UserImage extends Canvas{
	private Image img;
	int width, height;
	
	public UserImage(int width, int height) {
		this.width=width;
		this.height = height;
		img = ImageUtil.getIcon(this.getClass(), "omok/player/defaultImg.jpg", width, height).getImage();
		setPreferredSize(new Dimension(200, 200));
	}
	
	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, this);
	}

	public Image getImg() {
		return img;
	}

	public void setImg(Image img) {
		this.img = img;
	}
}
