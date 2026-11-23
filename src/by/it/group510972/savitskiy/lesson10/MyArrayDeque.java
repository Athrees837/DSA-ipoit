
package by.it.group510972.savitskiy.lesson10;

import java.util.Deque;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyArrayDeque<E> implements Deque<E> {

    private E[] elements;
    private int size;
    private int head;

    @SuppressWarnings("unchecked")
    public MyArrayDeque() {
        elements = (E[]) new Object[10];
        size = 0;
        head = 0;
    }

    // Увеличение размера массива
    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size == elements.length) {
            E[] newElements = (E[]) new Object[elements.length * 2];

            for (int i = 0; i < size; i++) {
                newElements[i] = elements[(head + i) % elements.length];
            }

            elements = newElements;
            head = 0;
        }
    }

    // Получение элемента по индексу
    private E getElement(int index) {
        return elements[(head + index) % elements.length];
    }

    // Удаление элемента по индексу
    private E removeAt(int index) {
        E result = getElement(index);

        if (index < size / 2) {
            for (int i = index; i > 0; i--) {
                elements[(head + i) % elements.length] =
                        elements[(head + i - 1) % elements.length];
            }

            elements[head] = null;
            head = (head + 1) % elements.length;
        } else {
            for (int i = index; i < size - 1; i++) {
                elements[(head + i) % elements.length] =
                        elements[(head + i + 1) % elements.length];
            }

            elements[(head + size - 1) % elements.length] = null;
        }

        size--;

        if (size == 0) {
            head = 0;
        }

        return result;
    }

    // Добавление элемента в начало
    @Override
    public void addFirst(E element) {
        if (element == null) {
            throw new NullPointerException();
        }

        ensureCapacity();

        head = (head - 1 + elements.length) % elements.length;
        elements[head] = element;
        size++;
    }

    // Добавление элемента в конец
    @Override
    public void addLast(E element) {
        if (element == null) {
            throw new NullPointerException();
        }

        ensureCapacity();

        int index = (head + size) % elements.length;
        elements[index] = element;
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

    // Получение первого элемента
    @Override
    public E getFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return elements[head];
    }

    // Получение последнего элемента
    @Override
    public E getLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return getElement(size - 1);
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peekFirst() {
        return size == 0 ? null : getFirst();
    }

    @Override
    public E peekLast() {
        return size == 0 ? null : getLast();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    // Извлечение первого элемента
    @Override
    public E pollFirst() {
        if (size == 0) {
            return null;
        }

        return removeAt(0);
    }

    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }

        return removeAt(size - 1);
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return removeAt(0);
    }

    @Override
    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return removeAt(size - 1);
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

    @Override
    public boolean removeFirstOccurrence(Object o) {
        for (int i = 0; i < size; i++) {
            E element = getElement(i);

            if (o == null ? element == null : o.equals(element)) {
                removeAt(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            E element = getElement(i);

            if (o == null ? element == null : o.equals(element)) {
                removeAt(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < size; i++) {
            E element = getElement(i);

            if (o == null ? element == null : o.equals(element)) {
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
            elements[(head + i) % elements.length] = null;
        }

        size = 0;
        head = 0;
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
            private int cursor = reverse ? size - 1 : 0;
            private int lastReturned = -1;

            @Override
            public boolean hasNext() {
                return reverse ? cursor >= 0 : cursor < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                lastReturned = cursor;

                if (reverse) {
                    cursor--;
                } else {
                    cursor++;
                }

                return getElement(lastReturned);
            }

            @Override
            public void remove() {
                if (lastReturned == -1) {
                    throw new IllegalStateException();
                }

                removeAt(lastReturned);

                if (!reverse) {
                    cursor--;
                }

                lastReturned = -1;
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

        for (int i = 0; i < size; i++) {
            result[i] = getElement(i);
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
            result[i] = (T) getElement(i);
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

            result.append(getElement(i));
        }

        result.append("]");
        return result.toString();
    }
}
