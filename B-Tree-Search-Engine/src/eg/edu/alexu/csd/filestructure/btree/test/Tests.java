package eg.edu.alexu.csd.filestructure.btree.test;

import static org.junit.Assert.*;

import eg.edu.alexu.csd.filestructure.btree.cs40.SearchEngine;
import org.junit.Test;

import eg.edu.alexu.csd.filestructure.btree.cs40.BTree;

public class Tests {

	@Test
	public void test1() {
		BTree<Integer, Integer> tr = new BTree<>(4);
		for(int i = 0; i < 60; i++) {
			tr.insert(i, i+i);
		}
		for(int i = 0; i < 30; i++) {
			assert(tr.delete(i)==true);
		}
		assert(tr.delete(0) == false);
		System.out.println("Tests.test1()");
	}
	@Test
	public void test2() {
		SearchEngine searchEngine = new SearchEngine(4);
		searchEngine.indexWebPage("res\\wiki_00");
		assertFalse(searchEngine.getbTree().getRoot() == null);
		searchEngine.deleteWebPage("res\\wiki_00");
		assertTrue(searchEngine.getbTree().getRoot() == null);
	}

}
