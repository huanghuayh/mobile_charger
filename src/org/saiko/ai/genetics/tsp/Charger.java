		 package org.saiko.ai.genetics.tsp;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;

import org.saiko.ai.genetics.tsp.TSP.NodeGrouperParameter;
import org.saiko.ai.genetics.tsp.feedbackscheduler.FeedbackScheduler;
import org.saiko.ai.genetics.tsp.feedbackscheduler.FeedbackPriorityScheduler;

import sdt_scheduler.SDTScheduler;
import feedbackEDF.*;

public class Charger {
	public City currentLocation;
	public double dVelocity=(double) 1200;
	public double dChargingRate=30;
	public int nCityIndex=0;
	public int nPreviousCity=0;
	
	public double[] dTravelTimeSeries;
	public double[] dChargingTimeSeries;
	public int[] nTargetIDs;

	
	public double[][] dDistanceMatrix;
	public double dTotalDistance=0;
	public double dTotalEnergyCharged=0;
	public double[] dTravelTimeData;
	public double dTotalTime=0;
	public int nTotalLife=0;
	public double dTotalEnergy=0;
	
	
	public int nNumDeadNodes=0;
	public double dCoverage=1;
		
	public double dEnergy;
	public double dAverageLateness;
	
	public StringBuffer statOutput;
	public double[][] dCoverageData;
	public double[] dDueTimeData;
	
	
	
	public double[] dGroupEnergy;
	public double[] dGroupConsumption;
	public double[] dGroupDueTime;
	public double[][][] dGroupCoverage;
	int nNumGroup;
	public int[] nInsertIndex;
	public double[] dChargingTime;
	public boolean is_charging_time=false;
	


	
	
	public String AlgoName;
	
	

	GroupScheduler groupScheduler;
	MaxRespInsert maxRespInsert;	
	DynamicScheduler dynamicScheduler;
	StaticScheduler staticScheduler; 
	FeedbackScheduler feedbackScheduler;
	FeedbackPriorityScheduler feedbackPriorityScheduler;
	FeedbackEDF feedbackEDF;
	SDTScheduler sdt_scheduler;
	
	private TSP parentTSP;
	private City[] parentCity;

	private NodeGrouper parentNodeGrouper;
	
	public Charger(/*City[] parentCity*/TSP parentTSP,NodeGrouper nodeGrouper, String algoName){
		this.parentCity=parentTSP.cities;
		this.parentTSP=parentTSP;
		
		dDistanceMatrix=new double[parentCity.length][parentCity.length];
		init_distance_matrix();
		
		groupScheduler=new GroupScheduler();
		maxRespInsert=new MaxRespInsert();
		dynamicScheduler=new DynamicScheduler();
		 
		//staticScheduler=new StaticScheduler(parentTSP);
		
		currentLocation=parentCity[nCityIndex];
		
		statOutput=new StringBuffer();
		
		nNumGroup=nodeGrouper.groupNodeNum.length;
		parentNodeGrouper=nodeGrouper;
	//	nGroupVisitTimes=new double[nNumGroup];
		
		AlgoName=algoName;
		if(AlgoName=="SDT_CHARING"||AlgoName=="TSP_CHARGING"||AlgoName=="MWF"||AlgoName=="MWF-I"||AlgoName=="MRF"){
			is_charging_time=true;
		}
		
	
	}
	
	
	private void init_distance_matrix(){
		int temp=1;
		
		for(int i=0;i<parentCity.length;i++){
			for(int j=i+1;j<parentCity.length;j++){
				dDistanceMatrix[i][j]=computeDistance(parentCity[i].getX()/temp,parentCity[i].getY()/temp,
						parentCity[j].getX()/temp,parentCity[j].getY()/temp);
		//		dDistanceMatrix[i][j]=computeDistance(parentCity[i].getX(),parentCity[i].getY(),
		//				parentCity[j].getX(),parentCity[j].getY());
			}
		}
		for(int i=0;i<parentCity.length;i++){
			for(int j=0;j<i;j++){
				dDistanceMatrix[i][j]=dDistanceMatrix[j][i];
			}
		}
		
	}

	
	
	
	
	public void travel(){

		nPreviousCity=nCityIndex;
		selectnext();
		currentLocation=parentCity[nCityIndex];


		find_traveltime_series();

		if(is_charging_time){

			dChargingTimeSeries=find_charging_time();
		}else{
			dChargingTimeSeries=new double[dTravelTimeSeries.length];
		}
		
	}


	private double[] find_charging_time(){

		double[] charging_time_series;
		if(null==nInsertIndex){
			charging_time_series=new double[1];
			charging_time_series[0]=(double) (parentTSP.configuration.nMaxEnergy-parentTSP.cities[nCityIndex].energy);
			charging_time_series[0]=charging_time_series[0]/parentTSP.configuration.dCHARGINGRATE;
		}
		else{

			charging_time_series=new double[nInsertIndex.length+1];


			for(int i=0;i<nInsertIndex.length;i++){
				charging_time_series[i]=(double) (parentTSP.configuration.nMaxEnergy-parentTSP.cities[nInsertIndex[i]].energy);
				charging_time_series[i]=charging_time_series[i]/parentTSP.configuration.dCHARGINGRATE;
			}

			charging_time_series[charging_time_series.length-1]=(double) (parentTSP.configuration.nMaxEnergy-parentTSP.cities[nCityIndex].energy);
			charging_time_series[charging_time_series.length-1]=charging_time_series[charging_time_series.length-1]/parentTSP.configuration.dCHARGINGRATE;

		}

		return charging_time_series;
	}


	private void find_traveltime_series(){
		if(null==nInsertIndex){
			dTravelTimeSeries=new double[1];
			dTravelTimeSeries[0]=dDistanceMatrix[nPreviousCity][nCityIndex]/dVelocity;
			nTargetIDs=new int[1];
			nTargetIDs[0]=nCityIndex;

		}
		else{
			dTravelTimeSeries=new double[nInsertIndex.length+1];
			dTravelTimeSeries[0]=dDistanceMatrix[nPreviousCity][nInsertIndex[0]]/dVelocity;

			nTargetIDs=new int[nInsertIndex.length+1];
			nTargetIDs[0]=nInsertIndex[0];

			for (int i=1;i<nInsertIndex.length;i++){
				dTravelTimeSeries[i]=dDistanceMatrix[nInsertIndex[i-1]][nInsertIndex[i]]/dVelocity;
				nTargetIDs[i]=nInsertIndex[i];

			}

			dTravelTimeSeries[nInsertIndex.length]=
					dDistanceMatrix[nInsertIndex[nInsertIndex.length-1]][nCityIndex]/dVelocity;
			nTargetIDs[nInsertIndex.length]=nCityIndex;
		}
	}
	
	private void selectnext(){
		double DueThreshold=120/dVelocity*parentCity.length;
		
		int k=3;
		int[] nRoute;
		
		
		
		
		
		if("MRF"==AlgoName){
		nCityIndex=dynamicScheduler.MRF_find_next(nCityIndex, 1000000);
		}
		else if ("MRFSD"==AlgoName){
			nCityIndex=dynamicScheduler.MRFSD_find_next(nCityIndex, DueThreshold);
			
		}
		else if ("MRFG"==AlgoName){
			groupScheduler.compute_group_energy();
			int nGoupIndex=groupScheduler.find_group(parentCity[nCityIndex].nGroupID, nCityIndex);
			nCityIndex=groupScheduler.find_node_in_group(nCityIndex, nGoupIndex, DueThreshold);
		}
		else if ("MRFI"==AlgoName){
			nCityIndex=maxRespInsert.find_next(nCityIndex, DueThreshold);
		}

		else if ("MWF"==AlgoName){
			double dAlpha=0.01;
			nCityIndex=dynamicScheduler.MWF_find_next(nCityIndex,dAlpha);

		}
		else if ("MWF-I"==AlgoName){
			double dAlpha=0.4;
			int current_location=nCityIndex;
			nCityIndex=dynamicScheduler.MWF_find_next(nCityIndex,dAlpha);
			dynamicScheduler.find_insert(current_location,nCityIndex);
		}

		else if ("TSP"==AlgoName){
			if(null==staticScheduler){
				staticScheduler=new StaticScheduler(parentTSP);
			}
			nCityIndex=staticScheduler.find_next(nCityIndex);
		}
		else if("ROUTE"==AlgoName){
			nRoute=RouteScheduler.find_next(nCityIndex, DueThreshold, parentCity, k);
			nCityIndex=nRoute[nRoute.length-1];
			nInsertIndex=new int[nRoute.length-2];
			
			for(int i=0;i<nRoute.length-2;i++){//take special care: the first index is the current location.
				nInsertIndex[i]=nRoute[i+1];
			}
		}
		
		else if("FEEDBACK"==AlgoName){
			if(null==feedbackScheduler){
				feedbackScheduler=new FeedbackScheduler(parentTSP);
			}
			int [] nRouteFeedback=feedbackScheduler.find_next(nCityIndex, DueThreshold);
			if(nRouteFeedback.length>1){
				nCityIndex=nRouteFeedback[nRouteFeedback.length-1];
								
				nInsertIndex=new int[nRouteFeedback.length-1];
				
				for(int i=0;i<nRouteFeedback.length-1;i++){//take special care: the first index is the current location.
					nInsertIndex[i]=nRouteFeedback[i];
				}
			}
			else{
				nInsertIndex=null;
				nCityIndex=nRouteFeedback[0];
			}
			
			

		}
		
		else if("FEEDBACKHORIZON"==AlgoName){
			if(null==feedbackScheduler){
				feedbackScheduler=new FeedbackScheduler(parentTSP);
			}
			
			int [] nRouteFeedback=feedbackScheduler.find_next(nCityIndex, DueThreshold);
			
			
			if(nRouteFeedback.length>1){
				nCityIndex=nRouteFeedback[nRouteFeedback.length-1];
								
				nInsertIndex=new int[nRouteFeedback.length-1];
				
				for(int i=0;i<nRouteFeedback.length-1;i++){//take special care: the first index is the current location.
					nInsertIndex[i]=nRouteFeedback[i];
				}
			}
			else{
				nInsertIndex=null;
				nCityIndex=nRouteFeedback[0];
			}
			
			

		}
		
		else if("FEEDBACKEDF"==AlgoName){
			if(null==feedbackEDF){
				feedbackEDF=new FeedbackEDF(parentTSP);
			}
			
			nCityIndex= feedbackEDF.find_next(nCityIndex, DueThreshold);
			
			
		}
		
		else if("FEEDBACKPRIORITY"==AlgoName){
			if(null==feedbackPriorityScheduler){
				feedbackPriorityScheduler=new FeedbackPriorityScheduler(parentTSP);
			}
			
			int [] nRouteFeedback=feedbackPriorityScheduler.find_next(nCityIndex, DueThreshold);
			
			
			if(nRouteFeedback.length>1){
				nCityIndex=nRouteFeedback[nRouteFeedback.length-1];
								
				nInsertIndex=new int[nRouteFeedback.length-1];
				
				for(int i=0;i<nRouteFeedback.length-1;i++){//take special care: the first index is the current location.
					nInsertIndex[i]=nRouteFeedback[i];
				}
			}
			else{
				nInsertIndex=null;
				nCityIndex=nRouteFeedback[0];
			}

		}
		
		else if("SDT"==AlgoName){
			if(null==sdt_scheduler){
				sdt_scheduler=new SDTScheduler(parentTSP);
			}
			
			int [] nRouteFeedback=sdt_scheduler.find_next(nCityIndex, DueThreshold);
			
			
			if(nRouteFeedback.length>1){
				nCityIndex=nRouteFeedback[nRouteFeedback.length-1];			
				nInsertIndex=new int[nRouteFeedback.length-1];
				for(int i=0;i<nRouteFeedback.length-1;i++){//take special care: the first index is the current location.
					nInsertIndex[i]=nRouteFeedback[i];
				}
			}
			else{
				nInsertIndex=null;
				nCityIndex=nRouteFeedback[0];
			}

		}


		else if("SDT_CHARGING"==AlgoName){
			if(null==sdt_scheduler){
				sdt_scheduler=new SDTScheduler(parentTSP);
			}

			int [] nRouteFeedback=sdt_scheduler.find_next_charging_time(nCityIndex, DueThreshold);


			if(nRouteFeedback.length>1){
				nCityIndex=nRouteFeedback[nRouteFeedback.length-1];
				nInsertIndex=new int[nRouteFeedback.length-1];
				for(int i=0;i<nRouteFeedback.length-1;i++){//take special care: the first index is the current location.
					nInsertIndex[i]=nRouteFeedback[i];
				}
			}
			else{
				nInsertIndex=null;
				nCityIndex=nRouteFeedback[0];
			}

		}


		
	}
	
	
	
	
	
	private static double computeDistance(int x1,int y1,int x2,int y2){
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
	
	
	public static int find_ID_by_coordinate(int x, int y, City[] cities){
		for(int i=0;i<cities.length;i++){
			if(cities[i].getX()==x&&cities[i].getY()==y){
				return i;
			}
		}
		
		return cities.length;
		
	}
	
	
	public static class RouteScheduler{
		
		
		public static int[] find_next(int Index,double dThreshold,City[] cities,int k){
			
			int[] nFoundRoute=new int[1];
			nFoundRoute[0]=Index;
			int[] nOutput=dfs(cities,Index,0,k,nFoundRoute);
			
			return nOutput;
		}
		
		
		
		public static int[] dfs(City[] cities,int nNodeID,int nCurrentDegree, int nTargetDegree,int[] nFoundRoute){
			
			int[] nOutputRoute=new int[nTargetDegree-nCurrentDegree+1];
			int[][] nTempRoute;
			int nTempRouteIndex;
			
			if(nCurrentDegree<nTargetDegree-1){
				nTempRoute=new int[4][];
				nTempRouteIndex=0;
				
				if(cities[nNodeID].getX()<1400){
					int nTemp=find_ID_by_coordinate(cities[nNodeID].getX()+100,cities[nNodeID].getY(),cities);
					if(nTemp<cities.length){
						boolean bInsert=true;
						for(int i=0;i<nFoundRoute.length;i++){
							if(nTemp==nFoundRoute[i]){
								bInsert=false;
							}
						}
						
						if(bInsert){
							int[] nNewRoute=new int[nFoundRoute.length+1];
							for(int i=0;i<nFoundRoute.length;i++){
								nNewRoute[i]=nFoundRoute[i];
							}
							nNewRoute[nNewRoute.length-1]=nTemp;
							nTempRoute[nTempRouteIndex++]=dfs(cities,nTemp,nCurrentDegree+1,nTargetDegree,nNewRoute);
						}
						
					}			
				}
				
				if(cities[nNodeID].getX()>0){
					int nTemp=find_ID_by_coordinate(cities[nNodeID].getX()-100,cities[nNodeID].getY(),cities);
					if(nTemp<cities.length){
						boolean bInsert=true;
						for(int i=0;i<nFoundRoute.length;i++){
							if(nTemp==nFoundRoute[i]){
								bInsert=false;
							}
						}
						
						if(bInsert){
							int[] nNewRoute=new int[nFoundRoute.length+1];
							for(int i=0;i<nFoundRoute.length;i++){
								nNewRoute[i]=nFoundRoute[i];
							}
							nNewRoute[nNewRoute.length-1]=nTemp;
							nTempRoute[nTempRouteIndex++]=dfs(cities,nTemp,nCurrentDegree+1,nTargetDegree,nNewRoute);
						}
					}			
				}
				
				if(cities[nNodeID].getY()>0){
					int nTemp=find_ID_by_coordinate(cities[nNodeID].getX(),cities[nNodeID].getY()-100,cities);
					if(nTemp<cities.length){
						boolean bInsert=true;
						for(int i=0;i<nFoundRoute.length;i++){
							if(nTemp==nFoundRoute[i]){
								bInsert=false;
							}
						}
						
						if(bInsert){
							int[] nNewRoute=new int[nFoundRoute.length+1];
							for(int i=0;i<nFoundRoute.length;i++){
								nNewRoute[i]=nFoundRoute[i];
							}
							nNewRoute[nNewRoute.length-1]=nTemp;
							nTempRoute[nTempRouteIndex++]=dfs(cities,nTemp,nCurrentDegree+1,nTargetDegree,nNewRoute);
						}
					}			
				}
				
				if(cities[nNodeID].getY()<1400){
					int nTemp=find_ID_by_coordinate(cities[nNodeID].getX(),cities[nNodeID].getY()+100,cities);
					if(nTemp<cities.length){
						boolean bInsert=true;
						for(int i=0;i<nFoundRoute.length;i++){
							if(nTemp==nFoundRoute[i]){
								bInsert=false;
							}
						}
						
						if(bInsert){
							int[] nNewRoute=new int[nFoundRoute.length+1];
							for(int i=0;i<nFoundRoute.length;i++){
								nNewRoute[i]=nFoundRoute[i];
							}
							nNewRoute[nNewRoute.length-1]=nTemp;
							nTempRoute[nTempRouteIndex++]=dfs(cities,nTemp,nCurrentDegree+1,nTargetDegree,nNewRoute);
						}
					}			
				}
				
			}
			
			
			else{
				
				
				nTempRoute=new int[3][1];
				nTempRouteIndex=0;
				
				if(cities[nNodeID].getX()<1400){
					int nTemp=find_ID_by_coordinate(cities[nNodeID].getX()+100,cities[nNodeID].getY(),cities);
					if(nTemp<cities.length){
						boolean bInsert=true;
						for(int i=0;i<nFoundRoute.length;i++){
							if(nTemp==nFoundRoute[i]){
								bInsert=false;
							}
						}
						
						if(bInsert){
							nTempRoute[nTempRouteIndex++][0]=nTemp;
						}
						
					}			
				}
				
				if(cities[nNodeID].getX()>0){
					int nTemp=find_ID_by_coordinate(cities[nNodeID].getX()-100,cities[nNodeID].getY(),cities);
					if(nTemp<cities.length){
						boolean bInsert=true;
						for(int i=0;i<nFoundRoute.length;i++){
							if(nTemp==nFoundRoute[i]){
								bInsert=false;
							}
						}
						
						if(bInsert){
							nTempRoute[nTempRouteIndex++][0]=nTemp;
						}
					}			
				}
				
				if(cities[nNodeID].getY()>0){
					int nTemp=find_ID_by_coordinate(cities[nNodeID].getX(),cities[nNodeID].getY()-100,cities);
					if(nTemp<cities.length){
						boolean bInsert=true;
						for(int i=0;i<nFoundRoute.length;i++){
							if(nTemp==nFoundRoute[i]){
								bInsert=false;
							}
						}
						
						if(bInsert){
							nTempRoute[nTempRouteIndex++][0]=nTemp;
						}
					}			
				}
				
				if(cities[nNodeID].getY()<1400){
					int nTemp=find_ID_by_coordinate(cities[nNodeID].getX(),cities[nNodeID].getY()+100,cities);
					if(nTemp<cities.length){
						boolean bInsert=true;
						for(int i=0;i<nFoundRoute.length;i++){
							if(nTemp==nFoundRoute[i]){
								bInsert=false;
							}
						}
						
						if(bInsert){
							nTempRoute[nTempRouteIndex++][0]=nTemp;
						}
					}			
				}
				
			}
			
			///////////////////////////Divide finished.Conquer Begins////////////////////
			
			double dWeightMax=-10000;
			int dRouteIDTemp=-1;
			
			for(int i=0;i<nTempRouteIndex;i++){
				double dWeightTemp=0;
				for(int j=0;j<nTempRoute[i].length;j++){
					dWeightTemp-=cities[nTempRoute[i][j]].energy;
				}
				if(dWeightTemp>dWeightMax){
					dWeightMax=dWeightTemp;
					dRouteIDTemp=i;
				}
				
			}
			
			if(-1!=dRouteIDTemp){
				nOutputRoute[0]=nNodeID;
				for(int i=1;i<nOutputRoute.length;i++){
					nOutputRoute[i]=nTempRoute[dRouteIDTemp][i-1];
				}
			}
			
			
			
			
			return nOutputRoute;
		}
		
		
		
		
	}
	
	
	
	public class DynamicScheduler{
			
		public int MRFSD_find_next(int nIndex,double dThreshold){
			
			double dTempRatio=0;
			int nTempIndex=0;
			double dWeight;
			double dDueTime;
			double dNormWaitTime;
			double dDieWeight=40;
			
			for(int i=0;i<parentCity.length;i++){
				dDueTime=parentCity[i].energy/parentCity[i].consumptionRate;
				if(dDueTime<dThreshold){
					if(i!=nIndex){
						dNormWaitTime=parentCity[i].dWaitTime*parentCity[i].consumptionRate;
				//		dDueTime=parentCity[i].energy/parentCity[i].consumptionRate;
						
						dWeight=(1001-parentCity[i].energy+dDieWeight*parentCity[i].dDieTime)/dDistanceMatrix[nIndex][i];
						//dWeight=(1001-parentCity[i].energy)/dDistanceMatrix[nIndex][i];
						//dWeight=(1001-parentCity[i].energy)*(1001-parentCity[i].energy)/dDistanceMatrix[nIndex][i];
						//dWeight=(1001-parentCity[i].energy+dDieWeight*parentCity[i].dDieTime)/dDistanceMatrix[nIndex][i]/dGroupEnergy[parentCity[i].nGroupID];
						if(dWeight>dTempRatio){
							nTempIndex=i;
							dTempRatio=dWeight;
						}
						
						
					}		
				}
			}					
			return nTempIndex;	
		}
		
		public int MRF_find_next(int nIndex,double dThreshold){
			
			double dTempRatio=0;
			int nTempIndex=0;
			double dWeight;
			double dDueTime;
			for(int i=0;i<parentCity.length;i++){
				dDueTime=parentCity[i].energy/parentCity[i].consumptionRate;
				if(dDueTime<dThreshold){
					if(i!=nIndex){
						dWeight=(parentCity[i].dWaitTime*parentCity[i].dTemporalConsumptionRate)/dDistanceMatrix[nIndex][i];
						if(dWeight>dTempRatio){
							nTempIndex=i;
							dTempRatio=dWeight;
						}
					}
				}
			}					
			return nTempIndex;	
		}



		
		
		public void find_insert(int nIndex,int nTarget){
		
			nInsertIndex=null;
			double dTemppriority=-10000;
			double dTravelTime;
			double price;
				
			for(int i=0;i<parentCity.length;i++){
									
				if(i!=nIndex&&i!=nTarget){
					dTravelTime=(dDistanceMatrix[nIndex][i]+dDistanceMatrix[i][nTarget])/dVelocity;

					double duetime=parentTSP.cities[nTarget].energy/parentTSP.cities[nTarget].consumptionRate;
					
					if(duetime>dTravelTime){
						
						price=(parentTSP.configuration.nMaxEnergy-parentTSP.cities[i].energy)/parentTSP.cities[i].consumptionRate;

						if(price-dTravelTime>dTemppriority){
							nInsertIndex=new int[1];
							nInsertIndex[0]=i;
							dTemppriority=price-dTravelTime;
						}	
						
					}
					
					
					
					
				}
			}				
						
	
		}
		
		
		public int MWF_find_next(int nIndex,double dAlpha){
			

			
			double dTempRatio=100000;
			int nTempIndex=0;
			
			for(int i=0;i<parentCity.length;i++){
				if(i!=nIndex){
					double dWeight;
					dWeight=dAlpha*dDistanceMatrix[nIndex][i]/dVelocity+(1-dAlpha)*parentCity[i].energy/parentCity[i].consumptionRate;
					if(dWeight<dTempRatio){
						nTempIndex=i;
						dTempRatio=dWeight;
					}
				}		

			}		
			return nTempIndex;	
		}


		public int MWF_find_next_charging(int nIndex,double dAlpha){



			double dTempRatio=100000;
			int nTempIndex=0;

			for(int i=0;i<parentCity.length;i++){
				if(i!=nIndex){
					double dWeight;
					dWeight=dAlpha*dDistanceMatrix[nIndex][i]/dVelocity+(1-dAlpha)*parentCity[i].energy/parentCity[i].consumptionRate;
					if(dWeight<dTempRatio){
						nTempIndex=i;
						dTempRatio=dWeight;
					}
				}

			}
			return nTempIndex;
		}
		

		
		
	}
	
	public class StaticScheduler{
		TSP parentTSP;
		int[] nStaticLst;
		double loop_time;
		
		public StaticScheduler(TSP parentTSP){
			this.parentTSP=parentTSP;
			nStaticLst=new int[parentTSP.cities.length];
			construct_lst();
		}
		
		private void construct_lst(){
			
			int[] nTempLst=new int[parentTSP.cities.length];


			if(parentTSP.bestChromosome==null){
				parentTSP.run();
			}
			
			
			for(int i=0;i<parentTSP.bestChromosome.cities.length;i++){
				for(int j=0;j<parentTSP.cities.length;j++){
					
					if(is_same_city(parentTSP.cities[j],parentTSP.bestChromosome.cities[i])){
						nTempLst[i]=j;
					}
					
				}
			}
			
			for(int i=0;i<parentTSP.cities.length;i++){
				for(int j=0;j<parentTSP.cities.length;j++){
					if(i==nTempLst[j]){
						if(j==parentTSP.cities.length-1){
							nStaticLst[i]=nTempLst[0];
						}
						else{
							nStaticLst[i]=nTempLst[j+1];
						}
					}
				}
			}

			loop_time=0.0;


			int this_index=0;
			int next_index=nStaticLst[this_index];

			for(int i=0;i<nStaticLst.length;i++){
				loop_time+=dDistanceMatrix[this_index][next_index]/dVelocity;
				this_index=next_index;
				next_index=nStaticLst[this_index];
			}

			System.out.println("loop time is "+Double.toString(loop_time));
			
			
			
		}
		
		private boolean is_same_city(City c1,City c2){
			if(Math.abs(c1.x-c2.x)<5&&Math.abs(c1.y-c2.y)<5)
				return true;
			else
				return false;
		}
		
		public int find_next(int nCityIndex){
			
			return(nStaticLst[nCityIndex]);
			
			
			
//			if(nCityIndex<parentTSP.cities.length-1)
//				return nStaticLst[nCityIndex+1];
//			else
//				return nStaticLst[0];
		}
		
		
		
		
		
	}
		
	public class GroupScheduler{
		public int find_group(int nCurrentIndex,int nCity){
						
			compute_group_energy();
			int nTempIndex=nCurrentIndex;
			if(is_group_change(nCurrentIndex)){
				double dTempWeight=1000000;
				double  dWeight;
				
				for(int i=0;i<parentNodeGrouper.groupNodeNum.length;i++){
					
					if(i!=nCurrentIndex){					
						//dWeight=dGroupDueTime[i]*parentNodeGrouper.groupDistance[nCurrentIndex][i];
						dWeight=dGroupDueTime[i]*computeDistance(parentNodeGrouper.groupGenerator[i].x,parentNodeGrouper.groupGenerator[i].y,
								parentCity[nCity].x,parentCity[nCity].y);
						
						if(dWeight<dTempWeight){
							nTempIndex=i;
							dTempWeight=dWeight;
						}						
					}		
				}
				
				/*double dTempWeight=10000;
				double  dWeight;
				double alpha=1;
				
				for(int i=0;i<parentNodeGrouper.groupNodeNum.length;i++){
					
					if(i!=nCurrentIndex&&i!=2&&i!=6){					
						dWeight=alpha*dGroupDueTime[i]+(1-alpha)*parentNodeGrouper.groupDistance[nCurrentIndex][i];
						
						if(dWeight<dTempWeight){
							nTempIndex=i;
							dTempWeight=dWeight;
						}						
					}		
				}*/
				
			}
			
			return nTempIndex;

		}
		
		public void compute_group_energy(){
			dGroupEnergy=new double[nNumGroup];
			dGroupConsumption=new double[nNumGroup];
			dGroupDueTime=new double[nNumGroup];
			
			for (int i=0;i<parentCity.length;i++){
				dGroupEnergy[parentCity[i].nGroupID]+=parentCity[i].energy;
			}
			
			for (int i=0;i<parentCity.length;i++){
				dGroupConsumption[parentCity[i].nGroupID]+=parentCity[i].consumptionRate;
			}
			
			for(int i=0;i<dGroupEnergy.length;i++){
				if(dGroupConsumption[i]>0){
					dGroupDueTime[i]=dGroupEnergy[i]/dGroupConsumption[i];
				}
				
			}
		}
		
		public boolean is_group_change(int nCurrentIndex){
			double dEnergyThreshold=200;
			for (int i=0;i<parentCity.length;i++){
				if(parentCity[i].nGroupID==nCurrentIndex){
					if(parentCity[i].energy==0) return false;		
				}
				
			}
			
			if (dGroupEnergy[nCurrentIndex]/parentNodeGrouper.groupNodeNum[nCurrentIndex]<dEnergyThreshold) return false;
			else return true;
			
		}
	
		
		public int find_node_in_group(int nCurrentIndex,int nGroupIndex,double dThreshold){
			double dTempRatio=0;
			int nTempIndex=0;
			double dWeight;
			double dDueTime;
			double dNormWaitTime;
			double dDieWeight=40;
			
			for(int i=0;i<parentCity.length;i++){
				if(parentCity[i].nGroupID==nGroupIndex){
					dDueTime=parentCity[i].energy/parentCity[i].consumptionRate;
					if(dDueTime<dThreshold){
						if(i!=nCurrentIndex){
							
							dWeight=(1001-parentCity[i].energy+dDieWeight*parentCity[i].dDieTime)/dDistanceMatrix[nCurrentIndex][i];							
							if(dWeight>dTempRatio){
								nTempIndex=i;
								dTempRatio=dWeight;
							}						
						}		
					}
				}
			}		
			return nTempIndex;	
			
			
		}
	}
	
	public class MaxRespInsert{
		
		public int find_next(int nIndex,double dThreshold){
			
			double dTempRatio=0;
			int nTempIndex=0;
			double dWeight;
			double dDueTime;
			double dNormWaitTime;
			double dDieWeight=40;
			
			for(int i=0;i<parentCity.length;i++){
				dDueTime=parentCity[i].energy/parentCity[i].consumptionRate;
				if(dDueTime<dThreshold){
					if(i!=nIndex){
						
						if(dDistanceMatrix[nIndex][i]<250 ){
							//dNormWaitTime=parentCity[i].dWaitTime*parentCity[i].consumptionRate;						
							//dWeight=(1001-parentCity[i].energy+dDieWeight*parentCity[i].dDieTime)/dDistanceMatrix[nIndex][i];						
							dWeight=Math.pow((1001-parentCity[i].energy), 0.5)/dDistanceMatrix[nIndex][i];						
							if(dWeight>dTempRatio){
								nTempIndex=i;
								dTempRatio=dWeight;
							}
						}
					}		
				}
			}	
			
			dDueTime=parentCity[nTempIndex].energy/parentCity[nTempIndex].consumptionRate;
			
			nInsertIndex=null;
			double dTempTravelTime=10000;
			double dTravelTime;
			double[] dVec1;
			double[] dVec2;
			
//			if(parentCity[nTempIndex].energy>0){
//				
//				for(int i=0;i<parentCity.length;i++){
//										
//					if(i!=nIndex&&i!=nTempIndex){
//						dTravelTime=(dDistanceMatrix[nIndex][i]+dDistanceMatrix[i][nTempIndex])/dVelocity;
//						if(dTravelTime<dDueTime){
//							
//							dVec1=new double[2];
//							dVec1[0]=parentCity[nIndex].x-parentCity[i].x;
//							dVec1[1]=parentCity[nIndex].y-parentCity[i].y;
//							
//							dVec2=new double[2];
//							dVec2[0]=parentCity[nTempIndex].x-parentCity[i].x;
//							dVec2[1]=parentCity[nTempIndex].y-parentCity[i].y;
//							
//							if(dVec1[0]*dVec2[0]+dVec1[1]*dVec2[1]<0){
//								if(dTravelTime<dTempTravelTime){
//									nInsertIndex=i;
//									dTempTravelTime=dTravelTime;
//								}	
//							}																																
//						}		
//					}
//				}				
//			}	

				
				for(int i=0;i<parentCity.length;i++){
										
					if(i!=nIndex&&i!=nTempIndex){
						
							
							dVec1=new double[2];
							dVec1[0]=parentCity[nIndex].x-parentCity[i].x;
							dVec1[1]=parentCity[nIndex].y-parentCity[i].y;
							
							dVec2=new double[2];
							dVec2[0]=parentCity[nTempIndex].x-parentCity[i].x;
							dVec2[1]=parentCity[nTempIndex].y-parentCity[i].y;
							
							if(dVec1[0]*dVec2[0]+dVec1[1]*dVec2[1]<1){
								dTravelTime=(dDistanceMatrix[nIndex][i]+dDistanceMatrix[i][nTempIndex])/dVelocity;
								if(dTravelTime<dTempTravelTime){
									nInsertIndex=new int[1];
									nInsertIndex[0]=i;
									dTempTravelTime=dTravelTime;
								}	
							}																																
	
					}
				}				
						
			
			
			return nTempIndex;	
		}
		
	}
	
	
	
		
	
	
}
