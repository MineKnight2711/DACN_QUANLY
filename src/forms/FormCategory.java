package forms;

import controller.CategoryController;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon; 
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import utils.ImageLoader;
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
    private CategoryController categoryController;
    private List<Category> categories;
    public FormCategory() {
        initComponents();
        categoryController=new CategoryController();
        createTableLastColumnCellEvent();
        getAllCategory();
        
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
        txtCategoryName.setText("");
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
//                    progress++;
//                    int progressPercent = (progress * 100) / totalCategories;
//                    progressBar.setValue(progressPercent);
                    // Use a separate final variable
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
                                        imageColumn.setCellRenderer(new ImageRenderer());
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
                imageColumn.setCellRenderer(new ImageRenderer());
                tbCategory.setRowHeight(50);
        } else {
            JOptionPane.showMessageDialog(this, "Không có danh mục nào!", "Lỗi", 0);
        }
    }
//    private void loadCategoryTable() {
//        if (!categories.isEmpty()) {
//            
//            DefaultTableModel model = (DefaultTableModel) tbCategory.getModel();
//            model.setRowCount(0);
//            int numberOfCategories = categories.size();
//        AtomicInteger count = new AtomicInteger(0);
//            for (Category cate : categories) {
//                String userID = cate.getCategoryID();
//                String imageID = cate.getCategoryName();
//                String imageUrl = cate.getImageUrl();
//                TableColumn spinnerColumn = tbCategory.getColumnModel().getColumn(2);
//                spinnerColumn.setCellRenderer(new ImageRenderer());
//                // Create an instance of ImageLoader to load the image asynchronously
//                ImageLoader imageLoader = new ImageLoader(imageUrl, 100, 100);
//
//                imageLoader.addPropertyChangeListener(evt -> {
//                if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE == evt.getNewValue()) {
//                    try {
//                        ImageIcon scaledImageIcon = imageLoader.get();
//                        
//                        // Update the UI on the EDT
//                        SwingUtilities.invokeLater(() -> {
//                            model.addRow(new Object[]{userID, imageID, scaledImageIcon});
//                            tbCategory.revalidate();
//
//                            int currentCount = count.incrementAndGet();
//                            if (currentCount == numberOfCategories) {
//                                // All images are loaded, do additional UI setup here
//                                TableColumn imageColumn = tbCategory.getColumnModel().getColumn(2);
//                                imageColumn.setCellRenderer(new ImageRenderer());
//                                tbCategory.setRowSorter(new TableRowSorter<>(model));
//                                tbCategory.setRowHeight(100);
//                                createTableRowClick();
//                            }
//                        });
//
//                    } catch (InterruptedException | ExecutionException e) {
//                        System.out.println("Error when loading image: " + e.getMessage());
//                    }
//                }
//            });
//
//            imageLoader.execute();
//            }
//
//
////            TableColumn imageColumn = tbCategory.getColumnModel().getColumn(2);
////            imageColumn.setCellRenderer(new ImageRenderer());
////            tbCategory.setRowSorter(new TableRowSorter(model));
////            tbCategory.setRowHeight(100);
////            createTableRowClick();
//        } else {
//            JOptionPane.showMessageDialog(this, "Không có danh mục nào!", "Lỗi", 0);
//        }
//        
//    }
    class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Icon icon) {
                setIcon(icon);
                setText(null); // Clear text
            } else if (value instanceof SpinnerProgress progressBar) { 
                progressBar.setStringPainted(true); 
                progressBar.setPreferredSize(new Dimension(50,50));
                return progressBar;
            } else {
                setIcon(null); // Clear icon
                setText((value == null) ? "" : value.toString());
            }

            setHorizontalAlignment(JLabel.CENTER);
            return this;
        }
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
        btnRefresh = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtCategoryName = new javax.swing.JTextField();
        txtUrl = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnLuu = new javax.swing.JButton();
        btnChooseImage = new javax.swing.JButton();
        pnImage = new javax.swing.JPanel();
        lbImage = new javax.swing.JLabel();

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

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 300, 980, 440));

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 200, 89, 46));

        jLabel1.setText("Tên danh mục");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, -1, -1));
        add(txtCategoryName, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, 355, 34));

        txtUrl.setEditable(false);
        txtUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUrlActionPerformed(evt);
            }
        });
        add(txtUrl, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 120, 355, 34));

        jLabel2.setText("Đường dẫn");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 130, -1, -1));

        btnLuu.setText("Lưu");
        btnLuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuActionPerformed(evt);
            }
        });
        add(btnLuu, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 200, 98, 48));

        btnChooseImage.setText("Chọn ảnh");
        btnChooseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseImageActionPerformed(evt);
            }
        });
        add(btnChooseImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 200, 104, 48));

        lbImage.setPreferredSize(new java.awt.Dimension(250, 250));
        pnImage.add(lbImage);

        add(pnImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 20, 250, 250));
    }// </editor-fold>//GEN-END:initComponents

    private void txtUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUrlActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUrlActionPerformed
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

    private void btnLuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuActionPerformed

        String response = categoryController.createCategory(txtCategoryName.getText(),choosenFile);
        if(response.equals("Success")){
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thêm danh mục thành công!");
            refesh();   
        }
        else{
             Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Thêm danh mục thất bại!");
        }
    }//GEN-LAST:event_btnLuuActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        refesh();
    }//GEN-LAST:event_btnRefreshActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseImage;
    private javax.swing.JButton btnLuu;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbImage;
    private javax.swing.JPanel pnImage;
    private javax.swing.JTable tbCategory;
    private javax.swing.JTextField txtCategoryName;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables
}
