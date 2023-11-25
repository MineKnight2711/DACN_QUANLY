package forms;

import controller.CategoryController;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon; 
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import model.Category;
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


/**
 *
 * @author Raven
 */
public class FormQLDonHang extends javax.swing.JPanel {
    private File choosenFile;
    private final CategoryController categoryController;
    private List<Category> categories;
    private JPopupMenu menu;
    private PanelSearch search;
    public FormQLDonHang() {
        initComponents();
      
        categoryController=new CategoryController();
        createTableLastColumnCellEvent();
        getAllCategory();
        
        
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
                if (tbCategory.isEditing()) {
                    tbCategory.getCellEditor().stopCellEditing();
                }
                DefaultTableModel model = (DefaultTableModel) tbCategory.getModel();
                String categoryId = (String) model.getValueAt(row, 0);
                deleteCategory(categoryId);
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
        tbCategory.getColumnModel().getColumn(3).setCellRenderer(new TableActionCellRender());
        tbCategory.getColumnModel().getColumn(3).setCellEditor(new TableActionCellEditor(event));
        tbCategory.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                setHorizontalAlignment(SwingConstants.LEFT);
                return super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
            }
        });
    }
    private void refesh(){
        txtIDVoucher.setText("");
      
       
        choosenFile=null;
        categories=null;
        getAllCategory();
    }
     private void getAllCategory(){
        List<Category> categoriesResult=categoryController.getAllCategory();
        if(categoriesResult!=null){
            categories=categoriesResult;
            loadCategoryTable();   
        }
        else
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Không có danh mục!");
    }
    private boolean deleteCategory(String categoryId){
        String result=categoryController.deleteCategory(categoryId);
        if(result.equals("Success")){
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Xoá danh mục thành công!");
            refesh(); 
            return true;
        }
        else{
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, result);
            return false;
        }
    }
    private void loadCategoryTable() {
        if (!categories.isEmpty()) {
                DefaultTableModel model = (DefaultTableModel) tbCategory.getModel();
                model.setRowCount(0);
                int totalCategories = categories.size();

                // Load categories first
                for (Category cate : categories) {
                  
                    String userID = cate.getCategoryID();
                    String imageID = cate.getCategoryName();
                    SpinnerProgress progressBar=new SpinnerProgress();
                    progressBar.setPreferredSize(new Dimension(50,50)); 
                    
                    model.addRow(new Object[]{userID, imageID, progressBar});
                }
                createTableRowClick();
                AtomicInteger count = new AtomicInteger(0);
                
                // Load images asynchronously
                for (int i = 0; i < totalCategories; i++) {
                    Category cate = categories.get(i);
                    String imageUrl = cate.getImageUrl();
                    final int currentIndex = i;
                    
                    ImageLoader imageLoader = new ImageLoader(imageUrl, 150, 150);
                    
                    imageLoader.addPropertyChangeListener(evt -> {
                        int progress = imageLoader.getProgress();

                        SpinnerProgress progressBar = (SpinnerProgress)model.getValueAt(currentIndex, 2);
                        progressBar.setValue(progress);
                        
                        
                        if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE == evt.getNewValue()) {
                            try {
                                ImageIcon scaledImageIcon = imageLoader.get();
                                 
                                // Update the UI on the EDT
                                SwingUtilities.invokeLater(() -> {
                                    model.setValueAt(scaledImageIcon, currentIndex, 2);
                                    
                                    int currentCount = count.incrementAndGet();
                                    if (currentCount == totalCategories) {
                                        // All images are loaded, do additional UI setup here
                                        TableColumn imageColumn = tbCategory.getColumnModel().getColumn(2);
                                        imageColumn.setCellRenderer(new ImageCellRender());
                                        tbCategory.setRowSorter(new TableRowSorter<>(model));
                                        tbCategory.setRowHeight(150);
                                        tbCategory.revalidate();
                                        tbCategory.repaint();
                                        
                                    }
                                });
                                
                            } catch (InterruptedException | ExecutionException e) {
                                System.out.println("Error when loading image: " + e.getMessage());
                            }
                        }
                    });
                    
                    imageLoader.execute();
                }
                TableColumn imageColumn = tbCategory.getColumnModel().getColumn(2);
                imageColumn.setCellRenderer(new ImageCellRender());
                tbCategory.setRowHeight(50);
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
    
    private void createTableRowClick(){
        tbCategory.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {

                int selectedRow = tbCategory.getSelectedRow();
                if(selectedRow < 0 || selectedRow == 6)
                    return;
                if (selectedRow >= 0) {
                    try {
                        Category selectedCategory = categories.get(selectedRow);
                        txtIDVoucher.setText(selectedCategory.getCategoryName());
                       

           
                       
                    } catch (Exception ex) {
                        System.out.println("Image error");
                    }
                }
            }
        });
    }
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
    


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dateStart = new com.raven.datechooser.DateChooser();
        dateExpired = new com.raven.datechooser.DateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbCategory = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtIDVoucher = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lbExit1 = new javax.swing.JLabel();
        txtSearch = new swing.MyTextField();
        btnXemChiTiet = new button.Button();
        btnRefesh = new button.Button();
        jLabel6 = new javax.swing.JLabel();
        txtPaymentMethod = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtOrderDate = new javax.swing.JTextField();
        btnChooseExpired = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtIDVoucher1 = new javax.swing.JTextField();
        btnPrint = new button.Button();
        txtIDVoucher2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtVoucher = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();

        dateStart.setForeground(new java.awt.Color(255, 102, 51));

        dateExpired.setTextField(txtOrderDate);

        setPreferredSize(new java.awt.Dimension(1366, 768));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbCategory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã đơn hàng", "Mã khách hàng", "Tên khách hàng", "Ngày đặt hàng", "Phương thức thanh toán", "Mã voucher", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbCategory.setRowHeight(40);
        tbCategory.setRowSelectionAllowed(false);
        jScrollPane2.setViewportView(tbCategory);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 1080, 440));

        jLabel1.setText("Mã đơn hàng:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, -1, -1));

        txtIDVoucher.setEditable(false);
        add(txtIDVoucher, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 360, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 153, 153));
        jLabel3.setText("Chào mừng chủ nhân đến với chuyên mục quản lý đơn hàng...");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 340, 30));

        jLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(225, 225, 225)));
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 64, 2000, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/doge-42.png"))); // NOI18N
        add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(153, 153, 153));
        jLabel9.setText("QUẢN LÝ ĐƠN HÀNG");
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

        txtSearch.setForeground(new java.awt.Color(153, 153, 153));
        txtSearch.setPrefixIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search_small.png"))); // NOI18N
        txtSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchMouseClicked(evt);
            }
        });
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 20, 420, 30));

        btnXemChiTiet.setBackground(new java.awt.Color(30, 180, 114));
        btnXemChiTiet.setForeground(new java.awt.Color(245, 245, 245));
        btnXemChiTiet.setText("Xem chi tiết đơn hàng");
        btnXemChiTiet.setRippleColor(new java.awt.Color(255, 255, 255));
        btnXemChiTiet.setShadowColor(new java.awt.Color(30, 180, 114));
        btnXemChiTiet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXemChiTietActionPerformed(evt);
            }
        });
        add(btnXemChiTiet, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 240, 150, 45));

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
        add(btnRefesh, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 240, 150, 45));

        jLabel6.setText("Tên khách hàng:");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, -1, -1));

        txtPaymentMethod.setEditable(false);
        add(txtPaymentMethod, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 90, 316, 30));

        jLabel7.setText("P. Thức thanh toán:");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 100, -1, -1));

        jLabel10.setText("Ngày đặt hàng:");
        add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 250, -1, -1));
        add(txtOrderDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 240, 310, 30));

        btnChooseExpired.setText("...");
        btnChooseExpired.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseExpiredActionPerformed(evt);
            }
        });
        add(btnChooseExpired, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 240, 40, 30));

        jLabel11.setText("Mã khách hàng:");
        add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, -1, -1));

        txtIDVoucher1.setEditable(false);
        add(txtIDVoucher1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 190, 360, 30));

        btnPrint.setBackground(new java.awt.Color(255, 102, 51));
        btnPrint.setForeground(new java.awt.Color(255, 255, 255));
        btnPrint.setText("Print");
        add(btnPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 240, 150, 45));

        txtIDVoucher2.setEditable(false);
        add(txtIDVoucher2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 140, 360, 30));

        jLabel2.setText("Áp mã voucher:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 150, -1, -1));

        txtVoucher.setEditable(false);
        add(txtVoucher, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 140, 316, 30));

        jLabel5.setText("Status:");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 200, -1, -1));

        txtStatus.setEditable(false);
        add(txtStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 190, 316, 30));
    }// </editor-fold>//GEN-END:initComponents
    private void setFileChooseUI(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void lbExit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbExit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_lbExit1MouseClicked

    private void txtSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchMouseClicked
        if (search.getItemSize() > 0) {
            menu.show(txtSearch, 0, txtSearch.getHeight());
        }
    }//GEN-LAST:event_txtSearchMouseClicked

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        String text = txtSearch.getText().trim().toLowerCase();
        search.setData(search(text));
        if (search.getItemSize() > 0) {
            //  * 2 top and bot border
            menu.show(txtSearch, 0, txtSearch.getHeight());
            menu.setPopupSize(menu.getWidth(), (search.getItemSize() * 35) + 2);
        } else {
            menu.setVisible(false);
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    private void btnXemChiTietActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXemChiTietActionPerformed
        String response = categoryController.createCategory(txtIDVoucher.getText(),choosenFile);
        if(response.equals("Success")){
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thêm danh mục thành công!");
            refesh();   
        }
        else{
             Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Thêm danh mục thất bại!");
        }
    }//GEN-LAST:event_btnXemChiTietActionPerformed

    private void btnRefeshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefeshActionPerformed
        refesh();
    }//GEN-LAST:event_btnRefeshActionPerformed

    private void btnChooseExpiredActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseExpiredActionPerformed
      dateExpired.showPopup();
    }//GEN-LAST:event_btnChooseExpiredActionPerformed
    
     

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseExpired;
    private button.Button btnPrint;
    private button.Button btnRefesh;
    private button.Button btnXemChiTiet;
    private com.raven.datechooser.DateChooser dateExpired;
    private com.raven.datechooser.DateChooser dateStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbExit1;
    private javax.swing.JTable tbCategory;
    private javax.swing.JTextField txtIDVoucher;
    private javax.swing.JTextField txtIDVoucher1;
    private javax.swing.JTextField txtIDVoucher2;
    private javax.swing.JTextField txtOrderDate;
    private javax.swing.JTextField txtPaymentMethod;
    private swing.MyTextField txtSearch;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JTextField txtVoucher;
    // End of variables declaration//GEN-END:variables
}
