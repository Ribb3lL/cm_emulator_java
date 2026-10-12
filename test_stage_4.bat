@echo off
chcp 65001 > nul

rem Создание папок
if not exist bin mkdir bin
if not exist demo\vfs_demo\docs mkdir demo\vfs_demo\docs
if not exist demo\vfs_demo\src mkdir demo\vfs_demo\src
type nul > demo\vfs_demo\file1.txt
type nul > demo\vfs_demo\docs\readme.md
type nul > demo\vfs_demo\src\app.java

rem Создание стартового скрипта с проверками всех команд Этапа 4
(
echo conf-dump
echo ls
echo uptime
echo tree
echo cd docs
echo ls
echo cd ..
echo cd non_existing_dir
echo tree docs
) > demo\stage4.script

rem Компиляция исходников
javac -encoding UTF-8 -d bin src\*.java

echo Запуск эмулятора для проверки Этапа 4...
java -Dfile.encoding=UTF-8 -cp bin src.Main --vfs demo\vfs_demo --script demo\stage4.script