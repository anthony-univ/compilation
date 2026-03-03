package fr.ufrst.m1info.comp4.memory;

import org.junit.Assert;
import org.junit.Test;

public class ElementTableTest {
    InfoInstance instance = new InfoInstance(3);
    InfoInstance instance2 = new InfoInstance(4);
    ElementTable element = new ElementTable("test", SORTE.INT, OBJ.VAR);
    ElementTable element2 = new ElementTable("test2", SORTE.INT, OBJ.VAR);
    @Test
    public void firstInstanceTest(){
        Assert.assertNull(element.getFirstInstance());

        element.setInstance(instance);

        Assert.assertEquals(instance, element.getFirstInstance());

        element.setInstance(instance2);

        Assert.assertEquals(instance, instance2.getNextInstance());

        Assert.assertEquals(instance2, element.getFirstInstance());
    }

    @Test(expected = SymbolException.class)
    public void removeInstanceTestWithException() throws SymbolException {
        element.removeInstance();
    }

    @Test
    public void removeInstanceTest() throws SymbolException {
        element.setInstance(instance);

        Assert.assertEquals(instance, element.getFirstInstance());

        element.removeInstance();

        Assert.assertNull(element.getFirstInstance());
    }

    @Test
    public void identTest(){
        Assert.assertEquals("test", element.getIdent());
    }

    @Test
    public void getInfoTest(){
        Assert.assertEquals(SORTE.INT, element2.getInfo().getSorte());
        Assert.assertEquals(OBJ.VAR, element2.getInfo().getObj());
    }

    @Test
    public void getInfoIdentTest() throws SymbolException {
        Assert.assertEquals(SORTE.INT, element2.getInfoIdent("test2").getSorte());
        Assert.assertEquals(OBJ.VAR, element2.getInfoIdent("test2").getObj());
    }

    @Test(expected = SymbolException.class)
    public void getInfoIdentTestWithException() throws SymbolException {
        element.getInfoIdent("aaa");
    }

    @Test(expected = SymbolException.class)
    public void addIdentTestWithException() throws SymbolException {
        element.addIdent("test", SORTE.INT, OBJ.VAR);
    }

    @Test
    public void getElementTest() throws SymbolException {
        element.addIdent("test2", SORTE.INT, OBJ.VAR);

        Assert.assertEquals("test2", element.getElement("test2").getIdent());
        Assert.assertEquals(SORTE.INT, element.getElement("test2").getInfo().getSorte());
        Assert.assertEquals(OBJ.VAR, element.getElement("test2").getInfo().getObj());
    }

    @Test(expected = SymbolException.class)
    public void getElementTestWithException() throws SymbolException {
        element.getElement("aaaaa");
    }

    @Test
     public void toStringTest() throws SymbolException {
        element.addIdent("test2", SORTE.INT, OBJ.VAR);

        Assert.assertEquals("[test,var,entier,null]->[test2,var,entier,null]->", element.toString());
    }
}
