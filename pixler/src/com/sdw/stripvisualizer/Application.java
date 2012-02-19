/**
 * 
 */
package com.sdw.stripvisualizer;

import javax.swing.JFrame;

/**
 * @author Steffen David Weber
 *
 */
public class Application {

    public static void main(String[] args) {
        TablePaint window = new TablePaint();
        window.setSize(900, 700);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.pack();
    }


}
