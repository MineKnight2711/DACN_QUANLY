package panel_content;

import main_frame.Application;

/**
 *
 * @author Raven
 */
public class LoginForm extends javax.swing.JPanel {

    public LoginForm() {
        initComponents();
        
   
//        init();
    }
    

//    private void init() {
//        setLayout(new LoginFormLayout());
//        setLayout(new LoginLayout());
//        lbTitle.putClientProperty(FlatClientProperties.STYLE, ""
//                + "font:$h1.font");
//        putClientProperty(FlatClientProperties.STYLE, ""
//                + "background:$Login.background;"
//                + "arc:20;"
//                + "border:30,40,50,30");
//
//        txtPass.putClientProperty(FlatClientProperties.STYLE, ""
//                + "showRevealButton:true;"
//                + "showCapsLock:true");
//        jButton1.putClientProperty(FlatClientProperties.STYLE, ""
//                + "borderWidth:0;"
//                + "focusWidth:0");
//        txtUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "User Name");
//        txtPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
//    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnLoginInfo = new javax.swing.JPanel();
        lbTitle = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtPass = new javax.swing.JPasswordField();
        disable = new javax.swing.JLabel();
        show = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        pnBannel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        lbLoginIcon = new javax.swing.JLabel();
        lbLoginBanner = new javax.swing.JLabel();
        lbExit = new javax.swing.JLabel();
        lbLoginBackgrounb = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(1366, 768));
        setPreferredSize(new java.awt.Dimension(914, 758));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnLoginInfo.setBackground(new java.awt.Color(255, 255, 255));
        pnLoginInfo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbTitle.setFont(new java.awt.Font("Segoe UI", 0, 32)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(102, 102, 102));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTitle.setText("USER LOGIN");
        pnLoginInfo.add(lbTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 420, 41));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Hello! Let's get started");
        pnLoginInfo.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 94, 420, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("Username");
        pnLoginInfo.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(34, 123, 341, -1));

        txtUser.setFont(txtUser.getFont().deriveFont(txtUser.getFont().getSize()+2f));
        txtUser.setForeground(new java.awt.Color(51, 51, 51));
        txtUser.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 204, 204)));
        pnLoginInfo.add(txtUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 360, 30));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnLoginInfo.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(335, 147, 40, 39));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setText("Password");
        pnLoginInfo.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(34, 192, 341, -1));

        txtPass.setFont(txtPass.getFont().deriveFont(txtPass.getFont().getSize()+2f));
        txtPass.setForeground(new java.awt.Color(102, 102, 102));
        txtPass.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 204, 204)));
        txtPass.setCaretColor(new java.awt.Color(255, 255, 255));
        pnLoginInfo.add(txtPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 360, 30));

        disable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        disable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        disable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                disableMouseClicked(evt);
            }
        });
        pnLoginInfo.add(disable, new org.netbeans.lib.awtextra.AbsoluteConstraints(335, 216, 40, 40));

        show.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        show.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        show.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showMouseClicked(evt);
            }
        });
        pnLoginInfo.add(show, new org.netbeans.lib.awtextra.AbsoluteConstraints(335, 216, 40, 40));

        jCheckBox1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jCheckBox1.setText("Remember Password");
        pnLoginInfo.add(jCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(34, 261, -1, -1));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(102, 102, 102));
        jButton1.setText("LOGIN");
        jButton1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        pnLoginInfo.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 310, 341, 40));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Forgot Password?");
        jLabel11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnLoginInfo.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 260, 121, 27));

        add(pnLoginInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 140, -1, 440));

        pnBannel.setBackground(new java.awt.Color(255, 153, 51));
        pnBannel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 153, 0));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("WELCOME TO DATFOOD MANAGER");
        pnBannel.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 360, 500, -1));

        lbLoginIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/bg-logo.png"))); // NOI18N
        pnBannel.add(lbLoginIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 410, 290));

        lbLoginBanner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/orgrane.png"))); // NOI18N
        lbLoginBanner.setPreferredSize(new java.awt.Dimension(1366, 768));
        pnBannel.add(lbLoginBanner, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 440));

        add(pnBannel, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 140, -1, 440));

        lbExit.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lbExit.setForeground(new java.awt.Color(102, 102, 102));
        lbExit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbExit.setText("X");
        lbExit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbExitMouseClicked(evt);
            }
        });
        add(lbExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(1310, 10, 40, 29));

        lbLoginBackgrounb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/bg.jpg"))); // NOI18N
        lbLoginBackgrounb.setMaximumSize(new java.awt.Dimension(1366, 768));
        lbLoginBackgrounb.setMinimumSize(new java.awt.Dimension(1366, 768));
        lbLoginBackgrounb.setName(""); // NOI18N
        lbLoginBackgrounb.setPreferredSize(new java.awt.Dimension(1366, 768));
        add(lbLoginBackgrounb, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1366, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void disableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_disableMouseClicked
        txtPass.setEchoChar((char)0);
        disable.setVisible(false);
        disable.setEnabled(false);
        show.setEnabled(true);
        show.setEnabled(true);
    }//GEN-LAST:event_disableMouseClicked

    private void showMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showMouseClicked
        txtPass.setEchoChar((char)8226);
        disable.setVisible(true);
        disable.setEnabled(true);
        show.setEnabled(false);
        show.setEnabled(false);
    }//GEN-LAST:event_showMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Application.login();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void lbExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbExitMouseClicked
        System.exit(0);
    }//GEN-LAST:event_lbExitMouseClicked

//    private class LoginFormLayout implements LayoutManager {
//
//        @Override
//        public void addLayoutComponent(String name, Component comp) {
//        }
//
//        @Override
//        public void removeLayoutComponent(Component comp) {
//        }
//
//        @Override
//        public Dimension preferredLayoutSize(Container parent) {
//            synchronized (parent.getTreeLock()) {
//                return new Dimension(0, 0);
//            }
//        }
//
//        @Override
//        public Dimension minimumLayoutSize(Container parent) {
//            synchronized (parent.getTreeLock()) {
//                return new Dimension(0, 0);
//            }
//        }
//
//        @Override
//        public void layoutContainer(Container parent) {
//            synchronized (parent.getTreeLock()) {
//                int width = parent.getWidth();
//                int height = parent.getHeight();
//                int loginWidth = UIScale.scale(320);
//                int loginHeight = getPreferredSize().height;
//                int x = (width - loginWidth) / 2;
//                int y = (height - loginHeight) / 2;
//                setBounds(x, y, loginWidth, loginHeight);
//            }
//        }
//    }
//
//    private class LoginLayout implements LayoutManager {
//
//        private final int titleGap = 10;
//        private final int textGap = 10;
//        private final int labelGap = 5;
//        private final int buttonGap = 50;
//
//        @Override
//        public void addLayoutComponent(String name, Component comp) {
//        }
//
//        @Override
//        public void removeLayoutComponent(Component comp) {
//        }
//
//        @Override
//        public Dimension preferredLayoutSize(Container parent) {
//            synchronized (parent.getTreeLock()) {
//                Insets insets = parent.getInsets();
//                int height = insets.top + insets.bottom;
//
//                height += lbTitle.getPreferredSize().height;
//                height += UIScale.scale(titleGap);
//                height += jLabel5.getPreferredSize().height;
//                height += UIScale.scale(labelGap);
//                height += txtUser.getPreferredSize().height;
//                height += UIScale.scale(textGap);
//
//                height += jLabel8.getPreferredSize().height;
//                height += UIScale.scale(labelGap);
//                height += txtPass.getPreferredSize().height;
//                height += UIScale.scale(buttonGap);
//                height += jButton1.getPreferredSize().height;
//                return new Dimension(0, height);
//            }
//        }
//
//        @Override
//        public Dimension minimumLayoutSize(Container parent) {
//            synchronized (parent.getTreeLock()) {
//                return new Dimension(0, 0);
//            }
//        }
//
//        @Override
//        public void layoutContainer(Container parent) {
//            synchronized (parent.getTreeLock()) {
//                Insets insets = parent.getInsets();
//                int x = insets.left;
//                int y = insets.top;
//                int width = parent.getWidth() - (insets.left + insets.right);
//
//                lbTitle.setBounds(x, y, width, lbTitle.getPreferredSize().height);
//                y += lbTitle.getPreferredSize().height + UIScale.scale(titleGap);
//
//                jLabel5.setBounds(x, y, width, jLabel5.getPreferredSize().height);
//                y += jLabel5.getPreferredSize().height + UIScale.scale(labelGap);
//                txtUser.setBounds(x, y, width, txtUser.getPreferredSize().height);
//                y += txtUser.getPreferredSize().height + UIScale.scale(textGap);
//
//                jLabel8.setBounds(x, y, width, jLabel8.getPreferredSize().height);
//                y += jLabel8.getPreferredSize().height + UIScale.scale(labelGap);
//                txtPass.setBounds(x, y, width, txtPass.getPreferredSize().height);
//                y += txtPass.getPreferredSize().height + UIScale.scale(buttonGap);
//
//                jButton1.setBounds(x, y, width, jButton1.getPreferredSize().height);
//            }
//        }
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel disable;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel lbExit;
    private javax.swing.JLabel lbLoginBackgrounb;
    private javax.swing.JLabel lbLoginBanner;
    private javax.swing.JLabel lbLoginIcon;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel pnBannel;
    private javax.swing.JPanel pnLoginInfo;
    private javax.swing.JLabel show;
    private javax.swing.JPasswordField txtPass;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}
