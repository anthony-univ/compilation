package fr.ufrst.m1info.comp4.memory;

import java.util.ArrayList;
import java.util.List;

public class Heap {
    private Object[] values;
    private HeapSymbol[] symbolTable;
    private List<Integer>[] freeBlocks;

    private int sizeHeap = 256;
    private int freeSize = sizeHeap;
    private int lastId = 0;
    private int symbolTableSize = 128;
    private List<Integer> freeId;

    public Heap() {
        values = new Object[sizeHeap];
        symbolTable = new HeapSymbol[symbolTableSize];
        int powerSize = closest2Power(sizeHeap) + 1;
        freeBlocks = new ArrayList[powerSize];
        freeId = new ArrayList<>();

        for (int i = 0; i < powerSize; i++) {
            freeBlocks[i] = new ArrayList<>();
        }
        freeBlocks[powerSize - 1].add(0);

        for (int i = 0; i < sizeHeap; i++) {
            values[i] = null;
        }

        for (int i = 0; i < symbolTableSize; i++) {
            symbolTable[i] = null;
        }
    }

    public int getFreeSize() {
        return freeSize;
    }

    public Object[] getValues() {
        return values;
    }

    public int allocate(int size) {
        if (Math.pow(2, closest2Power(size)) > freeSize) {
            grow();
        }

        Integer[] adrSize = searchFirstFreeBlock(size);
        cut(adrSize[0]+ size, adrSize[1] - size);
        freeSize -= size;
        return reference(adrSize[0], size);
    }

    public void incrementReference(int id) throws HeapException {
        HeapSymbol symbol = symbolTable[id];
        if (symbol == null) {
            throw new HeapException(String.format("Cannot find id '%d' in the heap : cannot increment reference", id));
        }
        symbol.incrementReferenceCount();
    }

    public void deallocate(int id) throws HeapException {
        if (symbolTable[id] == null) {
            throw new HeapException(String.format("Cannot find id '%d' in the heap : cannot deallocate", id));
        }
        if (symbolTable[id].getReferenceCount() == 1) {
            int addr = symbolTable[id].getAddress();
            int size = symbolTable[id].getSize();

            // Erase values in the heap
            for (int i = addr; i < addr + size; i++) {
                values[i] = null;
            }

            // Allocate free blocks
            cut(addr, size);

            // Delete the entry in the symbol table
            symbolTable[id] = null;
            freeId.add(id);
            freeSize += size;
        } else {
            symbolTable[id].decrementReferenceCount();
        }
    }

    public Integer[] searchFirstFreeBlock(int size) {
        int adrBlock = 0;
        int sizeBlock = 0;

        for (int i = closest2Power(size); i < freeBlocks.length; i++) {
             if (!freeBlocks[i].isEmpty()) {
                 adrBlock = freeBlocks[i].get(0);
                 freeBlocks[i].remove(0);
                 sizeBlock = (int) Math.pow(2, i);
                 break;
             }
        }

        if (sizeBlock == 0) {
            reassemble();
            Integer[] res = searchFirstFreeBlock(size);
            adrBlock = res[0];
            sizeBlock = res[1];
        }

        return new Integer[] { adrBlock, sizeBlock };
    }

    public boolean is2Power(int size) {
        return ((Math.log(size) / Math.log(2)) % 1) == 0;
    }

    public int closest2Power(int size) {
        return (int) Math.ceil(Math.log(size) / Math.log(2));
    }

    public void cut(int addr, int size) {
        int curr = addr + size;
        while (size > 0) {
            int size2Block = is2Power(size) ? closest2Power((int) (double) size) : closest2Power((int) Math.ceil((double) size / 2));
            int sizeBlock = (int) Math.pow(2, size2Block);
            curr -= sizeBlock;
            freeBlocks[size2Block].add(curr);
            size -= sizeBlock;
        }
    }

    public int reference(int adr, int size) {
        HeapSymbol symbol = new HeapSymbol(adr, size, 1);
        if (!freeId.isEmpty()) {
            int id = freeId.remove(0);
            symbolTable[id] = symbol;
            return id;
        }
        if (lastId == symbolTableSize) {
            growSymbolTable();
        }
        symbolTable[lastId] = symbol;
        return lastId++;
    }

    public void grow() {
        int oldSize = sizeHeap;
        freeBlocks[closest2Power(oldSize)].add(oldSize);

        sizeHeap *= 2;
        growFreeBlocks(closest2Power(sizeHeap));

        Object[] newValues = new Object[sizeHeap];
        for (int i = 0; i < sizeHeap; i++) {
            if (i < oldSize) {
                newValues[i] = values[i];
            } else {
                newValues[i] = null;
            }
        }
        values = newValues;
        freeSize += oldSize;
    }

    public void growFreeBlocks(int power) {
        List<Integer>[] newFreeBlocks = new ArrayList[power + 1];
        for (int i = 0; i < power + 1; i++) {
            newFreeBlocks[i] = new ArrayList<>();
            if (i != power) {
                for (Integer addr : freeBlocks[i]) {
                    newFreeBlocks[i].add(addr);
                }
            }
        }
        freeBlocks = newFreeBlocks;
    }

    public void growSymbolTable() {
        symbolTableSize *= 2;

        HeapSymbol[] newTable = new HeapSymbol[symbolTableSize];
		if (lastId >= 0) {
            System.arraycopy(symbolTable, 0, newTable, 0, lastId);
        }
        symbolTable = newTable;
    }

    private void deleteCreate(int to) {
        // Delete free blocks
        for (List<Integer> freeBlock : freeBlocks) {
            freeBlock.clear();
        }

        // Create new free blocks
        cut(to, sizeHeap - to);
    }

    public void reassemble() {
        // Move all values to the front of the heap
        int to = -1;
        int i = 0;
        while (i < sizeHeap) {
            int id = searchBlockByAddr(i);
            if (id == -1) {
                if (to == -1) {
                    to = i;
                }
                i += searchFreeBlockByAddr(i);
            } else {
                HeapSymbol symbol = symbolTable[id];
                int size = symbol.getSize();
                if (to != -1) {
                    int addr = symbol.getAddress();
                    symbol.setAddress(to);
                    for (int j = 0; j < size; j++) {
                        values[to++] = values[addr + j];
                    }
                }
                i += size;
            }
        }

        if (to != -1) {
            deleteCreate(to);
        }
    }

    public int searchBlockByAddr(int addr) {
        for (int i = 0; i < lastId; i++) {
            if (symbolTable[i] != null && symbolTable[i].getAddress() == addr) {
                return i;
            }
        }
        return -1;
    }

    public int searchFreeBlockByAddr(int addr) {
        for (int i = 0; i < freeBlocks.length; i++) {
            for (int a : freeBlocks[i]) {
                if (a == addr) {
                    return (int) Math.pow(2, i);
                }
            }
        }
        return 1;
    }

    public Object getValue(int id, int ind) throws HeapException {
        HeapSymbol symbol = symbolTable[id];
        if (symbol == null) {
            throw new HeapException(String.format("Cannot find id '%d' in the heap : cannot get value", id));
        }
        if (ind < 0 || ind >= symbol.getSize()) {
            throw new HeapException(String.format("Index '%d' is out of bound : cannot get value", ind));
        }
        return values[symbol.getAddress() + ind];
    }

    public void setValue(int id, int ind, Object val) throws HeapException {
        HeapSymbol symbol = symbolTable[id];
        if (symbol == null) {
            throw new HeapException(String.format("Cannot find id '%d' in the heap : cannot set value", id));
        }
        if (ind < 0 || ind >= symbol.getSize()) {
            throw new HeapException(String.format("Index '%d' is out of bound : cannot set value", ind));
        }
        values[symbol.getAddress() + ind] = val;
    }

    public HeapSymbol getSymbol(int id) throws HeapException {
        HeapSymbol symbol = symbolTable[id];
        if (symbol == null) {
            throw new HeapException(String.format("Cannot find id '%d' in the heap : cannot get symbol", id));
        }
        return symbol;
    }

    public int getSizeSymbol(int id) throws HeapException {
        HeapSymbol symbol = symbolTable[id];
        if (symbol == null) {
            throw new HeapException(String.format("Cannot find id '%d' in the heap : cannot get size", id));
        }
        return symbol.getSize();
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < lastId; i++) {
            if (symbolTable[i] != null) {
                res.append("id ").append(i).append(" : ").append(symbolTable[i].toString()).append(" = [");
                int addr = symbolTable[i].getAddress();
                int size = symbolTable[i].getSize();
                for (int j = addr; j < addr + size; j++) {
                    if (values[j] == null) {
                        res.append("?");
                    } else {
                        res.append(values[j]);
                    }
                    if (j != addr + size - 1) {
                        res.append(",");
                    }
                }
                res.append("]\n");
            }
        }
        return res.toString();
    }
}
