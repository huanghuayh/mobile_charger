package sdt_scheduler;


import org.saiko.ai.genetics.tsp.TSP;

public class SDTScheduler {

	TravelingGraph traveling_graph;
	ClusterPriority cluster_priority;
	
	public SDTScheduler(TSP parentTSP){
		traveling_graph=new TravelingGraph(parentTSP);
		cluster_priority=new ClusterPriority(parentTSP);
	}
	
	
	
	public int[] find_next(int Index,double dThreshold){
		
		int ntarget=cluster_priority.find_target(Index);
		
		
		int nstartpoint=traveling_graph.find_lstbyid(Index);
		int ndestination=traveling_graph.find_lstbyid(ntarget);
		
		try{if(-1==nstartpoint||-1==ndestination){throw new IDNotContainException();}
		 }catch(IDNotContainException ex){//Process message however you would like
			 System.out.println("problem occurs when searching for target node");
	         System.exit(0);
		 }
		

	    int[] route=traveling_graph.find_route( nstartpoint,  ndestination);
		
		return route;
	}
	
	
	

	
	
	
	
	class IDNotContainException extends Exception
	{
	      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		//Parameterless Constructor
	      public IDNotContainException() {}

	      //Constructor that accepts a message
	      public IDNotContainException(String message)
	      {
	         super(message);
	      }
	 }
	
}
