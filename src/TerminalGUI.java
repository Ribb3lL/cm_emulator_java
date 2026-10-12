package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Графический терминал на основе Java Swing.
 */
public class TerminalGui extends JFrame {
    private final ShellCore core;
    private final JTextArea outputArea;
    private final JTextField inputField;
    private final JLabel promptLabel;

    public TerminalGui(ShellCore core) {
        this.core = core;

        // Заголовок окна содержит имя VFS
        setTitle("Shell Emulator - [" + core.getVfsName() + "]");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color bgColor = new Color(30, 30, 30);
        Color fgColor = new Color(220, 220, 220);
        Font font = new Font("Monospaced", Font.PLAIN, 14);

        outputArea = new JTextArea();
        outputArea.setBackground(bgColor);
        outputArea.setForeground(fgColor);
        outputArea.setFont(font);
        outputArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(null);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(bgColor);

        promptLabel = new JLabel(core.getVfsName() + ":" + core.getCwd() + "$ ");
        promptLabel.setForeground(new Color(50, 255, 100));
        promptLabel.setFont(font);
        promptLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));

        inputField = new JTextField();
        inputField.setBackground(bgColor);
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setFont(font);
        inputField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        bottomPanel.add(promptLabel, BorderLayout.WEST);
        bottomPanel.add(inputField, BorderLayout.CENTER);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleInput();
                }
            }
        });

        // 1. Запуск стартового скрипта при открытии окна
        runStartupScript();
    }

    public void appendText(String text) {
        outputArea.append(text + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    public void updatePrompt() {
        promptLabel.setText(core.getVfsName() + ":" + core.getCwd() + "$ ");
    }

    public void clearScreen() {
        outputArea.setText("");
    }

    private void handleInput() {
        String line = inputField.getText();
        inputField.setText("");

        appendText(promptLabel.getText() + line);
        String res = core.execute(line);

        if ("__EXIT__".equals(res)) {
            dispose();
            System.exit(0);
        } else if ("__CLEAR__".equals(res)) {
            clearScreen();
        } else if (!res.isEmpty()) {
            appendText(res);
        }
        updatePrompt();
    }

    public void runStartupScript() {
        String script = core.getScriptPath();
        // 2. Безопасная проверка на null
        if (script == null || script.trim().isEmpty()) {
            return;
        }
        java.io.File file = new java.io.File(script);
        if (!file.exists()) {
            appendText("[Ошибка скрипта]: файл '" + script + "' не найден.");
            return;
        }
        appendText("--- Выполнение стартового скрипта: " + script + " ---");
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                appendText(promptLabel.getText() + trimmed);
                String res = core.execute(trimmed);
                if ("__EXIT__".equals(res)) {
                    dispose();
                    System.exit(0);
                } else if ("__CLEAR__".equals(res)) {
                    clearScreen();
                } else if (!res.isEmpty()) {
                    appendText(res);
                }
            }
        } catch (Exception ex) {
            appendText("[Ошибка чтения скрипта]: " + ex.getMessage());
        }
        appendText("--- Завершение стартового скрипта ---\n");
        updatePrompt();
    }
}