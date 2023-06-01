javac src/code/core/*.java src/code/board/*.java src/code/ai/*.java -cp lib/*/*.jar -d bin

cd bin

jar cfm ../versions/Minesweeper.jar ../data/compiler/manifest.txt code ../data

java -jar ../versions/Minesweeper.jar

pause