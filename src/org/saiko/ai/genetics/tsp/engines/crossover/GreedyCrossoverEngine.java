/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/engines/crossover/GreedyCrossoverEngine.java,v $
 * $Id: GreedyCrossoverEngine.java,v 1.3 2005/08/23 23:18:05 dsaiko Exp $
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
 * $Log: GreedyCrossoverEngine.java,v $
 * Revision 1.3  2005/08/23 23:18:05  dsaiko
 * Finished.
 *
 * Revision 1.2  2005/08/22 22:13:53  dsaiko
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

package org.saiko.ai.genetics.tsp.engines.crossover;

import java.util.Iterator;
import java.util.LinkedList;
import org.saiko.ai.genetics.tsp.City;
import org.saiko.ai.genetics.tsp.TSPChromosome;
import org.saiko.ai.genetics.tsp.engines.simpleUnisexMutatorHibrid2Opt.SimpleUnisexMutatorHibrid2OptEngine;

/**
 * @author Dusan Saiko (dusan@saiko.cz)
 * Last change $Date: 2005/08/23 23:18:05 $
 * 
 * Extends the SimpleUnisexMutatorEngine by providing new method for getting childs
 * to new population.
 * 
 * For the population modification, GreeadyCrossover algorithm is taken and adapted
 * from org.jgap.impl.GreedyCrossover
 * 
 * @see org.saiko.ai.genetics.tsp.engines.simpleUnisexMutatorHibrid2Opt.SimpleUnisexMutatorHibrid2OptEngine
 * @see org.jgap.impl.GreedyCrossover
 * @see #getChild(TSPChromosome) 
 * @see org.saiko.ai.genetics.tsp.TSPEngine
 */
public class GreedyCrossoverEngine extends SimpleUnisexMutatorHibrid2OptEngine {

   /** String containing the CVS revision. **/
   @SuppressWarnings("hiding")
   public final static String  CVS_REVISION = "$Revision: 1.3 $";


   /**
    * Create childs from the bestCount elements of population
    * Creates child from two parent
    */
   @Override
   protected void growPopulation(int bestCount) {
      //randomly find the parent
      int i1=rnd.nextInt(bestCount);
      int i2=rnd.nextInt(bestCount);
      if(i1==i2) {
         if(i2>0) { i2--; } else {i2++; }
      }
      //get child from parent
      getChild(population.get(i1),population.get(i2));
   }
   
   /**
    * Creates child from two parents using GreeadyCrossover algorithm.
    * It creates child from parent1+parent2; parent2+parent1; mutated parent1 and mutated parent2
    * 
    * @see #getChild(TSPChromosome, TSPChromosome)
    * @see SimpleUnisexMutatorHibrid2OptEngine#mutate(City[])
    * @param parent1 
    * @param parent2 
    */
   protected void getChild(TSPChromosome parent1, TSPChromosome parent2) {

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

      population.add(new TSPChromosome(child1));
      population.add(new TSPChromosome(child2));
      population.add(new TSPChromosome(child3));
      population.add(new TSPChromosome(child4));
      population.add(new TSPChromosome(child5));
      population.add(new TSPChromosome(child6));
   }

   /**
    * Creates one child from two parents applying the CrossOver algorithm
    * for genetic mating of chromosomes.
    * This code is taken and adjusted from rg.jgap.impl.GreedyCrossover
    * 
    * In short, algorithm takes first city from parent1 and looks for the
    * way from this first city in both, parent1 and parent2. then it uses the
    * better next city
    * 
    * @see org.jgap.impl.GreedyCrossover
    * @param chromosome1 - first chromosome
    * @param chromosome2 - second chromosome
    * @return newly ordered array of cities (=child =new chromosome)
    */
   protected City[] haveSex(TSPChromosome chromosome1, TSPChromosome chromosome2) {
      
     City[] c1=chromosome1.getCities();
     City[] c2=chromosome2.getCities();
      
     int n = c1.length;

     LinkedList<City> out = new LinkedList<City>();
     LinkedList<City> not_picked = new LinkedList<City>();

     out.add(c1[0]);
     for (int j = 1; j < n; j++) { // g[0] picked
       not_picked.add(c1[j]);
     }

     while (not_picked.size() > 1) {
       City last = out.getLast();
       City n1 = findNext(c1, last);
       City n2 = findNext(c2, last);

       City picked, other;

       boolean pick1;

       if (n1 == null) {
         pick1 = false;
       }
       else if (n2 == null) {
         pick1 = true;
       }
       else {
         pick1 = last.cost(n1) < last.cost(n2);
       }

       if (pick1) {
         picked = n1;
         other = n2;
       }
       else {
         picked = n2;
         other = n1;
       }

       if (out.contains(picked))
         picked = other;
       if (picked == null || out /* still */.contains(picked)) {
         // select a non-selected // it is not random
         picked = not_picked.getFirst();
       }

       out.add(picked);
       not_picked.remove(picked);
     }

     out.add(not_picked.getLast());

     City[] c = new City[n];
     Iterator gi = out.iterator();

     for (int i = 0; i < 0; i++) {
       c[i] = c1[i];
     }

     for (int i = 0; i < c.length; i++) {
       c[i] = (City) gi.next();
     }

     return c;

   }

   /**
    * Helper for GreedyCrossover getChild() algorithm.
    * It finds the next city after city "x" in the chromosome "cities"
    * @param cities - array in which to find the next city after city "x"
    * @param x - city for which we are looking for the next path 
    * @return next city to go from the chromosome
    */
   protected City findNext(City[] cities, City x) {
     for (int i = 0; i < cities.length - 1; i++) {
       if (cities[i].equals(x))
         return cities[i + 1];
     }
     //from the last city we go to the first one
     return cities[0];
   }   
}