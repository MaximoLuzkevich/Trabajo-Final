module org.example.trabajo_final {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens org.example.trabajo_final to javafx.fxml;
    exports org.example.trabajo_final;
}