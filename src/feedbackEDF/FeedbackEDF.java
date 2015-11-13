package feedbackEDF;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.saiko.ai.genetics.dijkstra.DijkstraAlgorithm;
import org.saiko.ai.genetics.dijkstra.Edge;
import org.saiko.ai.genetics.dijkstra.Graph;
import org.saiko.ai.genetics.dijkstra.Vertex;
import org.saiko.ai.genetics.tsp.TSP;
import org.saiko.ai.genetics.tsp.feedbackscheduler.*;
//import org.saiko.ai.genetics.tsp.feedbackscheduler.FeedbackScheduler.IDNotContainException;
//import org.saiko.ai.genetics.tsp.feedbackscheduler.FeedbackScheduler.IDNotContainException;


public class FeedbackEDF {

	TSP parentTSP;
	PIDController pidController;
	double MAXEDGEDISTANCE=101;
	List<Vertex> nodes;
	List<Edge> edges;
	public Graph graph;
	
	
	public FeedbackEDF(TSP parentTSP){
		this.parentTSP=parentTSP;
		init_graph(parentTSP);
		
		pidController=new PIDController(0.08,0.02,0.05);
		pidController.setSetpoint(90000);
		pidController.setInputRange(70000, 110000);
		pidController.setOutputRange(-800, 200);
		pidController.enable();
	}
	
	
	
	public int find_next(int Index,double dThreshold){
		
	double setpoint=401;
	
	pidController.getInput(parentTSP.charger.dEnergy);
	
	double dHorizon=setpoint-pidController.performPID();
	
	double dAlpha=0.001;// The parameter for the moving average
	
	double dTempRatio=100000;
	int nTempIndex=0;
	
	for(int i=0;i<parentTSP.cities.length;i++){
			if(i!=Index){
				if(parentTSP.charger.dDistanceMatrix[Index][i]<dHorizon){
					double dWeight;
					dWeight=dAlpha*parentTSP.charger.dDistanceMatrix[Index][i]+(1-dAlpha)*parentTSP.cities[i].energy/(parentTSP.cities[i].dTemporalConsumptionRate+1);
					if(dWeight<dTempRatio){
						nTempIndex=i;
						dTempRatio=dWeight;
					}
				}
			}		

	}		
	//return find_path(Index,nTempIndex);
	
	return nTempIndex;
	//return new int[]{nTempIndex};
	}
	

	public int[] find_path(int index,int target){
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		
		
		try
		 {
		     if(-1==find_lstbyid(index)||-1==find_lstbyid(target))
		     {
		          throw new IDNotContainException();
		     }
		 }
		 catch(IDNotContainException ex)
		 {
		      //Process message however you would like
		 }
		
		int nstartpoint=find_lstbyid(index);
		int ndestination=find_lstbyid(target);
		
		init_graph(parentTSP);
	    dijkstra.execute(nodes.get(nstartpoint));
	    LinkedList<Vertex> path = dijkstra.getPath(nodes.get(ndestination));
	    

	    int[] route=new int[path.size()-1];
	    
	    for(int i=0;i<path.size()-1;i++){
	    	route[i]=path.get(i+1).nodeid;
	    }
	    
		
		return route;
	}
	

	
	
	
	private void init_graph(TSP parentTSP){
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
