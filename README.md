# NBA Network Analysis #

## Team ## 

Ankit Das - Yoni Nachmany - Evan Tao

## About ##

This project analyzes a network consisting of NBA players connected to each other if they have been teammates in the last 10 years. 

## Dependencies ## 

JSoup

## Testing ##

## Conclusions ##

There are 1411 nodes and 31700 edges in this undirected graph. The diameter is a low 3, the clustering coefficient is a relatively high 0.547 and the degree distribution is heavy tailed. All of this matches the atributes of a large-scale network. 

These statistics of the NBA player network do make sense. There are only 30 teams in the NBA and players frequently switch teams; thus, it is expected that the diameter of the network is that of a "small world." Additionally, neighbors of a node are expected to have an edge betwen each other because everyone on a team in a given year forms a completely connected clique. Furthermore, there is a small fraction of players that have played with many more players than average over the course of their careers -- be it through longevity in the league or frequently switching teams. 

