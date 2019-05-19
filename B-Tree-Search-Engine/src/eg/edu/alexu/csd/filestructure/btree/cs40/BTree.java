package eg.edu.alexu.csd.filestructure.btree.cs40;

import java.util.ArrayList;

import javax.management.RuntimeErrorException;

import eg.edu.alexu.csd.filestructure.btree.IBTree;
import eg.edu.alexu.csd.filestructure.btree.IBTreeNode;
import javafx.util.Pair;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {
	/**
	 * the root of the BTree.
	 */
	private IBTreeNode<K, V> root;

	/**
	 * minimum degree.
	 */
	private int t;
	
	private int size;
	public BTree(int t) {
		if (t <= 1)
			throw new RuntimeErrorException(null);
		this.t = t;
		this.root = new BTreeNode<>();
		this.root.setLeaf(true);
	}

	@Override
	public int getMinimumDegree() {
		return this.t;
	}

	@Override
	public IBTreeNode<K, V> getRoot() {
		return this.root.getNumOfKeys() == 0 ? null : this.root;
	}

	@Override
	public void insert(K key, V value) {
		if (key == null || value == null)
			throw new RuntimeErrorException(null);
		IBTreeNode<K, V> r = this.root;
		if (r.getNumOfKeys() == 2 * this.t - 1) {
			IBTreeNode<K, V> newNode = new BTreeNode<>();
			this.root = newNode;
			newNode.getChildren().add(r);
			this.split(newNode, 0);
		}
		insertNonFull(this.root, key, value);
	}

	private void insertNonFull(IBTreeNode<K, V> r, K key, V value) {
		int size = r.getNumOfKeys();
		int i = size - 1;
		for (; i >= 0 && key.compareTo(r.getKeys().get(i)) < 0; i--)
			;
		i++;
		if (r.isLeaf()) {
			if (i != 0 && r.getKeys().get(i - 1).equals(key)) {// Duplicate key
				return;
			}
			r.getKeys().add(i, key);
			r.getValues().add(i, value);
			this.size++;
		} else {

			if (i != 0 && r.getKeys().get(i - 1).equals(key)) {
				r.getValues().remove(i - 1);
				r.getValues().add(i - 1, value);
				return;
			}
			IBTreeNode<K, V> ci = r.getChildren().get(i);
			if (ci.getNumOfKeys() == 2 * t - 1) {
				split(r, i);
				if (key.compareTo(r.getKeys().get(i)) > 0)
					i++;
				else if (r.getKeys().get(i).equals(key)) {
					return;
				}

			}

			insertNonFull(r.getChildren().get(i), key, value);
		}
	}

	private void split(IBTreeNode<K, V> x, int i) {
		IBTreeNode<K, V> right = new BTreeNode<>();
		IBTreeNode<K, V> left = x.getChildren().get(i);
		right.setLeaf(left.isLeaf());
		for (int j = 0; j < this.t - 1; j++) {// add second half of ci this.to right
			right.getKeys().add(left.getKeys().get(j + this.t));
			right.getValues().add(left.getValues().get(j + this.t));
		}
		if (!left.isLeaf()) {// if ci internal node add second half children of ci to right
			for (int j = 0; j < this.t; j++) {
				right.getChildren().add(left.getChildren().get(j + this.t));
			}
		}
		x.getKeys().add(i, left.getKeys().get(this.t - 1));
		x.getValues().add(i, left.getValues().get(this.t - 1));
		x.getChildren().add(i + 1, right);
		left.setNumOfKeys(this.t - 1);

	}

	@Override
	public V search(K key) {
		if (key == null || this.getRoot() == null)
			throw new RuntimeErrorException(null);
		return search(this.root, key);
	}

	private V search(IBTreeNode<K, V> x, K key) {

		int s = x.getNumOfKeys();
		ArrayList<K> keys = (ArrayList<K>) x.getKeys();
		int i = 0;
		for (; i < s && key.compareTo(keys.get(i)) > 0; i++)
			;
		if (i < s && key.equals(keys.get(i))) {
			return x.getValues().get(i);
		}
		if (x.isLeaf()) {
			return null;
		} else {
			return search(x.getChildren().get(i), key);
		}
	}

	@Override
	public boolean delete(K key) {
		if (key == null)
			throw new RuntimeErrorException(null);
		return delete(this.root, key);
	}

	private boolean delete(IBTreeNode<K, V> x, K key) {
		int size = x.getNumOfKeys();
		int i = 0;
		// we iterate until size - 1 to stop in last key if reached
		for (; i < size - 1 && key.compareTo(x.getKeys().get(i)) > 0; i++);
		boolean equal = key.equals(x.getKeys().get(i));

		if (x.isLeaf() && equal) {
			x.getKeys().remove(i);
			x.getValues().remove(i);
			this.size --;
			return true;
		} else if (equal) {
			IBTreeNode<K, V> y = x.getChildren().get(i);// the child that precedes key
			IBTreeNode<K, V> z = x.getChildren().get(i + 1);// the child that follows key

			if (y.getNumOfKeys() >= this.t) {
				Pair<K, V> predecessor = maximum(y);
				delete(y, predecessor.getKey());
				x.getKeys().remove(i);
				x.getValues().remove(i);
				x.getKeys().add(i, predecessor.getKey());
				x.getValues().add(i, predecessor.getValue());
			} else if (z.getNumOfKeys() >= this.t) {
				Pair<K, V> successor = minimum(z);
				delete(z, successor.getKey());
				x.getKeys().remove(i);
				x.getValues().remove(i);
				x.getKeys().add(i, successor.getKey());
				x.getValues().add(i, successor.getValue());
			} else {
				merge(x, i);
				delete(y, key);
			}
			return true;
		} else if (!x.isLeaf()) {
			IBTreeNode<K, V> target;
			int k = i;
			if (key.compareTo(x.getKeys().get(i)) > 0) {// case keys in last child
				k++;
			}
			target = x.getChildren().get(k);


			if (target.getNumOfKeys() == t - 1) {
				boolean l = hasLeft(x, k), r = hasRight(x, k);
				if (r && x.getChildren().get(k + 1).getNumOfKeys() >= this.t) {
					IBTreeNode<K, V> right = x.getChildren().get(k + 1);
					// move key from x to target
					target.getKeys().add(x.getKeys().get(k));
					target.getValues().add(x.getValues().get(k));
					x.getKeys().remove(k);
					x.getValues().remove(k);
					// move key from right to x
					x.getKeys().add(k, right.getKeys().get(0));
					x.getValues().add(k, right.getValues().get(0));
					right.getKeys().remove(0);
					right.getValues().remove(0);
					//move left most child of right to left most child of target 
					if(!right.isLeaf()) {
						target.getChildren().add(right.getChildren().get(0));
						right.getChildren().remove(0);
					}
					return delete(target, key);
				} else if (l && x.getChildren().get(k - 1).getNumOfKeys() >= this.t) {
					IBTreeNode<K, V> left = x.getChildren().get(k - 1);
					// move key from x to target
					target.getKeys().add(0, x.getKeys().get(k-1));
					target.getValues().add(0, x.getValues().get(k-1));
					x.getKeys().remove(k-1);
					x.getValues().remove(k-1);
					// move key from left to x
					int s = left.getNumOfKeys();
					x.getKeys().add(k-1, left.getKeys().get(s - 1));
					x.getValues().add(k-1, left.getValues().get(s - 1));
					left.getKeys().remove(s - 1);
					left.getValues().remove(s - 1);
					//move right most child of left to right most child of target
					if(!left.isLeaf()) {
						target.getChildren().add(0,left.getChildren().get(s));
						left.getChildren().remove(s);
					}
					return delete(target, key);
				} else if (r) {
					merge(x, k);
					return delete(target, key);
				} else if (l) {
					merge(x, k - 1);
					return delete(x.getChildren().get(k - 1), key);
				}
			} else {
				return delete(target, key);
			}
		}

		return false;
	}

	private boolean hasLeft(IBTreeNode<K, V> x, int k) {
		if (k == 0)
			return false;
		return true;
	}

	private boolean hasRight(IBTreeNode<K, V> x, int k) {
		if (k == x.getNumOfKeys())
			return false;
		return true;
	}

	/**
	 * merge two children of x.
	 * 
	 * @param x a node that have two children can be merged.
	 * @param i the index of a key which the previous child and next child of that
	 *          key ,that their indexes(i, i+1), will be deleted.
	 */
	public void merge(IBTreeNode<K, V> x, int i) {
		// get left & right children around ki
		IBTreeNode<K, V> right = x.getChildren().get(i + 1);
		IBTreeNode<K, V> left = x.getChildren().get(i);
		K ki = x.getKeys().get(i);
		V vi = x.getValues().get(i);
		// merge ki,left & right into left
		left.getKeys().add(ki);
		left.getValues().add(vi);
		right.getKeys().forEach(k -> left.getKeys().add(k));
		right.getValues().forEach(v -> left.getValues().add(v));

		if (!right.isLeaf()) {
			for (IBTreeNode<K, V> bnode : right.getChildren()) {
				left.getChildren().add(bnode);
			}
		}
		x.getKeys().remove(i);
		x.getValues().remove(i);
		x.getChildren().remove(i + 1);
		if(x == this.root && x.getNumOfKeys() == 0)
			this.root = left;

	}

	/**
	 * get minimum key of a subtree rooted at x.
	 * 
	 * @param x the root of a subtree
	 * @return minimum keys
	 */
	private Pair<K, V> minimum(IBTreeNode<K, V> x) {
		if (x.isLeaf())
			return new Pair<K, V>(x.getKeys().get(0), x.getValues().get(0));
		return minimum(x.getChildren().get(0));
	}

	/**
	 * get maximum key of a subtree rooted at x.
	 * 
	 * @param x the root of a subtree
	 * @return maximum keys
	 */
	private Pair<K, V> maximum(IBTreeNode<K, V> x) {
		if (x.isLeaf())
			return new Pair<K, V>(x.getKeys().get(x.getNumOfKeys() - 1), x.getValues().get(x.getNumOfKeys() - 1));
		return maximum(x.getChildren().get(x.getNumOfKeys()));
	}

}
