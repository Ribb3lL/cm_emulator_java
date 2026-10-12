package src;

import javax.swing.SwingUtilities;

/**
 * Точка входа в эмулятор (Этап 2).
 */
public class Main {
    public static void main(String[] args) {
        String vfsPath = null;
        String scriptPath = "";

        for (int i = 0; i < args.length; i++) {
            if ("--vfs".equals(args[i]) && i + 1 < args.length) {
                vfsPath = args[++i];
            } else if ("--script".equals(args[i]) && i + 1 < args.length) {
                scriptPath = args[++i];
            }
        }

        if (vfsPath == null) {
            System.err.println("Ошибка: параметр --vfs обязателен.");
            System.exit(1);
        }

        // Отладочный вывод всех параметров при запуске
        System.out.println("=== Параметры запуска эмулятора ===");
        System.out.println("Путь к VFS: " + vfsPath);
        System.out.println("Путь к скрипту: " + scriptPath);
        System.out.println("===================================");

        final String finalVfs = vfsPath;
        final String finalScript = scriptPath;

        SwingUtilities.invokeLater(() -> {
            ShellCore core = new ShellCore(finalVfs, finalScript);
            TerminalGui gui = new TerminalGui(core);
            gui.setVisible(true);
        });
    }
}