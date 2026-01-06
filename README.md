USAGE

mkdir bin
javac -d bin @sources.txt

java -cp "bin" Server

for the clients:

java -cp "bin" Client <ip> 9999
