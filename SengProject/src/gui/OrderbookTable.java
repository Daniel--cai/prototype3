package gui;

import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

public class OrderbookTable extends AbstractTableModel {

	private String[] columnNames =  {"#", "Timestamp", "ID", "Price", "Volume"};
	//private Object[][] data = {{"","00:00", new Long(0), new Double(0), new Integer(0)}};
	private LinkedList<Object[]> data2;
	
	 public OrderbookTable() {
	        data2 = new LinkedList<Object[]>();
	 }
	
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	
    public int getColumnCount() {
        return columnNames.length;
      
    }

	public int getRowCount() {
		//return data.length;
		return data2.size();
	}
	public String getColumnName(int col){
		return columnNames[col];
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		//return data[rowIndex][columnIndex];
		return data2.get(rowIndex)[columnIndex];
	}
	
	
	public void setValueAt(Object value, int row, int col) {
		data2.get(row)[col] = value;
	    fireTableCellUpdated(row, col);
	}

	public void setData(LinkedList<Object[]> fakedata) {
		data2 = fakedata;
		fireTableDataChanged();

	}
	   public void addElement(Object[] e) {
	        // Adds the element in the last position in the list
	        data2.add(e);
	        fireTableRowsInserted(data2.size()-1, data2.size()-1);
	    }

}
