package omok;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import common.image.ImageUtil;
import omok.gamePanel.GamePanel;
import omok.player.ChangeInfo;
import omok.player.Player;
import omok.player.RegistForm;
import omok.player.Rival;
import omok.rank.Rank;
import omok.server.DataFormat;
import omok.waitRoom.WaitRoom;

public class OmokThread extends Thread {
	OmokFrame omokFrame;
	Socket socket;
	BufferedReader buffr;
	BufferedWriter buffw;
	ObjectInputStream ois;
	Boolean flag = true;
	Rank rankpage;
	ImageIcon icon;

	public OmokThread(OmokFrame omokFrame, Socket socket) {
		this.omokFrame = omokFrame;
		this.socket = socket;

		try {
			buffr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		if(omokFrame.getImgThread()==this) {
			imgListen();
		}else {
			listen();
		}
	}

	public void listen() {
		String msg = null;
		try {
			while (flag) {
				msg = buffr.readLine();
				System.out.println(msg);
				GamePanel gamePanel = (GamePanel) omokFrame.getPanel(omokFrame.GAMEPANEL);
				RegistForm registForm = (RegistForm) omokFrame.getPanel(omokFrame.REGISTFORM);
				WaitRoom waitRoom = (WaitRoom) omokFrame.getPanel(omokFrame.WAITROOM);
				ChangeInfo changeInfo = waitRoom.getChangeInfo();
				Player player = omokFrame.getPlayer();
				Rival rival = omokFrame.getRival();
				if (getCommand(msg)[1].equals("allChat")) {
					waitRoom.getArea().append(getCommand(msg)[0] + "\n");
				} else if (getCommand(msg)[1].equals("gameChat")) {
					gamePanel.getArea().append(getCommand(msg)[0] + "\n");
				} else if (msg.equals("@@계정생성 성공")) {
					JOptionPane.showMessageDialog(omokFrame.getPanel(omokFrame.REGISTFORM), "계정생성 성공");
				} else if (msg.equals("@@계정생성 실패")) {
					JOptionPane.showMessageDialog(omokFrame.getPanel(omokFrame.REGISTFORM), "계정생성 실패");
				} else if (getCommand(msg)[0].equals("id 사용가능")) {
					JOptionPane.showMessageDialog(omokFrame.getPanel(omokFrame.REGISTFORM), "사용 가능한 id입니다.");
					registForm.setCheckId(true);
					registForm.setIdColor();
				} else if (getCommand(msg)[0].equals("id 사용불가")) {
					JOptionPane.showMessageDialog(omokFrame.getPanel(omokFrame.REGISTFORM), "이미 있는 id입니다.");
					registForm.setCheckId(false);
				} else if (getCommand(msg)[1].equals("checkNicknameRegist")) {
					if (getCommandInfo(getCommand(msg)[0])[0].equals("nickname 사용가능")) {
						JOptionPane.showMessageDialog(omokFrame.getPanel(omokFrame.REGISTFORM), "사용 가능한 별명입니다.");
						registForm.setCheckNickname(true);
						registForm.setNicknameColor();
					} else if (getCommandInfo(getCommand(msg)[0])[0].equals("nickname 사용불가")) {
						JOptionPane.showMessageDialog(omokFrame.getPanel(omokFrame.REGISTFORM), "이미 있는 별명입니다.");
						registForm.setCheckNickname(false);
					}
				} else if ((getCommand(msg)[1].equals("checkNickname"))) {
					if (getCommandInfo(getCommand(msg)[0])[0].equals("nickname 사용가능")) {
						JOptionPane.showMessageDialog(omokFrame.getPanel(omokFrame.REGISTFORM), "사용 가능한 별명입니다.");
						changeInfo.setCheckNickname(true);
						changeInfo.setNicknameColor();
					} else if (getCommandInfo(getCommand(msg)[0])[0].equals("nickname 사용불가")) {
						JOptionPane.showMessageDialog(omokFrame.getPanel(omokFrame.REGISTFORM), "이미 있는 별명입니다.");
						registForm.setCheckNickname(false);
					}
				} else if (msg.equals("@@계정삭제 성공")) {
					JOptionPane.showMessageDialog(omokFrame.getPanel(omokFrame.LOGINPANEL), "계정이 삭제되었습니다.");
				} else if (msg.equals("@@계정삭제 실패")) {
					JOptionPane.showMessageDialog(omokFrame.getPanel(omokFrame.LOGINPANEL), "계정과 비밀번호를 확인해주십시오.");
				} else if (getCommand(msg)[1].equals("로그인")) {
					String[] command = getCommandInfo(getCommand(msg)[0]);
					player.setId(command[0]);
					player.setPassword(command[1]);
					player.setNickname(command[2]);
					player.setWin(Integer.parseInt(command[3]));
					player.setTie(Integer.parseInt(command[4]));
					player.setLose(Integer.parseInt(command[5]));
					player.setScore(Integer.parseInt(command[6]));
					player.setRegdate(command[7]);
					player.setRank(Integer.parseInt(command[8]));
					waitRoom.setIdText(player.getId());
					waitRoom.setNicknameText(player.getNickname());
					waitRoom.setRecordText(player.getWin() + "승 " + player.getTie() + "무 " + player.getLose() + "패");
					waitRoom.setRankText(player.getScore() + "점 (" + player.getRank() + "위)");
					omokFrame.setPage(omokFrame.WAITROOM);
					omokFrame.pack();
				} else if (msg.equals("@@loging")) {
					JOptionPane.showMessageDialog(omokFrame.getPanel(omokFrame.LOGINPANEL), "로그인중입니다.");
				} else if (msg.equals("@@로그인 실패")) {
					JOptionPane.showMessageDialog(omokFrame.getPanel(omokFrame.LOGINPANEL), "계정과 비밀번호를 확인해주십시오.");
				} else if (getCommand(msg)[1].equals("비밀번호, 별명 변경 성공")) {
					JOptionPane.showMessageDialog(waitRoom.getChangeInfo(), "비밀번호, 별명 변경 성공");
					omokFrame.getPlayer().setPassword(getCommandInfo(getCommand(msg)[0])[0]);
					omokFrame.getPlayer().setNickname(getCommandInfo(getCommand(msg)[0])[1]);
					waitRoom.setNicknameText(omokFrame.getPlayer().getNickname());
				} else if (getCommand(msg)[1].equals("비밀번호, 별명 변경 실패")) {
					JOptionPane.showMessageDialog(waitRoom.getChangeInfo(), "비밀번호, 별명 변경 실패");
				} else if (msg.equals("@@비밀번호 변경 성공")) {
					omokFrame.getPlayer().setPassword(getCommand(msg)[0]);
					JOptionPane.showMessageDialog(waitRoom.getChangeInfo(), "비밀번호 변경 성공");
				} else if (msg.equals("@@비밀번호 변경 실패")) {
					JOptionPane.showMessageDialog(waitRoom.getChangeInfo(), "비밀번호 변경 실패");
				} else if (getCommand(msg)[1].equals("별명 변경 성공")) {
					omokFrame.getPlayer().setNickname(getCommand(msg)[0]);
					waitRoom.setNicknameText(omokFrame.getPlayer().getNickname());
					waitRoom.getChangeInfo().setCheckNickname(false);
					JOptionPane.showMessageDialog(waitRoom.getChangeInfo(), "별명 변경 성공");
				} else if (getCommand(msg)[1].equals("별명 변경 실패")) {
					JOptionPane.showMessageDialog(waitRoom.getChangeInfo(), "별명 변경 실패");
				} else if (getCommand(msg)[1].equals("rank")) {
					ArrayList<String[]> rank = new ArrayList<String[]>();
					String[] msgArr = getCommandArr(getCommand(msg)[0]);
					for (int i = 0; i < msgArr.length; i++) {
						String[] playerRank = new String[6];
						playerRank[0] = Integer.toString(i + 1);
						playerRank[1] = getCommandInfo(msgArr[i])[0];
						playerRank[2] = getCommandInfo(msgArr[i])[1];
						playerRank[3] = getCommandInfo(msgArr[i])[2];
						playerRank[4] = getCommandInfo(msgArr[i])[3];
						playerRank[5] = getCommandInfo(msgArr[i])[4];
						rank.add(playerRank);
					}
					rankpage = new Rank(rank);
					rankpage.setLocationRelativeTo(omokFrame);
				} else if (getCommand(msg)[1].equals("updateRoom")) {
					for (int i = 0; i < 6; i++) {
						int playerCount = Integer.parseInt(getCommandInfo(getCommand(msg)[0])[i]);
						waitRoom.setJoinPlayer(i, playerCount);
					}
					waitRoom.updateUI();
				} else if (getCommand(msg)[1].equals("systemOut")) {
					flag = false;
				} else if (getCommand(msg)[1].equals("rival")) {
					String[] command = getCommandInfo(getCommand(msg)[0]);
					rival.setId(command[0]);
					rival.setNickname(command[2]);
					rival.setWin(Integer.parseInt(command[3]));
					rival.setTie(Integer.parseInt(command[4]));
					rival.setLose(Integer.parseInt(command[5]));
					rival.setScore(Integer.parseInt(command[6]));
					rival.setRank(Integer.parseInt(command[8]));
					if (command[10].equals("black")) {
						rival.setColor(Color.black);
						player.setColor(Color.white);
					} else {
						rival.setColor(Color.white);
						player.setColor(Color.black);
					}
					waitRoom.updatePlayerInfo();
				} else if (msg.equals("@@bye")) {
					gamePanel.getArea().append("---'" + rival.getNickname() + "'님이 퇴장하셨습니다.---\n");
					rival.setId("");
					rival.setNickname("");
					rival.setWin(0);
					rival.setTie(0);
					rival.setLose(0);
					rival.setScore(0);
					rival.setRank(0);
					rival.setColor(Color.white);
					Image img = ImageUtil.getIcon(this.getClass(), "omok/player/defaultImg.jpg", 100, 100).getImage();
					Image GameRoomImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
					gamePanel.getImg2().setImg(GameRoomImg);
					gamePanel.getImg2().repaint();
					waitRoom.updatePlayerInfo();
				} else if (msg.equals("@@hi")) {
					gamePanel.getArea().append("---'" + rival.getNickname() + "'님이 입장하셨습니다.---\n");
					send("@@hi~");
				} else if (getCommand(msg)[1].equals("index")) {
					if (gamePanel.getOmokList().get(Integer.parseInt(getCommand(msg)[0])).getWidth() == 0) {
						gamePanel.getOmokList().get(Integer.parseInt(getCommand(msg)[0])).setColor(rival.getColor());
						gamePanel.getOmokList().get(Integer.parseInt(getCommand(msg)[0])).setWidth(30);
						gamePanel.getOmokList().get(Integer.parseInt(getCommand(msg)[0])).setHeight(30);
						gamePanel.getOmokList().get(Integer.parseInt(getCommand(msg)[0])).repaint();
						if(gamePanel.isStart()) {
							gamePanel.setPlayer_turn(true);
							gamePanel.setCount(100);
							gamePanel.setSecond(20);
						}
					}
				} else if (getCommand(msg)[1].equals("rivalReady")) {
					gamePanel.getLa_ready().setText("준비완료");
					gamePanel.setRival_ready(true);
				} else if (getCommand(msg)[1].equals("rivalUnReady")) {
					gamePanel.getLa_ready().setText("대기중");
					gamePanel.setRival_ready(false);
				} else if (getCommand(msg)[1].equals("gameStart")) {
					gamePanel.newGame();
					if (omokFrame.getPlayer().getColor() == Color.black) {
						gamePanel.setPlayer_turn(true);
						gamePanel.setCount(100);
						gamePanel.updateUI();
					}
					gamePanel.setStart(true);
					gamePanel.getBt_ready().setText("게임중");
				} else if (getCommand(msg)[1].equals("updateInfo")) {
					String[] command = getCommandInfo(getCommand(msg)[0]);
					player.setId(command[0]);
					player.setPassword(command[1]);
					player.setNickname(command[2]);
					player.setWin(Integer.parseInt(command[3]));
					player.setTie(Integer.parseInt(command[4]));
					player.setLose(Integer.parseInt(command[5]));
					player.setScore(Integer.parseInt(command[6]));
					player.setRegdate(command[7]);
					player.setRank(Integer.parseInt(command[8]));
					waitRoom.updatePlayerInfo();
				} else if (getCommand(msg)[1].equals("winnerInfo") || getCommand(msg)[1].equals("loserInfo")) {
					String[] command = getCommandInfo(getCommand(msg)[0]);
					rival.setId(command[0]);
					rival.setNickname(command[2]);
					rival.setWin(Integer.parseInt(command[3]));
					rival.setTie(Integer.parseInt(command[4]));
					rival.setLose(Integer.parseInt(command[5]));
					rival.setScore(Integer.parseInt(command[6]));
					rival.setRank(Integer.parseInt(command[8]));
					waitRoom.updatePlayerInfo();
					if (getCommand(msg)[1].equals("winnerInfo")) {
						gamePanel.setCount(-1);
						gamePanel.setSecond(20);
						gamePanel.setStart(false);
						gamePanel.setPlayer_ready(false);
						gamePanel.setRival_ready(false);
						gamePanel.setPlayer_turn(false);
						gamePanel.getBt_ready().setForeground(Color.white);
						gamePanel.getBt_ready().setBackground(Color.red);
						gamePanel.getBt_ready().setText("게임준비");
						gamePanel.getLa_ready().setText("대기중");
						omokFrame.getRival().setColor(Color.black);
						omokFrame.getPlayer().setColor(Color.white);
						waitRoom.updatePlayerInfo();
						omokFrame.getCommandThread().send(rival.getId() + "@@imLose");
						JOptionPane.showMessageDialog(omokFrame, "패배하였습니다.");
					}
				} else if (getCommand(msg)[1].equals("rivalGiveUp")) {
					gamePanel.win();
				} else if (getCommand(msg)[1].equals("yourTurn")) {
					gamePanel.setPlayer_turn(true);
					gamePanel.setCount(100);
				} else if (getCommand(msg)[1].equals("record")) {
					String[] msgArr = getCommandArr(getCommand(msg)[0]);
					ArrayList<String[]> data = new ArrayList<String[]>();
					for (int i = 0; i < msgArr.length; i++) {
						String[] record = new String[3];
						record[0] = getCommandInfo(msgArr[i])[0];
						if (!record[0].equals("")) {
							record[1] = getCommandInfo(msgArr[i])[1];
							record[2] = getCommandInfo(msgArr[i])[2];
							data.add(record);
						}
					}
					waitRoom.getRecordHandler().setRecord(data);
					waitRoom.getTb_record().updateUI();
				}else if (getCommand(msg)[1].equals("이미지 변경 완료")) {
					JOptionPane.showMessageDialog(omokFrame, "이미지 변경 완료");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void imgListen() {
		try {
			WaitRoom waitRoom = (WaitRoom) omokFrame.getPanel(omokFrame.WAITROOM);
			GamePanel gamePanel = (GamePanel) omokFrame.getPanel(omokFrame.GAMEPANEL);
			while(flag) {	
				DataFormat df = (DataFormat) ois.readObject();
				String fileName = df.getFileName();
				Image img = df.getIcon().getImage();
                if(fileName.equals(omokFrame.getPlayer().getId())) {
					Image waitRoomImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                	waitRoom.getUserImage().setImg(waitRoomImg);
                	waitRoom.getUserImage().repaint();
					Image GameRoomImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
					gamePanel.getImg1().setImg(GameRoomImg);			
					gamePanel.getImg1().repaint();			
				}else {
					Image GameRoomImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
					gamePanel.getImg2().setImg(GameRoomImg);
					gamePanel.getImg2().repaint();
				}
			}
		} catch (IOException e) {
			if(flag) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void send(String msg) {
		try {
			buffw.write(msg + "\n");
			buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendImg(String fileName, ImageIcon icon) {
		try {
			DataFormat df = new DataFormat(fileName, icon);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(df);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String[] getCommand(String msg) {
		String[] msgArr = msg.split("@@");
		return msgArr;
	}

	public String[] getCommandArr(String msgArr) {
		String[] commandArr = msgArr.split("#");
		return commandArr;
	}

	public String[] getCommandInfo(String msgArr) {
		String[] commandInfo = msgArr.split("!");
		return commandInfo;
	}

}
