/**
package PbsStat
public class PbsStat extends Object

@author P.B. Stark stark@stat.berkeley.edu
@version 0.1
@copyright 1997--2013 by P.B. Stark. All rights reserved.

Last modified 14 May 2007
.
Statistical and mathematical functions

Statistical:

betaCdf:        beta CDF
betaPdf:        beta density
binomialPmf:    binomial probability mass function
binomialCdf:    binomial CDF
binomialCoef:   binomial coefficients
chi2Cdf:        chi-square CDF
chi2Pdf:        chi-square density
chi2Inv:        chi-square quantile function
expCdf:         exponential CDF
expPdf:         exponential density
factorial:      factorial
gammaCdf:       gamma CDF
gammaPdf:       gamma density
geoCdf:         geometric CDF
geoPmf:         geometric probability mass function
geoTail:        geometric tail probability
hyperGeoPmf:    hypergeometric probability mass function
hyperGeoCdf:    hypergeometric CDF
hyperGeoTail:   hypergeometric upper tail probability
listOfRandInts: array of uniform random integers
listOfDistinctRandInts: array of unique random integers
negBinomialPmf: negative binomial probability mass function
negBinomialCdf: negative binomial CDF
normCdf:        normal CDF
normPdf:        normal PDF
normInv:        inverse normal CDF
normRand:       normal random variate
poissonPmf:     Poisson probability mass function
poissonCdf:     Poisson CDF
poissonTail:    Poisson tail probability
Mean:           mean of a double array
MeanNaN:        mean of data, removing NaNs that might appear
removeNaN:      remove NaN values from a double[]
removeZero:     remove zero values from a double[]
removeZeroNaN:  remove zero and NaN values from a double[]
rNorm:          pseudorandom normal
Sd:             sd of a double array
SdNaN:          sd of data, removing NaN's that might appear
sampleSd:       shat (sample sd) of a double array
tCdf:           Student's t-distribution cdf
tPdf:           Student's t density
tInv:           inverse CDF of Student's t
CorrCoef:       correlation coefficient
quantile:       quantile of a double[].
quartiles:      min, LQ, Med, UQ, max of a double[].

special functions:

beta:           beta function
incBeta:        incomplete beta function
lnBeta:         log of the beta function
erfc:           complementary error function
lnGamma:        log of the gamma function without computing gamma(x) (for stability)
incGamma:       incomplete Gamma function

utility:

vMax:       maximum of a double array
vMin:       minimum of a double array
vMinMax:    min and max of a double array
Rms:        rms of a vector
scalVMult:  scalar times a vector
sgn:        signum function
sort:       shell sort algorithm, versions for double[], String[], Date[]
vDot:       dot product of two vectors
vPNorm:     p-norm of a vector
vCumSum:    cumulative sum of a vector
vSum:       vector addition
vTot:       sum of the components of a vector
vTwoNorm:   2-norm of a vector
matVMult:   matrix times a vector
matMatMult: matrix times a matrix

numerical analysis:

trapZoid:   numerical integration using trapezoid rule



*/
package PbsStat;
import java.util.*;

public abstract class PbsStat extends Object {
    public static final double rmin = 2.3e-308;
    public static final double eps = 2.3e-16;

    public static double betaGuts(double x, double a, double b) {
// guts of the incomplete beta function
        double ap1 = a + 1;
        double am1 = a - 1;
        double apb = a + b;
        double am = 1;
        double bm = am;
        double y = am;
        double bz = 1 - apb * x / ap1;
        double d = 0;
        double app = d;
        double ap = d;
        double bpp = d;
        double bp = d;
        double yold = d;
        double m = 1;
        double t;
        while (y-yold > 4*eps*Math.abs(y)) {
           t = 2 * m;
           d = m * (b - m) * x / ((am1 + t) * (a + t));
           ap = y + d * am;
           bp = bz + d * bm;
           d = -(a + m) * (apb + m) * x / ((a + t) * (ap1 + t));
           app = ap + d * y;
           bpp = bp + d * bz;
           yold = y;
           am = ap / bpp;
           bm = bp / bpp;
           y = app / bpp;
           if (m == 1) bz = 1;
           m++;
        }
        return(y);
    }

    public static double lnBeta(double x, double y) {
        return(lnGamma(x) + lnGamma(y) - lnGamma(x+y));
    }

    public static double betaCdf(double x, double a, double b) {
       if (a <= 0 || b <= 0) {
          return(Double.NaN);
       } else if (x >= 1) {
          return(1.0);
       } else if ( x > 0.0) {
          return(Math.min(incBeta(x ,a ,b),1.0));
       } else {
          return(0.0);
       }
    }

    public static double betaPdf(double x, double a, double b) {
        if (a <= 0 || b <= 0 || x < 0 || x > 1) {
            return(Double.NaN);
        } else if ((x == 0 && a < 1) || (x == 2 && b < 1)) {
            return(Double.POSITIVE_INFINITY);
        } else if (!(a <= 0 || b <= 0 || x <= 0 || x >= 1)) {
            return(Math.exp((a - 1)*Math.log(x) + (b-1)*Math.log(1 - x) - lnBeta(a,b)));
        } else {
            return(0.0);
        }
    }


    public static double[] binomialPmf(int n, double p) {
        double[] pmf = new double[n+1];
        pmf[0] = Math.pow((1-p),n);
        for ( int i=1; i < n+1; i++) {
           if (p < 1) {
               pmf[i] = pmf[i-1]*p*(n-i+1)/((1-p)*i);
           } else {
               pmf[i] = 0;
           }
        }
        if ( p == 1.0) {
           pmf[n] = 1.0;
        }
        return(pmf);
    }// ends binomialPmf

    public static double binomialPmf(int n, int k, double p) { // binomial pmf at one point.
      // EX = np; SD(X) = (np(1-p))^1/2.
        double pmf = binomialCoef(n,k)*Math.pow(p,k)*Math.pow((1-p),(n-k));
        return(pmf);
    } // ends binomialPmf(int, int, double)


    public static double binomialCdf(int n, double p, int k) {  // binomial CDF:  Pr(X <= k), X~B(n,p)
        if (k < 0) {
            return(0.0);
        } else {
            if (k >= n) {
                return(1.0);
            } else {
                double[] pmf = binomialPmf(n, p);
                double cdf = 0.0;
                for (int i = 0; i <= k; i++) {
                     cdf += pmf[i];
                }
                return(cdf);
            }
        }
    }

    public static double binomialCoef(int n, int k)
    { // binomial coefficient for n things taken k at a time.
        if (n < k || n < 0) {
            return(0.0);
        } else if ( k == 0 || n == 0 || n == k) {
            return(1.0);
        } else {
            int minnk = Math.min(k, n-k);
            double coef = 1;
            for (int j = 0; j < minnk; j++) {
                coef *= ((double) (n-j))/((double) (minnk-j));
            }
            return(coef);
        }
    }

    public static double chi2Cdf(double x, double df) { // chi-square cdf for df degrees of freedom, evaluated at x
        return(gammaCdf(x,df/2,2));
    }

    public static double chi2Pdf(double x, int df) {
        return chi2Pdf(x, (double) df);
    }

    public static double chi2Pdf(double x, double df) {
        if (x <= 0) {
            return(0.0);
        } else {
            return(gammaPdf(x, Math.floor(df)/2, 2));
        }
    }

    public static double chi2Inv(double p, int df) { // kluge for chi-square quantile function.
        double guess = Double.NaN;
        if (p == 0.0) {
            guess = 0.0;
        } else if ( p == 1.0 ) {
            guess = Double.POSITIVE_INFINITY;
        } else if ( p < 0.0 ) {
            guess = Double.NaN;
        } else {
            guess = Math.max(0.0, df + Math.sqrt(2*df)*normInv(p)); // guess from normal approx
            double currP = chi2Cdf( guess, df);
            double loP = currP;
            double hiP = currP;
            double guessLo = guess;
            double guessHi = guess;
            while (loP > p) { // step down
                guessLo = 0.8*guessLo;
                loP = chi2Cdf( guessLo, df);
            }
            while (hiP < p) { // step up
                guessHi = 1.2*guessHi;
                hiP = chi2Cdf( guessHi, df);
            }
            guess = (guessLo + guessHi)/2.0;
            currP = chi2Cdf( guess, df);
            while ( Math.abs(currP - p) > eps ) { // bisect
                if ( currP < p ) {
                    guessLo = guess;
                } else {
                    guessHi = guess;
                }
                guess = (guessLo + guessHi)/2.0;
                currP = chi2Cdf(guess, df);
            }
        }
        return(guess);
    }

    public static double expCdf(double x, double lam) {
        return(1-Math.exp(-x/lam));
    }

    public static double expPdf(double x, double lam) {
        return((1.0/lam)*Math.exp(-x/lam));
    }

    public static double factorial(int n) {
        double fac = Double.NaN;
        if (n >= 0) {
            int k=n;
            fac = 1.0;
            while (k > 0) {fac *= k--;}
        }
        return(fac);
    }

    public static double gammaCdf(double x, double a, double b) { // gamma distribution CDF.
        double p = Double.NaN;
        if (a <= 0 || b <= 0) {
        } else if (x <= 0) {
            p = 0.0;
        } else {
            p = Math.min(incGamma(x/b, a), 1.0);
        }
        return(p);
    }

    public static double gammaCdf(double x, double a) {
        return(gammaCdf(x, a, 1));
    }

    public static double gammaPdf(double x, double a, double b) {
        double ans = Double.NaN;
        if (a <= 0 || b <= 0) {
            ans = Double.NaN;
        } else if (x > 0) {
            ans = Math.exp((a-1)*Math.log(x)-(x/b)-lnGamma(a)-a*Math.log(b));
        } else if (x == 0 && a < 1) {
            ans = Double.POSITIVE_INFINITY;
        } else if (x == 0 && a == 1) {
            ans = 1/b;
        }
        return(ans);
    }

    public static double geoPmf(double p, int k) {
      // chance it takes k trials to the first success in iid Bernoulli(p) trials
      // EX = 1/p; SD(X) = sqrt(1-p)/p
        if (k < 1 || p == 0.0) {
            return(0.0);
        } else {
            return(Math.pow((1-p),k-1)*p);
        }
    }

    public static double geoCdf(double p, int k) {
       // chance it takes k or fewer trials to the first success in iid Bernoulli(p) trials
        if (k < 1 || p == 0.0) {
            return(0.0);
        } else {
            return(1-Math.pow( 1-p, k));
        }
    }

    public static double geoTail(double p, int k) {
      // chance of k or more trials to the first success in iid Bernoulli(p) trials
        return(1 - geoCdf(p, k-1));
    }

    public static double hyperGeoPmf(int N, int M, int n, int m) {
      // chance of drawing m of M objects in a sample of size n from
      // N objects in all.  p = (M C m)*(N-M C n-m)/(N C n)
      // EX = n*M/N; SD(X)= sqrt((N-n)/(N-1))*sqrt(np(1-p));
        double p;
        if ( n < m || N < M || M < m  || m < 0 || N <= 0 || n <= 0 || N < n) {
            return(0.0);
        } else {
            p = binomialCoef(M,m)*binomialCoef(N-M,n-m)/binomialCoef(N,n);
            return(p);
        }
    }

    public static double hyperGeoCdf(int N, int M, int n, int m) {
      // chance of drawing m or fewer of M objects in a sample of size n from
      // N objects in all
        double p=0.0;
        int mMax = Math.min(m,M);
        mMax = Math.min(mMax,n);
        for (int i = 0; i <= mMax; i++) {
            p += hyperGeoPmf(N, M, n, i);
        }
        return(p);
    }

    public static double hyperGeoTail(int N, int M, int n, int m) {
      // chance of drawing m or more of M objects in a sample of size n from
      // N objects in all
        double p=0.0;
        for (int i = m; i <= M; i++) {
            p += hyperGeoPmf(N, M, n, i);
        }
        return(p);
    }

   public static double incBeta(double x, double a, double b) {
/* incomplete beta function
       I_x(z,w) = 1/beta(z,w) * integral from 0 to x of t^(z-1) * (1-t)^(w-1) dt
   Ref: Abramowitz & Stegun, Handbook of Mathemtical Functions, sec. 26.5.

*/
        double res;
        if (x < 0 || x > 1) {
            res = Double.NaN;
        } else {
            res = 0;
            double bt = Math.exp(lnGamma(a+b) - lnGamma(a) - lnGamma(b) +
                        a*Math.log(x) + b*Math.log(1-x));
            if (x < (a+1)/(a+b+2)) {
                res = bt * betaGuts(x, a, b) / a;
            } else {
                res = 1 - bt*betaGuts(1-x, b, a) / b;
            }
        }
        return(res);
    }

    public static double incGamma(double x, double a) {
/*
        incomplete gamma function:
        incGamma(x,a) = 1 / gamma(a) *int_0^x t^(a-1) exp(-t) dt
*/
        double inc = 0;

        double gam = lnGamma(a+rmin);
        if (x == 0) {
            inc=0;
        } else if (a == 0) {
            inc=1;
        } else if (x < a+1) {
            double ap = a;
            double sum = 1/ap;
            double del = sum;
            while (Math.abs(del) >= 10*eps*Math.abs(sum)) {
                del *= x/(++ap);
                sum += del;
            }
            inc = sum * Math.exp(-x + a*Math.log(x) - gam);
        } else if (x >= a+1) {
           double a0 = 1;
           double a1 = x;
           double b0 = 0;
           double b1 = 1;
           double fac = 1;
           int n = 1;
           double g = 1;
           double gold = 0;
           double ana;
           double anf;
           while (Math.abs(g-gold) >= 10*eps*Math.abs(g)) {
                gold = g;
                ana = n - a;
                a0 = (a1 + a0 *ana) * fac;
                b0 = (b1 + b0 *ana) * fac;
                anf = n*fac;
                a1 = x * a0 + anf * a1;
                b1 = x * b0 + anf * b1;
                fac = 1 / a1;
                g = b1 * fac;
                n++;
           }
           inc = 1 - Math.exp(-x + a*Math.log(x) - gam) * g;
        }
        return(inc);
    }

    public static int[] listOfRandInts(int n, int lo, int hi) { // n random integers between lo and hi
        int[] list = new int[n];
        for (int i=0; i<n; i++) {
            list[i] = (int) Math.floor((hi+1 - lo)*Math.random()) + lo;
        }
        return(list);
    }


    public static int[] listOfDistinctRandInts(int n, int lo, int hi) { // n dintinct random integers between lo and hi
        int[] list = new int[n];
        int trial;
        int i=0;
        boolean unique;
        while (i < n) {
            trial = (int) Math.floor((hi+1 - lo)*Math.random()) + lo;
            unique = true;
            for (int j = 0; j < i; j++) {
                if (trial == list[j]) unique = false;
            }
            if (unique) {
                list[i] = trial;
                i++;
            }
        }
        return(list);
    }

    public static double negBinomialPmf(double p, int s, int t) {
      // chance that the sth success in iid Bernoulli trials is on the tth trial
      // EX = s/p; SD(X) = sqrt(s(1-p))/p
        if (s > t || s < 0) return(0.0);
        double prob = p*binomialPmf(t-1,s-1,p);
        return(prob);
    }

    public static double negBinomialCdf(double p, int s, int t) {
      // chance the sth success in iid Bernoulli trials is on or before the tth trial
        double prob = 0.0;
        for (int i = s; i <= t; i++) {
            prob += negBinomialPmf(p, s, i);
        }
        return(prob);
    }

    public static double normCdf(double y) {
      // normal distribution cumulative distribution function
         return(0.5*erfc(-y*0.7071067811865475));
    }

    public static double erfc(double x) {
         double xbreak = 0.46875;     // for normal cdf
// coefficients for |x| <= 0.46875
         double[] a = {3.16112374387056560e00, 1.13864154151050156e02,
                             3.77485237685302021e02, 3.20937758913846947e03,
                             1.85777706184603153e-1};
         double[] b = {2.36012909523441209e01, 2.44024637934444173e02,
                             1.28261652607737228e03, 2.84423683343917062e03};
// coefficients for 0.46875 <= |x| <= 4.0
         double[] c = {5.64188496988670089e-1, 8.88314979438837594e00,
                             6.61191906371416295e01, 2.98635138197400131e02,
                             8.81952221241769090e02, 1.71204761263407058e03,
                             2.05107837782607147e03, 1.23033935479799725e03,
                             2.15311535474403846e-8};
         double[] d = {1.57449261107098347e01, 1.17693950891312499e02,
                             5.37181101862009858e02, 1.62138957456669019e03,
                             3.29079923573345963e03, 4.36261909014324716e03,
                             3.43936767414372164e03, 1.23033935480374942e03};
// coefficients for |x| > 4.0
         double[] p = {3.05326634961232344e-1, 3.60344899949804439e-1,
                             1.25781726111229246e-1, 1.60837851487422766e-2,
                             6.58749161529837803e-4, 1.63153871373020978e-2};
         double[] q = {2.56852019228982242e00, 1.87295284992346047e00,
                             5.27905102951428412e-1, 6.05183413124413191e-2,
                             2.33520497626869185e-3};


         double y, z, xnum, xden, result, del;

/*
   Translation of a FORTRAN program by W. J. Cody,
   Argonne National Laboratory, NETLIB/SPECFUN, March 19, 1990.
   The main computation evaluates near-minimax approximations
   from "Rational Chebyshev approximations for the error function"
   by W. J. Cody, Math. Comp., 1969, PP. 631-638.
*/

//  evaluate  erf  for  |x| <= 0.46875
        if(Math.abs(x) <= xbreak) {
            y = Math.abs(x);
            z = y * y;
            xnum = a[4]*z;
            xden = z;
            for (int i = 0; i< 3; i++) {
                xnum = (xnum + a[i]) * z;
                xden = (xden + b[i]) * z;
            }
            result = 1.0 - x* (xnum + a[3])/ (xden + b[3]);
        } else if (Math.abs(x) <= 4.0) {
            y = Math.abs(x);
            xnum = c[8]*y;
            xden = y;
            for (int i = 0; i < 7; i++) {
                xnum = (xnum + c[i])* y;
                xden = (xden + d[i])* y;
            }
            result = (xnum + c[7])/(xden + d[7]);
            if (y > 0.0) {
                z = Math.floor(y*16)/16.0;
            } else {
                z = Math.ceil(y*16)/16.0;
            }
            del = (y-z)*(y+z);
            result = Math.exp(-z*z) * Math.exp(-del)* result;
        } else {
            y = Math.abs(x);
            z = 1.0 / (y*y);
            xnum = p[5]*z;
            xden = z;
            for (int i = 0; i < 4; i++) {
                xnum = (xnum + p[i])* z;
                xden = (xden + q[i])* z;
            }
            result = z * (xnum + p[4]) / (xden + q[4]);
            result = (1.0/Math.sqrt(Math.PI) -  result)/y;
            if (y > 0.0) {
                z = Math.floor(y*16)/16.0;
            } else {
                z = Math.ceil(y*16)/16.0;
            }
            del = (y-z)*(y+z);
            result = Math.exp(-z*z) * Math.exp(-del) * result;
        }

        if (x < -xbreak) {
            result = 2.0 - result;
        }
        return(result);
    }

    public static double normInv(double p) {
        if ( p == 0.0 ) {
            return(Double.NEGATIVE_INFINITY);
        } else if ( p >= 1.0 ) {
            return(Double.POSITIVE_INFINITY);
        } else {
            return(Math.sqrt(2.0) * erfinv(2*p - 1));
        }
    }

    public static double erfinv(double y) {
         double[] a = { 0.886226899,
                        -1.645349621,
                        0.914624893,
                        -0.140543331
                   };
         double[] b = {-2.118377725,
                        1.442710462,
                       -0.329097515,
                        0.012229801
                       };
         double[] c = {-1.970840454,
                       -1.624906493,
                        3.429567803,
                        1.641345311
                      };
         double[] d = { 3.543889200,
                        1.637067800
                      };
        double y0 = 0.7;
        double x = 0;
        double z = 0;
        if (Math.abs(y) <= y0) {
            z = y*y;
            x = y * (((a[3]*z+a[2])*z+a[1])*z+a[0])/
             ((((b[3]*z+b[2])*z+b[1])*z+b[0])*z+1.0);
        } else if (y > y0 && y < 1.0) {
            z = Math.sqrt(-Math.log((1-y)/2));
            x = (((c[3]*z+c[2])*z+c[1])*z+c[0]) / ((d[1]*z+d[0])*z+1);
        } else if (y < -y0 && y > -1) {
            z = Math.sqrt(-Math.log((1+y)/2));
            x = -(((c[3]*z+c[2])*z+c[1])*z+c[0])/ ((d[1]*z+d[0])*z+1);
        }

    /* Two steps of Newton-Raphson correction to full accuracy.
     Without these steps, erfinv(y) would be about 3 times
     faster to compute, but accurate to only about 6 digits.
    */

        x = x - (1.0 - erfc(x) - y) / (2/Math.sqrt(Math.PI) * Math.exp(-x*x));
        x = x - (1.0 - erfc(x) - y) / (2/Math.sqrt(Math.PI) * Math.exp(-x*x));

        return(x);
    } // ends erfinv


    public static double lnGamma(double x) {
/*  natural ln(gamma(x)) without computing gamma(x)
    P.B. Stark, stark@stat.berkeley.edu

      This Java program is based on a MATLAB program by C. Moler,
      in turn based on a FORTRAN program by W. J. Cody,
      Argonne National Laboratory, NETLIB/SPECFUN, June 16, 1988.

      References:

      1) W. J. Cody and K. E. Hillstrom, 'Chebyshev Approximations for
         the Natural Logarithm of the Gamma Function,' Math. Comp. 21,
         1967, pp. 198-203.

      2) K. E. Hillstrom, ANL/AMD Program ANLC366S, DGAMMA/DLGAMA, May,
         1969.

      3) Hart, Et. Al., Computer Approximations, Wiley and sons, New
         York, 1968.
*/

     double d1 = -5.772156649015328605195174e-1;
     double[] p1 = {4.945235359296727046734888e0, 2.018112620856775083915565e2,
           2.290838373831346393026739e3, 1.131967205903380828685045e4,
           2.855724635671635335736389e4, 3.848496228443793359990269e4,
           2.637748787624195437963534e4, 7.225813979700288197698961e3};
     double[] q1 = {6.748212550303777196073036e1, 1.113332393857199323513008e3,
           7.738757056935398733233834e3, 2.763987074403340708898585e4,
           5.499310206226157329794414e4, 6.161122180066002127833352e4,
           3.635127591501940507276287e4, 8.785536302431013170870835e3};
     double d2 = 4.227843350984671393993777e-1;
     double[] p2 = {4.974607845568932035012064e0, 5.424138599891070494101986e2,
           1.550693864978364947665077e4, 1.847932904445632425417223e5,
           1.088204769468828767498470e6, 3.338152967987029735917223e6,
           5.106661678927352456275255e6, 3.074109054850539556250927e6};
     double[] q2 = {1.830328399370592604055942e2, 7.765049321445005871323047e3,
           1.331903827966074194402448e5, 1.136705821321969608938755e6,
           5.267964117437946917577538e6, 1.346701454311101692290052e7,
           1.782736530353274213975932e7, 9.533095591844353613395747e6};
     double d4 = 1.791759469228055000094023e0;
     double[] p4 = {1.474502166059939948905062e4, 2.426813369486704502836312e6,
           1.214755574045093227939592e8, 2.663432449630976949898078e9,
           2.940378956634553899906876e10, 1.702665737765398868392998e11,
           4.926125793377430887588120e11, 5.606251856223951465078242e11};
     double[] q4 = {2.690530175870899333379843e3, 6.393885654300092398984238e5,
           4.135599930241388052042842e7, 1.120872109616147941376570e9,
           1.488613728678813811542398e10, 1.016803586272438228077304e11,
           3.417476345507377132798597e11, 4.463158187419713286462081e11};
     double[] c = {-1.910444077728e-03, 8.4171387781295e-04,
          -5.952379913043012e-04, 7.93650793500350248e-04,
          -2.777777777777681622553e-03, 8.333333333333333331554247e-02,
           5.7083835261e-03};

     double lng = Double.NaN;
     double mach = 1.e-12;
     double den = 1.0;
     double num = 0;
     double xm1, xm2, xm4;

   if (x < 0) {
       return(lng);
   } else if (x <= mach) {
       return(-Math.log(x));
   } else if (x <= 0.5) {
      for (int i = 0; i < 8; i++) {
            num = num * x + p1[i];
            den = den * x + q1[i];
      }
      lng = -Math.log(x) + (x * (d1 + x * (num/den)));
   } else if (x <= 0.6796875) {
      xm1 = x - 1.0;
      for (int i = 0; i < 8; i++) {
         num = num * xm1 + p2[i];
         den = den * xm1 + q2[i];
      }
      lng = -Math.log(x) + xm1 * (d2 + xm1*(num/den));
   } else if (x <= 1.5) {
      xm1 = x - 1.0;
      for (int i = 0; i < 8; i++) {
         num = num*xm1 + p1[i];
         den = den*xm1 + q1[i];
      }
      lng = xm1 * (d1 + xm1*(num/den));
   } else if (x <= 4.0) {
      xm2 = x - 2.0;
      for (int i = 0; i < 8; i++) {
         num = num*xm2 + p2[i];
         den = den*xm2 + q2[i];
      }
      lng = xm2 * (d2 + xm2 * (num/den));
   } else if (x <= 12) {
      xm4 = x - 4.0;
      den = -1.0;
      for (int i = 0; i < 8; i++)  {
         num = num * xm4 + p4[i];
         den = den * xm4 + q4[i];
      }
      lng = d4 + xm4 * (num/den);
   } else {
      double r = c[6];
      double xsq = x * x;
      for (int i = 0; i < 6; i++) {
         r = r / xsq + c[i];
      }
      r = r / x;
      double lnx = Math.log(x);
      double spi = 0.9189385332046727417803297;
      lng = r + spi - 0.5*lnx + x*(lnx-1);
    }
    return(lng);
    } // ends lnGamma


    public static double normPdf(double mu, double sigma, double x) {
         return(Math.exp(-(x-mu)*(x-mu)/(2*sigma*sigma))/
                (Math.sqrt(2*Math.PI)*sigma));
    } // ends normPdf

    public static double poissonPmf(double lambda, int k) {
        double p=0.0;
        if (k >= 0) {
            p = Math.exp(-lambda)*Math.pow(lambda,k)/factorial(k);
        }
        return(p);
    }

    public static double poissonCdf(double lambda, int k) {
        double p = 0.0;
        if (k < 0 ) {
        } else {
            if (k < 200) {
                for ( int m=1; m<=k; m++) {
                    p += Math.pow(lambda,m)/factorial(m);
                }
            } else {
                double logLam = Math.log(lambda);
                for ( int m=1; m<=k; m++) {
                    p += Math.exp( m*logLam - lnGamma(m+1) );
                }
            }
            p /= Math.exp(lambda);
        }
        return(p);
    }

    public static double poissonTail(double lambda, int k) {
        return(1.0-poissonCdf(lambda, k-1));
    }

    public static double quantile(double[] data, double q) {
        double[] dumDat = removeNaN(data);
        if (dumDat.length == 0) {
            return(Double.NaN);
        } else {
            sort(dumDat);
            return(dumDat[(int) Math.ceil(q*dumDat.length)-1]);
        }
    }

    public static double[] quartiles(double[] data)  {
        double[] qt = new double[5];
        double[] dumDat = removeNaN(data);
        if (dumDat.length == 0) {
            qt[0] = Double.NaN;
            qt[1] = Double.NaN;
            qt[2] = Double.NaN;
            qt[3] = Double.NaN;
            qt[4] = Double.NaN;
        } else {
            sort(dumDat);
            qt[0] = dumDat[0];
            qt[1] = dumDat[(int) Math.ceil(0.25*dumDat.length)-1];
            qt[2] = dumDat[(int) Math.ceil(0.5*dumDat.length)-1];
            qt[3] = dumDat[(int) Math.ceil(0.75*dumDat.length)-1];
            qt[4] = dumDat[dumDat.length-1];
        }
        return(qt);
    }

    public static double[] removeNaN(double[] data) {
        int n = data.length;
        double[] dumDat = new double[n];
        int k = 0;
        for (int i = 0; i < n; i++)  {
            if (!Double.isNaN(data[i]))  {
                dumDat[k] = data[i];
                k += 1;
            }
        }
        double[] cleanDat = new double[k];
        System.arraycopy(dumDat, 0, cleanDat, 0, k);
        return(cleanDat);
    }

    public static double[] removeZero(double[] data) {
        int n = data.length;
        double[] dumDat = new double[n];
        int k = 0;
        for (int i = 0; i < n; i++)  {
            if (data[i] != 0.0)  {
                dumDat[k] = data[i];
                k += 1;
            }
        }
        double[] cleanDat = new double[k];
        System.arraycopy(dumDat, 0, cleanDat, 0, k);
        return(cleanDat);
    }

    public static double[] removeZeroNaN(double[] data) {
        int n = data.length;
        double[] dumDat = new double[n];
        int k = 0;
        for (int i = 0; i < n; i++)  {
            if (!Double.isNaN(data[i]) && data[i] != 0.0)  {
                dumDat[k] = data[i];
                k += 1;
            }
        }
        double[] cleanDat = new double[k];
        System.arraycopy(dumDat, 0, cleanDat, 0, k);
        return(cleanDat);
    }

    public static double rNorm() {
    /*    double y = Math.sqrt(12) *
          ((Math.random() + Math.random() + Math.random() + Math.random()
           + Math.random() + Math.random() + Math.random() + Math.random()
           + Math.random() + Math.random() + Math.random() + Math.random())
            / 12.0 - 0.5);
    */
        double y = normInv(Math.random());
        return(y);
    } // ends rNorm()


    public static double sampleSd(double[] data)  {
        int n = data.length;
        if (n < 2) {
            return(Double.NaN);
        } else {
           double mu = Mean(data);
           double sHat = 0;
           for (int i=0; i< n; i++) {
               sHat += (data[i] - mu)*(data[i] - mu);
           }
           sHat = Math.sqrt(sHat/(n-1));
           return(sHat);
        }
    }

    public static double sampleSd(double[] data, int nToUse) {
        double[] d = new double[nToUse];
        System.arraycopy(data,0,d,0,nToUse);
        return(sampleSd(d));
    }

    public static double Sd(double[] data) {
        double sHat;
        if (data.length == 0) {
            sHat = Double.NaN;
        } else {
            double mu = Mean(data);
            sHat = 0.0;
            for (int i=0; i < data.length; i++) {
                sHat += (data[i] - mu)*(data[i] - mu);
            }
            sHat = Math.sqrt(sHat/data.length);
        }
        return(sHat);
    }

    public static double Sd(double[] data, int nToUse) {
        double[] d = new double[nToUse];
        System.arraycopy(data,0,d,0,nToUse);
        return(Sd(d));
    }

    public static double SdNaN(double[] data) {
        return(Sd(removeNaN(data)));
    }

    public static double Mean(double[] data) {
        double mu;
        if (data.length == 0) {
            mu = Double.NaN;
        } else {
            mu = 0.0;
            for (int i=0; i< data.length; i++) {
                mu += data[i];
            }
            mu /= data.length;
        }
        return(mu);
    }

    public static double Mean(double[] data, int nToUse)  {
        double[] d = new double[nToUse];
        System.arraycopy(data,0,d,0,nToUse);
        return(Mean(d));
    }

    public static double MeanNaN(double[] data) {
        return(Mean(removeNaN(data)));
    }



    public static double CorrCoef(double[] xVal, double[] yVal) {
        double r = 0.0;
        double mX = Mean(xVal);
        double mY = Mean(yVal);
        double sdX = Sd(xVal);
        double sdY = Sd(yVal);
        for (int i = 0; i < xVal.length; i++) {
            r += (xVal[i] - mX)*(yVal[i] - mY)/(sdX * sdY);
        }
        r /= xVal.length;
        return(r);
    }

    public static double CorrCoef(double[] xVal, double[] yVal, int nToUse) {
        double[] x = new double[nToUse];
        double[] y = new double[nToUse];
        System.arraycopy(xVal,0,x,0,nToUse);
        System.arraycopy(yVal,0,y,0,nToUse);
        return(CorrCoef(x,y));
    }

    public static double Rms(double[] v) {
        return(vTwoNorm(v)/Math.sqrt(v.length));
    }

    public static double sgn(double v) { // signum function; sgn(0) = 1;
        double s;
        if (v != 0.0) {
            s = v/Math.abs(v);
        } else {
            s = 1.0;
        }
        return(s);
    }

    public static double tCdf(double x, int df) {
// cdf of Student's t distribution with df degrees of freedom
        double ans = Double.NaN;
        if (df < 1) {
            ans = Double.NaN;
        } else if (x == 0.0) {
            ans = 0.5;
        } else if (df == 1) {
            ans = .5 + Math.atan(x)/Math.PI;
        } else if (x > 0) {
            ans = 1 - (incBeta(df/(df+x*x), df/2.0, 0.5))/2;
        } else if (x < 0) {
            ans = incBeta(df/(df+x*x), df/2.0, 0.5)/2;
        }
        return(ans);
    }

    public static double tCdf(double x, double df) {
        return(tCdf(x,(int) Math.floor(df)));
    }

    public static double tPdf(double x, double df) {
        double d = Math.floor(df);
        return(Math.exp(lnGamma((d+1)/2) - lnGamma(d/2))/
              ( Math.sqrt(d*Math.PI)*Math.pow((1+x*x/d),(d+1)/2)) );
    }

    public static double tPdf(double x, int df) {
        return(tPdf(x, (double) df));
    }

    public static double tInv(double p, int df) { // inverse Student-t distribution with
                                                  // df degrees of freedom
        double z;
        if (df < 0 || p < 0) {
            return(Double.NaN);
        } else if (p == 0) {
            return(Double.NEGATIVE_INFINITY);
        } else if (p == 1) {
            return(Double.POSITIVE_INFINITY);
        } else if (df == 1) {
            return(Math.tan(Math.PI*(p-0.5)));
        } else if ( p >= 0.5) {
            z = betaInv(2.0*(1-p),df/2.0,0.5);
            return(Math.sqrt(df/z - df));
        } else {
            z = betaInv(2.0*p,df/2.0,0.5);
            return(-Math.sqrt(df/z - df));
        }
    }

    public static double betaInv(double p, double a, double b) {
        if (p < 0 || p > 1 || a <= 0 || b <= 0) {
            return(Double.NaN);
        } else if ( p == 0 ) {
            return(Double.NEGATIVE_INFINITY);
        } else if ( p == 1) {
            return(Double.POSITIVE_INFINITY);
        } else {
            int maxIt = 100;
            int it = 0;
            double tol = Math.sqrt(eps);
            double work = 1.0;
            double next;
            double x;
            if (a == 0.0 ) {
                x = Math.sqrt(eps);
            } else if ( b == 0.0) {
                x = 1 - Math.sqrt(eps);
            } else {
                x = a/(a+b);
            }
            while (Math.abs(work) > tol*Math.abs(x) && Math.abs(work) > tol && it < maxIt) {
               it++;
               work = (betaCdf(x,a,b) - p)/betaPdf(x,a,b);
               next =  x - work;
               while (next < 0 || next > 1) {
                   work = work/2;
                   next = x - work;
               }
               x = next;
             }
             return(x);
         }
    }

    public static double[] vMinMax(double[] data) {
        double[] vmx = new double[2];
        if (data == null || data.length == 0) {
            vmx[0] = Double.POSITIVE_INFINITY;
            vmx[1] = Double.NEGATIVE_INFINITY;
        } else {
            vmx[0] = data[0];
            vmx[1] = data[0];
            for (int i = 1; i < data.length; i++) {
                if (vmx[0] > data[i]) vmx[0] = data[i];
                if (vmx[1] < data[i]) vmx[1] = data[i];
            }
        }
        return(vmx);
    }

    public static double[] vMinMax(double[] data, int nToUse)  {
        double[] d = new double[nToUse];
        System.arraycopy(data,0,d,0,nToUse);
        return(vMinMax(d));
    }

    public static double vMax(double[] data) {
        double mx;
        if (data == null || data.length == 0) {
            mx = Double.NEGATIVE_INFINITY;
        } else {
            mx = data[0];
            for (int i = 1; i < data.length; i++) {
                if (mx < data[i]) mx = data[i];
            }
        }
        return(mx);
    }

    public static double vMax(double[] data, int nToUse) {
        double[] d = new double[nToUse];
        System.arraycopy(data,0,d,0,nToUse);
        return(vMax(d));
    }

    public static double vMin(double[] data) {
        double mn;
        if (data == null || data.length == 0) {
            mn = Double.POSITIVE_INFINITY;
        } else {
            mn = data[0];
            for (int i = 1; i < data.length; i++) {
                if (mn > data[i]) mn = data[i];
            }
        }
        return(mn);
    }

    public static double vMin(double[] data, int nToUse) {
        double[] d = new double[nToUse];
        System.arraycopy(data,0,d,0,nToUse);
        return(vMin(d));
    }

    public static double[] scalVMult(double s, double[] v) {
        double[] ans = new double[v.length];
        for (int i=0; i < v.length; i++) {
            ans[i] = s*v[i];
        }
        return(ans);
    }

    public static void sort(double[] a) {
        int n = a.length;
        int incr=n/2;
        while ( incr >= 1 ) {
            for (int i = incr; i < n ; i++ ){
                double temp = a[i];
                int j=i ;
                while ( j >= incr && temp < a[j-incr]) {
                    a[j]=a[j-incr];
                    j -= incr;
                }
                a[j] = temp;
            }
            incr /= 2;
        }
    }

    public static void sort(String[] a) {
        int n = a.length;
        int incr=n/2;
        while ( incr >= 1 ) {
            for (int i = incr; i < n ; i++ ){
                String temp = a[i];
                int j=i ;
                while ( j >= incr && temp.compareTo(a[j-incr]) < 0) {
                    a[j]=a[j-incr];
                    j -= incr;
                }
                a[j] = temp;
            }
            incr /= 2;
        }
    }

    public static void sort(Date[] a) {
        int n = a.length;
        int incr=n/2;
        while ( incr >= 1 ) {
            for (int i = incr; i < n ; i++ ){
                Date temp = a[i];
                int j=i ;
                while ( j >= incr && temp.before(a[j-incr])) {
                    a[j]=a[j-incr];
                    j -= incr;
                }
                a[j] = temp;
            }
            incr /= 2;
        }
    }

    public static int[] sortIndex(Date[] a) {
        int n = a.length;
        int[] inx = new int[n];
        for (int i = 0; i < n; i++) { inx[i] = i; }
        int incr=n/2;
        while ( incr >= 1 ) {
            for (int i = incr; i < n ; i++ ){
                Date temp = a[i];
                int t = inx[i];
                int j = i;
                while ( j >= incr && temp.before(a[j-incr])) {
                    a[j]=a[j-incr];
                    inx[j] = inx[j-incr];
                    j -= incr;
                }
                a[j] = temp;
                inx[j] = t;
            }
            incr /= 2;
        }
        return(inx);
    }


    public static double normRand(double mu, double sigma ) {
        double a = rNorm();
        return (mu + sigma*a) ;
    }

    public static double trapZoid(
        double xLo, double yLo, double xHi, double yHi, double x) {
/* linearly interpolates between (xLo, yLo) and (xHi, yHi) and integrates
   the result up to the point x \in (xLo, xHi)
*/
      double m = (yHi - yLo)/(xHi - xLo);
      double area = yLo * (x - xLo);
      area += (x-xLo)*(x-xLo)*m/2;
      return area;
   }// ends trapZoid

   public static double vDot(double[] x, double[] y) {
// dot product of two vectors.  Truncates one if the other is shorter.
        double z = 0.0;
        int n = Math.min(x.length, y.length);
        for (int i = 0; i < n; i++) {
            z += x[i]*y[i];
        }
        return(z);
   }

   public static double vPNorm(double[] v, int p) { // p-norm of a vector
        return(vPNorm(v, (double) p));
   }

   public static double vPNorm(double[] v, double p) { // p-norm of a vector
        double ans = 0.0;
        for (int i=0; i < v.length; i++) {
            ans += Math.pow(v[i],p);
        }
        ans = Math.pow(ans, 1.0/p);
        return(ans);
   }

   public static double[] vCumSum(double[] x) {  // cumlative sum of vector
        double[] ans = new double[x.length];
        ans[0] = x[0];
        for (int i=1; i < x.length; i++) {
            ans[i] = ans[i-1] + x[i];
        }
        return(ans);
   }

   public static int[] vCumSum(int[] x) {  // cumlative sum of vector
        int[] ans = new int[x.length];
        ans[0] = x[0];
        for (int i=1; i < x.length; i++) {
            ans[i] = ans[i-1] + x[i];
        }
        return(ans);
   }

   public static double[] vSum(double[] x, double[] y) { // vector sum of two vectors.  Truncates to shorter input
        int n = Math.min(x.length, y.length);
        double[] z = new double[n];
        for (int i=0; i<n; i++) {
            z[i] = x[i] - y[i];
        }
        return(z);
   }

   public static double vTot(double[] x) { // total of the components of a vector
        double ans = 0;
        for (int i=0; i < x.length; i++) {
            ans += x[i];
        }
        return(ans);
   }

   public int vTot(int[] x) { // total of the components of a vector
        int ans = 0;
        for (int i=0; i < x.length; i++) {
            ans += x[i];
        }
        return(ans);
   }

   public static double vTwoNorm(double[] v) { // 2-norm of a vector
        double ans = 0.0;
        for (int i=0; i < v.length; i++)  {
            ans += v[i]*v[i];
        }
        ans = Math.sqrt(ans);
        return(ans);
   }

   public static double[] matVMult(double[][] matrix, double[] vector) { // matrix times vector
        int m = matrix.length;
        int n = vector.length;
        double[] result = new double[m];
        for (int i=0; i < m; i++) {
            result[i] = vDot(matrix[i], vector);
        }
        return(result);
   }




}// ends pbsStat
