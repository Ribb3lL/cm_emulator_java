@echo off
chcp 65001 > nul

rem Создание папок для тестирования
if not exist bin mkdir bin
if not exist demo\vfs_minimal mkdir demo\vfs_minimal
if not exist demo\vfs_flat mkdir demo\vfs_flat
type nul > demo\vfs_flat\file1.txt
type nul > demo\vfs_flat\file2.txt

rem Создание VFS из 3 уровней каталогов
if not exist demo\vfs_3levels\level1\level2\level3 mkdir demo\vfs_3levels\level1\level2\level3
type nul > demo\vfs_3levels\level1\level2\level3\deep_file.txt

rem Компиляция исходников
javac -encoding UTF-8 -d bin src\*.java

echo [Тест 1]: Проверка ошибки загрузки несуществующей VFS:
java -Dfile.encoding=UTF-8 -cp bin src.Main --vfs demo\non_existing_folder

echo.
echo [Тест 2]: Запуск VFS из 3 уровней каталогов:
java -Dfile.encoding=UTF-8 -cp bin src.Main --vfs demo\vfs_3levels --script demo\test.script