package forms;

import controller.VoucherController;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
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
import model.Category;
import model.Dish;
import model.Voucher;
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


public class FormVoucher extends javax.swing.JPanel {
    private final VoucherController voucherController;
    private List<Voucher> vouchers;
    private JPopupMenu menu;
    private PanelSearch search;
    private NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private boolean isEditingEnabled = false,isAddEnabled = false;
    private String selectedVoucherType="";
    public FormVoucher() {
        initComponents();
      
        voucherController=new VoucherController();
        createTableLastColumnCellEvent();
        getAllVoucher();
        comboxBoxValueChangeEvent();
        progressLoading.setVisible(false);
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
                
                System.out.println("Click Item : " + data.getText());
            }

            @Override
            public void itemRemove(Component com, DataSearch data) {
                search.remove(com);
                removeHistory(data.getText());
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
                System.out.println("Edit row : " + row);
            }

            @Override
            public void onDelete(int row) {
                if (tbVoucher.isEditing()) {
                    tbVoucher.getCellEditor().stopCellEditing();
                }
                DefaultTableModel model = (DefaultTableModel) tbVoucher.getModel();
                String categoryId = (String) model.getValueAt(row, 0);
//                deleteCategory(categoryId);
//                if(deleteCategory(categoryId)){
//                    model.removeRow(row);
//                }
//                model.removeRow(row);
            }

            @Override
            public void onView(int row) {
                System.out.println("View row : " + row);
            }
        };
        tbVoucher.getColumnModel().getColumn(8).setCellRenderer(new TableActionCellRender());
        tbVoucher.getColumnModel().getColumn(8).setCellEditor(new TableActionCellEditor(event));
        for (int i = 0; i <= 7; i++) {
            tbVoucher.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    return super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
                }
            });
        }
    }
    private void refesh(){
        txtVoucherName.setText("");
        txtDiscountAmount.setText("");
        txtDiscountPercent.setText("");
        txtDateBegin.setText("");
        txtDateExpired.setText("");
        vouchers=null;
        getAllVoucher();
    }
     private void getAllVoucher()
    {
        List<Voucher> vouchersResult=voucherController.getAllVoucher();
        if(!vouchersResult.isEmpty()){
            vouchers=vouchersResult;
            loadVoucherTable();   
        }
        else
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Không có voucher!");
    }
     
//    private boolean deleteCategory(String categoryId)
//    {
//        String result=voucherController.deleteCategory(categoryId);
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
    private void loadVoucherTable() {
        if (!vouchers.isEmpty()) {
                DefaultTableModel model = (DefaultTableModel) tbVoucher.getModel();
                model.setRowCount(0);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                // Load vouchers first
                for (Voucher voucher : vouchers) {
                  
                    String voucherId = voucher.getVoucherID();
                    String voucherName = voucher.getVoucherName();
                    String startDate =sdf.format(voucher.getStartDate()) ;
                    String expDate =sdf.format(voucher.getExpDate()) ;
                    Double discountAmountReceived=voucher.getDiscountAmount() != null ? voucher.getDiscountAmount() : 0.0;        
                    String discountAmount =fmt.format(discountAmountReceived);
                    Integer discountPercent = voucher.getDiscountPercent() != null ? voucher.getDiscountPercent() : 0;
                    String type = voucher.getType()!= null ? voucher.getType() : "Undefined";
                    Integer pointsRequeired = voucher.getPointRequired();
                    model.addRow(new Object[]{voucherId, voucherName, startDate,expDate,discountAmount,discountPercent,type,pointsRequeired});
                }
//                createTableRowClick();
                
                TableColumn imageColumn = tbVoucher.getColumnModel().getColumn(2);
                imageColumn.setCellRenderer(new ImageCellRender());
                tbVoucher.setRowSorter(new TableRowSorter<>(model));
                tbVoucher.setRowHeight(30);
        } else {
            JOptionPane.showMessageDialog(this, "Không có danh mục nào!", "Lỗi", 0);
        }
    }


    private List<DataSearch> search(String search) {
        int limitData = 7;
        List<DataSearch> list = new ArrayList<>();
        String dataTesting[] = {"300 - Rise of an Empire",
            "Cosmic Sin",
            "Deadlock",
            "Deliver Us from Eva",
            "Empire of the Ants",
            "Empire of the Sun",
            "Empire Records",
            "Empire State",
            "Four Good Days",
            "Frozen Fever",
            "Frozen",
            "The Courier",
            "The First Purge",
            "To Olivia",
            "Underworld"};
        for (String d : dataTesting) {
            if (d.toLowerCase().contains(search)) {
                boolean story = isStory(d);
                if (story) {
                    list.add(0, new DataSearch(d, story));
                    //  add or insert to first record
                } else {
                    list.add(new DataSearch(d, story));
                    //  add to last record
                }
                if (list.size() == limitData) {
                    break;
                }
            }
        }
        return list;
    }
    String dataStory[] = {"300 - Rise of an Empire",
        "Empire Records",
        "Empire State",
        "Frozen",
        "The Courier"};

    private void removeHistory(String text) {
        for (int i = 0; i < dataStory.length; i++) {
            String d = dataStory[i];
            if (d.toLowerCase().equals(text.toLowerCase())) {
                dataStory[i] = "";
            }
        }
    }

    private boolean isStory(String text) {
        for (String d : dataStory) {
            if (d.toLowerCase().equals(text.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    private void enableEdit()
    {
        if(isAddEnabled)
        {
            btnUpdate.setVisible(false);
            btnSave.setVisible(true);
            btnSave.setEnabled(true);
            txtPointsRequired.setEditable(true);
            txtVoucherName.setEditable(true);
            txtDiscountAmount.setEditable(true);
            txtDiscountPercent.setEditable(true);
            txtDateBegin.setEditable(true);
            txtDateExpired.setEditable(true);
        }
        else if(isEditingEnabled){
            btnUpdate.setVisible(true);
            btnUpdate.setEnabled(true);
            btnSave.setVisible(false);
            txtPointsRequired.setEditable(true);
            txtVoucherName.setEditable(true);
            txtDiscountAmount.setEditable(true);
            txtDiscountPercent.setEditable(true);
            txtDateBegin.setEditable(true);
             txtDateExpired.setEditable(true);
        }
        else{
            btnSave.setEnabled(false);
            btnSave.setVisible(false);
            btnUpdate.setVisible(false);
            btnUpdate.setEnabled(false);
            txtPointsRequired.setEditable(false);
            txtVoucherName.setEditable(false);
            txtDiscountAmount.setEditable(false);
            txtDiscountPercent.setEditable(false);
            txtDateBegin.setEditable(false);
            txtDateExpired.setEditable(true);
        }
    }
//    private void createTableRowClick(){
//        tbCategory.addMouseListener(new MouseAdapter(){
//            @Override
//            public void mouseClicked(MouseEvent e) {
//
//                int selectedRow = tbCategory.getSelectedRow();
//                if(selectedRow < 0 || selectedRow == 6)
//                    return;
//                if (selectedRow >= 0) {
//                    try {
//                        Category selectedCategory = vouchers.get(selectedRow);
//                        txtVoucherName.setText(selectedCategory.getCategoryName());
//                    } catch (Exception ex) {
//                        System.out.println("Image error");
//                    }
//                }
//            }
//        });
//    }
//    class ImageRenderer extends DefaultTableCellRenderer {
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            if (value instanceof Icon icon) {
//                setIcon(icon);
//            } else {
//                setText((value == null) ? "" : value.toString());
//            }
//            setHorizontalAlignment(JLabel.CENTER);
//            return this;
//        }
//    }
    private void comboxBoxValueChangeEvent()
    {
        checkComboxBoxIndex();
         cmbType.addItemListener((ItemEvent e) -> {
             if (e.getStateChange() == ItemEvent.SELECTED) {
                 checkComboxBoxIndex();
             }
         });
    }
    private void checkComboxBoxIndex()
    {
        if (cmbType.getSelectedIndex()==0) 
        {
            selectedVoucherType="Percent";
            txtDiscountAmount.setEditable(false);
            txtDiscountPercent.setEditable(true);
        }
        else
        {
            selectedVoucherType="Amount";
            txtDiscountAmount.setEditable(true);
            txtDiscountPercent.setEditable(false);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dateStart = new com.raven.datechooser.DateChooser();
        dateExpired = new com.raven.datechooser.DateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbVoucher = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lbExit1 = new javax.swing.JLabel();
        btnSave = new utils.Button();
        btnRefesh = new utils.Button();
        jLabel6 = new javax.swing.JLabel();
        txtDiscountAmount = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDateBegin = new javax.swing.JTextField();
        btnChooseStart = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtDateExpired = new javax.swing.JTextField();
        btnChooseExpired = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtVoucherName = new javax.swing.JTextField();
        txtSearch = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox<>();
        txtDiscountPercent = new javax.swing.JTextField();
        progressLoading = new utils.spinner_progress.SpinnerProgress();
        btnUpdate = new utils.Button();
        btnAdd = new utils.Button();
        txtPointsRequired = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();

        dateStart.setForeground(new java.awt.Color(255, 102, 51));
        dateStart.setTextField(txtDateBegin);

        dateExpired.setTextField(txtDateExpired);

        setPreferredSize(new java.awt.Dimension(1366, 768));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbVoucher.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã voucher", "Tên voucher", "Ngày bắt đầu", "Ngày hết hạn", "Số tiền giảm", "Phần trăm giảm", "Loại", "Điểm cần đổi", "Thao tác"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbVoucher.setRowHeight(40);
        tbVoucher.setRowSelectionAllowed(false);
        jScrollPane2.setViewportView(tbVoucher);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 1080, 400));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 153, 153));
        jLabel3.setText("Chào mừng chủ nhân đến với chuyên mục quản lý voucher...");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 340, 30));

        jLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(225, 225, 225)));
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 64, 2000, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/doge-42.png"))); // NOI18N
        add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(153, 153, 153));
        jLabel9.setText("QUẢN LÝ VOUCHER");
        add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 6, 230, 30));

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
        btnSave.setEnabled(false);
        btnSave.setRippleColor(new java.awt.Color(255, 255, 255));
        btnSave.setShadowColor(new java.awt.Color(30, 180, 114));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 270, 160, 60));

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
        add(btnRefesh, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 270, 140, 60));

        jLabel6.setText("Ngày bắt đầu sử dụng:");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 100, -1, -1));

        txtDiscountAmount.setEditable(false);
        add(txtDiscountAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 240, 190, 30));

        jLabel7.setText("Điểm cần đổi");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 200, -1, -1));
        add(txtDateBegin, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 90, 310, 30));

        btnChooseStart.setText("...");
        btnChooseStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseStartActionPerformed(evt);
            }
        });
        add(btnChooseStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 90, 40, 30));

        jLabel10.setText("Ngày hết hạn:");
        add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 150, -1, -1));
        add(txtDateExpired, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 140, 310, 30));

        btnChooseExpired.setText("...");
        btnChooseExpired.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseExpiredActionPerformed(evt);
            }
        });
        add(btnChooseExpired, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 140, 40, 30));

        jLabel11.setText("Tên voucher:");
        add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, -1, -1));

        txtVoucherName.setEditable(false);
        add(txtVoucherName, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 90, 360, 30));
        add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 20, 400, -1));

        jLabel12.setText("Phần trăm giảm:");
        add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 250, -1, -1));

        jLabel13.setText("Loại");
        add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 30, 30));

        cmbType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Phần trăm", "Số tiền" }));
        add(cmbType, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 140, 190, 30));

        txtDiscountPercent.setEditable(false);
        add(txtDiscountPercent, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 190, 190, 30));

        progressLoading.setForeground(new java.awt.Color(255, 204, 51));
        progressLoading.setIndeterminate(true);
        add(progressLoading, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 280, 40, 40));

        btnUpdate.setBackground(new java.awt.Color(30, 180, 114));
        btnUpdate.setForeground(new java.awt.Color(245, 245, 245));
        btnUpdate.setText("Cập nhật");
        btnUpdate.setEnabled(false);
        btnUpdate.setRippleColor(new java.awt.Color(255, 255, 255));
        btnUpdate.setShadowColor(new java.awt.Color(30, 180, 114));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 270, 160, 60));

        btnAdd.setBackground(new java.awt.Color(30, 180, 114));
        btnAdd.setForeground(new java.awt.Color(245, 245, 245));
        btnAdd.setText("Thêm mới");
        btnAdd.setRippleColor(new java.awt.Color(255, 255, 255));
        btnAdd.setShadowColor(new java.awt.Color(30, 180, 114));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 280, 140, 50));

        txtPointsRequired.setEditable(false);
        add(txtPointsRequired, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 190, 190, 30));

        jLabel14.setText("Số tiền giảm:");
        add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, -1, -1));
    }// </editor-fold>//GEN-END:initComponents
    
    private void lbExit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbExit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_lbExit1MouseClicked
    private void createNewVoucher(){
        Voucher newVoucher=new Voucher();
        newVoucher.setVoucherName(txtVoucherName.getText());
        newVoucher.setPointRequired(Integer.valueOf(txtPointsRequired.getText()));
        newVoucher.setType(selectedVoucherType);
        if(selectedVoucherType.equals("Percent"))
        {
            newVoucher.setDiscountAmount(Double.valueOf(txtDiscountAmount.getText()));
        }
        else
        {
            newVoucher.setDiscountPercent(Integer.valueOf(txtDiscountPercent.getText()));
        }
        newVoucher.setStartDate(dateStart.getSelectedDate());
        newVoucher.setExpDate(dateExpired.getSelectedDate());

        String result=voucherController.createNewVoucher(newVoucher);
        if(result.equals("Success"))
        {
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thêm voucher thành công!");
            refesh();
            progressLoading.setVisible(false);
        }
        else
        {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, result);
            progressLoading.setVisible(false);
        }
        
    }
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        progressLoading.setVisible(true);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                createNewVoucher();
                return null;
            }
            @Override
            protected void done() {
                progressLoading.setVisible(false);
            }
        };
        worker.execute();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnRefeshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefeshActionPerformed
        refesh();
    }//GEN-LAST:event_btnRefeshActionPerformed

    private void btnChooseStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseStartActionPerformed
       dateStart.showPopup();
    }//GEN-LAST:event_btnChooseStartActionPerformed

    private void btnChooseExpiredActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseExpiredActionPerformed
      dateExpired.showPopup();
    }//GEN-LAST:event_btnChooseExpiredActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        isAddEnabled=true;
        isEditingEnabled=false;
        enableEdit();
    }//GEN-LAST:event_btnAddActionPerformed
    
     

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private utils.Button btnAdd;
    private javax.swing.JButton btnChooseExpired;
    private javax.swing.JButton btnChooseStart;
    private utils.Button btnRefesh;
    private utils.Button btnSave;
    private utils.Button btnUpdate;
    private javax.swing.JComboBox<String> cmbType;
    private com.raven.datechooser.DateChooser dateExpired;
    private com.raven.datechooser.DateChooser dateStart;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbExit1;
    private utils.spinner_progress.SpinnerProgress progressLoading;
    private javax.swing.JTable tbVoucher;
    private javax.swing.JTextField txtDateBegin;
    private javax.swing.JTextField txtDateExpired;
    private javax.swing.JTextField txtDiscountAmount;
    private javax.swing.JTextField txtDiscountPercent;
    private javax.swing.JTextField txtPointsRequired;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtVoucherName;
    // End of variables declaration//GEN-END:variables
}
