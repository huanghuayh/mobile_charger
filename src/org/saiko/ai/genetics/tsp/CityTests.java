/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/CityTests.java,v $
 * $Id: CityTests.java,v 1.2 2005/08/23 23:18:05 dsaiko Exp $
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
 * $Log: CityTests.java,v $
 * Revision 1.2  2005/08/23 23:18:05  dsaiko
 * Finished.
 *
 * Revision 1.1  2005/08/12 23:52:57  dsaiko
 * Initial revision created
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
 * City TestCase
 * @see org.saiko.ai.genetics.tsp.City
 */
public class CityTests extends TestCase {

   /** String containing the CVS revision. **/
   public final static String CVS_REVISION = "$Revision: 1.2 $";
   

   /**
    * Test routine
    */
   public void testCity() {
      TSP tsp=new TSP();
      
      City c1=new City(0,tsp.configuration,"x",2,1);
      City c2=new City(1,tsp.configuration,"y",2,2);
      
      City.initDistanceCache(2);
      
      assertTrue(c1.distance(c2)==c2.distance(c1));
      assertTrue(c1.distance(c1)==0);
      assertTrue(c2.distance(c2)==0);
      assertTrue(c1.distance(c2)==1);

      assertTrue(c1.cost(c2)==c2.cost(c1));
      assertTrue(c1.cost(c1)==0);
      assertTrue(c2.cost(c2)==0);
      assertTrue(c1.cost(c2)==1);
      
      assertTrue(c1.equals(c1));
      assertTrue(c2.equals(c2));
      assertTrue(!c1.equals(c2));
      assertTrue(!c2.equals(c1));
      
      //clone() array operation
      City[] gene1=new City[]{
            c1, c1, c2, c2, c1, c2, c1, c2, c2, c1
      };
      
      City[] gene2=gene1.clone();
      
      assertTrue(gene1!=gene2);
      for(int i=0; i<gene1.length; i++) {
         assertTrue(gene1[i].equals(gene2[i]));
         assertTrue(gene1[i]==gene2[i]);
      }
   }
}