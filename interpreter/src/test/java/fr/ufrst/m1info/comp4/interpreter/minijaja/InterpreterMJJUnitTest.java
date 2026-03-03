package fr.ufrst.m1info.comp4.interpreter.minijaja;

import fr.ufrst.m1info.comp4.memory.*;
import fr.ufrst.m1info.comp4.parser.minijaja.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static fr.ufrst.m1info.comp4.parser.minijaja.MinijajaTreeConstants.*;

public class InterpreterMJJUnitTest {
	private InterpreterMJJVisitor interpreter;
	private Memory memory;
	private SymbolTable symbolTable;
	private InterpreterMJJData data;

	@Before
	public void init() {
		interpreter = new InterpreterMJJVisitor();
		memory = new Memory();
		interpreter.setMemory(memory);
		symbolTable = memory.getSymbolTable();
		data = new InterpreterMJJData(InterpreterMode.DEFAULT);
	}

	@Test
	public void testClass() throws VisitorMJJException, SymbolException {
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
		symbolTable.addIdent("C@global", null, OBJ.VAR);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DELETE, data.getMode());
		Assert.assertTrue(memory.stackIsEmpty());
	}

	@Test(expected = InterpreterException.class)
	public void testClassExceptionDecl() throws VisitorMJJException {
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

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testIdent() throws VisitorMJJException, SymbolException, StackException {
		ASTMJJident ast = new ASTMJJident(JJTIDENT);
		ast.jjtSetValue("a@main");
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		data.setMode(InterpreterMode.EVAL);
		memory.enqueue(1);
		memory.identVal("a@main", 0);

		Assert.assertEquals(1, ast.jjtAccept(interpreter, data));
	}

	@Test(expected = InterpreterException.class)
	public void testIdentExceptionValNull() throws VisitorMJJException, SymbolException, StackException {
		ASTMJJident ast = new ASTMJJident(JJTIDENT);
		ast.jjtSetValue("a@main");
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		data.setMode(InterpreterMode.EVAL);
		memory.enqueue(null);
		memory.identVal("a@main", 0);

		ast.jjtAccept(interpreter, data);
	}

	@Test(expected = InterpreterException.class)
	public void testIdentExceptionIdent() throws VisitorMJJException {
		ASTMJJident ast = new ASTMJJident(JJTIDENT);
		ast.jjtSetValue("a@main");
		data.setMode(InterpreterMode.EVAL);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testDecls() throws VisitorMJJException {
		ASTMJJdecls ast = new ASTMJJdecls(JJTDECLS);
		ASTMJJvnil vnil1 = new ASTMJJvnil(JJTVNIL);
		ASTMJJvnil vnil2 = new ASTMJJvnil(JJTVNIL);
		ast.jjtAddChild(vnil1, 0);
		ast.jjtAddChild(vnil2, 1);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		data.setMode(InterpreterMode.DELETE);
		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DELETE, data.getMode());
	}

	@Test
	public void testCstDefault() throws VisitorMJJException, SymbolException {
		ASTMJJcst ast = new ASTMJJcst(JJTCST);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJomega omega = new ASTMJJomega(JJTOMEGA);
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		ast.jjtAddChild(omega, 2);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.CST);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(1, memory.getStackLength());
	}

	@Test(expected = InterpreterException.class)
	public void testCstDefaultException() throws VisitorMJJException {
		ASTMJJcst ast = new ASTMJJcst(JJTCST);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJomega omega = new ASTMJJomega(JJTOMEGA);
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		ast.jjtAddChild(omega, 2);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testCstDelete() throws VisitorMJJException, SymbolException {
		ASTMJJcst ast = new ASTMJJcst(JJTCST);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJomega omega = new ASTMJJomega(JJTOMEGA);
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		ast.jjtAddChild(omega, 2);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.CST);
		data.setMode(InterpreterMode.DELETE);
		memory.enqueue(5);
		memory.declCst("a@main");

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(0, memory.getStackLength());
	}

	@Test(expected = InterpreterException.class)
	public void testCstDeleteException() throws VisitorMJJException, SymbolException {
		ASTMJJcst ast = new ASTMJJcst(JJTCST);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJomega omega = new ASTMJJomega(JJTOMEGA);
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		ast.jjtAddChild(omega, 2);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.CST);
		data.setMode(InterpreterMode.DELETE);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testTableauDefault() throws VisitorMJJException, SymbolException {
		ASTMJJtableau ast = new ASTMJJtableau(JJTTABLEAU);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(2);
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		ast.jjtAddChild(nbre, 2);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(1, memory.getStackLength());
	}

	@Test(expected = InterpreterException.class)
	public void testTableauDefaultException() throws VisitorMJJException {
		ASTMJJtableau ast = new ASTMJJtableau(JJTTABLEAU);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(2);
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		ast.jjtAddChild(nbre, 2);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testTableauDelete() throws VisitorMJJException, SymbolException, HeapException {
		ASTMJJtableau ast = new ASTMJJtableau(JJTTABLEAU);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(2);
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		ast.jjtAddChild(nbre, 2);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.CST);
		data.setMode(InterpreterMode.DELETE);
		memory.enqueue(2);
		memory.declTab("a@main");

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(0, memory.getStackLength());
	}

	@Test(expected = InterpreterException.class)
	public void testTableauDeleteException() throws VisitorMJJException, SymbolException {
		ASTMJJtableau ast = new ASTMJJtableau(JJTTABLEAU);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(2);
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		ast.jjtAddChild(nbre, 2);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.CST);
		data.setMode(InterpreterMode.DELETE);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testMethDefault() throws VisitorMJJException, SymbolException {
		ASTMJJmethode ast = new ASTMJJmethode(JJTMETHODE);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("f->none");
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		symbolTable.addIdent("f->none", SORTE.INT, OBJ.METH);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(1, memory.getStackLength());
	}

	@Test(expected = InterpreterException.class)
	public void testMethDefaultException() throws VisitorMJJException {
		ASTMJJmethode ast = new ASTMJJmethode(JJTMETHODE);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("f->none");
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testMethDelete() throws VisitorMJJException, SymbolException {
		ASTMJJmethode ast = new ASTMJJmethode(JJTMETHODE);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("f->none");
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		symbolTable.addIdent("f->none", SORTE.INT, OBJ.METH);
		data.setMode(InterpreterMode.DELETE);
		memory.enqueue(null);
		memory.declMeth("f->none");

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(0, memory.getStackLength());
	}

	@Test(expected = InterpreterException.class)
	public void testMethDeleteException() throws VisitorMJJException, SymbolException {
		ASTMJJmethode ast = new ASTMJJmethode(JJTMETHODE);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("f->none");
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		symbolTable.addIdent("f->none", SORTE.INT, OBJ.METH);
		data.setMode(InterpreterMode.DELETE);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testVarDefault() throws VisitorMJJException, SymbolException {
		ASTMJJvar ast = new ASTMJJvar(JJTVAR);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJomega omega = new ASTMJJomega(JJTOMEGA);
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		ast.jjtAddChild(omega, 2);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(1, memory.getStackLength());
	}

	@Test(expected = InterpreterException.class)
	public void testVarDefaultException() throws VisitorMJJException {
		ASTMJJvar ast = new ASTMJJvar(JJTVAR);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJomega omega = new ASTMJJomega(JJTOMEGA);
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		ast.jjtAddChild(omega, 2);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testVarDelete() throws VisitorMJJException, SymbolException {
		ASTMJJvar ast = new ASTMJJvar(JJTVAR);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJomega omega = new ASTMJJomega(JJTOMEGA);
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		ast.jjtAddChild(omega, 2);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		data.setMode(InterpreterMode.DELETE);
		memory.enqueue(5);
		memory.declVar("a@main");

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(0, memory.getStackLength());
	}

	@Test(expected = InterpreterException.class)
	public void testVarDeleteException() throws VisitorMJJException, SymbolException {
		ASTMJJvar ast = new ASTMJJvar(JJTVAR);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJomega omega = new ASTMJJomega(JJTOMEGA);
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		ast.jjtAddChild(omega, 2);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		data.setMode(InterpreterMode.DELETE);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testVars() throws VisitorMJJException {
		ASTMJJvars ast = new ASTMJJvars(JJTVARS);
		ASTMJJvnil vnil1 = new ASTMJJvnil(JJTVNIL);
		ASTMJJvnil vnil2 = new ASTMJJvnil(JJTVNIL);
		ast.jjtAddChild(vnil1, 0);
		ast.jjtAddChild(vnil2, 1);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		data.setMode(InterpreterMode.DELETE);
		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DELETE, data.getMode());
	}

	@Test
	public void testEntetes() throws VisitorMJJException {
		ASTMJJentetes ast = new ASTMJJentetes(JJTENTETES);
		ASTMJJenil enil1 = new ASTMJJenil(JJTENIL);
		ASTMJJenil enil2 = new ASTMJJenil(JJTENIL);
		ast.jjtAddChild(enil1, 0);
		ast.jjtAddChild(enil2, 1);
		data.setMode(InterpreterMode.DELETE);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DELETE, data.getMode());
	}

	@Test
	public void testEntete() throws VisitorMJJException, SymbolException {
		ASTMJJentete ast = new ASTMJJentete(JJTENTETE);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@func->int");
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		symbolTable.addIdent("a@func->int", SORTE.INT, OBJ.VAR);
		memory.enqueue(5);
		memory.declVar("a@func->int");
		data.setMode(InterpreterMode.DELETE);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DELETE, data.getMode());
		Assert.assertTrue(memory.stackIsEmpty());
	}

	@Test(expected = InterpreterException.class)
	public void testEnteteException() throws VisitorMJJException, SymbolException {
		ASTMJJentete ast = new ASTMJJentete(JJTENTETE);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@func->int");
		ast.jjtAddChild(entier, 0);
		ast.jjtAddChild(ident, 1);
		symbolTable.addIdent("a@func->int", SORTE.INT, OBJ.VAR);
		data.setMode(InterpreterMode.DELETE);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testInstrs() throws VisitorMJJException {
		ASTMJJinstrs ast = new ASTMJJinstrs(JJTINSTRS);
		ASTMJJinil inil1 = new ASTMJJinil(JJTINIL);
		ASTMJJinil inil2 = new ASTMJJinil(JJTINIL);
		ast.jjtAddChild(inil1, 0);
		ast.jjtAddChild(inil2, 1);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertFalse(interpreter.getHasReturned());
	}

	@Test
	public void testInstrsHasReturned() throws VisitorMJJException {
		ASTMJJinstrs ast = new ASTMJJinstrs(JJTINSTRS);
		ASTMJJinil inil1 = new ASTMJJinil(JJTINIL);
		ASTMJJinil inil2 = new ASTMJJinil(JJTINIL);
		ast.jjtAddChild(inil1, 0);
		ast.jjtAddChild(inil2, 1);
		interpreter.setHasReturned(true);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertTrue(interpreter.getHasReturned());
	}

	@Test
	public void testReturn() throws VisitorMJJException, SymbolException {
		ASTMJJret ast = new ASTMJJret(JJTRET);
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(5);
		ast.jjtAddChild(nbre, 0);
		symbolTable.addIdent("C@global", null, OBJ.VAR);
		memory.enqueue(null);
		memory.declVar("C@global");

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals(5, memory.getVal("C@global"));
		Assert.assertTrue(interpreter.getHasReturned());
	}

	@Test(expected = InterpreterException.class)
	public void testReturnException() throws VisitorMJJException, SymbolException {
		ASTMJJret ast = new ASTMJJret(JJTRET);
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(5);
		ast.jjtAddChild(nbre, 0);
		symbolTable.addIdent("C@global", null, OBJ.VAR);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testWrite() throws VisitorMJJException {
		ASTMJJecrire ast = new ASTMJJecrire(JJTECRIRE);
		ASTMJJchaine chaine = new ASTMJJchaine(JJTCHAINE);
		chaine.jjtSetValue("yes");
		ast.jjtAddChild(chaine, 0);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals("yes", interpreter.getOutput());
	}

	@Test
	public void testWriteLn() throws VisitorMJJException {
		ASTMJJecrireln ast = new ASTMJJecrireln(JJTECRIRELN);
		ASTMJJchaine chaine = new ASTMJJchaine(JJTCHAINE);
		chaine.jjtSetValue("yes");
		ast.jjtAddChild(chaine, 0);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals("yes\n", interpreter.getOutput());
	}

	@Test
	public void testIf() throws VisitorMJJException {
		ASTMJJsi ast = new ASTMJJsi(JJTSI);
		ASTMJJvrai bool = new ASTMJJvrai(JJTVRAI);
		ASTMJJecrire write = new ASTMJJecrire(JJTECRIRE);
		ASTMJJchaine chaine = new ASTMJJchaine(JJTCHAINE);
		chaine.jjtSetValue("yes");
		write.jjtAddChild(chaine, 0);
		ast.jjtAddChild(bool, 0);
		ast.jjtAddChild(write, 1);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals("yes", interpreter.getOutput());
	}

	@Test
	public void testElse() throws VisitorMJJException {
		ASTMJJsi ast = new ASTMJJsi(JJTSI);
		ASTMJJfaux bool = new ASTMJJfaux(JJTFAUX);
		ASTMJJecrire write = new ASTMJJecrire(JJTECRIRE);
		ASTMJJchaine chaine = new ASTMJJchaine(JJTCHAINE);
		chaine.jjtSetValue("yes");
		write.jjtAddChild(chaine, 0);
		ast.jjtAddChild(bool, 0);
		ast.jjtAddChild(write, 2);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals("yes", interpreter.getOutput());
	}

	@Test
	public void testWhile() throws VisitorMJJException, SymbolException {
		ASTMJJtantque ast = new ASTMJJtantque(JJTTANTQUE);
		ASTMJJequal equal = new ASTMJJequal(JJTEQUAL);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(0);
		equal.jjtAddChild(ident, 0);
		equal.jjtAddChild(nbre, 1);

		ASTMJJinstrs instrs = new ASTMJJinstrs(JJTINSTRS);
		ASTMJJincrement inc = new ASTMJJincrement(JJTINCREMENT);
		inc.jjtAddChild(ident, 0);
		ASTMJJinil inil = new ASTMJJinil(JJTINIL);
		instrs.jjtAddChild(inc, 0);
		instrs.jjtAddChild(inil, 1);

		ast.jjtAddChild(equal, 0);
		ast.jjtAddChild(instrs, 1);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		memory.enqueue(0);
		memory.declVar("a@main");

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals(1, memory.getVal("a@main"));
	}

	@Test
	public void testAppelI() throws VisitorMJJException, SymbolException {
		ASTMJJappelI ast = new ASTMJJappelI(JJTAPPELI);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("f->int@classe");
		ASTMJJlistexp listexp = new ASTMJJlistexp(JJTLISTEXP);
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(0);
		ASTMJJexnil exnil = new ASTMJJexnil(JJTEXNIL);
		listexp.jjtAddChild(nbre, 0);
		listexp.jjtAddChild(exnil, 1);
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(listexp, 1);

		ASTMJJmethode meth = new ASTMJJmethode(JJTMETHODE);
		ASTMJJrien rien = new ASTMJJrien(JJTRIEN);
		ASTMJJentetes entetes = new ASTMJJentetes(JJTENTETES);
		ASTMJJentete entete = new ASTMJJentete(JJTENTETE);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident2 = new ASTMJJident(JJTIDENT);
		ident2.jjtSetValue("a@f->int");
		entete.jjtAddChild(entier, 0);
		entete.jjtAddChild(ident2, 1);
		ASTMJJenil enil = new ASTMJJenil(JJTENIL);
		entetes.jjtAddChild(entete, 0);
		entetes.jjtAddChild(enil, 1);
		ASTMJJvnil vnil = new ASTMJJvnil(JJTVNIL);
		ASTMJJinil inil = new ASTMJJinil(JJTINIL);
		meth.jjtAddChild(rien, 0);
		meth.jjtAddChild(ident, 1);
		meth.jjtAddChild(entetes, 2);
		meth.jjtAddChild(vnil, 3);
		meth.jjtAddChild(inil, 4);
		symbolTable.addIdent("f->int@classe", SORTE.VOID, OBJ.METH);
		memory.enqueue(meth);
		memory.declMeth("f->int@classe");
		symbolTable.addIdent("a@f->int", SORTE.INT, OBJ.VAR);
		interpreter.setHasReturned(true);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertFalse(interpreter.getHasReturned());
	}

	@Test(expected = InterpreterException.class)
	public void testAppelIExceptionNull() throws VisitorMJJException, SymbolException {
		ASTMJJappelI ast = new ASTMJJappelI(JJTAPPELI);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("f->int@classe");
		ASTMJJlistexp listexp = new ASTMJJlistexp(JJTLISTEXP);
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(0);
		ASTMJJexnil exnil = new ASTMJJexnil(JJTEXNIL);
		listexp.jjtAddChild(nbre, 0);
		listexp.jjtAddChild(exnil, 1);
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(listexp, 1);

		ASTMJJmethode meth = new ASTMJJmethode(JJTMETHODE);
		ASTMJJrien rien = new ASTMJJrien(JJTRIEN);
		ASTMJJentetes entetes = new ASTMJJentetes(JJTENTETES);
		ASTMJJentete entete = new ASTMJJentete(JJTENTETE);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident2 = new ASTMJJident(JJTIDENT);
		ident2.jjtSetValue("a@f->int");
		entete.jjtAddChild(entier, 0);
		entete.jjtAddChild(ident2, 1);
		ASTMJJenil enil = new ASTMJJenil(JJTENIL);
		entetes.jjtAddChild(entete, 0);
		entetes.jjtAddChild(enil, 1);
		ASTMJJvnil vnil = new ASTMJJvnil(JJTVNIL);
		ASTMJJinil inil = new ASTMJJinil(JJTINIL);
		meth.jjtAddChild(rien, 0);
		meth.jjtAddChild(ident, 1);
		meth.jjtAddChild(entetes, 2);
		meth.jjtAddChild(vnil, 3);
		meth.jjtAddChild(inil, 4);
		symbolTable.addIdent("f->int@classe", SORTE.VOID, OBJ.METH);
		memory.enqueue(null);
		memory.declMeth("f->int@classe");
		symbolTable.addIdent("a@f->int", SORTE.INT, OBJ.VAR);

		ast.jjtAccept(interpreter, data);
	}

	@Test(expected = InterpreterException.class)
	public void testAppelIExceptionGetVal() throws VisitorMJJException, SymbolException {
		ASTMJJappelI ast = new ASTMJJappelI(JJTAPPELI);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("f->int@class");
		ASTMJJlistexp listexp = new ASTMJJlistexp(JJTLISTEXP);
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(0);
		ASTMJJexnil exnil = new ASTMJJexnil(JJTEXNIL);
		listexp.jjtAddChild(nbre, 0);
		listexp.jjtAddChild(exnil, 1);
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(listexp, 1);

		ASTMJJmethode meth = new ASTMJJmethode(JJTMETHODE);
		ASTMJJrien rien = new ASTMJJrien(JJTRIEN);
		ASTMJJentetes entetes = new ASTMJJentetes(JJTENTETES);
		ASTMJJentete entete = new ASTMJJentete(JJTENTETE);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident2 = new ASTMJJident(JJTIDENT);
		ident2.jjtSetValue("a@f->int");
		entete.jjtAddChild(entier, 0);
		entete.jjtAddChild(ident2, 1);
		ASTMJJenil enil = new ASTMJJenil(JJTENIL);
		entetes.jjtAddChild(entete, 0);
		entetes.jjtAddChild(enil, 1);
		ASTMJJvnil vnil = new ASTMJJvnil(JJTVNIL);
		ASTMJJinil inil = new ASTMJJinil(JJTINIL);
		meth.jjtAddChild(rien, 0);
		meth.jjtAddChild(ident, 1);
		meth.jjtAddChild(entetes, 2);
		meth.jjtAddChild(vnil, 3);
		meth.jjtAddChild(inil, 4);
		symbolTable.addIdent("f->int@classe", SORTE.VOID, OBJ.METH);
		memory.enqueue(meth);
		memory.declMeth("f->int@classe");
		symbolTable.addIdent("a@f->int", SORTE.INT, OBJ.VAR);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testAffectation() throws VisitorMJJException, SymbolException {
		ASTMJJaffectation ast = new ASTMJJaffectation(JJTAFFECTATION);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(5);
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(nbre, 1);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		memory.enqueue(0);
		memory.declVar("a@main");

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals(5, memory.getVal("a@main"));
	}

	@Test
	public void testAffectationTab() throws VisitorMJJException, SymbolException, HeapException, StackException {
		ASTMJJaffectation ast = new ASTMJJaffectation(JJTAFFECTATION);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJident ident2 = new ASTMJJident(JJTIDENT);
		ident2.jjtSetValue("b@main");
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(ident2, 1);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("a@main");
		memory.affectValT("a@main", 2, 0);
		symbolTable.addIdent("b@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(1);
		memory.declTab("b@main");
		memory.affectValT("b@main", 5, 0);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals(1, memory.getVal("a@main"));
		Assert.assertEquals(5, memory.getValT("a@main", 0));
	}

	@Test(expected = InterpreterException.class)
	public void testAffectationException() throws VisitorMJJException {
		ASTMJJaffectation ast = new ASTMJJaffectation(JJTAFFECTATION);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(5);
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(nbre, 1);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testTab() throws VisitorMJJException, SymbolException, HeapException, StackException {
		ASTMJJtab ast = new ASTMJJtab(JJTTAB);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre ind = new ASTMJJnbre(JJTNBRE);
		ind.jjtSetValue(0);
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(ind, 1);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(2);
		memory.declTab("a@main");
		memory.affectValT("a@main", 5, 0);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(5, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test(expected = InterpreterException.class)
	public void testTabException() throws VisitorMJJException, SymbolException, HeapException, StackException {
		ASTMJJtab ast = new ASTMJJtab(JJTTAB);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre ind = new ASTMJJnbre(JJTNBRE);
		ind.jjtSetValue(2);
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(ind, 1);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(2);
		memory.declTab("a@main");
		memory.affectValT("a@main", 5, 0);
		data.setMode(InterpreterMode.EVAL);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testAffectationT() throws VisitorMJJException, SymbolException, HeapException {
		ASTMJJaffectation ast = new ASTMJJaffectation(JJTAFFECTATION);
		ASTMJJtab tab = new ASTMJJtab(JJTTAB);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre ind = new ASTMJJnbre(JJTNBRE);
		ind.jjtSetValue(0);
		tab.jjtAddChild(ident, 0);
		tab.jjtAddChild(ind, 1);
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(5);
		ast.jjtAddChild(tab, 0);
		ast.jjtAddChild(nbre, 1);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(2);
		memory.declTab("a@main");

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals(5, memory.getValT("a@main", 0));
	}

	@Test(expected = InterpreterException.class)
	public void testAffectationTException() throws VisitorMJJException {
		ASTMJJaffectation ast = new ASTMJJaffectation(JJTAFFECTATION);
		ASTMJJtab tab = new ASTMJJtab(JJTTAB);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre ind = new ASTMJJnbre(JJTNBRE);
		ind.jjtSetValue(0);
		tab.jjtAddChild(ident, 0);
		tab.jjtAddChild(ind, 1);
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(5);
		ast.jjtAddChild(tab, 0);
		ast.jjtAddChild(nbre, 1);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testSomme() throws VisitorMJJException, SymbolException {
		ASTMJJsomme ast = new ASTMJJsomme(JJTSOMME);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(5);
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(nbre, 1);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		memory.enqueue(1);
		memory.declVar("a@main");

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals(6, memory.getVal("a@main"));
	}

	@Test(expected = InterpreterException.class)
	public void testSommeException() throws VisitorMJJException, SymbolException {
		ASTMJJsomme ast = new ASTMJJsomme(JJTSOMME);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(5);
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(nbre, 1);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.CST);
		memory.enqueue(1);
		memory.declVar("a@main");

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testSommeT() throws VisitorMJJException, SymbolException, HeapException, StackException {
		ASTMJJsomme ast = new ASTMJJsomme(JJTSOMME);
		ASTMJJtab tab = new ASTMJJtab(JJTTAB);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre ind = new ASTMJJnbre(JJTNBRE);
		ind.jjtSetValue(0);
		tab.jjtAddChild(ident, 0);
		tab.jjtAddChild(ind, 1);
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(5);
		ast.jjtAddChild(tab, 0);
		ast.jjtAddChild(nbre, 1);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(2);
		memory.declTab("a@main");
		memory.affectValT("a@main", 1, 0);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals(6, memory.getValT("a@main", 0));
	}

	@Test(expected = InterpreterException.class)
	public void testSommeTException() throws VisitorMJJException, SymbolException, HeapException, StackException {
		ASTMJJsomme ast = new ASTMJJsomme(JJTSOMME);
		ASTMJJtab tab = new ASTMJJtab(JJTTAB);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre ind = new ASTMJJnbre(JJTNBRE);
		ind.jjtSetValue(2);
		tab.jjtAddChild(ident, 0);
		tab.jjtAddChild(ind, 1);
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(5);
		ast.jjtAddChild(tab, 0);
		ast.jjtAddChild(nbre, 1);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(2);
		memory.declTab("a@main");
		memory.affectValT("a@main", 1, 0);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testInc() throws VisitorMJJException, SymbolException {
		ASTMJJincrement ast = new ASTMJJincrement(JJTINCREMENT);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ast.jjtAddChild(ident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		memory.enqueue(0);
		memory.declVar("a@main");

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals(1, memory.getVal("a@main"));
	}

	@Test(expected = InterpreterException.class)
	public void testIncException() throws VisitorMJJException, SymbolException {
		ASTMJJincrement ast = new ASTMJJincrement(JJTINCREMENT);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ast.jjtAddChild(ident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.CST);
		memory.enqueue(0);
		memory.declVar("a@main");

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testIncT() throws VisitorMJJException, SymbolException, HeapException, StackException {
		ASTMJJincrement ast = new ASTMJJincrement(JJTINCREMENT);
		ASTMJJtab tab = new ASTMJJtab(JJTTAB);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre ind = new ASTMJJnbre(JJTNBRE);
		ind.jjtSetValue(0);
		tab.jjtAddChild(ident, 0);
		tab.jjtAddChild(ind, 1);
		ast.jjtAddChild(tab, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(2);
		memory.declTab("a@main");
		memory.affectValT("a@main", 1, 0);

		ast.jjtAccept(interpreter, data);
		Assert.assertEquals(InterpreterMode.DEFAULT, data.getMode());
		Assert.assertEquals(2, memory.getValT("a@main", 0));
	}

	@Test(expected = InterpreterException.class)
	public void testIncTException() throws VisitorMJJException, SymbolException, HeapException, StackException {
		ASTMJJincrement ast = new ASTMJJincrement(JJTINCREMENT);
		ASTMJJtab tab = new ASTMJJtab(JJTTAB);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ASTMJJnbre ind = new ASTMJJnbre(JJTNBRE);
		ind.jjtSetValue(2);
		tab.jjtAddChild(ident, 0);
		tab.jjtAddChild(ind, 1);
		ast.jjtAddChild(tab, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(2);
		memory.declTab("a@main");
		memory.affectValT("a@main", 1, 0);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testNonTrue() throws VisitorMJJException {
		ASTMJJnon ast = new ASTMJJnon(JJTNON);
		ASTMJJvrai vrai = new ASTMJJvrai(JJTVRAI);
		ast.jjtAddChild(vrai, 0);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(false, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testNonFalse() throws VisitorMJJException {
		ASTMJJnon ast = new ASTMJJnon(JJTNON);
		ASTMJJfaux faux = new ASTMJJfaux(JJTFAUX);
		ast.jjtAddChild(faux, 0);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(true, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testEtTrue() throws VisitorMJJException {
		ASTMJJet ast = new ASTMJJet(JJTET);
		ASTMJJvrai vrai = new ASTMJJvrai(JJTVRAI);
		ASTMJJfaux faux = new ASTMJJfaux(JJTFAUX);
		ast.jjtAddChild(vrai, 0);
		ast.jjtAddChild(faux, 1);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(false, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testEtFalse() throws VisitorMJJException {
		ASTMJJet ast = new ASTMJJet(JJTET);
		ASTMJJvrai vrai = new ASTMJJvrai(JJTVRAI);
		ASTMJJfaux faux = new ASTMJJfaux(JJTFAUX);
		ast.jjtAddChild(faux, 0);
		ast.jjtAddChild(vrai, 1);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(false, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testOuTrue() throws VisitorMJJException {
		ASTMJJou ast = new ASTMJJou(JJTOU);
		ASTMJJvrai vrai = new ASTMJJvrai(JJTVRAI);
		ASTMJJfaux faux = new ASTMJJfaux(JJTFAUX);
		ast.jjtAddChild(vrai, 0);
		ast.jjtAddChild(faux, 1);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(true, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testOuFalse() throws VisitorMJJException {
		ASTMJJou ast = new ASTMJJou(JJTOU);
		ASTMJJvrai vrai = new ASTMJJvrai(JJTVRAI);
		ASTMJJfaux faux = new ASTMJJfaux(JJTFAUX);
		ast.jjtAddChild(faux, 0);
		ast.jjtAddChild(vrai, 1);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(true, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testEqualInt() throws VisitorMJJException {
		ASTMJJequal ast = new ASTMJJequal(JJTEQUAL);
		ASTMJJnbre nbre1 = new ASTMJJnbre(JJTNBRE);
		nbre1.jjtSetValue(0);
		ASTMJJnbre nbre2 = new ASTMJJnbre(JJTNBRE);
		nbre2.jjtSetValue(0);
		ast.jjtAddChild(nbre1, 0);
		ast.jjtAddChild(nbre2, 1);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(true, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testEqualBoolean() throws VisitorMJJException {
		ASTMJJequal ast = new ASTMJJequal(JJTEQUAL);
		ASTMJJvrai vrai1 = new ASTMJJvrai(JJTVRAI);
		ASTMJJvrai vrai2 = new ASTMJJvrai(JJTVRAI);
		ast.jjtAddChild(vrai1, 0);
		ast.jjtAddChild(vrai2, 1);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(true, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testSupFalse() throws VisitorMJJException {
		ASTMJJsup ast = new ASTMJJsup(JJTSUP);
		ASTMJJnbre nbre1 = new ASTMJJnbre(JJTNBRE);
		nbre1.jjtSetValue(0);
		ASTMJJnbre nbre2 = new ASTMJJnbre(JJTNBRE);
		nbre2.jjtSetValue(0);
		ast.jjtAddChild(nbre1, 0);
		ast.jjtAddChild(nbre2, 1);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(false, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testSupTrue() throws VisitorMJJException {
		ASTMJJsup ast = new ASTMJJsup(JJTSUP);
		ASTMJJnbre nbre1 = new ASTMJJnbre(JJTNBRE);
		nbre1.jjtSetValue(1);
		ASTMJJnbre nbre2 = new ASTMJJnbre(JJTNBRE);
		nbre2.jjtSetValue(0);
		ast.jjtAddChild(nbre1, 0);
		ast.jjtAddChild(nbre2, 1);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(true, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testMoinsUnaire() throws VisitorMJJException {
		ASTMJJmoinsUnaire ast = new ASTMJJmoinsUnaire(JJTMOINSUNAIRE);
		ASTMJJnbre nbre1 = new ASTMJJnbre(JJTNBRE);
		nbre1.jjtSetValue(1);
		ast.jjtAddChild(nbre1, 0);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(-1, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testPlus() throws VisitorMJJException {
		ASTMJJplus ast = new ASTMJJplus(JJTPLUS);
		ASTMJJnbre nbre1 = new ASTMJJnbre(JJTNBRE);
		nbre1.jjtSetValue(5);
		ASTMJJnbre nbre2 = new ASTMJJnbre(JJTNBRE);
		nbre2.jjtSetValue(3);
		ast.jjtAddChild(nbre1, 0);
		ast.jjtAddChild(nbre2, 1);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(8, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testMoins() throws VisitorMJJException {
		ASTMJJmoins ast = new ASTMJJmoins(JJTMOINS);
		ASTMJJnbre nbre1 = new ASTMJJnbre(JJTNBRE);
		nbre1.jjtSetValue(5);
		ASTMJJnbre nbre2 = new ASTMJJnbre(JJTNBRE);
		nbre2.jjtSetValue(3);
		ast.jjtAddChild(nbre1, 0);
		ast.jjtAddChild(nbre2, 1);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(2, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testMult() throws VisitorMJJException {
		ASTMJJmult ast = new ASTMJJmult(JJTMULT);
		ASTMJJnbre nbre1 = new ASTMJJnbre(JJTNBRE);
		nbre1.jjtSetValue(5);
		ASTMJJnbre nbre2 = new ASTMJJnbre(JJTNBRE);
		nbre2.jjtSetValue(3);
		ast.jjtAddChild(nbre1, 0);
		ast.jjtAddChild(nbre2, 1);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(15, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testDiv() throws VisitorMJJException {
		ASTMJJdiv ast = new ASTMJJdiv(JJTDIV);
		ASTMJJnbre nbre1 = new ASTMJJnbre(JJTNBRE);
		nbre1.jjtSetValue(15);
		ASTMJJnbre nbre2 = new ASTMJJnbre(JJTNBRE);
		nbre2.jjtSetValue(3);
		ast.jjtAddChild(nbre1, 0);
		ast.jjtAddChild(nbre2, 1);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(5, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test(expected = InterpreterException.class)
	public void testDivZero() throws VisitorMJJException {
		ASTMJJdiv ast = new ASTMJJdiv(JJTDIV);
		ASTMJJnbre nbre1 = new ASTMJJnbre(JJTNBRE);
		nbre1.jjtSetValue(15);
		ASTMJJnbre nbre2 = new ASTMJJnbre(JJTNBRE);
		nbre2.jjtSetValue(0);
		ast.jjtAddChild(nbre1, 0);
		ast.jjtAddChild(nbre2, 1);
		data.setMode(InterpreterMode.EVAL);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testVrai() throws VisitorMJJException {
		ASTMJJvrai ast = new ASTMJJvrai(JJTVRAI);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(true, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testFaux() throws VisitorMJJException {
		ASTMJJfaux ast = new ASTMJJfaux(JJTFAUX);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(false, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testAppelE() throws VisitorMJJException, SymbolException {
		ASTMJJappelE ast = new ASTMJJappelE(JJTAPPELE);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("f->int@classe");
		ASTMJJlistexp listexp = new ASTMJJlistexp(JJTLISTEXP);
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(0);
		ASTMJJexnil exnil = new ASTMJJexnil(JJTEXNIL);
		listexp.jjtAddChild(nbre, 0);
		listexp.jjtAddChild(exnil, 1);
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(listexp, 1);

		ASTMJJmethode meth = new ASTMJJmethode(JJTMETHODE);
		ASTMJJentetes entetes = new ASTMJJentetes(JJTENTETES);
		ASTMJJentete entete = new ASTMJJentete(JJTENTETE);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident2 = new ASTMJJident(JJTIDENT);
		ident2.jjtSetValue("a@f->int");
		entete.jjtAddChild(entier, 0);
		entete.jjtAddChild(ident2, 1);
		ASTMJJenil enil = new ASTMJJenil(JJTENIL);
		entetes.jjtAddChild(entete, 0);
		entetes.jjtAddChild(enil, 1);
		ASTMJJvnil vnil = new ASTMJJvnil(JJTVNIL);
		ASTMJJinstrs instrs = new ASTMJJinstrs(JJTINSTRS);
		ASTMJJret ret = new ASTMJJret(JJTRET);
		ret.jjtAddChild(ident2, 0);
		ASTMJJinil inil = new ASTMJJinil(JJTINIL);
		instrs.jjtAddChild(ret, 0);
		instrs.jjtAddChild(inil, 1);
		meth.jjtAddChild(entier, 0);
		meth.jjtAddChild(ident, 1);
		meth.jjtAddChild(entetes, 2);
		meth.jjtAddChild(vnil, 3);
		meth.jjtAddChild(instrs, 4);
		symbolTable.addIdent("C@global", null, OBJ.VAR);
		memory.enqueue(null);
		memory.declMeth("C@global");
		symbolTable.addIdent("f->int@classe", SORTE.VOID, OBJ.METH);
		memory.enqueue(meth);
		memory.declMeth("f->int@classe");
		symbolTable.addIdent("a@f->int", SORTE.INT, OBJ.VAR);
		interpreter.setHasReturned(true);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(0, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
		Assert.assertFalse(interpreter.getHasReturned());
	}

	@Test(expected = InterpreterException.class)
	public void testAppelEExceptionNull() throws VisitorMJJException, SymbolException {
		ASTMJJappelE ast = new ASTMJJappelE(JJTAPPELE);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("f->int@classe");
		ASTMJJlistexp listexp = new ASTMJJlistexp(JJTLISTEXP);
		ASTMJJnbre nbre = new ASTMJJnbre(JJTNBRE);
		nbre.jjtSetValue(0);
		ASTMJJexnil exnil = new ASTMJJexnil(JJTEXNIL);
		listexp.jjtAddChild(nbre, 0);
		listexp.jjtAddChild(exnil, 1);
		ast.jjtAddChild(ident, 0);
		ast.jjtAddChild(listexp, 1);

		ASTMJJmethode meth = new ASTMJJmethode(JJTMETHODE);
		ASTMJJrien rien = new ASTMJJrien(JJTRIEN);
		ASTMJJentetes entetes = new ASTMJJentetes(JJTENTETES);
		ASTMJJentete entete = new ASTMJJentete(JJTENTETE);
		ASTMJJentier entier = new ASTMJJentier(JJTENTIER);
		ASTMJJident ident2 = new ASTMJJident(JJTIDENT);
		ident2.jjtSetValue("a@f->int");
		entete.jjtAddChild(entier, 0);
		entete.jjtAddChild(ident2, 1);
		ASTMJJenil enil = new ASTMJJenil(JJTENIL);
		entetes.jjtAddChild(entete, 0);
		entetes.jjtAddChild(enil, 1);
		ASTMJJvnil vnil = new ASTMJJvnil(JJTVNIL);
		ASTMJJinil inil = new ASTMJJinil(JJTINIL);
		meth.jjtAddChild(rien, 0);
		meth.jjtAddChild(ident, 1);
		meth.jjtAddChild(entetes, 2);
		meth.jjtAddChild(vnil, 3);
		meth.jjtAddChild(inil, 4);
		symbolTable.addIdent("C@global", null, OBJ.VAR);
		memory.enqueue(null);
		memory.declMeth("C@global");
		symbolTable.addIdent("f->int@classe", SORTE.VOID, OBJ.METH);
		memory.enqueue(meth);
		memory.declMeth("f->int@classe");
		symbolTable.addIdent("a@f->int", SORTE.INT, OBJ.VAR);
		interpreter.setHasReturned(true);
		data.setMode(InterpreterMode.EVAL);

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testVoid() throws VisitorMJJException {
		ASTMJJrien ast = new ASTMJJrien(JJTRIEN);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(SORTE.VOID, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testInt() throws VisitorMJJException {
		ASTMJJentier ast = new ASTMJJentier(JJTENTIER);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(SORTE.INT, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testBoolean() throws VisitorMJJException {
		ASTMJJbooleen ast = new ASTMJJbooleen(JJTBOOLEEN);
		data.setMode(InterpreterMode.EVAL);

		Assert.assertEquals(SORTE.BOOL, ast.jjtAccept(interpreter, data));
		Assert.assertEquals(InterpreterMode.EVAL, data.getMode());
	}

	@Test
	public void testLength() throws VisitorMJJException, SymbolException, HeapException {
		ASTMJJlongueur ast = new ASTMJJlongueur(JJTLONGUEUR);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ast.jjtAddChild(ident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(2);
		memory.declTab("a@main");

		Assert.assertEquals(2, ast.jjtAccept(interpreter, data));
	}

	@Test(expected = InterpreterException.class)
	public void testLengthExceptionVar() throws VisitorMJJException, SymbolException {
		ASTMJJlongueur ast = new ASTMJJlongueur(JJTLONGUEUR);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("a@main");
		ast.jjtAddChild(ident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		memory.enqueue(2);
		memory.declVar("a@main");

		ast.jjtAccept(interpreter, data);
	}

	@Test(expected = InterpreterException.class)
	public void testLengthExceptionGetVal() throws VisitorMJJException, SymbolException {
		ASTMJJlongueur ast = new ASTMJJlongueur(JJTLONGUEUR);
		ASTMJJident ident = new ASTMJJident(JJTIDENT);
		ident.jjtSetValue("b@main");
		ast.jjtAddChild(ident, 0);
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		memory.enqueue(2);
		memory.declVar("a@main");

		ast.jjtAccept(interpreter, data);
	}

	@Test
	public void testNodes() throws VisitorMJJException {
		ASTMJJlistexp listexp = new ASTMJJlistexp(JJTLISTEXP);
		listexp.jjtAccept(interpreter, data);
		ASTMJJexnil exnil = new ASTMJJexnil(JJTEXNIL);
		exnil.jjtAccept(interpreter, data);
		SimpleNode node = new SimpleNode(JJTCLASSE);
		Assert.assertNull(node.jjtAccept(interpreter, data));
	}
}
