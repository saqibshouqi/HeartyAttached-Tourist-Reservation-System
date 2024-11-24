package com.example.reservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML
    private Button createNewReservation;

    @FXML
    private Button viewReservation;

    @FXML
    private Button amendReservation;

    @FXML
    private Button cancelReservation;

    @FXML
    protected void onCreateNewReservationButtonClick(ActionEvent event) throws IOException {
        loadScene("NewReservation.fxml", "Create New Reservation");
    }

    @FXML
    protected void onViewReservationButtonClick(ActionEvent event) throws IOException {
        loadScene("ViewReservation1.fxml", "View Reservation");
    }

    @FXML
    protected void onAmendReservationButtonClick(ActionEvent event) throws IOException {
        loadScene("AmendReservation1.fxml", "Amend Reservation");
    }

    @FXML
    protected void OnCancelReservationButtonClick(ActionEvent event) throws IOException {
        loadScene("CancelReservation1.fxml", "Cancel Reservation");
    }

    private void loadScene(String fxmlFile, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}
