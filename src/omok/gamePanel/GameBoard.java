package omok.gamePanel;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import omok.OmokFrame;
import omok.player.Player;
import omok.player.Rival;

public class GameBoard extends Canvas {
	OmokFrame omokFrame;
	GamePanel gamePanel;
	private int width = 0;
	private int height = 0;
	int index;
	private Color color;

	public GameBoard(OmokFrame omokFrame, GamePanel gamePanel, int index, Color color) {
		this.omokFrame = omokFrame;
		this.gamePanel = gamePanel;
		GameBoard gameBoard = this;
		this.index = index;
		this.color = color;

		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (gamePanel.isPlayer_turn()) {
					if (width == 0) {
						width = 30;
						height = 30;
						omokFrame.getCommandThread().send(index + "@@index");
						gamePanel.setPlayer_turn(false);
						gamePanel.setCount(-1);
						gamePanel.setSecond(20);
						checkWin();
						repaint();
					}
				} else if (!gamePanel.isStart()) {

				} else if (!gamePanel.isPlayer_turn()) {
					JOptionPane.showMessageDialog(gamePanel, "내 차례가 아닙니다.");
				}
			}
		});
		setBackground(Color.orange);
		setPreferredSize(new Dimension(40, 40));
		setVisible(true);
	}

	public void checkWin() {
		int[] check = { 1, 18, 19, 20 };
		int[] count = { 0, 0, 0, 0 };
		for (int a = 0; a < check.length; a++) {
			for (int i = 0; i < gamePanel.getOmokList().size(); i++) {
				if (index + (i * check[a]) > 360) {
					continue;
				}
				if (gamePanel.getOmokList().get(index + (i * check[a])).width == 30
						&& gamePanel.getOmokList().get(index + (i * check[a])).color == omokFrame.getPlayer()
								.getColor()) {
					count[a] += 1;
					if ((index + (i * check[a])) % 19 == 18 && check[a] != 19) {
						break;
					}
				} else {
					break;
				}
			}
			for (int i = 0; i < gamePanel.getOmokList().size(); i++) {
				if (index - (i * check[a]) < 0) {
					continue;
				}
				if (gamePanel.getOmokList().get(index - (i * check[a])).width == 30
						&& gamePanel.getOmokList().get(index - (i * check[a])).color == omokFrame.getPlayer()
								.getColor()) {
					count[a] += 1;
					if ((index - (i * check[a])) % 19 == 0 && check[a] != 19) {
						break;
					}
				} else {
					break;
				}
			}
			if (count[a] - 1 == 5) {
				gamePanel.win();
			}
		}
	}

	public void checkSamsam() {

	}

	public void paint(Graphics g) {
		if (index == 60 || index == 66 || index == 72 || index == 174 || index == 180 || index == 186 || index == 288
				|| index == 294 || index == 300) {
			g.setColor(Color.black);
			g.drawLine(25, 0, 25, 50);
			g.drawLine(0, 25, 50, 25);
			g.fillOval(20, 20, 10, 10);
		} else if (index > 0 && index < 18) {
			g.drawLine(25, 25, 25, 50);// 세로
			g.drawLine(0, 25, 50, 25);// 가로
		} else if (index == 0) {
			g.drawLine(25, 25, 25, 50);
			g.drawLine(25, 25, 50, 25);
		} else if (index == 18) {
			g.drawLine(25, 25, 25, 50);
			g.drawLine(0, 25, 25, 25);
		} else if (index > 342 && index < 360) {
			g.drawLine(25, 0, 25, 25);
			g.drawLine(0, 25, 50, 25);
		} else if (index == 342) {
			g.drawLine(25, 0, 25, 25);
			g.drawLine(25, 25, 50, 25);
		} else if (index == 360) {
			g.drawLine(25, 0, 25, 25);
			g.drawLine(0, 25, 25, 25);
		} else if (index % 19 == 0) {
			g.drawLine(25, 0, 25, 50);
			g.drawLine(25, 25, 50, 25);
		} else if (index % 19 == 18) {
			g.drawLine(25, 0, 25, 50);
			g.drawLine(0, 25, 25, 25);
		} else {
			g.drawLine(25, 0, 25, 50);
			g.drawLine(0, 25, 50, 25);
		}
		g.setColor(color);
		g.fillOval(10, 10, width, height);
		repaint();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
