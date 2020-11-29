package omok.waitRoom;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Paint;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import common.image.ImageUtil;
import omok.OmokFrame;
import omok.gamePanel.GamePanel;
import omok.player.ChangeInfo;
import omok.player.Player;
import omok.player.Rival;
import omok.player.UserImage;

public class WaitRoom extends JPanel {
	JPanel p_center, p_room, p_chat;
	JButton[] bt_room = new JButton[6]; // 반복문으로 생성, 조립
	private JTextArea area;
	JScrollPane chat_scroll;
	JTextField t_chat;
	JButton bt_send;

	JPanel p_east;
	JPanel p_user;
	private UserImage userImage;
	JPanel p_info;
	JLabel la_id, la_nickName, la_record, la_rank;
	JPanel p_record;
	JPanel p_record_south;
	private JTable tb_record;
	JScrollPane scroll;
	
	JButton bt_rank;
	JButton bt_change;
	JButton bt_logout;

	OmokFrame omokFrame;
	ChangeInfo changeInfo;

	private RecordHandler recordHandler;

	int[] joinPlayer = { 0, 0, 0, 0, 0, 0 };

	public WaitRoom(OmokFrame omokFrame) {
		this.omokFrame = omokFrame;
		WaitRoom waitroom = this;
		// 생성
		p_center = new JPanel();
		p_room = new JPanel();
		p_chat = new JPanel();
		
		for (int i = 0; i < 6; i++) {
			bt_room[i] = new JButton((i + 1) + "번 게임방 (" + joinPlayer[i] + "/2)");
			bt_room[i].setBackground(Color.cyan);
			p_room.add(bt_room[i]);
		}
		
		area = new JTextArea();
		chat_scroll = new JScrollPane(area);
		t_chat = new JTextField(52);
		bt_send = new JButton("전송");
		
		p_east = new JPanel();
		p_user = new JPanel();
		userImage = new UserImage(200, 200);
		p_info = new JPanel();
		la_id = new JLabel("", SwingConstants.CENTER);
		la_nickName = new JLabel("", SwingConstants.CENTER);
		la_record = new JLabel("", SwingConstants.CENTER);
		la_rank = new JLabel("", SwingConstants.CENTER);

		p_record = new JPanel();
		tb_record = new JTable(recordHandler = new RecordHandler());
		scroll = new JScrollPane(tb_record);
				
		p_record_south = new JPanel();
		bt_rank = new JButton("순위");
		bt_change = new JButton("정보변경");
		bt_logout = new JButton("로그아웃");

		// 스타일
		setLayout(new BorderLayout());
		p_center.setLayout(new BorderLayout());
		p_room.setLayout(new GridLayout(3, 2));
		p_chat.setPreferredSize(new Dimension(650, 300));
		p_chat.setBackground(Color.white);
		area.setEditable(false);
		chat_scroll.setPreferredSize(new Dimension(650, 250));
		chat_scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
		
		p_east.setLayout(new GridLayout(2, 1));
		p_info.setLayout(new GridLayout(4, 1));
		p_record.setLayout(new BorderLayout());
		p_east.setPreferredSize(new Dimension(300, 450));
		la_id.setFont(new Font("vardana", Font.BOLD, 20));
		la_nickName.setFont(new Font("vardana", Font.BOLD, 20));
		la_record.setFont(new Font("vardana", Font.BOLD, 20));
		la_rank.setFont(new Font("vardana", Font.BOLD, 20));
		Player player = this.omokFrame.getPlayer();
		
		p_user.setBackground(Color.white);
		p_info.setBackground(Color.white);
		la_id.setBackground(Color.white);
		la_nickName.setBackground(Color.white);
		la_rank.setBackground(Color.white);
		la_record.setBackground(Color.white);
		p_record.setBackground(Color.white);
		p_record_south.setBackground(Color.white);
		scroll.setPreferredSize(new Dimension(200, 280));
		p_record_south.setPreferredSize(new Dimension(300, 80));

		// 조립
		p_chat.add(chat_scroll);
		p_chat.add(t_chat);
		p_chat.add(bt_send);
		
		p_center.add(p_room);
		p_center.add(p_chat, BorderLayout.SOUTH);
		
		p_info.add(la_id);
		p_info.add(la_nickName);
		p_info.add(la_record);
		p_info.add(la_rank);
		
		p_user.add(userImage);
		p_user.add(p_info, BorderLayout.SOUTH);

		p_record.add(scroll, BorderLayout.NORTH);
		p_record_south.add(bt_rank);
		p_record_south.add(bt_change);
		p_record_south.add(bt_logout);
		p_record.add(p_record_south, BorderLayout.SOUTH);

		p_east.add(p_user);
		p_east.add(p_record);
		
		add(p_east, BorderLayout.EAST);
		add(p_center);
		
		for(int i = 0 ; i < bt_room.length ; i++) {
			int n = i;
			bt_room[n].addActionListener((e) -> {
				if (joinPlayer[n] < 2) {
					omokFrame.getCommandThread().send(n + "@@enterRoom");
					GamePanel gamePanel = (GamePanel) omokFrame.getPanel(omokFrame.GAMEPANEL);
					gamePanel.setRoom(n);
					updatePlayerInfo();
					gamePanel.newGameBoard();
					omokFrame.setPage(omokFrame.GAMEPANEL);
					omokFrame.pack();
				} else {
					JOptionPane.showMessageDialog(waitroom, "방이 모두 찼습니다.");
				}
			});
		}
		
		t_chat.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (t_chat.getText() != "") {
						String chat = omokFrame.getPlayer().getNickname() +"("+omokFrame.getPlayer().getId()+")"+") " + t_chat.getText();
						omokFrame.getCommandThread().send(chat+"@@allChat");
						t_chat.setText("");
					}
				}
			}
		});

		bt_send.addActionListener((e) -> {
			if (t_chat.getText() != "") {
				String chat = omokFrame.getPlayer().getNickname() + ") " + t_chat.getText();
				omokFrame.getCommandThread().send(chat+"@@allChat");
				area.append(chat + "\n");
				t_chat.setText("");
			}
		});
		
		bt_rank.addActionListener((e) -> {
			omokFrame.getCommandThread().send("@@rank");
		});

		bt_change.addActionListener((e) -> {
			changeInfo = new ChangeInfo(omokFrame);
		});

		bt_logout.addActionListener((e) -> {
			omokFrame.getCommandThread().send("@@logout");
			GamePanel gamePanel = (GamePanel) omokFrame.getPanel(omokFrame.GAMEPANEL);
			omokFrame.setPage(omokFrame.LOGINPANEL);
			omokFrame.pack();
		});

		setPreferredSize(new Dimension(960, 760));
		setVisible(true);
	}

	public void setIdText(String value) {
		la_id.setText(value);
	}

	public void setNicknameText(String value) {
		la_nickName.setText(value);
	}

	public void setRecordText(String value) {
		la_record.setText(value);
	}

	public void setRankText(String value) {
		la_rank.setText(value);
	}

	public ChangeInfo getChangeInfo() {
		return changeInfo;
	}

	public void setJoinPlayer(int index, int playerCount) {
		joinPlayer[index] = playerCount;
		bt_room[index].setText((index + 1) + "번 게임방 (" + joinPlayer[index] + "/2)");
		if (joinPlayer[index] == 2) {
			bt_room[index].setBackground(Color.red);
			bt_room[index].setForeground(Color.white);
			updateUI();
		} else {
			bt_room[index].setBackground(Color.cyan);
			bt_room[index].setForeground(Color.black);
			updateUI();
		}
	}

	public void updatePlayerInfo() {
		Player player = omokFrame.getPlayer();
		Rival rival = omokFrame.getRival();
		GamePanel gamePanel = (GamePanel) omokFrame.getPanel(omokFrame.GAMEPANEL);
		
		la_id.setText(player.getId());
		la_nickName.setText(player.getNickname());
		la_record.setText(player.getWin() + "승 " + player.getTie() + "무 " + player.getLose() + "패");
		la_rank.setText(player.getScore() + "점 (" + player.getRank() + "위)");

		if (player.getColor() == null) {
			player.setColor(Color.black);
			rival.setColor(Color.white);
		} else if (rival.getColor() == Color.white) {
			player.setColor(Color.black);
		}
		gamePanel.setColor(player.getColor());

		gamePanel.getLa_id1().setText(player.getId());
		gamePanel.getLa_nickName1().setText(player.getNickname());
		gamePanel.getLa_record1().setText(player.getWin() + "승 " + player.getTie() + "무 " + player.getLose() + "패");
		gamePanel.getLa_rank1().setText(player.getScore() + "점 (" + player.getRank() + "위)");
		gamePanel.getP_player1().setBorder(new LineBorder(player.getColor(), 5));

		gamePanel.getLa_id2().setText(rival.getId());
		gamePanel.getLa_nickName2().setText(rival.getNickname());
		gamePanel.getLa_record2().setText(rival.getWin() + "승 " + rival.getTie() + "무 " + rival.getLose() + "패");
		gamePanel.getLa_rank2().setText(rival.getScore() + "점 (" + rival.getRank() + "위)");

		gamePanel.getP_player2().setBorder(new LineBorder(rival.getColor(), 5));
		gamePanel.updateUI();
	}
	
	public int[] getJoinPlayer() {
		return joinPlayer;
	}

	public JTextArea getArea() {
		return area;
	}

	public void setArea(JTextArea area) {
		this.area = area;
	}

	public RecordHandler getRecordHandler() {
		return recordHandler;
	}

	public void setRecordHandler(RecordHandler recordHandler) {
		this.recordHandler = recordHandler;
	}

	public JTable getTb_record() {
		return tb_record;
	}

	public void setTb_record(JTable tb_record) {
		this.tb_record = tb_record;
	}

	public UserImage getUserImage() {
		return userImage;
	}

	public void setUserImage(UserImage userImage) {
		this.userImage = userImage;
	}	
}
