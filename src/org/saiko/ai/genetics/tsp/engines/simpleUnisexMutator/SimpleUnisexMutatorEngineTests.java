/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/engines/simpleUnisexMutator/SimpleUnisexMutatorEngineTests.java,v $
 * $Id: SimpleUnisexMutatorEngineTests.java,v 1.2 2005/08/23 23:18:05 dsaiko Exp $
 * $Date: 2005/08/23 23:18:05 $
 * $Revision: 1.2 $
 * $Author: dsaiko $
 *
 * Traveling Salesman Problem genetic algorithm.
 * This source is released under GNU public licence agreement.
 * dusan@saiko.cz
 * http://www.saiko.cz/ai/tsp/
 * 
 * Change log:
 * $Log: SimpleUnisexMutatorEngineTests.java,v $
 * Revision 1.2  2005/08/23 23:18:05  dsaiko
 * Finished.
 *
 * Revision 1.1  2005/08/22 22:13:52  dsaiko
 * Packages rearanged
 *
 * Revision 1.1  2005/08/22 22:08:51  dsaiko
 * Created engines with heuristics
 *
 * Revision 1.1  2005/08/13 15:02:09  dsaiko
 * build task
 *
 * Revision 1.2  2005/08/13 10:32:34  dsaiko
 * JUnit tests modified - checking the result of algorithm (not for JGap - it does not work there ...)
 *
 * Revision 1.1  2005/08/12 23:52:17  dsaiko
 * Initial revision created
 *
 */

package org.saiko.ai.genetics.tsp.engines.simpleUnisexMutator;

import junit.framework.TestCase;
import org.saiko.ai.genetics.tsp.City;
import org.saiko.ai.genetics.tsp.TSP;

/**
 * @author Dusan Saiko (dusan@saiko.cz)
 * Last change $Date: 2005/08/23 23:18:05 $
 *
 * SimpleUnisexMutatorEngine TestCase
 * @see org.saiko.ai.genetics.tsp.engines.simpleUnisexMutatorHibrid2Opt.SimpleUnisexMutatorHibrid2OptEngine
 */
public class SimpleUnisexMutatorEngineTests extends TestCase {

   /** String containing the CVS revision. **/
   public final static String CVS_REVISION = "$Revision: 1.2 $";
   

   /**
    * Test routine
    */
   public void testEngine() {
      TSP tsp=new TSP();
      
      City c1=new City(0,tsp.configuration,"a",1,1);
      City c2=new City(1,tsp.configuration,"b",2,1);
      City c3=new City(2,tsp.configuration,"c",2,2);
      City c4=new City(3,tsp.configuration,"d",1,2);
      
      //clone() array operation
      City[] gene=new City[]{
            c1, c2, c3, c4
      };

      City.initDistanceCache(gene.length);
      
      SimpleUnisexMutatorEngine e=new SimpleUnisexMutatorEngine();
      tsp.configuration.setInitialPopulationSize(10000);
      e.initialize(tsp.configuration,gene);
      for(int i=0; i<100; i++) {
    	  e.nextGeneration();
      }
      assertTrue(e.getBestChromosome().getTotalDistance()==4.0);
   }
}