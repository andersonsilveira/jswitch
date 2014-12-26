package br.com.org.jswitch.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import br.com.org.jswitch.control.JSwitchManager;
import br.com.org.jswitch.model.JDK;

/**
 * 
 * @author Anderson
 *
 */
public class JDKTableModel extends AbstractTableModel {

    private static final ResourceBundle bundle = MessagesHelp.getBundle();

    private static final String NOT = bundle.getString("table.yes");

    private static final String YES = bundle.getString("table.not");

    private static final String NAME = bundle.getString("table.name");

    private static final String DERECTORY = bundle.getString("table.path");

    private static final String CONFIGURED = bundle.getString("table.config");

    private static final String NOT_POSSIBLE_EDIT = bundle.getString("warn.table.edit");

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

    public final Object[] longValues = { "jdk1.7.0_67(x64)", "C:\\Program Files\\Java\\jdk1.6.0_45     ", Boolean.TRUE };

    private List<JDK> jdks = new ArrayList<JDK>();

    JSwitchManager operationSystemManager = new JSwitchManager();

    private final String[] columnNames = new String[] { NAME, DERECTORY, CONFIGURED };

    public void addRow(JDK jdk) {
	Set<JDK> set = new HashSet<JDK>(jdks);
	Set<String> jdkNames = extractNames(jdks);
	if (!set.contains(jdk)) {
	    if (jdkNames.contains(jdk.getName())) {
		jdk.setName(jdk.getName() + "(1)");
	    }
	    jdks.add(jdk);
	}
	fireTableDataChanged();

    }

    private Set<String> extractNames(List<JDK> tmpJdks) {
	Set<String> names = new HashSet<String>();
	for (JDK jdk : tmpJdks) {
	    names.add(jdk.getName());
	}

	return names;

    }

    public List<JDK> getDataRows() {
	return new ArrayList<JDK>(jdks);
    }

    public boolean isEmpty() {
	return jdks.isEmpty();
    }

    public JDKTableModel(List<JDK> jdks) {
	this.jdks = jdks;
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

	case 2:
	    return jdkRow.getInstalled() ? YES : NOT;
	}
	return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	JDK row = jdks.get(rowIndex);
	if (0 == columnIndex) {
	    if (row.getInstalled()) {
		JOptionPane.showMessageDialog(null, NOT_POSSIBLE_EDIT, "JSwitch", JOptionPane.ERROR_MESSAGE);
	    } else {
		row.setName((String) aValue);
	    }
	} else if (1 == columnIndex) {
	    row.setPath((String) aValue);
	} else if (2 == columnIndex) {
	    row.setInstalled((Boolean) aValue);
	}
	fireTableDataChanged();
    }

}