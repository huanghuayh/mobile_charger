package sdt_scheduler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.saiko.ai.genetics.dijkstra.DijkstraAlgorithm;
import org.saiko.ai.genetics.dijkstra.Edge;
import org.saiko.ai.genetics.dijkstra.Graph;
import org.saiko.ai.genetics.dijkstra.Vertex;
import org.saiko.ai.genetics.tsp.TSP;

import sdt_scheduler.SDTScheduler.IDNotContainException;

public class TravelingGraph {
	
	List<Vertex> nodes;
	List<Edge> edges;
	public Graph graph;
	double MAXEDGEDISTANCE=151;
	TSP parentTSP;
	
	
	
	public TravelingGraph(TSP parentTSP){
		this.parentTSP=parentTSP;
		nodes=null;	
		nodes=new ArrayList<Vertex>();
	
		
		for(int i=0;i<parentTSP.cities.length;i++){
			Vertex location = new Vertex("Node_" + i, "Node_" + i);
			location.nodeid=i;
		    nodes.add(location);
		}
		
		update_edge_weight(1);
		
	}
	
	
	
	public int[] find_route(int nstartpoint, int ndestination){
		

		
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		
		//update_edge_weight( ndestination);
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
	
	public void update_edge_weight( int ndestination){
		graph=null;
		edges=null;
		edges= new ArrayList<Edge>();		
		
		for(int i=0;i<nodes.size();i++){
			for(int j=0;j<nodes.size();j++){
				if(i!=j){
					double travel_distance=parentTSP.charger.dDistanceMatrix[nodes.get(i).nodeid][nodes.get(j).nodeid];
					if(travel_distance<MAXEDGEDISTANCE){
						double time_spent=(parentTSP.cities[nodes.get(j).nodeid].energy)/parentTSP.configuration.dDEFAULTCONSUMPTIONRATE;
						double travel_time=travel_distance/parentTSP.charger.dVelocity;
						int cost=(int) (travel_time+time_spent);

						double energy_income=((double)parentTSP.configuration.nMaxEnergy-(double)parentTSP.cities[nodes.get(j).nodeid].energy);
						travel_time=travel_distance/parentTSP.charger.dVelocity;
						double charging_time=((double)parentTSP.configuration.nMaxEnergy-(double)parentTSP.cities[nodes.get(j).nodeid].energy)/parentTSP.configuration.dCHARGINGRATE;
						double energy_cost=(travel_time)*parentTSP.configuration.dDEFAULTCONSUMPTIONRATE*parentTSP.cities.length;
						cost=(int) (energy_cost-energy_income);
						Edge lane = new Edge(Integer.toString(i)+j,nodes.get(i), nodes.get(j),cost);//construct the directed edge to node i
						edges.add(lane);
					}
				}
			}	
		}
		graph = new Graph(nodes, edges);

	}

	public void update_edge_weight_charging_time( int ndestination){
		graph=null;
		edges=null;
		edges= new ArrayList<Edge>();

		for(int i=0;i<nodes.size();i++){
			for(int j=0;j<nodes.size();j++){
				if(i!=j){
					double travel_distance=parentTSP.charger.dDistanceMatrix[nodes.get(i).nodeid][nodes.get(j).nodeid];
					if(travel_distance<MAXEDGEDISTANCE){
						double energy_income=((double)parentTSP.configuration.nMaxEnergy-(double)parentTSP.cities[nodes.get(j).nodeid].energy);
						double travel_time=travel_distance/parentTSP.charger.dVelocity;
//						double charging_time=((double)parentTSP.configuration.nMaxEnergy-(double)parentTSP.cities[nodes.get(j).nodeid].energy)/parentTSP.configuration.dCHARGINGRATE;
						double charging_time=0;
						double energy_cost=(travel_time+charging_time)*parentTSP.configuration.dDEFAULTCONSUMPTIONRATE*parentTSP.cities.length;
						int cost=(int) (energy_cost-energy_income);
						Edge lane = new Edge(Integer.toString(i)+j,nodes.get(i), nodes.get(j),cost);//construct the directed edge to node i
						edges.add(lane);
					}
				}
			}
		}
		graph = new Graph(nodes, edges);

	}
	
	public int find_lstbyid(int cityid){
		for(int i=0;i<nodes.size();i++){
			if(cityid==nodes.get(i).nodeid){
				return i;
			}
		}
		
		return-1;
	}
	
	
	
	
	
	
	
	
	

}
