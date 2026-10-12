package src;

import javax.swing.SwingUtilities;

/**
 * Точка входа в программу.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ShellCore core = new ShellCore("test_vfs");
            TerminalGui gui = new TerminalGui(core);
            gui.setVisible(true);
        });
    }
}