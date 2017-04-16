package uk.ac.manchester.dstoolkit.service.impl.util.statistics;

/***
 * Implementation of the Bayesian formula
 * 
 * @author klitos
 *
 */
public class BayesianTheorem {
   private double likelihood = 0.0;
   private double prior = 0.0;
   private double n_likelihood = 0.0;
    
   /*Constructor*/
   public BayesianTheorem(double l, double p, double nl) {
	   this.likelihood = l;
       this.prior = p;
       this.n_likelihood = nl;        
   }   
    
   //Calculate the posterior
   public double calcPosterior() {          
       return (likelihood * prior) / ((likelihood * prior) + (n_likelihood * (1 - prior)));
   }//end getPosterior()
   
   //Calculate likelihood with Laplace
   public static double likelihoodWithLaplace(int count, int N, int k, int classes) {
	   return (count + k) / (double) (N + (k*classes));	   
   }//end likelihoodWithLaplace()
}//end BayesianTheorem
