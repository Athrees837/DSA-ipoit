
package by.it.group510972.savitskiy.lesson11;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {

    // Узел одновременно участвует в хеш-таблице
    // и в списке порядка добавления
    private static class Node<E> {
        E data;

        // Связи для хеш-таблицы
        Node<E> bucketNext;

        // Связи для порядка добавления
        Node<E> orderPrev;
        Node<E> orderNext;

        Node(E data) {
            this.data = data;
        }
    }

    // Массив хеш-таблицы
    private Node<E>[] table;

    // Первый и последний элементы списка порядка добавления
    private Node<E> first;
    private Node<E> last;

    // Количество элементов
    private int size;

    // Коэффициент заполнения
    private static final double LOAD_FACTOR = 0.75;

    // Начальная вместимость массива
    private static final int DEFAULT_CAPACITY = 16;

    // Порог увеличения массива
    private int threshold;

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        table = (Node<E>[]) new Node[DEFAULT_CAPACITY];

        threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);

        size = 0;
        first = null;
        last = null;
    }

    // Вычисление хеш-кода
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

    // Поиск элемента в хеш-таблице
    private Node<E> findNode(Object element) {
        int index = index(element);

        Node<E> current = table[index];

        while (current != null) {
            if (equals(current.data, element)) {
                return current;
            }

            current = current.bucketNext;
        }

        return null;
    }

    // Добавление элемента в конец списка порядка
    private void linkLast(Node<E> node) {
        node.orderPrev = last;
        node.orderNext = null;

        if (last == null) {
            first = node;
        } else {
            last.orderNext = node;
        }

        last = node;
    }

    // Удаление элемента из списка порядка
    private void unlinkOrder(Node<E> node) {
        Node<E> prev = node.orderPrev;
        Node<E> next = node.orderNext;

        if (prev == null) {
            first = next;
        } else {
            prev.orderNext = next;
        }

        if (next == null) {
            last = prev;
        } else {
            next.orderPrev = prev;
        }

        node.orderPrev = null;
        node.orderNext = null;
    }

    // Увеличение массива и перераспределение элементов
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;

        table = (Node<E>[]) new Node[oldTable.length * 2];

        threshold = (int) (table.length * LOAD_FACTOR);

        // Перераспределяем элементы, обходя список добавления.
        // Сам порядок элементов при этом не меняется.
        Node<E> current = first;

        while (current != null) {
            int index = index(current.data);

            current.bucketNext = table[index];
            table[index] = current;

            current = current.orderNext;
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

        first = null;
        last = null;

        size = 0;
    }

    @Override
    public boolean contains(Object element) {
        return findNode(element) != null;
    }

    @Override
    public boolean add(E element) {
        // Проверяем, существует ли элемент
        if (contains(element)) {
            return false;
        }

        // Увеличиваем массив при необходимости
        if (size + 1 > threshold) {
            resize();
        }

        // Создаём новый узел
        Node<E> node = new Node<>(element);

        // Добавляем в хеш-таблицу
        int index = index(element);

        node.bucketNext = table[index];
        table[index] = node;

        // Добавляем в конец списка порядка
        linkLast(node);

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

                // Удаляем из хеш-таблицы
                if (previous == null) {
                    table[index] = current.bucketNext;
                } else {
                    previous.bucketNext = current.bucketNext;
                }

                // Удаляем из списка порядка добавления
                unlinkOrder(current);

                size--;

                return true;
            }

            previous = current;
            current = current.bucketNext;
        }

        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private Node<E> nextNode = first;
            private Node<E> currentNode = null;

            @Override
            public boolean hasNext() {
                return nextNode != null;
            }

            @Override
            public E next() {
                if (nextNode == null) {
                    throw new NoSuchElementException();
                }

                currentNode = nextNode;
                nextNode = nextNode.orderNext;

                return currentNode.data;
            }

            @Override
            public void remove() {
                if (currentNode == null) {
                    throw new IllegalStateException();
                }

                MyLinkedHashSet.this.remove(currentNode.data);

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
                    a.getClass().getComponentType(),
                    size
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
