package fr.ufrst.m1info.comp4.interpreter.jajacode;

import fr.ufrst.m1info.comp4.memory.*;
import fr.ufrst.m1info.comp4.parser.jajacode.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static fr.ufrst.m1info.comp4.parser.jajacode.JajacodeTreeConstants.*;

public class InterpreterJJCUnitTest {
	private InterpreterJJCVisitor interpreter;
	private Memory memory;
	private SymbolTable symbolTable;

	@Before
	public void init() {
		interpreter = new InterpreterJJCVisitor();
		memory = new Memory();
		interpreter.setMemory(memory);
		symbolTable = memory.getSymbolTable();
	}

	@Test(expected = InterpreterException.class)
	public void testJajaCode() throws VisitorJJCException {
		ASTJJCJajaCode ast = new ASTJJCJajaCode(JJTJAJACODE);
		ast.jjtAccept(interpreter, null);
	}

	@Test
	public void testJcnil() throws VisitorJJCException {
		ASTJJCjcnil ast = new ASTJJCjcnil(JJTJCNIL);
		ast.jjtAccept(interpreter, null);
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testInit() throws VisitorJJCException {
		ASTJJCinit ast = new ASTJJCinit(JJTINIT);
		ast.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
	}

	@Test
	public void testPush() throws VisitorJJCException, HeapException, StackException, SymbolException {
		ASTJJCpush ast = new ASTJJCpush(JJTPUSH);
		ASTJJCjcnbre nbre = new ASTJJCjcnbre(JJTJCNBRE);
		nbre.jjtSetValue(1);
		ast.jjtAddChild(nbre, 0);

		ast.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(1, memory.dequeue());
	}

	@Test
	public void testSwap() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCswap ASTswap = new ASTJJCswap(JJTSWAP);
		memory.enqueue(0);
		memory.enqueue(1);

		ASTswap.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(0, memory.dequeue());
		Assert.assertEquals(1, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testJSwapException() throws VisitorJJCException {
		ASTJJCswap ASTswap = new ASTJJCswap(JJTSWAP);
		memory.enqueue(0);

		ASTswap.jjtAccept(interpreter, null);
	}

	@Test
	public void testNewVar() throws VisitorJJCException, StackException, SymbolException, HeapException {
		ASTJJCneww ASTnew = new ASTJJCneww(JJTNEWW);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTJJCentier ASTentier = new ASTJJCentier(JJTENTIER);
		ASTJJCvar ASTvar = new ASTJJCvar(JJTVAR);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(0);
		ASTnew.jjtAddChild(ASTjcident, 0);
		ASTnew.jjtAddChild(ASTentier, 1);
		ASTnew.jjtAddChild(ASTvar, 2);
		ASTnew.jjtAddChild(ASTjcnbre, 3);
        symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		memory.enqueue(1);

		ASTnew.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Object val = memory.dequeue();
		Assert.assertEquals(1, val);
	}

	@Test(expected = InterpreterException.class)
	public void testNewVarException() throws VisitorJJCException, SymbolException {
		ASTJJCneww ASTnew = new ASTJJCneww(JJTNEWW);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("b@main");
		ASTJJCentier ASTentier = new ASTJJCentier(JJTENTIER);
		ASTJJCvar ASTvar = new ASTJJCvar(JJTVAR);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(0);
		ASTnew.jjtAddChild(ASTjcident, 0);
		ASTnew.jjtAddChild(ASTentier, 1);
		ASTnew.jjtAddChild(ASTvar, 2);
		ASTnew.jjtAddChild(ASTjcnbre, 3);
        symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		memory.enqueue(0);

		ASTnew.jjtAccept(interpreter, null);
	}

	@Test
	public void testNewCst() throws VisitorJJCException, StackException, SymbolException, HeapException {
		ASTJJCneww ASTnew = new ASTJJCneww(JJTNEWW);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTJJCbooleen ASTbooleen = new ASTJJCbooleen(JJTBOOLEEN);
		ASTJJCcst ASTcst = new ASTJJCcst(JJTCST);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(0);
		ASTnew.jjtAddChild(ASTjcident, 0);
		ASTnew.jjtAddChild(ASTbooleen, 1);
		ASTnew.jjtAddChild(ASTcst, 2);
		ASTnew.jjtAddChild(ASTjcnbre, 3);
        symbolTable.addIdent("a@main", SORTE.BOOL, OBJ.CST);
		memory.enqueue(true);

		ASTnew.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Object val = memory.dequeue();
		Assert.assertEquals(true, val);
	}

	@Test(expected = InterpreterException.class)
	public void testNewCstException() throws VisitorJJCException {
		ASTJJCneww ASTnew = new ASTJJCneww(JJTNEWW);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTJJCbooleen ASTbooleen = new ASTJJCbooleen(JJTBOOLEEN);
		ASTJJCcst ASTcst = new ASTJJCcst(JJTCST);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(0);
		ASTnew.jjtAddChild(ASTjcident, 0);
		ASTnew.jjtAddChild(ASTbooleen, 1);
		ASTnew.jjtAddChild(ASTcst, 2);
		ASTnew.jjtAddChild(ASTjcnbre, 3);
		memory.enqueue(true);

		ASTnew.jjtAccept(interpreter, null);
	}

	@Test
	public void testNewMeth() throws VisitorJJCException, StackException, SymbolException, HeapException {
		ASTJJCneww ASTnew = new ASTJJCneww(JJTNEWW);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTJJCentier ASTentier = new ASTJJCentier(JJTENTIER);
		ASTJJCmeth ASTmeth = new ASTJJCmeth(JJTMETH);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(0);
		ASTnew.jjtAddChild(ASTjcident, 0);
		ASTnew.jjtAddChild(ASTentier, 1);
		ASTnew.jjtAddChild(ASTmeth, 2);
		ASTnew.jjtAddChild(ASTjcnbre, 3);
        symbolTable.addIdent("a@main", SORTE.INT, OBJ.METH);
		memory.enqueue(0);

		ASTnew.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Object val = memory.dequeue();
		Assert.assertEquals(0, val);
	}

	@Test(expected = InterpreterException.class)
	public void testNewMethException() throws VisitorJJCException {
		ASTJJCneww ASTnew = new ASTJJCneww(JJTNEWW);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTJJCentier ASTentier = new ASTJJCentier(JJTENTIER);
		ASTJJCmeth ASTmeth = new ASTJJCmeth(JJTMETH);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(0);
		ASTnew.jjtAddChild(ASTjcident, 0);
		ASTnew.jjtAddChild(ASTentier, 1);
		ASTnew.jjtAddChild(ASTmeth, 2);
		ASTnew.jjtAddChild(ASTjcnbre, 3);
		memory.enqueue(0);

		ASTnew.jjtAccept(interpreter, null);
	}

	@Test
	public void testNewArray() throws VisitorJJCException, StackException, SymbolException, HeapException {
		ASTJJCnewarray ASTnew = new ASTJJCnewarray(JJTNEWARRAY);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTJJCentier ASTentier = new ASTJJCentier(JJTENTIER);
		ASTnew.jjtAddChild(ASTjcident, 0);
		ASTnew.jjtAddChild(ASTentier, 1);
        symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(2);

		ASTnew.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		HeapSymbol symbol = memory.getHeap().getSymbol(0);
		Assert.assertEquals(2, symbol.getSize());
		Assert.assertEquals(0, symbol.getAddress());
		Object val = memory.dequeue();
		Assert.assertEquals(0, val);
	}

	@Test(expected = InterpreterException.class)
	public void testNewArrayException() throws VisitorJJCException {
		ASTJJCnewarray ASTnew = new ASTJJCnewarray(JJTNEWARRAY);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTJJCentier ASTentier = new ASTJJCentier(JJTENTIER);
		ASTJJCmeth ASTmeth = new ASTJJCmeth(JJTMETH);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(0);
		ASTnew.jjtAddChild(ASTjcident, 0);
		ASTnew.jjtAddChild(ASTentier, 1);
		ASTnew.jjtAddChild(ASTmeth, 2);
		ASTnew.jjtAddChild(ASTjcnbre, 3);
		memory.enqueue(0);

		ASTnew.jjtAccept(interpreter, null);
	}

	@Test
	public void testInvoke() throws VisitorJJCException, StackException, SymbolException, HeapException {
		ASTJJCinvoke ASTinvoke = new ASTJJCinvoke(JJTINVOKE);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTinvoke.jjtAddChild(ASTjcident, 0);
        symbolTable.addIdent("a@main", SORTE.INT, OBJ.METH);
        memory.enqueue(5);
        memory.declMeth("a@main");

		ASTinvoke.jjtAccept(interpreter, null);
		Assert.assertEquals(5, interpreter.getAdr());
		Assert.assertEquals(2, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testInvokeException() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCinvoke ASTinvoke = new ASTJJCinvoke(JJTINVOKE);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTinvoke.jjtAddChild(ASTjcident, 0);

		ASTinvoke.jjtAccept(interpreter, null);
	}

	@Test
	public void testReturn() throws VisitorJJCException {
		ASTJJCreturnn ASTreturn = new ASTJJCreturnn(JJTRETURNN);
		memory.enqueue(5);

		ASTreturn.jjtAccept(interpreter, null);
		Assert.assertEquals(5, interpreter.getAdr());
	}

	@Test(expected = InterpreterException.class)
	public void testReturnException() throws VisitorJJCException {
		ASTJJCreturnn ASTreturn = new ASTJJCreturnn(JJTRETURNN);

		ASTreturn.jjtAccept(interpreter, null);
	}

	@Test
	public void testWrite() throws VisitorJJCException {
		ASTJJCwrite ASTwrite = new ASTJJCwrite(JJTWRITE);
		memory.enqueue("yes");

		ASTwrite.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals("yes", interpreter.getOutput());
	}

	@Test(expected = InterpreterException.class)
	public void testWriteException() throws VisitorJJCException {
		ASTJJCwrite ASTwrite = new ASTJJCwrite(JJTWRITE);

		ASTwrite.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testWriteExceptionNull() throws VisitorJJCException {
		ASTJJCwrite ASTwrite = new ASTJJCwrite(JJTWRITE);
		memory.enqueue(null);

		ASTwrite.jjtAccept(interpreter, null);
	}

	@Test
	public void testWriteLn() throws VisitorJJCException {
		ASTJJCwriteln ASTwriteln = new ASTJJCwriteln(JJTWRITELN);
		memory.enqueue("yes");

		ASTwriteln.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals("yes\n", interpreter.getOutput());
	}

	@Test(expected = InterpreterException.class)
	public void testWriteLnException() throws VisitorJJCException {
		ASTJJCwriteln ASTwriteln = new ASTJJCwriteln(JJTWRITELN);

		ASTwriteln.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testWriteLnExceptionNull() throws VisitorJJCException {
		ASTJJCwriteln ASTwriteln = new ASTJJCwriteln(JJTWRITELN);
		memory.enqueue(null);

		ASTwriteln.jjtAccept(interpreter, null);
	}

	@Test
	public void testPop() throws VisitorJJCException {
		ASTJJCpop ASTpop = new ASTJJCpop(JJTPOP);
		memory.enqueue(0);

		ASTpop.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
        Assert.assertTrue(memory.stackIsEmpty());
	}

	@Test(expected = InterpreterException.class)
	public void testPopException() throws VisitorJJCException {
		ASTJJCpop ASTpop = new ASTJJCpop(JJTPOP);

		ASTpop.jjtAccept(interpreter, null);
	}

	@Test
	public void testLoad() throws VisitorJJCException, StackException, SymbolException, HeapException {
		ASTJJCload ASTload = new ASTJJCload(JJTLOAD);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTload.jjtAddChild(ASTjcident, 0);
        symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
        memory.enqueue(5);
        memory.identVal("a@main", 0);

		ASTload.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(5, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testLoadException() throws VisitorJJCException {
		ASTJJCload ASTload = new ASTJJCload(JJTLOAD);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTload.jjtAddChild(ASTjcident, 0);

		ASTload.jjtAccept(interpreter, null);
	}

	@Test
	public void testALoad() throws VisitorJJCException, StackException, SymbolException, HeapException {
		ASTJJCaload ASTaload = new ASTJJCaload(JJTALOAD);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTaload.jjtAddChild(ASTjcident, 0);
        symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
        memory.declTab("a@main");
		memory.enqueue(0);
		memory.affectValT("a@main", 5, 0);

		ASTaload.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(5, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testALoadExceptionNull() throws VisitorJJCException, StackException, SymbolException, HeapException {
		ASTJJCaload ASTaload = new ASTJJCaload(JJTALOAD);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTaload.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");
		memory.enqueue(null);
		memory.affectValT("a@main", 5, 0);

		ASTaload.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testALoadExceptionGetValT() throws VisitorJJCException, StackException, SymbolException, HeapException {
		ASTJJCaload ASTaload = new ASTJJCaload(JJTALOAD);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTaload.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");
		memory.enqueue(2);
		memory.affectValT("a@main", 5, 0);

		ASTaload.jjtAccept(interpreter, null);
	}

	@Test
	public void testStore() throws VisitorJJCException, SymbolException, StackException {
		ASTJJCstore ASTstore = new ASTJJCstore(JJTSTORE);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTstore.jjtAddChild(ASTjcident, 0);
        symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
        memory.enqueue(1);
        memory.identVal("a@main", 0);
        memory.enqueue(5);

		ASTstore.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(5, memory.getVal("a@main"));
	}

	@Test
	public void testStoreTab() throws VisitorJJCException, SymbolException, StackException, HeapException {
		ASTJJCstore ASTstore = new ASTJJCstore(JJTSTORE);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTstore.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");
		memory.affectValT("a@main", 2, 0);
		symbolTable.addIdent("b@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("b@main");
		memory.affectValT("b@main", 5, 0);
		memory.enqueue(1);

		ASTstore.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(1, memory.getVal("a@main"));
		Assert.assertEquals(5, memory.getValT("a@main", 0));
	}

	@Test(expected = InterpreterException.class)
	public void testStoreException() throws VisitorJJCException, SymbolException {
		ASTJJCstore ASTstore = new ASTJJCstore(JJTSTORE);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTstore.jjtAddChild(ASTjcident, 0);
        symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);

		ASTstore.jjtAccept(interpreter, null);
	}

	@Test
	public void testAStore() throws VisitorJJCException, SymbolException, HeapException {
		ASTJJCastore ASTastore = new ASTJJCastore(JJTASTORE);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTastore.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");
		memory.enqueue(0);
		memory.enqueue(5);

		ASTastore.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(5, memory.getValT("a@main", 0));
	}

	@Test(expected = InterpreterException.class)
	public void testAStoreExceptionNull() throws VisitorJJCException, SymbolException, HeapException {
		ASTJJCastore ASTastore = new ASTJJCastore(JJTASTORE);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTastore.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");
		memory.enqueue(null);
		memory.enqueue(5);

		ASTastore.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testAStoreExceptionAffectValT() throws VisitorJJCException, SymbolException, HeapException {
		ASTJJCastore ASTastore = new ASTJJCastore(JJTASTORE);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTastore.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");
		memory.enqueue(0);

		ASTastore.jjtAccept(interpreter, null);
	}

	@Test
	public void testIfTrue() throws VisitorJJCException {
		ASTJJCiff ASTif = new ASTJJCiff(JJTIFF);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(10);
		ASTif.jjtAddChild(ASTjcnbre, 0);
		memory.enqueue(true);

		ASTif.jjtAccept(interpreter, null);
		Assert.assertEquals(10, interpreter.getAdr());
	}

	@Test
	public void testIfFalse() throws VisitorJJCException {
		ASTJJCiff ASTif = new ASTJJCiff(JJTIFF);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(10);
		ASTif.jjtAddChild(ASTjcnbre, 0);
		memory.enqueue(false);

		ASTif.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
	}

	@Test(expected = InterpreterException.class)
	public void testIfException() throws VisitorJJCException {
		ASTJJCiff ASTif = new ASTJJCiff(JJTIFF);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(10);
		ASTif.jjtAddChild(ASTjcnbre, 0);

		ASTif.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testIfExceptionNull() throws VisitorJJCException {
		ASTJJCiff ASTif = new ASTJJCiff(JJTIFF);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(10);
		ASTif.jjtAddChild(ASTjcnbre, 0);
		memory.enqueue(null);

		ASTif.jjtAccept(interpreter, null);
	}

	@Test
	public void testGoto() throws VisitorJJCException {
		ASTJJCgotoo ASTgoto = new ASTJJCgotoo(JJTGOTOO);
		ASTJJCjcnbre ASTjcnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTjcnbre.jjtSetValue(10);
		ASTgoto.jjtAddChild(ASTjcnbre, 0);

		ASTgoto.jjtAccept(interpreter, null);
		Assert.assertEquals(10, interpreter.getAdr());
	}

	@Test
	public void testInc() throws VisitorJJCException, SymbolException, StackException {
		ASTJJCinc ASTinc = new ASTJJCinc(JJTINC);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTinc.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		memory.enqueue(1);
		memory.identVal("a@main", 0);
		memory.enqueue(5);

		ASTinc.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(6, memory.getVal("a@main"));
	}

	@Test(expected = InterpreterException.class)
	public void testIncException() throws VisitorJJCException, SymbolException, StackException {
		ASTJJCinc ASTinc = new ASTJJCinc(JJTINC);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("b@main");
		ASTinc.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		memory.enqueue(1);
		memory.identVal("a@main", 0);
		memory.enqueue(5);

		ASTinc.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testIncExceptionNullDequeue() throws VisitorJJCException, SymbolException, StackException {
		ASTJJCinc ASTinc = new ASTJJCinc(JJTINC);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTinc.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		memory.enqueue(1);
		memory.identVal("a@main", 0);
		memory.enqueue(null);

		ASTinc.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testIncExceptionNullGetVal() throws VisitorJJCException, SymbolException, StackException {
		ASTJJCinc ASTinc = new ASTJJCinc(JJTINC);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTinc.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		memory.enqueue(null);
		memory.identVal("a@main", 0);
		memory.enqueue(5);

		ASTinc.jjtAccept(interpreter, null);
	}

	@Test
	public void testAInc() throws VisitorJJCException, SymbolException, HeapException, StackException {
		ASTJJCainc ASTainc = new ASTJJCainc(JJTAINC);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTainc.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");
		memory.enqueue(0);
		memory.enqueue(5);
		memory.affectValT("a@main", 1, 0);

		ASTainc.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(6, memory.getValT("a@main", 0));
	}

	@Test(expected = InterpreterException.class)
	public void testAIncExceptionNull1() throws VisitorJJCException, SymbolException, HeapException, StackException {
		ASTJJCainc ASTainc = new ASTJJCainc(JJTAINC);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTainc.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");
		memory.enqueue(0);
		memory.enqueue(null);
		memory.affectValT("a@main", 1, 0);

		ASTainc.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testAIncExceptionNull2() throws VisitorJJCException, SymbolException, HeapException, StackException {
		ASTJJCainc ASTainc = new ASTJJCainc(JJTAINC);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTainc.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");
		memory.enqueue(null);
		memory.enqueue(5);
		memory.affectValT("a@main", 1, 0);

		ASTainc.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testAIncExceptionNull3() throws VisitorJJCException, SymbolException, HeapException {
		ASTJJCainc ASTainc = new ASTJJCainc(JJTAINC);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ASTainc.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");
		memory.enqueue(0);
		memory.enqueue(5);

		ASTainc.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testAIncExceptionAffectValT() throws VisitorJJCException, SymbolException, HeapException, StackException {
		ASTJJCainc ASTainc = new ASTJJCainc(JJTAINC);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("b@main");
		ASTainc.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");
		memory.enqueue(0);
		memory.enqueue(5);
		memory.affectValT("a@main", 1, 0);

		ASTainc.jjtAccept(interpreter, null);
	}

	@Test
	public void testNop() throws VisitorJJCException {
		ASTJJCnop ASTnop = new ASTJJCnop(JJTNOP);

		ASTnop.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
	}

	@Test
	public void testStop() throws VisitorJJCException {
		ASTJJCjcstop ASTstop = new ASTJJCjcstop(JJTJCSTOP);

		ASTstop.jjtAccept(interpreter, null);
		Assert.assertEquals(-1, interpreter.getAdr());
	}

	@Test
	public void testIdent() throws VisitorJJCException {
		ASTJJCjcident ASTident= new ASTJJCjcident(JJTJCIDENT);

		ASTident.jjtAccept(interpreter, null);
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testTrue() throws VisitorJJCException {
		ASTJJCjcvrai ASTvrai = new ASTJJCjcvrai(JJTJCVRAI);

		Assert.assertEquals(true, ASTvrai.jjtAccept(interpreter, null));
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testFalse() throws VisitorJJCException {
		ASTJJCjcfaux ASTfaux = new ASTJJCjcfaux(JJTJCFAUX);

		Assert.assertEquals(false, ASTfaux.jjtAccept(interpreter, null));
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testNbre() throws VisitorJJCException {
		ASTJJCjcnbre ASTnbre = new ASTJJCjcnbre(JJTJCNBRE);
		ASTnbre.jjtSetValue(5);

		Assert.assertEquals(5, ASTnbre.jjtAccept(interpreter, null));
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testNeg() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCneg ASTneg = new ASTJJCneg(JJTNEG);
		memory.enqueue(5);

		ASTneg.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(-5, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testNegException() throws VisitorJJCException {
		ASTJJCneg ASTneg = new ASTJJCneg(JJTNEG);

		ASTneg.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testNegExceptionNull() throws VisitorJJCException {
		ASTJJCneg ASTneg = new ASTJJCneg(JJTNEG);
		memory.enqueue(null);

		ASTneg.jjtAccept(interpreter, null);
	}

	@Test
	public void testNotTrue() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCnot ASTnot = new ASTJJCnot(JJTNOT);
		memory.enqueue(true);

		ASTnot.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(false, memory.dequeue());
	}

	@Test
	public void testNotFalse() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCnot ASTnot = new ASTJJCnot(JJTNOT);
		memory.enqueue(false);

		ASTnot.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(true, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testNotException() throws VisitorJJCException {
		ASTJJCnot ASTnot = new ASTJJCnot(JJTNOT);

		ASTnot.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testNotExceptionNull() throws VisitorJJCException {
		ASTJJCnot ASTnot = new ASTJJCnot(JJTNOT);
		memory.enqueue(null);

		ASTnot.jjtAccept(interpreter, null);
	}

	@Test
	public void testAdd() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCadd ASTadd = new ASTJJCadd(JJTADD);
		memory.enqueue(1);
		memory.enqueue(5);

		ASTadd.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(6, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testAddException() throws VisitorJJCException {
		ASTJJCadd ASTadd = new ASTJJCadd(JJTADD);
		memory.enqueue(1);

		ASTadd.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testAddExceptionNullDequeue1() throws VisitorJJCException {
		ASTJJCadd ASTadd = new ASTJJCadd(JJTADD);
		memory.enqueue(null);
		memory.enqueue(5);

		ASTadd.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testAddExceptionNullDequeue2() throws VisitorJJCException {
		ASTJJCadd ASTadd = new ASTJJCadd(JJTADD);
		memory.enqueue(1);
		memory.enqueue(null);

		ASTadd.jjtAccept(interpreter, null);
	}

	@Test
	public void testSub() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCsub ASTsub = new ASTJJCsub(JJTSUB);
		memory.enqueue(1);
		memory.enqueue(5);

		ASTsub.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(-4, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testSubException() throws VisitorJJCException {
		ASTJJCsub ASTsub = new ASTJJCsub(JJTSUB);
		memory.enqueue(1);

		ASTsub.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testSubExceptionNullDequeue1() throws VisitorJJCException {
		ASTJJCsub ASTsub = new ASTJJCsub(JJTSUB);
		memory.enqueue(null);
		memory.enqueue(5);

		ASTsub.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testSubExceptionNullDequeue2() throws VisitorJJCException {
		ASTJJCsub ASTsub = new ASTJJCsub(JJTSUB);
		memory.enqueue(1);
		memory.enqueue(null);

		ASTsub.jjtAccept(interpreter, null);
	}

	@Test
	public void testMul() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCmul ASTmul = new ASTJJCmul(JJTMUL);
		memory.enqueue(2);
		memory.enqueue(5);

		ASTmul.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(10, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testMulException() throws VisitorJJCException {
		ASTJJCmul ASTmul = new ASTJJCmul(JJTMUL);
		memory.enqueue(2);

		ASTmul.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testMulExceptionNullDequeue1() throws VisitorJJCException {
		ASTJJCmul ASTmul = new ASTJJCmul(JJTMUL);
		memory.enqueue(null);
		memory.enqueue(5);

		ASTmul.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testMulExceptionNullDequeue2() throws VisitorJJCException {
		ASTJJCmul ASTmul = new ASTJJCmul(JJTMUL);
		memory.enqueue(2);
		memory.enqueue(null);

		ASTmul.jjtAccept(interpreter, null);
	}

	@Test
	public void testDiv() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCdiv ASTdiv = new ASTJJCdiv(JJTDIV);
		memory.enqueue(2);
		memory.enqueue(5);

		ASTdiv.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(2/5, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testDivException() throws VisitorJJCException {
		ASTJJCdiv ASTdiv = new ASTJJCdiv(JJTDIV);
		memory.enqueue(2);

		ASTdiv.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testDivExceptionNullDequeue1() throws VisitorJJCException {
		ASTJJCdiv ASTdiv = new ASTJJCdiv(JJTDIV);
		memory.enqueue(null);
		memory.enqueue(5);

		ASTdiv.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testDivExceptionNullDequeue2() throws VisitorJJCException {
		ASTJJCdiv ASTdiv = new ASTJJCdiv(JJTDIV);
		memory.enqueue(2);
		memory.enqueue(null);

		ASTdiv.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testDivExceptionDivisionByZero() throws VisitorJJCException {
		ASTJJCdiv ASTdiv = new ASTJJCdiv(JJTDIV);
		memory.enqueue(2);
		memory.enqueue(0);

		ASTdiv.jjtAccept(interpreter, null);
	}

	@Test
	public void testCmpFalse() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCcmp ASTcmp = new ASTJJCcmp(JJTCMP);
		memory.enqueue(2);
		memory.enqueue(5);

		ASTcmp.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(false, memory.dequeue());
	}

	@Test
	public void testCmpTrue() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCcmp ASTcmp = new ASTJJCcmp(JJTCMP);
		memory.enqueue(2);
		memory.enqueue(2);

		ASTcmp.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(true, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testCmpException() throws VisitorJJCException {
		ASTJJCcmp ASTcmp = new ASTJJCcmp(JJTCMP);
		memory.enqueue(2);

		ASTcmp.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testCmpExceptionNullDequeue1() throws VisitorJJCException {
		ASTJJCcmp ASTcmp = new ASTJJCcmp(JJTCMP);
		memory.enqueue(null);
		memory.enqueue(5);

		ASTcmp.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testCmpExceptionNullDequeue2() throws VisitorJJCException {
		ASTJJCcmp ASTcmp = new ASTJJCcmp(JJTCMP);
		memory.enqueue(2);
		memory.enqueue(null);

		ASTcmp.jjtAccept(interpreter, null);
	}

	@Test
	public void testSupFalse() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCsup ASTsup = new ASTJJCsup(JJTSUP);
		memory.enqueue(2);
		memory.enqueue(5);

		ASTsup.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(false, memory.dequeue());
	}

	@Test
	public void testSupTrue() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCsup ASTsup = new ASTJJCsup(JJTSUP);
		memory.enqueue(5);
		memory.enqueue(2);

		ASTsup.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(true, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testSupException() throws VisitorJJCException {
		ASTJJCsup ASTsup = new ASTJJCsup(JJTSUP);
		memory.enqueue(2);

		ASTsup.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testSupExceptionNullDequeue1() throws VisitorJJCException {
		ASTJJCsup ASTsup = new ASTJJCsup(JJTSUP);
		memory.enqueue(null);
		memory.enqueue(5);

		ASTsup.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testSupExceptionNullDequeue2() throws VisitorJJCException {
		ASTJJCsup ASTsup = new ASTJJCsup(JJTSUP);
		memory.enqueue(2);
		memory.enqueue(null);

		ASTsup.jjtAccept(interpreter, null);
	}

	@Test
	public void testOrFirstTrue() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCor ASTor = new ASTJJCor(JJTOR);
		memory.enqueue(true);
		memory.enqueue(false);

		ASTor.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(true, memory.dequeue());
	}

	@Test
	public void testOrFirstFalse() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCor ASTor = new ASTJJCor(JJTOR);
		memory.enqueue(false);
		memory.enqueue(true);

		ASTor.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(true, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testOrException() throws VisitorJJCException {
		ASTJJCor ASTor = new ASTJJCor(JJTOR);
		memory.enqueue(true);

		ASTor.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testOrExceptionNullDequeue1() throws VisitorJJCException {
		ASTJJCor ASTor = new ASTJJCor(JJTOR);
		memory.enqueue(null);
		memory.enqueue(true);

		ASTor.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testOrExceptionNullDequeue2() throws VisitorJJCException {
		ASTJJCor ASTor = new ASTJJCor(JJTOR);
		memory.enqueue(false);
		memory.enqueue(null);

		ASTor.jjtAccept(interpreter, null);
	}

	@Test
	public void testAndFirstTrue() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCand ASTand = new ASTJJCand(JJTAND);
		memory.enqueue(true);
		memory.enqueue(false);

		ASTand.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(false, memory.dequeue());
	}

	@Test
	public void testAndFirstFalse() throws VisitorJJCException, StackException, HeapException, SymbolException {
		ASTJJCand ASTand = new ASTJJCand(JJTAND);
		memory.enqueue(false);
		memory.enqueue(true);

		ASTand.jjtAccept(interpreter, null);
		Assert.assertEquals(2, interpreter.getAdr());
		Assert.assertEquals(false, memory.dequeue());
	}

	@Test(expected = InterpreterException.class)
	public void testAndException() throws VisitorJJCException {
		ASTJJCand ASTand = new ASTJJCand(JJTAND);
		memory.enqueue(true);

		ASTand.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testAndExceptionNullDequeue1() throws VisitorJJCException {
		ASTJJCand ASTand = new ASTJJCand(JJTAND);
		memory.enqueue(null);
		memory.enqueue(true);

		ASTand.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testAndExceptionNullDequeue2() throws VisitorJJCException {
		ASTJJCand ASTand = new ASTJJCand(JJTAND);
		memory.enqueue(true);
		memory.enqueue(null);

		ASTand.jjtAccept(interpreter, null);
	}

	@Test
	public void testInt() throws VisitorJJCException {
		ASTJJCentier ASTint = new ASTJJCentier(JJTENTIER);

		Assert.assertEquals(SORTE.INT, ASTint.jjtAccept(interpreter, null));
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testBool() throws VisitorJJCException {
		ASTJJCbooleen ASTbool = new ASTJJCbooleen(JJTBOOLEEN);

		Assert.assertEquals(SORTE.BOOL, ASTbool.jjtAccept(interpreter, null));
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testVoid() throws VisitorJJCException {
		ASTJJCvoidd ASTvoid = new ASTJJCvoidd(JJTVOIDD);

		Assert.assertEquals(SORTE.VOID, ASTvoid.jjtAccept(interpreter, null));
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testVar() throws VisitorJJCException {
		ASTJJCvar ASTvar = new ASTJJCvar(JJTVAR);

		Assert.assertEquals(OBJ.VAR, ASTvar.jjtAccept(interpreter, null));
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testMeth() throws VisitorJJCException {
		ASTJJCmeth ASTmeth = new ASTJJCmeth(JJTMETH);

		Assert.assertEquals(OBJ.METH, ASTmeth.jjtAccept(interpreter, null));
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testTab() throws VisitorJJCException {
		ASTJJCtab ASTtab = new ASTJJCtab(JJTTAB);

		Assert.assertEquals(OBJ.TAB, ASTtab.jjtAccept(interpreter, null));
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testCst() throws VisitorJJCException {
		ASTJJCcst ASTcst = new ASTJJCcst(JJTCST);

		Assert.assertEquals(OBJ.CST, ASTcst.jjtAccept(interpreter, null));
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testVcst() throws VisitorJJCException {
		ASTJJCvcst ASTvcst = new ASTJJCvcst(JJTVCST);

		Assert.assertEquals(OBJ.VCST, ASTvcst.jjtAccept(interpreter, null));
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testString() throws VisitorJJCException {
		ASTJJCjcchaine ASTchaine = new ASTJJCjcchaine(JJTJCCHAINE);
		ASTchaine.jjtSetValue("yes");

		Assert.assertEquals("yes", ASTchaine.jjtAccept(interpreter, null));
		Assert.assertEquals(1, interpreter.getAdr());
	}

	@Test
	public void testLength() throws VisitorJJCException, SymbolException, HeapException, StackException {
		ASTJJClength ast = new ASTJJClength(JJTLENGTH);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ast.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");

		ast.jjtAccept(interpreter, null);
		Assert.assertEquals(1, memory.dequeue());
		Assert.assertEquals(2, interpreter.getAdr());
	}

	@Test(expected = InterpreterException.class)
	public void testLengthExceptionNonArray() throws VisitorJJCException, SymbolException, HeapException, StackException {
		ASTJJClength ast = new ASTJJClength(JJTLENGTH);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("a@main");
		ast.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		memory.enqueue(1);
		memory.declTab("a@main");

		ast.jjtAccept(interpreter, null);
	}

	@Test(expected = InterpreterException.class)
	public void testLengthExceptionGetVal() throws VisitorJJCException, SymbolException, HeapException, StackException {
		ASTJJClength ast = new ASTJJClength(JJTLENGTH);
		ASTJJCjcident ASTjcident = new ASTJJCjcident(JJTJCIDENT);
		ASTjcident.jjtSetValue("b@main");
		ast.jjtAddChild(ASTjcident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");

		ast.jjtAccept(interpreter, null);
	}

	@Test
	public void testSimpleNode() throws VisitorJJCException {
		SimpleNode AST = new SimpleNode(JJTNOP);

		AST.jjtAccept(interpreter, null);
		Assert.assertEquals(1, interpreter.getAdr());
	}
}
