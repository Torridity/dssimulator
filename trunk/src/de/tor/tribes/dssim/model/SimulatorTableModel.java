/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.model;

import de.tor.tribes.dssim.types.AbstractUnitElement;
import de.tor.tribes.dssim.types.KnightItem;
import de.tor.tribes.dssim.types.UnitHolder;
import de.tor.tribes.dssim.util.ConfigManager;
import de.tor.tribes.dssim.util.UnitManager;
import java.util.Hashtable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Charon
 */
public class SimulatorTableModel extends DefaultTableModel {

    private static SimulatorTableModel SINGLETON = null;
    private Class[] columnClasses = columnClasses = new Class[]{Object.class, String.class, Integer.class, Double.class, Object.class, Integer.class, Double.class, Object.class};
    private String[] columnNames = new String[]{"", "Einheit", "Angreifer", "Tech", "", "Verteidiger", "Tech", ""};

    SimulatorTableModel() {
        setupModel();
    }

    public static synchronized SimulatorTableModel getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new SimulatorTableModel();
        }
        return SINGLETON;
    }

    public void reset() {
        SINGLETON = null;
    }

    public void setupModel() {
        if (ConfigManager.getSingleton().getTech() == ConfigManager.ID_SIMPLE_TECH) {
            columnNames = new String[]{"", "Einheit", "Angreifer", "", "Verteidiger", ""};
            columnClasses = new Class[]{KnightItem.class, String.class, Integer.class, Object.class, Integer.class, KnightItem.class};
        } else {
            columnNames = new String[]{"", "Einheit", "Angreifer", "Tech", "", "Verteidiger", "Tech", ""};
            columnClasses = new Class[]{Object.class, String.class, Integer.class, Double.class, Object.class, Integer.class, Double.class, Object.class};
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
        if (getValueAt(rowIndex, columnIndex) != null) {
            return true;
        }
        return false;
    }

    public Hashtable<UnitHolder, AbstractUnitElement> getOff() {
        Hashtable<UnitHolder, AbstractUnitElement> result = new Hashtable<UnitHolder, AbstractUnitElement>();
        if (ConfigManager.getSingleton().getTech() == ConfigManager.ID_SIMPLE_TECH) {
            //return new world values
            for (int i = 0; i < getRowCount(); i++) {
                Integer count = (Integer) getValueAt(i, 2);
                //add element if cout larger than 0
                UnitHolder unit = (UnitHolder) UnitManager.getSingleton().getUnitByPlainName((String) getValueAt(i, 1));
                int tech = 1;
                AbstractUnitElement element = new AbstractUnitElement(unit, count, tech);
                result.put(unit, element);
            }//end of all rows
        } else {
            for (int i = 0; i < getRowCount(); i++) {
                Integer count = (Integer) getValueAt(i, 2);
                //add element if cout larger than 0
                UnitHolder unit = (UnitHolder) UnitManager.getSingleton().getUnitByPlainName((String) getValueAt(i, 1));
                int tech = (int) Math.rint((Double) getValueAt(i, 3));
                AbstractUnitElement element = new AbstractUnitElement(unit, count, tech);
                result.put(unit, element);
            }//end of all rows
        }//end of getting table
        //return final result
        return result;
    }

    public Hashtable<UnitHolder, AbstractUnitElement> getDef() {
        Hashtable<UnitHolder, AbstractUnitElement> result = new Hashtable<UnitHolder, AbstractUnitElement>();
        if (ConfigManager.getSingleton().getTech() == ConfigManager.ID_SIMPLE_TECH) {
            //return new world values
            for (int i = 0; i < getRowCount(); i++) {
                Integer count = (Integer) getValueAt(i, 4);
                //add element if cout larger than 0
                UnitHolder unit = (UnitHolder) UnitManager.getSingleton().getUnitByPlainName((String) getValueAt(i, 1));
                int tech = 1;
                AbstractUnitElement element = new AbstractUnitElement(unit, count, tech);
                result.put(unit, element);
            }//end of all rows
        } else {
            for (int i = 0; i < getRowCount(); i++) {
                Integer count = (Integer) getValueAt(i, 5);
                //add element if cout larger than 0
                UnitHolder unit = (UnitHolder) UnitManager.getSingleton().getUnitByPlainName((String) getValueAt(i, 1));
                int tech = 1;
                Object val = getValueAt(i, 6);

                if (val instanceof Double) {
                    tech = (int) Math.rint((Double) getValueAt(i, 6));
                } else {
                    tech = (Integer) getValueAt(i, 6);
                }

                AbstractUnitElement element = new AbstractUnitElement(unit, count, tech);
                result.put(unit, element);
            }//end of all rows
        }//end of getting table
        //return final result
        return result;
    }

    public void setOffUnitCount(UnitHolder pUnit, int pCount) {
        for (int i = 0; i < getRowCount(); i++) {
            String unitName = (String) getValueAt(i, 1);
            if (unitName != null && unitName.equals(pUnit.getPlainName())) {
                setValueAt(pCount, i, 2);
                return;
            }
        }
    }

    public void setDefUnitCount(UnitHolder pUnit, int pCount) {
        if (ConfigManager.getSingleton().getTech() == ConfigManager.ID_SIMPLE_TECH) {
            for (int i = 0; i < getRowCount(); i++) {
                String unitName = (String) getValueAt(i, 1);
                if (unitName != null && unitName.equals(pUnit.getPlainName())) {
                    setValueAt(pCount, i, 4);
                    return;
                }
            }
        } else {
            for (int i = 0; i < getRowCount(); i++) {
                String unitName = (String) getValueAt(i, 1);
                if (unitName != null && unitName.equals(pUnit.getPlainName())) {
                    setValueAt(pCount, i, 5);
                    return;
                }
            }
        }
    }

    public void setDef(Hashtable<UnitHolder, AbstractUnitElement> pDef) {
        if (ConfigManager.getSingleton().getTech() == ConfigManager.ID_SIMPLE_TECH) {
            //return new world values
            for (int i = 0; i < getRowCount(); i++) {
                String unitName = (String) getValueAt(i, 1);
                UnitHolder unit = UnitManager.getSingleton().getUnitByPlainName(unitName);
                setValueAt(pDef.get(unit).getCount(), i, 4);
            }//end of all rows
        } else {
            for (int i = 0; i < getRowCount(); i++) {
                String unitName = (String) getValueAt(i, 1);
                UnitHolder unit = UnitManager.getSingleton().getUnitByPlainName(unitName);
                setValueAt(pDef.get(unit).getCount(), i, 5);
                setValueAt(pDef.get(unit).getTech(), i, 6);
            }//end of all rows
        }//end of getting table
    }
}
