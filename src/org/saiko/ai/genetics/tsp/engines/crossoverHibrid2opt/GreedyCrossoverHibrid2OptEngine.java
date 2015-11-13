/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/engines/crossoverHibrid2opt/GreedyCrossoverHibrid2OptEngine.java,v $
 * $Id: GreedyCrossoverHibrid2OptEngine.java,v 1.2 2005/08/23 23:18:04 dsaiko Exp $
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
 * $Log: GreedyCrossoverHibrid2OptEngine.java,v $
 * Revision 1.2  2005/08/23 23:18:04  dsaiko
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
 * Revision 1.1  2005/08/12 23:52:17  dsaiko
 * Initial revision created
 *
 */

package org.saiko.ai.genetics.tsp.engines.crossoverHibrid2opt;

import org.saiko.ai.genetics.tsp.City;
import org.saiko.ai.genetics.tsp.TSPChromosome;
import org.saiko.ai.genetics.tsp.engines.crossover.GreedyCrossoverEngine;
import org.saiko.ai.genetics.tsp.engines.simpleUnisexMutatorHibrid2Opt.SimpleUnisexMutatorHibrid2OptEngine;

/**
 * @author Dusan Saiko (dusan@saiko.cz)
 * Last change $Date: 2005/08/23 23:18:04 $
 * 
 * Extends the GreedyCrossover engine by providing heuristics to extend greedy crossover mutation
 * 
 * For the population modification, GreeadyCrossover algorithm is taken and adapted
 * from org.jgap.impl.GreedyCrossover
 *
 * For heuristics, the 2opt mutation is used, as described at http://www.gcd.org/sengoku/docs/arob98.pdf
 * 
 * @see org.saiko.ai.genetics.tsp.engines.simpleUnisexMutatorHibrid2Opt.SimpleUnisexMutatorHibrid2OptEngine
 * @see org.jgap.impl.GreedyCrossover
 * @see org.saiko.ai.genetics.tsp.engines.crossover.GreedyCrossoverEngine
 * @see #getChild(TSPChromosome) 
 * @see org.saiko.ai.genetics.tsp.TSPEngine
 */
public class GreedyCrossoverHibrid2OptEngine extends GreedyCrossoverEngine {

   /** String containing the CVS revision. **/
   @SuppressWarnings("hiding")
   public final static String  CVS_REVISION = "$Revision: 1.2 $";

   
   /**
    * Creates child from two parents using GreeadyCrossover algorithm.
    * It creates child from parent1+parent2; parent2+parent1; mutated parent1 and mutated parent2
    * 
    * @see #getChild(TSPChromosome, TSPChromosome)
    * @see SimpleUnisexMutatorHibrid2OptEngine#mutate(City[])
    * @param parent1 
    * @param parent2 
    */
   @Override
   protected void getChild(TSPChromosome parent1, TSPChromosome parent2) {
	  //greedy crossover and random mutation
      City child1[]=parent1.getCities().clone();
      City child2[]=parent2.getCities().clone();
      City child3[]=haveSex(parent1, parent2);
      City child4[]=haveSex(parent2, parent1);
      City child5[]=child3.clone();
      City child6[]=child4.clone();
      
      mutate(child1);
      mutate(child2);
      //mutate(child3);
      //mutate(child4);
      mutate(child5);
      mutate(child6);

      //2opt heuristics
      heuristics2opt(child1);
      heuristics2opt(child2);
      heuristics2opt(child3);
      heuristics2opt(child4);
      heuristics2opt(child5);
      heuristics2opt(child6);

      population.add(new TSPChromosome(child1));
      population.add(new TSPChromosome(child2));
      population.add(new TSPChromosome(child3));
      population.add(new TSPChromosome(child4));
      population.add(new TSPChromosome(child5));
      population.add(new TSPChromosome(child6));
   }
   
   /**
    * Creates childs as heuristics optimalizations of chromosome
    * the algorithm is described at described at http://www.gcd.org/sengoku/docs/arob98.pdf
    * and used at http://www.zlote.jabluszko.net/tsp/
    * @param cities - chromosome to be optimalized
 	*/
   public static void heuristics2opt(City[] cities) {
       boolean done = false;
       int count = cities.length;
       for(int k = 0; k < count && !done; k++)
       {
           done = true;
           for(int i = 0; i < count; i++)
           {
               for(int j = i + 2; j < count; j++)
                   if(
                      cities[i].distance(cities[(i + 1) % count]) + cities[j].distance(cities[(j + 1) % count]) 
                		   > 
                   	  cities[i].distance(cities[j]) + cities[(i + 1) % count].distance(cities[(j + 1) % count])
                   )
                   {
                       City tmp = cities[(i + 1) % count];
                       cities[(i + 1) % count] = cities[j];
                       cities[j] = tmp;
                       reverse(cities, i + 2, j - 1);
                       done = false;
                   }
           }
       }
   }
   
   /**
    * Part of heuristics optimalizations of chromosome
    * taken from code at http://www.zlote.jabluszko.net/tsp/
    * @param cities - chromosome to be optimalized
    * @param startIndex 
    * @param stopIndex 
 	*/
   public static void reverse(City[] cities, int startIndex, int stopIndex)
   {
       if(startIndex >= stopIndex || startIndex >= cities.length || stopIndex < 0)
           return;
       for(; startIndex < stopIndex; stopIndex--)
       {
           City tmp = cities[startIndex];
           cities[startIndex] = cities[stopIndex];
           cities[stopIndex] = tmp;
           startIndex++;
       }

   }
   
}