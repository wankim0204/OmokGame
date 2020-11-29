package omok.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import common.image.ImageUtil;
import omok.OmokFrame;

public class RegistForm extends JPanel {
	JPanel p_north, p_center, p_south;
	JTextField t_id;
	JButton bt_checkId;
	JPasswordField t_pass;
	JTextField t_nickname;
	JButton bt_checkNickname;
	JButton bt_regist;
	JButton bt_cancle;
	OmokFrame omokFrame;
	private boolean checkId;
	private boolean checkNickname;

	public RegistForm(OmokFrame omokFrame) {
		this.omokFrame = omokFrame;
		// 생성
		p_north = new JPanel() {
			public void paint(Graphics g) {
				Image img = ImageUtil.getIcon(this.getClass(), "omok/omok.jpg", 300, 300).getImage();
				g.drawImage(img, 0, 0, this);
			}
		};
			
		p_center = new JPanel();
		p_south = new JPanel();
		t_id = new JTextField("계정(영문, 숫자 6자 이상, 10자 이하)", 18);
		bt_checkId = new JButton("중복확인");
		t_pass = new JPasswordField("비밀번호(!, @제외, 8자 이상, 20자 이하)", 26);
		t_nickname = new JTextField("별명(영문, 한글, 숫자 2자 이상, 10자 이하)", 18);
		bt_checkNickname = new JButton("중복확인");
		bt_regist = new JButton("계정 생성");
		bt_cancle = new JButton("취소");

		// 스타일
		bt_checkId.setForeground(Color.white);
		bt_checkId.setBackground(Color.red);
		bt_checkNickname.setForeground(Color.white);
		bt_checkNickname.setBackground(Color.red);

		p_north.setPreferredSize(new Dimension(300, 300));

		// 조립
		setLayout(new BorderLayout());
		p_north.setLayout(new BorderLayout());
		add(p_north, BorderLayout.NORTH);
		p_center.add(t_id);
		p_center.add(bt_checkId);
		p_center.add(t_pass);
		p_center.add(t_nickname);
		p_center.add(bt_checkNickname);
		add(p_center);
		p_south.add(bt_regist);
		p_south.add(bt_cancle);
		add(p_south, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(300, 450));
		setVisible(false);
				
		t_pass.setEchoChar((char) 0);
		t_id.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				if (t_id.getText().equals("")) {
					t_id.setText("계정(영문, 숫자 6자 이상, 10자 이하)");
				} else {
					t_id.setText(t_id.getText());
				}
			}

			public void focusGained(FocusEvent e) {
				if (t_id.getText().equals("계정(영문, 숫자 6자 이상, 10자 이하)")) {
					t_id.setText("");
				} else {
					t_id.setText(t_id.getText());
				}
			}
		});
		t_pass.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				if (new String(t_pass.getPassword()).equals("")) {
					t_pass.setEchoChar((char) 0);
					t_pass.setText("비밀번호(!, @제외, 8자 이상, 20자 이하)");
				} else {
					t_pass.setText(new String(t_pass.getPassword()));
				}
			}

			public void focusGained(FocusEvent e) {
				if (new String(t_pass.getPassword()).equals("비밀번호(!, @제외, 8자 이상, 20자 이하)")) {
					t_pass.setEchoChar('*');
					t_pass.setText("");
				} else {
					t_pass.setEchoChar('*');
					t_pass.setText(new String(t_pass.getPassword()));
				}
			}
		});

		t_nickname.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				if (t_nickname.getText().equals("")) {
					t_nickname.setText("별명(영문, 한글, 숫자 2자 이상, 10자 이하)");
				} else {
					t_nickname.setText(t_nickname.getText());
				}
			}

			public void focusGained(FocusEvent e) {
				if (t_nickname.getText().equals("별명(영문, 한글, 숫자 2자 이상, 10자 이하)")) {
					t_nickname.setText("");
				} else {
					t_nickname.setText(t_nickname.getText());
				}
			}
		});
		
		t_id.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				checkId = false;
				bt_checkId.setBackground(Color.red);
			}
		});

		bt_checkId.addActionListener((e) -> {
			String id = t_id.getText();
			for (int i = 0; i < id.toCharArray().length; i++) {
				char idChar = id.toCharArray()[i];
				if (idChar > 47 && idChar < 58 || idChar > 64 && idChar < 91 || idChar > 96 && idChar < 123) {
					if (i == id.toCharArray().length - 1) {
						omokFrame.getCommandThread().send(t_id.getText() + "@@checkIdRegist");
					}
				} else {
					JOptionPane.showMessageDialog(this, "계정에 올바르지 못한 문자가 사용되었습니다. (영문, 숫자만 사용)");
					t_id.requestFocusInWindow();
					break;
				}
			}
		});

		t_nickname.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				checkNickname = false;
				bt_checkNickname.setBackground(Color.red);
			}
		});

		bt_checkNickname.addActionListener((e) -> {
			String nickname = t_nickname.getText();
			for (int a = 0; a < nickname.toCharArray().length; a++) {
				char nicknameChar = nickname.toCharArray()[a];
				if (nicknameChar > 64 && nicknameChar < 91 || nicknameChar > 96 && nicknameChar < 123 || nicknameChar > 44031 && nicknameChar < 55204 || nicknameChar > 47 && nicknameChar < 58) {
					if (a == nickname.toCharArray().length - 1) {
						omokFrame.getCommandThread().send(t_nickname.getText() + "@@checkNicknameRegist");
					}
				} else {
					JOptionPane.showMessageDialog(this, "별명에 올바르지 못한 문자가 사용되었습니다. (영문, 한글, 숫자만 사용)");
					t_id.requestFocusInWindow();
					break;
				}
			}
		});

		bt_regist.addActionListener((e) -> {
			if (checkId && checkNickname) {
				regist();
			} else if (!checkId) {
				JOptionPane.showMessageDialog(this, "계정 중복확인을 해주세요.");
			} else if (!checkNickname) {
				JOptionPane.showMessageDialog(this, "별명 중복확인을 해주세요.");
			}
		});

		bt_cancle.addActionListener((e) -> {
			omokFrame.setPage(omokFrame.LOGINPANEL);
			omokFrame.pack();
		});
	}

	public void regist() {
		String id = t_id.getText();
		String pass = new String(t_pass.getPassword());
		String nickname = t_nickname.getText();
		if (id.equals("계정(영문, 숫자 6자 이상, 10자 이하)")) {
			JOptionPane.showMessageDialog(this, "계정을 입력하세요.");
			t_id.requestFocusInWindow();
		} else if (pass.equals("비밀번호(!, @제외, 8자 이상, 20자 이하)")) {
			JOptionPane.showMessageDialog(this, "비밀번호를 입력하세요.");
			t_pass.requestFocusInWindow();
		} else if (pass.equals("별명(영문, 한글, 숫자 2자 이상, 10자 이하)")) {
			JOptionPane.showMessageDialog(this, "별명을 입력하세요.");
			t_nickname.requestFocusInWindow();
		} else if (id.toCharArray().length > 10 || id.toCharArray().length < 6) {
			JOptionPane.showMessageDialog(this, "계정 글자수를 확인하세요. (영문, 숫자 6자 이상, 10자 이하)");
			t_id.requestFocusInWindow();
			t_pass.setText(pass);
			t_nickname.setText(nickname);
		} else if (pass.toCharArray().length < 8 || pass.toCharArray().length > 20) {
			JOptionPane.showMessageDialog(this, "비밀번호를 글자수를 확인하세요. (8자 이상, 20자 이하)");
			t_pass.requestFocusInWindow();
		}else if (nickname.toCharArray().length < 2 || nickname.toCharArray().length > 10) {
			JOptionPane.showMessageDialog(this, "별명 글자수를 확인하세요. (2자 이상 10자 이하)");
			t_nickname.requestFocusInWindow();
		} else {
			for (int n = 0; n < pass.toCharArray().length; n++) {
				if (pass.toCharArray()[n] == 33 || pass.toCharArray()[n] == 64) {
					JOptionPane.showMessageDialog(this, "비밀번호에 올바르지 못한 문자가 사용되었습니다. (!, @제외)");
					t_pass.requestFocusInWindow();
					break;
				}else if((n+1)==pass.toCharArray().length){
					omokFrame.getCommandThread().send(id + "!" + pass + "!" + nickname + "@@regist");
					t_id.setText("계정(영문, 숫자 6자 이상, 10자 이하)");
					t_pass.setEchoChar((char) 0);
					t_pass.setText("비밀번호(!, @제외, 8자 이상, 20자 이하)");
					t_nickname.setText("별명(영문, 한글, 숫자 2자 이상, 10자 이하)");
					checkId = false;
					bt_checkId.setBackground(Color.red);
					checkNickname = false;
					bt_checkNickname.setBackground(Color.red);
				}	
			}			
		}
	}

	public void setCheckId(boolean checkId) {
		this.checkId = checkId;
	}

	public void setCheckNickname(boolean checkNickname) {
		this.checkNickname = checkNickname;
	}

	public void setIdColor() {
		bt_checkId.setBackground(Color.green);
	}

	public void setNicknameColor() {
		bt_checkNickname.setBackground(Color.green);
	}

}
