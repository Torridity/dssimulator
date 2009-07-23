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
import de.tor.tribes.dssim.algo.AbstractSimulator;
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
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Charon
 */
public class DSWorkbenchSimulatorFrame extends javax.swing.JFrame {

    private AbstractSimulator sim = null;
    private SimulatorResult lastResult = null;

    /** Creates new form DSWorkbenchSimulatorFrame */
    public DSWorkbenchSimulatorFrame() {
        initComponents();
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

            @Override
            public void eventDispatched(AWTEvent event) {
                if (((KeyEvent) event).getID() == KeyEvent.KEY_RELEASED) {
                    KeyEvent e = (KeyEvent) event;
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        fireCalculateEvent();
                    }
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
        try {
            ImageManager.loadUnitIcons();
        } catch (Exception e) {
        }
        buildServerList();
        jServerList.setSelectedItem("de4");
        fireServerChangedEvent(null);
    }

    private void buildServerList() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (int i = 4; i <= 47; i++) {
            if (i != 3 && i != 9 && i != 34) {
                model.addElement("de" + i);
            }
        }
        jServerList.setModel(model);
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
        /* attackerModel.addTableModelListener(new TableModelListener() {

        @Override
        public void tableChanged(TableModelEvent e) {
        fireCalculateEvent();
        }
        });*/

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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jNukeInfo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jServerList = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("A*Star 1.0");

        jPanel1.setBackground(new java.awt.Color(239, 235, 223));

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/wall.png"))); // NOI18N

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/masks.png"))); // NOI18N
        jLabel29.setMaximumSize(new java.awt.Dimension(30, 14));
        jLabel29.setMinimumSize(new java.awt.Dimension(30, 14));
        jLabel29.setPreferredSize(new java.awt.Dimension(30, 14));

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/klee.png"))); // NOI18N
        jLabel30.setMaximumSize(new java.awt.Dimension(30, 14));
        jLabel30.setMinimumSize(new java.awt.Dimension(30, 14));
        jLabel30.setPreferredSize(new java.awt.Dimension(30, 14));

        jNightBonus.setText("Nachtbonus");
        jNightBonus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/sun.gif"))); // NOI18N
        jNightBonus.setMaximumSize(new java.awt.Dimension(85, 18));
        jNightBonus.setMinimumSize(new java.awt.Dimension(85, 18));
        jNightBonus.setOpaque(false);
        jNightBonus.setPreferredSize(new java.awt.Dimension(85, 18));
        jNightBonus.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/moon.png"))); // NOI18N
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
        jWallInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/wall.png"))); // NOI18N
        jWallInfo.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0)));
        jWallInfo.setMaximumSize(new java.awt.Dimension(0, 20));
        jWallInfo.setMinimumSize(new java.awt.Dimension(0, 20));
        jWallInfo.setPreferredSize(new java.awt.Dimension(0, 20));

        jWallSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 20, 1));
        jWallSpinner.setToolTipText("Wallstufe");
        jWallSpinner.setMaximumSize(new java.awt.Dimension(83, 18));
        jWallSpinner.setMinimumSize(new java.awt.Dimension(83, 18));
        jWallSpinner.setPreferredSize(new java.awt.Dimension(83, 18));
        jWallSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireStateChangedEvent(evt);
            }
        });

        jMoralSpinner.setModel(new javax.swing.SpinnerNumberModel(100, 30, 100, 1));
        jMoralSpinner.setToolTipText("Moral");
        jMoralSpinner.setMaximumSize(new java.awt.Dimension(83, 18));
        jMoralSpinner.setMinimumSize(new java.awt.Dimension(83, 18));
        jMoralSpinner.setPreferredSize(new java.awt.Dimension(83, 18));
        jMoralSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireStateChangedEvent(evt);
            }
        });

        jLuckSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, -25.0d, 25.0d, 0.1d));
        jLuckSpinner.setToolTipText("Glück");
        jLuckSpinner.setMaximumSize(new java.awt.Dimension(83, 18));
        jLuckSpinner.setMinimumSize(new java.awt.Dimension(83, 18));
        jLuckSpinner.setPreferredSize(new java.awt.Dimension(83, 18));
        jLuckSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireStateChangedEvent(evt);
            }
        });

        jBuildingInfo.setBackground(new java.awt.Color(255, 255, 255));
        jBuildingInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/main.png"))); // NOI18N
        jBuildingInfo.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0)));

        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/main.png"))); // NOI18N

        jCataTargetSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));
        jCataTargetSpinner.setToolTipText("Gebäudestufe");
        jCataTargetSpinner.setMaximumSize(new java.awt.Dimension(83, 18));
        jCataTargetSpinner.setMinimumSize(new java.awt.Dimension(83, 18));
        jCataTargetSpinner.setPreferredSize(new java.awt.Dimension(83, 18));
        jCataTargetSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireStateChangedEvent(evt);
            }
        });

        jLabel3.setText("Ergebnisse");
        jLabel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel3.setMaximumSize(new java.awt.Dimension(150, 20));
        jLabel3.setMinimumSize(new java.awt.Dimension(150, 20));
        jLabel3.setPreferredSize(new java.awt.Dimension(150, 20));

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jNukeInfo.setBackground(new java.awt.Color(255, 255, 255));
        jNukeInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/bomb_small.png"))); // NOI18N
        jNukeInfo.setText("(Einzelangriff)");
        jNukeInfo.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLuckSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMoralSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jNightBonus, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel31)
                            .addComponent(jLabel28))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jCataTargetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jWallSpinner, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jWallInfo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                    .addComponent(jBuildingInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                    .addComponent(jNukeInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMoralSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jNightBonus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLuckSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28)
                            .addComponent(jWallSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCataTargetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(10, 10, 10)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jWallInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBuildingInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jNukeInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/bomb.png"))); // NOI18N
        jButton2.setToolTipText("Berechnen, nach wievielen Angriffen alle Verteidiger besiegt sind");
        jButton2.setMaximumSize(new java.awt.Dimension(50, 33));
        jButton2.setMinimumSize(new java.awt.Dimension(50, 33));
        jButton2.setPreferredSize(new java.awt.Dimension(50, 33));
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireBombDefEvent(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/refresh.png"))); // NOI18N
        jButton3.setToolTipText("Überlebenden Truppen nochmal angreifen");
        jButton3.setMaximumSize(new java.awt.Dimension(50, 33));
        jButton3.setMinimumSize(new java.awt.Dimension(50, 33));
        jButton3.setPreferredSize(new java.awt.Dimension(50, 33));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAttackAgainEvent(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/exit.png"))); // NOI18N
        jButton4.setToolTipText("Beenden");
        jButton4.setMaximumSize(new java.awt.Dimension(50, 33));
        jButton4.setMinimumSize(new java.awt.Dimension(50, 33));
        jButton4.setPreferredSize(new java.awt.Dimension(50, 33));
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireExitEvent(evt);
            }
        });

        jServerList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireServerChangedEvent(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Server");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jServerList, 0, 57, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jServerList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(516, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void fireBombDefEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireBombDefEvent
        Hashtable<UnitHolder, AbstractUnitElement> off = SimulatorTableModel.getSingleton().getOff();
        Hashtable<UnitHolder, AbstractUnitElement> def = SimulatorTableModel.getSingleton().getDef();
        boolean nightBonus = jNightBonus.isSelected();
        int wallLevel = (Integer) jWallSpinner.getValue();
        int cataTarget = (Integer) jCataTargetSpinner.getValue();
        double luck = (Double) jLuckSpinner.getValue();
        double moral = (Integer) jMoralSpinner.getValue();
        SimulatorResult result = sim.bunkerBuster(off, def, nightBonus, luck, moral, wallLevel, cataTarget);
        int nukes = result.getNukes();
        if (nukes == Integer.MAX_VALUE) {
            jNukeInfo.setText("Dorf clean nach mehr als 1000 Angriffen (Abbruch)");
        } else {
            jNukeInfo.setText("Dorf clean nach " + ((nukes == 1) ? " 1 Angriff" : " " + nukes + " Angriffen"));
        }
        buildResultTable(result);
    }//GEN-LAST:event_fireBombDefEvent

    private void fireExitEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireExitEvent
        dispose();
    }//GEN-LAST:event_fireExitEvent

    private void fireAttackAgainEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAttackAgainEvent
        SimulatorTableModel.getSingleton().setDef(lastResult.getSurvivingDef());
        fireCalculateEvent();
    }//GEN-LAST:event_fireAttackAgainEvent

    private void fireServerChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireServerChangedEvent
        if (evt == null || evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            try {
                UnitManager.getSingleton().parseUnits((String) jServerList.getSelectedItem());
                ResultTableModel.getSingleton().clear();
                SimulatorTableModel.getSingleton().setupModel();
                ResultTableModel.getSingleton().setupModel();
                if (SimulatorTableModel.getSingleton().getColumnCount() == 6) {
                    sim = new NewSimulator();
                    lastResult = null;
                } else {
                    sim = new OldSimulator();
                    lastResult = null;
                }
                buildTables();
                buildResultTable(new SimulatorResult());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_fireServerChangedEvent

    private void fireCalculateEvent() {
        Hashtable<UnitHolder, AbstractUnitElement> off = SimulatorTableModel.getSingleton().getOff();
        Hashtable<UnitHolder, AbstractUnitElement> def = SimulatorTableModel.getSingleton().getDef();
        boolean nightBonus = jNightBonus.isSelected();
        int wallLevel = (Integer) jWallSpinner.getValue();
        int cataTarget = (Integer) jCataTargetSpinner.getValue();
        double luck = (Double) jLuckSpinner.getValue();
        double moral = (Integer) jMoralSpinner.getValue();

        SimulatorResult result = sim.calculate(off, def, nightBonus, luck, moral, wallLevel, cataTarget);
        jNukeInfo.setText("(Einzelangriff)");
        buildResultTable(result);
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
                        ((JLabel) c).setBackground(Constants.WINNER_GREEN);
                    } else if (row == 4 || row == 5 || row == 6) {
                        ((JLabel) c).setBackground(Constants.LOSER_RED);
                    }
                } else {
                    if (row == 0 || row == 1 || row == 2) {
                        ((JLabel) c).setBackground(Constants.LOSER_RED);
                    } else if (row == 4 || row == 5 || row == 6) {
                        ((JLabel) c).setBackground(Constants.WINNER_GREEN);
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
        lastResult = pResult;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        System.setProperty("swing.defaultlaf", "net.sourceforge.napkinlaf.NapkinLookAndFeel");
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new DSWorkbenchSimulatorFrame().setVisible(true);
            }
        });

    /*  List<Integer> nonos = new LinkedList<Integer>();
    nonos.add(3);
    nonos.add(9);
    nonos.add(34);
    for (int i = 1; i <= 47; i++) {
    if (!nonos.contains(i)) {
    System.out.println("Getting units from server de" + i);
    try {
    String url = "http://de" + i + ".die-staemme.de/interface.php?func=get_unit_info";
    URLConnection ucon = new URL(url).openConnection();
    BufferedReader reader = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
    FileWriter fout = new FileWriter("./servers/units_de" + i + ".xml");
    String line = null;
    while ((line = reader.readLine()) != null) {
    fout.write(line);
    }
    fout.flush();
    fout.close();
    //"/interface.php?func=get_config"
    } catch (Exception e) {
    e.printStackTrace();
    }
    }
    }*/


    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable jAttackerTable;
    private javax.swing.JLabel jBuildingInfo;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JSpinner jCataTargetSpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSpinner jLuckSpinner;
    private javax.swing.JSpinner jMoralSpinner;
    private javax.swing.JCheckBox jNightBonus;
    private javax.swing.JLabel jNukeInfo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTable jResultTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JComboBox jServerList;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel jWallInfo;
    private javax.swing.JSpinner jWallSpinner;
    // End of variables declaration//GEN-END:variables
}
