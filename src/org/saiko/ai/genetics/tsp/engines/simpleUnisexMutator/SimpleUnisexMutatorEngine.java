/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/engines/simpleUnisexMutator/SimpleUnisexMutatorEngine.java,v $
 * $Id: SimpleUnisexMutatorEngine.java,v 1.2 2005/08/23 23:18:05 dsaiko Exp $
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
 * $Log: SimpleUnisexMutatorEngine.java,v $
 * Revision 1.2  2005/08/23 23:18:05  dsaiko
 * Finished.
 *
 * Revision 1.1  2005/08/22 22:13:52  dsaiko
 * Packages rearanged
 *
 * Revision 1.1  2005/08/22 22:08:51  dsaiko
 * Created engines with heuristics
 *
 * Revision 1.3  2005/08/13 15:02:09  dsaiko
 * build task
 *
 * Revision 1.2  2005/08/13 14:41:35  dsaiko
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/12 23:52:17  dsaiko
 * Initial revision created
 *
 */

package org.saiko.ai.genetics.tsp.engines.simpleUnisexMutator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.saiko.ai.genetics.tsp.City;
import org.saiko.ai.genetics.tsp.TSPChromosome;
import org.saiko.ai.genetics.tsp.TSPConfiguration;
import org.saiko.ai.genetics.tsp.TSPEngine;

/**
 * @author Dusan Saiko (dusan@saiko.cz)
 * Last change $Date: 2005/08/23 23:18:05 $
 *
 * Simple random unisex genetic algorithm for solving the
 * Traveling Salesman Probleme
 * 
 * This class performs basic genetic operations and calls growPopulation(..) method.
 * In this way, this class can be used as supper class for overriding.
 * 
 * Initialization and nextGeneration method implement multi thread algorithm
 * which uses at maximum Runtime.getRuntime().availableProcessors()*2 threads.
 * 
 * This algorithm creates child in such way, that it just randomly swaps
 * two items in from parent
 * 
 * @see #getChild(TSPChromosome)
 * @see org.saiko.ai.genetics.tsp.engines.crossover.GreedyCrossoverEngine
 * @see org.saiko.ai.genetics.tsp.TSPEngine
 */
public class SimpleUnisexMutatorEngine implements TSPEngine {

   /** String containing the CVS revision. **/
   public final static String CVS_REVISION = "$Revision: 1.2 $";

      /**
    * Population of the chromosomes
    */
   protected List<TSPChromosome> population=Collections.synchronizedList(new ArrayList<TSPChromosome>()); 

   /**
    * Random generator
    */
   protected Random rnd;
   
   /**
    * Current population size - the population may grow
    */
   protected int populationSize;
   
   /**
    * Mutation ratio of population
    * (exactly percentage of mutation = 1/mutationRatio)
    */
   protected int mutationRatio;

   /**
    * configuration paramteres of application
    * @see TSPConfiguration
    */
   protected TSPConfiguration configuration;
   
   /**
    * @see org.saiko.ai.genetics.tsp.TSPEngine#initialize
    */
   public void initialize(TSPConfiguration appConfiguration, final City cities[]) {
      this.configuration=appConfiguration;
      rnd=new Random();

      //clear the population if the engine is re-initialized
      population.clear();
      
      populationSize=configuration.getInitialPopulationSize();
      
      final List<Integer> runningThreads=Collections.synchronizedList(new ArrayList<Integer>());

      final Thread parentThread=Thread.currentThread();
      //this has to be computed again as availableProcessors() can change in the time
      int maxThreadCount=Runtime.getRuntime().availableProcessors()*2;
      
      for(int i=0; i<maxThreadCount; i++) {
         runningThreads.add(1);
         new Thread(new Runnable(){public void run() {
            while(population.size()<populationSize) {
               TSPChromosome chromosome=new TSPChromosome(cities);
               randomize(rnd,chromosome.getCities());
               chromosome.computeCost();
               population.add(chromosome);
            }
            runningThreads.remove(0);
            parentThread.interrupt();
         }}).start();
      }
      while(runningThreads.size()>0) {
         try {
            Thread.sleep(1000);
         } catch(Throwable e) {
            //nop
         }
      } 
      
      orderPopulation();
      //recompute utation ratio so we can use if(rnd.nextInt(mutationRatio)==0)
      mutationRatio=(int)(1/configuration.getMutationRatio());
   } 
   
   /**
    * Randomizes cities in chromosome
    * @param randomizer 
    * @param cities 
    */
   public static void randomize(final Random randomizer, final City[] cities) {
      
      final int length=cities.length;
      
      //make sure that each city is swapped at leas once
      //else there could be created lots of similar chromosomes
      for(int i=0; i<length; i++) {
         int i1=i;
         int i2=randomizer.nextInt(length);
         if(i2==i1) {
            if(i2>0) { i2--; } else { i2++; }
         }
         City swap=cities[i1];
         cities[i1]=cities[i2];
         cities[i2]=swap;
      }  

      //randomize all the set more
      int randomizerSteps=10*length;

      //do the randomization of cities
      for(int n=0; n<randomizerSteps; n++) {
         int i1=randomizer.nextInt(length);
         int i2=randomizer.nextInt(length);
         if(i1!=i2) {
               City swap=cities[i1];
               cities[i1]=cities[i2];
               cities[i2]=swap;
         }
      }
   }

   /**
    * @see org.saiko.ai.genetics.tsp.TSPEngine#getPopulationSize
    */  
   public int getPopulationSize() {
      return populationSize;
   }

    /**
    * @see org.saiko.ai.genetics.tsp.TSPEngine#getBestChromosome
     */
   public TSPChromosome getBestChromosome() {
      return population.get(0);
   }
   
   /**
    * Orders population of chromosomes according to the costs of chromosomes
    * in ascending order
    */
   public void orderPopulation() {
      Collections.sort(population,new Comparator<TSPChromosome>() {
         public int compare(TSPChromosome o1, TSPChromosome o2) {
            double cost1=o1.getTotalCost();
            double cost2=o2.getTotalCost();
            return (cost1<cost2 ? -1 : (cost1>cost2?1 : 0));
         }
      });
   }   
   
   
   /**
    * @see org.saiko.ai.genetics.tsp.TSPEngine#nextGeneration
    */
   public void nextGeneration() {
      //the best is the first half of population 
      final int bestCount=(int)(populationSize*0.5);

      //leave only the best part of population
      int size=population.size();
      while(size>bestCount) {
         population.remove(size-1);
         size--;
      }
      
      //mutate from the first half of population
      final List<Integer> runningThreads=Collections.synchronizedList(new ArrayList<Integer>());
      final Thread parentThread=Thread.currentThread();

      //this has to be computed again as availableProcessors() can change in the time
      int maxThreadCount=Runtime.getRuntime().availableProcessors()*2;

      //it does not matter f countPerThread will not be exact
      final int countPerThread=bestCount/maxThreadCount;
      
      for(int i=0; i<maxThreadCount; i++) {
            runningThreads.add(1);
            new Thread(new Runnable(){public void run() {
               for(int index=0; index<countPerThread; index++) {
                  growPopulation(bestCount);
               }
               runningThreads.remove(0);
               parentThread.interrupt();
            }}).start();
      }
      while(runningThreads.size()>0) {
         try {
            Thread.sleep(1000);
         } catch(Throwable e) {
            //nop
         }
      }       

      //now, order the chromosomes according to the costs,
      orderPopulation();

      //now align the population size
      //remove the last (worst) part of population
      //the mutation can create more elements than there should 
      //remain in the population
      size=population.size();
      while(size>populationSize) {
         population.remove(size-1);
         size--;
      }
      
      //if specified by TSP_POPULATION_GROW,
      //grow the population
      populationSize=(int)(populationSize*(1+configuration.getPopulationGrow()));
   }

   /**
    * Create childs from the bestCount elements of population
    * @param bestCount
    */
   protected void growPopulation(int bestCount) {
      //the child here is created only from unisex adaptation
      getChild(population.get(rnd.nextInt(bestCount)));
   }
   
   /**
    * Creates new randomly mutated chromosome from its parent.
    * This is the most simple unisex genetic mutation algorithm.
    * @param parent
    */
   protected void getChild(TSPChromosome parent) {

      //clone the cities to new array
      City newCities[]=parent.getCities();
      
      //aply random swaping to cities
      mutate(newCities);
      
      //add new chromosome to population
      population.add(new TSPChromosome(newCities));
   }

   /**
    * Mutate randomly the cities (Chromosome)
    * this is dependent on mutation ratio set from TSP
    * @param cities
    */
   protected void mutate(City cities[]) {
      if(rnd.nextInt(mutationRatio)==0) {
         //randomly mutate two items in the chromosome
         int i1=rnd.nextInt(cities.length);
         int i2=rnd.nextInt(cities.length);
         if(i1==i2) {
            if(i2>0) {
               i2--;
            } else {
               i2++;
            }
         }
         City swap=cities[i1];
         cities[i1]=cities[i2];
         cities[i2]=swap;
      }
   }
}