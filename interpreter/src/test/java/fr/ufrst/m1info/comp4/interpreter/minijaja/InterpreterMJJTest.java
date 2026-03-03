package fr.ufrst.m1info.comp4.interpreter.minijaja;

import fr.ufrst.m1info.comp4.memory.*;
import fr.ufrst.m1info.comp4.parser.minijaja.*;
import org.junit.Assert;
import org.junit.Test;
import static fr.ufrst.m1info.comp4.parser.minijaja.MinijajaTreeConstants.*;

public class InterpreterMJJTest {
	@Test
	public void testInterpreter() throws VisitorMJJException, SymbolException {
		ASTMJJclasse ast = new ASTMJJclasse(JJTCLASSE);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("C@global");
		ASTMJJvnil vnil = new ASTMJJvnil(JJTVNIL);
		ASTMJJmain main = new ASTMJJmain(JJTMAIN);
		ASTMJJvnil vnil2 = new ASTMJJvnil(JJTVNIL);
		ASTMJJinil inil = new ASTMJJinil(JJTINIL);
		main.jjtAddChild(vnil2, 0);
		main.jjtAddChild(inil, 1);
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(vnil, 1);
		ast.jjtAddChild(main, 2);

		InterpreterMJJ interpreter = new InterpreterMJJ(ast);
		Memory memory = new Memory();
		memory.getSymbolTable().addIdent("C@global", null, OBJ.VAR);
		interpreter.setMemory(memory);
		Assert.assertEquals(memory, interpreter.getMemory());
		interpreter.interpret();
		Assert.assertEquals("", interpreter.getOutput());
	}

	@Test
	public void testException() {
		InterpreterException exception = new InterpreterException("Error", 1, 1);
		Assert.assertEquals("1:1 Error: Error", exception.getMessage());
	}
}
