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
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
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
 * @author MINHNHAT
 */
public class FormDish extends javax.swing.JPanel {
    private File choosenFile;
    private List<Category> listCategory;
    private Dish selectedDish;
    private List<Dish> listDish;
    private final CategoryController categoryController;
    private final DishController dishController;
    private boolean isEditingEnabled = false;
    private boolean isAddEnabled = false;
    private JPopupMenu menu;
    private PanelSearch search;
    private NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    public FormDish() {
        initComponents();
        categoryController=new CategoryController();
        dishController=new DishController();
        loadComboBox();
        createTableLastColumnCellEvent();
        getAllDish();
        
        circleProgress.setVisible(false);
        createSearchTextField();
        
        
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
                selectedDish=listDish.stream().filter((Dish dish) -> dish.getDishName().equals(data.getText())).findFirst().orElse(null);
                if(selectedDish!=null)
                {
                    isEditingEnabled=true;
                    enableEdit();
                    listDish.removeIf(dish -> !dish.getDishName().equals(data.getText()));
                    loadDishTable();
                    fillTextField(selectedDish);

                }
                
            }

            @Override
            public void itemRemove(Component com, DataSearch data) {
             
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
    private void fillTextField(Dish dish){
        txtDishName.setText(dish.getDishName());
        txtInstock.setText(String.valueOf(dish.getInStock()));
        cmbCategory.setSelectedItem(dish.getCategory());
        DecimalFormat decimalFormat = new DecimalFormat("0"); 
        txtPrice.setText(decimalFormat.format(dish.getPrice()));
        txtDescription.setText(dish.getDescription());
        ImageLoader loader = new ImageLoader(dish.getImageUrl(), lbImage.getWidth(), lbImage.getHeight());
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
        for (int i = 0; i <= 4; i++) {
            tbDish.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    return super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
                }
            });
        }
    }
    private void refesh(){
        isEditingEnabled=false;
        isAddEnabled=false;
        enableEdit();
        
        lbImage.setIcon(null);
        choosenFile=null;
        listDish=null;
        getAllDish();
    }
    private void clearText()
    {
        txtDishName.setText("");
        txtDescription.setText("");
        txtInstock.setText("");
        txtPrice.setText("");
        txtSearch.setText("");
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
                    
                    String formatedPrice=fmt.format(price);
                    SpinnerProgress progressBar=new SpinnerProgress();
                    progressBar.setPreferredSize(new Dimension(50,50)); 
                    
                    model.addRow(new Object[]{dishId, dishName,description,formatedPrice,inStock, progressBar});
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
                                        tbDish.setRowHeight(100);
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
        for (Dish d : listDish) {
            if (d.getDishName().toLowerCase().contains(search)) 
            {
                list.add(0, new DataSearch(d.getDishName()));
                if (list.size() == limitData) 
                {
                    break;
                }
            }
        }
        return list;
    }
    
     private void createTableRowClick(){
        tbDish.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                
                int selectedRow = tbDish.getSelectedRow();
                int modelRowIndex = tbDish.convertRowIndexToModel(selectedRow);
                if(modelRowIndex < 0 || modelRowIndex == 6)
                    return;
                if (modelRowIndex >= 0) {
                    isAddEnabled=false;
                    isEditingEnabled=false;
                    enableEdit();
                    try {
                        selectedDish= listDish.get(modelRowIndex);
                        txtDishName.setText(selectedDish.getDishName());
                        txtInstock.setText(String.valueOf(selectedDish.getInStock()));
                        cmbCategory.setSelectedItem(selectedDish.getCategory());
                        DecimalFormat decimalFormat = new DecimalFormat("0"); 
                        txtPrice.setText(decimalFormat.format(selectedDish.getPrice()));
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

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jMenuBar3 = new javax.swing.JMenuBar();
        jMenu6 = new javax.swing.JMenu();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuBar4 = new javax.swing.JMenuBar();
        jMenu8 = new javax.swing.JMenu();
        jMenu9 = new javax.swing.JMenu();
        jMenuBar5 = new javax.swing.JMenuBar();
        jMenu10 = new javax.swing.JMenu();
        jMenu11 = new javax.swing.JMenu();
        jMenuBar6 = new javax.swing.JMenuBar();
        jMenu12 = new javax.swing.JMenu();
        jMenu13 = new javax.swing.JMenu();
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
        btnRefesh = new utils.Button();
        btnChooseImage = new utils.Button();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbExit1 = new javax.swing.JLabel();
        btnSave = new utils.Button();
        circleProgress = new utils.spinner_progress.SpinnerProgress();
        btnUpdate = new utils.Button();
        btnAdd = new utils.Button();
        txtSearch = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        jMenu5.setText("jMenu5");

        jMenu6.setText("File");
        jMenuBar3.add(jMenu6);

        jMenu7.setText("Edit");
        jMenuBar3.add(jMenu7);

        jMenuItem1.setText("jMenuItem1");

        jMenu8.setText("File");
        jMenuBar4.add(jMenu8);

        jMenu9.setText("Edit");
        jMenuBar4.add(jMenu9);

        jMenu10.setText("File");
        jMenuBar5.add(jMenu10);

        jMenu11.setText("Edit");
        jMenuBar5.add(jMenu11);

        jMenu12.setText("File");
        jMenuBar6.add(jMenu12);

        jMenu13.setText("Edit");
        jMenuBar6.add(jMenu13);

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
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 210, 92, 30));

        txtInstock.setEditable(false);
        add(txtInstock, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 140, 220, 30));

        jLabel5.setText("Mô tả món:");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, -1, 30));

        jLabel6.setText("Giá:");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 80, 60, 24));

        txtPrice.setEditable(false);
        add(txtPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 80, 220, 30));

        txtDescription.setEditable(false);
        txtDescription.setColumns(20);
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        jScrollPane2.setViewportView(txtDescription);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, 260, 120));

        lbImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        lbImage.setPreferredSize(new java.awt.Dimension(200, 200));
        add(lbImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 190, 220, 190));

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
        add(btnRefesh, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 180, 140, -1));

        btnChooseImage.setBackground(new java.awt.Color(253, 83, 83));
        btnChooseImage.setForeground(new java.awt.Color(245, 245, 245));
        btnChooseImage.setText("Chọn ảnh");
        btnChooseImage.setEnabled(false);
        btnChooseImage.setRippleColor(new java.awt.Color(255, 255, 255));
        btnChooseImage.setShadowColor(new java.awt.Color(253, 83, 83));
        btnChooseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseImageActionPerformed(evt);
            }
        });
        add(btnChooseImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 130, 140, -1));

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
        add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 140, 92, 30));

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
        add(lbExit1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 10, 20, -1));

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
        add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 80, 140, -1));

        circleProgress.setForeground(new java.awt.Color(255, 153, 51));
        circleProgress.setIndeterminate(true);
        add(circleProgress, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 80, 40, 40));

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
        add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 80, 140, -1));

        btnAdd.setBackground(new java.awt.Color(30, 180, 114));
        btnAdd.setForeground(new java.awt.Color(245, 245, 245));
        btnAdd.setText("+ Thêm món");
        btnAdd.setRippleColor(new java.awt.Color(255, 255, 255));
        btnAdd.setShadowColor(new java.awt.Color(30, 180, 114));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 260, 140, -1));

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
        add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 20, 320, 30));

        jLabel11.setText("Tìm kiếm:");
        add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 30, 60, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void enableEdit()
    {
        if(isAddEnabled)
        {
            lbImage.setIcon(null);
            btnUpdate.setVisible(false);
            btnSave.setVisible(true);
            btnSave.setEnabled(true);
            btnChooseImage.setEnabled(true);
            txtDescription.setEditable(true);
            txtDishName.setEditable(true);
            txtInstock.setEditable(true);
            txtPrice.setEditable(true);
            clearText();
        }
        else if(isEditingEnabled){
            btnUpdate.setVisible(true);
            btnUpdate.setEnabled(true);
            btnSave.setVisible(false);
            btnChooseImage.setEnabled(true);
            txtDescription.setEditable(true);
            txtDishName.setEditable(true);
            txtInstock.setEditable(true);
            txtPrice.setEditable(true);
        }
        else{
            btnSave.setEnabled(false);
            btnSave.setVisible(false);
            btnUpdate.setVisible(false);
            btnUpdate.setEnabled(false);
            btnChooseImage.setEnabled(false);
            txtDescription.setEditable(false);
            txtDishName.setEditable(false);
            txtInstock.setEditable(false);
            txtPrice.setEditable(false);
        }
    }
    private void btnRefeshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefeshActionPerformed
        refesh();
    }//GEN-LAST:event_btnRefeshActionPerformed
    
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

    private void lbExit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbExit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_lbExit1MouseClicked
    private void createNewDish(){
        Dish newDish=new Dish();
        newDish.setDishName(txtDishName.getText());
        newDish.setDescription(txtDescription.getText());
        newDish.setInStock(Integer.parseInt(txtInstock.getText()));
        newDish.setPrice(Double.parseDouble(txtPrice.getText()));
        Category selectedCategory=(Category) cmbCategory.getSelectedItem();
        newDish.setCategory(selectedCategory);
        if(choosenFile!=null)
        {
            String result=dishController.createNewDish(choosenFile, newDish);
            if(result.equals("Success"))
            {
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thêm món thành công!");
                refesh();
                circleProgress.setVisible(false);
            }
            else
            {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, result);
                circleProgress.setVisible(false);
            }
        }
        else
        {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Bạn chưa chọn ảnh!");
            circleProgress.setVisible(false);
        }
    }
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        circleProgress.setVisible(true);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                createNewDish();
                return null;
            }
            @Override
            protected void done() {
                circleProgress.setVisible(false);
            }
        };
        worker.execute();
        
    }//GEN-LAST:event_btnSaveActionPerformed
    private void updateDish()
    {
        if(selectedDish!=null)
        {
            Dish updatedDish=selectedDish;
            updatedDish.setDishName(txtDishName.getText());
            updatedDish.setDescription(txtDescription.getText());
            updatedDish.setInStock(Integer.parseInt(txtInstock.getText()));
            updatedDish.setPrice(Double.parseDouble(txtPrice.getText()));
            Category selectedCategory=(Category) cmbCategory.getSelectedItem();
            updatedDish.setCategory(selectedCategory);
            
            String result=dishController.updateDish(choosenFile,selectedDish);
            
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
                updateDish();
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private utils.Button btnAdd;
    private utils.Button btnChooseImage;
    private utils.Button btnRefesh;
    private utils.Button btnSave;
    private utils.Button btnUpdate;
    private utils.spinner_progress.SpinnerProgress circleProgress;
    private javax.swing.JComboBox<String> cmbCategory;
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
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu12;
    private javax.swing.JMenu jMenu13;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuBar jMenuBar3;
    private javax.swing.JMenuBar jMenuBar4;
    private javax.swing.JMenuBar jMenuBar5;
    private javax.swing.JMenuBar jMenuBar6;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbExit1;
    private javax.swing.JLabel lbImage;
    private javax.swing.JTable tbDish;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtDishName;
    private javax.swing.JTextField txtInstock;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
