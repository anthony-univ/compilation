package fr.ufrst.m1info.comp4.memory;

public class ElementTable {
    private final String ident;
    private final InfoIdent info;
    private ElementTable next;
    private InfoInstance firstInstance;

    public ElementTable(String ident, SORTE sorte, OBJ obj) {
        this.ident = ident;
        this.next = null;
        this.info = new InfoIdent(sorte, obj);
        this.firstInstance = null;
    }

    public InfoInstance getFirstInstance() {
        return this.firstInstance;
    }

    public void addIdent(String ident, SORTE sorte, OBJ obj) throws SymbolException {
        ElementTable curr = this;
        while (curr != null) {
            if (curr.ident.equals(ident)) {
                throw new SymbolException(String.format("symbol '%s' already exists", ident));
            }
            curr = curr.next;
        }
        this.next = new ElementTable(ident , sorte, obj);
    }

    public InfoIdent getInfo() {
        return info;
    }

    public InfoIdent getInfoIdent(String ident) throws SymbolException {
        ElementTable curr = this;
        while (curr != null) {
            if (curr.ident.equals(ident)) {
                return curr.info;
            }
            curr = curr.next;
        }

        throw new SymbolException(String.format("symbol '%s' doesn't exist", ident));
    }

    public ElementTable getElement(String ident) throws SymbolException {
        ElementTable curr = this;
        while (curr != null) {
            if (curr.ident.equals(ident)) {
                return curr;
            }
            curr = curr.next;
        }
        throw new SymbolException(String.format("symbol '%s' doesn't exist", ident));
    }

    public String getIdent() {
        return this.ident;
    }

    public void setInstance(InfoInstance infoInstance) {
        if (this.firstInstance != null) {
            infoInstance.setNextInstance(firstInstance);
        }
        this.firstInstance = infoInstance;
    }
    public InfoInstance removeInstance() throws SymbolException {
        if (this.firstInstance == null) {
            throw new SymbolException(String.format("symbol '%s' has no instance", ident));
        } else {
            InfoInstance tmp = this.firstInstance;
            this.firstInstance = this.firstInstance.getNextInstance();
            return tmp;
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        ElementTable curr = this;
        while (curr != null) {
            s.append("[").append(curr.ident).append(",").append(curr.info).append(",").append(curr.firstInstance).append("]->");
            curr = curr.next;
        }
        return s.toString();
    }
}
