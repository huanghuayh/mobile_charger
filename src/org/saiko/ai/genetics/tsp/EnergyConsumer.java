package org.saiko.ai.genetics.tsp;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;


public class EnergyConsumer implements ActionListener {

	public TSP parent;
	public double dVelocity=(double) 20;
	public double dTimeInterval;
	public double[] dDueTime;
	public double[] dCoverageRatio;
	public double[] dTravelTime;
	public double[] dEnergyRecharged;
	private boolean bGUIRefresh;
	
	
	
	public EnergyConsumer(TSP tspParent,boolean bGUI){
		super();
		this.parent=tspParent;
		bGUIRefresh=bGUI;
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		parent.charger.travel();
		dDueTime=new double[parent.charger.dTravelTimeSeries.length];
		dCoverageRatio =new double[parent.charger.dTravelTimeSeries.length];
		dTravelTime=new double[parent.charger.dTravelTimeSeries.length];
		
		for(int j=0;j<parent.charger.dTravelTimeSeries.length;j++){
			parent.charger.nNumDeadNodes=0;
			for(int i=0;i<parent.cities.length;i++){
				parent.cities[i].dTemporalConsumptionRate=((double)parent.cities[i].consumptionRate)*get_consumption_rate();
			//	parent.cities[i].dTemporalConsumptionRate=((double)parent.cities[i].consumptionRate);
				parent.cities[i].energy=parent.cities[i].energy-(int) 
						(parent.cities[i].dTemporalConsumptionRate*parent.charger.dTravelTimeSeries[j]);
				if(parent.cities[i].energy<=0){
					parent.cities[i].energy=0;
					parent.cities[i].dDieTime+=parent.charger.dTravelTimeSeries[j];
					parent.charger.nNumDeadNodes++;
//					if(i!=parent.charger.nTargetIDs[j]){
//						parent.cities[i].dPIDPriority=parent.cities[i].compute_priority(parent.cities[i].energy);
//						
//					}
			//		parent.cities[i].dPIDPriority=parent.cities[i].compute_priority(parent.cities[i].energy);
			//		parent.cities[i].pidPriority.m_normalizedDerivativeTerm/=parent.charger.dTravelTimeSeries[j];
					
				}
				parent.cities[i].dWaitTime+=parent.charger.dTravelTimeSeries[j];
					
			}
			
			if(0==parent.cities[parent.charger.nTargetIDs[j]].energy){
				dDueTime[j]=0-parent.cities[parent.charger.nTargetIDs[j]].dDieTime;
				parent.charger.nNumDeadNodes--;
				parent.charger.dTotalEnergyCharged+=parent.configuration.nMaxEnergy;
			}
			else{
				dDueTime[j]=((double) parent.cities[parent.charger.nTargetIDs[j]].energy)
						/parent.cities[parent.charger.nTargetIDs[j]].consumptionRate;
				parent.charger.dTotalEnergyCharged+=parent.configuration.nMaxEnergy
						-parent.cities[parent.charger.nTargetIDs[j]].energy;
			}			
			
			parent.cities[parent.charger.nTargetIDs[j]].energy=parent.configuration.nMaxEnergy;
	//		parent.cities[parent.charger.nTargetIDs[j]].nLife++;
			parent.cities[parent.charger.nTargetIDs[j]].dDieTime=0;
			parent.cities[parent.charger.nTargetIDs[j]].dWaitTime=0;
			parent.charger.nTotalLife++;
			parent.charger.dTotalTime+=parent.charger.dTravelTimeSeries[j];
			
			
			
//			parent.cities[parent.charger.nTargetIDs[j]].dPIDPriority=
//					parent.cities[parent.charger.nTargetIDs[j]].compute_priority(parent.configuration.nMaxEnergy);
//			
			dCoverageRatio[j]=1-(double)parent.charger.nNumDeadNodes/parent.cities.length;
			dTravelTime[j]=parent.charger.dTravelTimeSeries[j];
			
		}	
		
		parent.charger.dCoverage=1-(double)parent.charger.nNumDeadNodes/parent.cities.length;
		for(int k=0;k<parent.cities.length;k++){
			parent.charger.dEnergy+=parent.cities[k].energy;
		}

		if(bGUIRefresh){
			parent.gui.createCityMap(false);
		}
	//	
		


	}

	private void update_network(double[] travel_times, City[] parentNodes){


		for(int i=0;i<travel_times.length;i++){
			for(int j=0;j<parentNodes.length;j++){

			}
		}
	}

	private void consume_energy(City node, double time){


	}



	
	private double get_consumption_rate(){
		
		double temp=Math.random()*parent.configuration.RANDOMENERGYCONSUMPTION.length;
		//return parent.configuration.RANDOMENERGYCONSUMPTION[(int)1];
		return parent.configuration.RANDOMENERGYCONSUMPTION[(int)temp];
	}
	

}
