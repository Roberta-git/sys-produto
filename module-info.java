module org.sysproduto {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.sysproduto to javafx.fxml;
    opens org.sysproduto.controller to javafx.fxml;
    opens org.sysproduto.model to javafx.base, javafx.fxml;

    exports org.sysproduto;
    exports org.sysproduto.controller;
    exports org.sysproduto.model;
}