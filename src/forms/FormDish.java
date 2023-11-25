/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package forms;

import controller.CategoryController;
import controller.DishController;
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
import javax.swing.DefaultComboBoxModel;
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
import model.Dish;
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
 * @author MINHNHAT
 */
public class FormDish extends javax.swing.JPanel {
    private File choosenFile;
    private List<Category> listCategory;
    private List<Dish> listDish;
    private final CategoryController categoryController;
    private final DishController dishController;
    private boolean isEditingEnabled = false;
    private boolean isEditMode = false;
    private JPopupMenu menu;
    private PanelSearch search;
    
    public FormDish() {
        initComponents();
        categoryController=new CategoryController();
        dishController=new DishController();
        loadComboBox();
        createTableLastColumnCellEvent();
        getAllDish();
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
                 isEditingEnabled = true;
    // Cập nhật chỉnh sửa cho hàng được nhấp vào

    // Mở khoá chỉnh sửa cho TextField "txtDishName"
    txtDishName.setEditable(true);
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
//    private void updateDish(String name){
//        String result=dishController.updateDish(dishId);
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
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        lbImage = new javax.swing.JLabel();
        btnSave = new button.Button();
        btnRefesh = new button.Button();
        cmd1 = new button.Button();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbExit1 = new javax.swing.JLabel();
        txtSearch = new swing.MyTextField();

        setMinimumSize(new java.awt.Dimension(1366, 768));
        setPreferredSize(new java.awt.Dimension(1366, 768));
        setRequestFocusEnabled(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Danh mục:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, 84, 30));

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        add(cmbCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 140, 151, 30));

        txtDishName.setEditable(false);
        add(txtDishName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 256, 30));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 153, 153));
        jLabel2.setText("Chào mừng chủ nhân đến với danh mục quản lý món ăn...");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 340, 30));

        jScrollPane1.setFocusable(false);

        tbDish.setForeground(new java.awt.Color(51, 51, 51));
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
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbDish.setRowSelectionAllowed(false);
        tbDish.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tbDish.setSelectionForeground(new java.awt.Color(204, 204, 204));
        jScrollPane1.setViewportView(tbDish);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 390, 1090, 360));

        jLabel4.setText("Ảnh món:");
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 210, 92, 30));

        txtInstock.setEditable(false);
        add(txtInstock, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 140, 220, 30));

        jLabel5.setText("Mô tả món:");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, -1, 30));

        jLabel6.setText("Giá:");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 80, 60, 24));

        txtPrice.setEditable(false);
        add(txtPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 80, 220, 30));

        txtDescription.setEditable(false);
        txtDescription.setColumns(20);
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        jScrollPane2.setViewportView(txtDescription);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, 260, 120));

        lbImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        lbImage.setPreferredSize(new java.awt.Dimension(200, 200));
        add(lbImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 190, 220, 190));

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
        add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 330, 140, -1));

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
        add(btnRefesh, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 330, 140, -1));

        cmd1.setBackground(new java.awt.Color(253, 83, 83));
        cmd1.setForeground(new java.awt.Color(245, 245, 245));
        cmd1.setText("Chọn ảnh");
        cmd1.setRippleColor(new java.awt.Color(255, 255, 255));
        cmd1.setShadowColor(new java.awt.Color(253, 83, 83));
        cmd1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmd1ActionPerformed(evt);
            }
        });
        add(cmd1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 330, 140, -1));

        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(225, 225, 225)));
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 64, 2000, -1));

        jLabel7.setText("Tên món ăn:");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 84, 30));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/doge-42.png"))); // NOI18N
        add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(153, 153, 153));
        jLabel9.setText("QUẢN LÝ MÓN ĂN");
        add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 6, 200, 30));

        jLabel10.setText("Số lượng tồn :");
        add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 140, 92, 30));

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

        txtSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
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
    }// </editor-fold>//GEN-END:initComponents
     private void setFileChooseUI(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.out.println("Loi chon hinh");
        }
    }
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnRefeshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefeshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRefeshActionPerformed

    private void cmd1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmd1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmd1ActionPerformed

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private button.Button btnRefesh;
    private button.Button btnSave;
    private javax.swing.JComboBox<String> cmbCategory;
    private button.Button cmd1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbExit;
    private javax.swing.JLabel lbExit1;
    private javax.swing.JLabel lbImage;
    private javax.swing.JTable tbDish;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtDishName;
    private javax.swing.JTextField txtInstock;
    private javax.swing.JTextField txtPrice;
    private swing.MyTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
