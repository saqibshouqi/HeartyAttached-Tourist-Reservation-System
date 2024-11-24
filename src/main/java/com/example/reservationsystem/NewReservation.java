package com.example.reservationsystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewReservation {
    @FXML
    private TextField reservationVoucherNumber, guestName, hotelName, numberOfAdults,numberOfChildrenBelow5, numberOfChildrenAbove5, roomCategory, roomType, numberOfRooms, remarks, rate, numberOfNights;
    @FXML
    private DatePicker checkIn, checkOut;
    @FXML
    private ChoiceBox<String> mealPlan, chaufferAccommodationRequired, market;
    @FXML
    private Button submitNewReservation;




    @FXML
    protected void onSubmitNewReservationButtonClick() {
        String voucherNumber = reservationVoucherNumber.getText();
        String guest = guestName.getText();
        java.sql.Date checkInDate = checkIn.getValue() == null ? null : java.sql.Date.valueOf(checkIn.getValue());
        java.sql.Date checkOutDate = checkOut.getValue() == null ? null : java.sql.Date.valueOf(checkOut.getValue());

        if (voucherNumber.isEmpty()) {
            showAlert("Error", "Reservation Voucher Number is mandatory");
            return;
        }
        if (guest.isEmpty()) {
            showAlert("Error", "Guest Name is mandatory");
            return;
        }
        if (checkInDate == null) {
            showAlert("Error", "Check-In Date is mandatory");
            return;
        }
        if (checkOutDate == null) {
            showAlert("Error", "Check-Out Date is mandatory");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {

            // Generate the next reservation voucher number if not already set
            if (voucherNumber.isEmpty()) {
                String getLastVoucherQuery = "SELECT MAX(reservationVoucherNumber) FROM guest";
                PreparedStatement lastVoucherStmt = conn.prepareStatement(getLastVoucherQuery);
                ResultSet rs = lastVoucherStmt.executeQuery();
                int lastVoucherNumber = 99;  // Default starting voucher number is 100
                if (rs.next() && rs.getString(1) != null) {
                    lastVoucherNumber = Integer.parseInt(rs.getString(1));
                }
                voucherNumber = String.valueOf(lastVoucherNumber + 1);
                reservationVoucherNumber.setText(voucherNumber);  // Display the generated voucher number
            }

            // Check if the voucher number is unique
            String checkVoucherQuery = "SELECT * FROM guest WHERE reservationVoucherNumber = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkVoucherQuery);
            checkStmt.setString(1, voucherNumber);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                showAlert("Error", "Reservation Voucher Number already exists");
                return;
            }

            // Convert optional fields to default values if they are empty
            int adults = numberOfAdults.getText().isEmpty() ? 1 : Integer.parseInt(numberOfAdults.getText());
            int childrenBelow5 = numberOfChildrenBelow5.getText().isEmpty() ? 0 : Integer.parseInt(numberOfChildrenBelow5.getText());
            int childrenAbove5 = numberOfChildrenAbove5.getText().isEmpty() ? 0 : Integer.parseInt(numberOfChildrenAbove5.getText());
            int rooms = numberOfRooms.getText().isEmpty() ? 1 : Integer.parseInt(numberOfRooms.getText());
            int nights = numberOfNights.getText().isEmpty() ? 0 : Integer.parseInt(numberOfNights.getText());

            // Insert new reservation
            String insertQuery = "INSERT INTO guest (reservationVoucherNumber, guestName, market, hotelName, checkIn, checkOut, numberOfNights, numberOfAdults, numberOfChildrenBelow5, numberOfChildrenAbove5, mealPlan, roomCategory, roomType, numberOfRooms, rate, chaufferAccommodationRequired, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, voucherNumber);
            insertStmt.setString(2, guestName.getText().isEmpty() ? null : guest);
            insertStmt.setString(3, market.getValue());
            insertStmt.setString(4, hotelName.getText().isEmpty() ? null : hotelName.getText());
            insertStmt.setDate(5, checkInDate);
            insertStmt.setDate(6, checkOutDate);
            insertStmt.setInt(7, nights);
            insertStmt.setInt(8, adults);
            insertStmt.setInt(9, childrenBelow5);
            insertStmt.setInt(10, childrenAbove5);
            insertStmt.setString(11, mealPlan.getValue());
            insertStmt.setString(12, roomCategory.getText().isEmpty() ? null : roomCategory.getText());
            insertStmt.setString(13, roomType.getText().isEmpty() ? null : roomType.getText());
            insertStmt.setInt(14, rooms);
            insertStmt.setString(15, rate.getText().isEmpty() ? null : rate.getText() );
            insertStmt.setString(16, chaufferAccommodationRequired.getValue());
            insertStmt.setString(17, remarks.getText().isEmpty() ? null : remarks.getText());
            insertStmt.executeUpdate();

            showAlert("Success", "Reservation created successfully");

            // Close the NewReservation scene after a successful reservation
            Stage stage = (Stage) submitNewReservation.getScene().getWindow();
            stage.close();


        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());

        }
    }


    @FXML
    public void initialize() {

        // Generate and display the next reservation voucher number
        try (Connection conn = DatabaseConnection.connect()) {
            String getLastVoucherQuery = "SELECT MAX(reservationVoucherNumber) FROM guest";
            PreparedStatement lastVoucherStmt = conn.prepareStatement(getLastVoucherQuery);
            ResultSet rs = lastVoucherStmt.executeQuery();
            int lastVoucherNumber = 9999;  // Default starting voucher number is 100
            if (rs.next() && rs.getString(1) != null) {
                lastVoucherNumber = Integer.parseInt(rs.getString(1));
            }
            String nextVoucherNumber = String.valueOf(lastVoucherNumber + 1);
            reservationVoucherNumber.setText(nextVoucherNumber);
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }

        // Add options to Meal Plan ChoiceBox
        mealPlan.getItems().addAll("All Inclusive", "Full Board", "Half Board", "Bed & Breakfast", "Room Only");

        // Add options to Chauffeur Accommodation ChoiceBox
        chaufferAccommodationRequired.getItems().addAll("Yes", "No");

        // Add options to Market ChoiceBox
        market.getItems().addAll("Asian", "Middle East", "European","American", "Commonwealth of Independent States", "China", "Local", "Rest Of the World");

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}