package sdt_scheduler;

import org.saiko.ai.genetics.tsp.TSP;

public class ClusterPriority {
	TSP parentTSP;
	
	
	
	public ClusterPriority(TSP parentTSP){
		this.parentTSP=parentTSP;
	}
	
	
	
	
	
	public int find_target(int nIndex){

		

//		double dTempDistance=dHorizon;
		double dTempPriority=-7000;
//		double dTempRatio=0;
		
		int nTempIndex=0;
		
		double[] dClusteredPriority=new double[parentTSP.cities.length];
		
		for(int i=0;i<parentTSP.cities.length;i++){

			if(i!=nIndex){

				dClusteredPriority[i]=compute_clustered_priority(i,nIndex);
				
				if(dClusteredPriority[i]>dTempPriority){
					nTempIndex=i;
					dTempPriority=dClusteredPriority[i];
				}						
			
			}		

		}
		
		
		
			
		return nTempIndex;	
	}
	
	private double compute_clustered_priority(int nTargetIndex, int nCurrentIndex){
		
		double dClusterSize;
		double dTempOutput=0;
		double dNumNodes=0.01;
					
//		dClusterSize=10*Math.sqrt(parentTSP.charger.dDistanceMatrix[nTargetIndex][nCurrentIndex])-1;
//		
//		dClusterSize=10*Math.sqrt(parentTSP.charger.dDistanceMatrix[nTargetIndex][nCurrentIndex])-1;
		
//		dClusterSize=Math.sin(Math.PI/12)*parentTSP.charger.dDistanceMatrix[nTargetIndex][nCurrentIndex];
		
		dClusterSize=parentTSP.configuration.DEFAULTCLUSTER;

		
		for(int i=0;i<parentTSP.cities.length;i++){				
			if(parentTSP.charger.dDistanceMatrix[nTargetIndex][i]<dClusterSize){
				double weight=(dClusterSize-parentTSP.charger.dDistanceMatrix[nTargetIndex][i])/dClusterSize;
				dTempOutput+=weight*(parentTSP.configuration.nMaxEnergy-parentTSP.cities[i].energy)/parentTSP.configuration.dDEFAULTCONSUMPTIONRATE;
				dNumNodes=weight+dNumNodes;
			}				
		}
		
		dTempOutput=dTempOutput/dNumNodes;		
	//	dTempOutput=dTempOutput-parentTSP.charger.dDistanceMatrix[nTargetIndex][nCurrentIndex]/parentTSP.charger.dVelocity;
		dTempOutput=dTempOutput/Math.sqrt(parentTSP.charger.dDistanceMatrix[nTargetIndex][nCurrentIndex]);
		
		return dTempOutput;
		
		
		
		
		
		
	}
}
