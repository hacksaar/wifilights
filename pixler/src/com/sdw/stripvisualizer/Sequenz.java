/**
 * 
 */
package com.sdw.stripvisualizer;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;

/**
 * @author Steffen David Weber
 */
public class Sequenz implements Serializable {

    private static final long serialVersionUID = 1134723061929614866L;
    private List<List<Color>> colorArray;
    private int duration = 0;

    public Sequenz() {

    }

    public void setColorArray(List<List<Color>> colorArray) {
        this.colorArray = colorArray;
    }

    public Color getColorAt(int row, int column) {
        return colorArray.get(column).get(row);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
