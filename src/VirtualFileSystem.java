package src;

import java.io.File;
import java.util.*;

/**
 * Виртуальная файловая система (VFS) в оперативной памяти (Этап 5).
 */
public class VirtualFileSystem {

    public static class Node {
        public final String name;
        public final boolean isDir;
        public final Map<String, Node> children;

        public Node(String name, boolean isDir) {
            this.name = name;
            this.isDir = isDir;
            this.children = isDir ? new TreeMap<>() : null;
        }
    }

    private final String rootName;
    private final Node root;
    private String cwd = "/";

    public VirtualFileSystem(String physicalPath) throws Exception {
        File dir = new File(physicalPath);
        if (!dir.exists()) {
            throw new IllegalArgumentException("Каталог VFS не найден: " + physicalPath);
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Указанный путь не является каталогом: " + physicalPath);
        }

        this.rootName = dir.getName();
        this.root = new Node("/", true);
        loadDirectory(dir, this.root);
    }

    public String getRootName() {
        return rootName;
    }

    public String getCwd() {
        return cwd;
    }

    private void loadDirectory(File currentFile, Node currentNode) {
        File[] files = currentFile.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                Node dirNode = new Node(f.getName(), true);
                currentNode.children.put(f.getName(), dirNode);
                loadDirectory(f, dirNode);
            } else {
                Node fileNode = new Node(f.getName(), false);
                currentNode.children.put(f.getName(), fileNode);
            }
        }
    }

    public Node getNode(String path) {
        String norm = normalizePath(path);
        if ("/".equals(norm)) {
            return root;
        }
        String[] parts = norm.substring(1).split("/");
        Node curr = root;
        for (String p : parts) {
            if (curr == null || !curr.isDir || !curr.children.containsKey(p)) {
                return null;
            }
            curr = curr.children.get(p);
        }
        return curr;
    }

    public String normalizePath(String path) {
        if (path == null || path.trim().isEmpty() || ".".equals(path)) {
            return cwd;
        }
        String combined = path.startsWith("/") ? path : (cwd.equals("/") ? "/" + path : cwd + "/" + path);
        String[] parts = combined.split("/");
        Deque<String> stack = new ArrayDeque<>();
        for (String p : parts) {
            if (p.isEmpty() || ".".equals(p)) {
                continue;
            }
            if ("..".equals(p)) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else {
                stack.push(p);
            }
        }
        List<String> list = new ArrayList<>(stack);
        Collections.reverse(list);
        return "/" + String.join("/", list);
    }

    public boolean changeDirectory(String target) {
        String norm = normalizePath(target);
        Node node = getNode(norm);
        if (node != null && node.isDir) {
            this.cwd = norm;
            return true;
        }
        return false;
    }

    public List<String> listDirectory(String target) {
        String path = (target == null || target.isEmpty()) ? cwd : target;
        Node node = getNode(path);
        if (node == null || !node.isDir) {
            return null;
        }
        return new ArrayList<>(node.children.keySet());
    }

    public List<String> buildTree(String target) {
        String path = (target == null || target.isEmpty()) ? cwd : target;
        Node node = getNode(path);
        if (node == null || !node.isDir) {
            return null;
        }
        List<String> result = new ArrayList<>();
        buildTreeRecursive(node, "", result);
        return result;
    }

    private void buildTreeRecursive(Node curr, String prefix, List<String> lines) {
        List<String> names = new ArrayList<>(curr.children.keySet());
        for (int i = 0; i < names.size(); i++) {
            boolean isLast = (i == names.size() - 1);
            String branch = isLast ? "└── " : "├── ";
            lines.add(prefix + branch + names.get(i));
            Node child = curr.children.get(names.get(i));
            if (child.isDir) {
                buildTreeRecursive(child, prefix + (isLast ? "    " : "│   "), lines);
            }
        }
    }

    /**
     * Создание файла в оперативной памяти (команда touch).
     * Физический диск не затрагивается.
     */
    public boolean touch(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        String norm = normalizePath(path);
        int lastSlash = norm.lastIndexOf('/');
        String parentPath = (lastSlash == 0) ? "/" : norm.substring(0, lastSlash);
        String fileName = norm.substring(lastSlash + 1);

        Node parent = getNode(parentPath);
        if (parent == null || !parent.isDir || fileName.isEmpty()) {
            return false;
        }

        if (!parent.children.containsKey(fileName)) {
            parent.children.put(fileName, new Node(fileName, false));
        }
        return true;
    }
}