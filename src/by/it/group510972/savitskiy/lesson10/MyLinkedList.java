
package by.it.group510972.savitskiy.lesson10;

import java.util.Deque;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<E> implements Deque<E> {

    private class Node {
        E data;
        Node next;
        Node prev;

        Node(E data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    private Node first;
    private Node last;
    private int size;

    public MyLinkedList() {
        first = null;
        last = null;
        size = 0;
    }

    private Node getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        Node current;

        if (index < size / 2) {
            current = first;

            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = last;

            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }

        return current;
    }

    @Override
    public void addFirst(E element) {
        if (element == null) {
            throw new NullPointerException();
        }

        Node newNode = new Node(element);

        if (first == null) {
            first = newNode;
            last = newNode;
        } else {
            newNode.next = first;
            first.prev = newNode;
            first = newNode;
        }

        size++;
    }

    @Override
    public void addLast(E element) {
        if (element == null) {
            throw new NullPointerException();
        }

        Node newNode = new Node(element);

        if (last == null) {
            first = newNode;
            last = newNode;
        } else {
            last.next = newNode;
            newNode.prev = last;
            last = newNode;
        }

        size++;
    }

    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    @Override
    public boolean offerFirst(E element) {
        addFirst(element);
        return true;
    }

    @Override
    public boolean offerLast(E element) {
        addLast(element);
        return true;
    }

    @Override
    public boolean offer(E element) {
        return offerLast(element);
    }

    @Override
    public E getFirst() {
        if (first == null) {
            throw new NoSuchElementException();
        }

        return first.data;
    }

    @Override
    public E getLast() {
        if (last == null) {
            throw new NoSuchElementException();
        }

        return last.data;
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peekFirst() {
        return first == null ? null : first.data;
    }

    @Override
    public E peekLast() {
        return last == null ? null : last.data;
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public E pollFirst() {
        if (first == null) {
            return null;
        }

        E result = first.data;
        first = first.next;

        if (first == null) {
            last = null;
        } else {
            first.prev = null;
        }

        size--;

        return result;
    }

    @Override
    public E pollLast() {
        if (last == null) {
            return null;
        }

        E result = last.data;
        last = last.prev;

        if (last == null) {
            first = null;
        } else {
            last.next = null;
        }

        size--;

        return result;
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E removeFirst() {
        if (first == null) {
            throw new NoSuchElementException();
        }

        return pollFirst();
    }

    @Override
    public E removeLast() {
        if (last == null) {
            throw new NoSuchElementException();
        }

        return pollLast();
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public void push(E element) {
        addFirst(element);
    }

    public E remove(int index) {
        Node current = getNode(index);

        if (current.prev == null) {
            first = current.next;
        } else {
            current.prev.next = current.next;
        }

        if (current.next == null) {
            last = current.prev;
        } else {
            current.next.prev = current.prev;
        }

        size--;

        return current.data;
    }

    public boolean remove(Object element) {
        return removeFirstOccurrence(element);
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        Node current = first;

        while (current != null) {
            if (o == null ? current.data == null : o.equals(current.data)) {
                if (current.prev == null) {
                    first = current.next;
                } else {
                    current.prev.next = current.next;
                }

                if (current.next == null) {
                    last = current.prev;
                } else {
                    current.next.prev = current.prev;
                }

                size--;
                return true;
            }

            current = current.next;
        }

        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        Node current = last;

        while (current != null) {
            if (o == null ? current.data == null : o.equals(current.data)) {
                if (current.prev == null) {
                    first = current.next;
                } else {
                    current.prev.next = current.next;
                }

                if (current.next == null) {
                    last = current.prev;
                } else {
                    current.next.prev = current.prev;
                }

                size--;
                return true;
            }

            current = current.prev;
        }

        return false;
    }

    @Override
    public boolean contains(Object o) {
        Node current = first;

        while (current != null) {
            if (o == null ? current.data == null : o.equals(current.data)) {
                return true;
            }

            current = current.next;
        }

        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Очистка списка
    @Override
    public void clear() {
        Node current = first;

        while (current != null) {
            Node next = current.next;
            current.data = null;
            current.next = null;
            current.prev = null;
            current = next;
        }

        first = null;
        last = null;
        size = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return createIterator(false);
    }

    @Override
    public Iterator<E> descendingIterator() {
        return createIterator(true);
    }

    private Iterator<E> createIterator(boolean reverse) {
        return new Iterator<E>() {
            private Node current = reverse ? last : first;
            private Node lastReturned = null;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }

                lastReturned = current;
                E result = current.data;
                current = reverse ? current.prev : current.next;

                return result;
            }

            @Override
            public void remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }

                Node previous = lastReturned.prev;
                Node next = lastReturned.next;

                if (previous == null) {
                    first = next;
                } else {
                    previous.next = next;
                }

                if (next == null) {
                    last = previous;
                } else {
                    next.prev = previous;
                }

                size--;
                lastReturned = null;
            }
        };
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == this) {
            Object[] copy = toArray();

            for (Object element : copy) {
                @SuppressWarnings("unchecked")
                E value = (E) element;
                addLast(value);
            }

            return copy.length > 0;
        }

        boolean changed = false;

        for (E element : c) {
            addLast(element);
            changed = true;
        }

        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        Iterator<E> iterator = iterator();

        while (iterator.hasNext()) {
            if (c.contains(iterator.next())) {
                iterator.remove();
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        Iterator<E> iterator = iterator();

        while (iterator.hasNext()) {
            if (!c.contains(iterator.next())) {
                iterator.remove();
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];

        Node current = first;

        for (int i = 0; i < size; i++) {
            result[i] = current.data;
            current = current.next;
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] array) {
        T[] result = array;

        if (array.length < size) {
            result = (T[]) java.lang.reflect.Array.newInstance(
                    array.getClass().getComponentType(), size);
        }

        Node current = first;

        for (int i = 0; i < size; i++) {
            result[i] = (T) current.data;
            current = current.next;
        }

        if (result.length > size) {
            result[size] = null;
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[");

        Node current = first;

        while (current != null) {
            result.append(current.data);

            if (current.next != null) {
                result.append(", ");
            }

            current = current.next;
        }

        result.append("]");

        return result.toString();
    }
}
