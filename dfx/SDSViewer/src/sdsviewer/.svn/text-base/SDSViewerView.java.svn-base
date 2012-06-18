/*
 * SDSViewerView.java
 */
package sdsviewer;

import accessibility.PartAttributes;
import accessibility.CPFReader;
import viewers.BinaryGraphViewer;
import helper.ElapsedTime;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import helper.HashStringBinaryTree;
import helper.HashStringTree;
import helper.StringList;
import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;
import newsds.algorithms.SDSGeneralAlgorithm;
import newsds.datastructures.BasicPart;
import newsds.datastructures.CompositePart;
import newsds.datastructures.SDSJointBinaryForest;
import newsds.algorithms.SDSNewAlgorithm;
import newsds.datastructures.SDSBinaryTree;
import newsds.datastructures.SDSJointForest;
import newsds.datastructures.SDSTree;
import testability.FunctionallyTestableIntermediateFinder;
import testability.IntermediateFinder;
import viewers.GraphViewer;

/**
 * The application's main frame.
 */
public class SDSViewerView extends FrameView {

    private VisualizationViewer<String, Number> _vv;
    private ArrayList<Object[]> _rowData = new ArrayList<Object[]>();
    private HashMap<String, StringList> _intermediates = new HashMap();
    private ArrayList<CompositePart> _goalParts;
    private ArrayList<StringList> _required;
    private ArrayList<StringList> _recommended;
    private HashMap<String, PartAttributes> _basicPartAttributes;
    private HashMap<String, Statistics> _statistics = new HashMap<String, Statistics>();
    private static JFrame frame = new JFrame("FrameDemo");
    private final String NEW_SDS_TITLE = "New SDS";
    private final String SDS_TITLE = "Original SDS";
    private final String GENERAL_SDS_TITLE = "General SDS";

    public SDSViewerView(SingleFrameApplication app) {
        super(app);
        frame.setVisible(false);
        frame.setPreferredSize(new Dimension(400, 80));
        frame.setSize(new Dimension(400, 80));
        frame.setLocation(500, 500);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
//                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
//                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
//        statusAnimationLabel.setIcon(idleIcon);
//        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
//                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
//                    progressBar.setVisible(true);
//                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
//                    statusAnimationLabel.setIcon(idleIcon);
//                    progressBar.setVisible(false);
//                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
//                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
//                    progressBar.setVisible(true);
//                    progressBar.setIndeterminate(false);
//                    progressBar.setValue(value);
                }
            }
        });

        //mainPanel.setPreferredSize(new Dimension(500, 500));
        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        //this.mainPanel.add(zoomPane);

        barDo = new JProgressBar(0, 100);
        frame.add(barDo);

        barDo.setBounds(10, 10, 280, 20);
    }

    public void findReqRecIntermediates() {
        _required = new ArrayList<StringList>();
        _recommended = new ArrayList<StringList>();

        DefaultTableModel model = ((DefaultTableModel) tIntermediates.getModel());

        for (int index = 0; index < model.getRowCount(); index++) {
            String partName = (String) model.getValueAt(index, 0);
            boolean isRequired = (Boolean) model.getValueAt(index, 2);
            boolean isRecommended = (Boolean) model.getValueAt(index, 3);

            if (isRequired) {
                _required.add(_intermediates.get(partName));
                continue;
            }
            if (isRecommended) {
                _recommended.add(_intermediates.get(partName));
            }
        }
    }
    static JProgressBar barDo;

    //The thread
    public static class thread1 implements Runnable {

        public void run() {
            frame.setVisible(true);

            for (int i = 0; i <= 100; i++) { //Progressively increment variable i
                barDo.setValue(i); //Set value
                barDo.repaint(); //Refresh graphics
                try {
                    Thread.sleep(20);
                } //Sleep 50 milliseconds
                catch (InterruptedException err) {
                }
            }
            frame.setVisible(false);
        }
    }

    public void addGraph(String title, VisualizationViewer vv) {
        GraphZoomScrollPane zoomPane = new GraphZoomScrollPane(vv);
        this.tpCanvas.addTab(title, zoomPane);
        this._vv = vv;
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = SDSViewerApp.getApplication().getMainFrame();
            aboutBox = new SDSViewerAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SDSViewerApp.getApplication().show(aboutBox);
    }

    public void resetAll() {
        tpCanvas.removeAll();
        _rowData.clear();

        DefaultTableModel model = ((DefaultTableModel) tIntermediates.getModel());

        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }

        tIntermediates.setModel(model);
    }
//
//    public void markAsRequired(CompositePart part) {
//        for (int ind = 0; ind < _rowData.size(); ind++) {
//            Object[] crow = _rowData.get(ind);
//            if (crow[0].equals(part.toString())) {
//                crow[2] = true;
//            }
//        }
//    }
//
//    public void markAsRecommended(CompositePart part) {
//        for (int ind = 0; ind < _rowData.size(); ind++) {
//            Object[] crow = _rowData.get(ind);
//            if (crow[0].equals(part.toString())) {
//                crow[3] = true;
//            }
//        }
//    }

    public void addRowsToTable() {
        for (int ind = 0; ind < _rowData.size(); ind++) {
            ((DefaultTableModel) tIntermediates.getModel()).addRow(_rowData.get(ind));
        }
    }

    public void addIntermediate(StringList part) {
        String partName = part.toString();
        _intermediates.put(partName, part);
        int length = 0;

        for (String basicPart : part) {
            length += _basicPartAttributes.get(basicPart).getLength();
        }

        _rowData.add(new Object[]{partName, length, false, false});
    }

    public void addFTIs(ArrayList<CompositePart> intermediates) {
//        for (int ind = 0; ind < intermediates.size(); ind++)
//            listFunctionallyTestable.add(intermediates.get(ind).toString());
    }

    public void addSTIs(ArrayList<CompositePart> intermediates) {
//        for (int ind = 0; ind < intermediates.size(); ind++)
//            listStructurallyTestable.add(intermediates.get(ind).toString());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        label2 = new java.awt.Label();
        jScrollPane1 = new javax.swing.JScrollPane();
        tIntermediates = new javax.swing.JTable();
        label3 = new java.awt.Label();
        taFileContents = new java.awt.TextArea();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        tpCanvas = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        lblStats = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblExecutionTime = new javax.swing.JLabel();
        lblSteps = new javax.swing.JLabel();
        lblStages = new javax.swing.JLabel();
        lblGoalParts = new javax.swing.JLabel();
        lblAsmTime = new javax.swing.JLabel();
        lblCost = new javax.swing.JLabel();
        btnExportImage = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblStIntermediates = new javax.swing.JLabel();
        lblFtIntermediates = new javax.swing.JLabel();
        tfPathToFiles = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnLoad = new javax.swing.JToggleButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lFiles = new javax.swing.JList();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setName("jPanel1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(sdsviewer.SDSViewerApp.class).getContext().getResourceMap(SDSViewerView.class);
        label2.setFont(resourceMap.getFont("label3.font")); // NOI18N
        label2.setName("label2"); // NOI18N
        label2.setText(resourceMap.getString("label2.text")); // NOI18N

        jScrollPane1.setFocusable(false);
        jScrollPane1.setFont(resourceMap.getFont("jScrollPane1.font")); // NOI18N
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tIntermediates.setFont(resourceMap.getFont("tIntermediates.font")); // NOI18N
        tIntermediates.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Intermediate", "Length", "Req", "Rec"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tIntermediates.setName("tIntermediates"); // NOI18N
        tIntermediates.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tIntermediates);
        tIntermediates.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tIntermediates.columnModel.title0")); // NOI18N
        tIntermediates.getColumnModel().getColumn(1).setPreferredWidth(70);
        tIntermediates.getColumnModel().getColumn(1).setMaxWidth(70);
        tIntermediates.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tIntermediates.columnModel.title2")); // NOI18N
        tIntermediates.getColumnModel().getColumn(2).setPreferredWidth(70);
        tIntermediates.getColumnModel().getColumn(2).setMaxWidth(70);
        tIntermediates.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTable1.columnModel.title2")); // NOI18N
        tIntermediates.getColumnModel().getColumn(3).setPreferredWidth(70);
        tIntermediates.getColumnModel().getColumn(3).setMaxWidth(70);
        tIntermediates.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTable1.columnModel.title3")); // NOI18N

        label3.setFont(resourceMap.getFont("label3.font")); // NOI18N
        label3.setName("label3"); // NOI18N
        label3.setText(resourceMap.getString("label3.text")); // NOI18N

        taFileContents.setEditable(false);
        taFileContents.setFont(resourceMap.getFont("taFileContents.font")); // NOI18N
        taFileContents.setName("taFileContents"); // NOI18N
        taFileContents.setText(resourceMap.getString("taFileContents.text")); // NOI18N

        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setMaximumSize(new java.awt.Dimension(147, 23));
        jButton8.setMinimumSize(new java.awt.Dimension(147, 23));
        jButton8.setName("jButton8"); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setMaximumSize(new java.awt.Dimension(147, 23));
        jButton9.setMinimumSize(new java.awt.Dimension(147, 23));
        jButton9.setName("jButton9"); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                    .addComponent(taFileContents, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                    .addComponent(label3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 124, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton6))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(taFileContents, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setName("jPanel2"); // NOI18N

        tpCanvas.setName("tabbedPane"); // NOI18N
        tpCanvas.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tpCanvasStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setName("jPanel3"); // NOI18N

        lblStats.setFont(resourceMap.getFont("lblStats.font")); // NOI18N
        lblStats.setText(resourceMap.getString("lblStats.text")); // NOI18N
        lblStats.setName("lblStats"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        jLabel2.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        lblExecutionTime.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        lblExecutionTime.setForeground(resourceMap.getColor("lblExecutionTime.foreground")); // NOI18N
        lblExecutionTime.setText(resourceMap.getString("lblExecutionTime.text")); // NOI18N
        lblExecutionTime.setName("lblExecutionTime"); // NOI18N

        lblSteps.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        lblSteps.setForeground(resourceMap.getColor("lblSteps.foreground")); // NOI18N
        lblSteps.setText(resourceMap.getString("lblSteps.text")); // NOI18N
        lblSteps.setName("lblSteps"); // NOI18N

        lblStages.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        lblStages.setForeground(resourceMap.getColor("lblStages.foreground")); // NOI18N
        lblStages.setText(resourceMap.getString("lblStages.text")); // NOI18N
        lblStages.setName("lblStages"); // NOI18N

        lblGoalParts.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        lblGoalParts.setForeground(resourceMap.getColor("lblGoalParts.foreground")); // NOI18N
        lblGoalParts.setText(resourceMap.getString("lblGoalParts.text")); // NOI18N
        lblGoalParts.setName("lblGoalParts"); // NOI18N

        lblAsmTime.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        lblAsmTime.setForeground(resourceMap.getColor("lblAsmTime.foreground")); // NOI18N
        lblAsmTime.setText(resourceMap.getString("lblAsmTime.text")); // NOI18N
        lblAsmTime.setName("lblAsmTime"); // NOI18N

        lblCost.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        lblCost.setForeground(resourceMap.getColor("lblCost.foreground")); // NOI18N
        lblCost.setText(resourceMap.getString("lblCost.text")); // NOI18N
        lblCost.setName("lblCost"); // NOI18N

        btnExportImage.setLabel(resourceMap.getString("btnExportImage.label")); // NOI18N
        btnExportImage.setName("btnExportImage"); // NOI18N
        btnExportImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportImageActionPerformed(evt);
            }
        });

        jButton3.setLabel(resourceMap.getString("btnClearTabs.label")); // NOI18N
        jButton3.setName("btnClearTabs"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton2.setLabel(resourceMap.getString("btnRunSds.label")); // NOI18N
        jButton2.setName("btnRunSds"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel14.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jLabel15.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        lblStIntermediates.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        lblStIntermediates.setForeground(resourceMap.getColor("lblStIntermediates.foreground")); // NOI18N
        lblStIntermediates.setText(resourceMap.getString("lblStIntermediates.text")); // NOI18N
        lblStIntermediates.setName("lblStIntermediates"); // NOI18N

        lblFtIntermediates.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        lblFtIntermediates.setForeground(resourceMap.getColor("lblFtIntermediates.foreground")); // NOI18N
        lblFtIntermediates.setText(resourceMap.getString("lblFtIntermediates.text")); // NOI18N
        lblFtIntermediates.setName("lblFtIntermediates"); // NOI18N

        tfPathToFiles.setText(resourceMap.getString("tfPathToFiles.text")); // NOI18N
        tfPathToFiles.setName("tfPathToFiles"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        btnLoad.setText(resourceMap.getString("btnLoad.text")); // NOI18N
        btnLoad.setName("btnLoad"); // NOI18N
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        lFiles.setName("lFiles"); // NOI18N
        lFiles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lFilesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(lFiles);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(34, 34, 34))
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblStages)
                            .addComponent(lblCost)
                            .addComponent(lblAsmTime)
                            .addComponent(lblSteps))
                        .addGap(71, 71, 71)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel14)
                            .addComponent(jLabel5)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblGoalParts)
                            .addComponent(lblStIntermediates)
                            .addComponent(lblFtIntermediates)
                            .addComponent(lblExecutionTime))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnExportImage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblStats)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 278, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(tfPathToFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLoad, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStats)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tfPathToFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(btnLoad)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel14)
                                    .addComponent(lblStIntermediates))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel15)
                                    .addComponent(lblFtIntermediates))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(lblExecutionTime)))
                            .addComponent(lblGoalParts)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(lblSteps))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(lblAsmTime))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(lblCost)))
                            .addComponent(lblStages))
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExportImage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );

        lblStats.getAccessibleContext().setAccessibleName(resourceMap.getString("jLabel1.AccessibleContext.accessibleName")); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(sdsviewer.SDSViewerApp.class).getContext().getActionMap(SDSViewerView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void btnExportImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportImageActionPerformed
    }//GEN-LAST:event_btnExportImageActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        resetAll();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void loadFile(String fileName) {
        // new Thread(new thread1()).start();
        resetAll();

        CPFReader cpfReader = null;
        try {
            cpfReader = new CPFReader(fileName);
            _basicPartAttributes = cpfReader.getBasicPartAttributes();
        } catch (Exception ex) {
            Logger.getLogger(SDSViewerView.class.getName()).log(Level.SEVERE, null, ex);
        }

        taFileContents.setText(cpfReader.getFileContents());

        _goalParts = cpfReader.getGoalParts();



        IntermediateFinder intFinder = new IntermediateFinder();
        ArrayList<StringList> intermediates = intFinder.getCompositeParts(convertGps(_goalParts));

        for (int ind = 0; ind < intermediates.size(); ind++) {
            addIntermediate(intermediates.get(ind));
        }

//        FunctionallyTestableIntermediateFinder ftFinder = new FunctionallyTestableIntermediateFinder();
//        ArrayList<CompositePart> fts = ftFinder.getCompositeParts(_goalParts);
//        for (int ind = 0; ind < fts.size(); ind++) {
//            markAsFt(fts.get(ind));
//            markAsRequired(fts.get(ind));
//        }
//
//        StructurallyTestableIntermediateFinder stFinder = new StructurallyTestableIntermediateFinder();
//        ArrayList<CompositePart> sts = stFinder.getCompositeParts(_goalParts);
//        for (int ind = 0; ind < sts.size(); ind++) {
//            markAsSt(sts.get(ind));
//            markAsRecommended(sts.get(ind));
//        }

        addRowsToTable();
    }

    private ArrayList<StringList> convertGps(ArrayList<CompositePart> gpsOrig) {
        ArrayList<StringList> gps = new ArrayList<StringList>();

        for (CompositePart cPart : gpsOrig) {
            StringList gp = new StringList();
            for (BasicPart bPart : cPart) {
                gp.add(bPart.getName());
            }
            gps.add(gp);
        }

        return gps;
    }

    private void runSDS() {
        SDSNewAlgorithm sds = new SDSNewAlgorithm();

        ArrayList<StringList> gps = convertGps(_goalParts);

        ElapsedTime.start();
        ArrayList<SDSBinaryTree> orgGoalPartTrees = sds.createAsmTreeMultipleGoalParts(gps, new ArrayList<StringList>(), new ArrayList<StringList>(), new HashStringBinaryTree());
        ElapsedTime.stop();

        SDSJointBinaryForest orgJbf = sds.convertTo2ab(orgGoalPartTrees);
        BinaryGraphViewer orgJbfViewer = new BinaryGraphViewer(orgJbf);
        orgJbfViewer.setBackground(new Color(206, 218, 255));
//        LayoutScalingControl scalingPlugin = new LayoutScalingControl();
//        orgJbfViewer.getVV().scaleToLayout(scalingPlugin);
//        scalingPlugin.scale(orgJbfViewer.getVV(), .2f, new Point(0,0));
        this.addGraph(this.SDS_TITLE, orgJbfViewer.getVV());

        Statistics stat = new Statistics();
        stat.setExecutionTime(ElapsedTime.getTime());
        stat.setStages(orgJbf.getNode().getStages());
        stat.setSteps(orgJbf.getNode().getSteps());
        stat.setGoalParts(_goalParts.size());
        _statistics.put(this.SDS_TITLE, stat);
    }

    private void runNewSDS() {
        SDSNewAlgorithm sds = new SDSNewAlgorithm();

        ArrayList<StringList> gps = convertGps(_goalParts);

        ElapsedTime.start();
        ArrayList<SDSBinaryTree> orgGoalPartTrees = sds.createAsmTreeMultipleGoalParts(gps, _required, _recommended, new HashStringBinaryTree());
        ElapsedTime.stop();

        SDSJointBinaryForest orgJbf = sds.convertTo2ab(orgGoalPartTrees);
        BinaryGraphViewer orgJbfViewer = new BinaryGraphViewer(orgJbf);
        orgJbfViewer.setBackground(new Color(211, 255, 206));
//        LayoutScalingControl scalingPlugin = new LayoutScalingControl();
//        orgJbfViewer.getVV().scaleToLayout(scalingPlugin);
//        scalingPlugin.scale(orgJbfViewer.getVV(), .2f, new Point(0,0));
        this.addGraph(this.NEW_SDS_TITLE, orgJbfViewer.getVV());

        Statistics stat = new Statistics();
        stat.setExecutionTime(ElapsedTime.getTime());
        stat.setStages(orgJbf.getNode().getStages());
        stat.setSteps(orgJbf.getNode().getSteps());
        stat.setGoalParts(_goalParts.size());
        _statistics.put(this.NEW_SDS_TITLE, stat);
    }

    private void runGeneralSDS() {
        FunctionallyTestableIntermediateFinder finder = new FunctionallyTestableIntermediateFinder();
        ArrayList<CompositePart> required = finder.getCompositeParts(_goalParts);

        SDSGeneralAlgorithm sds = new SDSGeneralAlgorithm(2, 10);

        ArrayList<StringList> gps = convertGps(_goalParts);
        ArrayList<StringList> reqsl = convertGps(required);

        ElapsedTime.start();
        ArrayList<SDSTree> orgGoalPartTrees = sds.createAsmTreeMultipleGoalParts(gps, reqsl, _recommended, new HashStringTree());
        ElapsedTime.stop();

        SDSJointForest orgJbf = sds.convertTo2ab(orgGoalPartTrees);
        GraphViewer orgJbfViewer = new GraphViewer(orgJbf);
        orgJbfViewer.setBackground(new Color(211, 255, 206));
//        LayoutScalingControl scalingPlugin = new LayoutScalingControl();
//        orgJbfViewer.getVV().scaleToLayout(scalingPlugin);
//        scalingPlugin.scale(orgJbfViewer.getVV(), .2f, new Point(0,0));
        this.addGraph(this.GENERAL_SDS_TITLE, orgJbfViewer.getVV());

        Statistics stat = new Statistics();
        stat.setExecutionTime(ElapsedTime.getTime());
        stat.setStages(orgJbf.getNode().getStages());
        stat.setSteps(orgJbf.getNode().getSteps());
        stat.setGoalParts(_goalParts.size());
        _statistics.put(this.GENERAL_SDS_TITLE, stat);
    }

    private String getPath() {
        int firstSelIx = lFiles.getSelectedIndex();
        if (firstSelIx == -1) {
            return null;
        }
        String path = tfPathToFiles.getText();
        String pathSeparator = System.getProperty("file.separator");
        if (!path.substring(busyIconIndex, busyIconIndex).equals(pathSeparator)) {
            path += pathSeparator;
        }
        path += (String) lFiles.getSelectedValue();
        return path;
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //new Thread(new thread1()).start();
        tpCanvas.removeAll();

        findReqRecIntermediates();
        runSDS();
        runNewSDS();
        runGeneralSDS();

        refreshCanvas();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        DefaultTableModel model = ((DefaultTableModel) tIntermediates.getModel());

        for (int index = 0; index < model.getRowCount(); index++) {
            model.setValueAt(false, index, 2);
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        DefaultTableModel model = ((DefaultTableModel) tIntermediates.getModel());

        for (int index = 0; index < model.getRowCount(); index++) {
            model.setValueAt(true, index, 2);
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        DefaultTableModel model = ((DefaultTableModel) tIntermediates.getModel());

        for (int index = 0; index < model.getRowCount(); index++) {
            model.setValueAt(false, index, 3);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        DefaultTableModel model = ((DefaultTableModel) tIntermediates.getModel());

        for (int index = 0; index < model.getRowCount(); index++) {
            model.setValueAt(true, index, 3);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void refreshCanvas() {
        int tabIndex = tpCanvas.getSelectedIndex();
        if (tabIndex < 0) {
            return;
        }
        String tabTitle = tpCanvas.getTitleAt(tabIndex);
        Statistics stats = _statistics.get(tabTitle);

        if (stats == null) {
            return;
        }

        lblStats.setText(tabTitle);

        lblStages.setText(stats.getStages());
        lblSteps.setText(stats.getSteps());
        lblAsmTime.setText(stats.getAsmTime());
        lblCost.setText(stats.getCost());
        lblGoalParts.setText(stats.getGoalParts());
        lblStIntermediates.setText(stats.getStIntermediates());
        lblFtIntermediates.setText(stats.getFtIntermediates());
        lblExecutionTime.setText(stats.getExecutionTime());
    }

    private void tpCanvasStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tpCanvasStateChanged
        refreshCanvas();
    }//GEN-LAST:event_tpCanvasStateChanged

    private void lFilesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lFilesMouseClicked
        int firstSelIx = lFiles.getSelectedIndex();
        if (firstSelIx > -1) {
            loadFile(getPath());
        }
    }//GEN-LAST:event_lFilesMouseClicked

    private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
        DefaultListModel listModel = new DefaultListModel();
        File folder = new File(tfPathToFiles.getText());
        for (final File fileEntry : folder.listFiles()) {
            String[] split = fileEntry.getAbsolutePath().split("\\.");
            if (split[split.length - 1].equals("cpf")) {
                listModel.addElement(fileEntry.getName());
            }
        }
        lFiles = new JList(listModel);
        jScrollPane2.setViewportView(lFiles);
        lFiles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lFilesMouseClicked(evt);
            }
        });
    }//GEN-LAST:event_btnLoadActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportImage;
    private javax.swing.JToggleButton btnLoad;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JList lFiles;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private javax.swing.JLabel lblAsmTime;
    private javax.swing.JLabel lblCost;
    private javax.swing.JLabel lblExecutionTime;
    private javax.swing.JLabel lblFtIntermediates;
    private javax.swing.JLabel lblGoalParts;
    private javax.swing.JLabel lblStIntermediates;
    private javax.swing.JLabel lblStages;
    private javax.swing.JLabel lblStats;
    private javax.swing.JLabel lblSteps;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTable tIntermediates;
    private java.awt.TextArea taFileContents;
    private javax.swing.JTextField tfPathToFiles;
    private javax.swing.JTabbedPane tpCanvas;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    private JDialog msgBox;
}
