package assign3;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class DBTableModel extends AbstractTableModel {
	
	private String[] columnNames = {"Metropolis", "Continent", "Population"};
	private List<Row> rows;
	
	/**
	 * constuctor
	 * @param rows the data object that supports the table model
	 */
	public DBTableModel(List<Row> rows) {
		this.rows = rows;
	}

	/**
	 * returns the number of columns
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * returns the number of rows
	 */
	@Override
	public int getRowCount() {
		return rows.size();
	}

	/**
	 * returns the value that corresponds to a particular component of Row in a given row
	 * @param rowCount the index of the row
	 * @param whichMember member 1-3
	 */
	@Override
	public Object getValueAt(int rowCount, int whichMember) {
		if(whichMember==0){
			return rows.get(rowCount).getMetropolis();
		}
		if(whichMember==1){
			return rows.get(rowCount).getContinent();
		}
		if(whichMember==2){
			return rows.get(rowCount).getPopulation();
		}
		return null;
	}
    
	/**
	 * returns the name of a column at specified index
	 * @param index the index of the column to be named
	 */
    @Override
    public String getColumnName(int index) {
    	return columnNames[index];
    }

}
