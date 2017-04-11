/************************************************************************************
 * Copyright (c) 2015 /// Project SWG /// www.projectswg.com                        *
 *                                                                                  *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on           *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies.  *
 * Our goal is to create an emulator which will provide a server for players to     *
 * continue playing a game similar to the one they used to play. We are basing      *
 * it on the final publish of the game prior to end-game events.                    *
 *                                                                                  *
 * This file is part of Holocore.                                                   *
 *                                                                                  *
 * -------------------------------------------------------------------------------- *
 *                                                                                  *
 * Holocore is free software: you can redistribute it and/or modify                 *
 * it under the terms of the GNU Affero General Public License as                   *
 * published by the Free Software Foundation, either version 3 of the               *
 * License, or (at your option) any later version.                                  *
 *                                                                                  *
 * Holocore is distributed in the hope that it will be useful,                      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                    *
 * GNU Affero General Public License for more details.                              *
 *                                                                                  *
 * You should have received a copy of the GNU Affero General Public License         *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.                *
 *                                                                                  *
 ***********************************************************************************/
package com.projectswg.common.control;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Queue;

class IntentQueue<E> implements Queue<E> {
	
	private final Node head;
	private int size;
	private int modificationCount;
	
	public IntentQueue() {
		head = new Node(null, null, null); // Left = Forward, Right = Reverse
		head.left = head;
		head.right = head;
		modificationCount = 0;
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	
	@Override
	public boolean contains(Object o) {
		Node n = head;
		while (n.left != head) {
			if (n.left.value == o)
				return true;
		}
		return false;
	}
	
	@Override
	public Iterator iterator() {
		return new Iterator();
	}

	@Override
	public Object [] toArray() {
		return null;
	}
	
	@Override
	public <T> T [] toArray(T [] a) {
		return null;
	}
	
	@Override
	public boolean remove(Object o) {
		modificationCount++;
		Node n = head;
		while (n.left != head) {
			if (n.left.value == o) {
				n.left = n.left.left;
				n.left.right = n;
			}
		}
		return false;
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains(o))
				return false;
		return true;
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean added = false;
		for (E i : c)
			added = add(i) || added;
		return added;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c)
			changed = remove(o) || changed;
		return changed;
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		Node n = head;
		while (n.left != head) {
			if (!c.contains(n.left.value)) {
				n.left.left.right = n;
				n.left = n.left.left;
			}
		}
		return changed;
	}
	
	@Override
	public void clear() {
		modificationCount++;
		size = 0;
		head.left = head;
		head.right = head;
	}
	
	@Override
	public boolean add(E e) {
		head.right.left = new Node(e, head, head.right);
		head.right = head.right.left;
		modificationCount++;
		size++;
		return true;
	}
	
	@Override
	public boolean offer(E e) {
		return add(e);
	}
	
	@Override
	public E remove() {
		if (isEmpty())
			throw new NoSuchElementException("Queue is empty!");
		modificationCount++;
		E i = head.left.value;
		head.left = head.left.left;
		head.left.right = head;
		size--;
		return i;
	}
	
	@Override
	public E poll() {
		if (isEmpty())
			return null;
		modificationCount++;
		E i = head.left.value;
		head.left = head.left.left;
		head.left.right = head;
		size--;
		return i;
	}
	
	@Override
	public E element() {
		if (isEmpty())
			throw new NoSuchElementException("Queue is empty!");
		return head.left.value;
	}
	
	@Override
	public E peek() {
		if (isEmpty())
			return null;
		return head.left.value;
	}
	
	private class Iterator implements java.util.Iterator<E> {
		
		private final int modificationCount;
		private Node currentNode;
		
		public Iterator() {
			this.modificationCount = IntentQueue.this.modificationCount;
			this.currentNode = IntentQueue.this.head;
		}
		
		@Override
		public boolean hasNext() {
			if (this.modificationCount != IntentQueue.this.modificationCount)
				throw new ConcurrentModificationException();
			return currentNode.left != IntentQueue.this.head;
		}
		
		@Override
		public E next() {
			if (!hasNext())
				throw new NoSuchElementException("Iterator has reached the end of the queue!");
			E i = currentNode.left.value;
			currentNode = currentNode.left;
			return i;
		}
		
	}
	
	private class Node {
		
		public final E value;
		public Node left;
		public Node right;
		
		public Node(E value, Node left, Node right) {
			this.value = value;
			this.left = left;
			this.right = right;
		}
		
	}
	
}
