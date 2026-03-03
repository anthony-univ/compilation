package fr.ufrst.m1info.comp4.GUI;

import javafx.scene.control.Label;
import javafx.scene.text.Text;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.richtext.CodeArea;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlighter {

    private static final String[] TYPES = {
            "int", "boolean", "String", "void"
    };

    private static final String[] INSTRUCTIONS = {
            "if", "else", "while", "return"
    };

    public static final String[] OPERATORS = {
        "\\+", "\\-", "\\*", "\\/(?!\\/|\\*)", "\\=", "\\>", "\\=\\=", "\\&\\&", "\\|\\|", "\\!", "\\+\\=", "\\+\\+"
    };

    private static final String TYPE_PATTERN = "\\b(" + String.join("|", TYPES) + ")\\b";
    private static final String INSTR_PATTERN = "\\b(" + String.join("|", INSTRUCTIONS) + ")\\b";
    private static final String CLASS_PATTERN = "class";
    private static final String MAIN_PATTERN = "main";
    private static final String OPERATOR_PATTERN = "(" + String.join("|", OPERATORS) + ")";
    private static final String COMMENT_PATTERN = "//[^\n\r]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String METHOD_PATTERN = "(_|[A-Za-z])([A-Za-z]|[0-9]|_)*(?=\\()";
    private static final String VAR_PATTERN = "(_|[A-Za-z])([A-Za-z]|[0-9]|_)*";
    private static final String INTEGER_PATTERN = "[\\-]?[0-9]+\\b";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String COMMA_PATTERN = "\\,";
    private static final String BOOLEAN_PATTERN = "true|false";

    private static final Pattern PATTERN_MINIJAJA = Pattern.compile(
            "(?<TYPE>" + TYPE_PATTERN + ")"
                    + "|(?<INSTRUCTION>" + INSTR_PATTERN + ")"
                    + "|(?<CLASS>" + CLASS_PATTERN + ")"
                    + "|(?<MAIN>" + MAIN_PATTERN + ")"
                    + "|(?<OPERATOR>" + OPERATOR_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<METHOD>" + METHOD_PATTERN + ")"
                    + "|(?<VAR>" + VAR_PATTERN + ")"
                    + "|(?<INTEGER>" + INTEGER_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<COMMA>" + COMMA_PATTERN + ")"
                    + "|(?<BOOLEAN>" + BOOLEAN_PATTERN + ")"
    );

    public void applySyntaxHighlightingMinijaja(CodeArea codeArea) {
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> codeArea.setStyleSpans(0, computeHighlightingMinijaja(codeArea.getText())));
    }

    private static StyleSpans<Collection<String>> computeHighlightingMinijaja(String text) {
        //System.out.println(text);
        Matcher matcher = PATTERN_MINIJAJA.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String styleClass =
                        matcher.group("TYPE") != null ? "type" :
                            matcher.group("INSTRUCTION") != null ? "instruction" :
                                matcher.group("CLASS") != null ? "class" :
                                    matcher.group("MAIN") != null ? "main" :
                                        matcher.group("OPERATOR") != null ? "operator" :
                                            matcher.group("COMMENT") != null ? "comment" :
                                                matcher.group("METHOD") != null ? "method" :
                                                    matcher.group("VAR") != null ? "var" :
                                                        matcher.group("INTEGER") != null ? "integer" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                matcher.group("PAREN") != null ? "paren" :
                                                                    matcher.group("BRACE") != null ? "brace" :
                                                                        matcher.group("BRACKET") != null ? "bracket" :
                                                                            matcher.group("SEMICOLON") != null ? "semicolon" :
                                                                                matcher.group("COMMA") != null ? "comma" :
                                                                                    matcher.group("BOOLEAN") != null ? "boolean" :
                                                    null;
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**
     * Apply syntax highlighting to the outputs
     */

    private static final String ERROR_PATTERN = "Error: ";

    private static final Pattern OUTPUT = Pattern.compile(
            "(?<ERROR>" + ERROR_PATTERN + ")"
    );

    public void applySyntaxHighlightingOutput(CodeArea codeArea) {
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> codeArea.setStyleSpans(0, computeHighlightingOutput(codeArea.getText())));
    }

    private StyleSpans<? extends Collection<String>> computeHighlightingOutput(String text) {
        //System.out.println(text);
        Matcher matcher = OUTPUT.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String styleClass =
                    matcher.group("ERROR") != null ? "error" :
                         null;

            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**
     * Apply syntax highlighting to the output of the compiler
     */

    private static final String LETTER = "[A-Za-z]";
    private static final String DIGIT = "[0-9]";
    private static final String SCOPE = "(" + LETTER + "|" + DIGIT + "|_|-|>|&)+";
    private static final String VARIABLE_PATTERN = "(_|"+ LETTER + ")(" + LETTER + "|" + DIGIT + "|_)*(->" + SCOPE + ")?@" + SCOPE;

    private static final String[] INSTRS_JAJACODE = {
            "init", "swap", "newarray", "new", "invoke", "return", "push", "pop", "aload", "load", "astore", "store","length", "goto", "writeln", "write", "if", "ainc", "inc", "nop", "sup", "add", "mul", "sub", "div", "or", "and", "neg", "cmp", "jcstop", "not"
    };

    private static final String[] TYPES_JAJACODE = {
            "entier", "booleen", "rien", "omega"
    };

    private static final String[] OBJ_JAJACODE = {
            "var", "cst", "vcst", "meth", "tab"
    };

    private static final Pattern JAJACODE = Pattern.compile(
        "(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<INSTRS>" + String.join("|", INSTRS_JAJACODE) + ")"
                    + "|(?<VARIABLE>" + VARIABLE_PATTERN + ")"
                    + "|(?<DIGIT>" + DIGIT + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<TYPES>" + String.join("|", TYPES_JAJACODE) + ")"
                    + "|(?<OBJ>" + String.join("|", OBJ_JAJACODE) + ")"
    );

    public void applySyntaxHighlightingJajacode(CodeArea codeArea) {
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> codeArea.setStyleSpans(0, computeHighlightingJajacode(codeArea.getText())));
    }

    private StyleSpans<? extends Collection<String>> computeHighlightingJajacode(String text) {
        //System.out.println(text);
        Matcher matcher = JAJACODE.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String styleClass =
                    matcher.group("STRING") != null ? "string" :
                        matcher.group("INSTRS") != null ? "main" :
                            matcher.group("VARIABLE") != null ? "var" :
                                matcher.group("DIGIT") != null ? "integer" :
                                    matcher.group("PAREN") != null ? "paren" :
                                        matcher.group("TYPES") != null ? "type" :
                                            matcher.group("OBJ") != null ? "class" :
                            null;

            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
