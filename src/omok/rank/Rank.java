package omok.rank;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import omok.OmokFrame;

public class Rank extends JFrame{
	ArrayList rank;
	JLabel la;
	JTable rankTable;
	JScrollPane scroll;
	RankHandler rankHandler;
	
	public Rank(ArrayList rank) {
		//����
		this.rank = rank;
		la = new JLabel("��  ��", SwingConstants.CENTER);
		rankTable = new JTable(rankHandler = new RankHandler());
		rankHandler.rank = rank;
		scroll = new JScrollPane(rankTable);

		//��Ÿ��
		la.setFont(new Font("����", Font.BOLD, 25));
		
		//����
		add(la, BorderLayout.NORTH);
		add(scroll);
		
		setSize(400, 300);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setDefaultLookAndFeelDecorated(false);
	}
}
