
package by.it.group510972.savitskiy.lesson12;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class MyRbMap implements SortedMap<Integer, String> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    // Узел красно-черного дерева
    private static class Node {
        Integer key;
        String value;

        Node left;
        Node right;
        Node parent;

        boolean color;

        Node(Integer key, String value, boolean color, Node parent) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.parent = parent;
        }
    }

    private Node root;
    private int size;

    // Проверка цвета узла
    private boolean colorOf(Node node) {
        return node == null ? BLACK : node.color;
    }

    private Node parentOf(Node node) {
        return node == null ? null : node.parent;
    }

    private Node leftOf(Node node) {
        return node == null ? null : node.left;
    }

    private Node rightOf(Node node) {
        return node == null ? null : node.right;
    }

    private void setColor(Node node, boolean color) {
        if (node != null) {
            node.color = color;
        }
    }

    // Левый поворот
    private void rotateLeft(Node node) {
        if (node == null) {
            return;
        }

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
        if (node == null) {
            return;
        }

        Node left = node.left;

        node.left = left.right;

        if (left.right != null) {
            left.right.parent = node;
        }

        left.parent = node.parent;

        if (node.parent == null) {
            root = left;
        } else if (node == node.parent.right) {
            node.parent.right = left;
        } else {
            node.parent.left = left;
        }

        left.right = node;
        node.parent = left;
    }

    // Поиск узла по ключу
    private Node findNode(Integer key) {
        Node current = root;

        while (current != null) {
            int comparison = key.compareTo(current.key);

            if (comparison == 0) {
                return current;
            }

            current = comparison < 0 ? current.left : current.right;
        }

        return null;
    }

    // Минимальный узел поддерева
    private Node minimum(Node node) {
        while (node != null && node.left != null) {
            node = node.left;
        }

        return node;
    }

    // Максимальный узел поддерева
    private Node maximum(Node node) {
        while (node != null && node.right != null) {
            node = node.right;
        }

        return node;
    }

    // Следующий узел по возрастанию ключа
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

    // Замена одного поддерева другим
    private void transplant(Node oldNode, Node newNode) {
        if (oldNode.parent == null) {
            root = newNode;
        } else if (oldNode == oldNode.parent.left) {
            oldNode.parent.left = newNode;
        } else {
            oldNode.parent.right = newNode;
        }

        if (newNode != null) {
            newNode.parent = oldNode.parent;
        }
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        Node parent = null;
        Node current = root;

        while (current != null) {
            parent = current;

            int comparison = key.compareTo(current.key);

            if (comparison < 0) {
                current = current.left;
            } else if (comparison > 0) {
                current = current.right;
            } else {
                String oldValue = current.value;
                current.value = value;
                return oldValue;
            }
        }

        Node node = new Node(key, value, RED, parent);

        if (parent == null) {
            root = node;
        } else if (key.compareTo(parent.key) < 0) {
            parent.left = node;
        } else {
            parent.right = node;
        }

        fixAfterInsertion(node);
        size++;

        return null;
    }

    // Балансировка после добавления
    private void fixAfterInsertion(Node node) {
        node.color = RED;

        while (node != null && node != root && colorOf(parentOf(node)) == RED) {

            if (parentOf(node) == leftOf(parentOf(parentOf(node)))) {

                Node uncle = rightOf(parentOf(parentOf(node)));

                if (colorOf(uncle) == RED) {
                    setColor(parentOf(node), BLACK);
                    setColor(uncle, BLACK);
                    setColor(parentOf(parentOf(node)), RED);

                    node = parentOf(parentOf(node));

                } else {

                    if (node == rightOf(parentOf(node))) {
                        node = parentOf(node);
                        rotateLeft(node);
                    }

                    setColor(parentOf(node), BLACK);
                    setColor(parentOf(parentOf(node)), RED);

                    rotateRight(parentOf(parentOf(node)));
                }

            } else {

                Node uncle = leftOf(parentOf(parentOf(node)));

                if (colorOf(uncle) == RED) {
                    setColor(parentOf(node), BLACK);
                    setColor(uncle, BLACK);
                    setColor(parentOf(parentOf(node)), RED);

                    node = parentOf(parentOf(node));

                } else {

                    if (node == leftOf(parentOf(node))) {
                        node = parentOf(node);
                        rotateRight(node);
                    }

                    setColor(parentOf(node), BLACK);
                    setColor(parentOf(parentOf(node)), RED);

                    rotateLeft(parentOf(parentOf(node)));
                }
            }
        }

        setColor(root, BLACK);
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
        return key instanceof Integer && findNode((Integer) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        Node current = minimum(root);

        while (current != null) {
            if (value == null ? current.value == null : value.equals(current.value)) {
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

        deleteNode(node);

        return oldValue;
    }

    // Удаление узла
    private void deleteNode(Node node) {
        Node replacement = node;
        boolean originalColor = replacement.color;

        Node fixNode;
        Node fixParent;

        if (node.left == null) {
            fixNode = node.right;
            fixParent = node.parent;

            transplant(node, node.right);

            if (fixNode != null) {
                fixParent = fixNode.parent;
            }

        } else if (node.right == null) {
            fixNode = node.left;
            fixParent = node.parent;

            transplant(node, node.left);

            if (fixNode != null) {
                fixParent = fixNode.parent;
            }

        } else {
            replacement = minimum(node.right);
            originalColor = replacement.color;
            fixNode = replacement.right;

            if (replacement.parent == node) {
                fixParent = replacement;

                if (fixNode != null) {
                    fixNode.parent = replacement;
                }

            } else {
                fixParent = replacement.parent;

                transplant(replacement, replacement.right);

                replacement.right = node.right;
                replacement.right.parent = replacement;
            }

            transplant(node, replacement);

            replacement.left = node.left;
            replacement.left.parent = replacement;

            replacement.color = node.color;
        }

        size--;

        if (originalColor == BLACK) {
            fixAfterDeletion(fixNode, fixParent);
        }
    }

    // Балансировка после удаления
    private void fixAfterDeletion(Node node, Node parent) {
        while (node != root && colorOf(node) == BLACK) {

            if (parent == null) {
                break;
            }

            if (node == parent.left) {

                Node sibling = parent.right;

                if (colorOf(sibling) == RED) {
                    setColor(sibling, BLACK);
                    setColor(parent, RED);

                    rotateLeft(parent);

                    sibling = parent.right;
                }

                if (sibling == null) {
                    node = parent;
                    parent = node.parent;
                    continue;
                }

                if (colorOf(sibling.left) == BLACK
                        && colorOf(sibling.right) == BLACK) {

                    setColor(sibling, RED);

                    node = parent;
                    parent = node.parent;

                } else {

                    if (colorOf(sibling.right) == BLACK) {
                        setColor(sibling.left, BLACK);
                        setColor(sibling, RED);

                        rotateRight(sibling);

                        sibling = parent.right;
                    }

                    setColor(sibling, colorOf(parent));
                    setColor(parent, BLACK);
                    setColor(sibling.right, BLACK);

                    rotateLeft(parent);

                    node = root;
                    parent = null;
                }

            } else {

                Node sibling = parent.left;

                if (colorOf(sibling) == RED) {
                    setColor(sibling, BLACK);
                    setColor(parent, RED);

                    rotateRight(parent);

                    sibling = parent.left;
                }

                if (sibling == null) {
                    node = parent;
                    parent = node.parent;
                    continue;
                }

                if (colorOf(sibling.right) == BLACK
                        && colorOf(sibling.left) == BLACK) {

                    setColor(sibling, RED);

                    node = parent;
                    parent = node.parent;

                } else {

                    if (colorOf(sibling.left) == BLACK) {
                        setColor(sibling.right, BLACK);
                        setColor(sibling, RED);

                        rotateLeft(sibling);

                        sibling = parent.left;
                    }

                    setColor(sibling, colorOf(parent));
                    setColor(parent, BLACK);
                    setColor(sibling.left, BLACK);

                    rotateRight(parent);

                    node = root;
                    parent = null;
                }
            }
        }

        setColor(node, BLACK);
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
            throw new java.util.NoSuchElementException();
        }

        return node.key;
    }

    @Override
    public Integer lastKey() {
        Node node = maximum(root);

        if (node == null) {
            throw new java.util.NoSuchElementException();
        }

        return node.key;
    }

    // Формирование новой карты из диапазона ключей
    private MyRbMap rangeMap(Integer from, boolean hasFrom,
                             Integer to, boolean hasTo) {

        MyRbMap result = new MyRbMap();

        Node current = minimum(root);

        while (current != null) {

            boolean greaterOrEqual = !hasFrom
                    || current.key.compareTo(from) >= 0;

            boolean lessThan = !hasTo
                    || current.key.compareTo(to) < 0;

            if (greaterOrEqual && lessThan) {
                result.put(current.key, current.value);
            }

            current = successor(current);
        }

        return result;
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        if (toKey == null) {
            throw new NullPointerException();
        }

        return rangeMap(null, false, toKey, true);
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        if (fromKey == null) {
            throw new NullPointerException();
        }

        return rangeMap(fromKey, true, null, false);
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        if (fromKey == null || toKey == null) {
            throw new NullPointerException();
        }

        if (fromKey.compareTo(toKey) > 0) {
            throw new IllegalArgumentException();
        }

        return rangeMap(fromKey, true, toKey, true);
    }

    // Вспомогательные методы интерфейса Map
    @Override
    public void putAll(Map<? extends Integer, ? extends String> map) {
        for (Entry<? extends Integer, ? extends String> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<Integer> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException();
    }

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
}
