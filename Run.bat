javac src/code/core/*.java src/code/math/*.java src/code/board/*.java src/code/ai/*.java -cp data/UI_1.0.3.jar -d bin

cd bin

jar cfm ../versions/Minesweeper.jar ../data/compiler/manifest.txt code ../data

java -jar ../versions/Minesweeper.jar

pause