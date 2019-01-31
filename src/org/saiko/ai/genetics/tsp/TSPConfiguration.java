/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/TSPConfiguration.java,v $
 * $Id: TSPConfiguration.java,v 1.3 2005/08/23 23:18:05 dsaiko Exp $
 * $Date: 2005/08/23 23:18:05 $
 * $Revision: 1.3 $
 * $Author: dsaiko $
 *
 * Traveling Salesman Problem genetic algorithm.
 * This source is released under GNU public licence agreement.
 * dusan@saiko.cz
 * http://www.saiko.cz/ai/tsp/
 * 
 * Change log:
 * $Log: TSPConfiguration.java,v $
 * Revision 1.3  2005/08/23 23:18:05  dsaiko
 * Finished.
 *
 * Revision 1.2  2005/08/22 22:08:51  dsaiko
 * Created engines with heuristics
 *
 * Revision 1.1  2005/08/12 23:52:17  dsaiko
 * Initial revision created
 *
 */

package org.saiko.ai.genetics.tsp;


/**
 * @author Dusan Saiko (dusan@saiko.cz)
 * Last change $Date: 2005/08/23 23:18:05 $
 * 
 * TSPConfiguration specifies the configuration parameters of the application
 */
public class TSPConfiguration {

   /** String containing the CVS revision. **/
   public final static String CVS_REVISION = "$Revision: 1.3 $";


   
   /**
    * Antialiasing flag for graphics rendering.
    * Slows the displaying quite a lot.
    */
   protected boolean antialiasing=false;

   /**
    * Initial population count which is set to the computation engine.
    */
   protected int initialPopulationSize=100;
   
   /**
    * Computation thread priority
    * @see Thread#setPriority(int)
    */
   protected int threadPriority=5;
   
   /**
    * Population growth between two generations.
    * This flag does not apply for all the engines.
    */
   protected double populationGrow=0.00075;
   
   /**
    * Ratio (0..1), how much the population should undergo random mutation 
    */
   protected double mutationRatio=0.5;
   
   /**
    * The count of generation which give the same best result after which the program should stop
    * computations; 
    */
   protected int maxBestCostAge=100;
   
   /**
    * flag that this computation should be done without GUI
    * @see TSP#main(String[]) 
    */
   protected boolean console=false;
   
   /**
    * Should the cost be computed like square root of distance, instead of 
    * only distance itself ? This should prefer the solutions with short distances inside of it
    * e.g. distances 5 6 3 6 2 give total distance 22 
    *      distances 2 2 1 3 13 give total distance 21
    * but in rms the distance at first examle is 5*5+6*6+3*3+6*6+2*2 = 110
    * and second example rms distance is 187.
    */
   protected boolean rmsCost=false;
   
   
   
   
   // The initial energy of each node
   ///////////////////////////////////////////////////////////////
   //THE relation between these quantities and real life standard units
   // energy consumption rate 0.12 w is equal to 20
   // battery capacity 10000J is equal to 1000
   // traveling speed 0.35 m/s is equal to 1200
   //time 120000/49 seconds is equal to 1
   // distance 1000 meter is equal to 1400
   ///////////////////////////////////////////////

   
   public int nMaxEnergy=1000;
   
   public int nEnergyInit=nMaxEnergy;
   
   public double nConsumptionRange=50;

   public double CHARGER_SPEED=1201.0;

   public double dDEFAULTCONSUMPTIONRATE=60.0;

   public double dCHARGINGRATE=2500.0;
   
   public int nWaitTime=210;
   
   
   //////////////Set the parameters for node priority//////////////////
   public double PRIORITYCONTROLP=1;
   public double PRIORITYCONTROLI=0.5;
   public double PRIORITYCONTROLD=0.5;
   
   public double FILTERPARAMETER=0.8;
   ////////////////Set parameters for dynamic energy consumption rate//////////////////
   
   public double[] RANDOMENERGYCONSUMPTION={0.906,1.154,1.215,0.761,1.149,1.174,0.888,0.702,1.136,0.953,0.875,1.083};
 //  public double[] RANDOMENERGYCONSUMPTION={0.906,1.154,1.215,0.761,0.888,0.702,0.953};
   
   ////////////////The names for schedulers////////////////////////////////////////
 //  public String DEFAULTSCHEDULER="FEEDBACKHORIZON";

   //public String DEFAULTSCHEDULER="FEEDBACKPRIORITY";
//  public String DEFAULTSCHEDULER="MRF";
   public String DEFAULTSCHEDULER="MWF-I";
  // public String DEFAULTSCHEDULER="FEEDBACKEDF";
 //  public String DEFAULTSCHEDULER="SDT";
   
   
	public enum AlgorithmName{
//		MRF("MRF"),
	//	MRFSD("MRFSD"),
	//	MRFG("MRFG"),
	//	MRFI("MRFI"),
		
	//	Route("ROUTE"),
	//	Feedback("FEEDBACK"),
		FeedbackHorizon("FEEDBACKHORIZON"),
	//	FeedbackPriority("FEEDBACKPRIORITY"),
		TSP("TSP"),
	//	FeedbackEDF("FEEDBACKEDF"),
		
		
//		MWF0("MWF0"),
		MWF("MWF"),
//		MWFI("MWF-I"),
//		MWF05("MWF05"),
	//	MWF08("MWF08"),
	//	MWF1("MWF1");
		SDT("SDT"),
//       SDTCHARGING("SDT_CHARGING")
		;

		
		public String name;
		AlgorithmName(String n){
			this.name=n;
		}
	}
   
   
   
   
   public double DEFAULTHORIZON=451;
   public double MINHORIZON=250;
   public double DEFAULTCLUSTER=151;
   
   
   
   public double DEFAULTPATHANGLE=Math.PI/3.9;
   
   
   /**
    * @return antialiasing flag for graphics rendering. Slows the displaying quite a lot.
    */
   public boolean isAntialiasing() {
      return antialiasing;
   }
   
   /**
    * @param antialiasing Antialiasing flag for graphics rendering. Slows the displaying quite a lot.
    */
   public void setAntialiasing(boolean antialiasing) {
      this.antialiasing=antialiasing;
   }
   
   /**
    * @return initial population count which is set to the computation engine.
    */
   public int getInitialPopulationSize() {
      return initialPopulationSize;
   }
   
   /**
    * @param initialPopulationSize Initial population count which is set to the computation engine.
    */
   public void setInitialPopulationSize(int initialPopulationSize) {
      this.initialPopulationSize=initialPopulationSize;
   }
   
   /**
    * @return Returns the maxBestCostAge.
    */
   public int getMaxBestCostAge() {
      return maxBestCostAge;
   }
   
   /**
    * @param maxBestCostAge The maxBestCostAge to set.
    */
   public void setMaxBestCostAge(int maxBestCostAge) {
      this.maxBestCostAge=maxBestCostAge;
   }
   
   /**
    * @return ratio (0..1), how much the population should undergo random mutation 
    */
   public double getMutationRatio() {
      return mutationRatio;
   }
   
   /**
    * @param mutationRatio Ratio (0..1), how much the population should undergo random mutation 
    */
   public void setMutationRatio(double mutationRatio) {
      this.mutationRatio=mutationRatio;
   }
   
   /**
    * @return Population growth between two generations.
    * This flag does not apply for all the engines.
    */
   public double getPopulationGrow() {
      return populationGrow;
   }
   
   /**
    * @param populationGrow Population growth between two generations.
    * This flag does not apply for all the engines.
    */
   public void setPopulationGrow(double populationGrow) {
      this.populationGrow=populationGrow;
   }
   
   /**
    * @return flag, id the cost should be computed like square root of distance, instead of 
    * only distance itself ? This should prefer the solutions with short distances inside of it
    * e.g. distances 5 6 3 6 2 give total distance 22 
    *      distances 2 2 1 3 13 give total distance 21
    * but in rms the distance at first examle is 5*5+6*6+3*3+6*6+2*2 = 110
    * and second example rms distance is 187.
    */
   public boolean isRmsCost() {
      return rmsCost;
   }
   
   /**
    * @param rmsCost Flag, id the cost should be computed like square root of distance, instead of 
    * only distance itself ? This should prefer the solutions with short distances inside of it
    * e.g. distances 5 6 3 6 2 give total distance 22 
    *      distances 2 2 1 3 13 give total distance 21
    * but in rms the distance at first examle is 5*5+6*6+3*3+6*6+2*2 = 110
    * and second example rms distance is 187.
    */
   public void setRmsCost(boolean rmsCost) {
      this.rmsCost=rmsCost;
   }
   
   /**
    * @return computation thread priority
    * @see Thread#setPriority(int)

    */
   public int getThreadPriority() {
      return threadPriority;
   }
   
   /**
    * @param threadPriority Computation thread priority
    * @see Thread#setPriority(int)
    */
   public void setThreadPriority(int threadPriority) {
      this.threadPriority=threadPriority;
   }

   
}