package omok.gamePanel;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import common.image.ImageUtil;
import omok.OmokFrame;
import omok.player.Player;
import omok.player.Rival;
import omok.player.UserImage;
import omok.waitRoom.WaitRoom;

public class GamePanel extends JPanel {
	private JPanel p_center;
	JPanel p_east;
	JPanel p_player1, p_player2;
	JPanel p_info1, p_info2;
	JPanel p_imgset1, p_imgset2;
	private UserImage img1, img2;
	JPanel p_chat;

	private JLabel la_id1, la_id2;
	private JLabel la_nickName1, la_nickName2;
	private JLabel la_record1, la_record2;
	private JLabel la_rank1, la_rank2;
	private JLabel la_ready;
	private JButton bt_ready;

	// p_caht
	private JTextArea area;
	JScrollPane scroll;
	JTextField t_chat;
	JButton bt_send, bt_giveUp, bt_exit;
	JProgressBar bar;
	JLabel la_second;

	OmokFrame omokFrame;
	GameBoard gameBoard;

	private ArrayList<GameBoard> omokList = new ArrayList<GameBoard>();

	private int room = -1;

	private boolean start = false;
	private boolean player_ready = false;
	private boolean rival_ready = false;
	private boolean player_turn = false;

	private Color color;
	Player player;

	Thread thread;
	private int count = -1;
	private int second = 20;

	public GamePanel(OmokFrame omokFrame) {
		this.omokFrame = omokFrame;
		player = omokFrame.getPlayer();

		// 생성
		p_center = new JPanel();
		p_east = new JPanel();
		p_player1 = new JPanel();
		p_player2 = new JPanel();
		p_imgset1 = new JPanel();
		p_imgset2 = new JPanel();
		img1 = new UserImage(100, 100);
		img2 = new UserImage(100, 100);
		p_info1 = new JPanel();
		p_info2 = new JPanel();
		
		p_chat = new JPanel();

		Player player = omokFrame.getPlayer();
		Rival rival = omokFrame.getRival();
		la_id1 = new JLabel(player.getId(), SwingConstants.CENTER);
		la_id2 = new JLabel(rival.getId(), SwingConstants.CENTER);
		la_nickName1 = new JLabel(player.getId(), SwingConstants.CENTER);
		la_nickName2 = new JLabel(rival.getNickname(), SwingConstants.CENTER);
		la_record1 = new JLabel(player.getWin() + "승 " + player.getTie() + "무 " + player.getLose() + "패",
				SwingConstants.CENTER);
		la_record2 = new JLabel(rival.getWin() + "승 " + rival.getTie() + "무 " + rival.getLose() + "패",
				SwingConstants.CENTER);
		la_rank1 = new JLabel(player.getScore() + "점 (" + player.getRank() + "위)", SwingConstants.CENTER);
		la_rank2 = new JLabel(rival.getScore() + "점 (" + rival.getRank() + "위)", SwingConstants.CENTER);
		bt_ready = new JButton("게임준비");
		la_ready = new JLabel("대기중", SwingConstants.CENTER);

		area = new JTextArea();
		scroll = new JScrollPane(area);
		t_chat = new JTextField(10);
		bt_send = new JButton("전송");
		bt_giveUp = new JButton("기권");
		bt_exit = new JButton("나가기");
		bar = new JProgressBar();
		la_second = new JLabel();

		// 스타일
		setLayout(new BorderLayout());
		p_center.setLayout(new GridLayout(19, 19, 0, 0));
		p_player1.setLayout(new BorderLayout());
		p_player2.setLayout(new BorderLayout());
		p_info1.setLayout(new GridLayout(5, 1));
		p_info2.setLayout(new GridLayout(5, 1));
		p_east.setLayout(new GridLayout(3, 1));
		
		p_player1.setBackground(Color.pink);
		p_player2.setBackground(Color.pink);
		p_imgset1.setBackground(Color.pink);
		p_imgset2.setBackground(Color.pink);
		p_imgset1.setPreferredSize(new Dimension(100, 100));
		p_imgset2.setPreferredSize(new Dimension(100, 100));
		img1.setPreferredSize(new Dimension(100, 100));
		img2.setPreferredSize(new Dimension(100, 100));
		p_info1.setBackground(Color.pink);
		p_info2.setBackground(Color.pink);

		la_id1.setFont(new Font("vardana", Font.BOLD, 15));
		la_nickName1.setFont(new Font("vardana", Font.BOLD, 15));
		la_record1.setFont(new Font("vardana", Font.BOLD, 15));
		la_rank1.setFont(new Font("vardana", Font.BOLD, 15));
		bt_ready.setBackground(Color.red);
		bt_ready.setForeground(Color.white);

		la_id2.setFont(new Font("vardana", Font.BOLD, 15));
		la_nickName2.setFont(new Font("vardana", Font.BOLD, 15));
		la_record2.setFont(new Font("vardana", Font.BOLD, 15));
		la_rank2.setFont(new Font("vardana", Font.BOLD, 15));
		la_ready.setFont(new Font("vardana", Font.BOLD, 15));

		bar.setForeground(Color.green);
		bar.setBackground(Color.gray);

		area.setEditable(false);
		scroll.setPreferredSize(new Dimension(190, 150));
		scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
		bar.setPreferredSize(new Dimension(150, 20));
		p_center.setPreferredSize(new Dimension(760, 760));
		p_east.setPreferredSize(new Dimension(200, 760));

		// 조립
		p_info1.add(la_id1);
		p_info1.add(la_nickName1);
		p_info1.add(la_record1);
		p_info1.add(la_rank1);
		p_info1.add(bt_ready);
		p_imgset1.add(img1);
		p_player1.add(p_imgset1, BorderLayout.NORTH);
		p_player1.add(p_info1);

		p_info2.add(la_id2);
		p_info2.add(la_nickName2);
		p_info2.add(la_record2);
		p_info2.add(la_rank2);
		p_info2.add(la_ready);
		p_imgset2.add(img2);
		p_player2.add(p_imgset2, BorderLayout.NORTH);
		p_player2.add(p_info2);

		p_chat.add(scroll);
		p_chat.add(t_chat);
		p_chat.add(bt_send);
		p_chat.add(bar);
		p_chat.add(la_second);
		p_chat.add(bt_giveUp);
		p_chat.add(bt_exit);

		add(p_center);
		p_east.add(p_player1);
		p_east.add(p_player2);
		p_east.add(p_chat);
		add(p_east, BorderLayout.EAST);

		setPreferredSize(new Dimension(960, 760));
		setVisible(false);

		t_chat.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (t_chat.getText() != "") {
						String chat = omokFrame.getPlayer().getNickname() + ") " + t_chat.getText();
						omokFrame.getCommandThread().send(chat+"@@gameChat");
						area.append(chat + "\n");
						t_chat.setText("");
					}
				}
			}
		});

		bt_send.addActionListener((e) -> {
			if (t_chat.getText() != "") {
				String chat = omokFrame.getPlayer().getNickname() + ") " + t_chat.getText();
				omokFrame.getImgThread().send(chat);
				area.append(chat + "\n");
				t_chat.setText("");
			}
		});

		bt_exit.addActionListener((e) -> {
			if(start) {
				JOptionPane.showMessageDialog(omokFrame, "게임중입니다.");
			}else {
				WaitRoom waitRoom = (WaitRoom) omokFrame.getPanel(omokFrame.WAITROOM); 
				start = false;
				player_ready = false;
				player_turn = false;
				omokFrame.getCommandThread().send(room + "@@exitRoom");
				exitRoom();
				room = -1;
				area.setText("");
				waitRoom.getArea().setText("");				
				omokFrame.setPage(omokFrame.WAITROOM);
				omokFrame.pack();
				updateUI();
			}
		});

		bt_ready.addActionListener((e) -> {
			WaitRoom waitRoom = (WaitRoom) omokFrame.getPanel(omokFrame.WAITROOM);
			if (waitRoom.getJoinPlayer()[room] == 2) {
				if (player_ready && !start) {
					player_ready = false;
					bt_ready.setForeground(Color.white);
					bt_ready.setBackground(Color.red);
					bt_ready.setText("게임준비");
					omokFrame.getCommandThread().send("@@unReady");
				} else {
					player_ready = true;
					bt_ready.setForeground(Color.black);
					bt_ready.setBackground(Color.green);
					bt_ready.setText("준비완료");
					omokFrame.getCommandThread().send("@@ready");
				}
			} else {
				JOptionPane.showMessageDialog(this, "상대방을 기다리는 중입니다.");
			}
		});
		
		bt_giveUp.addActionListener((e)->{
			if(start) {
				if(JOptionPane.showConfirmDialog(omokFrame, "기권하시겠습니까?")==JOptionPane.OK_OPTION) {
					omokFrame.getCommandThread().send("@@giveUp");
					omokFrame.getPlayer().setLose(omokFrame.getPlayer().getLose() + 1);
					WaitRoom waitRoom = (WaitRoom) omokFrame.getPanel(omokFrame.WAITROOM);
					waitRoom.updatePlayerInfo();
					start = false;
					player_ready = false;
					rival_ready=false;
					player_turn = false;
					bt_ready.setForeground(Color.white);
					bt_ready.setBackground(Color.red);
					bt_ready.setText("게임준비");
					la_ready.setText("대기중");
				}
			}
		});

		thread = new Thread() {
			public void run() {
				while (true) {
					bar.setValue(count);
					la_second.setText(second+"초");
					if(count == 0) {
						count = -1;
						second = 20;
						timeOver();
					}else if (count > 0){
						count -= 5;
						second--;
					}
					try {
						thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		};
		thread.start();
	}
	
	public void newGameBoard() {
		omokList.removeAll(omokList);
		p_center.removeAll();
		for (int i = 0; i < 361; i++) {
			gameBoard = new GameBoard(omokFrame, this, i, color);
			omokList.add(gameBoard);
			p_center.add(gameBoard);
		}
		updateUI();
		repaint();
	}
	
	public void newGame() {
		for (int i = 0; i < 361; i++) {
			omokList.get(i).setWidth(0);
			omokList.get(i).setHeight(0);
			omokList.get(i).setColor(omokFrame.getPlayer().getColor());
		}
		updateUI();
		repaint();
	}

	public void win() {
		count = -1;
		second = 20;
		Player player = (Player) omokFrame.getPlayer();
		Rival rival = (Rival) omokFrame.getRival();
		omokFrame.getCommandThread().send(rival.getId()+"@@endGame");
		player.setColor(Color.black);
		rival.setColor(Color.white);
		start = false;
		player_ready = false;
		rival_ready=false;
		player_turn = false;
		System.out.println(player_ready);
		bt_ready.setForeground(Color.white);
		bt_ready.setBackground(Color.red);
		bt_ready.setText("게임준비");
		la_ready.setText("대기중");
		JOptionPane.showMessageDialog(this, "승리하였습니다.");
	}
	
	public void timeOver() {
		omokFrame.getCommandThread().send("@@timeOver");
		player_turn=false;
	}
	
	public void exitRoom() {
		GamePanel gamePanel = (GamePanel) omokFrame.getPanel(omokFrame.GAMEPANEL);
		WaitRoom waitRoom = (WaitRoom) omokFrame.getPanel(omokFrame.WAITROOM);
		Rival rival = omokFrame.getRival();
		rival.setId("");
		rival.setNickname("");
		rival.setWin(0);
		rival.setTie(0);
		rival.setLose(0);
		rival.setScore(0);
		rival.setRank(0);
		Image img = ImageUtil.getIcon(this.getClass(), "omok/player/defaultImg.jpg", 100, 100).getImage();
		Image GameRoomImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		img2.setImg(GameRoomImg);
		img2.repaint();
		waitRoom.updatePlayerInfo();
	}
			
	public JPanel getP_center() {
		return p_center;
	}

	public void setP_center(JPanel p_center) {
		this.p_center = p_center;
	}

	public JTextArea getArea() {
		return area;
	}

	public void setArea(JTextArea area) {
		this.area = area;
	}

	public int getRoom() {
		return room;
	}

	public void setRoom(int room) {
		this.room = room;
	}

	public JLabel getLa_id1() {
		return la_id1;
	}

	public void setLa_id1(JLabel la_id1) {
		this.la_id1 = la_id1;
	}

	public JLabel getLa_id2() {
		return la_id2;
	}

	public void setLa_id2(JLabel la_id2) {
		this.la_id2 = la_id2;
	}

	public JLabel getLa_nickName1() {
		return la_nickName1;
	}

	public void setLa_nickName1(JLabel la_nickName1) {
		this.la_nickName1 = la_nickName1;
	}

	public JLabel getLa_nickName2() {
		return la_nickName2;
	}

	public void setLa_nickName2(JLabel la_nickName2) {
		this.la_nickName2 = la_nickName2;
	}

	public JLabel getLa_record1() {
		return la_record1;
	}

	public void setLa_record1(JLabel la_record1) {
		this.la_record1 = la_record1;
	}

	public JLabel getLa_record2() {
		return la_record2;
	}

	public void setLa_record2(JLabel la_record2) {
		this.la_record2 = la_record2;
	}

	public JLabel getLa_rank1() {
		return la_rank1;
	}

	public void setLa_rank1(JLabel la_rank1) {
		this.la_rank1 = la_rank1;
	}

	public JLabel getLa_rank2() {
		return la_rank2;
	}

	public void setLa_rank2(JLabel la_rank2) {
		this.la_rank2 = la_rank2;
	}

	public JLabel getLa_ready() {
		return la_ready;
	}

	public void setLa_ready(JLabel la_ready) {
		this.la_ready = la_ready;
	}

	public JPanel getP_player1() {
		return p_player1;
	}

	public void setP_player1(JPanel p_player1) {
		this.p_player1 = p_player1;
	}

	public JPanel getP_player2() {
		return p_player2;
	}

	public void setP_player2(JPanel p_player2) {
		this.p_player2 = p_player2;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public ArrayList<GameBoard> getOmokList() {
		return omokList;
	}

	public void setOmokList(ArrayList<GameBoard> omokList) {
		this.omokList = omokList;
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public boolean isPlayer_ready() {
		return player_ready;
	}

	public void setPlayer_ready(boolean player_ready) {
		this.player_ready = player_ready;
	}

	public boolean isRival_ready() {
		return rival_ready;
	}

	public void setRival_ready(boolean rival_ready) {
		this.rival_ready = rival_ready;
	}

	public boolean isPlayer_turn() {
		return player_turn;
	}

	public void setPlayer_turn(boolean player_turn) {
		this.player_turn = player_turn;
	}

	public JButton getBt_ready() {
		return bt_ready;
	}

	public void setBt_ready(JButton bt_ready) {
		this.bt_ready = bt_ready;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public UserImage getImg1() {
		return img1;
	}

	public void setImg1(UserImage img1) {
		this.img1 = img1;
	}

	public UserImage getImg2() {
		return img2;
	}

	public void setImg2(UserImage img2) {
		this.img2 = img2;
	}	
}
