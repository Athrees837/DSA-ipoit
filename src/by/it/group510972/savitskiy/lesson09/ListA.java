
package by.it.group510972.savitskiy.lesson09;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListA<E> implements List<E> {

    private Object[] elements = new Object[10];
    private int size = 0;
    private int modCount = 0;

    private void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            int newCapacity = elements.length * 2;

            if (newCapacity < capacity) {
                newCapacity = capacity;
            }

            Object[] newElements = new Object[newCapacity];

            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }

            elements = newElements;
        }
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

        for (int i = 0; i < size; i++) {
            result.append(elements[i]);

            if (i < size - 1) {
                result.append(", ");
            }
        }

        result.append("]");
        return result.toString();
    }

    @Override
    public boolean add(E e) {
        ensureCapacity(size + 1);

        elements[size] = e;
        size++;
        modCount++;

        return true;
    }

    @Override
    public E remove(int index) {
        checkIndex(index);

        E removed = (E) elements[index];

        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        elements[size - 1] = null;
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
        ensureCapacity(size + 1);

        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }

        elements[index] = element;
        size++;
        modCount++;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);

        if (index == -1) {
            return false;
        }

        remove(index);
        return true;
    }

    @Override
    public E set(int index, E element) {
        checkIndex(index);

        E oldElement = (E) elements[index];
        elements[index] = element;

        return oldElement;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }

        if (size > 0) {
            size = 0;
            modCount++;
        }
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size; i++) {
            if (equalsElement(elements[i], o)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public E get(int index) {
        checkIndex(index);
        return (E) elements[index];
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            if (equalsElement(elements[i], o)) {
                return i;
            }
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

        ensureCapacity(size + array.length);

        for (Object element : array) {
            elements[size++] = element;
        }

        modCount++;
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);

        Object[] array = c.toArray();

        if (array.length == 0) {
            return false;
        }

        ensureCapacity(size + array.length);

        for (int i = size - 1; i >= index; i--) {
            elements[i + array.length] = elements[i];
        }

        for (int i = 0; i < array.length; i++) {
            elements[index + i] = array[i];
        }

        size += array.length;
        modCount++;

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

        for (int i = 0; i < size; i++) {
            if (c.contains(elements[i])) {
                remove(i);
                i--;
                changed = true;
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

        for (int i = 0; i < size; i++) {
            if (!c.contains(elements[i])) {
                remove(i);
                i--;
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }

        ListA<E> result = new ListA<>();

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

                ListA.this.remove(lastRet);

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

                ListA.this.set(lastRet, e);
            }

            @Override
            public void add(E e) {
                checkModification();

                ListA.this.add(cursor, e);
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

        for (int i = 0; i < size; i++) {
            a[i] = (T) elements[i];
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];

        for (int i = 0; i < size; i++) {
            result[i] = elements[i];
        }

        return result;
    }

    // Итератор

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }
}
