package org.saiko.ai.genetics.tsp.feedbackscheduler;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.saiko.ai.genetics.dijkstra.*;
import org.saiko.ai.genetics.tsp.City;
import org.saiko.ai.genetics.tsp.TSP;



public class FeedbackPriorityScheduler {
	
	double MAXEDGEDISTANCE=101;
	List<Vertex> nodes;
	List<Edge> edges;
	public Graph graph;
	TSP parentTSP;
	PIDController pidController;
	
	public FeedbackPriorityScheduler(TSP parentTSP){
		this.parentTSP=parentTSP;
		init_graph(parentTSP);
		
		pidController=new PIDController(15000,8000,15000);
		pidController.setSetpoint(0.95);
		pidController.setInputRange(0, 1);
		pidController.setOutputRange(201, 1401);
		pidController.enable();
		
		
	}

	public int[] find_next(int Index,double dThreshold){
		
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		
		int ntarget=find_target(Index);
		
		
		
		
		try
		 {
		     if(-1==find_lstbyid(Index)||-1==find_lstbyid(ntarget))
		     {
		          throw new IDNotContainException();
		     }
		 }
		 catch(IDNotContainException ex)
		 {
		      //Process message however you would like
		 }
		
		int nstartpoint=find_lstbyid(Index);
		int ndestination=find_lstbyid(ntarget);
		
		init_graph(parentTSP);
	    dijkstra.execute(nodes.get(nstartpoint));
	    LinkedList<Vertex> path = dijkstra.getPath(nodes.get(ndestination));
	    
//	    if(path!=null){
//	    	if(path.size()>0){
//	    		for (Vertex vertex : path) {
//	    		      System.out.println(vertex);
//	    		    }
//	    	}
//	    }

	    int[] route=new int[path.size()-1];
	    
	    for(int i=0;i<path.size()-1;i++){
	    	route[i]=path.get(i+1).nodeid;
	    }
	    
		
		return route;
	}
	
	
	private int find_target(int nIndex){

		
		double dAlpha=0.001;// The parameter for the moving average
	
		pidController.getInput(parentTSP.charger.dCoverage);
		
		double dHorizon=pidController.performPID();
		
//		dHorizon=1401;
		
//		double dTempDistance=dHorizon;
		double dTempPriority=-7000;
//		double dTempRatio=0;
		
		int nTempIndex=0;
		
		double dClusteredPriority;
		
		for(int i=0;i<parentTSP.cities.length;i++){

				if(i!=nIndex){
					
					if(parentTSP.charger.dDistanceMatrix[nIndex][i]<dHorizon){						
						
							//To use node dynamic model, implement here
						dClusteredPriority=compute_clustered_priority(i,nIndex);
						
						if(dClusteredPriority>dTempPriority){
							nTempIndex=i;
							dTempPriority=dClusteredPriority;
						}						
					}				
				}		

		}
		
		
		
			
			return nTempIndex;	
		}
		
	private double compute_clustered_priority(int nTargetIndex, int nCurrentIndex){
		double dClusterSize;
		double dTempOutput=0;
		double dNumNodes=0.1;
				
		dClusterSize=10*Math.sqrt(parentTSP.charger.dDistanceMatrix[nTargetIndex][nCurrentIndex])-1;
		

		
		for(int i=0;i<parentTSP.cities.length;i++){
				
				if(parentTSP.charger.dDistanceMatrix[nTargetIndex][i]<dClusterSize){						
					dTempOutput+=parentTSP.cities[i].dPIDPriority;
					dNumNodes++;
				}				
		

		}
		
		return dTempOutput/dNumNodes;
	}
		

	
	
	
	
	
	
	public void init_graph(TSP parentTSP){
		graph=null;
		nodes=null;
		edges=null;
		
		
		nodes=new ArrayList<Vertex>();
		edges= new ArrayList<Edge>();
		
		
		for(int i=0;i<parentTSP.cities.length;i++){
			Vertex location = new Vertex("Node_" + i, "Node_" + i);
			location.nodeid=i;
		    nodes.add(location);
		}
		
		for(int i=0;i<nodes.size();i++){
			for(int j=0;j<nodes.size();j++){
				if(i!=j){
					if(parentTSP.charger.dDistanceMatrix[nodes.get(i).nodeid][nodes.get(j).nodeid]<MAXEDGEDISTANCE){
						Edge lane = new Edge(Integer.toString(i)+j,nodes.get(i), nodes.get(j), 
								parentTSP.cities[nodes.get(j).nodeid].energy);//construct the directed edge to node i
						edges.add(lane);
					}
					
				}
				
				
				
			}
			
		}
		
		graph = new Graph(nodes, edges);
		
		
		
	}
	
	private int find_lstbyid(int cityid){
		for(int i=0;i<nodes.size();i++){
			if(cityid==nodes.get(i).nodeid){
				return i;
			}
		}
		
		return-1;
	}
	

	class IDNotContainException extends Exception
	{
	      //Parameterless Constructor
	      public IDNotContainException() {}

	      //Constructor that accepts a message
	      public IDNotContainException(String message)
	      {
	         super(message);
	      }
	 }
}
