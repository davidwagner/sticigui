/*
package DataManager;

Class Variable.
@author Duncan Temple Lang and Philip B. Stark
@version hopeful
@copyright 1997, all rights reserved.
*/

package DataManager;

public class Variable
{
    public String name;
    public double[] val;

    Variable()
    {

    }

    Variable(String n, double[] v)
    {
        name = n;
        val = v;
    }

    public int length()
    {
        return val.length;
    }

    public String name()
    {
        return name;
    }

    public double[] data()
    {
        return val;
    }

    public double data(int i)
    {
        return val[i];
    }

   public String toString() {
    String str = name + " " + length();
    return(str);
   }

}
