/*
package DataManager;

Class RunnableDataSet. It's a dataset.  It's a thread.  It's BOTH!
@author Duncan Temple Lang and Philip B. Stark
@version hopeful
@copyright 1997, all rights reserved.
*/

package DataManager;

import java.net.*;

public class RunnableDataSet extends DataSet implements ManagerRunnable
{
    public URL u;
    protected DataManager data_manager;

    public RunnableDataSet()
    {
    }

    public RunnableDataSet(String name)
    {
      try {
         u = new URL(name);
      } catch(MalformedURLException e) {
         System.out.println("[RunnableDataSet(String)] " + e);
      }
    }

    public RunnableDataSet(URL u, DataManager d) {
      this(u);
      manager(d);
    }

    public RunnableDataSet(URL u)
    { super(u);
        this.u = u;
    }

    public DataManager manager(DataManager manager) {
      data_manager = manager;
      return manager();
    }


    public void run()
    {
        load(u);
        finish();
    }


  /* The following do not have to be implemented in a ManagerRunnable class */

    public void finish()
    {
        data_manager.insertDataSet((Object) this, u);
    }

    public DataManager manager() {
      return data_manager;
    }

    public String toString() {
       return u.toString();
    }

} // ends class RunnableDataSet


