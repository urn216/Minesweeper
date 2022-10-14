javac src/code/core/*.java src/code/math/*.java src/code/ui/*.java src/code/ui/elements/*.java src/code/board/*.java src/code/ai/*.java -d bin

cd bin

jar cfm ../versions/Minesweeper.jar data/compiler/manifest.txt code data

start "" javaw -jar ../versions/Minesweeper.jar

//pause