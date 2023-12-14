package forms;

import controller.CategoryController;
import controller.OrderController;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
import model.Category;
import model.OrderDTO;
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
    private final OrderController orderController;
    private List<OrderDTO> orders;
    private OrderDTO selectedOrder;
    private JPopupMenu menu;
    private PanelSearch search;
    public FormQLDonHang() {
        initComponents();
      
        orderController=new OrderController();
        createTableLastColumnCellEvent();
        getAllOrders();
        
        
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
         for (int i = 0; i <= 9; i++) {
            tbOrder.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    return super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
                }
            });
        }
    }
    private void refesh(){
        txtOrderId.setText("");
        txtAccountID.setText("");
        orders=null;
        getAllOrders();
    }
     private void getAllOrders(){
        List<OrderDTO> categoriesResult=orderController.getAllOrders();
        if(categoriesResult!=null){
            orders=categoriesResult;
            loadOrdersTable();   
        }
        else
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Không có danh mục!");
    }
//    private boolean deleteCategory(String categoryId){
//        String result=categoryController.deleteCategory(categoryId);
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
    private void loadOrdersTable() {
        if (!orders.isEmpty()) {
                DefaultTableModel model = (DefaultTableModel) tbOrder.getModel();
                model.setRowCount(0);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                for (OrderDTO details : orders) {
                  
                    String orderId = details.getOrder().getOrderID();
                    String status = details.getOrder().getStatus();
                    String deliveryInfo = details.getOrder().getDeliveryInfo();
                    String quantity = String.valueOf(details.getOrder().getQuantity());
                    String score = details.getOrder().getScore() != null ? String.valueOf(details.getOrder().getScore()) : "Chưa đánh giá";
                    String feedBack =  details.getOrder().getScore() != null ? details.getOrder().getFeedBack() : "Chưa đánh giá";
                    String dateFeedBack=details.getOrder().getDateFeedBack() != null ? sdf.format(details.getOrder().getDateFeedBack()): "Chưa đánh giá";
                    String voucherName=details.getOrder().getVoucher()!= null ? details.getOrder().getVoucher().getVoucherName() : "Không có";
                    String accountPhoneNumber=details.getOrder().getAccount().getPhoneNumber();
                    String dateOrder=sdf.format(details.getOrder().getOrderDate());
//                    Double discountAmountReceived=voucher.getDiscountAmount() != null ? voucher.getDiscountAmount() : 0.0;        
//                    String discountAmount =fmt.format(discountAmountReceived);
                    
                    model.addRow(new Object[]{orderId, status, deliveryInfo,quantity,score,feedBack,dateFeedBack,voucherName,accountPhoneNumber,dateOrder});
                }
                createTableRowClick();
                tbOrder.setRowSorter(new TableRowSorter<>(model));
                tbOrder.setRowHeight(50);
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
        tbOrder.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                
                int selectedRow = tbOrder.getSelectedRow();
                int modelRowIndex = tbOrder.convertRowIndexToModel(selectedRow);
                if(modelRowIndex < 0 || modelRowIndex == 10)
                    return;
                if (modelRowIndex >= 0) {
                    selectedOrder= orders.get(modelRowIndex);
                    txtOrderId.setText(selectedOrder.getOrder().getOrderID());
                    txtAccountID.setText(selectedOrder.getOrder().getAccount().getFullName());
                    txtVoucherId.setText(selectedOrder.getOrder().getVoucher()!= null ? selectedOrder.getOrder().getVoucher().getVoucherID(): "Không có");
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
        tbOrder = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtOrderId = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lbExit1 = new javax.swing.JLabel();
        btnXemChiTiet = new utils.Button();
        btnRefesh = new utils.Button();
        jLabel11 = new javax.swing.JLabel();
        btnUpdate = new utils.Button();
        txtAccountID = new javax.swing.JTextField();
        txtSearch = new javax.swing.JTextField();
        cmbOrderStatus = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        txtVoucherId = new javax.swing.JTextField();

        dateStart.setForeground(new java.awt.Color(255, 102, 51));

        setPreferredSize(new java.awt.Dimension(1366, 768));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbOrder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã đơn hàng", "Trạng thái", "Thông tin giao hàng", "Số lượng sản phẩm", "Điểm đánh giá", "Đánh giá", "Ngày đánh giá", "Mã ưu đãi", "SDT khách hàng", "Ngày đặt"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbOrder.setRowHeight(40);
        tbOrder.setRowSelectionAllowed(false);
        jScrollPane2.setViewportView(tbOrder);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 1080, 440));

        jLabel1.setText("Mã đơn hàng:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, -1, -1));

        txtOrderId.setEditable(false);
        add(txtOrderId, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 90, 200, 30));

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
        add(btnXemChiTiet, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 170, 150, 45));

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
        add(btnRefesh, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 170, 150, 45));

        jLabel11.setText("Khách hàng:");
        add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 90, -1, 30));

        btnUpdate.setBackground(new java.awt.Color(255, 102, 51));
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Cập nhật");
        add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 170, 150, 45));

        txtAccountID.setEditable(false);
        add(txtAccountID, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 90, 200, 30));
        add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 10, 230, -1));

        cmbOrderStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Đang thực hiện", "Đã hoàn tất" }));
        add(cmbOrderStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 360, 40));

        jLabel12.setText("Mã ưu đãi:");
        add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 90, -1, 30));

        txtVoucherId.setEditable(false);
        add(txtVoucherId, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 90, 200, 30));
    }// </editor-fold>//GEN-END:initComponents

    private void lbExit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbExit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_lbExit1MouseClicked

    private void btnXemChiTietActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXemChiTietActionPerformed
//        String response = categoryController.createCategory(txtIDVoucher.getText(),choosenFile);
//        if(response.equals("Success")){
//            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thêm danh mục thành công!");
//            refesh();   
//        }
//        else{
//             Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Thêm danh mục thất bại!");
//        }
    }//GEN-LAST:event_btnXemChiTietActionPerformed

    private void btnRefeshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefeshActionPerformed
        refesh();
    }//GEN-LAST:event_btnRefeshActionPerformed
    
     

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private utils.Button btnRefesh;
    private utils.Button btnUpdate;
    private utils.Button btnXemChiTiet;
    private javax.swing.JComboBox<String> cmbOrderStatus;
    private com.raven.datechooser.DateChooser dateExpired;
    private com.raven.datechooser.DateChooser dateStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbExit1;
    private javax.swing.JTable tbOrder;
    private javax.swing.JTextField txtAccountID;
    private javax.swing.JTextField txtOrderId;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtVoucherId;
    // End of variables declaration//GEN-END:variables
}
