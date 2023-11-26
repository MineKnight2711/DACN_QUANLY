package forms;

import com.formdev.flatlaf.FlatClientProperties;
import raven.toast.Notifications;

/**
 *
 * @author Raven
 */
public class FormDashboard extends javax.swing.JPanel {

    public FormDashboard() {
        initComponents();
        
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbExit = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1366, 768));

        lbExit.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lbExit.setForeground(new java.awt.Color(153, 153, 153));
        lbExit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbExit.setText("X");
        lbExit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbExitMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 1061, Short.MAX_VALUE)
                .addComponent(lbExit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lbExit, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 729, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void lbExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbExitMouseClicked
        System.exit(0);
    }//GEN-LAST:event_lbExitMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbExit;
    // End of variables declaration//GEN-END:variables
}
