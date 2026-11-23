
package by.it.group510972.savitskiy.lesson09;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListC<E> implements List<E> {

    private class Node {
        E data;
        Node next;

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

        Node current = first;

        for (int i = 0; i < index; i++) {
            current = current.next;
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
            last.next = newNode;
            last = newNode;
        }

        size++;
        modCount++;

        return true;
    }

    @Override
    public E remove(int index) {
        checkIndex(index);

        Node removed;

        if (index == 0) {
            removed = first;
            first = first.next;

            if (size == 1) {
                last = null;
            }
        } else {
            Node previous = getNode(index - 1);
            removed = previous.next;
            previous.next = removed.next;

            if (removed == last) {
                last = previous;
            }
        }

        size--;
        modCount++;

        return removed.data;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(int index, E element) {
        checkPositionIndex(index);

        Node newNode = new Node(element);

        if (index == 0) {
            newNode.next = first;
            first = newNode;

            if (size == 0) {
                last = newNode;
            }
        } else if (index == size) {
            last.next = newNode;
            last = newNode;
        } else {
            Node previous = getNode(index - 1);

            newNode.next = previous.next;
            previous.next = newNode;
        }

        size++;
        modCount++;
    }

    @Override
    public boolean remove(Object o) {
        Node current = first;
        Node previous = null;

        while (current != null) {
            if (equalsElement(current.data, o)) {
                if (previous == null) {
                    first = current.next;
                } else {
                    previous.next = current.next;
                }

                if (current == last) {
                    last = previous;
                }

                size--;
                modCount++;

                return true;
            }

            previous = current;
            current = current.next;
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
        Node current = first;
        int result = -1;
        int index = 0;

        while (current != null) {
            if (equalsElement(current.data, o)) {
                result = index;
            }

            current = current.next;
            index++;
        }

        return result;
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
        Node previous = null;

        while (current != null) {
            if (c.contains(current.data)) {
                Node next = current.next;

                if (previous == null) {
                    first = next;
                } else {
                    previous.next = next;
                }

                if (current == last) {
                    last = previous;
                }

                size--;
                modCount++;
                changed = true;

                current = next;
            } else {
                previous = current;
                current = current.next;
            }
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
        Node previous = null;

        while (current != null) {
            if (!c.contains(current.data)) {
                Node next = current.next;

                if (previous == null) {
                    first = next;
                } else {
                    previous.next = next;
                }

                if (current == last) {
                    last = previous;
                }

                size--;
                modCount++;
                changed = true;

                current = next;
            } else {
                previous = current;
                current = current.next;
            }
        }

        return changed;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }

        ListC<E> result = new ListC<>();

        Node current = first;

        for (int i = 0; i < fromIndex; i++) {
            current = current.next;
        }

        for (int i = fromIndex; i < toIndex; i++) {
            result.add(current.data);
            current = current.next;
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

                ListC.this.remove(lastRet);

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

                ListC.this.set(lastRet, e);
            }

            @Override
            public void add(E e) {
                checkModification();

                ListC.this.add(cursor, e);
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
