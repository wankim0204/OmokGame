package omok.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import common.image.ImageUtil;
import omok.OmokFrame;
import omok.gamePanel.GameBoard;
import omok.gamePanel.GamePanel;
import omok.waitRoom.WaitRoom;
import oracle.net.aso.g;

public class ChangeInfo extends JFrame {
	JPanel p_north, p_img, p_center, p_south;
	JButton bt_searchImg, bt_defaultImg, bt_applyImg;
	UserImage userImage;
	JFileChooser chooser;
	JTextField t_id;
	JPasswordField t_pass;
	JTextField t_nickname;
	JLabel la_id, la_pass, la_nickname;
	JButton bt_checkNickname;
	JButton bt_change;
	JButton bt_cancle;
	OmokFrame omokFrame;
	private boolean checkNickname;
	Image img;
	String imgPath;
	ImageIcon icon = ImageUtil.getIcon(this.getClass(), "omok/player/defaultImg.jpg", 200, 200);
	
	public ChangeInfo(OmokFrame omokFrame) {
		ChangeInfo changeInfo = this;
		this.omokFrame = omokFrame;
		// ����
		p_north = new JPanel();
		p_img = new JPanel();
		userImage = new UserImage(200, 200);
		bt_searchImg = new JButton("�̹��� ã��");
		bt_defaultImg = new JButton("�⺻ �̹�����");
		bt_applyImg = new JButton("�̹��� ����");
		chooser = new JFileChooser();
		
		p_center = new JPanel();
		t_pass = new JPasswordField(20);
		la_pass = new JLabel("��й�ȣ(8�� �̻�, 20�� ����)");
		t_nickname = new JTextField(12);
		la_nickname = new JLabel("����(2�� �̻�, 10�� ����)");
		bt_checkNickname = new JButton("�ߺ�Ȯ��");

		p_south = new JPanel();
		bt_change = new JButton("��й�ȣ / ���� ����");
		bt_cancle = new JButton("���");

		// ��Ÿ��
		p_north.setPreferredSize(new Dimension(300, 280));
		p_img.setPreferredSize(new Dimension(200, 200));
		p_south.setPreferredSize(new Dimension(300, 50));
		
		la_pass.setHorizontalAlignment(SwingConstants.CENTER);
		la_nickname.setHorizontalAlignment(SwingConstants.CENTER);
		bt_checkNickname.setForeground(Color.white);
		bt_checkNickname.setBackground(Color.red);

		// ����
		setLayout(new BorderLayout());
		p_img.add(userImage);
		p_north.add(p_img);
		p_north.add(bt_searchImg);
		p_north.add(bt_defaultImg);
		p_north.add(bt_applyImg);
		
		p_center.add(la_pass);
		p_center.add(t_pass);
		p_center.add(la_nickname);
		p_center.add(t_nickname);
		p_center.add(bt_checkNickname);

		p_south.add(bt_change);
		p_south.add(bt_cancle);

		add(p_north, BorderLayout.NORTH);
		add(p_center);
		add(p_south, BorderLayout.SOUTH);

		setSize(300, 500);
		setVisible(true);
		setLocationRelativeTo(omokFrame);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		bt_searchImg.addActionListener((e)->{
			int result = chooser.showOpenDialog(this);
			if(result == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				imgPath = file.getAbsolutePath();
				
				icon = new ImageIcon(imgPath);
				img = icon.getImage();
				img = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
				userImage.setImg(img);
				userImage.repaint();
			}
		});

		bt_defaultImg.addActionListener((e)->{
			img = ImageUtil.getIcon(this.getClass(), "omok/player/defaultImg.jpg", 200, 200).getImage();
			icon = ImageUtil.getIcon(this.getClass(), "omok/player/defaultImg.jpg", 200, 200);
			userImage.setImg(img);
			userImage.repaint();
		});
		
		bt_applyImg.addActionListener((e)->{
			if(JOptionPane.showConfirmDialog(this, "�̹����� �ٲٽðڽ��ϱ�?")==JOptionPane.OK_OPTION) {
				omokFrame.getImgThread().sendImg(omokFrame.getPlayer().getId(), icon);
				WaitRoom waitRoom = (WaitRoom) omokFrame.getPanel(omokFrame.WAITROOM);
				Image img = icon.getImage();
				img = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
				waitRoom.getUserImage().setImg(img);
				waitRoom.getUserImage().repaint();
				GamePanel gamePanel = (GamePanel) omokFrame.getPanel(omokFrame.GAMEPANEL);
				img = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
				gamePanel.getImg1().setImg(img);
				gamePanel.getImg1().repaint();
			}
		});
		
		t_nickname.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				checkNickname = false;
				bt_checkNickname.setBackground(Color.red);
			}
		});
				
		bt_checkNickname.addActionListener((e) -> {
			omokFrame.getCommandThread().send(t_nickname.getText() + "@@checkNickname");
		});

		bt_change.addActionListener((e) -> {
			String id = omokFrame.getPlayer().getId();
			String password = new String(t_pass.getPassword());
			String nickname = t_nickname.getText();
			if (password.toCharArray().length == 0 && nickname.toCharArray().length == 0) {
				JOptionPane.showMessageDialog(this, "������ ������ �Է����ּ���.");
			} else if (password.toCharArray().length > 0 && password.toCharArray().length < 8
					|| password.toCharArray().length > 20) {
				JOptionPane.showMessageDialog(this, "�Էµ� ��й�ȣ�� ���� Ȯ���ϼ���. (8�� �̻�, 20�� ����)");
				t_pass.requestFocusInWindow();
			} else if (nickname.toCharArray().length == 1 || nickname.toCharArray().length > 10) {
				JOptionPane.showMessageDialog(this, "�Էµ� ������ ���� Ȯ���ϼ���. (2�� �̻�, 10�� ����)");
				t_nickname.requestFocusInWindow();
			} else if (password.toCharArray().length != 0 && nickname.toCharArray().length == 0) {
				if (JOptionPane.showConfirmDialog(this, "��й�ȣ�� �����Ͻðڽ��ϱ�?") == JOptionPane.OK_OPTION) {
					omokFrame.getCommandThread().send(id + "!" + password + "@@changePassword");
					t_pass.setText("");
				}
			} else if (password.toCharArray().length == 0 && nickname.toCharArray().length != 0) {
				if (checkNickname) {
					if (JOptionPane.showConfirmDialog(this, "������ �����Ͻðڽ��ϱ�?") == JOptionPane.OK_OPTION) {
						omokFrame.getCommandThread().send(id + "!" + nickname + "@@changeNickname");
					}
				} else {
					JOptionPane.showMessageDialog(this, "���� �ߺ�Ȯ���� ���ּ���.");
				}
			} else {
				if (checkNickname) {
					if (JOptionPane.showConfirmDialog(this, "��й�ȣ�� ������ �����Ͻðڽ��ϱ�?") == JOptionPane.OK_OPTION) {
						omokFrame.getCommandThread().send(id + "!" + password + "!" + nickname + "@@changeAll");
						t_pass.setText("");
					}
				} else {
					JOptionPane.showMessageDialog(this, "���� �ߺ�Ȯ���� ���ּ���.");
				}
			}
		});

		bt_cancle.addActionListener((e)->{
			WaitRoom waitRoom = (WaitRoom) omokFrame.getPanel(omokFrame.WAITROOM);
			//waitRoom.getP_img().repaint();
			this.setVisible(false);
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				WaitRoom waitRoom = (WaitRoom) omokFrame.getPanel(omokFrame.WAITROOM);
				//waitRoom.getP_img().repaint();
				changeInfo.setVisible(false);
			}
		});
	}
	
	public void setCheckNickname(boolean checkNickname) {
		this.checkNickname = checkNickname;
	}

	public void setNicknameColor() {
		bt_checkNickname.setBackground(Color.green);
	}
}
