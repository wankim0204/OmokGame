package omok.server;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import common.image.ImageUtil;
import omok.player.Player;

public class ServerThread extends Thread {
	Socket socket;
	ServerFrame serverFrame;
	BufferedReader buffr;
	BufferedWriter buffw;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	Boolean flag = true;
	int index;
	int room = -1;
	Player player = new Player();

	boolean start = false;
	boolean player_ready = false;
	boolean rival_ready = false;
	boolean player_turn = false;
	boolean rival_turn = false;

	public ServerThread(ServerFrame serverFrame, Socket socket, int index) {
		this.serverFrame = serverFrame;
		this.socket = socket;
		this.index = index;
		try {
			buffr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		if (index % 2 == 1) {
			imgListen();
		} else {
			listen();
		}
	}

	public void listen() {
		String msg = null;
		try {
			while (flag) {
				msg = buffr.readLine();
				if (index % 2 == 0) {
					String commandType = getCommandType(msg);
					String command = getCommand(msg);
					String[] commandInfo = getCommandInfo(command);
					if (commandType.equals("checkIdRegist")) {
						checkOverlab("id", command, "checkIdRegist");
					} else if (commandType.equals("checkNicknameRegist")) {
						checkOverlab("nickname", command, "checkNicknameRegist");
					} else if (commandType.equals("checkNickname")) {
						checkOverlab("nickname", command, "checkNickname");
					} else if (commandType.equals("regist")) {
						String id = getCommandInfo(getCommand(msg))[0];
						String password = commandInfo[1];
						String nickname = commandInfo[2];
						regist(id, password, nickname);
					} else if (commandType.equals("login")) {
						for (int i = 0; i < serverFrame.loginList.size(); i++) {
							if (serverFrame.loginList.get(i).equals(commandInfo[0])) {
								for (int n = 0; n < serverFrame.threadList.size(); n++) {
									if (serverFrame.threadList.get(n) == this) {
										send("@@loging", n);
										serverFrame.area.append(commandInfo[0] + " 중복 접속\n");
									}
								}
								break;
							} else if ((i + 1) == serverFrame.loginList.size()) {
								String id = commandInfo[0];
								String password = commandInfo[1];
								login(id, password);
								String joinPlayer = "";
								for (int r = 0; r < serverFrame.roomList.length; r++) {
									joinPlayer += serverFrame.roomList[r] + "!";
								}
								for (int n = 0; n < serverFrame.threadList.size(); n++) {
									if (serverFrame.threadList.get(n) == this) {
										send(joinPlayer + "@@updateRoom", n);
									}
								}
								break;
							}
						}
					} else if (commandType.equals("delete")) {
						String id = commandInfo[0];
						String password = commandInfo[1];
						delete(id, password);
					} else if (commandType.equals("changeAll")) {
						String id = commandInfo[0];
						String password = commandInfo[1];
						String nickname = commandInfo[2];
						changeAll(id, password, nickname);
					} else if (commandType.equals("changePassword")) {
						String id = commandInfo[0];
						String password = commandInfo[1];
						changePassword(id, password);
					} else if (commandType.equals("changeNickname")) {
						String id = commandInfo[0];
						String nickname = commandInfo[1];
						changeNickname(id, nickname);
					} else if (commandType.equals("rank")) {
						rank();
					} else if (commandType.equals("enterRoom")) {
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) == this) {
								room = Integer.parseInt(command);
								serverFrame.threadList.get(i + 1).room = Integer.parseInt(command);
								serverFrame.roomList[room] += 1;
							}
						}
						String color = "";
						if (serverFrame.roomList[room] == 1) {
							player.setColor(Color.black);
							color = "black";
						} else {
							player.setColor(Color.white);
							color = "white";
						}
						String joinPlayer = "";
						for (int i = 0; i < serverFrame.roomList.length; i++) {
							joinPlayer += serverFrame.roomList[i] + "!";
						}
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (i % 2 == 0) {
								send(joinPlayer + "@@updateRoom", i);
							}
						}
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) != this && serverFrame.threadList.get(i).room == room
									&& i % 2 == 0) {
								String playerInfo = getPlayerInfo();
								send(playerInfo + "!" + color + "@@rival", i);
								sendImg(player.getId(), i+1);
								if (serverFrame.roomList[room] == 2) {
									send("@@hi", i);
								}
							}
						}
					} else if (commandType.equals("exitRoom")) {
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) == this) {
								room = Integer.parseInt(command);
								serverFrame.threadList.get(i + 1).room = Integer.parseInt(command);
								serverFrame.roomList[room] -= 1;
							}
						}
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) != this && serverFrame.threadList.get(i).room == room
									&& i % 2 == 0) {
								send("@@bye", i);
							}
						}
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) == this) {
								room = -1;
								serverFrame.threadList.get(i + 1).room = -1;
							}
						}
						String joinPlayer = "";
						for (int i = 0; i < serverFrame.roomList.length; i++) {
							joinPlayer += serverFrame.roomList[i] + "!";
						}
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (i % 2 == 0) {
								send(joinPlayer + "@@updateRoom", i);
							}
						}
					} else if (commandType.equals("logout")) {
						serverFrame.loginList.remove(player.getId());
						serverFrame.area.append(player.getId() + " 로그아웃\n");
						player.setId(null);
						player.setPassword(null);
						player.setNickname(null);
						player.setWin(0);
						player.setTie(0);
						player.setLose(0);
						player.setScore(0);
						player.setRegdate(null);
						player.setRank(0);
						player.setColor(null);
					} else if (commandType.equals("systemOut")) {
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) == this) {
								serverFrame.threadList.get(i + 1).flag = false;
								serverFrame.threadList.remove(serverFrame.threadList.get(i + 1));
								serverFrame.area.append(serverFrame.threadList.size() / 2 + "명 접속중\n");
								send("@@systemOut", i);
							}
						}
						serverFrame.threadList.remove(this);
						flag = false;
					} else if (commandType.equals("hi~")) {
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) != this && serverFrame.threadList.get(i).room == room
									&& i % 2 == 0) {
								String playerInfo = getPlayerInfo();
								send(playerInfo + "!black" + "@@rival", i);
							}
						}
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) != this && serverFrame.threadList.get(i).room == room
									&& i % 2 == 1) {
								sendImg(player.getId(), i);
							}
						}
					} else if (commandType.equals("index")) {
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) != this && serverFrame.threadList.get(i).room == room
									&& i % 2 == 0) {
								send(command + "@@index", i);
							}
						}
					} else if (commandType.equals("ready")) {
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) != this && serverFrame.threadList.get(i).room == room
									&& i % 2 == 0) {
								serverFrame.threadList.get(i).rival_ready = true;
								send("@@rivalReady", i);
							}
						}
						player_ready = true;

						if (player_ready && rival_ready) {
							start = true;
							for (int i = 0; i < serverFrame.threadList.size(); i++) {
								if (serverFrame.threadList.get(i).room == room && i % 2 == 0) {
									send("@@gameStart", i);
								}
							}
						}
					} else if (commandType.equals("unReady")) {
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) != this && serverFrame.threadList.get(i).room == room
									&& i % 2 == 0) {
								serverFrame.threadList.get(i).rival_ready = false;
								send("@@rivalUnReady", i);
							}
						}
						player_ready = false;
					} else if (commandType.equals("endGame")) {
						winner(player.getId());
						recordWin(player.getId(), command);
						start = false;
						rival_ready = false;
						player_ready = false;
						player_turn=false;
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) == this) {
								send(getPlayerInfo() + "@@updateInfo", i);
								send(record(player.getId()) + "@@record", i);
							} else if (serverFrame.threadList.get(i) != this
									&& serverFrame.threadList.get(i).room == room && i % 2 == 0) {
								send(getPlayerInfo() + "@@winnerInfo", i);
							}
						}
					} else if (commandType.equals("imLose")) {
						start = false;
						rival_ready = false;
						player_ready = false;
						player_turn=false;
						loser(player.getId());
						recordLose(player.getId(), command);
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) == this) {
								send(getPlayerInfo() + "@@updateInfo", i);
								send(record(player.getId()) + "@@record", i);
							} else if (serverFrame.threadList.get(i) != this
									&& serverFrame.threadList.get(i).room == room && i % 2 == 0) {
								send(getPlayerInfo() + "@@loserInfo", i);
							}
						}
					} else if (commandType.equals("giveUp")) {
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) != this && serverFrame.threadList.get(i).room == room
									&& i % 2 == 0) {
								send("@@rivalGiveUp", i);
							}
						}
					} else if (commandType.equals("timeOver")) {
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) != this && serverFrame.threadList.get(i).room == room
									&& i % 2 == 0) {
								send("@@yourTurn", i);
							}
						}
					} else if (commandType.equals("allChat")) {
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (i % 2 == 0) {
								send(msg + "@@allChat", i);
							}
						}
					} else if (commandType.equals("gameChat")) {
						for (int i = 0; i < serverFrame.threadList.size(); i++) {
							if (serverFrame.threadList.get(i) != this && serverFrame.threadList.get(i).room == room
									&& i % 2 == 0) {
								send(msg + "@@gameChat", i);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			if (flag) {
				e.printStackTrace();
			}
		}
	}

	public void imgListen() {
		try {
			while (flag) {
				ois = new ObjectInputStream(socket.getInputStream());
				DataFormat df = (DataFormat) ois.readObject();
				String fileName = df.getFileName();
				Image img = df.getIcon().getImage();
				String path = "E:/국비수업/workspace/OmokGame/src/omok/server/userImage/";
				BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null),
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g2 = bimg.createGraphics();
				g2.drawImage(img, 0, 0, null);
				g2.dispose();
				ImageIO.write(bimg, "jpg", new File(path + fileName + ".jpg"));
				playerImg(fileName);
			}
		} catch (IOException e) {
			if (flag) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if(ois!=null) {
					ois.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void send(String msg, int i) {
		ServerThread serverThread = serverFrame.threadList.get(i);
		try {
			serverThread.buffw.write(msg + "\n");
			serverThread.buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendImg(String fileName, int i) {
		try {
			ImageIcon icon = getPlayerIcon(fileName);
			DataFormat df = new DataFormat(fileName, icon);
			serverFrame.threadList.get(i).oos.writeObject(df);
			serverFrame.threadList.get(i).oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getCommandType(String msg) {
		int index = msg.lastIndexOf("@@");
		String commandType = msg.substring(index + 2, msg.length());
		return commandType;
	}

	public String getCommand(String msg) {
		int index = msg.lastIndexOf("@@");
		String msgArr = msg.substring(0, index);
		return msgArr;
	}

	public String[] getCommandInfo(String msgArr) {
		String[] commandInfo = msgArr.split("!");
		return commandInfo;
	}

	public void checkOverlab(String type, String msg, String command) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select * from player where " + type + " = '" + msg + "'";

		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send(type + " 사용불가@@" + command, i);
					}
				}
			} else {
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send(type + " 사용가능@@" + command, i);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt, rs);
		}
	}

	public void regist(String id, String password, String nickname) {
		PreparedStatement pstmt = null;

		String sql = "insert into player(id, password, nickname, playerimg) values(?, ?, ?, ?)";
		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, password);
			pstmt.setString(3, nickname);
			pstmt.setString(4, "defaultImg.jpg");
			int result = pstmt.executeUpdate();
			if (result != 0) {
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send("@@계정생성 성공", i);
						serverFrame.area.append(id + "계정 생성 성공\n");
						Image img = ImageUtil.getIcon(this.getClass(), "omok/server/userImage/defaultImg.jpg", 300, 300).getImage();
						String path = "E:/국비수업/workspace/OmokGame/src/omok/server/userImage/";
						BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null),
								BufferedImage.TYPE_INT_RGB);
						Graphics2D g2 = bimg.createGraphics();
						g2.drawImage(img, 0, 0, null);
						g2.dispose();
						ImageIO.write(bimg, "jpg", new File(path + id + ".jpg"));
					}
				}
			} else {
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send("@@계정생성 실패", i);
						serverFrame.area.append(id + "계정 생성 실패\n");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt);
		}
	}

	public void login(String id, String password) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select * from player where id = ? and password =?";

		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, password);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				player.setId(rs.getString("id"));
				player.setPassword(rs.getString("password"));
				player.setNickname(rs.getString("nickname"));
				player.setWin(rs.getInt("win"));
				player.setTie(rs.getInt("tie"));
				player.setLose(rs.getInt("lose"));
				player.setScore(rs.getInt("score"));
				player.setRegdate(rs.getString("regdate"));
				player.setRank(personalRank(player.getId()));
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					String playerInfo = getPlayerInfo();
					if (serverFrame.threadList.get(i) == this) {
						send(playerInfo + "@@로그인", i);
						send(record(player.getId()) + "@@record", i);
						serverFrame.area.append(id + " 로그인\n");
						sendImg(player.getId(), i + 1);
					}
				}
				serverFrame.loginList.add(player.getId());
			}
			if (rs.getRow() == 0) {
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send("@@로그인 실패", i);
						serverFrame.area.append(id + " 로그인 실패\n");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt, rs);
		}
	}

	public int personalRank(String id) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select id from player order by score desc";
		int pesonalRank = 0;
		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getString("id").equals(id)) {
					pesonalRank = rs.getRow();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt, rs);
		}
		return pesonalRank;
	}

	public int getScore(String id) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select * from player";
		int score = 0;
		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getString("id").equals(id)) {
					score = rs.getInt("score");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt, rs);
		}
		return score;
	}

	public void delete(String id, String password) {
		PreparedStatement pstmt = null;

		String sql = "delete from player where id = ? and password = ?";

		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, password);
			int result = pstmt.executeUpdate();

			if (result != 0) {
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send("@@계정삭제 성공", i);
						serverFrame.area.append(id + " 계정 삭제\n");
					}
				}
			} else if (result == 0) {
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send("@@계정삭제 실패", i);
						serverFrame.area.append(id + " 계정삭제 실패\n");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt);
		}
	}

	public void changeAll(String id, String password, String nickname) {
		PreparedStatement pstmt = null;

		String sql = "update player set password = ?, nickname = ? where id = ?";

		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			pstmt.setString(1, password);
			pstmt.setString(2, nickname);
			pstmt.setString(3, id);
			int result = pstmt.executeUpdate();
			if (result != 0) {
				player.setPassword(password);
				player.setNickname(nickname);
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send(password + "!" + nickname + "@@비밀번호, 별명 변경 성공", i);
						serverFrame.area.append(id + " 비밀번호, 별명 변경\n");
					}
				}
			} else if (result == 0) {
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send("@@비밀번호, 별명 변경 실패", i);
						serverFrame.area.append(id + " 비밀번호, 별명 변경 실패\n");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt);
		}
	}

	public void changePassword(String id, String password) {
		PreparedStatement pstmt = null;

		String sql = "update player set password = ? where id = ?";

		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			pstmt.setString(1, password);
			pstmt.setString(2, id);
			int result = pstmt.executeUpdate();
			if (result != 0) {
				player.setPassword(password);
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send("@@비밀번호 변경 성공", i);
						serverFrame.area.append(id + " 비밀번호 변경\n");
					}
				}
			} else if (result == 0) {
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send("@@비밀번호 변경 실패", i);
						serverFrame.area.append(id + " 비밀번호 변경 실패\n");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt);
		}
	}

	public void changeNickname(String id, String nickname) {
		PreparedStatement pstmt = null;

		String sql = "update player set nickname = ? where id = ?";

		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			pstmt.setString(1, nickname);
			pstmt.setString(2, id);
			int result = pstmt.executeUpdate();
			if (result != 0) {
				player.setNickname(nickname);
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send(nickname + "@@별명 변경 성공", i);
						serverFrame.area.append(id + " 별명 변경\n");
					}
				}
			} else if (result == 0) {
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send("@@별명 변경 실패", i);
						serverFrame.area.append(id + " 별명 변경 실패\n");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt);
		}
	}

	public void rank() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select * from player order by score desc";

		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			rs = pstmt.executeQuery();
			String msg = "";
			while (rs.next()) {
				msg += rs.getString("id") + "!";
				msg += rs.getString("win") + "!";
				msg += rs.getString("tie") + "!";
				msg += rs.getString("lose") + "!";
				msg += rs.getString("score") + "#";
			}
			for (int i = 0; i < serverFrame.threadList.size(); i++) {
				if (serverFrame.threadList.get(i) == this) {
					send(msg + "@@rank", i);
					serverFrame.area.append("순위 정보 전송\n");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt, rs);
		}
	}

	public void winner(String winner) {
		PreparedStatement pstmt = null;

		String sql = "update player set win = win+1, score = score+3 where id = '" + winner + "'";
		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt);
		}
	}

	public void loser(String loser) {
		PreparedStatement pstmt = null;

		String sql = "update player set lose = lose+1, score = case when (score-1)>0 then score-1 else 0 end where id = '"
				+ loser + "'";

		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt);
		}
	}

	public String getPlayerInfo() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select * from player where id = '" + player.getId() + "'";

		String playerInfo = "";
		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				player.setId(rs.getString("id"));
				player.setPassword(rs.getString("password"));
				player.setNickname(rs.getString("nickname"));
				player.setWin(rs.getInt("win"));
				player.setTie(rs.getInt("tie"));
				player.setLose(rs.getInt("lose"));
				player.setScore(rs.getInt("score"));
				player.setRegdate(rs.getString("regdate"));
				player.setRank(personalRank(player.getId()));
			}
			playerInfo += player.getId() + "!";
			playerInfo += player.getPassword() + "!";
			playerInfo += player.getNickname() + "!";
			playerInfo += player.getWin() + "!";
			playerInfo += player.getTie() + "!";
			playerInfo += player.getLose() + "!";
			playerInfo += player.getScore() + "!";
			playerInfo += player.getRegdate() + "!";
			playerInfo += player.getRank() + "!";
			playerInfo += player.getColor();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt, rs);
		}
		return playerInfo;
	}

	public void recordWin(String id, String rival) {
		PreparedStatement pstmt = null;

		String sql = "insert into record(id, rival, gameresult) values (?, ?, ?)";

		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, rival);
			pstmt.setString(3, "승");
			int result = pstmt.executeUpdate();
			if (result != 0) {
				serverFrame.area.append(id + "승 " + rival + "패\n");
			} else {
				System.out.println("기록 등록 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt);
		}
	}

	public void recordLose(String id, String rival) {
		PreparedStatement pstmt = null;

		String sql = "insert into record(id, rival, gameresult) values (?, ?, ?)";

		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, rival);
			pstmt.setString(3, "패");
			int result = pstmt.executeUpdate();
			if (result == 0) {
				System.out.println("기록 등록 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt);
		}
	}

	public String record(String id) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select * from record where id ='" + id + "'";
		String msg = "";
		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				msg += rs.getDate("matchdate") + "!";
				msg += rs.getString("gameresult") + "!";
				msg += rs.getString("rival") + "#";
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt, rs);
		}
		return msg;
	}

	public void playerImg(String id) {
		PreparedStatement pstmt = null;

		String sql = "update player set playerimg = ? where id = '" + id + "'";

		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			pstmt.setString(1, id + ".jpg");
			int result = pstmt.executeUpdate();
			if (result != 0) {
				for (int i = 0; i < serverFrame.threadList.size(); i++) {
					if (serverFrame.threadList.get(i) == this) {
						send("@@이미지 변경 완료", i - 1);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt);
		}
	}

	public ImageIcon getPlayerIcon(String id) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select playerimg from player where id = '" + id + "'";

		ImageIcon icon = null;
		try {
			pstmt = serverFrame.getCon().prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				icon = ImageUtil.getIcon(this.getClass(), "omok/server/userImage/" + rs.getString("playerimg"), 300,
						300);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			serverFrame.getDBManager().close(pstmt, rs);
		}
		return icon;
	}
}
