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
		//생성
		this.rank = rank;
		la = new JLabel("순  위", SwingConstants.CENTER);
		rankTable = new JTable(rankHandler = new RankHandler());
		rankHandler.rank = rank;
		scroll = new JScrollPane(rankTable);

		//스타일
		la.setFont(new Font("돋움", Font.BOLD, 25));
		
		//조립
		add(la, BorderLayout.NORTH);
		add(scroll);
		
		setSize(400, 300);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setDefaultLookAndFeelDecorated(false);
	}
}
