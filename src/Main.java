package src;

import javax.swing.SwingUtilities;

/**
 * Точка входа в программу эмулятора (Этап 3).
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
            System.err.println("Ошибка: обязательный параметр --vfs не указан.");
            System.exit(1);
        }

        // Отладочный вывод параметров запуска
        System.out.println("=== Параметры запуска эмулятора ===");
        System.out.println("Путь к VFS: " + vfsPath);
        System.out.println("Путь к скрипту: " + scriptPath);
        System.out.println("===================================");

        try {
            ShellCore core = new ShellCore(vfsPath, scriptPath);
            SwingUtilities.invokeLater(() -> {
                TerminalGui gui = new TerminalGui(core);
                gui.setVisible(true);
            });
        } catch (Exception e) {
            // Обработка ошибки загрузки VFS по требованиям методички
            System.err.println("Ошибка загрузки VFS: " + e.getMessage());
            System.exit(1);
        }
    }
}