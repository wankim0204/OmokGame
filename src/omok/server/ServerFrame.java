package omok.server;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import omok.util.db.DBManager;

public class ServerFrame extends JFrame {
	JButton bt;
	JPanel p_north;
	JTextArea area;
	JScrollPane scroll;

	ServerSocket server;

	ArrayList<ServerThread> threadList = new ArrayList<ServerThread>();
	int index = 0;
	Thread thread;

	private DBManager dbManager;
	private Connection con;

	ArrayList<String> loginList = new ArrayList<String>();
	int[] roomList = { 0, 0, 0, 0, 0, 0 };

	public ServerFrame() {
		loginList.add("");
		// 생성
		bt = new JButton("서버가동");
		p_north = new JPanel();
		area = new JTextArea();
		scroll = new JScrollPane(area);
		dbManager = new DBManager();
		con = dbManager.connect();
		if (con == null) {
			JOptionPane.showMessageDialog(this, "접속 실패");
			System.exit(0);
		} else {
			this.setTitle("게임 서버 ");
		}

		scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
		
		// 조립
		p_north.add(bt);
		add(p_north, BorderLayout.NORTH);
		add(scroll);

		setSize(500, 400);
		setVisible(true);
		setLocationRelativeTo(null);

		bt.addActionListener((e) -> {
			thread = new Thread() {
				public void run() {
					startServer();
				}
			};
			thread.start();
		});

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dbManager.disConnect(con);
				System.exit(0);
			}
		});
	}

	public void startServer() {
		try {
			server = new ServerSocket(9999);
			area.append("서버가동\n");
			while (true) {
				Socket socket = server.accept();

				ServerThread serverThread = new ServerThread(this, socket, index++);
				serverThread.start();

				threadList.add(serverThread);
				if (threadList.size() % 2 == 0) {
					area.append(threadList.size() / 2 + "명 접속중\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DBManager getDBManager() {
		return dbManager;
	}

	public Connection getCon() {
		return con;
	}

	public static void main(String[] args) {
		new ServerFrame();
	}
}
