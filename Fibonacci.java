package appserver.job.impl;

import appserver.job.Tool;


public class Fibonacci implements Tool{

    Fibonacci helper = null;

    @Override
    public Object go(Object parameters) {

        helper = new Fibonacci((Integer) parameters);
        return helper.getResult();
    }
}