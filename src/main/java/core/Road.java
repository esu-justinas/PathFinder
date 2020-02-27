package core;

public class Road {
    private static int count = 0;
    private int roadId;
    private String group;
    private int originCityId;
    private String originCityName;

    private int paths;

    private int adjecencyId;
    private String adjecencyCityName;

    private double distanceKm;
    private double timeHours;
    private double fuelCost;
    private double tollCost;
    private double driverCost;

    public Road(int roadId, String group, int originCityId, String originCityName, int paths, int adjecencyId,
                String adjecencyCityName, double distanceKm, double timeHours, double fuelCost, double tollCost,
                double driverCost) {
        this.roadId = roadId;
        this.group = group;
        this.originCityId = originCityId;
        this.originCityName = originCityName;
        this.paths = paths;
        this.adjecencyId = adjecencyId;
        this.adjecencyCityName = adjecencyCityName;
        this.distanceKm = distanceKm;
        this.timeHours = timeHours;
        this.fuelCost = fuelCost;
        this.tollCost = tollCost;
        this.driverCost = driverCost;
        Road.count++;
    }

    public String getEntryForCsv() {
        return roadId + "," + group + "," + originCityId + "," + originCityName + "," + paths + "," + adjecencyId +
                "," + adjecencyCityName + "," + distanceKm + "," + timeHours + "," + fuelCost + "," + tollCost +
                "," + driverCost + ",DAY/NIGHT";
    }

    public String toString() {
        return originCityName;
    }

    public int getRoadId() {
        return roadId;
    }

    public String getGroup() {
        return group;
    }

    public int getOriginCityId() {
        return originCityId;
    }

    public String getOriginCityName() {
        return originCityName;
    }

    public int getPaths() {
        return paths;
    }

    public int getAdjecencyId() {
        return adjecencyId;
    }

    public String getAdjecencyCityName() {
        return adjecencyCityName;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getTimeHours() {
        return timeHours;
    }

    public double getFuelCost() {
        return fuelCost;
    }

    public double getTollCost() {
        return tollCost;
    }

    public double getDriverCost() {
        return driverCost;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setOriginCityName(String originCityName) {
        this.originCityName = originCityName;
    }

    public void setPaths(int paths) {
        this.paths = paths;
    }

    public void setAdjecencyCityName(String adjecencyCityName) {
        this.adjecencyCityName = adjecencyCityName;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public void setTimeHours(double timeHours) {
        this.timeHours = timeHours;
    }

    public void setFuelCost(double fuelCost) {
        this.fuelCost = fuelCost;
    }

    public void setTollCost(double tollCost) {
        this.tollCost = tollCost;
    }

    public void setDriverCost(double driverCost) {
        this.driverCost = driverCost;
    }

    public static int getCount() {
        return count;
    }

    public static void resetCount() {
        count = 0;
    }
}
