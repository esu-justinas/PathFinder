package app.model;

import app.PathFinder;
import core.Warehouse;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class WarehouseTableModel extends AbstractTableModel{
    private ArrayList<Warehouse> warehouses;
    private final String[] COLUMN_NAMES = {"Warehouse ID",
            "Warehouse Name",
            "City Name",
            };

    public WarehouseTableModel(){
        super();
        warehouses = PathFinder.getWarehouses();
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public int getRowCount() {
        return warehouses.size();
    }

    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    public Object getValueAt(int row, int col) {
        Warehouse entry = warehouses.get(row);

        if (col == 0) return entry.getWarehouseId();
        if (col == 1) return entry.getWarehouseName();
        if (col == 2) return entry.getCityName();
        return null;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        return col != 0;
    }

    public void setValueAt(Object value, int row, int col) {
        Warehouse modifiedWarehouse = warehouses.get(row);
        if(col == 1) {
            modifiedWarehouse.setWarehouseName((String) value);
        }
        else if(col == 2) {
            modifiedWarehouse.setCityName((String) value);
        }
        fireTableCellUpdated(row, col);
    }
}