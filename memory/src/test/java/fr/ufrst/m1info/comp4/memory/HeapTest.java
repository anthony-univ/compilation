package fr.ufrst.m1info.comp4.memory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HeapTest {
	private Heap heap;

	@Before
	public void init() {
		heap = new Heap();
	}

	@Test
	public void testAllocate() throws HeapException {
		heap.allocate(2);
		HeapSymbol symbol = heap.getSymbol(0);
		Assert.assertEquals(2, symbol.getSize());
		Assert.assertEquals(0, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());
		Assert.assertEquals(254, heap.getFreeSize());
	}

	@Test
	public void testAllocateGrow() throws HeapException {
		heap.allocate(500);
		HeapSymbol symbol = heap.getSymbol(0);
		Assert.assertEquals(500, symbol.getSize());
		Assert.assertEquals(0, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());
		Assert.assertEquals(12, heap.getFreeSize());
	}

	@Test
	public void testAllocateMultiple() throws HeapException {
		heap.allocate(7);
		heap.allocate(29);
		heap.allocate(1);
		heap.allocate(72);

		HeapSymbol symbol = heap.getSymbol(0);
		Assert.assertEquals(7, symbol.getSize());
		Assert.assertEquals(0, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());

		symbol = heap.getSymbol(1);
		Assert.assertEquals(29, symbol.getSize());
		Assert.assertEquals(32, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());

		symbol = heap.getSymbol(2);
		Assert.assertEquals(1, symbol.getSize());
		Assert.assertEquals(7, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());

		symbol = heap.getSymbol(3);
		Assert.assertEquals(72, symbol.getSize());
		Assert.assertEquals(128, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());
		Assert.assertEquals(147, heap.getFreeSize());
	}

	@Test
	public void testIncrementReference() throws HeapException {
		heap.allocate(2);
		heap.incrementReference(0);

		HeapSymbol symbol = heap.getSymbol(0);
		Assert.assertEquals(2, symbol.getSize());
		Assert.assertEquals(0, symbol.getAddress());
		Assert.assertEquals(2, symbol.getReferenceCount());
		Assert.assertEquals(254, heap.getFreeSize());
	}

	@Test(expected = HeapException.class)
	public void testIncrementReferenceException() throws HeapException {
		heap.incrementReference(0);
	}

	@Test
	public void testDeallocate() throws HeapException {
		heap.allocate(2);
		heap.setValue(0, 0, 5);
		heap.setValue(0, 1, 2);
		heap.deallocate(0);

		Object[] values = heap.getValues();
		Assert.assertNull(values[0]);
		Assert.assertNull(values[1]);
		Assert.assertEquals(256, heap.getFreeSize());
	}

	@Test(expected = HeapException.class)
	public void testDeallocateException() throws HeapException {
		heap.deallocate(0);
	}

	@Test
	public void testDeallocateMultipleReference() throws HeapException {
		heap.allocate(2);
		heap.setValue(0, 0, 5);
		heap.setValue(0, 1, 2);
		heap.incrementReference(0);
		heap.deallocate(0);

		Object[] values = heap.getValues();
		Assert.assertEquals(5, values[0]);
		Assert.assertEquals(2, values[1]);
		Assert.assertEquals(254, heap.getFreeSize());
	}

	@Test
	public void testIs2Power() {
		Assert.assertTrue(heap.is2Power(1));
		Assert.assertTrue(heap.is2Power(2));
		Assert.assertTrue(heap.is2Power(4));
		Assert.assertTrue(heap.is2Power(8));
		Assert.assertTrue(heap.is2Power(16));
		Assert.assertTrue(heap.is2Power(32));
		Assert.assertTrue(heap.is2Power(64));
		Assert.assertTrue(heap.is2Power(128));
		Assert.assertTrue(heap.is2Power(256));
		Assert.assertTrue(heap.is2Power(512));
		Assert.assertTrue(heap.is2Power(1024));
		Assert.assertTrue(heap.is2Power(2048));

		Assert.assertFalse(heap.is2Power(3));
		Assert.assertFalse(heap.is2Power(6));
		Assert.assertFalse(heap.is2Power(30));
	}

	@Test
	public void testClosest2Power() {
		Assert.assertEquals(0, heap.closest2Power(1));
		Assert.assertEquals(1, heap.closest2Power(2));
		Assert.assertEquals(2, heap.closest2Power(3));
		Assert.assertEquals(3, heap.closest2Power(8));
		Assert.assertEquals(5, heap.closest2Power(17));
		Assert.assertEquals(5, heap.closest2Power(31));
		Assert.assertEquals(8, heap.closest2Power(256));
	}

	@Test
	public void testReferenceGrow() {
		for (int i = 0; i < 150; i++) {
			heap.reference(i, 1);
		}
		Assert.assertEquals(256, heap.getFreeSize());
	}

	@Test
	public void testAllocateDeallocate() throws HeapException {
		heap.allocate(7);
		heap.allocate(29);
		heap.deallocate(0);
		heap.allocate(1);

		HeapSymbol symbol = heap.getSymbol(0);
		Assert.assertEquals(1, symbol.getSize());
		Assert.assertEquals(7, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());

		symbol = heap.getSymbol(1);
		Assert.assertEquals(29, symbol.getSize());
		Assert.assertEquals(32, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());
	}

	@Test
	public void testAllocateReassemble() throws HeapException {
		heap.allocate(7);
		heap.allocate(29);
		heap.allocate(1);
		heap.allocate(72);
		heap.allocate(100);

		HeapSymbol symbol = heap.getSymbol(0);
		Assert.assertEquals(7, symbol.getSize());
		Assert.assertEquals(0, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());

		symbol = heap.getSymbol(1);
		Assert.assertEquals(29, symbol.getSize());
		Assert.assertEquals(8, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());

		symbol = heap.getSymbol(2);
		Assert.assertEquals(1, symbol.getSize());
		Assert.assertEquals(7, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());

		symbol = heap.getSymbol(3);
		Assert.assertEquals(72, symbol.getSize());
		Assert.assertEquals(37, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());

		symbol = heap.getSymbol(4);
		Assert.assertEquals(100, symbol.getSize());
		Assert.assertEquals(128, symbol.getAddress());
		Assert.assertEquals(1, symbol.getReferenceCount());
	}

	@Test
	public void testSearchBlockByAddrExists() {
		heap.allocate(7);
		heap.allocate(29);
		heap.allocate(1);

		Assert.assertEquals(1, heap.searchBlockByAddr(32));
	}

	@Test
	public void testSearchBlockByAddrNotExists() {
		heap.allocate(7);
		heap.allocate(29);
		heap.allocate(1);

		Assert.assertEquals(-1, heap.searchBlockByAddr(8));
	}

	@Test
	public void testSearchFreeBlockByAddrExists() {
		heap.allocate(7);
		heap.allocate(29);
		heap.allocate(1);

		Assert.assertEquals(8, heap.searchFreeBlockByAddr(8));
	}

	@Test
	public void testSearchFreeBlockByAddrNotExists() {
		heap.allocate(7);
		heap.allocate(29);
		heap.allocate(1);

		Assert.assertEquals(1, heap.searchFreeBlockByAddr(66));
	}

	@Test
	public void testGetSetValue() throws HeapException {
		heap.allocate(2);
		heap.setValue(0, 0, 5);
		heap.setValue(0, 1, 2);

		Assert.assertEquals(5, heap.getValue(0, 0));
		Assert.assertEquals(2, heap.getValue(0, 1));
	}

	@Test(expected = HeapException.class)
	public void testGetValueException() throws HeapException {
		heap.getValue(0, 0);
	}

	@Test(expected = HeapException.class)
	public void testGetValueExceptionInd() throws HeapException {
		heap.allocate(2);
		heap.setValue(0, 0, 5);
		heap.setValue(0, 1, 2);

		heap.getValue(0, 2);
	}

	@Test(expected = HeapException.class)
	public void testSetValueException() throws HeapException {
		heap.setValue(0, 0, 1);
	}

	@Test(expected = HeapException.class)
	public void testSetValueExceptionInd() throws HeapException {
		heap.allocate(2);
		heap.setValue(0, 2, 5);
	}

	@Test(expected = HeapException.class)
	public void testGetSymbolException() throws HeapException {
		heap.getSymbol(0);
	}

	@Test
	public void testGetSizeSymbol() throws HeapException {
		heap.allocate(2);
		Assert.assertEquals(2, heap.getSizeSymbol(0));
	}

	@Test(expected = HeapException.class)
	public void testGetSizeSymbolException() throws HeapException {
		heap.getSizeSymbol(0);
	}

	@Test
	public void testToString() throws HeapException {
		heap.allocate(2);
		heap.setValue(0, 0, 5);
		heap.setValue(0, 1, 2);
		heap.allocate(2);
		heap.setValue(1, 0, 1);

		Assert.assertEquals("id 0 : <0,2,1> = [5,2]\nid 1 : <2,2,1> = [1,?]\n", heap.toString());
	}
}
