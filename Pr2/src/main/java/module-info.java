module com.example.pr2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.postgresql.jdbc;
    requires java.sql;

    opens com.example.pr2 to javafx.fxml;
    exports com.example.pr2;
    exports com.example.pr2.Controllers;
    opens com.example.pr2.Controllers to javafx.fxml;
    exports com.example.pr2.Models;
    opens com.example.pr2.Models to javafx.fxml;
    //exports com.example.pr2.Controllers;
    //opens com.example.pr2.Controllers to javafx.fxml;
}