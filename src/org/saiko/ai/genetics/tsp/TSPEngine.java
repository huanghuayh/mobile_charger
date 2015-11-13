/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/TSPEngine.java,v $
 * $Id: TSPEngine.java,v 1.2 2005/08/23 23:18:04 dsaiko Exp $
 * $Date: 2005/08/23 23:18:04 $
 * $Revision: 1.2 $
 * $Author: dsaiko $
 *
 * Traveling Salesman Problem genetic algorithm.
 * This source is released under GNU public licence agreement.
 * dusan@saiko.cz
 * http://www.saiko.cz/ai/tsp/
 * 
 * Change log:
 * $Log: TSPEngine.java,v $
 * Revision 1.2  2005/08/23 23:18:04  dsaiko
 * Finished.
 *
 * Revision 1.1  2005/08/12 23:52:17  dsaiko
 * Initial revision created
 *
 */

package org.saiko.ai.genetics.tsp;

/**
 * @author Dusan Saiko (dusan@saiko.cz)
 * Last change $Date: 2005/08/23 23:18:04 $
 *
 * Interface definition for Traveling salesman genetic engine
 */
public interface TSPEngine {
   
   /** String containing the CVS revision. **/
   public final static String CVS_REVISION = "$Revision: 1.2 $";

   
   /**
    * Initialize engine for given population size and list of cities.
    * Can be calledseveral times to reinitialize engine.
    * @param appConfiguration
    * @param cities
    * @see TSPConfiguration
    */
   public void initialize(TSPConfiguration appConfiguration, City cities[]);
   
   
   /**
    * @return current population size from the engine. the population could be growing.
    */
   public int  getPopulationSize();
   
   /**
    * @return the best chromosome from the population
    */
   public TSPChromosome getBestChromosome();
   
   /**
    * Make new generation of population.
    * the genetics principles are left to responsibility of engine
    */
   public void nextGeneration();
}
