package forms;

import controller.CategoryController;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon; 
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import model.Category;
import model.Dish;
import raven.toast.Notifications;
import utils.DataSearch;
import utils.EventClick;
import utils.ImageCellRender;
import utils.ImageLoader;
import utils.ImagePreviewLabel;
import utils.PanelSearch;
import utils.spinner_progress.SpinnerProgress;
import utils.table.TableActionCellEditor;
import utils.table.TableActionCellRender;
import utils.table.TableActionEvent;


/**
 *
 * @author Raven
 */
public class FormCategory extends javax.swing.JPanel {
    private File choosenFile;
    private final CategoryController categoryController;
    private List<Category> categories;
    private Category selectedCategory;
    private boolean isEditingEnabled = false,isAddEnabled = false;
    private JPopupMenu menu;
    private PanelSearch search;
    public FormCategory() {
        initComponents();
        categoryController=new CategoryController();
        createTableLastColumnCellEvent();
        getAllCategory();
        createSearchTextField();
        circleProgress.setVisible(false);
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
                if (tbCategory.isEditing()) {
                    tbCategory.getCellEditor().stopCellEditing();
                }
                DefaultTableModel model = (DefaultTableModel) tbCategory.getModel();
                String categoryId = (String) model.getValueAt(row, 0);
                deleteCategory(categoryId);
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
     private void createSearchTextField()
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
                selectedCategory=categories.stream().filter((Category category) -> category.getCategoryName().equals(data.getText())).findFirst().orElse(null);
                if(selectedCategory!=null)
                {
                    isEditingEnabled=true;
                    enableEdit();
                    categories.removeIf(cate -> !cate.getCategoryName().equals(data.getText()));
                    loadCategoryTable();
                    fillTextField(selectedCategory);

                }
                
            }

            @Override
            public void itemRemove(Component com, DataSearch data) {
             
            }
        });
    }
    private void fillTextField(Category category){
        txtCategoryName.setText(category.getCategoryName());
        txtUrl.setText(category.getCategoryName());
        ImageLoader loader = new ImageLoader(category.getImageUrl(), lbImage.getWidth(), lbImage.getHeight());
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
    }
    private void refesh(){
        txtCategoryName.setText("");
        txtUrl.setText("");
        lbImage.setIcon(null);
        choosenFile=null;
        categories=null;
        getAllCategory();
    }
    private void clearText()
    {
        txtCategoryName.setText("");
        txtUrl.setText("");
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
            txtCategoryName.setEditable(true);

            clearText();
        }
        else if(isEditingEnabled){
            btnUpdate.setVisible(true);
            btnUpdate.setEnabled(true);
            btnSave.setVisible(false);
            btnChooseImage.setEnabled(true);
            txtCategoryName.setEditable(true);

        }
        else{
            btnSave.setEnabled(false);
            btnSave.setVisible(false);
            btnUpdate.setVisible(false);
            btnUpdate.setEnabled(false);
            btnChooseImage.setEnabled(false);
            txtCategoryName.setEditable(false);

        }
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


    private void createTableRowClick(){
        tbCategory.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {

                int selectedRow = tbCategory.getSelectedRow();
                int modelRowIndex = tbCategory.convertRowIndexToModel(selectedRow);
                if(modelRowIndex < 0 || modelRowIndex == 6)
                    return;
                if (modelRowIndex >= 0) {
                    isAddEnabled=false;
                    isEditingEnabled=false;
                    enableEdit();
                    try {
                        selectedCategory = categories.get(modelRowIndex);
                        txtCategoryName.setText(selectedCategory.getCategoryName());
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

        jScrollPane2 = new javax.swing.JScrollPane();
        tbCategory = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtCategoryName = new javax.swing.JTextField();
        txtUrl = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        pnImage = new javax.swing.JPanel();
        lbImage = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lbExit1 = new javax.swing.JLabel();
        btnSave = new utils.Button();
        btnChooseImage = new utils.Button();
        btnRefesh = new utils.Button();
        jLabel5 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnUpdate = new utils.Button();
        btnAdd = new utils.Button();
        circleProgress = new utils.spinner_progress.SpinnerProgress();
        jLabel6 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1366, 768));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbCategory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã danh mục", "Tên danh mục", "Hình", "Thao tác"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbCategory.setRowHeight(40);
        tbCategory.setRowSelectionAllowed(false);
        jScrollPane2.setViewportView(tbCategory);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 1080, 440));

        jLabel1.setText("Tên danh mục");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        txtCategoryName.setEditable(false);
        add(txtCategoryName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 355, 34));

        txtUrl.setEditable(false);
        txtUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUrlActionPerformed(evt);
            }
        });
        add(txtUrl, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 150, 355, 34));

        jLabel2.setText("Tìm kiếm:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 30, 60, -1));

        pnImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        lbImage.setForeground(new java.awt.Color(255, 255, 255));
        lbImage.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lbImage.setPreferredSize(new java.awt.Dimension(250, 250));
        pnImage.add(lbImage);

        add(pnImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 80, 210, 190));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 153, 153));
        jLabel3.setText("Chào mừng chủ nhân đến với quản lý danh mục...");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 340, 30));

        jLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(225, 225, 225)));
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 64, 2000, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/doge-42.png"))); // NOI18N
        add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(153, 153, 153));
        jLabel9.setText("QUẢN LÝ DANH MỤC");
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
        btnSave.setRippleColor(new java.awt.Color(255, 255, 255));
        btnSave.setShadowColor(new java.awt.Color(30, 180, 114));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 80, 140, -1));

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
        add(btnChooseImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 120, 140, -1));

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
        add(btnRefesh, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 160, 140, -1));

        jLabel5.setText("Đường dẫn");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, -1, -1));

        txtSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchMouseClicked(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 20, 300, 30));

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
        add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 80, 140, -1));

        btnAdd.setBackground(new java.awt.Color(30, 180, 114));
        btnAdd.setForeground(new java.awt.Color(245, 245, 245));
        btnAdd.setText("+ Thêm danh mục");
        btnAdd.setRippleColor(new java.awt.Color(255, 255, 255));
        btnAdd.setShadowColor(new java.awt.Color(30, 180, 114));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 230, 140, -1));

        circleProgress.setForeground(new java.awt.Color(255, 153, 51));
        circleProgress.setIndeterminate(true);
        add(circleProgress, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 80, 40, 40));

        jLabel6.setText("Ảnh danh mục:");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 90, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void txtUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUrlActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUrlActionPerformed

    private void lbExit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbExit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_lbExit1MouseClicked

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        
        circleProgress.setVisible(true);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                String response = categoryController.createCategory(txtCategoryName.getText(),choosenFile);
                if(response.equals("Success")){
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thêm danh mục thành công!");
                    refesh();   
                }
                else{
                     Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Thêm danh mục thất bại!");
                }
                return null;
            }
            @Override
            protected void done() {
                circleProgress.setVisible(false);
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
    private void updateCategory()
    {
        if(selectedCategory!=null)
        {
            Category updatedCategory=selectedCategory;
            updatedCategory.setCategoryName(txtCategoryName.getText());
            String result=categoryController.updateCategory(choosenFile,updatedCategory);
            
            if(result.equals("Success")){
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Cập nhật món thành công!");
                refesh(); 
            }
            else{
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, result);
            }
        }
    }
    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        circleProgress.setVisible(true);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                updateCategory();
                return null;
            }
            @Override
            protected void done() {
                circleProgress.setVisible(false);
            }
        };
        worker.execute();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        isAddEnabled=true;
        isEditingEnabled=false;
        enableEdit();
    }//GEN-LAST:event_btnAddActionPerformed

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

    private void txtSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchMouseClicked
        if (search.getItemSize() > 0) {
            menu.show(txtSearch, 0, txtSearch.getHeight());
        }
    }//GEN-LAST:event_txtSearchMouseClicked
    private List<DataSearch> search(String search) {
        int limitData = 7;
        List<DataSearch> list = new ArrayList<>();
        for (Category c : categories) {
            if (c.getCategoryName().toLowerCase().contains(search)) 
            {
                list.add(0, new DataSearch(c.getCategoryName()));
                if (list.size() == limitData) 
                {
                    break;
                }
            }
        }
        return list;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private utils.Button btnAdd;
    private utils.Button btnChooseImage;
    private utils.Button btnRefesh;
    private utils.Button btnSave;
    private utils.Button btnUpdate;
    private utils.spinner_progress.SpinnerProgress circleProgress;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbExit1;
    private javax.swing.JLabel lbImage;
    private javax.swing.JPanel pnImage;
    private javax.swing.JTable tbCategory;
    private javax.swing.JTextField txtCategoryName;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables
}
