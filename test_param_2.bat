@echo off
chcp 65001 > nul

rem Создание необходимых папок
if not exist bin mkdir bin
if not exist demo mkdir demo
if not exist demo\test_vfs mkdir demo\test_vfs

rem Создание стартового скрипта эмулятора (команды и ошибочная строка)
(
echo conf-dump
echo ls
echo bad_command_test
echo cd docs
) > demo\test.script

rem Компиляция исходников с кодировкой UTF-8
javac -encoding UTF-8 -d bin src\*.java

rem Запуск эмулятора с передачей обоих параметров: --vfs и --script
java -Dfile.encoding=UTF-8 -cp bin src.Main --vfs demo\test_vfs --script demo\test.script