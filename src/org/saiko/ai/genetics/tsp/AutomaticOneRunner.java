

package org.saiko.ai.genetics.tsp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.tools.ant.types.Path;
import org.saiko.ai.genetics.tsp.TSPConfiguration.AlgorithmName;

import algorithms.Mergesort;

public class AutomaticOneRunner implements ActionListener {
	TSP tspParent;
	EnergyConsumer energyConsumer;
	int nExpTimes;
	int nRunningTimes;
	int nEventID;
	boolean bEventHappened=false;
	boolean bEventTimeRecorded=false;
	
	boolean bEventRecovered=false;
	
	double dEventStartTime;
	double dEventEndTime;
	
	
	double[] dTravelTimeData;
	double[] dCoverageData;
	double[] dEnergyData;
	double[] dLatenessData;
	
	int nCoverageDataIndex;
	public int nDuetimeDataIndex;
	
	String CoverageOutput="";
	String LatenessOutput="";
	String EnergyOutput="";
	String EfficiencyOutput="";
	
	String ScaleCoverageOutput="";
	String ScaleTardinessOutput="";
	String ScaleEfficiencyOutput="";
	
	
	int nAlgorithmIndex=1;
	String SpeedData="";
	
	String TransientResponseOutput="";
	int nTransientIndex;
	
	
	public AutomaticOneRunner(TSP parent){
		tspParent=parent;
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

		String input =  JOptionPane.showInputDialog("Number of experiment rounds","10000");
		nRunningTimes=Integer.parseInt(input);
		String expNum=JOptionPane.showInputDialog("Number of Speeds","3");
		nExpTimes=Integer.parseInt(expNum);
		
		input =  JOptionPane.showInputDialog("ID of event","0");
		nEventID=Integer.parseInt(input);
		
		
		
		double speed=2402;
		//double speed=1201;
		for(int i=0;i<nExpTimes;i++){
			
			
					

			ScaleCoverageOutput+="\n";
			ScaleTardinessOutput+="\n";
			ScaleEfficiencyOutput+="\n";
			nAlgorithmIndex=1;
			
			
			
			SpeedData+="\n"+Integer.toString((int)(speed/1000))+" ";
			
			
			for(AlgorithmName n: AlgorithmName.values()){

				tspParent.charger=null;
				tspParent.charger=new Charger(tspParent,tspParent.nodeGrouper,n.name);			
				tspParent.charger.dDueTimeData=new double[nRunningTimes];
				nDuetimeDataIndex=0;
				dTravelTimeData=new double[nRunningTimes];
				dCoverageData=new double[nRunningTimes];
				dEnergyData=new double[nRunningTimes];
				dLatenessData=new double[nRunningTimes];
				nCoverageDataIndex=0;

				tspParent.charger.dVelocity=speed;//To change the velocity of MC

				energyConsumer=null;
				energyConsumer=new EnergyConsumer(tspParent,false);				
				
				bEventHappened=false;
				bEventRecovered=false;
				bEventTimeRecorded=false;
					
					for(int j=0;j<nRunningTimes;j++){
						
						
						event_change();
						
						energyConsumer.actionPerformed(arg0);
						
						record_coverage();
						record_duetime();
						
						record_event_end_time();

						if(tspParent.charger.nTotalLife>nRunningTimes) break;
	
					}

				refresh_node();
				
				compute_statistics(n.name);
				export_transient_data();
				nAlgorithmIndex++;
				
			}

			
			export_coverage(CoverageOutput);
			export_lateness(LatenessOutput);
			export_efficiency(EfficiencyOutput);
			export_energy(EnergyOutput);
			
			speed/=1.414;
//			nAlgorithmIndex=1;
//			CoverageOutput="";
		}
		export_string(SpeedData,"speed");
		export_string(ScaleCoverageOutput,"scale_coverage");
		export_string(ScaleTardinessOutput,"scale_tardiness");
		export_string(ScaleEfficiencyOutput,"scale_efficiency");
		
		tspParent.gui.createCityMap(false);
		
		
	}
	
	
	
	private void event_change(){
		
		
		if(1==nEventID||2==nEventID||3==nEventID||4==nEventID){//EventID==0 means no event will happen
			if(bEventHappened==false){ //Event not happened yet.
				if(tspParent.charger.dTotalTime>200){// This is what needs to be checked
					dEventStartTime=tspParent.charger.dTotalTime;
					
					
					if(1==nEventID){
						light_impulse_event();
						bEventHappened=true;
					}
					else if(2==nEventID){
						medium_impulse_event();
						bEventHappened=true;
					}
					else if(3==nEventID){
						large_impulse_event();
						bEventHappened=true;
					}
					else if (4==nEventID){
						step_event();
						bEventHappened=true;
					}
					
				}
			}
		}
		
		
		if(false==bEventRecovered){
			if(4==nEventID&&bEventHappened==true&&tspParent.charger.dTotalTime>400){
				step_event_recover();
				bEventRecovered=true;
			}
		}
	}
	
	
	private void record_event_end_time(){
		if(false==bEventTimeRecorded){
			if(bEventHappened){
				if(dCoverageData[nCoverageDataIndex-1]>0.9){
					dEventEndTime=tspParent.charger.dTotalTime;
					bEventTimeRecorded=true;
				}
					
			}
		}
	}
	
	
	private void export_string(String data,String name){
		StringBuffer sName=new StringBuffer("/home/harry/Desktop/ChargingExp/");
		sName.append(name);
		sName.append(".txt");
		
		
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
	
	private void export_lateness(String data){
		
		
		StringBuffer sName=new StringBuffer("/home/harry/Desktop/ChargingExp/");
		sName.append(Double.toString(tspParent.charger.dVelocity));
		sName.append("onetime_lateness.txt");
		
		
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
	
	
     private void export_coverage(String data){
		
		
		StringBuffer sName=new StringBuffer("/home/harry/Desktop/ChargingExp/");
		sName.append(Double.toString(tspParent.charger.dVelocity));
		sName.append("onetime_coverage.txt");
		
		
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
	
     private void export_efficiency(String data){
 		
 		
 		StringBuffer sName=new StringBuffer("/home/harry/Desktop/ChargingExp/");
 		sName.append(Double.toString(tspParent.charger.dVelocity));
 		sName.append("onetime_efficiency.txt");
 		
 		
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
     
     private void export_energy(String data){
 		
 		
 		StringBuffer sName=new StringBuffer("/home/harry/Desktop/ChargingExp/");
 		sName.append(Double.toString(tspParent.charger.dVelocity));
 		sName.append("onetime_energy.txt");
 		
 		
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

     
     
	private void record_coverage(){
		for(int i=0;i<energyConsumer.dCoverageRatio.length;i++){
			if(nCoverageDataIndex==10000){
				break;
			}
			
			dCoverageData[nCoverageDataIndex]=energyConsumer.dCoverageRatio[i];
			for(int j=0;j<tspParent.cities.length;j++){
				dEnergyData[nCoverageDataIndex]+=tspParent.cities[j].energy;
				dLatenessData[nCoverageDataIndex]+=tspParent.cities[j].dDieTime;
			}
			dLatenessData[nCoverageDataIndex]/=tspParent.cities.length;
			dEnergyData[nCoverageDataIndex]/=tspParent.cities.length;
			dEnergyData[nCoverageDataIndex]/=10;
			
			
			

			dTravelTimeData[nCoverageDataIndex]=energyConsumer.dTravelTime[i];
			nCoverageDataIndex++;
		}
	}
     
     
	private void record_duetime(){
		for(int i=0;i<energyConsumer.dDueTime.length;i++){
			if(nDuetimeDataIndex==10000){
				break;
			}
			tspParent.charger.dDueTimeData[nDuetimeDataIndex++]=energyConsumer.dDueTime[i];
		}
	}
	
	
	
	private void light_impulse_event(){
		for(int i=0;i<tspParent.cities.length;i++){
			if(1==tspParent.cities[i].nGroupID){
				tspParent.cities[i].energy=0;
			   }		
			
		   }
	}
	
	private void medium_impulse_event(){
		for(int i=0;i<tspParent.cities.length;i++){
			if(1==tspParent.cities[i].nGroupID||7==tspParent.cities[i].nGroupID){
				tspParent.cities[i].energy=0;
			   }		
			
		   }
	}
	
	private void large_impulse_event(){
		for(int i=0;i<tspParent.cities.length;i++){
			if(1==tspParent.cities[i].nGroupID||7==tspParent.cities[i].nGroupID||4==tspParent.cities[i].nGroupID){
				tspParent.cities[i].energy=0;
			   }		
			
		   }
	}
	
	private void step_event(){
		for(int i=0;i<tspParent.cities.length;i++){
				tspParent.cities[i].consumptionRate*=2;
		
			
		   }
	}
	
	private void step_event_recover(){
		for(int i=0;i<tspParent.cities.length;i++){
				tspParent.cities[i].consumptionRate/=2;
		
			
		   }
	}
	

	

	private void refresh_node(){
		for(int i=0;i<tspParent.cities.length;i++){
			
//			tspParent.cities[i].consumptionRate=(int) (Math.random()*tspParent.configuration.nConsumptionRange+1);//initialize the consumption rate

		//	tspParent.cities[i].set_energy();
			tspParent.cities[i].energy=tspParent.configuration.nEnergyInit;
			tspParent.cities[i].dWaitTime=tspParent.configuration.nWaitTime;
			
			if("random"==tspParent.ConsumptionPattern){
				tspParent.gui.menu.setRandomRate();
			}
			else if("group"==tspParent.ConsumptionPattern){
				tspParent.gui.menu.setGroupRate();
			}
			else{
				tspParent.gui.menu.setCoordinateRate();
			}
			
		}
		
		tspParent.charger.nTotalLife=0;
	}
	
	private void compute_statistics(String name){
		
	//	export_array(dCoverageData,"coverage",tspParent.charger.AlgoName);
	//	export_array(dTravelTimeData,"time",tspParent.charger.AlgoName);
//		process_time_data();
		export_coverage_trend();
		export_energy_trend();
		travelling_time_trend();
		export_duetime_cluster();
		export_duetime_data();
		
		
		
		
	//	double dAvgOut=find_average(dCoverageData);
		double dAvgOut=find_weighted_average(dCoverageData,dTravelTimeData);
		double dStd=find_std(dCoverageData,dTravelTimeData,dAvgOut);
		
		double dAvgLatenessOut=find_weighted_average(dLatenessData,dTravelTimeData);
		double dStdLatenessOut=find_std(dLatenessData,dTravelTimeData,dAvgLatenessOut);
		
		
		
		
		
		double dAvgEnergyout=find_weighted_average(dEnergyData,dTravelTimeData);
		
		
		ScaleCoverageOutput+=Integer.toString(nAlgorithmIndex+25-(int)(Math.floor(tspParent.charger.dVelocity/100)*1.25))
				+" "+Double.toString(dAvgOut)+" "+Double.toString(dStd)+" "+tspParent.charger.AlgoName+" ";
		
		ScaleTardinessOutput+=Integer.toString(nAlgorithmIndex+25-(int)(Math.floor(tspParent.charger.dVelocity/100)*1.25))
				+" "+Double.toString(dAvgLatenessOut)+" "+Double.toString(dStdLatenessOut)+" "+tspParent.charger.AlgoName+" ";
		
		ScaleEfficiencyOutput+=Integer.toString(nAlgorithmIndex+25-(int)(Math.floor(tspParent.charger.dVelocity/100)*1.25))
				+" "+Double.toString(tspParent.charger.dTotalEnergyCharged/tspParent.charger.dTotalDistance)
				+" "+tspParent.charger.AlgoName+" ";
		
		
		
		CoverageOutput+=Integer.toString(nAlgorithmIndex)+" "+Double.toString(dAvgOut)+" "+
				Double.toString(dStd)+" "+tspParent.charger.AlgoName+"\n";
		
		LatenessOutput+=Integer.toString(nAlgorithmIndex)+" "+Double.toString(dAvgLatenessOut)+" "
		+Double.toString(dStdLatenessOut)+" "+tspParent.charger.AlgoName+"\n";
		
		EfficiencyOutput+=Integer.toString(nAlgorithmIndex)+" "
		+Double.toString(tspParent.charger.dTotalEnergyCharged/tspParent.charger.dTotalDistance)+" "
				+tspParent.charger.AlgoName+"\n";
		
		EnergyOutput+=Integer.toString(nAlgorithmIndex)+" "
				+Double.toString(dAvgEnergyout)+" "
						+tspParent.charger.AlgoName+"\n";
		
		SpeedData+=Double.toString(dAvgOut)+" ";
		

	}
	
	void export_transient_data(){
		double dAvgOut=find_average(dCoverageData);
		TransientResponseOutput+=Integer.toString(nEventID)+" "+Integer.toString(nAlgorithmIndex)+" "+Double.toString(dEventEndTime-dEventStartTime);
		
		
		StringBuffer sName=new StringBuffer("/home/harry/Desktop/ChargingExp/");
//		sName.append(Double.toString(tspParent.charger.dVelocity));
		sName.append("transient_data.txt");
		
		
		File file = new File(sName.toString());
		if(file.exists()){
			file.delete();
		}
		
		
		try {
			file.createNewFile();
			BufferedWriter statOutput = new BufferedWriter(new FileWriter(file));
			statOutput.write(TransientResponseOutput);
		//	statOutput.flush();
			statOutput.close();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
/*	private void process_time_data(){
		double[] dProcessedTravelData=new double[dTravelTimeData.length];
		for(int i=0;i<dProcessedTravelData.length;i++){
			
			if(i!=0){
				dTravelTimeData[i]=dTravelTimeData[i-1]+dTravelTimeData[i];
			}
			
			dProcessedTravelData[i]=dTravelTimeData[i];
		}
		
		dTravelTimeData=null;
		dTravelTimeData=dProcessedTravelData;
	}*/
	
	private void travelling_time_trend(){
		
		double[] dProcessedTravelData=new double[dTravelTimeData.length];
		
		dProcessedTravelData[0]=dTravelTimeData[0];
		
		for(int i=1;i<dProcessedTravelData.length;i++){
			
			
				dProcessedTravelData[i]=dProcessedTravelData[i-1]+dTravelTimeData[i];
			

		}
		
		
		export_2d_array(dProcessedTravelData,dTravelTimeData,"travelling_time"+Integer.toString(nEventID),tspParent.charger.AlgoName);
	}
	
	private void export_coverage_trend(){
		
		double[] dProcessedTravelData=new double[dTravelTimeData.length];
		
		dProcessedTravelData[0]=dTravelTimeData[0];
		
		for(int i=1;i<dProcessedTravelData.length;i++){
			
			
				dProcessedTravelData[i]=dProcessedTravelData[i-1]+dTravelTimeData[i];
			

		}
		
		
		export_2d_array(dProcessedTravelData,dCoverageData,"trend"+Integer.toString(nEventID),tspParent.charger.AlgoName);
	}
	
	

	private void export_energy_trend(){

		double[] dProcessedTravelData=new double[dTravelTimeData.length];
		
		dProcessedTravelData[0]=dTravelTimeData[0];
		
		for(int i=1;i<dProcessedTravelData.length;i++){
			
			
				dProcessedTravelData[i]=dProcessedTravelData[i-1]+dTravelTimeData[i];
			

		}
		
		export_2d_array(dProcessedTravelData,dEnergyData,"energy_trend"+Integer.toString(nEventID),tspParent.charger.AlgoName);
	}
	
	
	private void export_lateness_trend(){

		double[] dProcessedTravelData=new double[dTravelTimeData.length];
		
		dProcessedTravelData[0]=dTravelTimeData[0];
		
		for(int i=1;i<dProcessedTravelData.length;i++){
			
			
				dProcessedTravelData[i]=dProcessedTravelData[i-1]+dTravelTimeData[i];
			

		}
		
		export_2d_array(dProcessedTravelData,dLatenessData,"lateness_trend"+Integer.toString(nEventID),tspParent.charger.AlgoName);
	}
	
	
	
	private void export_duetime_cluster(){

		double[] processedtimedata=new double[1000];
		double[] processedduedata=new double[1000];
		
		for(int i=0;i<processedtimedata.length;i++){
			processedtimedata[i]=dTravelTimeData[i+2000];
			processedduedata[i]=tspParent.charger.dDueTimeData[i+2000];
		}
		
		
		
		export_2d_array(processedtimedata,processedduedata,"duetime_cluster"+Integer.toString(nEventID),tspParent.charger.AlgoName);
	}
	
	
	
	private void export_duetime_data(){
		
		
		
		
		Mergesort mergesort=new Mergesort();
		mergesort.sort(tspParent.charger.dDueTimeData);
		
		double[] dPercentage=new double[nDuetimeDataIndex];
		for(int i=0;i<nDuetimeDataIndex;i++){
			dPercentage[i]=((double) i)/nDuetimeDataIndex;
		}
		
		double[] temp=mergesort.numbers;
		
		export_2d_array(temp,dPercentage,
				"duetime"+Integer.toString(nEventID),tspParent.charger.AlgoName);
		
	}
	
	
	private void export_2d_array(double[] input1,double[] input2,String fileName,String folderName){
		File file;
		StringBuffer output=new StringBuffer();
		for(int i=0;i<input1.length;i++){

			output.append(Double.toString(input1[i])+" "+Double.toString(input2[i])+"\n");
		}
		
		
		if(fileName==null){						
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
			StringBuffer sName=new StringBuffer("/home/harry/Desktop/ChargingExp/"+folderName+"/");	
			
			File folder=new File(sName.toString());
			if(!folder.exists()){
				folder.mkdir();
			}
			
//			sName.append(tspParent.charger.AlgoName);
			sName.append(fileName);		
			sName.append(".txt");
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
	
	
	
	
	private void export_array(double[] input,String fileName,String folderName){
		File file;
		StringBuffer output=new StringBuffer();
		for(int i=0;i<input.length;i++){
			output.append(Double.toString(input[i])+"\n");
		}
		
		
		if(fileName==null){						
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
			StringBuffer sName=new StringBuffer("/home/harry/Desktop/ChargingExp/"+folderName+"/");	
			
			File folder=new File(sName.toString());
			if(!folder.exists()){
				folder.mkdir();
			}
			
//			sName.append(tspParent.charger.AlgoName);
			sName.append(fileName);			
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
		sName.append("OneTime_");

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
	
	
	
	private double find_weighted_average(double[] numbers,double[] weight){
		
		if(numbers.length<1) return 0.0;
		
		double sum=0.0;
		double length=0;
		for(int i=0;i<numbers.length-1;i++){
			if(numbers[i]>0){
				sum+=numbers[i+1]*weight[i];
				length+=weight[i];
			}
			
		}
		sum/=length;
		return sum;
		
	}
	private double find_average(double[] input){
		if(input.length<1) return 0.0;
		
		double sum=0.0;
		double length=0;
		for(int i=0;i<input.length;i++){
			if(input[i]>0){
				sum+=input[i];
				length++;
			}
			
		}
		sum/=length;
		return sum;
	}
	
	private double find_std(double[] input,double[] weight,double avg){
		if(input.length<1) return 0.0;
		
		
		
		double sum=0.0;
		double count=0.0;
		
		for(int i=0;i<input.length-1;i++){
			sum+=weight[i+1]*Math.pow((input[i]-avg), 2);
			count+=weight[i+1];
		}
		sum/=(count);
		sum=Math.sqrt(sum);
		return sum;
	}
	
	void compute_group_coverage(int ni,int nj){
		for(int i=0;i<tspParent.cities.length;i++){
			if(tspParent.cities[i].energy>1){
				tspParent.charger.dGroupCoverage[ni][tspParent.cities[i].nGroupID][nj]++;						
			}
		}
		
		for(int i=0;i<tspParent.nodeGrouper.groupNodeNum.length;i++){
			tspParent.charger.dGroupCoverage[ni][i][nj]/=tspParent.nodeGrouper.groupNodeNum[i];
		}
			
	}
		



}
