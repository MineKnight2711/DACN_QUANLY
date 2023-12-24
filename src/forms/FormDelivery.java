/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forms;


import controller.DeliveryController;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import model.Account;
import model.DeliveryResponse;
import model.Orders;
import model.ResponseModel;
import raven.toast.Notifications;
import utils.ImageCellRender;
import utils.ImageLoader;
import utils.spinner_progress.SpinnerProgress;
import utils.table.TableActionCellEditor;
import utils.table.TableActionCellRender;
import utils.table.TableActionEvent;

/**
 *
 * @author WitherDragon
 */
public class FormDelivery extends javax.swing.JFrame {
    private static Orders currentOrder;
    private DeliveryController deliveryController;
    private List<Account> delivers;
    private int selectedRow=0;
    public FormDelivery(Orders order) {
        initComponents();
        FormDelivery.currentOrder=order;
        deliveryController=new DeliveryController();
        getAllDeliver();
        createTableLastColumnCellEvent();
    }
    private void getAllDeliver()
    {
        List<Account> deliversResult=deliveryController.getAllDeliver();
        if(!deliversResult.isEmpty()){
            delivers=deliversResult;
            loadDeliverTable();   
        }
        else
        {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Không có voucher!"); 
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tbDeliver = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tbDeliver.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Ảnh nhân viên", "Tên nhân viên", "Đang giao", "Thao tác"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbDeliver.setRowSelectionAllowed(false);
        jScrollPane1.setViewportView(tbDeliver);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    private void loadDeliverTable() {
        if (!delivers.isEmpty()) {
                DefaultTableModel model = (DefaultTableModel) tbDeliver.getModel();
                model.setRowCount(0);
                int totalAccounts = delivers.size();

                for (Account account : delivers) {
                    int deliverOrderCount=0;
                    List<DeliveryResponse> deliveryResponse=deliveryController.getAccountDeliveryDetails(account.getAccountID());
                    if(deliveryResponse!=null)
                    {
                        deliverOrderCount=deliveryResponse.size();
                    }
                    String deliverName = account.getFullName();
                    SpinnerProgress progressBar=new SpinnerProgress();
                    progressBar.setPreferredSize(new Dimension(50,50)); 
                    model.addRow(new Object[]{progressBar,deliverName, deliverOrderCount});
                }
//                createTableRowClick();
                AtomicInteger count = new AtomicInteger(0);
                
                // Load images asynchronously
                for (int i = 0; i < totalAccounts; i++) {
                    Account acc = delivers.get(i);
                    String imageUrl = acc.getImageUrl();
                    final int currentIndex = i;
                    
                    ImageLoader imageLoader = new ImageLoader(imageUrl, 150, 150);
                    
                    imageLoader.addPropertyChangeListener(evt -> {
                        int progress = imageLoader.getProgress();

                        SpinnerProgress progressBar = (SpinnerProgress)model.getValueAt(currentIndex, 0);
                        progressBar.setValue(progress);
                        
                        
                        if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE == evt.getNewValue()) {
                            try {
                                ImageIcon scaledImageIcon = imageLoader.get();
                                 
                                // Update the UI on the EDT
                                SwingUtilities.invokeLater(() -> {
                                    model.setValueAt(scaledImageIcon, currentIndex, 0);
                                    
                                    int currentCount = count.incrementAndGet();
                                    if (currentCount == totalAccounts) {
                                        // All images are loaded, do additional UI setup here
                                        TableColumn imageColumn = tbDeliver.getColumnModel().getColumn(0);
                                        imageColumn.setCellRenderer(new ImageCellRender());
                                        tbDeliver.setRowHeight(150);
                                        tbDeliver.revalidate();
                                        tbDeliver.repaint();
                                        
                                    }
                                });
                                
                            } catch (InterruptedException | ExecutionException e) {
                                System.out.println("Error when loading image: " + e.getMessage());
                            }
                        }
                    });
                    
                    imageLoader.execute();
                }
                TableColumn imageColumn = tbDeliver.getColumnModel().getColumn(0);
                imageColumn.setCellRenderer(new ImageCellRender());
                tbDeliver.setRowHeight(50);
        } else {
            JOptionPane.showMessageDialog(this, "Không có nhân viên nào!", "Lỗi", 0);
        }
    }
    private void asignToDeliver(String accountId)
    {
        ResponseModel response=deliveryController.asignToDeliver(currentOrder.getOrderID(),accountId);
        if(response.getMessage().equals("Success"))
        {
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã giao đơn hàng cho nhân viên!");
            getAllDeliver();
            
            this.dispose();
        }
        else
        {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Có lỗi xảy ra!\nChi tiết"+response.getData());
        }
    }
    private void cancelDeliver()
    {
        
    }
    private void createTableLastColumnCellEvent(){
         TableActionEvent event = new TableActionEvent() {
            @Override
            public void onEdit(int row) {
                Account selectedDeliver= delivers.get(row);
                
                int result=JOptionPane.showConfirmDialog(FormDelivery.this, "Bạn có muốn giao đơn hàng cho "+selectedDeliver.getFullName());
                if(result==JOptionPane.YES_OPTION)
                {
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {

                            asignToDeliver(selectedDeliver.getAccountID());
                            return null;
                        }
                        @Override
                        protected void done() {
                            
                        }
                    };
                    worker.execute();
                }
            }

            @Override
            public void onDelete(int row) {
                if (tbDeliver.isEditing()) {
                    tbDeliver.getCellEditor().stopCellEditing();
                }
                DefaultTableModel model = (DefaultTableModel) tbDeliver.getModel();
                String accountId = (String) model.getValueAt(row, 0);
                cancelDeliver();
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
        tbDeliver.getColumnModel().getColumn(3).setCellRenderer(new TableActionCellRender());
        tbDeliver.getColumnModel().getColumn(3).setCellEditor(new TableActionCellEditor(event));
        for (int i = 1; i <= 2; i++) {
            tbDeliver.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    return super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
                }
            });
        }
    }
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormDelivery(currentOrder).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbDeliver;
    // End of variables declaration//GEN-END:variables
}
