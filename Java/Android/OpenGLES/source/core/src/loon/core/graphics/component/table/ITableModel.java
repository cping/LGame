package loon.core.graphics.component.table;

public interface ITableModel {

	public String getColumnName(int columnIndex);

	public int getColumnCount();

	public Object getValue(int row, int column); 

	public int getRowCount();

}