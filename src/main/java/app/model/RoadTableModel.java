package app.model;

import app.PathFinder;
import core.*;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class RoadTableModel extends AbstractTableModel {
    private ArrayList<City> cities;
    private ArrayList<Warehouse> warehouses;
    private ArrayList<Road> roads;
    private String[] COLUMN_NAMES = {"Road ID",
            "Group",
            "Paths",
            "Origin City Name",
            "Adjacency City Name",
            "Distance KM",
            "Time Hours",
            "Fuel Cost",
            "Toll Cost",
            "Driver Cost"};

    public RoadTableModel(){
        super();
        cities = PathFinder.getCities();
        warehouses = PathFinder.getWarehouses();
        roads = PathFinder.getRoads();
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public int getRowCount() {
        return roads.size();
    }

    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    public Object getValueAt(int row, int col) {
        Road entry = roads.get(row);

        if (col == 0) return entry.getRoadId();
        if (col == 1) return entry.getGroup();
        if (col == 2) return entry.getPaths();
        if (col == 3) return entry.getOriginCityName();
        if (col == 4) return entry.getAdjecencyCityName();
        if (col == 5) return entry.getDistanceKm();
        if (col == 6) return entry.getTimeHours();
        if (col == 7) return entry.getFuelCost();
        if (col == 8) return entry.getTollCost();
        if (col == 9) return entry.getDriverCost();
        return null;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        return col != 0 && col != 2;
    }

    public void setValueAt(Object value, int row, int col) {
        Road modifiedRoad = roads.get(row);
        if(col == 1) {
            modifiedRoad.setGroup((String) value);
        }
        else if(col == 2) {
            modifiedRoad.setPaths((Integer) value);
        }
        else if(col == 3) {
            //Changes the name in all of the incoming and outgoing roads for that city
            for (Road r : roads) {
                if ( r.getOriginCityId() == modifiedRoad.getOriginCityId() ) {
                    r.setOriginCityName((String) value);
                } else if ( r.getAdjecencyId() == modifiedRoad.getOriginCityId() ) {
                    r.setAdjecencyCityName((String) value);
                }
            }
            for (City c : cities) {
                if ( c.getCityId() == modifiedRoad.getOriginCityId() ) {
                    c.setCityName((String) value);
                    if( c.isHasWarehouse() ) {
                        for (Warehouse w : warehouses) {
                            if ( c.getCityId() == w.getCityId() ) {
                                w.setCityName((String) value);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            modifiedRoad.setOriginCityName((String) value);
            fireTableStructureChanged();
        }
        else if(col == 4) {
            for (Road r : roads) {
                if ( r.getOriginCityId() == modifiedRoad.getAdjecencyId() ) {
                    r.setOriginCityName((String) value);
                } else if ( r.getAdjecencyId() == modifiedRoad.getAdjecencyId() ) {
                    r.setAdjecencyCityName((String) value);
                }
            }
            for (City c : cities) {
                if ( c.getCityId() == modifiedRoad.getAdjecencyId() ) {
                    c.setCityName((String) value);
                    if( c.isHasWarehouse() ) {
                        for (Warehouse w : warehouses) {
                            if ( c.getCityId() == w.getCityId() ) {
                                w.setCityName((String) value);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            modifiedRoad.setAdjecencyCityName((String) value);
            fireTableStructureChanged();
        }
        else if(col == 5) {
            modifiedRoad.setDistanceKm((Double) value);
        }
        else if(col == 6) {
            modifiedRoad.setTimeHours((Double) value);
        }
        else if(col == 7) {
            modifiedRoad.setFuelCost((Double) value);
        }
        else if(col == 8) {
            modifiedRoad.setTollCost((Double) value);
        }
        else if(col == 9) {
            modifiedRoad.setDriverCost((Double) value);
        }

        fireTableCellUpdated(row, col);
    }

}