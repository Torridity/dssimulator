/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.model;

import de.tor.tribes.dssim.types.UnitHolder;
import de.tor.tribes.dssim.util.UnitManager;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Charon
 */
public class ResultTableModel extends DefaultTableModel {

    private static ResultTableModel SINGLETON = null;
    private Class[] columnClasses = columnClasses = new Class[]{Object.class, String.class, Integer.class, Double.class, Object.class, Integer.class, Double.class, Object.class};
    private String[] columnNames = new String[]{"", "Einheit", "Angreifer", "Tech", "", "Verteidiger", "Tech", ""};

    ResultTableModel() {
        setupModel();
    }

    public static synchronized ResultTableModel getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new ResultTableModel();
        }
        return SINGLETON;
    }

    public void setupModel() {

        if (UnitManager.getSingleton().getUnits().length > 11) {
            columnNames = new String[]{"", "spear", "sword", "axe", "archer", "spy", "light", "marcher", "heavy", "ram", "catapult", "knight", "snob"};
            columnClasses = new Class[]{String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class};
        } else {
            columnNames = new String[]{"", "spear", "sword", "axe", "spy", "light", "heavy", "ram", "catapult", "snob"};
            columnClasses = new Class[]{String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class};
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void clear() {
        while (getRowCount() != 0) {
            removeRow(0);
        }
    }
}
