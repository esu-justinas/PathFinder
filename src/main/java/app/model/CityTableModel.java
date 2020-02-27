package app.model;

import app.*;
import core.*;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class CityTableModel extends AbstractTableModel{
    private ArrayList<City> cities;
    private ArrayList<Warehouse> warehouses;
    private ArrayList<Road> roads;
    private final String[] COLUMN_NAMES = {"City ID",
            "City Name",
            "District Name",
            "Has Warehouse",
            "Has Road",
            "# of Roads"};

    public CityTableModel(){
        super();
        cities = PathFinder.getCities();
        warehouses = PathFinder.getWarehouses();
        roads = PathFinder.getRoads();
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public int getRowCount() {
        return cities.size();
    }

    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    public Object getValueAt(int row, int col) {
        City entry = cities.get(row);

        if (col == 0) return entry.getCityId();
        if (col == 1) return entry.getCityName();
        if (col == 2) return entry.getDistrictName();
        if (col == 3) return entry.isHasWarehouse();
        if (col == 4) return entry.isHasRoad();
        if (col == 5) return entry.getRoads();
        return null;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        return col != 0 && col != 3 && col != 4 && col != 5;
    }

    public void setValueAt(Object value, int row, int col) {
        City modifiedCity = cities.get(row);

        if(col == 1) {
            //Changing city name changes city name in warehouse data as well
            if(modifiedCity.isHasWarehouse()){
                for(Warehouse w : warehouses) {
                    if ( w.getCityId() == modifiedCity.getCityId() ) {
                        w.setCityName((String) value);
                        PathFinder.getWarehouseModel().fireTableStructureChanged();
                        break;
                    }
                }
            }
            //Changing city name changes it in road data as well
            if (modifiedCity.isHasRoad()) {
                for (Road r : roads) {
                    if ( r.getOriginCityId() == modifiedCity.getCityId() ) {
                        r.setOriginCityName((String) value);
                    } else if ( r.getAdjecencyId() == modifiedCity.getCityId() ) {
                        r.setAdjecencyCityName((String) value);
                    }
                }
                PathFinder.getRoadModel().fireTableStructureChanged();
            }

            modifiedCity.setCityName((String) value);
        } else if(col == 2) {
            modifiedCity.setDistrictName((String) value);
        } else if(col == 4) {
            modifiedCity.setHasRoad((Boolean) value);
        } else if(col == 5) {
            modifiedCity.setRoads((Integer) value);
        }
        fireTableCellUpdated(row, col);
    }

}
