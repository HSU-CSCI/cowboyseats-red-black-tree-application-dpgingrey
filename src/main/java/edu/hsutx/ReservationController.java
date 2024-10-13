package edu.hsutx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReservationController {
    private static final double DELAY_SECONDS = 10.0 ;  // Set this to the number of seconds to pause after updating each entry in the csv file
    private CowboySeatTree seatTree;
    private SwingView view;

    // Constructor to link model and view
    public ReservationController(CowboySeatTree seatTree, SwingView view) {
        this.seatTree = seatTree;
        this.view = view;
    }

    // Method to process the CSV file
    public void processCSV(String filePath) {
        // Logic to read and process the CSV file
        // For each row, perform seat assignment or deletion based on the CSV contents
        // Update the seatTree and refresh the view as necessary

        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Read the file line by line
            while ((line = br.readLine()) != null) {
                // Split the line by commas
                String[] values = line.split(",");

                // Extract the four fields
                char command = values[0].charAt(0);  // The first field is a single character (a for add, d for delete)
                String name = values[1];             // The second field is a string (name)
                String seatStr = values[2];          // The third field is a string (seatStr - # of seats to reserve)
                int preference = Integer.parseInt(values[3]);  // The fourth field is an integer (seat preference)

                // The first field is either 'a' for add a reservation or 'd' for delete
                if (command=='a') {
                    // add a reservation
                    int seatsNeeded = Integer.parseInt(seatStr);
                    //delete # of open seats, insert new seat reservation & insert unassigned blocks to right and left
                    SeatAssignment emptyBlock = seatTree.getUnassignedBlockOverThreshold(seatsNeeded);
                    if ( emptyBlock == null) {
                        System.out.println("Error adding "+seatsNeeded+" seats for "+ name + " -- no sufficient free block found.");
                        continue;
                    }

                    int openSeats = emptyBlock.getSeatQuantity();
                    int openSeatStart = emptyBlock.getSeatStartIndex();

                    if (openSeats == seatsNeeded) {
                        // Case where the seats needed exactly equal the block size
                        seatTree.delete(emptyBlock);
                        seatTree.insert(new SeatAssignment(name, seatsNeeded, openSeatStart));
                        view.updateStadiumVisualization(openSeatStart, seatsNeeded, true);
                        view.updateTreeVisualization(seatTree);
                        continue;
                    }

                    int startSeat = (int) ((openSeats-seatsNeeded) * (preference / 100)) + openSeatStart;
                    seatTree.delete(emptyBlock);
                    seatTree.insert(new SeatAssignment(name, seatsNeeded, startSeat));
                    view.updateStadiumVisualization(startSeat, seatsNeeded, true);

                    // If needed, add a new unassigned block left of the new SeatAssignment
                    int leftSeats = startSeat - openSeatStart;
                    if (leftSeats > 0) seatTree.insert(new SeatAssignment(leftSeats, openSeatStart));

                    // If needed, add a new unassigned block right of the new SeatAssignment
                    if (leftSeats+seatsNeeded < openSeats)
                        seatTree.insert(new SeatAssignment(openSeats - leftSeats - seatsNeeded, startSeat+seatsNeeded));

                    view.updateTreeVisualization(seatTree);

                } else if (command=='d') {
                    // delete a reservation, deletes by name
                    // TODO - implement delete code
                    String deleteName = name;
                    SeatAssignment deleteSeat = null;

                    deleteSeat = findByOwner(seatTree.root, name);  //method below

                    if(deleteSeat != null){
                        seatTree.delete(deleteSeat);view.updateTreeVisualization(seatTree);
                        System.out.println("Deleted reservation for " + name);
                    } else {
                        System.out.println("No reservation found for " + name);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SeatAssignment findByOwner(CowboySeatTree.Node node, String name){
        if(node == null){
            return null;  //No node to check
        }

        // Check the current node
        if (name.equals(node.value.getOwner())) {
            return node.value; // Found the matching SeatAssignment
        }

        // Search in the left subtree
        SeatAssignment leftResult = findByOwner(node.left, name);
        if (leftResult != null) {
            return leftResult; // Found in left subtree
        }

        // Search in the right subtree
        return findByOwner(node.right, name); // Continue searching in right subtree
    }

}
