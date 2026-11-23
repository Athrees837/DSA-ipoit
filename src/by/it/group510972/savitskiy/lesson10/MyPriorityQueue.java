
package by.it.group510972.savitskiy.lesson10;

import java.util.Queue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyPriorityQueue<E extends Comparable<? super E>> implements Queue<E> {

    private E[] elements;
    private int size;

    @SuppressWarnings("unchecked")
    public MyPriorityQueue() {
        elements = (E[]) new Comparable[10];
        size = 0;
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size == elements.length) {
            E[] newElements = (E[]) new Comparable[elements.length * 2];

            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }

            elements = newElements;
        }
    }

    private void swap(int i, int j) {
        E temp = elements[i];
        elements[i] = elements[j];
        elements[j] = temp;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;

            if (elements[index].compareTo(elements[parent]) >= 0) {
                break;
            }

            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size &&
                    elements[left].compareTo(elements[smallest]) < 0) {
                smallest = left;
            }

            if (right < size &&
                    elements[right].compareTo(elements[smallest]) < 0) {
                smallest = right;
            }

            if (smallest == index) {
                break;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    @Override
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException();
        }

        ensureCapacity();

        elements[size] = element;
        siftUp(size);
        size++;

        return true;
    }

    @Override
    public boolean offer(E element) {
        return add(element);
    }

    @Override
    public E peek() {
        if (size == 0) {
            return null;
        }

        return elements[0];
    }

    @Override
    public E poll() {
        if (size == 0) {
            return null;
        }

        E result = elements[0];

        elements[0] = elements[size - 1];
        elements[size - 1] = null;
        size--;

        if (size > 0) {
            siftDown(0);
        }

        return result;
    }

    @Override
    public E element() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return elements[0];
    }

    @Override
    public E remove() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return poll();
    }

    @Override
    public boolean remove(Object element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {

                elements[i] = elements[size - 1];
                elements[size - 1] = null;
                size--;

                if (i < size) {
                    if (i > 0 &&
                            elements[i].compareTo(elements[(i - 1) / 2]) < 0) {
                        siftUp(i);
                    } else {
                        siftDown(i);
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean contains(Object element) {
        if (element == null) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {
                return true;
            }
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

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }

        size = 0;
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
                add(value);
            }

            return copy.length > 0;
        }

        boolean changed = false;

        for (E element : c) {
            add(element);
            changed = true;
        }

        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }

        int oldSize = size;
        int newSize = 0;

        for (int i = 0; i < oldSize; i++) {
            if (!c.contains(elements[i])) {
                elements[newSize] = elements[i];
                newSize++;
            }
        }

        for (int i = newSize; i < oldSize; i++) {
            elements[i] = null;
        }

        size = newSize;

        for (int i = size / 2 - 1; i >= 0; i--) {
            siftDown(i);
        }

        return oldSize != newSize;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }

        int oldSize = size;
        int newSize = 0;

        for (int i = 0; i < oldSize; i++) {
            if (c.contains(elements[i])) {
                elements[newSize] = elements[i];
                newSize++;
            }
        }

        for (int i = newSize; i < oldSize; i++) {
            elements[i] = null;
        }

        size = newSize;

        for (int i = size / 2 - 1; i >= 0; i--) {
            siftDown(i);
        }

        return oldSize != newSize;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int cursor = 0;
            private int lastReturned = -1;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                lastReturned = cursor;
                return elements[cursor++];
            }

            @Override
            public void remove() {
                if (lastReturned == -1) {
                    throw new IllegalStateException();
                }

                int index = lastReturned;

                elements[index] = elements[size - 1];
                elements[size - 1] = null;
                size--;

                if (index < size) {
                    if (index > 0 &&
                            elements[index].compareTo(elements[(index - 1) / 2]) < 0) {
                        siftUp(index);
                    } else {
                        siftDown(index);
                    }
                }

                cursor = index;
                lastReturned = -1;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];

        for (int i = 0; i < size; i++) {
            result[i] = elements[i];
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

        for (int i = 0; i < size; i++) {
            result[i] = (T) elements[i];
        }

        if (result.length > size) {
            result[size] = null;
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[");

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                result.append(", ");
            }

            result.append(elements[i]);
        }

        result.append("]");

        return result.toString();
    }
}
