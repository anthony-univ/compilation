package fr.ufrst.m1info.comp4.GUI;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.IntFunction;

public class CodeAreaWithBreakPoints {
    private CodeArea codeArea;
    private Set<Integer> breakPoints;

    public CodeArea getCodeArea() {
        return codeArea;
    }

    public Set<Integer> getBreakPoints() {
        return breakPoints;
    }

    public void setBreakPoints(Set<Integer> breakPoints) {
        this.breakPoints = breakPoints;
    }

    public CodeAreaWithBreakPoints(CodeArea codeArea) {
        this.codeArea = codeArea;
        this.breakPoints = new TreeSet<>();
        apply(true);
    }

    public CodeAreaWithBreakPoints() {
        this.codeArea = new CodeArea();
        this.breakPoints = new TreeSet<>();
        apply(false);
    }

    public void apply(Boolean outputJajacode) {
        codeArea.setId("codeMinijaja");

        if (outputJajacode) {
            new SyntaxHighlighter().applySyntaxHighlightingJajacode(codeArea);
        } else {
            new SyntaxHighlighter().applySyntaxHighlightingMinijaja(codeArea);
        }

        IntFunction<Node> noFactory = LineNumberFactory.get(codeArea);

        IntFunction<Node> graphicFactory = line -> {
            HBox lineBox = new HBox(noFactory.apply(line));
            lineBox.getStyleClass().add("lineno-box");

            Tooltip tooltip = new Tooltip("Break point");
            tooltip.setStyle("-fx-text-fill: white;");
            Tooltip.install(lineBox, tooltip);

            Circle circle = new Circle(5);

            if (breakPoints.contains(line+1)) {
                circle.setFill(Color.RED);
            } else {
                circle.setFill(Color.TRANSPARENT);
            }

            HBox hbox = new HBox(circle, lineBox);
            hbox.setAlignment(Pos.CENTER_LEFT);

            lineBox.setOnMouseClicked(e -> {
                if (breakPoints.contains(line+1)) {
                    breakPoints.remove(line+1);
                    circle.setFill(Color.TRANSPARENT);
                } else {
                    breakPoints.add(line+1);
                    circle.setFill(Color.RED);
                }
            });

            return hbox;
        };
        codeArea.setParagraphGraphicFactory(graphicFactory);
    }
}
