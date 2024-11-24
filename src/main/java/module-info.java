module com.example.reservationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.poi.ooxml;
    requires org.apache.pdfbox;


    opens com.example.reservationsystem to javafx.fxml;
    exports com.example.reservationsystem;
}