package omok.rank;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class RankHandler extends AbstractTableModel{
	ArrayList<String[]> rank = new ArrayList<String[]>();
	String[] column = {"����", "ID", "��", "��", "��", "����"};
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
