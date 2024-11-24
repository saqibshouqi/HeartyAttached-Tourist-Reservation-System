package com.example.reservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;


public class AmendReservation2 {


    private boolean isSubmitted = false; // Track if the submit button has been clicked

    @FXML
    private TextField reservationVoucherNumberAmendReservation2, guestNameAmendReservation2,
            hotelNameAmendReservation2, numberOfAdultsAmendReservation2, numberOfChildrenBelow5AmendReservation2, numberOfChildrenAbove5AmendReservation2,
            roomCategoryAmendReservation2, roomTypeAmendReservation2, numberOfRoomsAmendReservation2,
            remarksAmendReservation2, amendmentVoucherNumber, numberOfNightsAmendReservation2, rateAmendReservation2;

    @FXML
    private DatePicker checkInAmendReservation2, checkOutAmendReservation2;

    @FXML
    private ChoiceBox<String> mealPlanAmendReservation2, chaufferAccommodationRequiredAmendReservation2, reasonForAmendment, marketAmendReservation2;

    @FXML
    private Button submitNewReservationAmendReservation2;

    @FXML
    public void initialize() {
        // Populate Meal Plan ChoiceBox
        mealPlanAmendReservation2.getItems().addAll("Full Board", "Half Board", "Bed & Breakfast");

        // Populate Chauffeur Accommodation Required ChoiceBox
        chaufferAccommodationRequiredAmendReservation2.getItems().addAll("Yes", "No");

        // Populate Market ChoiceBox
        marketAmendReservation2.getItems().addAll("Asian", "Middle East", "European","American", "Commonwealth of Independent States", "China", "Local", "Rest Of the World");

        amendmentVoucherNumber.setText("A-");

        reasonForAmendment.getItems().addAll(
                "Family emergency",
                "Death of family member",
                "Office / work emergency",
                "Sick of family member",
                "Flight cancelled by operator",
                "Postponed due to personal reason",
                "COVID",
                "Natural calamity in originating country",
                "Travel ban by country of origin",
                "Excessive heat in SL",
                "Ethnical riots in SL",
                "Heavy rain and flooding",
                "Bomb blast",
                "Political unrest",
                "Change of destination",
                "Pandemic",
                "Other"
        );
    }


    public void setReservationData(ResultSet rs) throws SQLException {
        reservationVoucherNumberAmendReservation2.setText(rs.getString("reservationVoucherNumber"));
        guestNameAmendReservation2.setText(rs.getString("guestName"));
        marketAmendReservation2.setValue(rs.getString("market"));
        hotelNameAmendReservation2.setText(rs.getString("hotelName"));
        checkInAmendReservation2.setValue(rs.getDate("checkIn").toLocalDate());
        checkOutAmendReservation2.setValue(rs.getDate("checkOut").toLocalDate());
        numberOfNightsAmendReservation2.setText(String.valueOf(rs.getInt("numberOfNights")));
        numberOfAdultsAmendReservation2.setText(String.valueOf(rs.getInt("numberOfAdults")));
        numberOfChildrenBelow5AmendReservation2.setText(String.valueOf(rs.getInt("NumberOfChildrenBelow5")));
        numberOfChildrenAbove5AmendReservation2.setText(String.valueOf(rs.getInt("NumberOfChildrenAbove5")));
        mealPlanAmendReservation2.setValue(rs.getString("mealPlan"));
        roomCategoryAmendReservation2.setText(rs.getString("roomCategory"));
        roomTypeAmendReservation2.setText(rs.getString("roomType"));
        numberOfRoomsAmendReservation2.setText(String.valueOf(rs.getInt("numberOfRooms")));
        rateAmendReservation2.setText(rs.getString("rate"));
        chaufferAccommodationRequiredAmendReservation2.setValue(rs.getString("chaufferAccommodationRequired"));
        remarksAmendReservation2.setText(rs.getString("remarks"));
    }

    @FXML
    protected void onSubmitAmendReservation2ButtonClick(ActionEvent actionEvent) {
        // Capture all input values
        String voucherNumber = reservationVoucherNumberAmendReservation2.getText();
        String amendmentVoucher = amendmentVoucherNumber.getText();
        String reason = reasonForAmendment.getValue();
        String guestName = guestNameAmendReservation2.getText();
        String market = marketAmendReservation2.getValue();
        String hotelName = hotelNameAmendReservation2.getText();
        String mealPlan = mealPlanAmendReservation2.getValue();
        String rate = rateAmendReservation2.getText();
        String chaufferAccommodation = chaufferAccommodationRequiredAmendReservation2.getValue();


        if (amendmentVoucher.isEmpty() || amendmentVoucher.equals("A-")) {
            showAlert("Error", "Amendment Voucher Number is mandatory and cannot be 'A-'");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            // Check if Amendment Voucher Number is unique
            String checkAmendmentQuery = "SELECT * FROM Amendment WHERE AmendmentVoucherNumber = ?";
            PreparedStatement checkAmendStmt = conn.prepareStatement(checkAmendmentQuery);
            checkAmendStmt.setString(1, amendmentVoucher);
            ResultSet rsAmend = checkAmendStmt.executeQuery();
            if (rsAmend.next()) {
                showAlert("Error", "Amendment Voucher Number already exists");
                return;
            }

            // Update Guest table
            String updateGuestQuery = "UPDATE guest SET guestName = ?, market = ?, hotelName = ?, checkIn = ?, checkOut = ?,numberOfNights = ?,  numberOfAdults = ?, NumberOfChildrenBelow5 = ?, NumberOfChildrenAbove5 = ?, mealPlan = ?, roomCategory = ?, roomType = ?, numberOfRooms = ?, rate = ?,  chaufferAccommodationRequired = ?, remarks = ? WHERE reservationVoucherNumber = ?";

            PreparedStatement updateGuestStmt = conn.prepareStatement(updateGuestQuery);

            // Handle optional integer fields
            int adults = (numberOfAdultsAmendReservation2.getText() == null || numberOfAdultsAmendReservation2.getText().isEmpty()) ? 1 : Integer.parseInt(numberOfAdultsAmendReservation2.getText());
            int childrenBelow5 = (numberOfChildrenBelow5AmendReservation2.getText() == null || numberOfChildrenBelow5AmendReservation2.getText().isEmpty()) ? 0 : Integer.parseInt(numberOfChildrenBelow5AmendReservation2.getText());
            int childrenAbove5 = (numberOfChildrenAbove5AmendReservation2.getText() == null || numberOfChildrenAbove5AmendReservation2.getText().isEmpty()) ? 0 : Integer.parseInt(numberOfChildrenAbove5AmendReservation2.getText());
            int rooms = (numberOfRoomsAmendReservation2.getText() == null || numberOfRoomsAmendReservation2.getText().isEmpty()) ? 1 : Integer.parseInt(numberOfRoomsAmendReservation2.getText());
            int nights = (numberOfNightsAmendReservation2.getText() == null || numberOfNightsAmendReservation2.getText().isEmpty()) ? 0 : Integer.parseInt(numberOfNightsAmendReservation2.getText());



            updateGuestStmt.setString(1, guestName != null && !guestName.isEmpty() ? guestName : null);
            updateGuestStmt.setString(2, market != null && !market.isEmpty() ? market : null);
            updateGuestStmt.setString(3, hotelName);
            updateGuestStmt.setDate(4, java.sql.Date.valueOf(checkInAmendReservation2.getValue()));
            updateGuestStmt.setDate(5, java.sql.Date.valueOf(checkOutAmendReservation2.getValue()));
            updateGuestStmt.setInt(6,nights);
            updateGuestStmt.setInt(7, adults);
            updateGuestStmt.setInt(8, childrenBelow5);
            updateGuestStmt.setInt(9, childrenAbove5);
            updateGuestStmt.setString(10, mealPlan);
            updateGuestStmt.setString(11, roomCategoryAmendReservation2.getText() != null && !roomCategoryAmendReservation2.getText().isEmpty() ? roomCategoryAmendReservation2.getText() : null);
            updateGuestStmt.setString(12, roomTypeAmendReservation2.getText() != null && !roomTypeAmendReservation2.getText().isEmpty() ? roomTypeAmendReservation2.getText() : null);
            updateGuestStmt.setInt(13, rooms);
            updateGuestStmt.setString(14,rate);
            updateGuestStmt.setString(15, chaufferAccommodation);
            updateGuestStmt.setString(16, remarksAmendReservation2.getText() != null && !remarksAmendReservation2.getText().isEmpty() ? remarksAmendReservation2.getText() : null);
            updateGuestStmt.setString(17, voucherNumber);



            int rowsUpdated = updateGuestStmt.executeUpdate();
            if (rowsUpdated == 0) {
                showAlert("Error", "No reservation found to update");
                return;
            }

            // Insert into Amendment table
            String insertAmendmentQuery = "INSERT INTO Amendment (AmendmentVoucherNumber, ReservationVoucherNumber, ReasonForAmendment) VALUES (?, ?, ?)";
            PreparedStatement insertAmendStmt = conn.prepareStatement(insertAmendmentQuery);
            insertAmendStmt.setString(1, amendmentVoucher);
            insertAmendStmt.setString(2, voucherNumber);
            insertAmendStmt.setString(3, reason != null && !reason.isEmpty() ? reason : null);

            insertAmendStmt.executeUpdate();

            // Mark as submitted
            isSubmitted = true;  // Set this flag when form is successfully submitted

            showAlert("Success", "Reservation amended successfully");


        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Error", "Number fields must contain valid integers");
        }
    }


    @FXML
    public void onGenerateVoucherAmendReservation2ButtonClick(ActionEvent actionEvent) {

        // Check if the form is submitted before generating the voucher
        if (!isSubmitted) {
            showAlert("Error", "Please submit the amendment before generating the voucher");
            return;
        }

        String amendmentVoucher = amendmentVoucherNumber.getText();
        String reason = reasonForAmendment.getValue();
        String reservationVoucher = reservationVoucherNumberAmendReservation2.getText();
        String guestName = guestNameAmendReservation2.getText();
        String market = marketAmendReservation2.getValue();
        String hotelName = hotelNameAmendReservation2.getText();
        String checkIn = checkInAmendReservation2.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String checkOut = checkOutAmendReservation2.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String nights = numberOfNightsAmendReservation2.getText();
        String adults = numberOfAdultsAmendReservation2.getText();
        String childrenBelow5 = numberOfChildrenBelow5AmendReservation2.getText();
        String childrenAbove5 = numberOfChildrenAbove5AmendReservation2.getText();
        String mealPlan = mealPlanAmendReservation2.getValue();
        String roomCategory = roomCategoryAmendReservation2.getText();
        String roomType = roomTypeAmendReservation2.getText();
        String rooms = numberOfRoomsAmendReservation2.getText();
        String rate = rateAmendReservation2.getText();
        String chauffeurAccommodation = chaufferAccommodationRequiredAmendReservation2.getValue();
        String remarks = remarksAmendReservation2.getText();



        try (FileInputStream fis = new FileInputStream("D:\\Others\\Nuzran Project\\VoucherFormat.docx");
             XWPFDocument document = new XWPFDocument(fis)) {

            XWPFParagraph titleParagraph = document.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);

            titleRun.setText("AMENDMENT VOUCHER");
            titleRun.setFontFamily("Calibri");
            titleRun.setFontSize(13);
            titleRun.setBold(true);

            //XWPFParagraph paragraph = document.createParagraph();
            //XWPFRun run = paragraph.createRun();
            addParagraph(document, "Amendment Voucher Number: " + amendmentVoucher);
            addParagraph(document, "Reservation Voucher Number: " + reservationVoucher);
            addParagraph(document, "Reason for Amendment: " + reason);
            addParagraph(document, "Guest Name: " +guestName);
            addParagraph(document, "Market: " + market);
            addParagraph(document, "Hotel Name: " + hotelName);
            addParagraph(document, "Check-in: " + checkIn);
            addParagraph(document, "Check-out: " + checkOut);
            addParagraph(document, "Number of Nights: " + nights);
            addParagraph(document, "Number of Adults: " + adults);
            addParagraph(document, "Number of Children (5 years or below): " + childrenBelow5);
            addParagraph(document, "Number of Children (Above 5 years): " + childrenAbove5);
            addParagraph(document, "Meal Plan: " + mealPlan);
            addParagraph(document, "Room Category: " + roomCategory);
            addParagraph(document, "Room Type: " + roomType);
            addParagraph(document, "Number of Rooms: " + rooms);
            addParagraph(document, "Rate: " + rate);
            addParagraph(document, "Chauffeur Accommodation Required: " + chauffeurAccommodation);
            addParagraph(document, "Remarks: " + remarks);

            // Add a blank paragraph for space
            addParagraph(document, "");

            // Add the confirmation sentence at the bottom
            addParagraph(document, "Please send the confirmation to info@heartattached.com");



            // Save the document with the desired name
            String fileName = "D:\\Others\\Nuzran Project\\Amendment Vouchers\\" + amendmentVoucher + "_" + guestName + ".docx";
            try (FileOutputStream fos = new FileOutputStream(new File(fileName))) {
                document.write(fos);
            }

            showAlert("Success", "Voucher generated successfully!");

            Stage stage = (Stage) submitNewReservationAmendReservation2.getScene().getWindow();
            stage.close();


        } catch (IOException e) {
            showAlert("Error", "Error generating voucher: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addParagraph(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontFamily("Calibri");
        run.setFontSize(11);
    }

}