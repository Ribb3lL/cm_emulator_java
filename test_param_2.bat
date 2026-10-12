@echo off
chcp 65001 > nul

if not exist bin mkdir bin
if not exist demo mkdir demo
if not exist demo\test_vfs mkdir demo\test_vfs

(
echo conf-dump
echo ls
echo bad_command_test
echo cd docs
) > demo\test.script

javac -encoding UTF-8 -d bin src\*.java
java -Dfile.encoding=UTF-8 -cp bin src.Main --vfs demo\test_vfs --script demo\test.script