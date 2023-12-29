package forms;

import controller.AccountController;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import raven.toast.Notifications;
import utils.DataSearch;
import utils.EventClick;
import utils.ImageCellRender;
import utils.ImageLoader;
import utils.PanelSearch;
import utils.spinner_progress.SpinnerProgress;
import utils.table.TableActionCellEditor;
import utils.table.TableActionCellRender;
import utils.table.TableActionEvent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Account;
import model.Category;
import model.ResponseModel;
import utils.ImagePreviewLabel;

/**
 *
 * @author Raven
 */
public class FormQuanLyNhanVien extends javax.swing.JPanel {
    private File choosenFile;
    private final AccountController accountController;
    private List<Account> accounts;
    private JPopupMenu menu;
    private PanelSearch search;
    private boolean isAddEnabled,isEditingEnabled = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private Account selectedAccount;
    public FormQuanLyNhanVien() {
        initComponents();
        iniSearchTextFieldColumn();
        accountController=new AccountController();
        createTableLastColumnCellEvent();
        initButtonGroup();
        getAllStaff();
        progressLoading.setVisible(false);
        
    }
    private void initButtonGroup()
    {
        btgGender.add(rbNam);
        btgGender.add(rbNu);
    }
    private void iniSearchTextFieldColumn()
    {
        menu = new JPopupMenu();
        search = new PanelSearch();
        menu.setBorder(BorderFactory.createLineBorder(new Color(164, 164, 164)));
        menu.add(search);
        menu.setFocusable(false);
        search.addEventClick(new EventClick() {
            @Override
            public void itemClick(DataSearch data) {
                menu.setVisible(false);
                txtSearch.setText(data.getText());
                
                
            }

            @Override
            public void itemRemove(Component com, DataSearch data) {
                search.remove(com);
                
                menu.setPopupSize(menu.getWidth(), (search.getItemSize() * 35) + 2);
                if (search.getItemSize() == 0) {
                    menu.setVisible(false);
                }
                System.out.println("Remove Item : " + data.getText());
            }
        });
    }
    private void createTableLastColumnCellEvent(){
         TableActionEvent event = new TableActionEvent() {
            @Override
            public void onEdit(int row) {
                isEditingEnabled = true;
                isAddEnabled=false;
                enableEdit();
            }

            @Override
            public void onDelete(int row) {
                if (tbStaff.isEditing()) {
                    tbStaff.getCellEditor().stopCellEditing();
                }
                DefaultTableModel model = (DefaultTableModel) tbStaff.getModel();
                String accountId = (String) model.getValueAt(row, 0);
//                deleteStaff(categoryId);

            }

            @Override
            public void onView(int row) {
                System.out.println("View row : " + row);
            }
        };
        tbStaff.getColumnModel().getColumn(7).setCellRenderer(new TableActionCellRender());
        tbStaff.getColumnModel().getColumn(7).setCellEditor(new TableActionCellEditor(event));
        centeredTableColumn();
    }
    private void centeredTableColumn()
    {
        for (int i = 0; i <= 6; i++) {
            tbStaff.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    return super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
                }
            });
        }
    }
    private void refesh(){
        isAddEnabled=false;
        isEditingEnabled=false;
        enableEdit();
        clearText();
        lbImage.setIcon(null);
        choosenFile=null;
        accounts=null;
        getAllStaff();
    }
     private void getAllStaff(){
        List<Account> accountsResult=accountController.getAllAdmin();
        if(accountsResult!=null){
            accounts=accountsResult;
            loadStaffTable();   
        }
        else
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Không có danh mục!");
    }
//    private boolean deleteStaff(String accountID){
//        String result=accountController.deleteStaff(accountID);
//        if(result.equals("Success")){
//            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Xoá danh mục thành công!");
//            refesh(); 
//            return true;
//        }
//        else{
//            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, result);
//            return false;
//        }
//    }
    private void loadRadioButtonGender(String currentGender)
    {
        if(currentGender.equals("Nam"))
        {
            rbNam.setSelected(true);
            rbNu.setSelected(false);
        }
        else
        {
            rbNam.setSelected(false);
            rbNu.setSelected(true);
        }
    }
    private void loadStaffTable() {
        if (!accounts.isEmpty()) {
                DefaultTableModel model = (DefaultTableModel) tbStaff.getModel();
                model.setRowCount(0);
                int totalAccounts = accounts.size();

                // Load accounts first
                for (Account acc : accounts) {
                  
                    String accountId = acc.getAccountID();
                    String accountName = acc.getFullName();
                    String email= acc.getEmail();
                    String gender = acc.getGender();
                    String phone = acc.getPhoneNumber();
                    String birthDay =sdf.format(acc.getBirthday()) ;
                    SpinnerProgress progressBar=new SpinnerProgress();
                    progressBar.setPreferredSize(new Dimension(50,50)); 
                    
                    model.addRow(new Object[]{accountId, accountName,birthDay,email,gender,phone,progressBar});
                }
                createTableRowClick();
                AtomicInteger count = new AtomicInteger(0);
                
                // Load images asynchronously
                for (int i = 0; i < totalAccounts; i++) {
                    Account acc = accounts.get(i);
                    String imageUrl = acc.getImageUrl();
                    final int currentIndex = i;
                    
                    ImageLoader imageLoader = new ImageLoader(imageUrl, 150, 150);
                    
                    imageLoader.addPropertyChangeListener(evt -> {
                        int progress = imageLoader.getProgress();

                        SpinnerProgress progressBar = (SpinnerProgress)model.getValueAt(currentIndex, 6);
                        progressBar.setValue(progress);
                        
                        
                        if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE == evt.getNewValue()) {
                            try {
                                ImageIcon scaledImageIcon = imageLoader.get();
                                 
                                // Update the UI on the EDT
                                SwingUtilities.invokeLater(() -> {
                                    model.setValueAt(scaledImageIcon, currentIndex,6);
                                    
                                    int currentCount = count.incrementAndGet();
                                    if (currentCount == totalAccounts) {
                                        TableColumn imageColumn = tbStaff.getColumnModel().getColumn(6);
                                        imageColumn.setCellRenderer(new ImageCellRender());
                                        tbStaff.setRowSorter(new TableRowSorter<>(model));
                                        tbStaff.setRowHeight(150);
                                        tbStaff.revalidate();
                                        tbStaff.repaint();
                                        
                                    }
                                });
                                
                            } catch (InterruptedException | ExecutionException e) {
                                System.out.println("Error when loading image: " + e.getMessage());
                            }
                        }
                    });
                    
                    imageLoader.execute();
                }
                TableColumn imageColumn = tbStaff.getColumnModel().getColumn(6);
                imageColumn.setCellRenderer(new ImageCellRender());
                tbStaff.setRowHeight(50);
        } else {
            JOptionPane.showMessageDialog(this, "Không có nhân viên nào!", "Lỗi", 0);
        }
    }


//    private List<DataSearch> search(String search) {
//        
//    }
  
    
    private void createTableRowClick(){
        tbStaff.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {

                int selectedRow = tbStaff.getSelectedRow();
                int modelRowIndex = tbStaff.convertRowIndexToModel(selectedRow);
                if(modelRowIndex < 0 || selectedRow == 6)
                    return;
                if (modelRowIndex >= 0) {
                    isAddEnabled=false;
                    isEditingEnabled=false;
                    enableEdit();
                    try {
                        selectedAccount = accounts.get(modelRowIndex);
                        txtIDEmployee.setText(selectedAccount.getAccountID());
                        txtEmployeeName.setText(selectedAccount.getFullName());
                        txtEmail.setText(selectedAccount.getEmail());
                        birthDayChooser.setSelectedDate(selectedAccount.getBirthday());
                        loadRadioButtonGender(selectedAccount.getGender());
                        txtPhoneNumber.setText(selectedAccount.getPhoneNumber());
                        ImageLoader loader = new ImageLoader(selectedAccount.getImageUrl(), lbImage.getWidth(), lbImage.getHeight());
                        loader.execute();

                        loader.addPropertyChangeListener(evt -> {
                            if ("state".equals(evt.getPropertyName()) && 
                               SwingWorker.StateValue.DONE == evt.getNewValue()) {

                                try {
                                    // Set image on EDT thread
                                    ImageIcon icon = loader.get();
                                    lbImage.setIcon(icon);
                                } catch (InterruptedException | ExecutionException ex) {
                                    System.out.println("Image error");
                                }
                            }
                        });
                    } catch (Exception ex) {
                        System.out.println("Image error");
                    }
                }
            }
        });
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btgGender = new javax.swing.ButtonGroup();
        birthDayChooser = new com.raven.datechooser.DateChooser();
        btgRole = new javax.swing.ButtonGroup();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbStaff = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtIDEmployee = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        pnImage = new javax.swing.JPanel();
        lbImage = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblQLNV = new javax.swing.JLabel();
        lbExit1 = new javax.swing.JLabel();
        btnSave = new utils.Button();
        btnChooseImage = new utils.Button();
        btnRefesh = new utils.Button();
        jLabel6 = new javax.swing.JLabel();
        txtEmployeeName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        rbNu = new javax.swing.JRadioButton();
        rbNam = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtPhoneNumber = new javax.swing.JTextField();
        txtSearch = new javax.swing.JTextField();
        btnUpdate = new utils.Button();
        txtBirthDay = new javax.swing.JTextField();
        btnChooseBirthDay = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        progressLoading = new utils.spinner_progress.SpinnerProgress();
        btnAdd = new utils.Button();
        cmbPosition = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();

        birthDayChooser.setTextField(txtBirthDay);

        setPreferredSize(new java.awt.Dimension(1366, 768));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbStaff.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã nhân viên", "Tên nhân viên", "Ngày sinh", "Email", "Giới tính", "Số điện thoại", "Hình", "Thao tác"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbStaff.setRowHeight(40);
        tbStaff.setRowSelectionAllowed(false);
        jScrollPane2.setViewportView(tbStaff);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 400, 1080, 340));

        jLabel1.setText("Mã nhân viên:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, -1, -1));

        txtIDEmployee.setEditable(false);
        add(txtIDEmployee, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 355, 34));

        jLabel2.setText("Ảnh nhân viên:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 150, -1, -1));

        pnImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        lbImage.setForeground(new java.awt.Color(255, 255, 255));
        lbImage.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lbImage.setPreferredSize(new java.awt.Dimension(250, 250));
        pnImage.add(lbImage);

        add(pnImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 180, 200, 180));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 153, 153));
        jLabel3.setText("Chào mừng chủ nhân đến với chuyên mục quản lý nhân viên...");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 340, 30));

        jLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(225, 225, 225)));
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 64, 2000, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/doge-42.png"))); // NOI18N
        add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        lblQLNV.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblQLNV.setForeground(new java.awt.Color(153, 153, 153));
        lblQLNV.setText("QUẢN LÝ NHÂN VIÊN");
        add(lblQLNV, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 6, 230, 30));

        lbExit1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lbExit1.setForeground(new java.awt.Color(153, 153, 153));
        lbExit1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbExit1.setText("X");
        lbExit1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbExit1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbExit1MouseClicked(evt);
            }
        });
        add(lbExit1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 10, -1, -1));

        btnSave.setBackground(new java.awt.Color(30, 180, 114));
        btnSave.setForeground(new java.awt.Color(245, 245, 245));
        btnSave.setText("Lưu");
        btnSave.setRippleColor(new java.awt.Color(255, 255, 255));
        btnSave.setShadowColor(new java.awt.Color(30, 180, 114));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 150, 140, -1));

        btnChooseImage.setBackground(new java.awt.Color(253, 83, 83));
        btnChooseImage.setForeground(new java.awt.Color(245, 245, 245));
        btnChooseImage.setText("Chọn ảnh");
        btnChooseImage.setRippleColor(new java.awt.Color(255, 255, 255));
        btnChooseImage.setShadowColor(new java.awt.Color(253, 83, 83));
        btnChooseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseImageActionPerformed(evt);
            }
        });
        add(btnChooseImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 210, 140, -1));

        btnRefesh.setBackground(new java.awt.Color(29, 162, 253));
        btnRefesh.setForeground(new java.awt.Color(245, 245, 245));
        btnRefesh.setText("Làm mới");
        btnRefesh.setRippleColor(new java.awt.Color(255, 255, 255));
        btnRefesh.setShadowColor(new java.awt.Color(29, 162, 253));
        btnRefesh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefeshActionPerformed(evt);
            }
        });
        add(btnRefesh, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 270, 140, -1));

        jLabel6.setText("Tên nhân viên:");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        txtEmployeeName.setEditable(false);
        add(txtEmployeeName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 130, 355, 34));

        jLabel7.setText("Chức vụ:");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 80, -1, -1));

        txtEmail.setEditable(false);
        add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 170, 355, 34));

        rbNu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rbNu.setText("Nữ");
        add(rbNu, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 310, -1, -1));

        rbNam.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rbNam.setText("Nam");
        add(rbNam, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 310, -1, -1));

        jLabel10.setText("Email:");
        add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, -1, -1));

        jLabel11.setText("Số điện thoại:");
        add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 360, -1, -1));

        txtPhoneNumber.setEditable(false);
        add(txtPhoneNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 350, 360, 34));
        add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 20, 260, -1));

        btnUpdate.setBackground(new java.awt.Color(30, 180, 114));
        btnUpdate.setForeground(new java.awt.Color(245, 245, 245));
        btnUpdate.setText("Cập nhật");
        btnUpdate.setRippleColor(new java.awt.Color(255, 255, 255));
        btnUpdate.setShadowColor(new java.awt.Color(30, 180, 114));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 150, 140, -1));
        add(txtBirthDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, 290, 30));

        btnChooseBirthDay.setText("...");
        btnChooseBirthDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseBirthDayActionPerformed(evt);
            }
        });
        add(btnChooseBirthDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 260, 40, 30));

        jLabel9.setText("Ngày sinh:");
        add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, 70, -1));

        txtPassword.setEditable(false);
        add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 210, 355, 34));

        jLabel12.setText("Mật khẩu:");
        add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, -1));

        progressLoading.setForeground(new java.awt.Color(255, 204, 51));
        progressLoading.setIndeterminate(true);
        add(progressLoading, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 150, 50, 40));

        btnAdd.setBackground(new java.awt.Color(30, 180, 114));
        btnAdd.setForeground(new java.awt.Color(245, 245, 245));
        btnAdd.setText("+ Thêm nhân viên");
        btnAdd.setRippleColor(new java.awt.Color(255, 255, 255));
        btnAdd.setShadowColor(new java.awt.Color(30, 180, 114));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 330, 140, -1));

        cmbPosition.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Quản lý", "Giao hàng" }));
        add(cmbPosition, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 70, 210, 40));

        jLabel13.setText("Giới tính:");
        add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void lbExit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbExit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_lbExit1MouseClicked
    private void clearText()
    {
        rbNam.setSelected(false);
        rbNu.setSelected(false);
        txtEmployeeName.setText("");
        txtEmail.setText("");
        txtPhoneNumber.setText("");
        txtIDEmployee.setText("");
        txtSearch.setText("");
    }
    private void enableEdit()
    {
        if(isAddEnabled)
        {
            lbImage.setIcon(null);
            btnUpdate.setVisible(false);
            btnSave.setVisible(true);
            btnSave.setEnabled(true);
            btnChooseImage.setEnabled(true);
            txtEmployeeName.setEditable(true);
            txtEmail.setEditable(true);
            txtPassword.setEditable(true);
            txtPhoneNumber.setEditable(true);
            
            clearText();
        }
        else if(isEditingEnabled){
            btnUpdate.setVisible(true);
            btnUpdate.setEnabled(true);
            btnSave.setVisible(false);
            btnChooseImage.setEnabled(true);
            txtEmployeeName.setEditable(true);
            txtPassword.setEditable(true);
            txtEmail.setEditable(true);
        }
        else{
            btnSave.setEnabled(false);
            btnSave.setVisible(false);
            btnUpdate.setVisible(false);
            btnUpdate.setEnabled(false);
            btnChooseImage.setEnabled(false);
            txtEmployeeName.setEditable(false);
            txtPassword.setEditable(false);
            txtEmail.setEditable(false);
        }
    }
    private void createNewAccount()
    {
        Account account = new Account();
        account.setFullName(txtEmployeeName.getText());
        if(cmbPosition.getSelectedIndex()==0)
        {
            account.setRole("Admin");
        }
        else
        {
            account.setRole("Deliver");
        }
        account.setPassword(txtPassword.getText());
        account.setEmail(txtEmail.getText());
        account.setBirthday(birthDayChooser.getSelectedDate()); 
        if(rbNam.isSelected())
        {
            account.setGender("Nam");
        }
        else if(rbNu.isSelected())
        {
             account.setGender("Nữ");
        }
        account.setPhoneNumber(txtPhoneNumber.getText());
        if(choosenFile!=null)
        {
            ResponseModel response=accountController.createStaff(choosenFile, account);
            if(response.getMessage().equals("Success"))
            {
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thêm nhân viên thành công!");
                refesh();
                return;
            }
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Có lỗi xảy ra!\nChi tiết:"+response.getMessage());
        }
        else
        {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Bạn chưa chọn ảnh nhân viên!");
        }
    }
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        
        progressLoading.setVisible(true);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    createNewAccount();
                    return null;
                }
                @Override
                protected void done() {
                    progressLoading.setVisible(false);
                }
            };
        worker.execute();
    }//GEN-LAST:event_btnSaveActionPerformed
 
    private void btnChooseImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseImageActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Images", "jpg", "jpeg", "png");
        chooser.setFileFilter(filter);
        ImagePreviewLabel imagePreview = new ImagePreviewLabel();
            chooser.setAccessory(imagePreview); 

            chooser.addPropertyChangeListener((PropertyChangeEvent evt1) -> {
                if (evt1.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                    File f = (File) evt1.getNewValue();
                    imagePreview.setImage(f);
                }
        });
        
        int returnValue = chooser.showOpenDialog(this); 

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            choosenFile=selectedFile;
            ImageIcon imageIcon = new ImageIcon(filePath);
            Image scaledImage = imageIcon.getImage().getScaledInstance(lbImage.getWidth(), lbImage.getHeight(), Image.SCALE_SMOOTH);
            
            lbImage.setIcon(new ImageIcon(scaledImage));
        } else {
            JOptionPane.showMessageDialog(this, "Không có file được chọn","Thông báo",2);
        }

    }//GEN-LAST:event_btnChooseImageActionPerformed

    private void btnRefeshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefeshActionPerformed
        refesh();
    }//GEN-LAST:event_btnRefeshActionPerformed
    private void updateAccount(){
        if(selectedAccount!=null)
            {
                Account updatedAccount=selectedAccount;
                updatedAccount.setFullName(txtEmployeeName.getText());
                updatedAccount.setBirthday(birthDayChooser.getSelectedDate());
                updatedAccount.setPhoneNumber(txtPhoneNumber.getText());

                ResponseModel result=accountController.updateAccount(selectedAccount);

                if(result.getMessage().equals("Success")){
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Cập nhật nhân viên thành công!");
                    refesh(); 
                }
                else{
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Có lỗi xảy ra");
                }
            }
    }
    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        progressLoading.setVisible(true);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    updateAccount();
                    return null;
                }
                @Override
                protected void done() {
                    progressLoading.setVisible(false);
                }
            };
        worker.execute();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnChooseBirthDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseBirthDayActionPerformed
        birthDayChooser.showPopup();
    }//GEN-LAST:event_btnChooseBirthDayActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        isAddEnabled=true;
        isEditingEnabled=false;
        enableEdit();
    }//GEN-LAST:event_btnAddActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.datechooser.DateChooser birthDayChooser;
    private javax.swing.ButtonGroup btgGender;
    private javax.swing.ButtonGroup btgRole;
    private utils.Button btnAdd;
    private javax.swing.JButton btnChooseBirthDay;
    private utils.Button btnChooseImage;
    private utils.Button btnRefesh;
    private utils.Button btnSave;
    private utils.Button btnUpdate;
    private javax.swing.JComboBox<String> cmbPosition;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbExit1;
    private javax.swing.JLabel lbImage;
    private javax.swing.JLabel lblQLNV;
    private javax.swing.JPanel pnImage;
    private utils.spinner_progress.SpinnerProgress progressLoading;
    private javax.swing.JRadioButton rbNam;
    private javax.swing.JRadioButton rbNu;
    private javax.swing.JTable tbStaff;
    private javax.swing.JTextField txtBirthDay;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEmployeeName;
    private javax.swing.JTextField txtIDEmployee;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtPhoneNumber;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
