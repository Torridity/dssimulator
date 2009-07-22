/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DSWorkbenchSimulatorFrame.java
 *
 * Created on 19.07.2009, 16:45:19
 */
package de.tor.tribes.dssim.ui;

import de.tor.tribes.dssim.Constants;
import de.tor.tribes.dssim.algo.NewSimulator;
import de.tor.tribes.dssim.algo.OldSimulator;
import de.tor.tribes.dssim.editor.SpreadSheetCellEditor;
import de.tor.tribes.dssim.editor.TechLevelCellEditor;
import de.tor.tribes.dssim.model.ResultTableModel;
import de.tor.tribes.dssim.model.SimulatorTableModel;
import de.tor.tribes.dssim.renderer.TableHeaderRenderer;
import de.tor.tribes.dssim.renderer.UnitTableCellRenderer;
import de.tor.tribes.dssim.types.AbstractUnitElement;
import de.tor.tribes.dssim.types.SimulatorResult;
import de.tor.tribes.dssim.types.UnitHolder;
import de.tor.tribes.dssim.util.ImageManager;
import de.tor.tribes.dssim.util.UnitManager;
import java.awt.Color;
import java.awt.Component;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Charon
 */
public class DSWorkbenchSimulatorFrame extends javax.swing.JFrame {

    private NewSimulator sim = null;

    /** Creates new form DSWorkbenchSimulatorFrame */
    public DSWorkbenchSimulatorFrame() {
        initComponents();
        sim = new NewSimulator();

        try {
            UnitManager.getSingleton().parseUnits();
            ImageManager.loadUnitIcons();
            SimulatorTableModel.getSingleton().setupModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        buildTables();
        buildResultTable(new SimulatorResult());
    }

    private void buildTables() {
        //build attacker table
        SimulatorTableModel attackerModel = SimulatorTableModel.getSingleton();
        jAttackerTable.setModel(attackerModel);
        jAttackerTable.setDefaultEditor(Double.class, new TechLevelCellEditor(3));
        jAttackerTable.setDefaultEditor(Integer.class, new SpreadSheetCellEditor());
        jAttackerTable.setDefaultRenderer(String.class, new UnitTableCellRenderer());
        if (attackerModel.getColumnCount() == 8) {
            //old model (empty, unit, attacker, tech,empty, defender, tech, empty)
            jAttackerTable.getColumnModel().getColumn(1).setMaxWidth(60);
            jAttackerTable.getColumnModel().getColumn(2).setMaxWidth(80);
            jAttackerTable.getColumnModel().getColumn(3).setMaxWidth(40);
            jAttackerTable.getColumnModel().getColumn(4).setMaxWidth(10);
            jAttackerTable.getColumnModel().getColumn(5).setMaxWidth(80);
            jAttackerTable.getColumnModel().getColumn(6).setMaxWidth(40);
            jAttackerTable.invalidate();
            for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
                attackerModel.addRow(new Object[]{"", unit.getPlainName(), 0, 1.0, "", 0, 1.0, ""});
            }
            jAttackerTable.revalidate();
        } else {
            //new model (empty, unit, attacker, empty, defender, empty)
            jAttackerTable.getColumnModel().getColumn(1).setMaxWidth(60);
            jAttackerTable.getColumnModel().getColumn(2).setMaxWidth(80);
            jAttackerTable.getColumnModel().getColumn(3).setMaxWidth(10);
            jAttackerTable.getColumnModel().getColumn(4).setMaxWidth(80);
            jAttackerTable.invalidate();
            for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
                attackerModel.addRow(new Object[]{"", unit.getPlainName(), 0, "", 0, ""});
            }
            jAttackerTable.revalidate();
        }

        jAttackerTable.setRowHeight(20);
        jScrollPane1.getViewport().setBackground(Constants.DS_BACK_LIGHT);
        jAttackerTable.setBackground(Constants.DS_BACK_LIGHT);
        attackerModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                fireCalculateEvent();
            }
        });

        //add header renderers
        for (int i = 0; i < jAttackerTable.getColumnCount(); i++) {
            jAttackerTable.getColumnModel().getColumn(i).setHeaderRenderer(new TableHeaderRenderer());
        }

        jResultTable.setModel(ResultTableModel.getSingleton());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jNightBonus = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jAttackerTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jResultTable = new javax.swing.JTable();
        jWallInfo = new javax.swing.JLabel();
        jWallSpinner = new javax.swing.JSpinner();
        jMoralSpinner = new javax.swing.JSpinner();
        jLuckSpinner = new javax.swing.JSpinner();
        jBuildingInfo = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jCataTargetSpinner = new javax.swing.JSpinner();
        jOffToKillBox = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Simulator");

        jButton1.setText("Schließen");

        jPanel1.setBackground(new java.awt.Color(239, 235, 223));

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/wall.png"))); // NOI18N

        jLabel29.setText("Moral");
        jLabel29.setMaximumSize(new java.awt.Dimension(30, 14));
        jLabel29.setMinimumSize(new java.awt.Dimension(30, 14));
        jLabel29.setPreferredSize(new java.awt.Dimension(30, 14));

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/klee.png"))); // NOI18N
        jLabel30.setMaximumSize(new java.awt.Dimension(30, 14));
        jLabel30.setMinimumSize(new java.awt.Dimension(30, 14));
        jLabel30.setPreferredSize(new java.awt.Dimension(30, 14));

        jNightBonus.setText("Nachbonus");
        jNightBonus.setOpaque(false);
        jNightBonus.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireNightBonusStateChangedEvent(evt);
            }
        });

        jScrollPane1.setBackground(new java.awt.Color(225, 213, 190));
        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(225, 213, 190), 1, true));
        jScrollPane1.setMaximumSize(new java.awt.Dimension(160, 404));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(160, 404));
        jScrollPane1.setOpaque(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(160, 404));

        jAttackerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jAttackerTable.setGridColor(new java.awt.Color(225, 213, 190));
        jAttackerTable.setOpaque(false);
        jScrollPane1.setViewportView(jAttackerTable);

        jScrollPane3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(225, 213, 190), 1, true));

        jResultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jResultTable.setShowHorizontalLines(false);
        jScrollPane3.setViewportView(jResultTable);

        jWallInfo.setBackground(new java.awt.Color(255, 255, 255));
        jWallInfo.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0)));
        jWallInfo.setMaximumSize(new java.awt.Dimension(0, 20));
        jWallInfo.setMinimumSize(new java.awt.Dimension(0, 20));
        jWallInfo.setPreferredSize(new java.awt.Dimension(0, 20));

        jWallSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 20, 1));
        jWallSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireStateChangedEvent(evt);
            }
        });

        jMoralSpinner.setModel(new javax.swing.SpinnerNumberModel(100, 30, 100, 1));
        jMoralSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireStateChangedEvent(evt);
            }
        });

        jLuckSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, -25.0d, 25.0d, 0.1d));
        jLuckSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireStateChangedEvent(evt);
            }
        });

        jBuildingInfo.setBackground(new java.awt.Color(255, 255, 255));
        jBuildingInfo.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0)));

        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/main.png"))); // NOI18N

        jCataTargetSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));
        jCataTargetSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireStateChangedEvent(evt);
            }
        });

        jOffToKillBox.setText("OffToKill");
        jOffToKillBox.setOpaque(false);

        jLabel3.setText("Ergebnisse");
        jLabel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel3.setMaximumSize(new java.awt.Dimension(150, 20));
        jLabel3.setMinimumSize(new java.awt.Dimension(150, 20));
        jLabel3.setPreferredSize(new java.awt.Dimension(150, 20));

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addComponent(jWallInfo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLuckSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(jMoralSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jNightBonus)
                        .addGap(79, 79, 79)
                        .addComponent(jOffToKillBox)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel31))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jCataTargetSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                            .addComponent(jWallSpinner, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)))
                    .addComponent(jBuildingInfo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMoralSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jNightBonus)
                            .addComponent(jOffToKillBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLuckSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE)
                            .addComponent(jWallSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                            .addComponent(jCataTargetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(10, 10, 10)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jWallInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBuildingInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fireStateChangedEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireStateChangedEvent
        fireCalculateEvent();
}//GEN-LAST:event_fireStateChangedEvent

    private void fireNightBonusStateChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireNightBonusStateChangedEvent
        fireCalculateEvent();
    }//GEN-LAST:event_fireNightBonusStateChangedEvent

    private void fireCalculateEvent() {
        Hashtable<UnitHolder, AbstractUnitElement> off = SimulatorTableModel.getSingleton().getOff();
        Hashtable<UnitHolder, AbstractUnitElement> def = SimulatorTableModel.getSingleton().getDef();
        boolean nightBonus = jNightBonus.isSelected();
        int wallLevel = (Integer) jWallSpinner.getValue();
        int cataTarget = (Integer) jCataTargetSpinner.getValue();
        double luck = (Double) jLuckSpinner.getValue();
        double moral = (Integer) jMoralSpinner.getValue();
        if (!jOffToKillBox.isSelected()) {
            SimulatorResult result = sim.calculate(off, def, nightBonus, luck, moral, wallLevel, cataTarget);
            buildResultTable(result);
        } else {
            Integer count = 1;
            calculateOffsToKill(off, def, nightBonus, luck, moral, wallLevel, cataTarget, count);
        }
    }

    private void calculateOffsToKill(Hashtable<UnitHolder, AbstractUnitElement> pOff, Hashtable<UnitHolder, AbstractUnitElement> pDef, boolean pNightBonus, double pLuck, double pMoral, int pWallLevel, int pBuildingLevel, Integer cnt) {
        /*sim.calculate(pOff, pDef, pNightBonus, pLuck, pMoral, pWallLevel, pBuildingLevel);
        boolean won = sim.isWin();
        double offDecrement = sim.getOffDecrement();
        double defDecrement = sim.getDefDecrement();
        int wallResult = sim.getWallResult();
        int cataResult = sim.getCataResult();
        if (won) {
        System.out.println("Won after " + cnt + " rounds");
        buildResultTable(won, offDecrement, defDecrement, wallResult, cataResult);
        } else {
        //get remaining troops
        for (int i = 0; i < UnitManager.getSingleton().getUnits().length; i++) {
        UnitHolder unit = UnitManager.getSingleton().getUnits()[i];
        AbstractUnitElement part = pDef.get(unit);
        if (part != null) {
        int defUnitCount = part.getCount();
        int defLoss = 0;
        if (defDecrement >= 1) {
        defLoss = defUnitCount;
        } else {
        defLoss = (int) Math.round(defDecrement * (double) defUnitCount);
        }
        part.setCount(defUnitCount - defLoss);
        pDef.put(unit, part);
        }
        }
        calculateOffsToKill(pOff, pDef, pNightBonus, pLuck, pMoral, wallResult, cataResult, new Integer(cnt + 1));
        }*/
    }

    private void buildResultTable(SimulatorResult pResult) {

        ResultTableModel.getSingleton().clear();

        // <editor-fold defaultstate="collapsed" desc="Build header renderer">
        for (int i = 0; i < jResultTable.getColumnCount(); i++) {
            jResultTable.getColumn(jResultTable.getColumnName(i)).setHeaderRenderer(new UnitTableCellRenderer());
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Build result table rows">
        List<Object> attackerBefore = new LinkedList<Object>();
        attackerBefore.add("Angreifer");
        List<Object> attackerLosses = new LinkedList<Object>();
        attackerLosses.add("Verluste");
        List<Object> attackerSurvivors = new LinkedList<Object>();
        attackerSurvivors.add("Überlebende");
        List<Object> defenderBefore = new LinkedList<Object>();
        defenderBefore.add("Verteidiger");
        List<Object> defenderLosses = new LinkedList<Object>();
        defenderLosses.add("Verluste");
        List<Object> defenderSurvivors = new LinkedList<Object>();
        defenderSurvivors.add("Überlebende");

        Hashtable<UnitHolder, AbstractUnitElement> off = SimulatorTableModel.getSingleton().getOff();
        Hashtable<UnitHolder, AbstractUnitElement> def = SimulatorTableModel.getSingleton().getDef();
        for (int i = 1; i < ResultTableModel.getSingleton().getColumnCount(); i++) {
            UnitHolder u = UnitManager.getSingleton().getUnitByPlainName(ResultTableModel.getSingleton().getColumnName(i));
            AbstractUnitElement offElement = off.get(u);
            AbstractUnitElement defElement = def.get(u);
            //set units of type before
            attackerBefore.add(offElement.getCount());
            defenderBefore.add(defElement.getCount());
            attackerSurvivors.add(pResult.getSurvivingOff().get(u).getCount());
            defenderSurvivors.add(pResult.getSurvivingDef().get(u).getCount());
            attackerLosses.add(offElement.getCount() - pResult.getSurvivingOff().get(u).getCount());
            defenderLosses.add(defElement.getCount() - pResult.getSurvivingDef().get(u).getCount());

        /*            //set units of type lost
        attackerLosses.add(((pOffDecrement >= 1) ? offElement.getCount() : (int) Math.round(pOffDecrement * (double) offElement.getCount())));
        defenderLosses.add(((pDefDecrement >= 1) ? defElement.getCount() : (int) Math.round(pDefDecrement * (double) defElement.getCount())));
        //set units of type survived
        attackerSurvivors.add(((pOffDecrement >= 1) ? 0 : offElement.getCount() - (int) Math.round(pOffDecrement * (double) offElement.getCount())));
        defenderSurvivors.add(((pDefDecrement >= 1) ? 0 : defElement.getCount() - (int) Math.round(pDefDecrement * (double) defElement.getCount())));*/
        }

        jResultTable.invalidate();
        ResultTableModel.getSingleton().addRow(attackerBefore.toArray());
        ResultTableModel.getSingleton().addRow(attackerLosses.toArray());
        ResultTableModel.getSingleton().addRow(attackerSurvivors.toArray());
        ResultTableModel.getSingleton().addRow(new Object[]{});
        ResultTableModel.getSingleton().addRow(defenderBefore.toArray());
        ResultTableModel.getSingleton().addRow(defenderLosses.toArray());
        ResultTableModel.getSingleton().addRow(defenderSurvivors.toArray());

        jResultTable.revalidate();
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Winner/Loser color renderer">
        final boolean won = pResult.isWin();
        DefaultTableCellRenderer winLossRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
                c.setBackground(Constants.DS_BACK);
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                try {
                    ((JLabel) c).setText(Integer.toString((Integer) value));
                } catch (Exception e) {
                    ((JLabel) c).setText((String) value);
                }

                if (won) {
                    if (row == 0 || row == 1 || row == 2) {
                        ((JLabel) c).setBackground(Color.GREEN);
                    } else if (row == 4 || row == 5 || row == 6) {
                        ((JLabel) c).setBackground(Color.RED);
                    }
                } else {
                    if (row == 0 || row == 1 || row == 2) {
                        ((JLabel) c).setBackground(Color.RED);
                    } else if (row == 4 || row == 5 || row == 6) {
                        ((JLabel) c).setBackground(Color.GREEN);
                    }
                }
                return c;
            }
        };
        // </editor-fold>

        jScrollPane3.getViewport().setBackground(Constants.DS_BACK_LIGHT);
        jResultTable.setDefaultRenderer(Integer.class, winLossRenderer);
        jResultTable.setDefaultRenderer(String.class, winLossRenderer);
        jResultTable.getColumnModel().getColumn(0).setMinWidth(100);
        jResultTable.getColumnModel().getColumn(0).setResizable(false);

        int wall = (Integer) jWallSpinner.getValue();
        if (wall != pResult.getWallLevel()) {
            jWallInfo.setText("Wall zerstört von Stufe " + wall + " auf Stufe " + pResult.getWallLevel());
        } else if (wall == 0) {
            jWallInfo.setText("Wall nicht vorhanden");
        } else {
            jWallInfo.setText("Wall nicht beschädigt");
        }

        int building = (Integer) jCataTargetSpinner.getValue();
        if (building != pResult.getBuildingLevel()) {
            jBuildingInfo.setText("Gebäude zerstört von Stufe " + building + " auf Stufe " + pResult.getBuildingLevel());
        } else if (building == 0) {
            jBuildingInfo.setText("Gebäude nicht vorhanden");
        } else {
            jBuildingInfo.setText("Gebäude nicht beschädigt");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        System.setProperty("swing.defaultlaf", "net.sourceforge.napkinlaf.NapkinLookAndFeel");


        //UIManager.put("Panel.background", Constants.DS_BACK);
      /*  UIManager.put("Label.background", Constants.DS_BACK);
        UIManager.put("MenuBar.background", Constants.DS_BACK);
        UIManager.put("ScrollPane.background", Constants.DS_BACK);
        UIManager.put("Button.background", Constants.DS_BACK);
        UIManager.put("TabbedPane.background", Constants.DS_BACK);
        UIManager.put("SplitPane.background", Constants.DS_BACK);
        UIManager.put("Separator.background", Constants.DS_BACK);
        UIManager.put("Menu.background", Constants.DS_BACK);
        UIManager.put("OptionPane.background", Constants.DS_BACK);
        UIManager.put("ToolBar.background", Constants.DS_BACK);*/
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new DSWorkbenchSimulatorFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable jAttackerTable;
    private javax.swing.JLabel jBuildingInfo;
    private javax.swing.JButton jButton1;
    private javax.swing.JSpinner jCataTargetSpinner;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSpinner jLuckSpinner;
    private javax.swing.JSpinner jMoralSpinner;
    private javax.swing.JCheckBox jNightBonus;
    private javax.swing.JCheckBox jOffToKillBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTable jResultTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel jWallInfo;
    private javax.swing.JSpinner jWallSpinner;
    // End of variables declaration//GEN-END:variables
}