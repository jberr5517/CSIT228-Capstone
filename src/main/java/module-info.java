module com.example.groupactivitycapstone {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.groupactivitycapstone to javafx.fxml;
    exports com.example.groupactivitycapstone;
}