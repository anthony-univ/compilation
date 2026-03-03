package fr.ufrst.m1info.comp4.typeChecker;

import fr.ufrst.m1info.comp4.memory.SymbolTable;
import fr.ufrst.m1info.comp4.parser.minijaja.Node;
import fr.ufrst.m1info.comp4.parser.minijaja.VisitorMJJException;

import java.util.Set;
import java.util.TreeSet;

public class TypeChecker {
	private final TypeCheckerVisitor visitor;
	private final Node root;

	public TypeChecker(Node root) {
		this.visitor = new TypeCheckerVisitor();
		this.root = root;
	}

	public void setTable(SymbolTable table) {
		visitor.setTable(table);
	}

	public Set<ErrorType> getErrors() { return this.visitor.getErrors();}

	public void check() throws VisitorMJJException {
		this.visitor.setErrors(new TreeSet<>());
		this.root.jjtAccept(this.visitor, new TypeCheckerData(Mode.FIRST_PASS, "global"));
		this.root.jjtAccept(this.visitor, new TypeCheckerData(Mode.SECOND_PASS, "global"));
	}
}
