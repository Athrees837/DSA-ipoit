
package by.it.group510972.savitskiy.lesson12;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MyAvlMap implements Map<Integer, String> {

    // Узел АВЛ-дерева
    private static class Node {
        Integer key;
        String value;

        Node left;
        Node right;

        int height;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private Node root;
    private int size;

    // Высота узла
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    // Обновление высоты узла
    private void updateHeight(Node node) {
        node.height = 1 + Math.max(
                height(node.left),
                height(node.right)
        );
    }

    // Коэффициент балансировки
    private int balanceFactor(Node node) {
        return height(node.right) - height(node.left);
    }

    // Малое левое вращение
    private Node rotateLeft(Node node) {
        Node rightChild = node.right;

        node.right = rightChild.left;
        rightChild.left = node;

        updateHeight(node);
        updateHeight(rightChild);

        return rightChild;
    }

    // Малое правое вращение
    private Node rotateRight(Node node) {
        Node leftChild = node.left;

        node.left = leftChild.right;
        leftChild.right = node;

        updateHeight(node);
        updateHeight(leftChild);

        return leftChild;
    }

    // Балансировка узла
    private Node balance(Node node) {
        if (node == null) {
            return null;
        }

        updateHeight(node);

        int factor = balanceFactor(node);

        // Правое поддерево тяжелее левого
        if (factor == 2) {
            if (balanceFactor(node.right) < 0) {
                node.right = rotateRight(node.right);
            }

            return rotateLeft(node);
        }

        // Левое поддерево тяжелее правого
        if (factor == -2) {
            if (balanceFactor(node.left) > 0) {
                node.left = rotateLeft(node.left);
            }

            return rotateRight(node);
        }

        return node;
    }

    // Вставка нового узла
    private Node insert(Node node, Integer key, String value) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }

        int comparison = key.compareTo(node.key);

        if (comparison < 0) {
            node.left = insert(node.left, key, value);
        } else if (comparison > 0) {
            node.right = insert(node.right, key, value);
        } else {
            // Если ключ уже существует, заменяем значение
            node.value = value;
            return node;
        }

        return balance(node);
    }

    // Поиск узла по ключу
    private Node findNode(Integer key) {
        Node current = root;

        while (current != null) {
            int comparison = key.compareTo(current.key);

            if (comparison == 0) {
                return current;
            }

            if (comparison < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return null;
    }

    // Поиск минимального узла
    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }

        return node;
    }

    // Удаление минимального узла
    private Node removeMin(Node node) {
        if (node.left == null) {
            return node.right;
        }

        node.left = removeMin(node.left);

        return balance(node);
    }

    // Удаление узла
    private Node delete(Node node, Integer key) {
        if (node == null) {
            return null;
        }

        int comparison = key.compareTo(node.key);

        if (comparison < 0) {
            node.left = delete(node.left, key);
        } else if (comparison > 0) {
            node.right = delete(node.right, key);
        } else {
            Node left = node.left;
            Node right = node.right;

            if (right == null) {
                return left;
            }

            Node min = findMin(right);
            min.right = removeMin(right);
            min.left = left;

            return balance(min);
        }

        return balance(node);
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        Node oldNode = findNode(key);
        String oldValue = oldNode == null ? null : oldNode.value;

        root = insert(root, key, value);

        return oldValue;
    }

    @Override
    public String remove(Object key) {
        if (key == null) {
            return null;
        }

        if (!(key instanceof Integer)) {
            return null;
        }

        Integer intKey = (Integer) key;

        Node node = findNode(intKey);

        if (node == null) {
            return null;
        }

        String oldValue = node.value;

        root = delete(root, intKey);
        size--;

        return oldValue;
    }

    @Override
    public String get(Object key) {
        if (key == null || !(key instanceof Integer)) {
            return null;
        }

        Node node = findNode((Integer) key);

        return node == null ? null : node.value;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null || !(key instanceof Integer)) {
            return false;
        }

        return findNode((Integer) key) != null;
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

    // Проверка наличия значения
    @Override
    public boolean containsValue(Object value) {
        return containsValue(root, value);
    }

    private boolean containsValue(Node node, Object value) {
        if (node == null) {
            return false;
        }

        if (value == null ? node.value == null : value.equals(node.value)) {
            return true;
        }

        return containsValue(node.left, value)
                || containsValue(node.right, value);
    }

    // Обход дерева для формирования строкового представления
    private void appendToString(Node node, StringBuilder result) {
        if (node == null) {
            return;
        }

        appendToString(node.left, result);

        if (result.length() > 1) {
            result.append(", ");
        }

        result.append(node.key).append("=").append(node.value);

        appendToString(node.right, result);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");

        appendToString(root, result);

        result.append("}");

        return result.toString();
    }

    // Остальные методы интерфейса Map

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
}
