package com.example.reservationsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewReservation1 {
    @FXML
    private TextField reservationVoucherNumberViewReservation1;

    @FXML
    protected void onContinueViewReservation1ButtonClick() {
        String voucherNumber = reservationVoucherNumberViewReservation1.getText();

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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewReservation2.fxml"));
            Scene scene = new Scene(loader.load());
            ViewReservation2 controller = loader.getController();

            // Set data in ViewReservation2
            controller.setReservationData(rs);

            // Close the ViewReservation1 scene after opening ViewReservation2
            Stage currentStage = (Stage) reservationVoucherNumberViewReservation1.getScene().getWindow();
            currentStage.close();


            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

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

