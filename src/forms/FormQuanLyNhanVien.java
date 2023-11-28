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
import javax.swing.ImageIcon;
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
import javax.swing.JFileChooser;

/**
 *
 * @author Raven
 */
public class FormQuanLyNhanVien extends javax.swing.JPanel {
   private File choosenFile;
    private final CategoryController categoryController;
    private List<Category> categories;
    private JPopupMenu menu;
    private PanelSearch search;
    public FormQuanLyNhanVien() {
        initComponents();
        menu = new JPopupMenu();
        search = new PanelSearch();
        categoryController=new CategoryController();
        createTableLastColumnCellEvent();
        initButtonGroup();
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
    private void initButtonGroup()
    {
        btgGender.add(rbNam);
        btgGender.add(rbNu);
        btgRole.add(rbAdmin);
        btgRole.add(rbDeliver);
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
        txtIDEmployee.setText("");
        txtUrl.setText("");
        lbImage.setIcon(null);
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
                        txtIDEmployee.setText(selectedCategory.getCategoryName());
                        txtUrl.setText(selectedCategory.getImageUrl());

                        ImageLoader loader = new ImageLoader(selectedCategory.getImageUrl(), lbImage.getWidth(), lbImage.getHeight());
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
        btgRole = new javax.swing.ButtonGroup();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbCategory = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtIDEmployee = new javax.swing.JTextField();
        txtUrl = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        pnImage = new javax.swing.JPanel();
        lbImage = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblQLNV = new javax.swing.JLabel();
        lbExit1 = new javax.swing.JLabel();
        btnLuu = new utils.Button();
        btnChooseImage = new utils.Button();
        btnRefesh = new utils.Button();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtEmployeeName1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        rbNu = new javax.swing.JRadioButton();
        rbNam = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtPhoneNumber = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        rbDeliver = new javax.swing.JRadioButton();
        rbAdmin = new javax.swing.JRadioButton();
        txtSearch = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(1366, 768));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbCategory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã nhân viên", "Tên nhân viên", "Email", "Giới tính", "Số điện thoại", "Hình", "Chức vụ", "Thao tác"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbCategory.setRowHeight(40);
        tbCategory.setRowSelectionAllowed(false);
        jScrollPane2.setViewportView(tbCategory);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 400, 1080, 340));

        jLabel1.setText("Mã nhân viên:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, -1, -1));

        txtIDEmployee.setEditable(false);
        add(txtIDEmployee, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 355, 34));

        txtUrl.setEditable(false);
        txtUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUrlActionPerformed(evt);
            }
        });
        add(txtUrl, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 90, 355, 34));

        jLabel2.setText("Ảnh nhân viên:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 200, -1, -1));

        pnImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        lbImage.setForeground(new java.awt.Color(255, 255, 255));
        lbImage.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lbImage.setPreferredSize(new java.awt.Dimension(250, 250));
        pnImage.add(lbImage);

        add(pnImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 190, 210, 190));

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

        btnLuu.setBackground(new java.awt.Color(30, 180, 114));
        btnLuu.setForeground(new java.awt.Color(245, 245, 245));
        btnLuu.setText("Lưu");
        btnLuu.setRippleColor(new java.awt.Color(255, 255, 255));
        btnLuu.setShadowColor(new java.awt.Color(30, 180, 114));
        btnLuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuActionPerformed(evt);
            }
        });
        add(btnLuu, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 210, 140, -1));

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
        add(btnChooseImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 270, 140, -1));

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
        add(btnRefesh, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 330, 140, -1));

        jLabel5.setText("Đường dẫn");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 100, -1, -1));

        jLabel6.setText("Tên nhân viên:");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, -1, -1));

        txtEmployeeName1.setEditable(false);
        add(txtEmployeeName1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 150, 355, 34));

        jLabel7.setText("Giới tính:");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, -1, -1));

        txtEmail.setEditable(false);
        add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 210, 355, 34));

        rbNu.setText("Nữ");
        add(rbNu, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 270, -1, -1));

        rbNam.setText("Nam");
        add(rbNam, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 270, -1, -1));

        jLabel10.setText("Email:");
        add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, -1));

        jLabel11.setText("Số điện thoại:");
        add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 340, -1, -1));

        txtPhoneNumber.setEditable(false);
        add(txtPhoneNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 330, 360, 34));

        jLabel12.setText("Chức vụ:");
        add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 150, -1, -1));

        rbDeliver.setText("Deliver");
        add(rbDeliver, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 150, -1, -1));

        rbAdmin.setText("Admin");
        add(rbAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 150, -1, -1));
        add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 20, 260, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void txtUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUrlActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUrlActionPerformed

    private void lbExit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbExit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_lbExit1MouseClicked

    private void btnLuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuActionPerformed
        String response = categoryController.createCategory(txtIDEmployee.getText(),choosenFile);
        if(response.equals("Success")){
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thêm danh mục thành công!");
            refesh();
        }
        else{
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Thêm danh mục thất bại!");
        }
    }//GEN-LAST:event_btnLuuActionPerformed
  private void setFileChooseUI(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void btnChooseImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseImageActionPerformed
        setFileChooseUI();
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null); // Open a file chooser dialog

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            choosenFile=selectedFile;
            // Load the selected image and set it in the label
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btgGender;
    private javax.swing.ButtonGroup btgRole;
    private utils.Button btnChooseImage;
    private utils.Button btnLuu;
    private utils.Button btnRefesh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbExit1;
    private javax.swing.JLabel lbImage;
    private javax.swing.JLabel lblQLNV;
    private javax.swing.JPanel pnImage;
    private javax.swing.JRadioButton rbAdmin;
    private javax.swing.JRadioButton rbDeliver;
    private javax.swing.JRadioButton rbNam;
    private javax.swing.JRadioButton rbNu;
    private javax.swing.JTable tbCategory;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEmployeeName1;
    private javax.swing.JTextField txtIDEmployee;
    private javax.swing.JTextField txtPhoneNumber;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables
}
