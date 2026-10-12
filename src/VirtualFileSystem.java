package src;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Виртуальная файловая система, загружаемая в память из физической папки.
 */
public class VirtualFileSystem {
    public static class Node {
        public final String name;
        public final boolean isDir;
        public byte[] content;
        public final Map<String, Node> children = new TreeMap<>();

        public Node(String name, boolean isDir) {
            this.name = name;
            this.isDir = isDir;
        }
    }

    private final String rootName;
    private final Node root;
    private String cwd = "/";

    public VirtualFileSystem(String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new IOException("Каталог не найден: " + dirPath);
        }
        if (!dir.isDirectory()) {
            throw new IOException("Указанный путь не является каталогом: " + dirPath);
        }
        this.rootName = dir.getName();
        this.root = new Node("", true);
        loadDirectory(dir, this.root);
    }

    private void loadDirectory(File source, Node target) {
        File[] items = source.listFiles();
        if (items == null) {
            return;
        }
        for (File f : items) {
            if (f.isDirectory()) {
                Node dirNode = new Node(f.getName(), true);
                target.children.put(f.getName(), dirNode);
                loadDirectory(f, dirNode);
            } else {
                Node fileNode = new Node(f.getName(), false);
                try {
                    fileNode.content = Files.readAllBytes(f.toPath());
                } catch (IOException e) {
                    fileNode.content = new byte[0];
                }
                target.children.put(f.getName(), fileNode);
            }
        }
    }

    public String getRootName() {
        return rootName;
    }

    public String getCwd() {
        return cwd;
    }

    public String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return cwd;
        }
        String combined = path.startsWith("/") ? path : (cwd.equals("/") ? "/" + path : cwd + "/" + path);
        String[] parts = combined.split("/");
        Deque<String> stack = new ArrayDeque<>();
        for (String p : parts) {
            if (p.isEmpty() || p.equals(".")) {
                continue;
            }
            if (p.equals("..")) {
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

    public Node getNode(String path) {
        String norm = normalizePath(path);
        if (norm.equals("/")) {
            return root;
        }
        String[] parts = norm.substring(1).split("/");
        Node curr = root;
        for (String p : parts) {
            if (!curr.isDir || !curr.children.containsKey(p)) {
                return null;
            }
            curr = curr.children.get(p);
        }
        return curr;
    }
}