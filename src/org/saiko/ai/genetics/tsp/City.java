/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/City.java,v $
 * $Id: City.java,v 1.3 2005/08/23 23:18:04 dsaiko Exp $
 * $Date: 2005/08/23 23:18:04 $
 * $Revision: 1.3 $
 * $Author: dsaiko $
 *
 * Traveling Salesman Problem genetic algorithm.
 * This source is released under GNU public licence agreement.
 * dusan@saiko.cz
 * http://www.saiko.cz/ai/tsp/
 * 
 * Change log:
 * $Log: City.java,v $
 * Revision 1.3  2005/08/23 23:18:04  dsaiko
 * Finished.
 *
 * Revision 1.2  2005/08/13 12:53:02  dsaiko
 * XML2PDF report finished
 *
 * Revision 1.1  2005/08/12 23:52:17  dsaiko
 * Initial revision created
 *
 */

package org.saiko.ai.genetics.tsp;

import org.saiko.ai.genetics.tsp.feedbackscheduler.PIDController;;

/**
 * @author Dusan Saiko (dusan@saiko.cz)
 * Last change $Date: 2005/08/23 23:18:04 $
 *
 * The city definition for traveling salesman problem.
 * City has basic properties as x and y coordinates and name
 * and some functionality to get the distance to other cities 
 */
public class City {

   /** String containing the CVS revision. **/
   public final static String CVS_REVISION = "$Revision: 1.3 $";
   
   /**
    * serialVersionUID
    */
   protected static final long serialVersionUID =-554746071631292503L;

   /**
    * X coordinate of the city. 
    * It could be S-JTSK coordinate [m].
    */
   protected int    		x;

   /**
    * Y coordinate of the city. 
    * It could be S-JTSK coordinate [m].
    */
   protected int    		y;
   
   
   /**
    * X coordinate of the city - original value in sjtsk coordinates. 
    * It could be S-JTSK coordinate [m].
    */
   final protected int            SJTSKX;

   /**
    * Y coordinate of the city - original value in sjtsk coordinates. 
    * It could be S-JTSK coordinate [m].
    */
   final protected int            SJTSKY;
   
   /**
    * city name
    */
   protected String 		name;
   
   
   /**
    * numeric id of the city - index of city in the original arrays of cities
    * main characteristic is, that id is less then the length of the city array 
    */
   protected int          id;

   /**
    * start city flag 0 city from which the salesman starts
    */
   protected boolean startCity=false;

   /**
    * cache for distances to other cities
    * the cities are indexed beginning from 0 and this index is written into id property
    * this id is then used as index into distanceCache array
    * this chache holds distances from [id1] city to [id2] city
    */
   public static double distanceCache[][]=null;

   /**
    * configuration parameters of application
    * @see TSPConfiguration
    */
   protected TSPConfiguration configuration;
   
   /**
    * Constructor for the city object
    * @param id int id of city (its index)
    * @param configuration configuration parameters of application
    * @param name - name of the city
    * @param x - X coordinate of the city [S-JTSK - [m]]
    * @param y - Y coordinate of the city [S-JTSK - [m]]
    * @see TSPConfiguration
    */
   
   
   ////////////////////////Add two parameters to characterize the energy consumption///////////
   public int energy;  
   public int preenergy;
   private double dFilterParameter;
   public int consumptionRate;
   public double dTemporalConsumptionRate=0;
   
   ///////////////////////The number of charging of the battery///////////////////
   int nLife=0;
   
   /////////////////////////The group ID
   public int nGroupID=0;
   
   //////////////////////////The dying time/////////////////
   public double dDieTime=0;
   
   public double dWaitTime;
   
   
   
   ////////////////////The implemeation of PID priority controller////////////////////
   
   public double dPIDPriority;
   PIDController pidPriority;

   
   public City(int id, TSPConfiguration configuration, String name, int x, int y) {
      this.id=id;
      this.x=x;
      this.y=y;
      this.SJTSKX=x;
      this.SJTSKY=y;
      this.name=name;
      this.configuration=configuration;
      this.consumptionRate=(int) configuration.dDEFAULTCONSUMPTIONRATE;//initialize the consumption rate
      this.energy=configuration.nEnergyInit;
      this.preenergy=energy;
      this.dWaitTime=configuration.nWaitTime;
      
      
      pidPriority=new PIDController(1,0.01,0.02);
      pidPriority.setSetpoint(configuration.nMaxEnergy/2);
      pidPriority.setInputRange(0, configuration.nMaxEnergy);
      pidPriority.setOutputRange(-10000, 10000);
      pidPriority.enable();
      dFilterParameter=configuration.FILTERPARAMETER;
    //  pidPriority.setPID(configuration.PRIORITYCONTROLP,configuration.PRIORITYCONTROLI, configuration.PRIORITYCONTROLD);
   }

   public double compute_priority(int nEnergyInput){
	   double dFilteredInput=dFilterParameter*nEnergyInput+(1-dFilterParameter)*preenergy;
	   
	   pidPriority.getInput(dFilteredInput);
	   
	   preenergy=nEnergyInput;
	   
	   return pidPriority.performPID();
	   
	   
   }
   
//   public void setPID(double dPDerivative){
//	   pidPriority.setPID(configuration.PRIORITYCONTROLP,configuration.PRIORITYCONTROLI, dPDerivative);
//   }
   
   
   
   
   

   /**
    * initializes the distance cache for know number of cities
    * @param length - length of the cache = number of cities
    */
   static synchronized public void initDistanceCache(int length) {
      distanceCache=new double[length][length];
      //reset the cache to -1
      for(int i=0; i<length; i++) {
         for(int j=0; j<length; j++) {
            distanceCache[i][j]=-1;
         }
      }
   }
   
   /**
    * Computes distance over two cities.
    * If coorfinates are in S-JTSK, then this distance is in meters.
    * Uses the cache to hold the distances between two cities 
    * without having to compute them every time
    * @param otherCity
    * @param useCache - true if the cache should be used 
    * @return distance between the two cities. 
    */
   public double distance(City otherCity, boolean useCache) {
	   
	  if(useCache==false) {
		return distance(otherCity.getX(), otherCity.getY());   
	  }
	  
      int id1=this.id;
      int id2=otherCity.id;
      if(id1==id2) return 0.0;
      
      if(id1>id2) {
         int swap=id1;
         id1=id2;
         id1=swap;
      }
      
      //distance is cached in the 2 dimensional array
      //we order the indexes of cities, so B->A is computed as A->B - it
      //saves us half of combinations
      double distance=distanceCache[id1][id2];
      if(distance==-1) {
         //no distance found in cache, compute it
         distance=distance(otherCity.getX(), otherCity.getY());
         distanceCache[id1][id2]=distance;
         
         ///////////////////To facilitate computation, divide the distance by 100;
         distanceCache[id1][id2]/=100;
      }
      return distance;
   }
   
   /**
    * Computes distance over two cities.
    * If coorfinates are in S-JTSK, then this distance is in meters.
    * Uses the cache to hold the distances between two cities 
    * without having to compute them every time
    * @param otherCity
    * @return distance between the two cities. 
    */
   public double distance(City otherCity) {
      return distance(otherCity,true);
   }
   
   /**
    * @param otherCity
    * @return cost for traveling to otherCity from this. It may differ from distance.
    */
   public double cost(City otherCity) {
      double distance=distance(otherCity);
      if(configuration.isRmsCost()) {
         return distance*distance;
      }
      return distance;
   }
   
   /**
    * Computes distance from point
    * If coorfinates are in S-JTSK, then this distance is in meters.
    * This city has to have the same coordinate system like a given point.
    * @param pX
    * @param pY
    * @return distance between this city and some point in the world. 
    */
   protected double distance(int pX, int pY) {
      double dx=this.x - pX;
      double dy=this.y - pY;
      double distance=Math.sqrt(dx * dx + dy * dy);
      return distance;
   }
   
   /**
    * @return Returns the name of the city
    */
   public String getName() {
      return name;
   }
   
   /**
    * @return Returns the x coordinate of city
    */
   public int getX() {
      return x;
   }
   
   /**
    * @return Returns the y coordinate of the city
    */
   public int getY() {
      return y;
   }
   
   
   
   
   /**
    * @return Returns the x coordinate of city - original value in sjtsk coordinates
    */
   public int getSJTSKX() {
      return SJTSKX;
   }
   
   /**
    * @return Returns the y coordinate of the city - original value in sjtsk coordinates
    */
   public int getSJTSKY() {
      return SJTSKY;
   }
   
   /**
    * @return Name of city with coordinates
    */
   @Override
   public String toString(){
      return name+": ["+x+";"+y+"]";
   }


   /**
    * @return true, if the ids of two cities are the same
    */
   @Override
   public boolean equals(Object obj) {
      if(obj==null) return false;
      if(obj==this) return true;
      if(!(obj instanceof City)) return false;
      return ((City)obj).id==this.id;
   }

   
   /**
    * return numeric id of the city - index of city in the original arrays of cities
    * main characteristic is, that id is less then the length of the city array 
    * @return id of the city.
    */
   public int getId() {
      return id;
   }
   
   
   public void set_energy(){
	   consumptionRate=nGroupID*35+4;
   }
   
}