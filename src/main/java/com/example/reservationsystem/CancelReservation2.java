package com.example.reservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CancelReservation2 {

    @FXML
    private TextField cancellationVoucherNumber;

    @FXML
    private TextField reservationVoucherNumber;

    @FXML
    private ChoiceBox<String> reasonForCancellation;

    @FXML
    public void initialize() {
        cancellationVoucherNumber.setText("C-");


        reasonForCancellation.getItems().addAll(
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


    private boolean isReservationCancelled = false;  // Flag to track if reservation is cancelled

    @FXML
    protected void onCancelReservationCancelReservation2ButtonClick() {
        String voucherNumber = cancellationVoucherNumber.getText();
        String reason = reasonForCancellation.getValue();
        String reservationVoucher = reservationVoucherNumber.getText();

        if (voucherNumber.isEmpty() || voucherNumber.equals("C-")) {
            showAlert("Error", "Cancellation Voucher Number is required and cannot be 'C-'");
            return;
        }

        // Check if the Reservation Voucher Number is filled
        if (reservationVoucher.isEmpty()) {
            showAlert("Error", "Reservation Voucher Number is required");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            // Check if the cancellation voucher number already exists
            String checkQuery = "SELECT * FROM cancellation WHERE cancellationVoucherNumber = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, voucherNumber);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                showAlert("Error", "Cancellation Voucher Number already exists");
                return;
            }

            // Confirm cancellation
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText("Are you sure you want to cancel this reservation?");
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Insert cancellation record
                    try {
                        String insertQuery = "INSERT INTO cancellation (cancellationVoucherNumber, reasonForCancellation, reservationVoucherNumber) VALUES (?, ?, ?)";
                        PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                        insertStmt.setString(1, voucherNumber);
                        insertStmt.setString(2, reason.isEmpty() ? null : reason);
                        insertStmt.setString(3, reservationVoucher);

                        insertStmt.executeUpdate();
                        showAlert("Success", "Reservation cancelled successfully");

                        isReservationCancelled = true; // Set the flag to true when cancellation is successful

                    } catch (SQLException e) {
                        showAlert("Error", "Database error: " + e.getMessage());
                    }
                }
            });

        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    public void onGenerateVoucherAmendReservation2ButtonClick(ActionEvent actionEvent) {
        if (!isReservationCancelled) {  // Check if the reservation is cancelled
            showAlert("Error", "Please cancel the reservation before generating the voucher");
            return;
        }

        String voucherNumber = cancellationVoucherNumber.getText();
        String reason = reasonForCancellation.getValue();
        String reservationVoucher = reservationVoucherNumber.getText();

        if (voucherNumber.isEmpty()) {
            showAlert("Error", "Cancellation Voucher Number is required to generate the voucher");
            return;
        }

        try {
            // Load the existing Word document template
            String templatePath = "D:\\Others\\Nuzran Project\\VoucherFormat.docx";
            try (XWPFDocument doc = new XWPFDocument(new FileInputStream(templatePath))) {

                // Add heading "CANCELLATION VOUCHER" at the top of the document
                XWPFParagraph heading = doc.createParagraph();
                XWPFRun headingRun = heading.createRun();


                headingRun.setText("CANCELLATION VOUCHER");
                headingRun.setFontFamily("Calibri");
                headingRun.setFontSize(14);
                headingRun.setBold(true);

                heading.setAlignment(ParagraphAlignment.CENTER);

                // Add cancellation details to the document
                addParagraph(doc, "");
                addParagraph(doc, "Cancellation Voucher Number: " + voucherNumber);
                addParagraph(doc, "Reason For Cancellation: " + (reason.isEmpty() ? "Not Provided" : reason));
                addParagraph(doc, "Reservation Voucher Number: " + reservationVoucher);

                // Add a blank paragraph for space
                addParagraph(doc, "");

                // Add the confirmation sentence at the bottom
                addParagraph(doc, "Please send the confirmation to info@heartattached.com");

                // Save the document as a new Word file with the cancellation voucher number
                String filePath = "D:\\Others\\Nuzran Project\\Cancellation Vouchers\\" + voucherNumber + ".docx";
                try (FileOutputStream out = new FileOutputStream(filePath)) {
                    doc.write(out);
                }

                // Show success message
                showAlert("Success", "Voucher saved successfully as Word document at " + filePath);

                // Close the current stage (CancelReservation2) after voucher is saved
                Stage currentStage = (Stage) cancellationVoucherNumber.getScene().getWindow();
                currentStage.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save voucher: " + e.getMessage());
        }
    }

    private void addParagraph(XWPFDocument doc, String text) {
        XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontFamily("Calibri"); // Set font to Calibri
        run.setFontSize(12);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
