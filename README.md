# NBA Network Analysis #

## About ##

This project analyzes a network consisting of NBA players connected to each other if they have been teammates in the 2011-2012 or 2012-2013 seasons.

## Contributors ## 

Ankit Das - Yoni Nachmany - Evan Tao

## Dependencies ## 

`JSoup 1.17.1`

## Testing ##

- Compile `javac -cp .:jsoup-1.17.1.jar Main.java`
- Run `java -cp .:jsoup-1.17.1.jar Main`

## Conclusions ##

There are 1411 nodes and 31700 edges in this undirected graph. The diameter is a low 3, the clustering coefficient is a relatively high 0.547 and the degree distribution is heavy tailed. This matches the attributes of a large-scale network. 

