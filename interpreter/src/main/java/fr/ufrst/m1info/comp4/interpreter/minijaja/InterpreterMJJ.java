package fr.ufrst.m1info.comp4.interpreter.minijaja;

import fr.ufrst.m1info.comp4.interpreter.debugger.Debugger;
import fr.ufrst.m1info.comp4.memory.Memory;
import fr.ufrst.m1info.comp4.parser.minijaja.Node;
import fr.ufrst.m1info.comp4.parser.minijaja.VisitorMJJException;

public class InterpreterMJJ {
	private final InterpreterMJJVisitor visitor;
	private final Node root;

	public InterpreterMJJ(Node root) {
		this.visitor = new InterpreterMJJVisitor();
		this.root = root;
	}

	public void setMemory(Memory memory) {
		visitor.setMemory(memory);
	}
	public Memory getMemory() { return this.visitor.getMemory(); }
	public void setDebugger(Debugger debugger) { this.visitor.setDebugger(debugger); }

	public void interpret() throws VisitorMJJException {
		this.root.jjtAccept(this.visitor, new InterpreterMJJData(InterpreterMode.DEFAULT));
	}

	public String getOutput() {
		return visitor.getOutput();
	}
}
