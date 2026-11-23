
package by.it.group510972.savitskiy.lesson11;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class MyHashSet<E> implements Set<E> {

    // Узел односвязного списка
    private static class Node<E> {
        E data;
        Node<E> next;

        Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }

    // Массив хеш-таблицы
    private Node<E>[] table;

    // Количество элементов
    private int size;

    // Коэффициент заполнения
    private static final double LOAD_FACTOR = 0.75;

    // Порог увеличения массива
    private int threshold;

    // Начальная вместимость
    private static final int DEFAULT_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public MyHashSet() {
        table = (Node<E>[]) new Node[DEFAULT_CAPACITY];
        threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
        size = 0;
    }

    // Получение хеш-кода элемента
    private int hash(Object element) {
        if (element == null) {
            return 0;
        }

        int h = element.hashCode();
        return h ^ (h >>> 16);
    }

    // Получение индекса массива
    private int index(Object element) {
        return (hash(element) & 0x7fffffff) % table.length;
    }

    // Сравнение элементов
    private boolean equals(Object a, Object b) {
        if (a == b) {
            return true;
        }

        return a != null && a.equals(b);
    }

    // Увеличение массива и перераспределение элементов
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;

        table = (Node<E>[]) new Node[oldTable.length * 2];
        threshold = (int) (table.length * LOAD_FACTOR);

        for (Node<E> head : oldTable) {
            Node<E> current = head;

            while (current != null) {
                Node<E> next = current.next;

                int index = (hash(current.data) & 0x7fffffff)
                        % table.length;

                current.next = table[index];
                table[index] = current;

                current = next;
            }
        }
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
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }

        size = 0;
    }

    @Override
    public boolean contains(Object element) {
        int index = index(element);

        Node<E> current = table[index];

        while (current != null) {
            if (equals(current.data, element)) {
                return true;
            }

            current = current.next;
        }

        return false;
    }

    @Override
    public boolean add(E element) {
        int index = index(element);

        Node<E> current = table[index];

        // Проверяем отсутствие дубликатов
        while (current != null) {
            if (equals(current.data, element)) {
                return false;
            }

            current = current.next;
        }

        // При необходимости увеличиваем массив
        if (size + 1 > threshold) {
            resize();
            index = index(element);
        }

        // Добавляем элемент в начало списка
        table[index] = new Node<>(element, table[index]);

        size++;

        return true;
    }

    @Override
    public boolean remove(Object element) {
        int index = index(element);

        Node<E> current = table[index];
        Node<E> previous = null;

        while (current != null) {
            if (equals(current.data, element)) {

                if (previous == null) {
                    table[index] = current.next;
                } else {
                    previous.next = current.next;
                }

                size--;

                return true;
            }

            previous = current;
            current = current.next;
        }

        return false;
    }

    // Итератор
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private int nextBucket = 0;
            private int currentBucket = -1;

            private Node<E> nextNode;
            private Node<E> nextPrevious;

            private Node<E> currentNode;
            private Node<E> currentPrevious;

            private void findNext() {
                while (nextNode == null && nextBucket < table.length) {
                    currentBucket = nextBucket;
                    nextNode = table[nextBucket];
                    nextPrevious = null;
                    nextBucket++;
                }
            }

            @Override
            public boolean hasNext() {
                findNext();
                return nextNode != null;
            }

            @Override
            public E next() {
                findNext();

                if (nextNode == null) {
                    throw new NoSuchElementException();
                }

                currentNode = nextNode;
                currentPrevious = nextPrevious;

                nextNode = currentNode.next;
                nextPrevious = currentNode;

                return currentNode.data;
            }

            @Override
            public void remove() {
                if (currentNode == null) {
                    throw new IllegalStateException();
                }

                if (currentPrevious == null) {
                    table[currentBucket] = currentNode.next;
                } else {
                    currentPrevious.next = currentNode.next;
                }

                if (nextPrevious == currentNode) {
                    nextPrevious = currentPrevious;
                }

                size--;
                currentNode = null;
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

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];

        int index = 0;

        for (E element : this) {
            result[index++] = element;
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        T[] result = a;

        if (a.length < size) {
            result = (T[]) Array.newInstance(
                    a.getClass().getComponentType(), size
            );
        }

        int index = 0;

        for (E element : this) {
            result[index++] = (T) element;
        }

        if (result.length > size) {
            result[size] = null;
        }

        return result;
    }

    @Override
    public int hashCode() {
        int result = 0;

        for (E element : this) {
            if (element != null) {
                result += element.hashCode();
            }
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

        Iterator<E> iterator = iterator();

        while (iterator.hasNext()) {
            E element = iterator.next();

            result.append(element);

            if (iterator.hasNext()) {
                result.append(", ");
            }
        }

        result.append("]");

        return result.toString();
    }
}
