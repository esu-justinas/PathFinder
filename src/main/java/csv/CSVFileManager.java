package csv;

import core.City;
import core.Road;
import core.Warehouse;

import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CSVFileManager {
    private final static String SOURCEPATH = System.getProperty("user.dir");

    public static void readFromFile(ArrayList<City> cities, ArrayList<Warehouse> warehouses, ArrayList<Road> roads,
                                    String cityFileName, String warehouseFileName, String roadFileName) throws FileNotFoundException, NoSuchElementException {
        Scanner reader;
        boolean hasWarehouse;
        boolean hasRoad;
        String line;
        String[] lines;

        //Reading road.csv file and filling roads ArrayList with new values
        try {
            reader = new Scanner(new File(SOURCEPATH + roadFileName));
            reader.nextLine();

            while(reader.hasNextLine()) {
                line = reader.nextLine();
                lines = line.split(",");

                roads.add(new Road(Integer.parseInt(lines[0]), lines[1], Integer.parseInt(lines[2]), lines[3],
                        Integer.parseInt(lines[4]), Integer.parseInt(lines[5]), lines[6], Double.parseDouble(lines[7]),
                        Double.parseDouble(lines[8]), Double.parseDouble(lines[9]), Double.parseDouble(lines[10]),
                        Double.parseDouble(lines[11])));
            }
            reader.close();
        } catch(FileNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
            throw e;
        }

        //Reading city.csv file and filling cities ArrayList with new values
        try {
            reader = new Scanner(new File(SOURCEPATH + cityFileName));
            reader.nextLine();

            while(reader.hasNext()) {
                line = reader.nextLine();
                lines = line.split(",");

                hasWarehouse = lines[3].equals("Yes");
                hasRoad = lines[4].equals("Yes");

                ArrayList<Road> outgoingRoads = new ArrayList<>();

                for (Road r : roads) {
                    if (Integer.parseInt(lines[1]) == r.getOriginCityId())
                        outgoingRoads.add(r);
                }

                cities.add(new City(lines[0], Integer.parseInt(lines[1]), lines[2], hasWarehouse,
                        hasRoad, Integer.parseInt(lines[5]), outgoingRoads));
            }
            reader.close();
        } catch(FileNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
            throw e;
        }


        //Reading warehouse.csv file and filling warehouses ArrayList with new values
        try {
            reader = new Scanner(new File(SOURCEPATH + warehouseFileName));

            reader.nextLine();
            while(reader.hasNext()) {
                line = reader.nextLine();
                lines = line.split(",");
                hasRoad = lines[4].equals("Yes");

                warehouses.add(new Warehouse(Integer.parseInt(lines[0]), Integer.parseInt(lines[1]), lines[2],
                        lines[3], hasRoad, Integer.parseInt(lines[5])));
            }
            reader.close();
        } catch(FileNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
            throw e;
        }

    }


    public static void saveCityFile(ArrayList<City> cities, String cityFileName) throws IOException {
        final String HEADER = "CityName,CityId,DistrictName,CityHasWarehouse,HasRoad,Roads";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SOURCEPATH + cityFileName))) {
            bw.write(HEADER);
            bw.newLine();
            for(City c : cities) {
                bw.write(c.getEntryForCsv());
                //Checks if c equals to last entry in the core.City array list
                if(!c.equals(cities.get(cities.size() - 1))) {
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void saveWarehouseFile(ArrayList<Warehouse> warehouses, ArrayList<City> cities,
                                         String warehouseFileName, String cityFileName) throws IOException {
        final String HEADER = "WarehouseId,CityId,WarehouseName,CityName,HasRoad,Roads";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SOURCEPATH + warehouseFileName))) {
            bw.write(HEADER);
            bw.newLine();
            for(Warehouse w : warehouses) {
                bw.write(w.getEntryForCsv());
                //Checks if w equals to last entry in the core.Warehouse array list
                if(!w.equals(warehouses.get(warehouses.size() - 1))) {
                    bw.newLine();
                }
            }
            saveCityFile(cities, cityFileName);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

    }

    public static void saveRoadFile(ArrayList<Road> roads, ArrayList<Warehouse> warehouses, ArrayList<City> cities,
                                    String roadFileName, String warehouseFileName, String cityFileName) throws IOException{
        final String HEADER = "RoadId,Group,OriginCityId,OriginCityName,Paths,AdjacencyId,AdjacencyCityName,DistanceKM,TimeHours,FuelCost,TollCost,DriverCost,Period";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SOURCEPATH + roadFileName))) {
            bw.write(HEADER);
            bw.newLine();
            for(Road r : roads) {
                bw.write(r.getEntryForCsv());
                //Checks if r equals to last entry in the core.Road array list
                if(!r.equals(roads.get(roads.size() - 1))) {
                    bw.newLine();
                }
            }
            saveWarehouseFile(warehouses, cities, warehouseFileName, cityFileName);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


}
