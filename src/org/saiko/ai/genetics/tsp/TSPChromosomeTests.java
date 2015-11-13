/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/TSPChromosomeTests.java,v $
 * $Id: TSPChromosomeTests.java,v 1.2 2005/08/23 23:18:05 dsaiko Exp $
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
 * $Log: TSPChromosomeTests.java,v $
 * Revision 1.2  2005/08/23 23:18:05  dsaiko
 * Finished.
 *
 * Revision 1.1  2005/08/12 23:52:17  dsaiko
 * Initial revision created
 *
 */

package org.saiko.ai.genetics.tsp;

import junit.framework.TestCase;

/**
 * @author Dusan Saiko (dusan@saiko.cz)
 * Last change $Date: 2005/08/23 23:18:05 $
 *
 * TSPChromosome TestCase
 * @see org.saiko.ai.genetics.tsp.TSPChromosome
 */
public class TSPChromosomeTests extends TestCase {

   /** String containing the CVS revision. **/
   public final static String CVS_REVISION = "$Revision: 1.2 $";
   

   /**
    * Test routine
    */
   public void testChromosome() {
      TSPChromosome c=new TSPChromosome(new TSP().cities);
      assertTrue(c.totalCost>=c.totalDistance && c.totalDistance>0);
      
   }
}