/**
package PbsGui;

public abstract class Format extends Object

Some functions to format numbers as Strings, convert Strings to
numbers, and make axes.

@author P.B. Stark stark@stat.berkeley.edu
@version 1.1 28 August 2000.
@copyright 1997, 1998, 1999, 2000, P.B. Stark.  All rights reserved.


*/
package PbsGui;
import java.util.*;
import java.net.*;
import java.lang.*;

public abstract class Format extends Object {
    public static final int maxDigits =
        (int) Math.floor(Math.log(Double.MAX_VALUE)/Math.log(10));

    public static final int maxDecimals =
        (int) Math.floor(Math.log(Integer.MAX_VALUE)/Math.log(10));


    public static int strToInt(String s) {
        return(strToInt(s, 0));
    }

    public static int strToInt(String s, int def) {
       int v = def;
       try {
           v = Integer.parseInt(s.trim());
       } catch (NumberFormatException e) {
           System.out.println("Number format exception in PbsGui.Format.strToInt: " + s);
       } catch (NullPointerException f) {
       }
       return v;
    }

    public static int getMaxDigits() {
        return(maxDigits);
    }

    public static double strToDouble(String s) {
        return(strToDouble(s, 0.0));
    }

    public static double strToDouble(String s, double def) {
        double v = def;
        if (s != null) {
            if (s.trim().equals("NaN")) {
                v = Double.NaN;
            } else if (s.trim().toLowerCase().equals("inf")) {
                v = Double.POSITIVE_INFINITY;
            } else if (s.trim().toLowerCase().equals("-inf")) {
                v = Double.NEGATIVE_INFINITY;
            } else if (s.indexOf('%') > -1) {
                try {
                    v = Double.valueOf(s.substring(0,s.indexOf('%'))).doubleValue()/100;
                } catch (NumberFormatException e) {
                    System.out.println("number format exception in PbsGui.Format.strToDouble: "
                            + s);
                }
            } else {
                try {
                    v = Double.valueOf(s.trim()).doubleValue();
                } catch (NumberFormatException e) {
                    System.out.println("number format exception in PbsGui.Format.strToDouble: "
                        + s);
                }
            }
        }
        return(v);
   }

   public static String numToString(double v, int places) {
        return(numToString(v, places, places, true, true));
   }

   public static String numToString(double v, int digits, int decimals, boolean trim) {
        return(numToString(v,digits,decimals,trim,true));
   }

   public static String numToString(double v, int digits, int decimals, boolean trim,
        boolean eNotation)
   { // format doubles to Strings w/ width at most digits and at most decimals
     // places after the decimal point. If trim, trim trailing zeros
        digits = Math.min(digits, maxDigits);
        decimals = Math.min(decimals, maxDecimals);
        if (Double.isNaN(v)) {
            return("NaN");
        } else if (Double.isInfinite(v)) {
            return("Inf");
        } else if (v == 0.0) {
            return("0");
        } else {
            String mantissa = "", exponent = "", lbl = (v + "");
            int decInx, mantSpace, eInx, exp = 0, expSpace=0;
            double mant;
            int nChars = lbl.length();          // "natural" characters in number
// look for exponential notation
            eInx = Math.max(lbl.toLowerCase().indexOf("e"), lbl.toLowerCase().indexOf("d"));
            if (eInx > -1) {                    // there is an exponent
                expSpace = lbl.length() - eInx;                         // space the exponent wants
                exponent = lbl.substring(eInx + 1, lbl.length());       // exponent as string
                exp = strToInt(exponent);                               // exponent as int
                if (exp != 0) {                                         // use e-notation
                    exponent = "e" + exponent;
                } else {
                    expSpace = 0;
                }
                mantissa = lbl.substring(0, eInx);                      // mantissa as string
                if (!eNotation) {                                       // user doesn't want e
                // try to format number as real; otherwise, return "overflow"
                    if (v >= Math.pow(10,digits+1) || v <= - Math.pow(10,digits)) {
                        return("OVFL");
                    } else if (Math.abs(v) <= Math.pow(10,-digits+1)) {
                        return("0");
                    }
                }
            }
            else mantissa = lbl;                                    // no exponent
            decInx = mantissa.indexOf('.');                         // where's the '.'?
            mantSpace = digits - expSpace;                          // space for mantissa
            if (decInx < 0) {
                decInx = mantissa.length();                         // mantissa gets all
            } else {                                                // leave space for '.'
                mantSpace++; digits++;
            }
            mant = Double.valueOf(mantissa).doubleValue();          // mantissa as double
            if (v < 0.0) {                                          // leave space for -
                mantSpace++; digits++;
            }
 /* the number of places available for the mantissa digits is digits - expSpace, not counting
    punctuation.
    The power of 10 that will be in the least significant place is
    - (mantSpace - #digits before the decimal) = decInx - mantSpace + 1.
 */
            int shiftDecimal;
            if (Math.abs(mant) <= 2*Double.MIN_VALUE) {
                shiftDecimal = 0;
            }
            else {
                shiftDecimal = (int) Math.floor(Math.log(Math.abs(mant))/Math.log(10));
            }
            double x = roundTo(mant,Math.pow(10,-(mantSpace - shiftDecimal)+1));
            String sValue = "" + x;
            if (sValue.indexOf('.') > -1) {
            // make sure that the precision is within the user's spec.
                x = roundTo(x,Math.pow(10,-decimals));
                sValue = ("" + x);
                decInx = sValue.indexOf('.');
                if (decInx > -1) {
                    sValue = sValue.substring(0,Math.min(sValue.length(),decInx+decimals+1));
                }
                if (trim) {
                    sValue = removeTrailingZeros(sValue);
                }
            }
            digits = Math.max(digits, expSpace + 1);
            sValue = removeTrailingZeros(sValue);
            sValue = sValue.substring(0,
                    Math.min(sValue.length(),digits-expSpace+1))+exponent;
            return(sValue);
        }
   }

   public static String numToString(double v, int digits, boolean trim) {
   // format doubles to Strings w/ at most digits places. If trim, trim trailing zeros
        return(numToString(v, digits, digits, trim, true));
   }

   public static String doubleToPct(double v) {
   // format a double in [0,1] to a string percent
        return(doubleToPct(v,4,1,true,true));
   } // ends doubleToPct(double)

   public static String doubleToPct(double v, int dig) {
        return(doubleToPct(v,dig,1,true,true));
   }

   public static String doubleToPct(double v, int dig, int dec) {
        return(doubleToPct(v,dig,dec,true,true));
   }

   public static String doubleToPct(double v, int dig, int dec, boolean eNotation) {
        return(doubleToPct(v,dig,dec,true,eNotation));
   }

   public static String doubleToPct(double v, int dig, boolean eNotation) {
        return(doubleToPct(v,dig,1,true,eNotation));
   }

   public static String doubleToPct(double v, int dig, int dec,
                                    boolean trim, boolean eNotation) {
     // format a double in [0,1] to a string percentage, dig is the number of digits
        if (Double.isNaN(v)) {
            return("NaN");
        } else {
            return(numToString(v*100,dig,dec,trim,eNotation)+"%");
            /*
            int dTrunc = dig;
            if (v < .01) {dTrunc++;}
            double val = roundTo(v, Math.pow(10, -(dTrunc-1)));
            String s = "" + 100*val;
            String a = s.substring(0,(int) Math.min(s.length(),dig));
            a = a + "%";
            return(a); */
        }
   } // ends doubleToPct(double, int)

   public static String doubleToStr(double xIn, int p) {
   // format a double that has at least one leading digit, no exponent
   // rounds to p digits after the decimal point
       if (Double.isNaN(xIn)) {
           return("NaN");
       } else {
           double x = roundTo(xIn,Math.pow(10,-p));
           int dig = (int) (Math.floor( Math.log( Math.abs(x))
                                 /Math.log(10)));
           if (dig < 0) {
               dig = 1;
           }
           String lbl = ("" + x);
           int nChars = lbl.length();
           if (x > 0) {
               lbl = lbl.substring(0, Math.min(dig+p+1,nChars));
           } else {
               lbl = lbl.substring(0, Math.min(dig+p+2, nChars));
           }
           if (lbl.indexOf('.') > -1) {
               lbl = removeTrailingZeros(lbl);
               if (lbl.charAt(lbl.length() - 1) == '.') {
                   lbl = lbl.substring(0, lbl.length()-1);
               }
           }
           return(lbl);
       }
   } // ends doubleToStr( double, int )

   public static boolean strToBoolean(String s, boolean def)
   { // defaults to the value def if s is not the string representation of a boolean
        boolean b = false;
        try {
            b = new Boolean(s.trim()).booleanValue();
        } catch (Exception e) {
            b = def;
        }
        return(b);
   } // ends strToBoolean(String, boolean)

   public static boolean strToBoolean(String s) {
        return(strToBoolean(s, false));
   }

   public static double roundTo(double val, double off) { // rounds to nearest off.
        double mult;
        double rat = val/off;
        double sgn = 1;
        if (rat < 0) sgn = -1;
        rat = Math.abs(rat);
        if (rat < Integer.MAX_VALUE) {
            return(sgn*Math.round(rat) * off);
        }
        else {
            mult = rat/Integer.MAX_VALUE;
            if (mult < (double) Integer.MAX_VALUE) {
                mult = Math.floor(mult);
                rat = rat - mult*Integer.MAX_VALUE;
                return(sgn*(mult*Integer.MAX_VALUE + Math.round(rat))*off);
            }
            else {
                System.out.println("Integer overflow in PbsGui.Format.roundTo");
                return(Double.NaN);
            }
        }
   }

   public static double[] niceAxis(double xMin, double xMax) {
        double[] xIn = new double[2];
        xIn[0] = xMin;
        xIn[1] = xMax;
        return(niceAxis(xIn));
   }

   public static double[] niceAxis(double[] xIn) {
/* adapted from FORTRAN nicer subroutine by R.L. Parker.
   scales intervals and calculates a round tick-mark interval
   Between 7 and 15 ticks are made

   Input array:
   xin[0],xin[1]  extremes of variable  x  in its own units.

   Output array:
   niceAxis[0]: xin[0] rounded to a nice number
   niceAxis[1]: xin[1] rounded to a nice number
   niceAxis[2]: interval between tick marks---always a nice number

   The interval [niceAxis[0], niceAxis[1]] always includes [xIn[0], xIn[1]]
*/
         double[] divs = { 0.1, 0.2, 0.5, 1.0 };
         double plus, units, bias;
         int index;
         double[] xOut = new double[3];

         xOut[0] = xIn[0];
         xOut[1] = xIn[1];
         if (xOut[1] == xOut[0]) xOut[1] += Math.abs(xOut[1]) + 1.0;
         plus = 1000.0 + Math.log(xOut[1]-xOut[0])/Math.log(10);
         index = (int) Math.floor(1.4969 + 2.857*(plus%1)) - 1;
         units = divs[index]*Math.pow(10, ((int) plus - 1000));
         bias = units * Math.ceil(Math.max(Math.abs(xOut[0]), Math.abs(xOut[1]))/units);
         xOut[0]=xOut[0] - (xOut[0]+bias)%units;
         xOut[1]=xOut[1] + (bias - xOut[1])%units;
         if (Math.abs(xOut[0]/units) <= 0.01) xOut[0]=0.0;
         if (Math.abs(xOut[1]/units) <= 0.01) xOut[1]=0.0;
         xOut[2] = units;
         return(xOut);
    }

    public static String removeTrailingZeros(String s) {
         if (s.indexOf('.') > -1) {
            while (s.charAt(s.length()-1) == '0') {s = s.substring(0,s.length()-1);}
         }
         if (s.indexOf('.') == s.length()-1) {s = s.substring(0,s.length()-1);}
         return(s);
    }

    public static URL stringToURL(String s, URL codeBase) {
    // make a URL from s: check whether complete URL; if not, add codebase
         URL u = null;
         if ( s.indexOf(":") == -1) { // not a complete URL
               try {
                   u = new URL(codeBase, s);
               } catch (MalformedURLException e) {
                   System.out.println(" Malformed URL " + s);
               }
          } else { // assume complete URL if the filename contains a colon
               try {
                   u = new URL(s);
               } catch (MalformedURLException e) {
                   System.out.println(" Malformed URL " + s);
               }
          }
          return(u);
    }

    public static String crypt(String s, String t) {
        int slen = s.length();
        if (slen == 0) {
            return(null);
        } else {
            int tlen = t.length();
            int rad = 16;
            int chl = 2;
            int j = -1;
            int oneChar;
            String result = "";
            String tmp;
            if (slen >= 2 && s.substring(0,2).equals("0x")) {
                for (int i=2; i < slen; i+=chl) {
                    if (++j >= tlen) {
                        j = 0;
                    }
                    tmp = s.substring(i,i+chl).trim();
                    try {
                        oneChar = Integer.parseInt(tmp, rad);
                        result += (char) (oneChar ^ t.charAt(j));
                    } catch (NumberFormatException e) {
                        System.out.println("Error " + e + " in Format.crypt decoder: unparsable integer " + tmp + "!\n"
                            + " Input string: " + s );
                    }
                }
            } else {
                result = "0x";
                for (int i=0; i < slen; i++) {
                   if (++j >= tlen) {
                      j = 0;
                   }
                   tmp = "" + Integer.toHexString(((int) s.charAt(i)) ^ ((int) t.charAt(j)));
                   while (tmp.length() < chl) {
                      tmp = " " + tmp;
                   }
                   result += tmp;
                }
            }
            return(result);
        }
    } // ends crypt
}