/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.model;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Charon
 */
public class SimulatorTableModel extends DefaultTableModel {

    private Class[] columnClasses = new Class[]{
        String.class, Integer.class, Double.class
    };
    String[] columnNames = new String[]{"Einheit", "Anzahl", "Tech"};

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
        return (columnIndex != 0);
    }
}
