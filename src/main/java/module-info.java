module org.example.trabajo_final {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.desktop;


    opens org.example.trabajo_final to javafx.fxml;
    exports org.example.trabajo_final;
}