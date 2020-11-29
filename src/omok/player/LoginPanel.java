package omok.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import common.image.ImageUtil;
import omok.OmokFrame;

public class LoginPanel extends JPanel {
	JPanel p_north;
	JPanel p_center;
	JPanel p_centerC, p_centerE;
	JPanel p_south;
	JLabel la_id;
	JLabel la_pass;
	JTextField t_id;
	JPasswordField t_pass;
	JButton bt_login;
	JButton bt_regist;
	JButton bt_delId;
	JButton bt_rank;
	OmokFrame omokFrame;

	public LoginPanel(OmokFrame omokFrame) {
		this.omokFrame = omokFrame;
		OmokFrame of = this.omokFrame;
		p_north = new JPanel() {
			public void paint(Graphics g) {
				Image img = ImageUtil.getIcon(this.getClass(), "omok/omok.jpg", 300, 300).getImage();
				g.drawImage(img, 0, 0, this);
			}
		};
		// ����
		p_center = new JPanel();
		p_centerC = new JPanel();
		p_centerE = new JPanel();
		p_south = new JPanel();
		la_id = new JLabel("����");
		t_id = new JTextField("", 15);
		la_pass = new JLabel("��й�ȣ");
		t_pass = new JPasswordField("", 13);
		bt_login = new JButton("�α���");
		bt_regist = new JButton("��������");
		bt_delId = new JButton("��������");
		bt_rank = new JButton("����");

		// ����
		p_center.setLayout(new BorderLayout());
		p_centerE.setLayout(new BorderLayout());
		p_center.add(p_centerC);
		p_center.add(p_centerE, BorderLayout.EAST);
		p_centerC.add(la_id);
		p_centerC.add(t_id);
		p_centerC.add(la_pass);
		p_centerC.add(t_pass);
		p_centerE.add(bt_login, BorderLayout.NORTH);
		p_centerE.add(bt_delId, BorderLayout.SOUTH);
		p_south.add(bt_regist);
		p_south.add(bt_rank);

		setLayout(new BorderLayout());
		add(p_north, BorderLayout.NORTH);
		add(p_center);
		add(p_south, BorderLayout.SOUTH);

		// ��Ÿ��
		p_north.setPreferredSize(new Dimension(300, 300));
		
		t_id.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (t_id.getText().equals("")) {
						JOptionPane.showMessageDialog(of, "������ �Է��ϼ���.");
					} else if (new String(t_pass.getPassword()).equals("")) {
						JOptionPane.showMessageDialog(of, "��й�ȣ�� �Է��ϼ���.");
					} else {
						login(t_id.getText(), new String(t_pass.getPassword()));
					}
				}
			}
		});
		
		t_pass.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (t_id.getText().equals("")) {
						JOptionPane.showMessageDialog(of, "������ �Է��ϼ���.");
					} else if (new String(t_pass.getPassword()).equals("")) {
						JOptionPane.showMessageDialog(of, "��й�ȣ�� �Է��ϼ���.");
					} else {
						login(t_id.getText(), new String(t_pass.getPassword()));
					}
				}
			}
		});
		
		bt_login.addActionListener((e) -> {
			if (t_id.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "������ �Է��ϼ���.");
			} else if (new String(t_pass.getPassword()).equals("")) {
				JOptionPane.showMessageDialog(this, "��й�ȣ�� �Է��ϼ���.");
			} else {
				login(t_id.getText(), new String(t_pass.getPassword()));
			}
		});

		bt_regist.addActionListener((e) -> {
			omokFrame.setPage(omokFrame.REGISTFORM);
			t_id.setText("");
			t_pass.setText("");
			RegistForm rf = (RegistForm) omokFrame.getPanel(omokFrame.REGISTFORM);
			omokFrame.pack();
			//omokFrame.setLocationRelativeTo(null);
		});

		bt_delId.addActionListener((e) -> {
			if (t_id.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "������ �Է��ϼ���.");
				t_id.requestFocusInWindow();
			} else if (new String(t_pass.getPassword()).equals("")) {
				JOptionPane.showMessageDialog(this, "��й�ȣ�� �Է��ϼ���.");
				t_id.requestFocusInWindow();
			} else if (JOptionPane.showConfirmDialog(this, "�ش� ������ �����Ͻðڽ��ϱ�?") == JOptionPane.OK_OPTION) {
				delId(t_id.getText(), new String(t_pass.getPassword()));
			}
		});
		
		bt_rank.addActionListener((e)->{
			omokFrame.getCommandThread().send("@@rank");
		});

		setPreferredSize(new Dimension(300, 400));
		setVisible(true);
	}

	public void login(String id, String password) {
		String msg = id + "!" + password+"@@login";
		omokFrame.getCommandThread().send(msg);
		t_pass.setText("");
	}

	public void delId(String id, String password) {
		String msg = id + "!" + password+"@@delete";
		omokFrame.getCommandThread().send(msg);
		t_id.setText("");
		t_pass.setText("");
	}
}
