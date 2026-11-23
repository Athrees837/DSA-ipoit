
package by.it.group510972.savitskiy.lesson09;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListB<E> implements List<E> {

    private class Node {
        E data;
        Node next;
        Node prev;

        Node(E data) {
            this.data = data;
        }
    }

    private Node first;
    private Node last;
    private int size = 0;
    private int modCount = 0;

    private Node getNode(int index) {
        checkIndex(index);

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

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }
    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
    }

    private boolean equalsElement(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
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

    @Override
    public boolean add(E e) {
        Node newNode = new Node(e);

        if (first == null) {
            first = newNode;
            last = newNode;
        } else {
            newNode.prev = last;
            last.next = newNode;
            last = newNode;
        }

        size++;
        modCount++;

        return true;
    }

    @Override
    public E remove(int index) {
        Node current = getNode(index);
        E removed = current.data;

        if (current.prev != null) {
            current.prev.next = current.next;
        } else {
            first = current.next;
        }

        if (current.next != null) {
            current.next.prev = current.prev;
        } else {
            last = current.prev;
        }

        size--;
        modCount++;

        return removed;
    }

    @Override
    public int size() {
        return size;
    }


    @Override
    public void add(int index, E element) {
        checkPositionIndex(index);

        if (index == size) {
            add(element);
            return;
        }

        Node current = getNode(index);
        Node newNode = new Node(element);

        newNode.next = current;
        newNode.prev = current.prev;

        if (current.prev != null) {
            current.prev.next = newNode;
        } else {
            first = newNode;
        }

        current.prev = newNode;

        size++;
        modCount++;
    }

    @Override
    public boolean remove(Object o) {
        Node current = first;
        int index = 0;

        while (current != null) {
            if (equalsElement(current.data, o)) {
                remove(index);
                return true;
            }

            current = current.next;
            index++;
        }

        return false;
    }

    @Override
    public E set(int index, E element) {
        Node current = getNode(index);

        E oldElement = current.data;
        current.data = element;

        return oldElement;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        Node current = first;

        while (current != null) {
            Node next = current.next;
            current.prev = null;
            current.next = null;
            current.data = null;
            current = next;
        }

        first = null;
        last = null;

        if (size > 0) {
            size = 0;
            modCount++;
        }
    }

    @Override
    public int indexOf(Object o) {
        Node current = first;
        int index = 0;

        while (current != null) {
            if (equalsElement(current.data, o)) {
                return index;
            }

            current = current.next;
            index++;
        }

        return -1;
    }

    @Override
    public E get(int index) {
        return getNode(index).data;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        Node current = last;
        int index = size - 1;

        while (current != null) {
            if (equalsElement(current.data, o)) {
                return index;
            }

            current = current.prev;
            index--;
        }

        return -1;
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
        Object[] array = c.toArray();

        if (array.length == 0) {
            return false;
        }

        for (Object element : array) {
            add((E) element);
        }

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);

        Object[] array = c.toArray();

        if (array.length == 0) {
            return false;
        }

        for (Object element : array) {
            add(index++, (E) element);
        }

        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == this) {
            boolean changed = !isEmpty();
            clear();
            return changed;
        }

        boolean changed = false;
        Node current = first;
        int index = 0;

        while (current != null) {
            Node next = current.next;

            if (c.contains(current.data)) {
                remove(index);
                changed = true;
            } else {
                index++;
            }

            current = next;
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == this) {
            return false;
        }

        boolean changed = false;
        Node current = first;
        int index = 0;

        while (current != null) {
            Node next = current.next;

            if (!c.contains(current.data)) {
                remove(index);
                changed = true;
            } else {
                index++;
            }

            current = next;
        }

        return changed;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }

        ListB<E> result = new ListB<>();

        for (int i = fromIndex; i < toIndex; i++) {
            result.add(get(i));
        }

        return result;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);

        return new ListIterator<E>() {
            int cursor = index;
            int lastRet = -1;
            int expectedModCount = modCount;

            private void checkModification() {
                if (expectedModCount != modCount) {
                    throw new java.util.ConcurrentModificationException();
                }
            }

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                checkModification();

                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }

                lastRet = cursor;
                return get(cursor++);
            }

            @Override
            public boolean hasPrevious() {
                return cursor > 0;
            }

            @Override
            public E previous() {
                checkModification();

                if (!hasPrevious()) {
                    throw new java.util.NoSuchElementException();
                }

                lastRet = --cursor;
                return get(cursor);
            }

            @Override
            public int nextIndex() {
                return cursor;
            }

            @Override
            public int previousIndex() {
                return cursor - 1;
            }

            @Override
            public void remove() {
                checkModification();

                if (lastRet == -1) {
                    throw new IllegalStateException();
                }

                ListB.this.remove(lastRet);

                if (lastRet < cursor) {
                    cursor--;
                }

                lastRet = -1;
                expectedModCount = modCount;
            }

            @Override
            public void set(E e) {
                checkModification();

                if (lastRet == -1) {
                    throw new IllegalStateException();
                }

                ListB.this.set(lastRet, e);
            }

            @Override
            public void add(E e) {
                checkModification();

                ListB.this.add(cursor, e);
                cursor++;

                lastRet = -1;
                expectedModCount = modCount;
            }
        };
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size
            );
        }

        Node current = first;

        for (int i = 0; i < size; i++) {
            a[i] = (T) current.data;
            current = current.next;
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
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
    public Iterator<E> iterator() {
        return listIterator();
    }
}
