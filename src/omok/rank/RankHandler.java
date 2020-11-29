package omok.rank;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class RankHandler extends AbstractTableModel{
	ArrayList<String[]> rank = new ArrayList<String[]>();
	String[] column = {"순위", "ID", "승", "무", "패", "점수"};
	public int getRowCount() {
		return rank.size();
	}
	
	public int getColumnCount() {
		return column.length;
	}

	public String getColumnName(int col) {
		return column[col];
	}

	public Object getValueAt(int row, int col) {
		return rank.get(row)[col];
	}
	
}
