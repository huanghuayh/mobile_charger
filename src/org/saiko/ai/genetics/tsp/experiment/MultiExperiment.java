package org.saiko.ai.genetics.tsp.experiment;

import algorithms.Mergesort;
import org.saiko.ai.genetics.tsp.*;
import org.saiko.ai.genetics.tsp.state_analysis.CurrentState;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.time.Duration;
import java.time.Instant;

public class MultiExperiment implements ActionListener {
    TSP tspParent;
    EnergyConsumer energyConsumer;
    CurrentState networkState;





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

    boolean IS_AUTOMATIC=true;

    public MultiExperiment(TSP parent){
        tspParent=parent;

    }
    @Override
    public void actionPerformed(ActionEvent arg0) {
        nRunningTimes=5;

        ArrayList<Double> speed_lst=new ArrayList<Double>();
        for (int i=0;i<1;i++){
            speed_lst.add(400.0+50.0*i);
        }
        ArrayList<Integer> consumption_lst=new ArrayList<Integer>();
        for (int i=0;i<1;i++){
            consumption_lst.add(30+10*i);
        }

        iterate_algorithms(speed_lst,consumption_lst,false,new int[]{20},"./cal_time_100");
//        iterate_algorithms(speed_lst,consumption_lst,false,new int[]{20},"./even");
//        iterate_algorithms(speed_lst,consumption_lst,true,new int[]{6},"./avg_cvg_group");
//        iterate_algorithms(speed_lst,consumption_lst,true,new int[]{6,7},"./avg_cvg_group2");
//        iterate_algorithms(speed_lst,consumption_lst,true,new int[]{6,7,8},"./avg_cvg_group3");
//        iterate_algorithms(speed_lst,consumption_lst,true,new int[]{6,7,1,2},"./avg_cvg_group4");



        tspParent.gui.createCityMap(false);
    }


    private void iterate_algorithms(List<Double> speed_lst,List<Integer> consumption_lst,boolean should_group,int[] group_lst,String filename){





        File file = new File(filename);
        if (file.exists()){
            file.delete();
        }


        for(TSPConfiguration.AlgorithmName n: TSPConfiguration.AlgorithmName.values()){
            for (double spd:speed_lst){
                for(int cspt:consumption_lst){
                    tspParent.charger=null;
                    tspParent.charger=new Charger(tspParent,tspParent.nodeGrouper,n.name);
                    reset_network(spd,cspt);
                    if (should_group){
                        setGroupRate(tspParent,group_lst);// set different energy consumption rates based on location
                    }


                    //get system time
                    Instant start=Instant.now();
                    List<ArrayList<Float>> result=simulate_run(nRunningTimes);
                    Instant end=Instant.now();
                    Duration duration=Duration.between(start,end);


                    // compare system time elapsed
                    //record the time spent
                    float mean_cvg=find_mean(result.get(0));
                    float mean_due=find_mean(result.get(1));
                    System.out.println(n+" speed "+spd+" rate "+cspt+" coverage "+mean_cvg+" due "+mean_due+" num_node "+tspParent.cities.length+" duration "+duration.toMillis());
                    write_string(filename+"_cvg",n+" speed "+spd+" rate "+cspt+" coverage "+mean_cvg+" due "+mean_due+
                            " num_node "+tspParent.cities.length+" duration "+duration.toMillis()+"\n");
                }
            }

        }
    }


    private void reset_network(double speed, int default_consumption_rate){
        tspParent.charger.dVelocity=speed;//To change the velocity of MC

        for(City city:tspParent.cities){
            city.consumptionRate=default_consumption_rate/3;
//            city.energy=tspParent.configuration.nEnergyInit;
            city.energy=0;
            city.dWaitTime=tspParent.configuration.nWaitTime;
        }

        energyConsumer=null;
        energyConsumer=new EnergyConsumer(tspParent,false);





        tspParent.charger.nTotalLife=0;
    }

//    public void setGroupRate(TSP parent){
//        parent.ConsumptionPattern="group";
//        for(int i=0;i<parent.cities.length;i++){
//
//            //  if(0==parent.cities[i].nGroupID||8==parent.cities[i].nGroupID||4==parent.cities[i].nGroupID){
//            if(6==parent.cities[i].nGroupID){
//                //  if(2==parent.cities[i].nGroupID){
//
////                parent.cities[i].consumptionRate=(int) (parent.configuration.dDEFAULTCONSUMPTIONRATE*2);
//                parent.cities[i].consumptionRate*=2;
//            }
////		   else if(2==parent.cities[i].nGroupID||6==parent.cities[i].nGroupID){
////			   parent.cities[i].consumptionRate=(int) (parent.configuration.dDEFAULTCONSUMPTIONRATE*1.5);
////		   }
////		   else if(1==parent.cities[i].nGroupID||7==parent.cities[i].nGroupID||3==parent.cities[i].nGroupID||5==parent.cities[i].nGroupID){
////			   parent.cities[i].consumptionRate=(int) (parent.configuration.dDEFAULTCONSUMPTIONRATE/2);
////		   }
//            else{
////                parent.cities[i].consumptionRate= (int) (parent.configuration.dDEFAULTCONSUMPTIONRATE/3);
//                parent.cities[i].consumptionRate/=3;
//            }
//
//        }
//
//
//    }


    public void setGroupRate(TSP parent,int[] group_lst){
        parent.ConsumptionPattern="group";

        int m=group_lst.length;
        int k=5;
        int N=9;
        for(int i=0;i<parent.cities.length;i++){
            boolean found=false;
            double res=N*parent.cities[i].consumptionRate*k/(k*m+9.0-m);
            for (int heavy_group:group_lst){


                if(heavy_group==parent.cities[i].nGroupID){
//                    parent.cities[i].consumptionRate*=5;
                    parent.cities[i].consumptionRate=(int)res;

                    found=true;
                }
            }
            if(!found){
                parent.cities[i].consumptionRate=(int)(res/k);
            }
        }
    }



    private float find_mean(ArrayList<Float> lst){
        float avg=0;
        int cnt=0;
        for (float n:lst){
            avg+=n;
            cnt++;
        }
        return (avg/cnt);
    }

    private void write_string(String path, String input){

        File file = new File(path);

        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            BufferedWriter statOutput = new BufferedWriter(new FileWriter(file,true));
//            statOutput.write(input+"\n");
            statOutput.append(input);
            statOutput.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


    }


    private List<ArrayList<Float>> simulate_run(int num_iteration){

        ArrayList<Float> cvg = new ArrayList<Float>();
        ArrayList<Float> duetime_lst = new ArrayList<Float>();
        for(int j=0;j<num_iteration;j++){

            tspParent.charger.travel();


            for(int travel_idx=0;travel_idx<tspParent.charger.dTravelTimeSeries.length;travel_idx++){
                double all_due=0.0;
                tspParent.charger.nNumDeadNodes=0;
                for(City city: tspParent.cities){
                    city.dTemporalConsumptionRate=((double)city.consumptionRate);
                    city.energy=city.energy-(int)(city.dTemporalConsumptionRate*tspParent.charger.dTravelTimeSeries[travel_idx]);
                    if(city.energy<=0){
                        city.energy=0;
                        city.dDieTime+=tspParent.charger.dTravelTimeSeries[travel_idx];
                        tspParent.charger.nNumDeadNodes++;
                        city.dWaitTime+=tspParent.charger.dTravelTimeSeries[travel_idx];
                        all_due+=tspParent.charger.dTravelTimeSeries[travel_idx];
                    }
                }

                if(tspParent.cities[tspParent.charger.nTargetIDs[travel_idx]].energy==0){
                    tspParent.charger.nNumDeadNodes--;
                }

                tspParent.cities[tspParent.charger.nTargetIDs[travel_idx]].energy=tspParent.configuration.nMaxEnergy;
                tspParent.cities[tspParent.charger.nTargetIDs[travel_idx]].dDieTime=0;
                tspParent.cities[tspParent.charger.nTargetIDs[travel_idx]].dWaitTime=0;
                tspParent.charger.nTotalLife++;
                tspParent.charger.dTotalTime+=tspParent.charger.dTravelTimeSeries[travel_idx];
                tspParent.charger.dCoverage=1-(float)tspParent.charger.nNumDeadNodes/tspParent.cities.length;

                double avg_duetime=all_due/((double) tspParent.cities.length);

                duetime_lst.add((float)avg_duetime);
                cvg.add((float)tspParent.charger.dCoverage);

                for(int k=0;k<tspParent.cities.length;k++){
                    tspParent.charger.dEnergy+=tspParent.cities[k].energy;
                }

            }


        }

        List<ArrayList<Float>> result=new ArrayList<ArrayList<Float>>();
        result.add(cvg);
        result.add(duetime_lst);



        return result;
    }


    private double get_consumption_rate(){

        double temp=Math.random()*tspParent.configuration.RANDOMENERGYCONSUMPTION.length;
        return tspParent.configuration.RANDOMENERGYCONSUMPTION[(int)temp];
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