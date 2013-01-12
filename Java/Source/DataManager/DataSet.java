/*
package DataManager;

Class DataSet.
@author Duncan Temple Lang and Philip B. Stark (stark AT stat.berkeley.edu)
@version hopeful
@copyright 1997, 1998, all rights reserved.
*/

package DataManager;

import java.util.*;
import java.io.*;
import java.net.*;

public class DataSet
{
    public char commentChar = '#';
    public char quote_char = '"';   //  recognize " as quote -
                                    //  could use ' or any other character
    public Hashtable Var = new Hashtable(10);
    public String name = "";
    public Hashtable Comments = new Hashtable(10);  // will hold comments from each line

    public DataSet()
    {
    }

    public DataSet(URL u)
    {
        this(u,false);
    }

    public DataSet(URL u, boolean load)
    {
        name = u.toString();
        if(load) {load(u);}
    }

    protected boolean load(URL u)
    {
        Vector varName = new Vector(10);
        Vector data = new Vector(10000);
        DataInputStream inp;
        try
        {
            inp = new DataInputStream(u.openStream());
        }
        catch (IOException e)
        {
            System.out.println(" exception in load " + e);
            return false;
        }

        StreamTokenizer st = new StreamTokenizer(inp);

        st.commentChar(commentChar);
        st.slashSlashComments(true);
        st.slashStarComments(true);
        st.quoteChar(quote_char);
        st.eolIsSignificant(false);
        st.parseNumbers();
        while (true)
        {
            int val;
            try
            {
               val = st.nextToken();
            }
            catch (IOException e)
            {
                System.out.println(" no Token in load " + e);
                return false;
            }

// check for a quote or word, or for NaN
            if (st.ttype == StreamTokenizer.TT_WORD )
            {
                if ( st.sval.equals("NaN"))
                {
                    data.addElement(new Double(Double.NaN));
                }
                else
                {
                    varName.addElement(st.sval);
                }
            }
            else if(val == quote_char )
            {
                varName.addElement(st.sval);
            }
            else if ( st.ttype == StreamTokenizer.TT_EOF )
            {
                break;
            }
            else  // we must have a number
            {
                data.addElement(new Double(st.nval));
            }

        }

        double[][] val = new double[varName.size()][data.size()/varName.size()];
        int ctr = 0;
        for ( int i = 0; i < val[0].length; i++ )
        {
            for ( int j = 0; j < val.length; j++, ctr++ )
            {
                val[j][i] = ( (Double) data.elementAt(ctr)).doubleValue();
            }
        }

        ctr = 0;
        String name;
        for (Enumeration e = varName.elements(); e.hasMoreElements(); )
        {
            name = (String) e.nextElement();
            Var.put(name, new Variable(name, val[ctr++]));
        }

        return true;
    }

   public String shortDisplay()
   {
        String str = "";
        for(Enumeration e = Var.elements();e.hasMoreElements();)
        {
            str += " " + e.nextElement().toString();
        }
        return(str);
   }

   public String toString()
   {
        String str = shortDisplay();
        return(str);
   }

    public Variable getVariable(String s)
    {
        Variable v = (Variable) Var.get(s);
        return v;
    }

    public void setCommentChar(char s)
    {
        this.commentChar = s;
    }

    public char getCommentChar()
    {
        return(commentChar);
    }

} // ends class DataSet.

