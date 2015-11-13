package org.saiko.ai.genetics.tsp.feedbackscheduler;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.saiko.ai.genetics.dijkstra.*;
import org.saiko.ai.genetics.tsp.City;
import org.saiko.ai.genetics.tsp.TSP;





public class FeedbackScheduler {
	
	double MAXEDGEDISTANCE=151;
	List<Vertex> nodes;
	List<Edge> edges;
	public Graph graph;
	TSP parentTSP;
	PIDController pidController;
	
	public FeedbackScheduler(TSP parentTSP){
		this.parentTSP=parentTSP;
		init_graph(parentTSP);
		
		pidController=new PIDController(10000,5000,100);
		pidController.setSetpoint(0.05);
		pidController.setInputRange(-1, 1);

		pidController.setOutputRange(-1800, parentTSP.configuration.DEFAULTHORIZON-parentTSP.configuration.MINHORIZON);
		pidController.enable();
		
//		pidController=new PIDController(0.1,0.05,0);
//		pidController.setSetpoint(100000);
//		pidController.setInputRange(90000, 110000);
//		pidController.setOutputRange(201, 2001);
//		pidController.enable();
		
		
	}

	public int[] find_next(int Index,double dThreshold){
		
//		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		
		int ntarget=find_target(Index);
		
		
		
		
		return find_path(Index,ntarget);
	}
	
	
	public int[] find_path(int index,int target){
		
		
		
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
		
		update_edges(index,target);
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
	    dijkstra.execute(nodes.get(nstartpoint));
	    LinkedList<Vertex> path = dijkstra.getPath(nodes.get(ndestination));
	    

	    int[] route=new int[path.size()-1];
	    
	    for(int i=0;i<path.size()-1;i++){
	    	route[i]=path.get(i+1).nodeid;
	    }
	    
		
		return route;
	}
	
	
	
	private int find_target(int nIndex){

		double dCoverageDifference;
		
		try
		 {
		     if(-1==find_local_coverage(nIndex))
		     {
		          throw new IDNotContainException();
		     }
		 }
		 catch(IDNotContainException ex)
		 {
		      //Process message however you would like
		 }
		
		dCoverageDifference=find_local_coverage(nIndex)-parentTSP.charger.dCoverage;
		
		
		
	
		pidController.getInput(dCoverageDifference);
		
		double dHorizon=parentTSP.configuration.DEFAULTHORIZON-pidController.performPID();
		
		
		if("FEEDBACK"==parentTSP.charger.AlgoName){
			dHorizon=Double.MAX_VALUE;
		}
		

		
		boolean bRescue=false;
		
		double dTempDistance=dHorizon;
		double dTempPriority=Double.MAX_VALUE;
		double dTempRatio=-100000;
		
		int nTempIndex=0;				
		
		for(int i=0;i<parentTSP.cities.length;i++){

				if(i!=nIndex){
					
					if(parentTSP.charger.dDistanceMatrix[nIndex][i]<parentTSP.configuration.DEFAULTHORIZON){
					//if(parentTSP.charger.dDistanceMatrix[nIndex][i]<dHorizon){
						double dEarliness=parentTSP.cities[i].energy/parentTSP.cities[i].consumptionRate-parentTSP.charger.dDistanceMatrix[nIndex][i]/parentTSP.charger.dVelocity;
					//double dEarliness=parentTSP.cities[i].energy/parentTSP.cities[i].consumptionRate;
					//	double dEarliness=parentTSP.cities[i].energy-5;			
								
						if(dEarliness<0){
							bRescue=true;
							if(dTempPriority>parentTSP.charger.dDistanceMatrix[nIndex][i]){
								nTempIndex=i;
								dTempPriority=parentTSP.charger.dDistanceMatrix[nIndex][i];
							}
						}
						
						
					}
					
				
				}		

		}
//		
//		if(true==bRescue){
//			return nTempIndex;	
//		}
//		
//		double dDeadNodeRatio;
//		double dTempDeadNodeRatio=0;
//		for(int i=0;i<parentTSP.cities.length;i++){
//
//			if(i!=nIndex){
//				
//				if(parentTSP.charger.dDistanceMatrix[nIndex][i]<dHorizon){
//					double dEarliness=parentTSP.cities[i].energy/parentTSP.cities[i].consumptionRate-parentTSP.charger.dDistanceMatrix[nIndex][i]/parentTSP.charger.dVelocity;
//				//double dEarliness=parentTSP.cities[i].energy/parentTSP.cities[i].consumptionRate;
//				//	double dEarliness=parentTSP.cities[i].energy-5;			
//							
//					if(dEarliness<0){
//						bRescue=true;
//					
//						dDeadNodeRatio=find_local_dead(i,parentTSP.charger.dDistanceMatrix[nIndex][i]);
//						dDeadNodeRatio/=parentTSP.charger.dDistanceMatrix[nIndex][i];
//						if(dDeadNodeRatio>dTempDeadNodeRatio){
//							nTempIndex=i;
//							dTempDeadNodeRatio=dDeadNodeRatio;
//						}
//					}
//					
//					
//				}
//				
//			
//			}		
//
//		}
		
		
		
		if(true==bRescue){
			return nTempIndex;	
		}else{
			
			double dWeight=0;
			dTempRatio=Double.MIN_VALUE;
			nTempIndex=-1000;
			
			for(int i=0;i<parentTSP.cities.length;i++){
				if(i!=nIndex){
					if(parentTSP.charger.dDistanceMatrix[nIndex][i]<dHorizon){
					
						dWeight=find_local_energy(nIndex,i,parentTSP.charger.dDistanceMatrix[nIndex][i]);
					
						dWeight/=Math.pow(parentTSP.charger.dDistanceMatrix[nIndex][i], 1);
						if(dWeight>dTempRatio){
							nTempIndex=i;
							dTempRatio=dWeight;
						}
					}
				}
			}				
			return nTempIndex;	
		}
	}
	
	
	private double find_local_dead(int nTarget,double dDistance){
	
		
		double dLocalEnergy=0;
		double nNodeNumber=0;
		double dClusterSize;
		if(dDistance<parentTSP.configuration.DEFAULTHORIZON){
			dClusterSize=1;
		}
		else{
			dClusterSize=(dDistance-parentTSP.configuration.DEFAULTHORIZON)*0.2;
			dClusterSize=101;
		}
		
		dClusterSize=101;
		
		
		for(int i=0;i<parentTSP.cities.length;i++){
			if(parentTSP.charger.dDistanceMatrix[nTarget][i]<dClusterSize){

				dLocalEnergy+=(parentTSP.configuration.nMaxEnergy-parentTSP.cities[i].energy)
						*(dClusterSize-parentTSP.charger.dDistanceMatrix[nTarget][i])/dClusterSize;

				nNodeNumber+=(dClusterSize-parentTSP.charger.dDistanceMatrix[nTarget][i])/dClusterSize;


			}
		}
		
		
		if(nNodeNumber==0){
			return -1;
		}
		
		return (dLocalEnergy/(nNodeNumber));
		
		
		
/*		double dLocalEnergy=0;
		double nDeadNode=0;
		double dClusterSize;

		
		dClusterSize=parentTSP.configuration.DEFAULTCLUSTER;
		dClusterSize=1;
	
		
		for(int i=0;i<parentTSP.cities.length;i++){
			if(parentTSP.charger.dDistanceMatrix[nTarget][i]<dClusterSize){
				double dEarliness=parentTSP.cities[i].energy/parentTSP.cities[i].consumptionRate-dDistance/parentTSP.charger.dVelocity;	
				if(dEarliness<0){
					nDeadNode++;
				}
				

			}
		}
		
		return nDeadNode;*/

	}
	
	private double find_local_energy(int nStart,int nTarget,double dDistance){
		double dLocalEnergy=0;
		double nNodeNumber=0;
		double dClusterSize;
		if(dDistance<parentTSP.configuration.MINHORIZON){
			dClusterSize=1;
		}
		else{
			dClusterSize=(dDistance-parentTSP.configuration.MINHORIZON)*0.2;

		}
		

//		dClusterSize=dDistance*0.1;
		
/*		for(int i=0;i<parentTSP.cities.length;i++){
			if(parentTSP.charger.dDistanceMatrix[nTarget][i]<dClusterSize){

				dLocalEnergy+=(parentTSP.configuration.nMaxEnergy-parentTSP.cities[i].energy)
						*(dClusterSize-parentTSP.charger.dDistanceMatrix[nTarget][i])/dClusterSize;

				nNodeNumber+=(dClusterSize-parentTSP.charger.dDistanceMatrix[nTarget][i])/dClusterSize;


			}
		}*/
		
		double weight;
		for(int i=0;i<parentTSP.cities.length;i++){
			if(parentTSP.charger.dDistanceMatrix[nTarget][i]<dClusterSize){

//				dLocalEnergy+=(parentTSP.configuration.nMaxEnergy-parentTSP.cities[i].energy)
//						*(dClusterSize-parentTSP.charger.dDistanceMatrix[nTarget][i])/dClusterSize;
//
//				nNodeNumber+=(dClusterSize-parentTSP.charger.dDistanceMatrix[nTarget][i])/dClusterSize;


				weight=(parentTSP.charger.dDistanceMatrix[i][nStart]-parentTSP.charger.dDistanceMatrix[nTarget][i])/parentTSP.charger.dDistanceMatrix[i][nStart];
				
//				dLocalEnergy+=weight*(parentTSP.configuration.nMaxEnergy-parentTSP.cities[i].energy
//						
//						+parentTSP.cities[i].dDieTime*5000);
				dLocalEnergy+=weight*(parentTSP.configuration.nMaxEnergy-parentTSP.cities[i].energy);
				
				if(parentTSP.cities[i].energy-dDistance/parentTSP.charger.dVelocity<0){
					dLocalEnergy+=weight*15000;
				}
//				
				nNodeNumber+=weight;
				
			}
		}
		
		
//		if(parentTSP.cities[nTarget].energy-dDistance/parentTSP.charger.dVelocity<0){
//			dLocalEnergy+=parentTSP.cities[nTarget].dDieTime*5000;
//			nNodeNumber+=1;
//		}
		
//		if(nNodeNumber==0){
//			return -1;
//		}
		
	//	return (dLocalEnergy/(nNodeNumber*Math.pow(dDistance, 1)));
		
//		return (dLocalEnergy/(nNodeNumber)
//				-dDistance/parentTSP.charger.dVelocity*parentTSP.charger.dCoverage*parentTSP.cities.length*parentTSP.configuration.dDEFAULTCONSUMPTIONRATE);
	//	return (dLocalEnergy);
		return (dLocalEnergy);
	}
	
	
	private double find_local_coverage(int nIndex){
		double dLocalCoverage=0;
		double nNodeNumber=0;
		
		for(int i=0;i<parentTSP.cities.length;i++){
			if(parentTSP.charger.dDistanceMatrix[nIndex][i]<parentTSP.configuration.DEFAULTHORIZON){
				nNodeNumber++;
				if(1<parentTSP.cities[i].energy){
					dLocalCoverage++;
				}
			}
		}
		
		
		if(nNodeNumber==0){
			return -1;
		}
		
		return (dLocalCoverage/nNodeNumber);
		
		
		
		
	}
	
	private void update_edges(int nStart,int nTargetIndex){
		edges=null;
		graph=null;
		edges= new ArrayList<Edge>();
		
		double dTime;
		double dWeight;
		int cost;
		
		
		for(int i=0;i<nodes.size();i++){
			
			if(i!=nStart&&is_edge(nodes.get(nStart).nodeid,nodes.get(i).nodeid,nodes.get(nTargetIndex).nodeid)){
			
//				dTime=parentTSP.charger.dDistanceMatrix[nodes.get(nStart).nodeid][nodes.get(i).nodeid]/parentTSP.charger.dVelocity;
//				dWeight=parentTSP.charger.dCoverage*parentTSP.cities.length*parentTSP.configuration.dDEFAULTCONSUMPTIONRATE/2;
//				cost=(int) (dTime*dWeight-(parentTSP.configuration.nMaxEnergy-parentTSP.cities[nodes.get(i).nodeid].energy));
//	//			cost=(int) (-parentTSP.configuration.nMaxEnergy+parentTSP.cities[nodes.get(j).nodeid].energy);
//				
//				Edge lane = new Edge(Integer.toString(nStart)+i,nodes.get(nStart), nodes.get(i),cost);//construct the directed edge to node i
//				edges.add(lane);
				
				add_edge(nStart,i);
			
				for(int j=0;j<nodes.size();j++){
					if(i!=j){
						if(is_edge(nodes.get(i).nodeid,nodes.get(j).nodeid,nodes.get(nTargetIndex).nodeid)){
							
//							dTime=parentTSP.charger.dDistanceMatrix[nodes.get(i).nodeid][nodes.get(j).nodeid]/parentTSP.charger.dVelocity;
//							dWeight=parentTSP.charger.dCoverage*parentTSP.cities.length*parentTSP.configuration.dDEFAULTCONSUMPTIONRATE/2;
//							cost=(int) (dTime*dWeight-(parentTSP.configuration.nMaxEnergy-parentTSP.cities[nodes.get(j).nodeid].energy));
//				//			cost=(int) (-parentTSP.configuration.nMaxEnergy+parentTSP.cities[nodes.get(j).nodeid].energy);
//							
//							Edge lane1 = new Edge(Integer.toString(i)+j,nodes.get(i), nodes.get(j),cost);//construct the directed edge to node i
//							edges.add(lane1);
							
							add_edge(i,j);
						}
						
					}
					
				}
			
			
			
			}
		}
			
			
			
//			for(int j=0;j<nodes.size();j++){
//				if(i!=j){
//			//		if(parentTSP.charger.dDistanceMatrix[nodes.get(i).nodeid][nodes.get(nTargetIndex).nodeid]
//			//				>parentTSP.charger.dDistanceMatrix[nodes.get(j).nodeid][nodes.get(nTargetIndex).nodeid]){
//					
//						
//					if(is_edge(nodes.get(i).nodeid,nodes.get(j).nodeid,nodes.get(nTargetIndex).nodeid)){
//						
//						
//						dTime=parentTSP.charger.dDistanceMatrix[nodes.get(i).nodeid][nodes.get(j).nodeid]/parentTSP.charger.dVelocity;
//						dWeight=parentTSP.charger.dCoverage*parentTSP.cities.length*parentTSP.configuration.dDEFAULTCONSUMPTIONRATE/2;
//						cost=(int) (dTime*dWeight-(parentTSP.configuration.nMaxEnergy-parentTSP.cities[nodes.get(j).nodeid].energy));
//			//			cost=(int) (-parentTSP.configuration.nMaxEnergy+parentTSP.cities[nodes.get(j).nodeid].energy);
//						
//						Edge lane = new Edge(Integer.toString(i)+j,nodes.get(i), nodes.get(j),cost);//construct the directed edge to node i
//						edges.add(lane);
//					}
//					
//				}
//				
//				
//				
//			}
			
		
		
		graph = new Graph(nodes, edges);
		
		
	}
	
	
	private void add_edge(int i, int j){
		
		double dTime = parentTSP.charger.dDistanceMatrix[nodes.get(i).nodeid][nodes.get(j).nodeid]/parentTSP.charger.dVelocity;
		double dWeight = parentTSP.charger.dCoverage*parentTSP.cities.length*parentTSP.configuration.dDEFAULTCONSUMPTIONRATE/2;
		int cost = (int) (dTime*dWeight-200-(parentTSP.configuration.nMaxEnergy-parentTSP.cities[nodes.get(i).nodeid].energy));
	//	int	cost=(int) (100);
		
		Edge lane = new Edge(Integer.toString(i)+j,nodes.get(i), nodes.get(j),cost);//construct the directed edge to node i
		edges.add(lane);
		
	}
	
	
	private boolean is_edge(int ni, int nj,int nt){
		
		double anglecos;
		double angle;
		double vx1;
		double vy1;
		double vx2;
		double vy2;
		
		if(parentTSP.charger.dDistanceMatrix[ni][nt]>=parentTSP.charger.dDistanceMatrix[nj][ni]){
			
			
			
			vx1=parentTSP.cities[nj].getX()-parentTSP.cities[ni].getX();
			vy1=parentTSP.cities[nj].getY()-parentTSP.cities[ni].getY();
		
			vx2=parentTSP.cities[nt].getX()-parentTSP.cities[ni].getX();
			vy2=parentTSP.cities[nt].getY()-parentTSP.cities[ni].getY();
		
			anglecos=(vx1*vx2+vy1*vy2)/
					(Math.sqrt(vx1*vx1+vy1*vy1)*Math.sqrt(vx2*vx2+vy2*vy2));
			
			if(anglecos>1){
				anglecos=1;
			}
			
			if(anglecos<-1){
				anglecos=-1;
			}
			
			angle=Math.acos(anglecos);
			
			if(angle<parentTSP.configuration.DEFAULTPATHANGLE){
				
				return true;
				
			}
	//		return true;
		}
		
		return false;
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
		
/*		for(int i=0;i<nodes.size();i++){
			for(int j=0;j<nodes.size();j++){
				if(i!=j){
					if(parentTSP.charger.dDistanceMatrix[nodes.get(i).nodeid][nodes.get(j).nodeid]<MAXEDGEDISTANCE){
						Edge lane = new Edge(Integer.toString(i)+j,nodes.get(i), nodes.get(j), 
								(int) (parentTSP.charger.dDistanceMatrix[nodes.get(i).nodeid][nodes.get(j).nodeid]
										/(parentTSP.configuration.nMaxEnergy-parentTSP.cities[nodes.get(j).nodeid].energy)));//construct the directed edge to node i
						edges.add(lane);
					}
					
				}
				
				
				
			}
			
		}
		
		graph = new Graph(nodes, edges);*/
		
		
		
	}
	
	
	private double find_local_dead_node(int nIndex){
		double dClusterSize=51;
		double nDeadNum=0;
		
		
		for(int i=0;i<parentTSP.cities.length;i++){
			if(parentTSP.charger.dDistanceMatrix[nIndex][i]<dClusterSize){
				double dEarliness=parentTSP.cities[i].energy/parentTSP.cities[i].consumptionRate-
						parentTSP.charger.dDistanceMatrix[nIndex][i]/parentTSP.charger.dVelocity;
				if(dEarliness<0){
					nDeadNum++;
				}
			}
		}
		
		return nDeadNum;
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
