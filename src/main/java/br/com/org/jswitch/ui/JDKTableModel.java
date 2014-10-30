package br.com.org.jswitch.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import br.com.org.jswitch.control.OperationSystemManager;
import br.com.org.jswitch.model.JDK;

/**
 * 
 * @author Anderson
 *
 */
public class JDKTableModel extends AbstractTableModel {

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	if (columnIndex == 0) {
	    return true;
	} else
	    return false;
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = -6927900094627966098L;

    private List<JDK> jdks = new ArrayList<JDK>();

    OperationSystemManager operationSystemManager = new OperationSystemManager();

    private final String[] columnNames = new String[] {
	    "Name", "Diretório"
    };

    public void addRow(JDK jdk) {
	operationSystemManager.ajustNames(jdk,jdks);
	jdks.add(jdk);
    }

    public List<JDK> getDataRows() {
	return jdks;
    }

    public boolean isEmpty() {
	return jdks.isEmpty();
    }

    public JDKTableModel(List<JDK> jdks) {
	List<JDK> dest = new ArrayList<JDK>(jdks);
	Set<JDK> set = new HashSet<JDK>(dest);
	Set<String> names = new HashSet<String>();
	  for (JDK newJDK : set) {
		if(!names.add(newJDK.getName())){
		   newJDK.setName(newJDK.getName()+"(1)");
	    }
	}
	this.jdks = new ArrayList<JDK>(set);
    }

    @Override
    public String getColumnName(int column) {
	return columnNames[column];
    }

    public int getColumnCount() {
	return columnNames.length;
    }

    public int getRowCount() {
	return jdks.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
	JDK jdkRow = jdks.get(rowIndex);

	switch (columnIndex) {
	case 0:
	    return jdkRow.getName();
	case 1:
	    return jdkRow.getPath();
	}
	return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	JDK row = jdks.get(rowIndex);
	       if(0 == columnIndex) {
	           row.setName((String) aValue);
	       }
	       else if(1 == columnIndex) {
	           row.setPath((String) aValue);
	       }
    }
    
    
}