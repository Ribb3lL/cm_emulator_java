package src;

import java.util.ArrayList;
import java.util.List;

/**
 * Ядро командного процессора (Этап 5).
 */
public class ShellCore {
    private final String vfsPath;
    private final String scriptPath;
    private final VirtualFileSystem vfs;
    private final long startTime;

    public ShellCore(String vfsPath, String scriptPath) throws Exception {
        this.vfsPath = vfsPath;
        this.scriptPath = (scriptPath == null) ? "" : scriptPath;
        this.vfs = new VirtualFileSystem(vfsPath);
        this.startTime = System.currentTimeMillis();
    }

    public String getVfsPath() {
        return vfsPath;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public String getVfsName() {
        return vfs.getRootName();
    }

    public String getCwd() {
        return vfs.getCwd();
    }

    public List<String> parse(String line) {
        List<String> tokens = new ArrayList<>();
        if (line == null || line.trim().isEmpty()) {
            return tokens;
        }
        for (String p : line.trim().split(" ")) {
            if (!p.isEmpty()) {
                tokens.add(p);
            }
        }
        return tokens;
    }

    public String execute(String line) {
        List<String> tokens = parse(line);
        if (tokens.isEmpty()) {
            return "";
        }

        String cmd = tokens.get(0);
        List<String> args = tokens.subList(1, tokens.size());

        if (cmd.equals("exit")) {
            return args.isEmpty() ? "__EXIT__" : "exit: слишком много аргументов";
        }
        if (cmd.equals("conf-dump")) {
            return "vfs_path=" + vfsPath + "\n"
                 + "script_path=" + scriptPath + "\n"
                 + "vfs_name=" + getVfsName() + "\n"
                 + "cwd=" + getCwd();
        }
        if (cmd.equals("clear")) {
            return "__CLEAR__";
        }
        if (cmd.equals("uptime")) {
            long seconds = (System.currentTimeMillis() - startTime) / 1000;
            return "up " + seconds + " seconds, 1 user";
        }
        if (cmd.equals("ls")) {
            String path = args.isEmpty() ? "" : args.get(0);
            List<String> items = vfs.listDirectory(path);
            if (items == null) {
                return "ls: невозможно получить доступ к '" + path + "': Нет такого файла или каталога";
            }
            return String.join("  ", items);
        }
        if (cmd.equals("cd")) {
            if (args.isEmpty()) {
                vfs.changeDirectory("/");
                return "";
            }
            if (args.size() > 1) {
                return "cd: слишком много аргументов";
            }
            if (!vfs.changeDirectory(args.get(0))) {
                return "cd: " + args.get(0) + ": Нет такого файла или каталога";
            }
            return "";
        }
        if (cmd.equals("tree")) {
            String path = args.isEmpty() ? "" : args.get(0);
            List<String> lines = vfs.buildTree(path);
            if (lines == null) {
                return "tree: [" + path + "] ошибка открытия каталога";
            }
            String rootLabel = path.isEmpty() ? getCwd() : vfs.normalizePath(path);
            if (lines.isEmpty()) {
                return rootLabel + "\n(каталог пуст)";
            }
            return rootLabel + "\n" + String.join("\n", lines);
        }
        if (cmd.equals("touch")) {
            if (args.isEmpty()) {
                return "touch: пропущен операнд файла";
            }
            for (String file : args) {
                if (!vfs.touch(file)) {
                    return "touch: невозможно выполнить touch для '" + file + "': Каталог не существует";
                }
            }
            return "";
        }

        return cmd + ": команда не найдена";
    }
}