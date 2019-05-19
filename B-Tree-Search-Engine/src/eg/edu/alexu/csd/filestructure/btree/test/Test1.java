package eg.edu.alexu.csd.filestructure.btree.test;

import static org.junit.Assert.*;

import org.junit.Test;

import eg.edu.alexu.csd.filestructure.btree.cs40.BTree;

public class Test1 {

	@Test
	public void test() {
		BTree<Integer, Integer> tr = new BTree<>(4);
		for(int i = 0; i < 60; i++) {
			tr.insert(i, i+i);
		}
		for(int i = 0; i < 30; i++) {
			assert(tr.delete(i)==true);
		}
		assert(tr.delete(0) == false);
		System.out.println("Test1.test()");
	}

}
