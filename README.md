# NBA Network Analysis #

## About ##

This project generates and then analyzes the network consisting of NBA players connected to each other if they have been teammates in any of the last 10 years.

## Contributors ## 

Ankit Das - Yoni Nachmany - Evan Tao

## Dependencies ## 

`JSoup 1.17.1`

## Testing ##

- Compile `javac -cp .:jsoup-1.17.1.jar Main.java`
- Run `java -cp .:jsoup-1.17.1.jar Main`, and then type in the years of whichever seasons you want to include in the network. 

## Conclusions ##

- There are 1411 nodes and 31700 edges in this undirected graph. 
- The diameter is a low 3. 
- The clustering coefficient is a relatively high 0.547. 
- The degree distribution is heavy tailed.

![Degree distribution](./demo_image.png)

