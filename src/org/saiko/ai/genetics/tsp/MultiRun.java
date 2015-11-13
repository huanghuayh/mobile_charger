package org.saiko.ai.genetics.tsp;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

public class MultiRun implements Runnable {

/*	
	MultiRun(TSP parent, String algoname, int ExpTimes,int RunningTimes){
		tspParent=parent;
		AlgoName=algoname;
		nExpTimes=ExpTimes;
		nRunningTimes=RunningTimes;
		
		if(tspParent.charger!=null)
			tspParent.charger=null;
		tspParent.charger=new Charger(tspParent,tspParent.nodeGrouper,AlgoName);
	}
	
*/	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	/*	for(int i=0;i<nExpTimes;i++){
			energyConsumer=new EnergyConsumer(tspParent);
	//		tspParent.charger.dCoverageData[i]=new double[nRunningTimes];////////Doing the memory assignment for the record array
	//		tspParent.charger.dDueTimeData[i]=new double[nRunningTimes];
			ActionEvent arg0=new ActionEvent(new Object(), i, AlgoName);
			
			for(int j=0;j<nRunningTimes;j++){
				energyConsumer.actionPerformed(arg0);
				tspParent.charger.dCoverageData[i][j]=tspParent.charger.dCoverage;
				tspParent.charger.dDueTimeData[i][j]=energyConsumer.dDueTime;
				compute_group_coverage(j);
			}
			
			refresh_node();
			//tspParent.charger.dVelocity+=18;
		}
*/
	}
	
	
	/*
	private void refresh_node(){
		for(int i=0;i<tspParent.cities.length;i++){
			
			tspParent.cities[i].consumptionRate=(int) (Math.random()*tspParent.configuration.nConsumptionRange+1);//initialize the consumption rate
			tspParent.cities[i].energy=tspParent.configuration.nEnergyInit;
			tspParent.cities[i].dWaitTime=tspParent.configuration.nWaitTime;
		}
	}
	
	private void compute_statistics(){
		double[] dAvgLst=new double[tspParent.charger.dDueTimeData.length];
		double[] dStdLst=new double[tspParent.charger.dDueTimeData.length];
		double[] dGroupCoverage=new double[tspParent.nodeGrouper.groupNodeNum.length];
		
		double dAvgOut;
		double dStdOfAvgOut;
		double dAvgOfStdOut;
		
		for(int i=0;i<dAvgLst.length;i++){
			dAvgLst[i]=find_average(tspParent.charger.dCoverageData[i]);
			dStdLst[i]=find_std(tspParent.charger.dCoverageData[i],dAvgLst[i]);
			
		}
		
		for(int i=0;i<dGroupCoverage.length;i++){
			//dGroupCoverage[i]=find_average(tspParent.charger.dGroupCoverage[i]);
					
		}
		
		
		dAvgOut=find_average(dAvgLst);
		dStdOfAvgOut=find_std(dAvgLst,dAvgOut);
		dAvgOfStdOut=find_average(dStdLst);
		
		String output="A "+Double.toString(dAvgOut)+
				" SoA "+Double.toString(dStdOfAvgOut)+
				" AoS "+ Double.toString(dAvgOfStdOut);
		
		export_string(output);
		//export_array(tspParent.charger.dCoverageData[0],"OneEXP");
		//export_array(dAvgLst,"asdf");
		//export_array(tspParent.charger.nGroupVisitTimes,"GroupDistribution");
		//export_array(tspParent.charger.dDueTimeData[0],"DueTime");
		//export_array(dGroupCoverage,"GroupCoverage");
	}
	
	
	private void export_array(double[] input,String name){
		File file;
		StringBuffer output=new StringBuffer();
		for(int i=0;i<input.length;i++){
			output.append(Double.toString(input[i])+"\n");
		}
		
		
		if(name==null){						
			JFileChooser chooser = new JFileChooser();
			int returnVal=chooser.showSaveDialog(null);
			if(returnVal==JFileChooser.APPROVE_OPTION){
				file =chooser.getSelectedFile();
				if(file.exists()){
					file.delete();
				}
			}
			else  
				file=new File("/home/asdf");
		}
		else{
			StringBuffer sName=new StringBuffer("/home/harry/Desktop/ChargingExp/");		
			sName.append(tspParent.charger.AlgoName);
			sName.append(name);			
			file = new File(sName.toString());
			if(file.exists()){
				file.delete();
			}
		}
					
			
		try {
			file.createNewFile();
			BufferedWriter statOutput = new BufferedWriter(new FileWriter(file));
			statOutput.write(output.toString());
			//	statOutput.flush();
			statOutput.close();
				
		} catch (IOException e1) {
				// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	private void export_string(String data){
		
		
		StringBuffer sName=new StringBuffer("/home/harry/Desktop/ChargingExp/");
		
		sName.append(tspParent.charger.AlgoName);
		sName.append("E_");
		sName.append(Integer.toString(nExpTimes));
		sName.append("R_");
		sName.append(Integer.toString(nRunningTimes));
		
		
		File file = new File(sName.toString());
		if(file.exists()){
			file.delete();
		}
		
		
		try {
			file.createNewFile();
			BufferedWriter statOutput = new BufferedWriter(new FileWriter(file));
			statOutput.write(data);
		//	statOutput.flush();
			statOutput.close();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	
	
	private double find_average(double[] input){
		if(input.length<1) return 0.0;
		
		double sum=0.0;
		for(int i=0;i<input.length;i++){
			sum+=input[i];
		}
		sum/=input.length;
		return sum;
	}
	
	private double find_std(double[] input,double avg){
		if(input.length<1) return 0.0;
		
		double sum=0.0;
		for(int i=0;i<input.length;i++){
			sum+=Math.pow((input[i]-avg), 2);
		}
		sum/=(input.length-1);
		sum=Math.sqrt(sum);
		return sum;
	}
	
	void compute_group_coverage(int nIndex){
		for(int i=0;i<tspParent.cities.length;i++){
			if(tspParent.cities[i].energy>1){
			//	tspParent.charger.dGroupCoverage[tspParent.cities[i].nGroupID][nIndex]++;						
			}
		}
		
		for(int i=0;i<tspParent.nodeGrouper.groupNodeNum.length;i++){
		//	tspParent.charger.dGroupCoverage[i][nIndex]/=tspParent.nodeGrouper.groupNodeNum[i];
		}
			
	}
*/
}
