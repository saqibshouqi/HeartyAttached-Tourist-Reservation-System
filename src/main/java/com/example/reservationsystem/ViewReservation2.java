package com.example.reservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;




public class ViewReservation2 {
    public Button exitViewReservation2;
    @FXML
    private TextField reservationVoucherNumberViewReservation2, guestNameViewReservation2, hotelNameViewReservation2, numberOfAdultsViewReservation2, numberOfChildrenBelow5ViewReservation2, numberOfChildrenAbove5ViewReservation2, roomCategoryViewReservation2, roomTypeViewReservation2, numberOfRoomsViewReservation2, remarksViewReservation2, rateViewReservation2, numberOfNightsViewReservation2;
    @FXML
    private DatePicker checkInViewReservation2, checkOutViewReservation2;
    @FXML
    private ChoiceBox<String> mealPlanViewReservation2, chaufferAccomodationRequiredViewReservation2, marketViewReservation2;

    public void setReservationData(ResultSet rs) throws SQLException {
        reservationVoucherNumberViewReservation2.setText(rs.getString("reservationVoucherNumber"));
        guestNameViewReservation2.setText(rs.getString("guestName"));
        marketViewReservation2.setValue(rs.getString("market"));
        hotelNameViewReservation2.setText(rs.getString("hotelName"));
        checkInViewReservation2.setValue(rs.getDate("checkIn").toLocalDate());
        checkOutViewReservation2.setValue(rs.getDate("checkOut").toLocalDate());
        numberOfNightsViewReservation2.setText(String.valueOf(rs.getInt("numberOfNights")));
        numberOfAdultsViewReservation2.setText(String.valueOf(rs.getInt("numberOfAdults")));
        numberOfChildrenBelow5ViewReservation2.setText(String.valueOf(rs.getInt("numberOfChildrenBelow5")));
        numberOfChildrenAbove5ViewReservation2.setText(String.valueOf(rs.getInt("numberOfChildrenAbove5")));
        mealPlanViewReservation2.setValue(rs.getString("mealPlan"));
        roomCategoryViewReservation2.setText(rs.getString("roomCategory"));
        roomTypeViewReservation2.setText(rs.getString("roomType"));
        numberOfRoomsViewReservation2.setText(String.valueOf(rs.getInt("numberOfRooms")));
        rateViewReservation2.setText(rs.getString("rate"));
        chaufferAccomodationRequiredViewReservation2.setValue(rs.getString("chaufferAccommodationRequired"));
        remarksViewReservation2.setText(rs.getString("remarks"));

        // Disable all fields
        reservationVoucherNumberViewReservation2.setEditable(false);
        guestNameViewReservation2.setEditable(false);
        marketViewReservation2.setDisable(true);
        hotelNameViewReservation2.setEditable(false);
        checkInViewReservation2.setDisable(true);
        checkOutViewReservation2.setDisable(true);
        numberOfNightsViewReservation2.setEditable(false);
        numberOfAdultsViewReservation2.setEditable(false);
        numberOfChildrenBelow5ViewReservation2.setEditable(false);
        numberOfChildrenAbove5ViewReservation2.setEditable(false);
        mealPlanViewReservation2.setDisable(true);
        roomCategoryViewReservation2.setEditable(false);
        roomTypeViewReservation2.setEditable(false);
        numberOfRoomsViewReservation2.setEditable(false);
        rateViewReservation2.setEditable(false);
        chaufferAccomodationRequiredViewReservation2.setDisable(true);
        remarksViewReservation2.setEditable(false);
    }

    public void onExitViewReservation2ButtonClick(ActionEvent actionEvent) {
        Stage stage = (Stage) exitViewReservation2.getScene().getWindow();
        stage.close();
    }

    public void onGenerateVoucherViewReservation2ButtonClick(ActionEvent actionEvent) {



        try {
            String reservationVoucherNumber = reservationVoucherNumberViewReservation2.getText();
            String guestName = guestNameViewReservation2.getText();
            String market = marketViewReservation2.getValue();
            String hotelName = hotelNameViewReservation2.getText();
            String checkIn = checkInViewReservation2.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String checkOut = checkOutViewReservation2.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String nights = numberOfNightsViewReservation2.getText();
            String numberOfAdults = numberOfAdultsViewReservation2.getText();
            String numberOfChildrenBelow5 = numberOfChildrenBelow5ViewReservation2.getText();
            String numberOfChildrenAbove5 = numberOfChildrenAbove5ViewReservation2.getText();
            String mealPlan = mealPlanViewReservation2.getValue();
            String roomCategory = roomCategoryViewReservation2.getText();
            String roomType = roomTypeViewReservation2.getText();
            String numberOfRooms = numberOfRoomsViewReservation2.getText();
            String rate = rateViewReservation2.getText();
            String chaufferAccommodationRequired = chaufferAccomodationRequiredViewReservation2.getValue();
            String remarks = remarksViewReservation2.getText();

            // Path where the document will be saved
            String filePath1 = "D:\\Others\\Nuzran Project\\Reservation Vouchers\\" + reservationVoucherNumber + "__" + guestName + ".docx";
            File file = new File(filePath1);

            // Check if the file already exists
            if (file.exists()) {
                // Show an alert informing the user that the voucher has already been generated
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Voucher has already been generated for the given Reservation Voucher Number.");
                alert.showAndWait();
                return;  // Exit the method, no need to regenerate the voucher
            }

            // Load the existing Word document template
            String templatePath = "D:\\Others\\Nuzran Project\\VoucherFormat.docx";
            try (XWPFDocument doc = new XWPFDocument(new FileInputStream(templatePath))) {

                // Add the "NEW RESERVATION" topic
                XWPFParagraph titleParagraph = doc.createParagraph();
                titleParagraph.setAlignment(ParagraphAlignment.CENTER);

                XWPFRun titleRun = titleParagraph.createRun();
                titleRun.setText("NEW RESERVATION");
                titleRun.setFontFamily("Calibri");
                titleRun.setFontSize(13);
                titleRun.setBold(true);

                // Create a new paragraph for each field and add it to the document
                addParagraph(doc, "Reservation Voucher Number: " + reservationVoucherNumber);
                addParagraph(doc, "Guest Name: " + guestName);
                addParagraph(doc, "Market: " + market);
                addParagraph(doc, "Hotel Name: " + hotelName);
                addParagraph(doc, "Check-in: " + checkIn);
                addParagraph(doc, "Check-out: " + checkOut);
                addParagraph(doc, "Number of Nights: " + nights);
                addParagraph(doc, "Number of Adults: " + numberOfAdults);
                addParagraph(doc, "Number of Children (5 years or below): " + numberOfChildrenBelow5);
                addParagraph(doc, "Number of Children (Above 5 years): " + numberOfChildrenAbove5);
                addParagraph(doc, "Meal Plan: " + mealPlan);
                addParagraph(doc, "Room Category: " + roomCategory);
                addParagraph(doc, "Room Type: " + roomType);
                addParagraph(doc, "Number of Rooms: " + numberOfRooms);
                addParagraph(doc, "Rate: " + rate);
                addParagraph(doc, "Chauffer Accommodation Required: " + chaufferAccommodationRequired);
                addParagraph(doc, "Remarks: " + remarks);

                // Add a blank paragraph for space
                addParagraph(doc, "");

                // Add the confirmation sentence at the bottom
                addParagraph(doc, "Please send the confirmation to info@heartattached.com");

                // Save the document as a Word file
                String filePath = "D:\\Others\\Nuzran Project\\Reservation Vouchers\\" + reservationVoucherNumber + "__" + guestName + ".docx";
                try (FileOutputStream out = new FileOutputStream(filePath)) {
                    doc.write(out);
                }

                // Show success message
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Voucher saved successfully as Word document at " + filePath);
                alert.showAndWait();

                // Close the ViewReservation2 scene after saving the voucher successfully
                Stage stage = (Stage) exitViewReservation2.getScene().getWindow();
                stage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();  // Print stack trace to console for debugging
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save voucher: " + e.getMessage());
            alert.showAndWait();
        }
    }


    private void addParagraph(XWPFDocument doc, String text) {
        XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontFamily("Calibri");
        run.setFontSize(11);
    }

}