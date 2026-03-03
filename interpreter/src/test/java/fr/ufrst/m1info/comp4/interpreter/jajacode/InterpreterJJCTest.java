package fr.ufrst.m1info.comp4.interpreter.jajacode;

import fr.ufrst.m1info.comp4.memory.*;
import fr.ufrst.m1info.comp4.parser.jajacode.*;
import org.junit.Assert;
import org.junit.Test;

import static fr.ufrst.m1info.comp4.parser.jajacode.JajacodeTreeConstants.*;

public class InterpreterJJCTest {
	@Test
	public void testInterpreter() throws VisitorJJCException {
		ASTJJCJajaCode ASTjjc = new ASTJJCJajaCode(JJTJAJACODE);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(1);
		ASTJJCinit ASTinit = new ASTJJCinit(JJTINIT);
		ASTJJCJajaCode ASTjjc2 = new ASTJJCJajaCode(JJTJAJACODE);
		ASTJJCjcnbre ASTjcnbre2 = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre2.jjtSetValue(2);
		ASTJJCjcstop ASTjcstop = new ASTJJCjcstop(JJTJCSTOP);
		ASTJJCjcnil ASTjcnil = new ASTJJCjcnil(JJTJCNIL);
		ASTjjc2.jjtAddChild(ASTjcnbre2, 0);
		ASTjjc2.jjtAddChild(ASTjcstop, 1);
		ASTjjc2.jjtAddChild(ASTjcnil, 2);
		ASTjjc.jjtAddChild(ASTjcnbre, 0);
		ASTjjc.jjtAddChild(ASTinit, 1);
		ASTjjc.jjtAddChild(ASTjjc2, 2);

		InterpreterJJC interpreter = new InterpreterJJC(ASTjjc);
		Memory memory = new Memory();
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
