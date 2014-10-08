package br.com.org.jswitch.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import br.com.org.jswitch.model.JDK;

public class JDKTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6927900094627966098L;

	private List<JDK> jdks = new ArrayList<JDK>();
	
	public void addRow(JDK jdk){
		jdks.add(jdk);
	}
	
	public List<JDK> getDataRows(){
		return jdks;
	}
	
	public boolean isEmpty(){
		return jdks.isEmpty();
	}

	public JDKTableModel(List<JDK> jdks) {
		this.jdks = jdks;
	}

	@Override
	 public String getColumnName(int column) {
	   switch (column) {
	   case 0:
	     return "Nome";
	   case 1:
	     return "Diretório";
	   }
	   return "";
	 }
	
	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return jdks.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		JDK n = jdks.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return n.getName();
		case 1:
			return n.getPath();
		}
		return null;
	}
}