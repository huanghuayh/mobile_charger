package org.saiko.ai.genetics.tsp;

import org.saiko.ai.genetics.tsp.TSP.NodeGrouperParameter;

public class NodeGrouper {

	
	NodeGrouperParameter parameter;
	City[] city;

	double dHorizontalStep;
	double dVerticalStep;
	public int[] groupNodeNum;
	public Point[] groupGenerator;
	public double[][] groupDistance;
	
	public class Point{
		public int x;
		public int y;
	}
	
	
	public NodeGrouper(NodeGrouperParameter parameter,City[] city){
		this.parameter=parameter;
		this.city=city;
		groupGenerator=new Point[parameter.nNumGroup];
		groupDistance=new double[parameter.nNumGroup][parameter.nNumGroup];
		groupNodeNum=new int[parameter.nNumGroup];
		
		group();
	}
	
	void group(){
		int nPartx=(int) Math.sqrt(parameter.nNumGroup); 
		int nParty=(int) Math.sqrt(parameter.nNumGroup);
		dHorizontalStep=1+(parameter.nMaxX-parameter.nMinX)/(nPartx);
		dVerticalStep=1+(parameter.nMaxY-parameter.nMinY)/(nParty);
		//find_generator(dHorizontalStep,dVerticalStep,nPartx,nParty);
		compute_group(parameter.nCostFuncID);
//		find_generator(dHorizontalStep,dVerticalStep,nPartx,nParty);
	}
	
	void find_generator(double dHorizontalStep,double dVerticalStep,int nPartx,int nParty){
		
		for(int i=0;i<groupGenerator.length;i++){
			groupGenerator[i]=new Point();
		}
		
		for(int i=0;i<city.length;i++){
			groupGenerator[city[i].nGroupID].x+=city[i].x;
			groupGenerator[city[i].nGroupID].x-=parameter.nMinX;
			groupGenerator[city[i].nGroupID].y+=city[i].y;
			groupGenerator[city[i].nGroupID].y-=parameter.nMinY;
		}
		
		for(int i=0;i<groupGenerator.length;i++){
			if(groupNodeNum[i]>0){
				groupGenerator[i].x/=groupNodeNum[i];
				groupGenerator[i].y/=groupNodeNum[i];
			}
			
		}
		
		for(int i=0;i<groupGenerator.length;i++){
			for(int j=i+1;j<groupGenerator.length;j++){
				groupDistance[i][j]=compute_distance(1,groupGenerator[i].x/100,groupGenerator[i].y/100,groupGenerator[j].x/100,groupGenerator[j].y/100);
			}
		
		}
		for(int i=0;i<groupGenerator.length;i++){
			for(int j=0;j<i;j++){
				groupDistance[i][j]=groupDistance[j][i];
			}
		}
		
		
		
	}
	
	void compute_group(int nCostFuncID){
		double dMinDistance;
		double dDistance;
		int nHoriIndex;
		int nVertiIndex;
		

		for(int i=0;i<city.length;i++){
			nHoriIndex=(int) Math.floor((city[i].x-parameter.nMinX)/dHorizontalStep);
			nVertiIndex=(int) Math.floor((city[i].y-parameter.nMinY)/dVerticalStep);
			
			city[i].nGroupID=nHoriIndex*3+nVertiIndex;
			groupNodeNum[nHoriIndex*3+nVertiIndex]++;
			
		}
	}
	
	double compute_distance(int nCostFuncID, int x1,int y1,int x2,int y2){
		double dOutput=0;
		switch(nCostFuncID){
		case 1: dOutput= Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
		break;
		}
		return dOutput;
	}
	
	
}
