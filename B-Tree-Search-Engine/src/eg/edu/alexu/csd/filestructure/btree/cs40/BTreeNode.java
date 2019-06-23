package eg.edu.alexu.csd.filestructure.btree.cs40;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import eg.edu.alexu.csd.filestructure.btree.IBTreeNode;

public class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {
    /**
     * for check this node is boolean or not.
     */
    private boolean leaf;
    /**
     * the keys that separate the range of children.
     */
    private List<K> keys;
    /**
     * values of all keys.
     */
    private List<V> values;
    /**
     * list of children BTreeNodes of this node.
     */
    private List<IBTreeNode<K, V>> children;

    /**
     * constructor.
     */
    BTreeNode() {
        this.leaf = false;
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    @Override
    public int getNumOfKeys() {
        return this.keys.size();
    }

    @Override
    public void setNumOfKeys(int numOfKeys) {
        while (this.keys.size() > numOfKeys) {
            this.keys.remove(this.keys.size() - 1);
            this.values.remove(this.values.size() - 1);

        }
        if (!this.leaf)
            while (this.children.size() > numOfKeys + 1) {
                this.children.remove(this.children.size() - 1);
            }
    }

    @Override
    public boolean isLeaf() {
        return this.leaf;
    }

    @Override
    public void setLeaf(boolean isLeaf) {
        this.leaf = isLeaf;
    }

    @Override
    public List<K> getKeys() {
        return this.keys;
    }

    @Override
    public void setKeys(List<K> keys) {
        if (keys == null)
            throw new RuntimeErrorException(null);
        this.keys.clear();
        this.keys.addAll(keys);
    }

    @Override
    public List<V> getValues() {
        return this.values;
    }

    @Override
    public void setValues(List<V> values) {
        if (values == null)
            throw new RuntimeErrorException(null);
        this.values.clear();
        this.values.addAll(values);
    }

    @Override
    public List<IBTreeNode<K, V>> getChildren() {
        return this.children;
    }

    @Override
    public void setChildren(List<IBTreeNode<K, V>> children) {
        if (children == null)
            throw new RuntimeErrorException(null);
        this.children.addAll(children);
    }

}
