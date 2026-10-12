@echo off
chcp 65001 > nul

rem Создание структуры папок
if not exist bin mkdir bin
if not exist tests mkdir tests
if not exist demo\vfs_demo\docs mkdir demo\vfs_demo\docs
if not exist demo\vfs_demo\src mkdir demo\vfs_demo\src
type nul > demo\vfs_demo\file1.txt
type nul > demo\vfs_demo\docs\readme.md

rem Создание финального сценария со всеми командами
(
echo conf-dump
echo ls
echo tree
echo touch created_in_ram.txt
echo touch docs\nested_ram.txt
echo ls
echo tree
echo cd docs
echo ls
echo cd ..
echo touch missing_dir\file.txt
echo uptime
) > demo\stage5.script

rem Компиляция всех файлов
javac -encoding UTF-8 -d bin src\*.java tests\*.java

echo --- [1/2] Запуск модульных тестов ---
java -ea -Dfile.encoding=UTF-8 -cp bin tests.ShellTest

echo.
echo --- [2/2] Запуск графического эмулятора ---
java -Dfile.encoding=UTF-8 -cp bin src.Main --vfs demo\vfs_demo --script demo\stage5.script