package fr.ufrst.m1info.comp4.memory;

public class InfoIdent {
    private SORTE sorte;
    private OBJ obj;

    public InfoIdent(SORTE sorte, OBJ obj) {
        this.sorte = sorte;
        this.obj = obj;
    }

    public SORTE getSorte() {
        return sorte;
    }

    public void setSorte(SORTE sorte) {
        this.sorte = sorte;
    }

    public OBJ getObj() {
        return obj;
    }

    public void setObj(OBJ obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return obj + "," +
                ((sorte==null) ? "OMEGA" : sorte);
    }
}
