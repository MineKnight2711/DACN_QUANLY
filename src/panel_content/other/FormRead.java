package panel_content.other;


/**
 *
 * @author Raven
 */
public class FormRead extends javax.swing.JPanel {

    public FormRead() {
        initComponents();
//        this.putClientProperty(FlatClientProperties.STYLE, ""
//                + "font:$h1.font");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbImage = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbDanhMuc = new javax.swing.JTable();
        btnRefresh = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtCategoryName = new javax.swing.JTextField();
        txtUrl = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnLuu = new javax.swing.JButton();
        btnChooseImage = new javax.swing.JButton();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbImage.setBackground(new java.awt.Color(255, 255, 255));
        lbImage.setOpaque(true);
        add(lbImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 20, 200, 220));

        tbDanhMuc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã danh mục", "Tên danh mục", "Hình"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tbDanhMuc);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 330, 760, 380));

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
    }// </editor-fold>//GEN-END:initComponents

    private void txtUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUrlActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUrlActionPerformed

    private void btnChooseImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseImageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnChooseImageActionPerformed

    private void btnLuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLuuActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRefreshActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseImage;
    private javax.swing.JButton btnLuu;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbImage;
    private javax.swing.JTable tbDanhMuc;
    private javax.swing.JTextField txtCategoryName;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables
}
