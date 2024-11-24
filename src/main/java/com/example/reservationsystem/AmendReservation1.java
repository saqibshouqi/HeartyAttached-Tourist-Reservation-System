package com.example.reservationsystem;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AmendReservation1 {
    public Button continueAmendReservation1;
    @FXML
    private TextField reservationVoucherNumberAmendReservation1;

    @FXML
    protected void onContinueAmendReservation1ButtonClick() {
        String voucherNumber = reservationVoucherNumberAmendReservation1.getText();

        if (voucherNumber.isEmpty()) {
            showAlert("Error", "Reservation Voucher Number is required");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT * FROM guest WHERE reservationVoucherNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, voucherNumber);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                showAlert("Error", "No reservation found for the provided voucher number");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("AmendReservation2.fxml"));
            Scene scene = new Scene(loader.load());
            AmendReservation2 controller = loader.getController();

            // Set data in AmendReservation2
            controller.setReservationData(rs);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            // Close the current stage (AmendReservation1)
            Stage currentStage = (Stage) continueAmendReservation1.getScene().getWindow();
            currentStage.close();

        } catch (SQLException | IOException e) {
            showAlert("Error", "Database or Loading error: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

