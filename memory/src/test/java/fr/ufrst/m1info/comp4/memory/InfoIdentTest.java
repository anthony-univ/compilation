package fr.ufrst.m1info.comp4.memory;

import org.junit.Assert;
import org.junit.Test;

public class InfoIdentTest {
    InfoIdent ident = new InfoIdent(SORTE.INT, OBJ.VAR);

    @Test
    public void sorteTest(){
        Assert.assertEquals(SORTE.INT, ident.getSorte());

        ident.setSorte(SORTE.BOOL);

        Assert.assertEquals(SORTE.BOOL, ident.getSorte());
    }

    @Test
    public void objTest(){
        Assert.assertEquals(OBJ.VAR, ident.getObj());

        ident.setObj(OBJ.METH);

        Assert.assertEquals(OBJ.METH, ident.getObj());
    }
}
