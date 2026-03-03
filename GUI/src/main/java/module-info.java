module GUI{
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires json.simple;
    requires reactfx;
    requires compiler;
    requires interpreter;
    requires parser;
    requires memory;
    requires typeChecker;

    opens fr.ufrst.m1info.comp4.GUI to javafx.fxml;
    exports fr.ufrst.m1info.comp4.GUI;
}