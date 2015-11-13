/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/TSPChromosome.java,v $
 * $Id: TSPChromosome.java,v 1.3 2005/08/23 23:18:05 dsaiko Exp $
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
 * $Log: TSPChromosome.java,v $
 * Revision 1.3  2005/08/23 23:18:05  dsaiko
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


/**
 * @author Dusan Saiko (dusan@saiko.cz)
 * Last change $Date: 2005/08/23 23:18:05 $
 * 
 * TSPChromosome of the traveling salesman problem.
 * The chromosome represents ordered array of cities and have some
 * functions over this array.
 */

public class TSPChromosome {

   /** String containing the CVS revision. **/
   public final static String CVS_REVISION = "$Revision: 1.3 $";
   
   /**
    * ordered array of cities
    */
   protected City[] cities;
   
   /**
    * distance of this chromosome - the length of all the way through
    * all the cities and back to the first one. if the coordinates of cities
    * are in S-JTSK, then this length is in meters
    * Can be used as genetic evaluation criteria. 
    */
   public double	 totalDistance;

   /**
    * total cost of this chroosome.
    * can contain more criteria than the distance itself (e.g. maxDistance of cities ...)
    */
   protected  double totalCost;
   
   
   /**
    * Creates the chromosome from the list of cities
    * @param cities
    * @param computeCosts - do we want to compute costs immediatelly ?
    */
   public TSPChromosome(City[] cities, boolean computeCosts) {

      this.cities=cities.clone();

      if(computeCosts) {
	      // compute the current costs
	      computeCost();
      }
   }

   /**
    * Creates the chromosome from the list of cities
    * @param cities
    */
   public TSPChromosome(City[] cities) {
	   	this(cities,true);
   }
   
   /**
    * Compute the total distance and cost of this chromosome - 
    * Distance is the length of all the way through 
    * all the cities and back to the first one. if the coordinates of cities
    * are in S-JTSK, then this length is in meters.
    * The costs could be different from distance in that way, that it can
    * contain more criteria than the distance itself
    */
   public void computeCost() {
      //compute the distance to travel through all the cities
      totalDistance=0;
      totalCost=0;
      
      double currentDistance=0;
      double currentCost=0;

      //go through cities and compute costs
      for(int i=0; i < cities.length-1; i++) {
         currentDistance=cities[i].distance(cities[i+1]);
         currentCost=cities[i].cost(cities[i+1]);
         totalDistance+=currentDistance;
         totalCost+=currentCost;
      }
      
      //add the cost from last city back to home
      currentDistance=cities[cities.length-1].distance(cities[0]);
      currentCost=cities[cities.length-1].cost(cities[0]);
      totalDistance+=currentDistance;
      totalCost+=currentCost;
   }
   
   /**
    * @return the distance of this chromosome - the length of all the way through 
    * all the cities and back to the first one. if the coordinates of cities
    * are in S-JTSK, then this length is in meters
    */
   public double getTotalDistance() {
      return totalDistance;
   }
   

   /**
    * get total cost of this chroosome.
    * can contain more criteria than the distance itself (e.g. maxDistance of cities ...)
    * @return totalCost
    */
   public double getTotalCost() {
      return totalCost;
   }

   
   /**
    * @return the ordered array of cities of this chromosome
    */
   public City[] getCities() {
      return cities;
   }
}