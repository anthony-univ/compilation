package fr.ufrst.m1info.comp4.interpreter.minijaja;

import fr.ufrst.m1info.comp4.parser.minijaja.VisitorMJJException;

public class InterpreterException extends VisitorMJJException {
	public InterpreterException(String message, int line, int column) {
		super(message, line, column);
	}

	@Override
	public String getMessage() {
		return super.getLine() + ":" + super.getColumn() + " Error: " + super.getMessage();
	}
}
