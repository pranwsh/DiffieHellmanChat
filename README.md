USAGE

mkdir bin
javac -d bin @sources.txt

java -cp "bin" Server

CLIENTS
java -cp "bin" Client <ip> 9999
