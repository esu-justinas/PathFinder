package core;

import java.util.*;

public class Algorithm {
    private List<City> cities;
    private List<Road> roads;
    private Set<City> settledNodes;
    private Set<City> unsettledNodes;
    private Map<City, Double> distance;
    private Map<City, City> predecessor;
    private int criteria;


    public Algorithm(List<City> cities, List<Road> roads) {
        this.cities = cities;
        this.roads = roads;
    }

    public String execute(City source, City destination, int criteria) {
        settledNodes = new HashSet<>();
        unsettledNodes = new HashSet<>();

        distance = new HashMap<>();
        predecessor = new HashMap<>();

        this.criteria = criteria;

        //setting source as having distance of 0
        distance.put(source, 0.0);

        //adding source to the set of unsettledNodes
        unsettledNodes.add(source);

        while (unsettledNodes.size() > 0) {
            //picks the city with the lowest distance from unsettledNodes (first iteration it picks source)
            City currentCity = getLowestDistanceCity(unsettledNodes);

            //if currentCity is our destination, that means that it is the city with shortest unexplored path, and that
            //means that we can stop our loop, cause there is no way of finding a shorter path
            if (currentCity == destination) {
                break;
            }

            //marking current city as settled
            settledNodes.add(currentCity);
            unsettledNodes.remove(currentCity);
            //---------------------------------

            //checks all the adjacent cities from current city and adjusts weights as needed
            findLowestDistances(currentCity);
        }

        if (distance.get(destination) != null) {
            switch (criteria) {
                case 0:
                    return "Results for shortest path from " + source.getCityName() + " to " + destination.getCityName() +
                            " (weight distance km):\n\n" + getPath(destination) + "\n\nTotal distance: " +
                            String.format("%.2f", distance.get(destination)) + "Km";
                case 1:
                    return "Results for shortest path from " + source.getCityName() + " to " + destination.getCityName() +
                            " (weight time hours):\n\n" + getPath(destination) + "\n\nTotal hours: " +
                            String.format("%.2f", distance.get(destination)) + "h";
                case 2:
                    return "Results for shortest path from " + source.getCityName() + " to " + destination.getCityName() +
                            " (weight fuel cost):\n\n" + getPath(destination) + "\n\nTotal fuel cost: " +
                            String.format("%.2f", distance.get(destination)) + "eur";
                case 3:
                    return "Results for shortest path from " + source.getCityName() + " to " + destination.getCityName() +
                            " (weight toll cost):\n\n" + getPath(destination) + "\n\nTotal toll cost: " +
                            String.format("%.2f", distance.get(destination)) + "eur";
                case 4:
                    return "Results for shortest path from " + source.getCityName() + " to " + destination.getCityName() +
                            " (weight driver cost):\n\n" + getPath(destination) + "\n\nTotal driver cost: " +
                            String.format("%.2f", distance.get(destination)) + "eur";
            }
        }

        return "No roads leading to " + destination.getCityName() + " were found, please update your Roads data";
    }

    //goes through all the adjacent cities of a specified city. Every time checking if it can offer a better path
    private void findLowestDistances(City city) {
        ArrayList<City> adjacentCities = getAdjacentCities(city);

        for (City adjacent : adjacentCities) {
            //Checks if the distance to adjacent is larger than the (distance from source to the city) + (distance from city to the adjacent)
            //city is a source in first iteration of a loop from execute()

            if ( getShortestDistance(adjacent) > (getShortestDistance(city) + getWeight(city, adjacent)) ) {
                //if adjacent had a larger distance, it means we found a new better path, we add this distance to the map
                distance.put(adjacent, (getShortestDistance(city) + getWeight(city, adjacent)) );
                unsettledNodes.add(adjacent);
                //declaring a new predecessor
                predecessor.put(adjacent, city);
            }
        }
    }


    //gets distance from city to the target (they have to be adjacent)
    private double getWeight(City city, City target) {
        for(Road r : city.getOutgoingRoads()) {
            if (r.getAdjecencyId() == target.getCityId()) {
                switch (criteria) {
                    case 0:
                        return r.getDistanceKm();
                    case 1:
                        return r.getTimeHours();
                    case 2:
                        return r.getFuelCost();
                    case 3:
                        return r.getTollCost();
                    case 4:
                        return r.getDriverCost();
                }

            }
        }
        throw new RuntimeException("Problem with data");
    }

    //returns arraylist of adjacent cities
    private ArrayList<City> getAdjacentCities(City city) {
        ArrayList<City> adjacentCities = new ArrayList<>();

        for(Road r : city.getOutgoingRoads()) {
            for (City c : cities) {
                if (r.getAdjecencyId() == c.getCityId() && !settledNodes.contains(c)) {
                    adjacentCities.add(c);
                    break;
                }
            }
        }

        return adjacentCities;
    }

    private City getLowestDistanceCity(Set <City> unsettledCities) {
        City lowestDistanceCity = null;

        for (City c : unsettledCities) {
            //First iteration we put lowestDistanceCity as first object in unsettledCities set
            if (lowestDistanceCity == null) {
                lowestDistanceCity = c;
            }
            else if (getShortestDistance(c) < getShortestDistance(lowestDistanceCity)) {
                lowestDistanceCity = c;     //We check weather there are any cities with lower distance, and assign them
            }
        }

        return lowestDistanceCity;
    }

    //returns either a distance, or max value, based on city provided
    private double getShortestDistance(City destination) {
        Double d = distance.get(destination);
        if (d == null) {
            return Double.MAX_VALUE;
        } else {
            return d;
        }
    }

    private StringBuilder getPath(City destination) {
        StringBuilder pathBuilder = new StringBuilder();
        ArrayList<City> path = new ArrayList<>();
        City step = destination;

        path.add(step);
        while (predecessor.get(step) != null) {
            step = predecessor.get(step);
            path.add(step);
        }

        Collections.reverse(path);

        for(City c : path) {
            if (path.get(path.size() - 1) == c) {
                pathBuilder.append(c.getCityName());
            } else {
                pathBuilder.append(c.getCityName()).append(" --> ");
            }
        }
        return pathBuilder;
    }

}
