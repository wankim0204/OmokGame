package omok.waitRoom;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class RecordHandler extends AbstractTableModel{
	private ArrayList<String[]> record = new ArrayList<String[]>();
		
	String[] column = {"대전일자", "대전결과", "상대방"};
	
	public int getRowCount() {
		return record.size();
	}

	public String getColumnName(int col) {
		return column[col];
	}
	
	public int getColumnCount() {
		return column.length;
	}

	public Object getValueAt(int row, int col) {
		return record.get(row)[col];
	}

	public ArrayList<String[]> getRecord() {
		return record;
	}

	public void setRecord(ArrayList<String[]> record) {
		this.record = record;
	}
	
	
}
