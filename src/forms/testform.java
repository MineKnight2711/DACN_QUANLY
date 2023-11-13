/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package forms;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author WitherDragon
 */
public class testform extends JFrame {


  public testform(){
    super( "AnimatedIconTable Example" );

    final Object[][] data =  new Object[][]{
      {new ImageIcon("/icons/loading.gif"),new ImageIcon("/icons/loading.gif")},
      {new ImageIcon("/icons/loading.gif"),new ImageIcon("/icons/loading.gif")}};
    final Object[] column = new Object[]{"Boy and Girl","Dog and Cat"};

    AbstractTableModel model = new AbstractTableModel() {
      @Override
      public int getColumnCount() { return column.length; }
      @Override
      public int getRowCount()    { return data.length; }
      @Override
      public String getColumnName(int col) {
       return (String)column[col]; 
      }
      @Override
      public Object getValueAt(int row, int col) { 
        return data[row][col]; 
      }
      @Override
      public Class getColumnClass(int col) {
        return ImageIcon.class;
      }
    };

    JTable table = new JTable( model );
    table.setRowHeight(50);
    setImageObserver(table);
    JScrollPane pane = new JScrollPane(table);
    getContentPane().add(pane);
  }

  private void setImageObserver(JTable table) {
    TableModel model = table.getModel();
    int colCount = model.getColumnCount();
    int rowCount = model.getRowCount();
    for (int col=0;col<colCount;col++) {
      if (ImageIcon.class == model.getColumnClass(col)) {
        for (int row=0;row<rowCount;row++) {
          ImageIcon icon = (ImageIcon)model.getValueAt(row,col);
          if (icon != null) {
            icon.setImageObserver((ImageObserver) new CellImageObserver(table, row, col));
          }
        }
      }
    }
  }

  class CellImageObserver implements ImageObserver {
    JTable table;
    int row;
    int col;

    CellImageObserver(JTable table,int row, int col) {
      this.table = table;
      this.row   = row;
      this.col   = col;
    }

    public boolean imageUpdate(Image img, int flags,
                   int x, int y, int w, int h) {
      if ((flags & (FRAMEBITS|ALLBITS)) != 0) {
        Rectangle rect = table.getCellRect(row,col,false);
        table.repaint(rect);
      }
      return (flags & (ALLBITS|ABORT)) == 0;
    }
  }

  public static void main(String[] args) {
    testform frame = new testform();
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    frame.setSize( 300, 150 );
    frame.setVisible(true);
  }
}
