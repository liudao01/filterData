module com.sprt.y36wifi.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.desktop;

    opens com.sprt.y36wifi.demo to javafx.fxml;
    exports com.sprt.y36wifi.demo;
}