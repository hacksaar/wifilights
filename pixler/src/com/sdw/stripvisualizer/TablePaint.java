/**
 * 
 */
package com.sdw.stripvisualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventObject;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * @author Steffen David Weber
 */
public class TablePaint extends JFrame {

    private static final long serialVersionUID = -3003053959052488696L;
    private static final Color defaultColor = new Color(0,0,0);
    private static final int defaultColumnCount = 20;
    private List<List<Color>> colorArray;

    private final JScrollPane scrollPane = new JScrollPane();
    private final JLabel editorComponent = new JLabel();
    private final JColorChooser colorChooser;
    private JTable table;
    private JMenuBar menu;
    private ColorTableModel colorTableModel;
    private JMenuItem saveAsItem;
    private JMenuItem saveItem;
    private JMenuItem openButton;
    private JMenuItem plusItem;
    private JMenuItem minuItem;
    private JMenuItem sequenzItem;
    private JTextField durationField;
    private File currentFile;

    private Color getPaintColor() {
        return colorChooser.getSelectionModel().getSelectedColor();
    }

    public TablePaint() {
        colorChooser = new JColorChooser(defaultColor);
        scrollPane.setViewportView(createColorTable());
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(createMenu2(), BorderLayout.NORTH);
        this.add(colorChooser, BorderLayout.WEST);
    }

    private JTextField getDurationField() {
        if (durationField == null) {
            durationField = new JTextField("1000");
            durationField.setEditable(true);
            durationField.setEnabled(true);
            durationField.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) { }

                @Override
                public void keyReleased(KeyEvent e) { }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        getDurationFrame().setVisible(false);
                    }
                }
            });

        }
        return durationField;
    }

    private JMenuItem getSequnezerItem() {
        if (sequenzItem == null) {
            sequenzItem = new JMenuItem("Generate");
            sequenzItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println(createSequnez());
                }
            });
        }
        return sequenzItem;
    }

    private String createSequnez() {
        StringBuffer buffer = new StringBuffer();
        for (List<Color> row : colorArray) {
            for (Color color : row) {
                buffer.append(((Integer) color.getRed()).byteValue());
                buffer.append(((Integer) color.getGreen()).byteValue());
                buffer.append(((Integer) color.getBlue()).byteValue());
            }
        }
        return buffer.toString();
    }

    private JComponent createMenu2() {
        if (menu == null) {
            menu = new JMenuBar();
            menu.add(createFileMenu());
            menu.add(createArrayMenu());
            menu.add(createSequenzMenu());
        }
        return menu;
    }

    private JMenu createSequenzMenu() {
        JMenu seqMenu = new JMenu("Sequenz");
        seqMenu.add(getDurationItem());
        seqMenu.add(getSequnezerItem());
        return seqMenu;
    }

    private JMenuItem durationItem;

    private JMenuItem getDurationItem() {
        if (durationItem == null) {
            durationItem = new JMenuItem("Set duration");

            durationItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    getDurationFrame().setLocation(
                            ((Double) getRootPane().getMousePosition().getX()).intValue(),
                            ((Double) getRootPane().getMousePosition().getY()).intValue());
                    getDurationFrame().pack();
                    getDurationFrame().setVisible(true);

                }

            });

        }
        return durationItem;
    }

    JFrame durationFrame;

    JFrame getDurationFrame() {
        if (durationFrame == null) {
            durationFrame = new JFrame("set durarion");
            durationFrame.add(getDurationField());
        }
        return durationFrame;
    }

    private JMenu createArrayMenu() {
        JMenu arrayMenu = new JMenu("Change array");
        arrayMenu.add(getPlusItem());
        arrayMenu.add(getMinuItemn());
        return arrayMenu;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(getOpenItem());
        fileMenu.add(getSaveItem());
        fileMenu.add(getSaveAsItem());
        return fileMenu;
    }

    private JMenuItem getSaveItem() {
        if(saveItem == null) {
            saveItem = new JMenuItem("Save");
            saveItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if (currentFile != null) {
                        save(currentFile);
                    }
                }
            });
        }
        return saveItem;
    }

    private JMenuItem getMinuItemn() {
        if (minuItem == null) {
            minuItem = new JMenuItem("Remove column");
            minuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    colorTableModel.removeColumn();
                }
            });
        }
        return minuItem;
    }

    private JMenuItem getPlusItem() {
        if (plusItem == null) {
            plusItem = new JMenuItem("Add column");
            plusItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    colorTableModel.addColumn();
                }
            });
        }
        return plusItem;
    }

    private JMenuItem getSaveAsItem() {
        if (saveAsItem == null) {
            saveAsItem = new JMenuItem("Save as");
            saveAsItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    JFileChooser fileChooser = new JFileChooser();
                    if (fileChooser.showSaveDialog(rootPane) == 0) {
                        save(fileChooser.getSelectedFile());
                    }
                }

            });
        }
        return saveAsItem;
    }

    private void save(File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(colorArray);
            currentFile = file;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JMenuItem getOpenItem() {
        if (openButton == null) {
            openButton = new JMenuItem("Open");
            openButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    JFileChooser fileChooser = new JFileChooser();
                    int resultId = fileChooser.showOpenDialog(rootPane);
                    if (resultId == 0) {
                        File file =fileChooser.getSelectedFile();
                        try {
                            FileInputStream fis = new FileInputStream(file);
                            ObjectInputStream ois = new ObjectInputStream(fis);
                            Object readObj = ois.readObject();
                            @SuppressWarnings("unchecked")
                            List<List<Color>> array = (List<List<Color>>) readObj;
                            getColorTableModel().setColorArray(array);
                            extractUsedColors(array);
                            currentFile = file;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        return openButton;
    }

    protected void extractUsedColors(List<List<Color>> array) {
        Set<Color> usedColors = new HashSet<Color>();
        for (List<Color> column : array) {
            for (Color cellColor : column) {
                usedColors.add(cellColor);
            }
        }
        for (Color color : usedColors) {
            colorChooser.setColor(color);
        }

    }

    private JTable createColorTable() {
        if (table == null) {
            ColorTableModel model = getColorTableModel();
            model.setColorArray(createColorArray(defaultColumnCount));
            table = new JTable(model);
            table.setDefaultRenderer(Color.class, createColorCellRenderer());
            table.setDefaultEditor(Color.class, createColorCellEditor());
            table.setColumnSelectionAllowed(false);
            table.setRowSelectionAllowed(false);
            table.setAutoCreateRowSorter(false);
            table.setAutoCreateColumnsFromModel(true);

        }
        return table;
    }

    private ColorTableModel getColorTableModel() {
        if (colorTableModel == null) {
            colorTableModel = new ColorTableModel();
        }
        return colorTableModel;
    }

    private TableCellRenderer createColorCellRenderer() {
        return new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2,
                    boolean arg3,  int arg4, int arg5) {
                Color color = (Color) arg1;
                JLabel renderer = new JLabel();
                renderer.setBackground(color);
                renderer.setOpaque(true);
                return renderer;
            }
        };
    }

    private TableCellEditor createColorCellEditor() {
        TableCellEditor editor = new TableCellEditor() {

            @Override
            public boolean isCellEditable(EventObject arg0) {
                return true;
            }

            @Override
            public Object getCellEditorValue() {
                return null;
            }

            @Override
            public void cancelCellEditing() { }

            @Override
            public void addCellEditorListener(CellEditorListener arg0) { }

            @Override
            public Component getTableCellEditorComponent(final JTable table, final Object value,
                    boolean isSelected, final int row, final int column) {

                for (MouseListener listener : editorComponent.getMouseListeners()) {
                    editorComponent.removeMouseListener(listener);
                }
                editorComponent.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseReleased(MouseEvent arg0) { }

                    @Override
                    public void mousePressed(MouseEvent arg0) {
                        editorComponent.setBackground(getPaintColor());
                        table.setValueAt(getPaintColor(), row, column);
                    }

                    @Override
                    public void mouseExited(MouseEvent arg0) { }

                    @Override
                    public void mouseEntered(MouseEvent arg0) { }

                    @Override
                    public void mouseClicked(MouseEvent arg0) { }
                });
                editorComponent.setBackground((Color) value);
                editorComponent.setOpaque(true);
                return editorComponent;

            }

            @Override
            public void removeCellEditorListener(CellEditorListener arg0) { }

            @Override
            public boolean shouldSelectCell(EventObject arg0) {
                return true;
            }

            @Override
            public boolean stopCellEditing() {
                return true;
            }
        };

        return editor;
    }

    private List<List<Color>> createColorArray(int columnCount) {
        List<List<Color>> columns = new LinkedList<List<Color>>();
        for (int i = 0; i < columnCount; i++) {
            columns.add(i, createColorColumn());
        }
        return columns;
    }

    private List<Color> createColorColumn() {
        List<Color> row = new LinkedList<Color>();
        for (int r = 0; r < 32; r++) {
            row.add(r, new Color(0,0,0));
        }
        return row;
    }

    class ColorTableModel extends AbstractTableModel {

        private static final int DEFAULT_ROW_SIZE = 31;
        private static final long serialVersionUID = -4027731088669776264L;

        public ColorTableModel() {
            colorArray = createColorArray(0);
        }

        public void setColorArray(List<List<Color>> newColorArray) {
            colorArray = newColorArray;
            fireTableStructureChanged();
        }

        @Override
        public Class<?> getColumnClass(int arg0) {
            return Color.class;
        }

        @Override
        public int getColumnCount() {
            return colorArray.toArray().length;
        }

        @Override
        public String getColumnName(int arg0) {
            if (arg0 > colorArray.size() -1) {
                return null;
            }
            return String.valueOf(arg0);
        }

        @Override
        public int getRowCount() {
            return DEFAULT_ROW_SIZE;
        }

        @Override
        public Object getValueAt(int arg0, int arg1) {
            if (arg0 > DEFAULT_ROW_SIZE || arg1 > colorArray.size() -1) {
                return null;
            }
            return colorArray.get(arg1).get(arg0);
        }

        @Override
        public boolean isCellEditable(int arg0, int arg1) {
            return true;
        }

        @Override
        public void setValueAt(Object arg0, int arg1, int arg2) {
            if (arg0 instanceof Color && arg1 <= DEFAULT_ROW_SIZE && arg2 <= colorArray.size() - 1) {
                colorArray.get(arg2).set(arg1, (Color) arg0);
                fireTableDataChanged();
            }
        }

        void addColumn() {
            colorArray.add(createColorColumn());
            fireTableStructureChanged();
        }

        void removeColumn() {
            if(colorArray.size() > 1) {
                colorArray.remove(colorArray.size() -1);
                fireTableStructureChanged();
            }
        }
    }
}
