package fr.ufrst.m1info.comp4.memory;

import org.junit.Assert;
import org.junit.Test;

public class InfoInstanceTest {

    InfoInstance instance = new InfoInstance(3);
    InfoInstance instance2 = new InfoInstance(4);
    @Test
    public void nextStackTest(){
        instance.setNextStack(instance2);

        Assert.assertEquals(instance2, instance.getNextStack());
    }

    @Test
    public void prevStackTest(){
        instance.setPrevStack(instance2);

        Assert.assertEquals(instance2, instance.getPrevStack());
    }

    @Test
    public void identTest(){
        ElementTable e = new ElementTable("test", SORTE.INT, OBJ.VAR);
        instance.setIdent(e);

        Assert.assertEquals(e, instance.getElementTable());
    }

    @Test
    public void nextInstanceTest(){
        instance.setNextInstance(instance2);

        Assert.assertEquals(instance2, instance.getNextInstance());
    }

    @Test
    public void valueTest(){
        Assert.assertEquals(3, instance.getVal());
        instance.setVal(4);
        Assert.assertEquals(4, instance.getVal());
    }

    @Test
    public void toStringTest(){
        Assert.assertEquals("(3)-", instance.toString());
    }
}
