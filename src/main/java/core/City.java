package core;

import java.util.ArrayList;
import java.util.List;

public class City {
    private static int count = 0;
    private int cityId;
    private String cityName;
    private String districtName;
    private boolean hasWarehouse;
    private boolean hasRoad;
    private int roads;

    private List<Road> outgoingRoads;


    public City(String cityName, int cityId, String districtName, boolean cityHasWarehouse, boolean hasRoad, int roads) {
        this.cityName = cityName;
        this.cityId = cityId;
        this.districtName = districtName;
        this.hasWarehouse = cityHasWarehouse;
        this.hasRoad = hasRoad;
        this.roads = roads;
        this.outgoingRoads = new ArrayList<>();
        City.count++;
    }

    public City(String cityName, int cityId, String districtName, boolean cityHasWarehouse, boolean hasRoad, int roads,
                List<Road> outgoingRoads) {
        this.cityName = cityName;
        this.cityId = cityId;
        this.districtName = districtName;
        this.hasWarehouse = cityHasWarehouse;
        this.hasRoad = hasRoad;
        this.roads = roads;
        this.outgoingRoads = outgoingRoads;
        City.count++;
    }

    public String getEntryForCsv() {
        return cityName + "," + cityId + "," + districtName + "," + (hasWarehouse ? "Yes" : "No") + "," +
                (hasRoad ? "Yes" : "No") + "," + roads;
    }

    public String toString() {
        return cityName;
    }

    public int getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public boolean isHasWarehouse() {
        return hasWarehouse;
    }

    public boolean isHasRoad() {
        return hasRoad;
    }

    public int getRoads() {
        return roads;
    }

    public static int getCount() {
        return count;
    }

    public List<Road> getOutgoingRoads() {
        return outgoingRoads;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public void setHasWarehouse(boolean hasWarehouse) {
        this.hasWarehouse = hasWarehouse;
    }

    public void setHasRoad(boolean hasRoad) {
        this.hasRoad = hasRoad;
    }

    public void setRoads(int roads) {
        this.roads = roads;
    }

    public void addOutgoingRoad(Road outgoingRoad) {
        this.outgoingRoads.add(outgoingRoad);
        this.roads++;
    }

    public void removeOutgoingRoad(Road outgoingRoad) {
        this.outgoingRoads.remove(outgoingRoad);
        this.roads--;
    }

    public static void resetCount() {
        count = 0;
    }
}
