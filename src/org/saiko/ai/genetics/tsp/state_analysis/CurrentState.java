package org.saiko.ai.genetics.tsp.state_analysis;

import org.saiko.ai.genetics.tsp.City;
import org.saiko.ai.genetics.tsp.TSP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * CurrentState class is application specific, meaning that it depends on the requirement of each individual application
 */
public class CurrentState {
    double[] lateness;
    double[] energy_level;
    double[] due_time;
    double[] e_consmp_rate;
    public double coverage_ratio=0;
    int num_deadnode=0;
    double MAXENERGY;

    double charger_speed=0;
    double charging_rate=0;


    List<Double> coverage_record;


    public CurrentState(City[] all_nodes, int max_energy, TSP parentTSP){
        lateness=new double[all_nodes.length];
        energy_level=new double[all_nodes.length];
        e_consmp_rate=new double[all_nodes.length];
        due_time=new double[all_nodes.length];
        ini_energy_level(all_nodes);
        MAXENERGY=max_energy;
        coverage_record=new LinkedList<Double>();

        charger_speed=parentTSP.configuration.CHARGER_SPEED;
        charging_rate=parentTSP.configuration.dCHARGINGRATE;

    }

    void ini_energy_level(City[] all_nodes){
        for(int i=0;i<all_nodes.length;i++){
            energy_level[i]=all_nodes[i].energy;
            e_consmp_rate[i]=all_nodes[i].consumptionRate;
        }
    }


    public void update_network(double[] travel_times, int[] node_recharge){


        for(int i=0;i<travel_times.length;i++){
            for(int j=0;j<energy_level.length;j++){

                energy_level[j]-=travel_times[i]*e_consmp_rate[j];
                if(energy_level[j]<0){
                    energy_level[j]=0;
                }
            }
        }
        recharge_nodes(node_recharge);
        num_deadnode=count_deadnode();
        coverage_ratio=1-num_deadnode/((double)energy_level.length);
        coverage_record.add(coverage_ratio);
    }

    private void recharge_nodes(int[] nodeID){

        for(int i=0;i<nodeID.length;i++){
            energy_level[nodeID[i]]=MAXENERGY;
        }


    }

    private int count_deadnode(){
        int output =0;
        for(int i=0;i<energy_level.length;i++){
            if(energy_level[i]==0){
                output++;
            }
        }
        return output;
    }


    public void output_data(String root_path){

       write_double_stream(root_path+"cov_trend.csv",coverage_record);
        StringBuilder stringBuilder=new StringBuilder();

        stringBuilder.append(Double.toString(charger_speed)+" ");
        stringBuilder.append(Double.toString(charging_rate)+" ");
       Double avg= calculateAverage(coverage_record);
        stringBuilder.append(Double.toString(avg)+" ");
        Double std=calculateSTD(coverage_record);
        stringBuilder.append(Double.toString(std)+"\n");

        write_string(root_path+"statistics.csv",stringBuilder.toString());

    }


    private double calculateAverage(List <Double> marks) {
        Double sum = 0.0;
        if(!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    private double calculateSTD(List <Double> marks) {
        Double avg=calculateAverage(marks);
        Double sum = 0.0;
        if(!marks.isEmpty()) {
            for (Double mark : marks) {
                sum =sum+ (mark-avg)*(mark-avg);
            }
            sum=sum/(marks.size()-1);
            sum=Math.sqrt(sum);
            return sum;
        }
        return sum;
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

    private void write_double_stream(String path, List<Double> input){


        File file = new File(path);
        if(file.exists()){
            file.delete();
        }

        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            BufferedWriter statOutput = new BufferedWriter(new FileWriter(file));


            input.forEach((data_point)->{
                try {
                    statOutput.write(Double.toString(data_point)+"\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            //	statOutput.flush();
            statOutput.close();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


    }


}
