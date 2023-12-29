package forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.StyleContext;
import model.AccountSession;


/**
 *
 * @author Raven
 */
public class FormDashboard extends javax.swing.JPanel {
    private AccountSession currentAccount;
    private Image backgroundImage;

    public FormDashboard(AccountSession account) {
        setLayout(new BorderLayout());
        this.currentAccount=account;
        // Load the background image
        backgroundImage = new ImageIcon("src\\image\\background_2.jpg").getImage();

        // Create a panel for the background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        add(backgroundPanel, BorderLayout.CENTER);

        addLabel(backgroundPanel,currentAccount.getLoggedInAccount().getFullName());
        addCloseButton(backgroundPanel);
    }

    private void addLabel(JPanel backgroundPanel,String accountName) {
        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
        labelsPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Chào mừng trở lại, "+accountName+"!");
        JLabel wishLabel = new JLabel("Chúc một ngày làm việc tốt lành.");

        // Set font styles if needed
        Font segoeScriptFont = createFont("Segoe Script",Font.PLAIN,24);


        welcomeLabel.setFont(segoeScriptFont);
        wishLabel.setFont(segoeScriptFont);
        welcomeLabel.setForeground(Color.BLACK);
        wishLabel.setForeground(Color.BLACK);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT-Float.parseFloat("0.4"));
        wishLabel.setAlignmentX(Component.CENTER_ALIGNMENT-Float.parseFloat("0.2"));
        // Add welcomeLabel to the NORTH of labelsPanel
        labelsPanel.add(welcomeLabel, BorderLayout.NORTH);

        labelsPanel.add(welcomeLabel);
        labelsPanel.add(wishLabel);

        // Add labelsPanel to backgroundPanel at the CENTER
        backgroundPanel.add(labelsPanel, BorderLayout.CENTER);
    }
    
    private void addCloseButton(JPanel backgroundPanel) {
        JButton closeButton = new JButton("X");
        closeButton.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonWrapper.setOpaque(false); // Make the panel transparent
        buttonWrapper.add(closeButton);

        // Add the button panel to the main panel at the NORTH position
        backgroundPanel.add(buttonWrapper, BorderLayout.NORTH);
    }
    private Font createFont( String family, int style,  int size) {
        return new NonUIResourceFont(StyleContext.getDefaultStyleContext().getFont(family, style, size));
    }

    class NonUIResourceFont extends Font {

        public NonUIResourceFont(final Font font) {
            super(font);
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(1366, 768));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1366, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 768, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
