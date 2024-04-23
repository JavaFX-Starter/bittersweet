module bittersweet {
    requires kotlin.stdlib;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.media;
    requires javafx.web;

    requires org.slf4j;

    exports com.icuxika.bittersweet;
    exports com.icuxika.bittersweet.control;
    exports com.icuxika.bittersweet.delegate;
    exports com.icuxika.bittersweet.dsl;
    exports com.icuxika.bittersweet.extension;
    exports com.icuxika.bittersweet.skin;
}