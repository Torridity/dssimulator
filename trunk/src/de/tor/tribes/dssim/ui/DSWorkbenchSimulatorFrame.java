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
import de.tor.tribes.dssim.types.KnightItem;
import de.tor.tribes.dssim.types.SimulatorResult;
import de.tor.tribes.dssim.types.UnitHolder;
import de.tor.tribes.dssim.util.ConfigManager;
import de.tor.tribes.dssim.util.ImageManager;
import de.tor.tribes.dssim.util.UnitManager;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *@TODO Set pala depending on pala item selection
 * @author Charon
 */
public class DSWorkbenchSimulatorFrame extends javax.swing.JFrame {

    private static DSWorkbenchSimulatorFrame SINGLETON = null;
    private AbstractSimulator sim = null;
    private SimulatorResult lastResult = null;

    public static synchronized DSWorkbenchSimulatorFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchSimulatorFrame();
        }
        return SINGLETON;
    }

    /** Creates new form DSWorkbenchSimulatorFrame */
    DSWorkbenchSimulatorFrame() {
        initComponents();
        setTitle("A*Star - Attack Simulator for Tribal Wars v" + Constants.VERSION + Constants.VERSION_ADDITION);
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
            fireGlobalErrorEvent("Einheitensymbole konnten nicht geladen werden.");
        }
        buildServerList();
        jServerList.setSelectedIndex(0);
        fireServerChangedEvent(null);
        jOffKnightItemList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                fireCalculateEvent();
            }
        });
        jDefKnightItemList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                fireCalculateEvent();
            }
        });
        jAboutDialog.getContentPane().setBackground(Constants.DS_BACK);
        jAboutDialog.pack();
    }

    private void buildServerList() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (int i = 3; i <= 48; i++) {
            model.addElement("de" + i);
        }
        jServerList.setModel(model);
    }

    private void buildTables() {
        //build attacker table
        SimulatorTableModel attackerModel = SimulatorTableModel.getSingleton();
        jAttackerTable.setModel(attackerModel);

        jAttackerTable.setDefaultEditor(Double.class, new TechLevelCellEditor((ConfigManager.getSingleton().getTech() == ConfigManager.ID_TECH_3) ? 3 : 10));
        jAttackerTable.setDefaultEditor(Integer.class, new SpreadSheetCellEditor());
        jAttackerTable.setDefaultRenderer(String.class, new UnitTableCellRenderer());
        if (ConfigManager.getSingleton().getTech() != ConfigManager.ID_SIMPLE_TECH) {
            //old model (empty, unit, attacker, tech,empty, defender, tech, empty)
            jAttackerTable.getColumnModel().getColumn(1).setMaxWidth(60);
            jAttackerTable.getColumnModel().getColumn(2).setMaxWidth(80);
            jAttackerTable.getColumnModel().getColumn(3).setMaxWidth(40);
            jAttackerTable.getColumnModel().getColumn(4).setMaxWidth(10);
            jAttackerTable.getColumnModel().getColumn(5).setMaxWidth(80);
            jAttackerTable.getColumnModel().getColumn(6).setMaxWidth(40);
            jAttackerTable.invalidate();
            for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
                attackerModel.addRow(new Object[]{null, unit.getPlainName(), 0, 1.0, null, 0, 1.0, null});
            }
            jAttackerTable.revalidate();
        } else {
            //new model (empty, unit, attacker, empty, defender, empty)
            jAttackerTable.getColumnModel().getColumn(1).setMaxWidth(50);
            jAttackerTable.getColumnModel().getColumn(2).setMaxWidth(70);
            jAttackerTable.getColumnModel().getColumn(3).setMaxWidth(5);
            jAttackerTable.getColumnModel().getColumn(4).setMaxWidth(70);
            jAttackerTable.invalidate();
            for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
                attackerModel.addRow(new Object[]{null, unit.getPlainName(), 0, null, 0, null});
            }
        }
        jAttackerTable.revalidate();
        jAttackerTable.setRowHeight(20);

        jScrollPane1.getViewport().setBackground(Constants.DS_BACK_LIGHT);
        jAttackerTable.setBackground(Constants.DS_BACK_LIGHT);

        //add header renderers

        for (int i = 0; i < jAttackerTable.getColumnCount(); i++) {
            jAttackerTable.getColumnModel().getColumn(i).setHeaderRenderer(new TableHeaderRenderer());
        }

        //setup knight items
        DefaultListModel model = new DefaultListModel();
        DefaultListModel model2 = new DefaultListModel();
        if (ConfigManager.getSingleton().getKnightType() == ConfigManager.ID_KNIGHT_WITH_ITEMS) {
            for (int i = 0; i <= KnightItem.ID_SNOB; i++) {
                model.addElement(KnightItem.factoryKnightItem(i));
                model2.addElement(KnightItem.factoryKnightItem(i));
            }
        } else {
            model.addElement(KnightItem.factoryKnightItem(KnightItem.ID_NO_ITEM));
            model2.addElement(KnightItem.factoryKnightItem(KnightItem.ID_NO_ITEM));
        }
        jOffKnightItemList.setModel(model);
        jDefKnightItemList.setModel(model2);
        jOffKnightItemList.setSelectedIndex(0);
        jDefKnightItemList.setSelectedIndex(0);
        //list selection listeners
        jOffKnightItemList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                KnightItem item = (KnightItem) jOffKnightItemList.getSelectedValue();
                if (item == null || item.getItemId() == KnightItem.ID_NO_ITEM) {
                    UnitHolder knight = UnitManager.getSingleton().getUnitByPlainName("knight");
                    if (knight != null) {
                        SimulatorTableModel.getSingleton().setOffUnitCount(knight, 0);
                    }
                } else {
                    UnitHolder knight = UnitManager.getSingleton().getUnitByPlainName("knight");
                    if (knight != null) {
                        SimulatorTableModel.getSingleton().setOffUnitCount(knight, 1);
                    }
                }
            }
        });
        jDefKnightItemList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                Object[] selection = jDefKnightItemList.getSelectedValues();

                if (selection == null || selection.length == 0) {
                    UnitHolder knight = UnitManager.getSingleton().getUnitByPlainName("knight");
                    if (knight != null) {
                        SimulatorTableModel.getSingleton().setDefUnitCount(knight, 0);
                    }
                } else {
                    int itemCount = 0;
                    boolean noItemSelected = false;
                    for (Object o : selection) {
                        KnightItem item = (KnightItem) o;
                        if (item.getItemId() != KnightItem.ID_NO_ITEM) {
                            itemCount++;
                        } else {
                            noItemSelected = true;
                        }
                    }
                    UnitHolder knight = UnitManager.getSingleton().getUnitByPlainName("knight");
                    if (knight != null) {
                        SimulatorTableModel.getSingleton().setDefUnitCount(knight, itemCount);
                    }
                    if (noItemSelected && itemCount > 0) {
                        jDefKnightItemList.getSelectionModel().removeSelectionInterval(0, 0);
                    }
                }
            }
        });
        //set result model and re-layout
        jResultTable.setModel(ResultTableModel.getSingleton());
        Dimension dim = new Dimension(jScrollPane1.getWidth(), UnitManager.getSingleton().getUnits().length * 20 + 20 + 1);
        jPanel3.setPreferredSize(dim);
        jPanel3.setMinimumSize(dim);
        jPanel3.setSize(dim);
        jPanel3.doLayout();
        pack();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jAboutDialog = new javax.swing.JDialog();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jNightBonus = new javax.swing.JCheckBox();
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
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jAttackerTable = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jDefKnightItemList = new javax.swing.JList();
        jScrollPane6 = new javax.swing.JScrollPane();
        jOffKnightItemList = new javax.swing.JList();
        jFarmLabel = new javax.swing.JLabel();
        jFarmLevelSpinner = new javax.swing.JSpinner();
        jAttackerBelieve = new javax.swing.JCheckBox();
        jDefenderBelieve = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jServerList = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jAlwaysOnTopButton = new javax.swing.JToggleButton();
        jButton5 = new javax.swing.JButton();

        jAboutDialog.setTitle("About...");
        jAboutDialog.setResizable(false);
        jAboutDialog.setUndecorated(true);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setPreferredSize(new java.awt.Dimension(400, 184));

        jLabel6.setForeground(new java.awt.Color(0, 51, 255));
        jLabel6.setText("http://www.dsworkbench.de");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireOpenHomepageEvent(evt);
            }
        });

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("<html><u><b>eMail:</b></u></html>");

        jLabel9.setText("support@dsworkbench.de");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("<html><u><b>Web:</b></u></html>");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("<html><u><b>IRC:</b></u></html>");

        jLabel11.setText("#ds-workbench @irc.quakenet.org");

        jLabel12.setText("Ein besonderer Dank geht an capibarbaroja, Leandro, Cheesaurus und unzählige andere Spieler dafür,");

        jLabel13.setText("dass sie unermüdlich nach den grundlegenden Formeln des DS Kampfsystems gesucht haben.");

        jLabel5.setText("<html>&copy; Torridity (2009)</html>");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/astar_splash.gif"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING))
                                    .addGap(16, 16, 16))
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel10)
                                    .addGap(18, 18, 18)))
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        jButton1.setText("Schließen");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCloseAboutEvent(evt);
            }
        });

        javax.swing.GroupLayout jAboutDialogLayout = new javax.swing.GroupLayout(jAboutDialog.getContentPane());
        jAboutDialog.getContentPane().setLayout(jAboutDialogLayout);
        jAboutDialogLayout.setHorizontalGroup(
            jAboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jAboutDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jAboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE))
                .addContainerGap())
        );
        jAboutDialogLayout.setVerticalGroup(
            jAboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jAboutDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("A*Star 1.0");

        jPanel1.setBackground(new java.awt.Color(239, 235, 223));

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/wall.png"))); // NOI18N

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/masks.png"))); // NOI18N
        jLabel29.setMaximumSize(new java.awt.Dimension(16, 16));
        jLabel29.setMinimumSize(new java.awt.Dimension(16, 16));
        jLabel29.setPreferredSize(new java.awt.Dimension(16, 16));

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/klee.png"))); // NOI18N
        jLabel30.setMaximumSize(new java.awt.Dimension(16, 16));
        jLabel30.setMinimumSize(new java.awt.Dimension(16, 16));
        jLabel30.setPreferredSize(new java.awt.Dimension(16, 16));

        jNightBonus.setText("Nachtbonus");
        jNightBonus.setToolTipText("Nachtbonus aktivieren/deaktivieren");
        jNightBonus.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jNightBonus.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jNightBonus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/sun.gif"))); // NOI18N
        jNightBonus.setIconTextGap(2);
        jNightBonus.setMargin(new java.awt.Insets(2, 0, 2, 2));
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
        jWallSpinner.setMaximumSize(new java.awt.Dimension(60, 18));
        jWallSpinner.setMinimumSize(new java.awt.Dimension(60, 18));
        jWallSpinner.setPreferredSize(new java.awt.Dimension(60, 18));
        jWallSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireStateChangedEvent(evt);
            }
        });

        jMoralSpinner.setModel(new javax.swing.SpinnerNumberModel(100, 30, 100, 1));
        jMoralSpinner.setToolTipText("Moral (Angreifer)");
        jMoralSpinner.setMaximumSize(new java.awt.Dimension(60, 18));
        jMoralSpinner.setMinimumSize(new java.awt.Dimension(60, 18));
        jMoralSpinner.setPreferredSize(new java.awt.Dimension(60, 18));
        jMoralSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireStateChangedEvent(evt);
            }
        });

        jLuckSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, -25.0d, 25.0d, 0.1d));
        jLuckSpinner.setToolTipText("Glück (Angreifer)");
        jLuckSpinner.setMaximumSize(new java.awt.Dimension(60, 18));
        jLuckSpinner.setMinimumSize(new java.awt.Dimension(60, 18));
        jLuckSpinner.setPreferredSize(new java.awt.Dimension(60, 18));
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
        jCataTargetSpinner.setToolTipText("Gebäudestufe Katapultziel");
        jCataTargetSpinner.setMaximumSize(new java.awt.Dimension(60, 18));
        jCataTargetSpinner.setMinimumSize(new java.awt.Dimension(60, 18));
        jCataTargetSpinner.setPreferredSize(new java.awt.Dimension(60, 18));
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

        jPanel3.setMaximumSize(new java.awt.Dimension(2147483647, 280));
        jPanel3.setPreferredSize(new java.awt.Dimension(516, 200));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(225, 213, 190));
        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(225, 213, 190), 1, true));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setMaximumSize(new java.awt.Dimension(800, 280));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(160, 120));
        jScrollPane1.setOpaque(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(160, 120));

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
        jAttackerTable.setPreferredSize(new java.awt.Dimension(500, 252));
        jScrollPane1.setViewportView(jAttackerTable);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jScrollPane5.setMaximumSize(new java.awt.Dimension(180, 70));
        jScrollPane5.setMinimumSize(new java.awt.Dimension(180, 70));
        jScrollPane5.setPreferredSize(new java.awt.Dimension(180, 70));

        jDefKnightItemList.setToolTipText("Paladingegenstände des Verteidigers");
        jScrollPane5.setViewportView(jDefKnightItemList);

        jScrollPane6.setMaximumSize(new java.awt.Dimension(180, 70));
        jScrollPane6.setMinimumSize(new java.awt.Dimension(180, 70));
        jScrollPane6.setPreferredSize(new java.awt.Dimension(180, 70));

        jOffKnightItemList.setToolTipText("Paladingegenstand des Angreifers");
        jScrollPane6.setViewportView(jOffKnightItemList);

        jFarmLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/farm.png"))); // NOI18N
        jFarmLabel.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/farm_disabled.png"))); // NOI18N

        jFarmLevelSpinner.setModel(new javax.swing.SpinnerNumberModel(30, 1, 30, 1));
        jFarmLevelSpinner.setToolTipText("Gebäudestufe des Bauernhofs");
        jFarmLevelSpinner.setMaximumSize(new java.awt.Dimension(60, 18));
        jFarmLevelSpinner.setMinimumSize(new java.awt.Dimension(60, 18));
        jFarmLevelSpinner.setPreferredSize(new java.awt.Dimension(60, 18));
        jFarmLevelSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireStateChangedEvent(evt);
            }
        });

        jAttackerBelieve.setSelected(true);
        jAttackerBelieve.setText("Gläubig");
        jAttackerBelieve.setToolTipText("Angreifer ist gläubig");
        jAttackerBelieve.setOpaque(false);
        jAttackerBelieve.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireBelieveChangedEvent(evt);
            }
        });

        jDefenderBelieve.setSelected(true);
        jDefenderBelieve.setText("Gläubig");
        jDefenderBelieve.setToolTipText("Verteidiger ist gläubig");
        jDefenderBelieve.setOpaque(false);
        jDefenderBelieve.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireBelieveChangedEvent(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                    .addComponent(jWallInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                    .addComponent(jBuildingInfo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                    .addComponent(jNukeInfo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jAttackerBelieve, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jDefenderBelieve, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)))
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jFarmLabel)
                                .addGap(18, 18, 18)
                                .addComponent(jFarmLevelSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jMoralSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLuckSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(jNightBonus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel28)
                                        .addComponent(jLabel31))
                                    .addGap(18, 18, 18)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jWallSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jCataTargetSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jWallSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCataTargetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jFarmLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFarmLevelSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMoralSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLuckSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jNightBonus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAttackerBelieve)
                    .addComponent(jDefenderBelieve))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jWallInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBuildingInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jNukeInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
        jButton3.setToolTipText("Die überlebenden Verteidiger einfügen und erneut angreifen");
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

        jAlwaysOnTopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/pin_grey.png"))); // NOI18N
        jAlwaysOnTopButton.setToolTipText("A*Star immer im Vordergrund halten");
        jAlwaysOnTopButton.setMaximumSize(new java.awt.Dimension(50, 33));
        jAlwaysOnTopButton.setMinimumSize(new java.awt.Dimension(50, 33));
        jAlwaysOnTopButton.setPreferredSize(new java.awt.Dimension(50, 33));
        jAlwaysOnTopButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/pin_blue.png"))); // NOI18N
        jAlwaysOnTopButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireAlwaysOnTopChangeEvent(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/information.png"))); // NOI18N
        jButton5.setToolTipText("About...");
        jButton5.setMaximumSize(new java.awt.Dimension(50, 33));
        jButton5.setMinimumSize(new java.awt.Dimension(50, 33));
        jButton5.setPreferredSize(new java.awt.Dimension(50, 33));
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireInformationEvent(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jServerList, 0, 57, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                    .addComponent(jAlwaysOnTopButton, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 340, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAlwaysOnTopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        boolean cataFarm = false;
        boolean cataChurch = false;
        double luck = (Double) jLuckSpinner.getValue();
        double moral = (Integer) jMoralSpinner.getValue();
        int farmLevel = 0;
        if (jFarmLevelSpinner.isEnabled()) {
            farmLevel = (Integer) jFarmLevelSpinner.getValue();
        }
        boolean attackerBelieve = true;
        boolean defenderBelieve = true;
        if (jAttackerBelieve.isEnabled() && jDefenderBelieve.isEnabled()) {
            attackerBelieve = jAttackerBelieve.isSelected();
            defenderBelieve = jDefenderBelieve.isSelected();
        }
        SimulatorResult result = sim.bunkerBuster(off, def, nightBonus, luck, moral, wallLevel, cataTarget, farmLevel, attackerBelieve, defenderBelieve, cataChurch, cataFarm);
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
        try {
            SimulatorTableModel.getSingleton().setDef(lastResult.getSurvivingDef());
            jWallSpinner.setValue(lastResult.getWallLevel());
            jCataTargetSpinner.setValue(lastResult.getBuildingLevel());
            fireCalculateEvent();
        } catch (Exception e) {
        }
    }//GEN-LAST:event_fireAttackAgainEvent

    private void fireServerChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireServerChangedEvent
        if (evt == null || evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            try {
                String serverID = (String) jServerList.getSelectedItem();
                ConfigManager.getSingleton().parseConfig(serverID);
                UnitManager.getSingleton().parseUnits(serverID);
                SimulatorTableModel.getSingleton().reset();
                ResultTableModel.getSingleton().reset();
                SimulatorTableModel.getSingleton().setupModel();
                ResultTableModel.getSingleton().setupModel();
                /*if ((ConfigManager.getSingleton().getTech() == ConfigManager.ID_SIMPLE_TECH) &&
                (ConfigManager.getSingleton().getFarmLimit() == 0)) {*/
                if (UnitManager.getSingleton().getUnitByPlainName("archer") != null) {
                    sim = new NewSimulator();
                    lastResult = null;
                } else {
                    sim = new OldSimulator();
                    lastResult = null;
                }

                jFarmLevelSpinner.setEnabled(ConfigManager.getSingleton().getFarmLimit() != 0);
                jFarmLabel.setEnabled(ConfigManager.getSingleton().getFarmLimit() != 0);
                jAttackerBelieve.setEnabled(ConfigManager.getSingleton().isChurch());
                jDefenderBelieve.setEnabled(ConfigManager.getSingleton().isChurch());
                buildTables();
                buildResultTable(new SimulatorResult());
            } catch (Exception e) {
                fireGlobalWarningEvent("Fehler beim Wechseln des Servers (Grund: " + e.getMessage() + ")");
            }
        }
    }//GEN-LAST:event_fireServerChangedEvent

    private void fireBelieveChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireBelieveChangedEvent
        fireCalculateEvent();
    }//GEN-LAST:event_fireBelieveChangedEvent

    private void fireAlwaysOnTopChangeEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireAlwaysOnTopChangeEvent
        setAlwaysOnTop(jAlwaysOnTopButton.isSelected());
    }//GEN-LAST:event_fireAlwaysOnTopChangeEvent

    private void fireInformationEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireInformationEvent
        jAboutDialog.setLocationRelativeTo(this);
        jAboutDialog.setVisible(true);
    }//GEN-LAST:event_fireInformationEvent

    private void fireOpenHomepageEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireOpenHomepageEvent
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URL("http://www.dsworkbench.de/index.php?id=73").toURI());
            }
        } catch (Exception e) {
        }
}//GEN-LAST:event_fireOpenHomepageEvent

    private void fireCloseAboutEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCloseAboutEvent
        jAboutDialog.setVisible(false);
    }//GEN-LAST:event_fireCloseAboutEvent

    private void fireCalculateEvent() {
        Hashtable<UnitHolder, AbstractUnitElement> off = SimulatorTableModel.getSingleton().getOff();
        Hashtable<UnitHolder, AbstractUnitElement> def = SimulatorTableModel.getSingleton().getDef();
        boolean nightBonus = jNightBonus.isSelected();
        boolean cataFarm = false;
        boolean cataChurch = false;
        int wallLevel = (Integer) jWallSpinner.getValue();
        int cataTarget = (Integer) jCataTargetSpinner.getValue();
        int farmLevel = 0;
        if (jFarmLevelSpinner.isEnabled()) {
            farmLevel = (Integer) jFarmLevelSpinner.getValue();
        }
        double luck = (Double) jLuckSpinner.getValue();
        double moral = (Integer) jMoralSpinner.getValue();
        boolean attackerBelieve = true;
        boolean defenderBelieve = true;
        if (jAttackerBelieve.isEnabled() && jDefenderBelieve.isEnabled()) {
            attackerBelieve = jAttackerBelieve.isSelected();
            defenderBelieve = jDefenderBelieve.isSelected();
        }

        //build knight item list
        List<KnightItem> defItems = new LinkedList<KnightItem>();

        KnightItem offItem = (KnightItem) jOffKnightItemList.getSelectedValue();
        if (offItem == null) {
            offItem = KnightItem.factoryKnightItem(KnightItem.ID_NO_ITEM);
        }
        if (jDefKnightItemList.getSelectedIndices() != null) {
            for (int i : jDefKnightItemList.getSelectedIndices()) {
                KnightItem item = (KnightItem) jDefKnightItemList.getModel().getElementAt(i);
                if (item.getItemId() != KnightItem.ID_NO_ITEM) {
                    defItems.add(item);
                }
            }
        }

        SimulatorResult result = null;
        if (sim instanceof NewSimulator) {
            //use items
            result = ((NewSimulator) sim).calculate(off, def, offItem, defItems, nightBonus, luck, moral, wallLevel, cataTarget, farmLevel, attackerBelieve, defenderBelieve, cataChurch, cataFarm);
        } else {
            result = sim.calculate(off, def, nightBonus, luck, moral, wallLevel, cataTarget, farmLevel, attackerBelieve, defenderBelieve, cataChurch, cataFarm);
        }
        jNukeInfo.setText("(Einzelangriff)");
        buildResultTable(result);
    }

    private void buildResultTable(SimulatorResult pResult) {

        ResultTableModel.getSingleton().clear();

        // <editor-fold defaultstate="collapsed" desc="Build header renderer">
        for (int i = 0; i <
                jResultTable.getColumnCount(); i++) {
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
        for (int i = 1; i <
                ResultTableModel.getSingleton().getColumnCount(); i++) {
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

    public void fireGlobalErrorEvent(String pMessage) {
        JOptionPane.showMessageDialog(this, pMessage, "Fehler", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    public void fireGlobalWarningEvent(String pMessage) {
        JOptionPane.showMessageDialog(this, pMessage, "Warnung", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            System.setProperty("swing.defaultlaf", "net.sourceforge.napkinlaf.NapkinLookAndFeel");
            java.awt.EventQueue.invokeLater(new Runnable() {

                public void run() {
                    try {
                        DSWorkbenchSimulatorFrame.getSingleton().setVisible(true);
                    } catch (Throwable t) {
                        JOptionPane.showMessageDialog(null, "A*Star konnte nicht gestartet werden. (Grund: " + t.getMessage() + ")");
                        System.exit(1);
                    }
                }
            });
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(null, "A*Star konnte nicht gestartet werden. (Grund: " + t.getMessage() + ")");
            System.exit(1);
        }


    // System.setProperty("http.proxyHost", "proxy.fzk.de");
    //  System.setProperty("http.proxyPort", "8000");

    /*   for (int i = 3; i <= 48; i++) {
    System.out.println("Getting units from server de" + i);
    try {

    String config = "interface.php?func=get_config";
    String unit = "interface.php?func=get_unit_info";
    String url = "http://de" + i + ".die-staemme.de/" + config;
    URLConnection ucon = new URL(url).openConnection();
    BufferedReader reader = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
    FileWriter fout = new FileWriter("./src/res/servers/config_de" + i + ".xml");
    String line = null;
    while ((line = reader.readLine()) != null) {
    fout.write(line + "\n");
    }
    fout.flush();
    fout.close();
    //"/interface.php?func=get_config"
    } catch (Exception e) {
    e.printStackTrace();
    }

    }*/


    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog jAboutDialog;
    private javax.swing.JToggleButton jAlwaysOnTopButton;
    private javax.swing.JCheckBox jAttackerBelieve;
    private javax.swing.JTable jAttackerTable;
    private javax.swing.JLabel jBuildingInfo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JSpinner jCataTargetSpinner;
    private javax.swing.JList jDefKnightItemList;
    private javax.swing.JCheckBox jDefenderBelieve;
    private javax.swing.JLabel jFarmLabel;
    private javax.swing.JSpinner jFarmLevelSpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSpinner jLuckSpinner;
    private javax.swing.JSpinner jMoralSpinner;
    private javax.swing.JCheckBox jNightBonus;
    private javax.swing.JLabel jNukeInfo;
    private javax.swing.JList jOffKnightItemList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTable jResultTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JComboBox jServerList;
    private javax.swing.JLabel jWallInfo;
    private javax.swing.JSpinner jWallSpinner;
    // End of variables declaration//GEN-END:variables
}
