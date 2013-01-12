/**
package PbsGui;

public class PositiveDoubleValidator extends Object implements Validator

a validator for double input that must be >= 0.

@author P.B. Stark stark@stat.berkeley.edu
@version 1.0

Last modified 11 October 1997

*/

package PbsGui;

public class PositiveDoubleValidator extends Object implements Validator
{
    public boolean isValid(String s)
    {
        double val;
        try {
            val = Double.valueOf(s).doubleValue();
        } catch (NumberFormatException e) {
            return(false);
        }
        if (val >= 0.0 ) {
            return(true);
        } else {
            return(false);
        }
    }

    public double makeValid(String s)
    {
        double val = Double.valueOf(s).doubleValue();
        if (val < 0.0)
        {
            val = 0.0;
        }
        return(val);
    }

    public double makeValid(Double d)
    {
        double val = d.doubleValue();
        if (val < 0.0)
        {
            val = 0.0;
        }
        return(val);
    }

    public double makeValid(double d)
    {
        double val = d;
        if (val < 0.0)
        {
            val = 0.0;
        }
        return(val);
    }
}