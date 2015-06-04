/**
 * CS 241: Data Structures and Algorithms II
 * Professor: Edwin Rodr&iacute;guez
 *
 * Programming Assignment #2
 *
 * Simple code of a self balancing Red-Black tree
 * 
 * Jacob Romero
 * 
 */

public interface Tree<K extends Comparable<K>, V> {
	public void add(K key, V value);
	public V remove(K key);
	public V lookup(K key);
	public String toPrettyString();
}
