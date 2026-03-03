package fr.ufrst.m1info.comp4.memory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MemoryTest {
	private Memory mem;
	private SymbolTable symbolTable;

	@Before
	public void init() {
		mem = new Memory();
		symbolTable = mem.getSymbolTable();
	}

    @Test
    public void declVarTest() throws SymbolException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(null);
		mem.declVar("a@main");
        Assert.assertEquals(1, mem.getStackLength());
		Assert.assertEquals(OBJ.VAR, mem.getObj("a@main"));
    }

	@Test
	public void declCstTest() throws SymbolException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.CST);
		mem.enqueue(5);
		mem.declCst("a@main");
		Assert.assertEquals(1, mem.getStackLength());
		Assert.assertEquals(OBJ.CST, mem.getObj("a@main"));
	}

	@Test
	public void declTabTest() throws SymbolException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		mem.enqueue(2);
		mem.declTab("a@main");
		Assert.assertEquals(1, mem.getStackLength());
		Assert.assertEquals(OBJ.TAB, mem.getObj("a@main"));
	}

	@Test(expected = HeapException.class)
	public void declTabExceptionNullTest() throws SymbolException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		mem.enqueue(null);
		mem.declTab("a@main");
	}

	@Test(expected = HeapException.class)
	public void declTabExceptionNegTest() throws SymbolException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		mem.enqueue(-1);
		mem.declTab("a@main");
	}

	@Test
	public void declMethTest() throws SymbolException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.METH);
		mem.enqueue(null);
		mem.declMeth("a@main");
		Assert.assertEquals(1, mem.getStackLength());
		Assert.assertEquals(OBJ.METH, mem.getObj("a@main"));
	}

	@Test
	public void identValTest() throws SymbolException, StackException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(5);
		symbolTable.addIdent("b@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(2);
		mem.declVar("b@main");
		mem.identVal("a@main", 1);
		Assert.assertEquals(5, mem.getVal("a@main"));
	}

	@Test(expected = StackException.class)
	public void identValExceptionTest() throws SymbolException, StackException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.identVal("a@main", 0);
	}

	@Test
	public void removeDeclBottomTest() throws SymbolException, StackException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(5);
		mem.declVar("a@main");
		mem.removeDecl("a@main");
		Assert.assertTrue(mem.stackIsEmpty());
	}

	@Test(expected = StackException.class)
	public void removeDeclExceptionTest() throws SymbolException, StackException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.removeDecl("a@main");
	}

	@Test
	public void removeDeclTopTest() throws SymbolException, StackException, HeapException {
		symbolTable.addIdent("b@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(2);
		mem.declVar("b@main");
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(5);
		mem.declVar("a@main");
		mem.removeDecl("a@main");
		Assert.assertEquals(1, mem.getStackLength());
	}

	@Test
	public void removeDeclTest() throws SymbolException, StackException, HeapException {
		symbolTable.addIdent("b@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(2);
		mem.declVar("b@main");
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(5);
		mem.declVar("a@main");
		symbolTable.addIdent("c@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(3);
		mem.declVar("c@main");
		mem.removeDecl("a@main");
		Assert.assertEquals(2, mem.getStackLength());
	}

	@Test
	public void removeDeclTabTest() throws SymbolException, StackException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		mem.enqueue(5);
		mem.declTab("a@main");
		mem.removeDecl("a@main");
		Assert.assertTrue(mem.stackIsEmpty());
		Assert.assertEquals(256, mem.getHeap().getFreeSize());
	}

	@Test
	public void affectValVarTest() throws SymbolException, StackException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(5);
		mem.declVar("a@main");
		mem.affectVal("a@main", 1);
		Assert.assertEquals(1, mem.getVal("a@main"));
	}

	@Test(expected = SymbolException.class)
	public void affectValExceptionCstTest() throws SymbolException, StackException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.CST);
		mem.enqueue(5);
		mem.declCst("a@main");
		mem.affectVal("a@main", 1);
	}

	@Test
	public void affectValVcstTest() throws SymbolException, StackException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VCST);
		mem.enqueue(null);
		mem.declCst("a@main");
		mem.affectVal("a@main", 1);
		Assert.assertEquals(1, mem.getVal("a@main"));
		Assert.assertEquals(OBJ.CST, mem.getObj("a@main"));
	}

	@Test(expected = StackException.class)
	public void affectValExceptionNullTest() throws SymbolException, StackException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.CST);
		mem.affectVal("a@main", 1);
	}

	@Test(expected = StackException.class)
	public void affectValTExceptionNullTest() throws SymbolException, StackException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		mem.affectValT("a@main", 1, 0);
	}

	@Test(expected = SymbolException.class)
	public void affectValTExceptionNotTabTest() throws SymbolException, StackException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.CST);
		mem.enqueue(5);
		mem.declCst("a@main");
		mem.affectValT("a@main", 1, 0);
	}

	@Test
	public void affectValTTest() throws SymbolException, StackException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		mem.enqueue(5);
		mem.declTab("a@main");
		mem.affectValT("a@main", 1, 0);
		Assert.assertEquals(1, mem.getValT("a@main", 0));
	}

	@Test(expected = SymbolException.class)
	public void getValExceptionTest() throws SymbolException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(null);
		mem.declVar("a@main");
		mem.getVal("a@main");
	}

	@Test(expected = SymbolException.class)
	public void getValTExceptionTest() throws SymbolException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		mem.enqueue(1);
		mem.declTab("a@main");
		mem.getValT("a@main", 0);
	}

	@Test
	public void getClassNameTest() throws StackException, SymbolException {
		symbolTable.addIdent("a@class", SORTE.INT, OBJ.VAR);
		mem.enqueue(1);
		mem.declVar("a@class");
		Assert.assertEquals("a@class", mem.getNameClass());
	}

	@Test(expected = StackException.class)
	public void getClassNameExceptionTest() throws StackException, SymbolException {
		symbolTable.addIdent("a@class", SORTE.INT, OBJ.VAR);
		mem.getNameClass();
	}

	@Test
	public void getValClassTest() throws StackException, SymbolException {
		symbolTable.addIdent("a@class", SORTE.INT, OBJ.VAR);
		mem.enqueue(1);
		mem.declVar("a@class");
		Assert.assertEquals(1, mem.getValClass());
	}

	@Test(expected = StackException.class)
	public void getValClassExceptionTest() throws StackException, SymbolException {
		symbolTable.addIdent("a@class", SORTE.INT, OBJ.VAR);
		mem.getValClass();
	}

	@Test
	public void getArraySizeTest() throws SymbolException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		mem.enqueue(5);
		mem.declTab("a@main");
		Assert.assertEquals(5, mem.getArraySize(0));
	}

	@Test
	public void dequeueTest() throws StackException, SymbolException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(1);
		mem.declVar("a@main");
		mem.dequeue();
		Assert.assertTrue(mem.stackIsEmpty());
	}

	@Test
	public void dequeue1Test() throws StackException, SymbolException, HeapException {
		mem.enqueue(1);
		mem.dequeue();
		Assert.assertTrue(mem.stackIsEmpty());
	}

	@Test
	public void dequeue2Test() throws StackException, SymbolException, HeapException {
		mem.enqueue(1);
		mem.enqueue(2);
		mem.dequeue();
		Assert.assertEquals(1, mem.getStackLength());
	}

	@Test(expected = StackException.class)
	public void dequeueExceptionTest() throws StackException, SymbolException, HeapException {
		mem.dequeue();
	}

	@Test
	public void swapTest() throws StackException, SymbolException, HeapException {
		mem.enqueue(1);
		mem.enqueue(2);
		mem.swap();
		Assert.assertEquals(2, mem.getStackLength());
		Assert.assertEquals(1, mem.dequeue());
		Assert.assertEquals(2, mem.dequeue());
	}

	@Test(expected = StackException.class)
	public void swapExceptionNullTest() throws StackException {
		mem.swap();
	}

	@Test(expected = StackException.class)
	public void swapException1ValTest() throws StackException {
		mem.enqueue(1);
		mem.swap();
	}

	@Test
	public void toStringTest() throws SymbolException {
		symbolTable.addIdent("b@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(2);
		mem.declVar("b@main");
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(5);
		mem.declVar("a@main");
		Assert.assertEquals("SymbolTable : \n[12]\n\t[b@main,var,entier,(2)-]->\n[69]\n\t[a@main,var,entier,(5)-]->\n", mem.toString());
	}

	@Test
	public void toStringTopStackTest() throws SymbolException {
		symbolTable.addIdent("b@main", null, OBJ.VAR);
		mem.enqueue(null);
		mem.declVar("b@main");
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(5);
		mem.declVar("a@main");
		Assert.assertEquals("<a@main,5,var,entier>\n<b@main,omega,var,omega>\n", mem.toStringTopStack());
	}

	@Test
	public void toStringHeapTest() throws SymbolException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		mem.enqueue(5);
		mem.declTab("a@main");
		Assert.assertEquals("id 0 : <0,5,1> = [?,?,?,?,?]\n", mem.toStringHeap());
	}

	@Test
	public void clearTest() throws SymbolException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.VAR);
		mem.enqueue(5);
		mem.declVar("a@main");
		mem.clear(symbolTable);
		Assert.assertTrue(mem.stackIsEmpty());
	}

	@Test
	public void deallocateHeapTest() throws SymbolException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		mem.enqueue(5);
		mem.declTab("a@main");
		mem.deallocateHeap(0);
		Assert.assertEquals(256, mem.getHeap().getFreeSize());
	}

	@Test
	public void incrementReferenceHeapTest() throws SymbolException, HeapException {
		symbolTable.addIdent("a@main", SORTE.INT, OBJ.TAB);
		mem.enqueue(5);
		mem.declTab("a@main");
		mem.incrementReferenceHeap(0);
		Assert.assertEquals(251, mem.getHeap().getFreeSize());
	}

	@Test
	public void setSymbolTableTest() {
		mem.setSymbolTable(symbolTable);
		Assert.assertEquals(symbolTable, mem.getSymbolTable());
	}

	@Test
	public void sorteToStringTest() {
		SORTE entier = SORTE.INT;
		SORTE bool = SORTE.BOOL;
		SORTE vide = SORTE.VOID;
		SORTE any = SORTE.ANY;
		Assert.assertEquals("entier", entier.toString());
		Assert.assertEquals("booleen", bool.toString());
		Assert.assertEquals("rien", vide.toString());
		Assert.assertEquals("undefined", any.toString());
	}
}
