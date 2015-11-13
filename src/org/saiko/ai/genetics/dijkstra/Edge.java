package org.saiko.ai.genetics.dijkstra;

public class Edge  {
	  private final String id; 
	  private final Vertex source;
	  private final Vertex destination;
	  private int weight; 

	  
	  public Edge(String id, Vertex source, Vertex destination, int weight) {
	    this.id = id;
	    this.source = source;
	    this.destination = destination;
	    this.weight = weight;
	  }
	  
	  public String getId() {
	    return id;
	  }
	  public Vertex getDestination() {
	    return destination;
	  }

	  public Vertex getSource() {
	    return source;
	  }
	  public int getWeight() {
	    return weight;
	  }
	  
	  public void changeWeight(int value){
		  this.weight=value;
	  }
	  
	  @Override
	  public String toString() {
	    return source + " " + destination;
	  }
	  
	  
	} 