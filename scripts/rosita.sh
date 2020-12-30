#/bin/bash

cd /home/pi/rosita
python3 /home/pi/rosita/rosita-java/py/serialCom.py > /home/pi/rosita/arduinoOut &

cd /home/pi/rosita/rosita-java
java -jar ./target/rosita-jar-with-dependencies.jar
