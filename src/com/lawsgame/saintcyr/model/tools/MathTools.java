package com.lawsgame.saintcyr.model.tools;

import java.util.Random;

public class MathTools {
    private static boolean TEST = true;

    public static void main(String[] args) {

    }


    private static final Random rand = new Random();


    public static int[] calcBinomialLawExpectation(int n, float p) {
        int mean = (int) (n * p);
        int standardDeviationX3 = (int) (3*Math.sqrt(mean * (1 - p)));
        return new int[]{ mean - standardDeviationX3, mean, mean + standardDeviationX3 };
    }


    public static int doBinomialLaw(int n, float p) throws MathToolsException{
        if(n > 30 && p * n > 5 && (1 - p) * n > 5){
            return doBinomialLawGauss(n, p);
        }
        return doBinomialLawExperiment(n, p);
    }


    /**
     * approximation of Binomial low through gauss
     * @param n
     * @param p
     * @return
     * @throws MathToolsException
     */
    private static int doBinomialLawGauss(int n, float p) throws MathToolsException {
        if (p < 0 || 1 < p) {
            throw new MathToolsException("p is not a probability :" + p);
        } else if (n < 1) {
            return 0;
        }
        double mean = n * p;
        double standardDeviation = Math.sqrt(mean * (1 - p));
        double nbSuccesses = mean + standardDeviation * rand.nextGaussian();
        if (nbSuccesses < 0) {
            nbSuccesses = 0.0;
        }
        return (int) nbSuccesses;

    }


    /**
     *
     * @param n : number of experience
     * @param p : probability of success
     * @return number of successes
     */
    private static int doBinomialLawExperiment(int n, float p) throws MathToolsException{
        int nbSuccesses = 0;
        if(p < 0 || 1 < p){
            throw new MathToolsException("p is not a probability :"+p);
        }else if(n  < 1){
            return 0;
        }
        int i;
        for(i = 0; i < n; i++){
            if(rand.nextFloat() < p){
                nbSuccesses += 1;
            }
        }
        return nbSuccesses;
    }

    public static float gauss(float mean, float standardDeviation) {
        return (float) (mean + standardDeviation * rand.nextGaussian());
    }

    public static float fact(int n) throws MathToolsException{
        if(n<0){
            throw new MathToolsException("n fac for negative value: "+n);
        }
        int res = 1;
        for(int q = 1; q <= n; q++){
            res *= q;
        }
        return res;
    }


    public static float pow(float x, int p) throws MathToolsException{
        if(p < 0){
            throw new MathToolsException("power lesser than 0: "+p);
        }
        float res = 0;
        for(int q = 0; q < p; q++){
            res *= x;
        }
        return res;
    }

    public static float logNormal(double logmean, double logsd) throws MathToolsException{

        if(logsd < 0 ) {
            throw new MathToolsException("standard deviation lesser than 0 ("+logsd+")");
        }
        double meanNormal = Math.log(logmean*logmean / Math.sqrt(logmean*logmean  + logsd*logsd));
        double sdNormal = Math.sqrt(Math.log(1 + (logsd*logsd)/(logmean*logmean)));
        double normalRes = meanNormal + sdNormal * rand.nextGaussian();
        double logNormalResult = Math.exp(normalRes);

        System.out.printf("log normal (%s, %s) => normal (%s, %s) => logres: %s\n", logmean, Math.sqrt(logsd*logsd), meanNormal, sdNormal, (int)logNormalResult);

        return (float) logNormalResult;
    }

    public static class MathToolsException extends Exception{
        public MathToolsException(String message){
            super(message);
        }
    }
}
