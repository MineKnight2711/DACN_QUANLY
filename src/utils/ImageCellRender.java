/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import utils.spinner_progress.SpinnerProgress;

/**
 *
 * @author MINHNHAT
 */
public class ImageCellRender extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Icon icon) {
            setIcon(icon);
            setText(null); // Clear text
        } else if (value instanceof SpinnerProgress progressBar) { 
            progressBar.setStringPainted(true); 
            progressBar.setPreferredSize(new Dimension(50,50));
            return progressBar;
        } else {
            setIcon(null); // Clear icon
            setText((value == null) ? "" : value.toString());
        }
        
        setHorizontalAlignment(JLabel.CENTER);
        return this;
    }
}
