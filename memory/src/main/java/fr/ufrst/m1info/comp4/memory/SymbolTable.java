package fr.ufrst.m1info.comp4.memory;

public class SymbolTable {

    private static final int MAX_SIZE = 131;
    private final ElementTable[] table;

    public SymbolTable() {
        this.table = new ElementTable[MAX_SIZE];
        for (int i = 0; i < MAX_SIZE; i++) {
            this.table[i] = null;
        }
    }

    public void addIdent(String ident, SORTE sorte, OBJ obj) throws SymbolException {
        int hash = hash(ident);
        if(this.table[hash] == null) {
            this.table[hash] = new ElementTable(ident, sorte, obj);
        } else {
            this.table[hash].addIdent(ident, sorte, obj);
        }
    }

    public InfoIdent getInfoIdent(String ident) throws SymbolException {
        int hash = hash(ident);

        ElementTable curr = this.table[hash];
        if(curr == null) {
            throw new SymbolException(String.format("symbol '%s' doesn't exist : cannot get", ident));
        }

        return curr.getInfoIdent(ident);
    }

    public ElementTable getElement(String ident) throws SymbolException {
        int hash = hash(ident);

        ElementTable curr = this.table[hash];
        if(curr == null) {
            throw new SymbolException(String.format("symbol '%s' doesn't exist : cannot get", ident));
        }
        return curr.getElement(ident);
    }

    public OBJ getObj(String id) throws SymbolException {
        return getInfoIdent(id).getObj();
    }

    public int hash(String ident) {
        int hash = 0;
        for (int i = 0; i < ident.length(); i++) {
                hash = hash * 33  ^ ((int) ident.charAt(i));
        }
        return Math.abs(hash)%MAX_SIZE;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("SymbolTable : \n");
        for (int i = 0; i < MAX_SIZE; i++) {
            if (this.table[i] != null) {
                res.append("[").append(i).append("]\n\t").append(this.table[i].toString()).append("\n");
            }
        }

        return res.toString();
    }

}
