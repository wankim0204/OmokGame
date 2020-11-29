package omok;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import omok.gamePanel.GamePanel;
import omok.player.LoginPanel;
import omok.player.Player;
import omok.player.RegistForm;
import omok.player.Rival;
import omok.rank.Rank;
import omok.waitRoom.WaitRoom;

public class OmokFrame extends JFrame {
	JPanel p_center;
	private JPanel[] panelList = new JPanel[4];

	public static final int LOGINPANEL = 0;
	public static final int GAMEPANEL = 1;
	public static final int WAITROOM = 2;
	public static final int REGISTFORM = 3;

	Socket socket;
	Socket socket2;
	private OmokThread commandThread;
	private OmokThread imgThread;
	private Player player = new Player();
	private Rival rival = new Rival();
	
	public OmokFrame() {
		super("오목 게임");
		OmokFrame omokFrame = this;
		// 생성
		p_center = new JPanel();
		panelList[0] = new LoginPanel(this);
		panelList[1] = new GamePanel(this);
		panelList[2] = new WaitRoom(this);
		panelList[3] = new RegistForm(this);

		// 조립
		p_center.add(panelList[0]);
		p_center.add(panelList[1]);
		p_center.add(panelList[2]);
		p_center.add(panelList[3]);

		setPage(0);
		add(p_center);

		connectServer();

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				GamePanel gamePanel = (GamePanel) panelList[GAMEPANEL];
				if(gamePanel.isStart()) {
					JOptionPane.showMessageDialog(omokFrame, "게임중입니다.");
				}else if (!gamePanel.isStart()){
					exitSystem();
				}
			}
		});
	}
	
	public void setPage(int index) {
		for (int i = 0; i < panelList.length; i++) {
			if (i == index) {
				panelList[i].setVisible(true);
			} else {
				panelList[i].setVisible(false);
			}
		}
	}

	public JPanel getPanel(int pageName) {
		return panelList[pageName];
	}

	public void connectServer() {
		try {
			socket = new Socket("localhost", 9999);
			commandThread = new OmokThread(this, socket);
			commandThread.start();

			socket2 = new Socket("localhost", 9999);
			imgThread = new OmokThread(this, socket2);
			imgThread.start();
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(this, "서버접속 실패");
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "서버접속 실패");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void exitSystem() {
		GamePanel gamePanel = (GamePanel) panelList[GAMEPANEL];
		imgThread.flag = false;
		if (panelList[1].isVisible() == true) {
			commandThread.send(gamePanel.getRoom() + "@@exitRoom");
		}
		commandThread.send("@@logout");
		commandThread.send("@@systemOut");
		System.exit(0);
	}

	public OmokFrame getOmokFrame() {
		return this;
	}

	public OmokThread getCommandThread() {
		return commandThread;
	}

	public OmokThread getImgThread() {
		return imgThread;
	}

	public static void main(String[] args) {
		new OmokFrame();
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Rival getRival() {
		return rival;
	}

	public void setRival(Rival rival) {
		this.rival = rival;
	}

}
