package fr.ufrst.m1info.comp4.memory;

public class HeapSymbol {
    private int address;
    private final int size;
    private int referenceCount;

    public HeapSymbol(int address, int size, int referenceCount) {
        this.address = address;
        this.size = size;
        this.referenceCount = referenceCount;
    }

    public int getAddress() {
        return address;
    }

    public int getSize() {
        return size;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void incrementReferenceCount() {
        this.referenceCount++;
    }

    public void decrementReferenceCount() {
        this.referenceCount--;
    }

    @Override
    public String toString() {
        return '<' + Integer.toString(address) + "," + size + "," + referenceCount + '>';
    }
}
