#!/bin/bash
# client.sh
ECE419_HOME=/cad2/ece419s/
JAVA_HOME=${ECE419_HOME}/java/jdk1.6.0/

# arguments to Mazewar
# $1 = hostname of where MazeServer is located
# $2 = port # where MazeServer is listening

${JAVA_HOME}/bin/java Mazewar $1 $2



