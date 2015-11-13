
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

public class AutomaticRunner implements ActionListener {
	TSP tspParent;
	EnergyConsumer energyConsumer;
	int nExpTimes;
	int nRunningTimes;
	String CoverageOutput;
	
	
	public AutomaticRunner(TSP parent){
		tspParent=parent;
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String expNum=JOptionPane.showInputDialog(this,"50");
		String input =  JOptionPane.showInputDialog(this,"10000");

		nExpTimes= Integer.parseInt(expNum);
		nRunningTimes=Integer.parseInt(input);
		
		
		
		
		
		for(AlgorithmName n: AlgorithmName.values()){
			
			if(tspParent.charger!=null)
				tspParent.charger=null;
			tspParent.charger=new Charger(tspParent,tspParent.nodeGrouper,n.name);
			
			tspParent.charger.dCoverageData=new double[nExpTimes][nRunningTimes];
//			tspParent.charger.dDueTimeData=new double[nExpTimes][nRunningTimes];
			tspParent.charger.dTravelTimeData=new double[nRunningTimes];
			
			tspParent.charger.dGroupCoverage=new double[nExpTimes][tspParent.charger.nNumGroup][nRunningTimes];

			
			
			
			
			for(int i=0;i<nExpTimes;i++){
				energyConsumer=new EnergyConsumer(tspParent,false);
		//		tspParent.charger.dCoverageData[i]=new double[nRunningTimes];////////Doing the memory assignment for the record array
		//		tspParent.charger.dDueTimeData[i]=new double[nRunningTimes];
				
				
				
				for(int j=0;j<nRunningTimes;j++){
					energyConsumer.actionPerformed(arg0);
					
					tspParent.charger.dCoverageData[i][j]=tspParent.charger.dCoverage;
	//				tspParent.charger.dDueTimeData[i][j]=energyConsumer.dDueTime;
					
					if(i<1){
						tspParent.charger.dTravelTimeData[j]=100;
					}
					
//					compute_group_coverage(i,j);
					if(tspParent.charger.nTotalLife>nRunningTimes) break;
					
					if(null!=tspParent.charger.nInsertIndex){
						for(int k=0;k<tspParent.charger.nInsertIndex.length;k++){
							j++;
							tspParent.charger.dCoverageData[i][j]=tspParent.charger.dCoverage;
//							tspParent.charger.dDueTimeData[i][j]=energyConsumer.dDueTime;
							if(i<1){
								tspParent.charger.dTravelTimeData[j]=100;
							}
							
						}
					}
					
				}
				
				refresh_node();
				energyConsumer=null;
				//tspParent.charger.dVelocity+=18;
			}
			
			compute_statistics(n.name);
			
		}

		tspParent.gui.createCityMap(false);
		export_string(CoverageOutput);
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
		double[] dAvgLst=new double[tspParent.charger.dDueTimeData.length];
		double[] dStdLst=new double[tspParent.charger.dDueTimeData.length];
		double[][] dGroupCoverage=new double[tspParent.charger.dDueTimeData.length][tspParent.nodeGrouper.groupNodeNum.length];
		
		double dAvgOut;
		double dStdOfAvgOut;
		double dAvgOfStdOut;
		
		double[] dGroupCoverage2=new double[tspParent.charger.dDueTimeData.length];
		double dGroupSTDout;
		
		for(int i=0;i<dAvgLst.length;i++){
			dAvgLst[i]=find_average(tspParent.charger.dCoverageData[i]);
			dStdLst[i]=find_std(tspParent.charger.dCoverageData[i],dAvgLst[i]);
			
			for(int j=0;j<tspParent.nodeGrouper.groupNodeNum.length;j++){
				dGroupCoverage[i][j]=find_average(tspParent.charger.dGroupCoverage[i][j]);						
			}
			
			dGroupCoverage2[i]=find_std(dGroupCoverage[i],find_average(dGroupCoverage[i]));
			
		}
		
		
		dGroupSTDout=find_average(dGroupCoverage2);
		
		dAvgOut=find_average(dAvgLst);
		dStdOfAvgOut=find_std(dAvgLst,dAvgOut);
		dAvgOfStdOut=find_average(dStdLst);
		
		//dGroupSTDout=find_std()
		
		String output="A "+Double.toString(dAvgOut)+
				//" SoA "+Double.toString(dStdOfAvgOut)+
				" AoS "+ Double.toString(dAvgOfStdOut)
				+"Grp "+ Double.toString(dGroupSTDout)
				+" distance "+Double.toString(tspParent.charger.dTotalTime);
		
		CoverageOutput+=Double.toString(dAvgOfStdOut)+" "+Double.toString(dAvgOut)+" "+
				Double.toString(dAvgOfStdOut)+" "+tspParent.charger.AlgoName+"\n";
		
		//export_string(output);
		//export_array(tspParent.charger.dCoverageData[0],"OneEXP");
		//export_array(dAvgLst,"asdf");
		//export_array(tspParent.charger.nGroupVisitTimes,"GroupDistribution");
		//export_array(tspParent.charger.dDueTimeData[0],"DueTime",tspParent.charger.AlgoName);
		//export_array(dGroupCoverage,"GroupCoverage");
		//export_array(tspParent.charger.dCoverageData[0],"coverage",tspParent.charger.AlgoName);
		//export_array(tspParent.charger.dTravelTimeData,"time",tspParent.charger.AlgoName);
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
		
/*	public class AlgorithmName{
		public String MRF="MRF";
		public String MRFSD="MRFSD";
		public String MRFG="MRFG";
		public String MRFI="MRFI";
		
		public String MWF0="MWF0";
		public String MWF02="MWF02";
		public String MWF05="MWF05";
		public String MWF08="MWF08";
		public String MWF1="MWF1";
	
		public String[]=new S
	}*/
	public enum AlgorithmName{
		MRF("MRF"),
	//	MRFSD("MRFSD"),
	//	MRFG("MRFG"),
	//	MRFI("MRFI"),
		
	//	Route("ROUTE"),
		Feedback("FEEDBACK"),
		TSP("TSP"),
		
		
	//	MWF0("MWF0"),
	//	MWF02("MWF02"),
	//	MWF05("MWF05"),
	//	MWF08("MWF08"),
	//	MWF1("MWF1");
		;

		
		public String name;
		AlgorithmName(String n){
			this.name=n;
		}
	}

}
