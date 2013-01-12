/**
package PbsGui;

public class ProbabilityValidator extends Object implements Validator

a validator for double input that must be between 0 and 1.

@author P.B. Stark stark@stat.berkeley.edu
@version 1.0

Last modified 11 October 1997

*/

package PbsGui;

public class ProbabilityValidator extends Object implements Validator
{
    public boolean isValid(String s)
    {
        double val = 0.0;
        if (s.indexOf('%') > -1)
        {
            try
            {
                val = Double.valueOf(s.substring(0,s.indexOf('%'))).doubleValue()/100;
            }
            catch (NumberFormatException e)
            {
                return(false);
            }
        }
        else
        {
            try
            {
                val = Double.valueOf(s).doubleValue();
            }
            catch (NumberFormatException e)
            {
                return(false);
            }
        }
        if (val >= 0.0 && val <= 1.0)
        {
            return(true);
        }
        return(false);
    }

    public double makeValid(String s)
    {
        double val = 0.0;
        if (s.indexOf('%') > -1)
        {
            try
            {
                val = Double.valueOf(s.substring(0,s.indexOf('%'))).doubleValue()/100;
            }
            catch (NumberFormatException e) {}
        }
        else
        {
            val = Double.valueOf(s).doubleValue();
        }
        if (val > 1.0)
        {
            val = 1.0;
        }
        else if (val < 0.0)
        {
            val = 0.0;
        }
        return(val);
    }

    public double makeValid(Double d)
    {
        return(makeValid(d.toString()));
    }

    public double makeValid(double d)
    {
        return(makeValid(d + ""));
    }
}