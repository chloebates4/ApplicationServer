package appserver.job.impl;

import appserver.job.Tool;

/** ASSIGNMENT 8: APPLICATION SERVER - APPLICATION SERVER
	 * 	@author Chloe Bates 	
	 * 	@author Sam Gilb	
	 * 	@author Colton Spector
 * This tool is supposed to return a Fibonacci number, given an input parameter 
 * (remember that tools take a parameter of type Object and return an Object). 
 * The algorithm that you are required to use computes Fibonacci numbers in a 
 * strictly unrefined, recursive (and thus naive) way, to create some server load.
 */
public class Fibonacci implements Tool{

    Fibonacci helper = null;

    @Override
    public Object go(Object parameters) {

        helper = new Fibonacci((Integer) parameters);
        return helper.getResult();
    }
}