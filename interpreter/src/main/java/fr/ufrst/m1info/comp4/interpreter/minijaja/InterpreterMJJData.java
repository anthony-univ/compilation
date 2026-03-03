package fr.ufrst.m1info.comp4.interpreter.minijaja;

public class InterpreterMJJData {
	private InterpreterMode mode;
	public InterpreterMJJData(InterpreterMode mode) {
		this.mode = mode;
	}

	public InterpreterMode getMode() {
		return mode;
	}

	public void setMode(InterpreterMode mode) {
		this.mode = mode;
	}
}
