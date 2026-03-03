package fr.ufrst.m1info.comp4.interpreter.jajacode;

import fr.ufrst.m1info.comp4.parser.jajacode.VisitorJJCException;

public class InterpreterException extends VisitorJJCException{
    public InterpreterException(String message, int line, int column) {
        super(message, line, column);
    }

    @Override
    public String getMessage() {
        return super.getLine() + ":" + super.getColumn() + " Error: " + super.getMessage();
    }
}

