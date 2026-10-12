package src;

import java.util.ArrayList;
import java.util.List;

/**
 * Ядро командного процессора (Этап 3).
 */
public class ShellCore {
    private final String vfsPath;
    private final String scriptPath;
    private final VirtualFileSystem vfs;

    public ShellCore(String vfsPath, String scriptPath) throws Exception {
        this.vfsPath = vfsPath;
        this.scriptPath = scriptPath == null ? "" : scriptPath;
        // Загрузка VFS в оперативную память
        this.vfs = new VirtualFileSystem(vfsPath);
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
        if (cmd.equals("ls")) {
            return "[stub] ls: " + args;
        }
        if (cmd.equals("cd")) {
            return args.size() > 1 ? "cd: слишком много аргументов" : "[stub] cd: " + args;
        }

        return cmd + ": команда не найдена";
    }
}