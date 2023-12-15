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
        lbExit1 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1366, 768));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        add(lbExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(1326, 0, 40, 29));

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
        add(lbExit1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 10, 30, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void lbExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbExitMouseClicked
        System.exit(0);
    }//GEN-LAST:event_lbExitMouseClicked

    private void lbExit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbExit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_lbExit1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbExit;
    private javax.swing.JLabel lbExit1;
    // End of variables declaration//GEN-END:variables
}
