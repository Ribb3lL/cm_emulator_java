package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ядро командного процессора эмулятора оболочки.
 */
public class ShellCore {
    private final String vfsName;
    private String cwd = "/";

    public ShellCore(String vfsName) {
        this.vfsName = vfsName;
    }

    public String getVfsName() {
        return vfsName;
    }

    public String getCwd() {
        return cwd;
    }

    /**
     * Парсер: разделяет ввод на токены по пробелам.
     */
    public List<String> parse(String line) {
        List<String> tokens = new ArrayList<>();
        if (line == null || line.trim().isEmpty()) {
            return tokens;
        }
        String[] parts = line.trim().split(" ");
        for (String p : parts) {
            if (!p.isEmpty()) {
                tokens.add(p);
            }
        }
        return tokens;
    }

    /**
     * Выполняет команду и возвращает текстовый вывод.
     */
    public String execute(String line) {
        List<String> tokens = parse(line);
        if (tokens.isEmpty()) {
            return "";
        }

        String cmd = tokens.get(0);
        List<String> args = tokens.subList(1, tokens.size());

        if (cmd.equals("exit")) {
            if (!args.isEmpty()) {
                return "exit: слишком много аргументов";
            }
            return "__EXIT__";
        }
        if (cmd.equals("ls")) {
            return "[stub] ls вызван с аргументами: " + args;
        }
        if (cmd.equals("cd")) {
            if (args.size() > 1) {
                return "cd: слишком много аргументов";
            }
            return "[stub] cd вызван с аргументами: " + args;
        }

        return cmd + ": команда не найдена";
    }
}