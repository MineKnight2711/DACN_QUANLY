/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package forms;

import controller.CategoryController;
import controller.DishController;
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
import javax.swing.DefaultComboBoxModel;
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
import model.Dish;
import raven.toast.Notifications;
import utils.ImageCellRender;
import utils.ImageLoader;
import utils.spinner_progress.SpinnerProgress;
import utils.table.TableActionCellEditor;
import utils.table.TableActionCellRender;
import utils.table.TableActionEvent;

/**
 *
 * @author MINHNHAT
 */
public class FormDish extends javax.swing.JPanel {
    private File choosenFile;
    private List<Category> listCategory;
    private List<Dish> listDish;
    private final CategoryController categoryController;
    private final DishController dishController;
    public FormDish() {
        initComponents();
        categoryController=new CategoryController();
        dishController=new DishController();
        loadComboBox();
        createTableLastColumnCellEvent();
        getAllDish();
    }
    private void loadComboBox(){
        listCategory = categoryController.getAllCategory();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for(Category cate : listCategory){
            model.addElement(cate);
        }
        cmbCategory.setModel(model);
    }
     private void getAllDish(){
        List<Dish> dishesResult=dishController.getAllDish();
        if(dishesResult!=null){
            listDish=dishesResult;
            loadDishTable();   
        }
        else
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Không có danh mục!");
    }
     
    private void createTableLastColumnCellEvent(){
         TableActionEvent event = new TableActionEvent() {
            @Override
            public void onEdit(int row) {
                System.out.println("Edit row : " + row);
            }

            @Override
            public void onDelete(int row) {
                if (tbDish.isEditing()) {
                    tbDish.getCellEditor().stopCellEditing();
                }
                DefaultTableModel model = (DefaultTableModel) tbDish.getModel();
                String dishId = (String) model.getValueAt(row, 0);
                deleteDish(dishId); 
            }

            @Override
            public void onView(int row) {
                System.out.println("View row : " + row);
            }
        };
        tbDish.getColumnModel().getColumn(6).setCellRenderer(new TableActionCellRender());
        tbDish.getColumnModel().getColumn(6).setCellEditor(new TableActionCellEditor(event));
        
        
    }
    private void refesh(){
        txtDishName.setText("");
        txtDescription.setText("");
        txtInstock.setText("");
        txtPrice.setText("");
        lbImage.setIcon(null);
        choosenFile=null;
        listDish=null;
        getAllDish();
    }
    private boolean deleteDish(String dishId){
        String result=dishController.deleteDish(dishId);
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
    private void loadDishTable() {
        if (!listDish.isEmpty()) {
                DefaultTableModel model = (DefaultTableModel) tbDish.getModel();
                model.setRowCount(0);
                int totalDishes = listDish.size();

                // Load categories first
                for (Dish dish : listDish) {
                  
                    String dishId = dish.getDishID();
                    String dishName=dish.getDishName();
                    String description=dish.getDescription();
                    int inStock=dish.getInStock();
                    double price=dish.getPrice();
                    
                    SpinnerProgress progressBar=new SpinnerProgress();
                    progressBar.setPreferredSize(new Dimension(50,50)); 
                    
                    model.addRow(new Object[]{dishId, dishName,description,price,inStock, progressBar});
                }
                createTableRowClick();
                AtomicInteger count = new AtomicInteger(0);
                
                // Load images asynchronously
                for (int i = 0; i < totalDishes; i++) {
                    Dish dish = listDish.get(i);
                    String imageUrl = dish.getImageUrl();
                    final int currentIndex = i;
                    
                    ImageLoader imageLoader = new ImageLoader(imageUrl, 100, 100);
                    
                    imageLoader.addPropertyChangeListener(evt -> {
                        int progress = imageLoader.getProgress();

                        SpinnerProgress progressBar = (SpinnerProgress)model.getValueAt(currentIndex, 5);
                        progressBar.setValue(progress);
                        
                        
                        if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE == evt.getNewValue()) {
                            try {
                                ImageIcon scaledImageIcon = imageLoader.get();
                                 
                                // Update the UI on the EDT
                                SwingUtilities.invokeLater(() -> {
                                    model.setValueAt(scaledImageIcon, currentIndex, 5);
                                    
                                    int currentCount = count.incrementAndGet();
                                    if (currentCount == totalDishes) {
                                        // All images are loaded, do additional UI setup here
                                        TableColumn imageColumn = tbDish.getColumnModel().getColumn(5);
                                        imageColumn.setCellRenderer(new ImageCellRender());
                                        tbDish.setRowSorter(new TableRowSorter<>(model));
                                        tbDish.setRowHeight(150);
                                        tbDish.revalidate();
                                        tbDish.repaint();
                                        
                                    }
                                });
                                
                            } catch (InterruptedException | ExecutionException e) {
                                System.out.println("Error when loading image: " + e.getMessage());
                            }
                        }
                    });
                    
                    imageLoader.execute();
                }
                TableColumn imageColumn = tbDish.getColumnModel().getColumn(5);
                imageColumn.setCellRenderer(new ImageCellRender());
                tbDish.setRowHeight(50);
        } else {
            JOptionPane.showMessageDialog(this, "Không có danh mục nào!", "Lỗi", 0);
        }
    }
    
     private void createTableRowClick(){
        tbDish.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {

                int selectedRow = tbDish.getSelectedRow();
                if(selectedRow < 0 || selectedRow == 6)
                    return;
                if (selectedRow >= 0) {
                    try {
                        Dish selectedDish = listDish.get(selectedRow);
                        txtDishName.setText(selectedDish.getDishName());
                        txtInstock.setText(String.valueOf(selectedDish.getInStock()));
                        txtPrice.setText(String.valueOf(selectedDish.getPrice()));
                        txtDescription.setText(selectedDish.getDescription());
                        ImageLoader loader = new ImageLoader(selectedDish.getImageUrl(), lbImage.getWidth(), lbImage.getHeight());
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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cmbCategory = new javax.swing.JComboBox<>();
        txtDishName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbDish = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        txtInstock = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnRefesh = new javax.swing.JButton();
        btnChooseImage = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        lbImage = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1050, 740));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Danh mục:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 84, 30));

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        add(cmbCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(182, 60, 151, 30));
        add(txtDishName, new org.netbeans.lib.awtextra.AbsoluteConstraints(182, 6, 256, 30));

        jLabel2.setText("Tên món ăn:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 84, 30));

        tbDish.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã món ăn", "Tên món ăn", "Mô tả", "Giá", "Còn lại", "Hình", "Thao tác"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tbDish);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 350, 990, -1));

        jLabel4.setText("Số lượng tồn :");
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 180, 92, 30));
        add(txtInstock, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 180, 216, 30));

        jLabel5.setText("Mô tả món:");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 250, -1, 30));

        jLabel6.setText("Giá:");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, 60, 24));
        add(txtPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 110, 277, 37));

        btnSave.setText("Lưu");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 250, 142, 47));

        btnRefesh.setText("Làm mới");
        btnRefesh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefeshActionPerformed(evt);
            }
        });
        add(btnRefesh, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 250, 85, 50));

        btnChooseImage.setText("Chọn ảnh");
        btnChooseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseImageActionPerformed(evt);
            }
        });
        add(btnChooseImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 250, 99, 50));

        txtDescription.setColumns(20);
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        jScrollPane2.setViewportView(txtDescription);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 230, 362, 71));

        lbImage.setPreferredSize(new java.awt.Dimension(200, 200));
        add(lbImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 30, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        Dish newDish=new Dish();
        newDish.setDishName(txtDishName.getText());
        newDish.setDescription(txtDescription.getText());
        newDish.setInStock(Integer.parseInt(txtInstock.getText()));
        newDish.setPrice(Double.parseDouble(txtPrice.getText()));
        Category selectedCategory=(Category) cmbCategory.getSelectedItem();
        newDish.setCategoryId(selectedCategory.getCategoryID());
        String result=dishController.createNewDish(choosenFile, newDish);
        JOptionPane.showMessageDialog(this, result);
    }//GEN-LAST:event_btnSaveActionPerformed
     private void setFileChooseUI(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.out.println("Loi chon hinh");
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
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRefeshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseImage;
    private javax.swing.JButton btnRefesh;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbImage;
    private javax.swing.JTable tbDish;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtDishName;
    private javax.swing.JTextField txtInstock;
    private javax.swing.JTextField txtPrice;
    // End of variables declaration//GEN-END:variables
}
