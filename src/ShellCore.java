package src;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Ядро командного процессора (Этап 2).
 */
public class ShellCore {
    private final String vfsPath;
    private final String scriptPath;
    private final String vfsName;
    private String cwd = "/";

    public ShellCore(String vfsPath, String scriptPath) {
        this.vfsPath = vfsPath;
        this.scriptPath = scriptPath == null ? "" : scriptPath;
        this.vfsName = new File(vfsPath).getName();
    }

    public String getVfsPath() {
        return vfsPath;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public String getVfsName() {
        return vfsName;
    }

    public String getCwd() {
        return cwd;
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
                 + "vfs_name=" + vfsName + "\n"
                 + "cwd=" + cwd;
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