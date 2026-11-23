
package by.it.group510972.savitskiy.lesson12;

import java.util.*;

public class MySplayMap implements NavigableMap<Integer, String> {

    // Узел Splay-дерева
    private static class Node {
        Integer key;
        String value;

        Node left;
        Node right;
        Node parent;

        Node(Integer key, String value, Node parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
    }

    private Node root;
    private int size;

    // Левый поворот
    private void rotateLeft(Node node) {
        Node right = node.right;

        node.right = right.left;

        if (right.left != null) {
            right.left.parent = node;
        }

        right.parent = node.parent;

        if (node.parent == null) {
            root = right;
        } else if (node == node.parent.left) {
            node.parent.left = right;
        } else {
            node.parent.right = right;
        }

        right.left = node;
        node.parent = right;
    }

    // Правый поворот
    private void rotateRight(Node node) {
        Node left = node.left;

        node.left = left.right;

        if (left.right != null) {
            left.right.parent = node;
        }

        left.parent = node.parent;

        if (node.parent == null) {
            root = left;
        } else if (node == node.parent.left) {
            node.parent.left = left;
        } else {
            node.parent.right = left;
        }

        left.right = node;
        node.parent = left;
    }

    // Перемещение узла в корень
    private void splay(Node node) {
        if (node == null) {
            return;
        }

        while (node.parent != null) {

            Node parent = node.parent;
            Node grandparent = parent.parent;

            // Zig: родитель является корнем
            if (grandparent == null) {

                if (node == parent.left) {
                    rotateRight(parent);
                } else {
                    rotateLeft(parent);
                }

                // Zig-Zig: оба узла находятся слева
            } else if (node == parent.left
                    && parent == grandparent.left) {

                rotateRight(grandparent);
                rotateRight(parent);

                // Zig-Zig: оба узла находятся справа
            } else if (node == parent.right
                    && parent == grandparent.right) {

                rotateLeft(grandparent);
                rotateLeft(parent);

                // Zig-Zag: левый, затем правый
            } else if (node == parent.right
                    && parent == grandparent.left) {

                rotateLeft(parent);
                rotateRight(grandparent);

                // Zig-Zag: правый, затем левый
            } else {

                rotateRight(parent);
                rotateLeft(grandparent);
            }
        }
    }

    // Поиск узла.
    // При отсутствии ключа в корень перемещается
    // последний посещённый узел.
    private Node findNode(Integer key) {
        Node current = root;
        Node last = null;

        while (current != null) {
            last = current;

            int comparison = key.compareTo(current.key);

            if (comparison == 0) {
                splay(current);
                return current;
            }

            if (comparison < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        if (last != null) {
            splay(last);
        }

        return null;
    }

    // Минимальный элемент поддерева
    private Node minimum(Node node) {
        if (node == null) {
            return null;
        }

        while (node.left != null) {
            node = node.left;
        }

        return node;
    }

    // Максимальный элемент поддерева
    private Node maximum(Node node) {
        if (node == null) {
            return null;
        }

        while (node.right != null) {
            node = node.right;
        }

        return node;
    }

    // Следующий элемент по возрастанию
    private Node successor(Node node) {
        if (node.right != null) {
            return minimum(node.right);
        }

        Node parent = node.parent;

        while (parent != null && node == parent.right) {
            node = parent;
            parent = parent.parent;
        }

        return parent;
    }

    // Предыдущий элемент по возрастанию
    private Node predecessor(Node node) {
        if (node.left != null) {
            return maximum(node.left);
        }

        Node parent = node.parent;

        while (parent != null && node == parent.left) {
            node = parent;
            parent = parent.parent;
        }

        return parent;
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        if (root == null) {
            root = new Node(key, value, null);
            size++;
            return null;
        }

        Node current = root;

        while (true) {
            int comparison = key.compareTo(current.key);

            if (comparison == 0) {
                String oldValue = current.value;
                current.value = value;
                splay(current);
                return oldValue;
            }

            if (comparison < 0) {
                if (current.left == null) {
                    current.left = new Node(key, value, current);
                    splay(current.left);
                    size++;
                    return null;
                }

                current = current.left;

            } else {
                if (current.right == null) {
                    current.right = new Node(key, value, current);
                    splay(current.right);
                    size++;
                    return null;
                }

                current = current.right;
            }
        }
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }

        Node node = findNode((Integer) key);

        return node == null ? null : node.value;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) {
            return false;
        }

        return findNode((Integer) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        Node current = minimum(root);

        while (current != null) {
            if (value == null
                    ? current.value == null
                    : value.equals(current.value)) {
                return true;
            }

            current = successor(current);
        }

        return false;
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }

        Node node = findNode((Integer) key);

        if (node == null) {
            return null;
        }

        String oldValue = node.value;

        Node left = node.left;
        Node right = node.right;

        if (left != null) {
            left.parent = null;
        }

        if (right != null) {
            right.parent = null;
        }

        node.left = null;
        node.right = null;
        node.parent = null;

        size--;

        if (left == null) {
            root = right;
        } else {
            root = left;

            // Перемещаем максимальный узел левого
            // поддерева в корень
            Node max = maximum(root);
            splay(max);

            // Присоединяем правое поддерево
            root.right = right;

            if (right != null) {
                right.parent = root;
            }
        }

        return oldValue;
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
        root = null;
        size = 0;
    }

    @Override
    public Integer firstKey() {
        Node node = minimum(root);

        if (node == null) {
            throw new NoSuchElementException();
        }

        return node.key;
    }

    @Override
    public Integer lastKey() {
        Node node = maximum(root);

        if (node == null) {
            throw new NoSuchElementException();
        }

        return node.key;
    }

    // Получение записи
    private Map.Entry<Integer, String> createEntry(Node node) {
        if (node == null) {
            return null;
        }

        return new SimpleEntry(node.key, node.value);
    }

    // Собственная реализация Map.Entry
    private static class SimpleEntry
            implements Map.Entry<Integer, String> {

        private final Integer key;
        private String value;

        SimpleEntry(Integer key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            String oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }

            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) obj;

            return key.equals(entry.getKey())
                    && (value == null
                    ? entry.getValue() == null
                    : value.equals(entry.getValue()));
        }

        @Override
        public int hashCode() {
            return key.hashCode()
                    ^ (value == null ? 0 : value.hashCode());
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    // Поиск ближайшего ключа.
    // mode: -2 lower, -1 floor, 1 ceiling, 2 higher
    private Node findNear(Integer key, int mode) {
        if (key == null) {
            throw new NullPointerException();
        }

        Node current = root;
        Node candidate = null;
        Node last = null;

        while (current != null) {
            last = current;

            int comparison = key.compareTo(current.key);

            if (comparison == 0) {
                if (mode == -1 || mode == 1) {
                    candidate = current;
                } else {
                    candidate = mode == -2
                            ? predecessor(current)
                            : successor(current);
                }
                break;
            }

            if (comparison < 0) {
                if (mode == 1 || mode == 2) {
                    candidate = current;
                }

                current = current.left;

            } else {
                if (mode == -1 || mode == -2) {
                    candidate = current;
                }

                current = current.right;
            }
        }

        if (candidate != null) {
            splay(candidate);
        } else if (last != null) {
            splay(last);
        }

        return candidate;
    }

    @Override
    public Integer lowerKey(Integer key) {
        Node node = findNear(key, -2);
        return node == null ? null : node.key;
    }

    @Override
    public Integer floorKey(Integer key) {
        Node node = findNear(key, -1);
        return node == null ? null : node.key;
    }

    @Override
    public Integer ceilingKey(Integer key) {
        Node node = findNear(key, 1);
        return node == null ? null : node.key;
    }

    @Override
    public Integer higherKey(Integer key) {
        Node node = findNear(key, 2);
        return node == null ? null : node.key;
    }

    @Override
    public Map.Entry<Integer, String> lowerEntry(Integer key) {
        return createEntry(findNear(key, -2));
    }

    @Override
    public Map.Entry<Integer, String> floorEntry(Integer key) {
        return createEntry(findNear(key, -1));
    }

    @Override
    public Map.Entry<Integer, String> ceilingEntry(Integer key) {
        return createEntry(findNear(key, 1));
    }

    @Override
    public Map.Entry<Integer, String> higherEntry(Integer key) {
        return createEntry(findNear(key, 2));
    }

    @Override
    public Map.Entry<Integer, String> firstEntry() {
        return createEntry(minimum(root));
    }

    @Override
    public Map.Entry<Integer, String> lastEntry() {
        return createEntry(maximum(root));
    }

    @Override
    public Map.Entry<Integer, String> pollFirstEntry() {
        Node node = minimum(root);

        if (node == null) {
            return null;
        }

        Map.Entry<Integer, String> result = createEntry(node);
        remove(node.key);

        return result;
    }

    @Override
    public Map.Entry<Integer, String> pollLastEntry() {
        Node node = maximum(root);

        if (node == null) {
            return null;
        }

        Map.Entry<Integer, String> result = createEntry(node);
        remove(node.key);

        return result;
    }

    // Создание карты с элементами заданного диапазона
    private MySplayMap rangeMap(
            Integer from,
            boolean hasFrom,
            boolean fromInclusive,
            Integer to,
            boolean hasTo,
            boolean toInclusive) {

        MySplayMap result = new MySplayMap();

        Node current = minimum(root);

        while (current != null) {
            boolean validFrom = true;
            boolean validTo = true;

            if (hasFrom) {
                int comparison = current.key.compareTo(from);
                validFrom = fromInclusive
                        ? comparison >= 0
                        : comparison > 0;
            }

            if (hasTo) {
                int comparison = current.key.compareTo(to);
                validTo = toInclusive
                        ? comparison <= 0
                        : comparison < 0;
            }

            if (validFrom && validTo) {
                result.put(current.key, current.value);
            }

            current = successor(current);
        }

        return result;
    }

    @Override
    public NavigableMap<Integer, String> headMap(
            Integer toKey, boolean inclusive) {

        if (toKey == null) {
            throw new NullPointerException();
        }

        return rangeMap(
                null, false, false,
                toKey, true, inclusive
        );
    }

    @Override
    public NavigableMap<Integer, String> tailMap(
            Integer fromKey, boolean inclusive) {

        if (fromKey == null) {
            throw new NullPointerException();
        }

        return rangeMap(
                fromKey, true, inclusive,
                null, false, false
        );
    }

    @Override
    public NavigableMap<Integer, String> subMap(
            Integer fromKey,
            boolean fromInclusive,
            Integer toKey,
            boolean toInclusive) {

        if (fromKey == null || toKey == null) {
            throw new NullPointerException();
        }

        if (fromKey.compareTo(toKey) > 0) {
            throw new IllegalArgumentException();
        }

        return rangeMap(
                fromKey, true, fromInclusive,
                toKey, true, toInclusive
        );
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        return headMap(toKey, false);
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        return tailMap(fromKey, true);
    }

    @Override
    public SortedMap<Integer, String> subMap(
            Integer fromKey, Integer toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    // Обход дерева для формирования строки
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");

        Node current = minimum(root);

        while (current != null) {
            if (result.length() > 1) {
                result.append(", ");
            }

            result.append(current.key)
                    .append("=")
                    .append(current.value);

            current = successor(current);
        }

        result.append("}");

        return result.toString();
    }

    // Остальные методы Map и NavigableMap

    @Override
    public void putAll(Map<? extends Integer, ? extends String> map) {
        for (Map.Entry<? extends Integer, ? extends String> entry
                : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<Integer> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> navigableKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> descendingKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> descendingMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
