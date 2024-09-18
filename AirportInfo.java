package com.gradescope.airportinfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * File: AirportInfo.java
 * Author: Murali Karthik Ganji
 * Description: This program processes flight route data from a CSV file 
 * to provide information about airport departures, total flights, 
 * and airports with flights exceeding a specified limit.
 */

public class AirportInfo {

    /**
     * Reads the flight routes CSV file and returns a HashMap where the key is the 
     * source airport and the value is a space-separated string of destination airports.
     * 
     * @param filename Name of the CSV file to read from.
     * @return A HashMap with source airports as keys and destination airports as values.
     * @throws FileNotFoundException If the file is not found.
     */
    public static HashMap<String, String> getDestinations(String filename) throws FileNotFoundException {
        HashMap<String, String> destinations = new HashMap<>();
        Scanner scanner = new Scanner(new File(filename));

        // Skip the header row
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        // Read and process each line of the CSV file
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] columns = line.split(",");
            String sourceAirport = columns[2];
            String destinationAirport = columns[4];

            // Add destination airports to the source, separated by spaces
            destinations.merge(sourceAirport, destinationAirport, (existing, newDest) -> existing + " " + newDest);
        }

        scanner.close();
        return destinations;
    }

    /**
     * Reads the flight routes CSV file and returns a HashMap where the key is an airport 
     * and the value is the total number of flights (arriving and departing).
     * 
     * @param filename Name of the CSV file to read from.
     * @return A HashMap with airports as keys and total flight counts as values.
     * @throws FileNotFoundException If the file is not found.
     */
    public static HashMap<String, Integer> getAirportCount(String filename) throws FileNotFoundException {
        HashMap<String, Integer> airportCount = new HashMap<>();
        Scanner scanner = new Scanner(new File(filename));

        // Skip the header row
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        // Read and process each line of the CSV file
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] columns = line.split(",");
            String sourceAirport = columns[2];
            String destinationAirport = columns[4];

            // Increment flight counts for both source and destination airports
            airportCount.put(sourceAirport, airportCount.getOrDefault(sourceAirport, 0) + 1);
            airportCount.put(destinationAirport, airportCount.getOrDefault(destinationAirport, 0) + 1);
        }

        scanner.close();
        return airportCount;
    }

    /**
     * Finds and returns the airport(s) with the maximum number of total flights.
     * 
     * @param airportCount A HashMap with airports and their total flight counts.
     * @return A formatted string showing the airport(s) with the maximum flights.
     */
    public static String getMax(HashMap<String, Integer> airportCount) {
        int maxFlights = 0;
        ArrayList<String> maxAirports = new ArrayList<>();

        // Find the maximum flight count and the corresponding airports
        for (Map.Entry<String, Integer> entry : airportCount.entrySet()) {
            int count = entry.getValue();
            if (count > maxFlights) {
                maxFlights = count;
                // Clear the list for new max
                maxAirports.clear();  
                maxAirports.add(entry.getKey());
            } else if (count == maxFlights) {
                maxAirports.add(entry.getKey());
            }
        }

        // Sort the airports alphabetically if there is a tie
        Collections.sort(maxAirports);

        // Return the result in the format: MAX FLIGHTS x : airport1 airport2 ...
        StringBuilder output = new StringBuilder("MAX FLIGHTS " + maxFlights + " : ");
        for (String airport : maxAirports) {
            output.append(airport).append(" ");
        }

        return output.toString().trim();
    }

    /**
     * Returns a formatted string of all departures from each airport to their destinations.
     * 
     * @param destinations A HashMap with source airports and their destination airports.
     * @return A formatted string of airport departures.
     */
    public static String getDepartures(HashMap<String, String> destinations) {
        // Sort the entries by airport code
        ArrayList<Map.Entry<String, String>> entries = new ArrayList<>(destinations.entrySet());
        entries.sort(Map.Entry.comparingByKey());

        StringBuilder output = new StringBuilder();

        // Build the output string for each airport and its destinations
        for (Map.Entry<String, String> entry : entries) {
            output.append(entry.getKey()).append(" flies to ").append(entry.getValue()).append("\n");
        }

        return output.toString().trim();  // Remove trailing newline
    }

    /**
     * Filters and returns airports with flight counts greater than the specified limit.
     * 
     * @param limit The minimum number of flights for an airport to be included.
     * @param airportCount A HashMap with airports and their total flight counts.
     * @return A formatted string of airports that meet the flight count limit.
     */
    public static String getLimits(int limit, HashMap<String, Integer> airportCount) {
        ArrayList<String> validAirports = new ArrayList<>();

        // Add airports that have more flights than the given limit
        for (Map.Entry<String, Integer> entry : airportCount.entrySet()) {
            if (entry.getValue() > limit) {
                validAirports.add(entry.getKey() + " - " + entry.getValue());
            }
        }

        // Sort the result alphabetically
        Collections.sort(validAirports);

        StringBuilder output = new StringBuilder();

        // Build the output string
        for (String airport : validAirports) {
            output.append(airport).append("\n");
        }
        // Remove trailing newline
        return output.toString().trim();  
    }

    
    public static void main(String[] args) throws FileNotFoundException {
        
        HashMap<String, String> destinations = getDestinations(args[0]);
        HashMap<String, Integer> airportCount = getAirportCount(args[0]);
        
        
        if (args[1].equals("MAX")) {
            System.out.println(getMax(airportCount));
        }
        
        if (args[1].equals("DEPARTURES")) {
            System.out.println(getDepartures(destinations));
        }
        
        if (args[1].equals("LIMIT")) {
            System.out.println(getLimits(Integer.valueOf(args[2]), airportCount));
        }
        
    
    }
}