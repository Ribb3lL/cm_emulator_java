package tests;

import java.io.File;
import src.ShellCore;

/**
 * Автоматические модульные тесты для Варианта №23.
 */
public class ShellTest {
    public static void main(String[] args) {
        try {
            File demoDir = new File("demo/vfs_test_suite");
            demoDir.mkdirs();
            new File(demoDir, "folder1").mkdirs();
            new File(demoDir, "base_file.txt").createNewFile();

            ShellCore core = new ShellCore("demo/vfs_test_suite", "");

            // 1. Проверка conf-dump
            String conf = core.execute("conf-dump");
            assert conf.contains("vfs_name=vfs_test_suite") : "Ошибка теста: conf-dump";

            // 2. Проверка ls
            String ls = core.execute("ls");
            assert ls.contains("base_file.txt") && ls.contains("folder1") : "Ошибка теста: ls";

            // 3. Проверка cd
            core.execute("cd folder1");
            assert "/folder1".equals(core.getCwd()) : "Ошибка теста: cd";
            core.execute("cd ..");
            assert "/".equals(core.getCwd()) : "Ошибка теста: cd ..";

            // 4. Проверка touch в памяти
            core.execute("touch ram_only.txt");
            String lsAfterTouch = core.execute("ls");
            assert lsAfterTouch.contains("ram_only.txt") : "Ошибка теста: файл не появился в памяти";

            // КРИТИЧЕСКАЯ ПРОВЕРКА: файл НЕ должен появиться на реальном диске!
            File realDiskFile = new File("demo/vfs_test_suite/ram_only.txt");
            assert !realDiskFile.exists() : "ОШИБКА: Файл был создан на реальном диске!";

            // 5. Проверка uptime и tree
            assert core.execute("uptime").contains("up") : "Ошибка теста: uptime";
            assert core.execute("tree").contains("folder1") : "Ошибка теста: tree";

            // 6. Проверка ошибок
            assert core.execute("cd missing_folder").contains("Нет такого файла") : "Ошибка теста: cd ошибки";
            assert core.execute("touch missing_folder/f.txt").contains("Каталог не существует") : "Ошибка теста: touch ошибки";

            System.out.println("[OK] Все тесты успешно пройдены! Диск не модифицирован.");
        } catch (Throwable t) {
            System.err.println("[FAIL] Ошибка тестирования: " + t.getMessage());
            System.exit(1);
        }
    }
}