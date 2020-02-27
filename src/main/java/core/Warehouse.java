package core;

public class Warehouse {
    private static int count = 0;
    private int warehouseId;
    private int cityId;
    private String warehouseName;
    private String cityName;
    private boolean hasRoads;
    private int roads;


    public Warehouse(int warehouseId, int cityId, String warehouseName, String cityName, boolean hasRoads, int roads) {
        this.warehouseId = warehouseId;
        this.cityId = cityId;
        this.warehouseName = warehouseName;
        this.cityName = cityName;
        this.hasRoads = hasRoads;
        this.roads = roads;
        Warehouse.count++;
    }

    public String getEntryForCsv() {
        return warehouseId + "," + cityId + "," + warehouseName + "," + cityName + "," +
                (hasRoads ? "Yes" : "No") + "," + roads;
    }

    public String toString(){
        return warehouseName;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public int getCityId() {
        return cityId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public String getCityName() {
        return cityName;
    }

    public boolean isHasRoads() {
        return hasRoads;
    }

    public int getRoads() {
        return roads;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setHasRoads(boolean hasRoads) {
        this.hasRoads = hasRoads;
    }

    public void setRoads(int roads) {
        this.roads = roads;
    }

    public static int getCount() {
        return count;
    }

    public static void resetCount() {
        count = 0;
    }
}
