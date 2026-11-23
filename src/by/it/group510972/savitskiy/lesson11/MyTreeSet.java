
package by.it.group510972.savitskiy.lesson11;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class MyTreeSet<E extends Comparable<? super E>> implements Set<E> {

    // Массив для хранения элементов
    private E[] elements;

    // Текущее количество элементов
    private int size;

    // Начальная вместимость массива
    private static final int DEFAULT_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public MyTreeSet() {
        elements = (E[]) new Comparable[DEFAULT_CAPACITY];
        size = 0;
    }

    // Увеличение вместимости массива
    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size == elements.length) {
            E[] newElements =
                    (E[]) new Comparable[elements.length * 2];

            System.arraycopy(elements, 0, newElements, 0, size);

            elements = newElements;
        }
    }

    // Бинарный поиск элемента
    // Если элемент найден, возвращается его индекс.
    // Если не найден, возвращается -(позиция вставки + 1).
    private int binarySearch(E element) {
        int left = 0;
        int right = size - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;

            int comparison = elements[middle].compareTo(element);

            if (comparison < 0) {
                left = middle + 1;
            } else if (comparison > 0) {
                right = middle - 1;
            } else {
                return middle;
            }
        }

        return -(left + 1);
    }

    // Поиск элемента по Object
    private int searchObject(Object element) {
        if (element == null) {
            throw new NullPointerException();
        }

        try {
            @SuppressWarnings("unchecked")
            E value = (E) element;

            return binarySearch(value);
        } catch (ClassCastException exception) {
            return -1;
        }
    }

    // Удаление элемента по индексу
    private E removeAt(int index) {
        E removed = elements[index];

        int numberMoved = size - index - 1;

        if (numberMoved > 0) {
            System.arraycopy(
                    elements,
                    index + 1,
                    elements,
                    index,
                    numberMoved
            );
        }

        elements[--size] = null;

        return removed;
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
    public boolean contains(Object element) {
        return searchObject(element) >= 0;
    }

    @Override
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException();
        }

        int index = binarySearch(element);

        // Элемент уже существует
        if (index >= 0) {
            return false;
        }

        // Определяем позицию вставки
        index = -(index + 1);

        ensureCapacity();

        // Сдвигаем элементы вправо
        int numberMoved = size - index;

        if (numberMoved > 0) {
            System.arraycopy(
                    elements,
                    index,
                    elements,
                    index + 1,
                    numberMoved
            );
        }

        // Вставляем новый элемент
        elements[index] = element;

        size++;

        return true;
    }

    @Override
    public boolean remove(Object element) {
        int index = searchObject(element);

        if (index < 0) {
            return false;
        }

        removeAt(index);

        return true;
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
        boolean modified = false;

        for (E element : c) {
            if (add(element)) {
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;

        Iterator<E> iterator = iterator();

        while (iterator.hasNext()) {
            E element = iterator.next();

            if (c.contains(element)) {
                iterator.remove();
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;

        Iterator<E> iterator = iterator();

        while (iterator.hasNext()) {
            E element = iterator.next();

            if (!c.contains(element)) {
                iterator.remove();
                modified = true;
            }
        }

        return modified;
    }

    // Итератор с поддержкой удаления
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
                if (lastReturned < 0) {
                    throw new IllegalStateException();
                }

                removeAt(lastReturned);

                cursor = lastReturned;
                lastReturned = -1;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];

        System.arraycopy(elements, 0, result, 0, size);

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        T[] result = a;

        if (a.length < size) {
            result = (T[]) Array.newInstance(
                    a.getClass().getComponentType(),
                    size
            );
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
    public int hashCode() {
        int result = 0;

        for (int i = 0; i < size; i++) {
            result += elements[i].hashCode();
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Set)) {
            return false;
        }

        Set<?> other = (Set<?>) obj;

        if (other.size() != size) {
            return false;
        }

        return containsAll(other);
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
}
