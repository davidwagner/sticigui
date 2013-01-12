/*
package DataManager;
Class DataManager. A multithreaded data server.
@author Duncan Temple Lang and Philip B. Stark
@version 0.9
@copyright 1997, 1998, all rights reserved.

This class has been generalized and extended in a simple
way to provide considerably more utility.
The basic idea is simple. An applet/application has a
collection of data structures (numerical data sets, images, documents, etc.)
that are typically loaded when needed. If the task of obtaining such an object
takes longer than even a few fractions of a second, there can be a noticeable
lack of responsiveness in the GUI.

The data manager can handle inhomogeous types of data in its cache.
The only thing that connects the classes of objects is that they must
implement ManagerRunnable.

*/

package DataManager;
import java.util.*;
import java.io.*;
import java.net.*;


public class DataManager extends Thread
{

    public static final int ADDED_OBJECT = 1;
    public static final int REMOVED_OBJECT = 2;

    protected boolean kill_self = false; // signals this thread that it should die
    public static final int DefaultPriority = Thread.MIN_PRIORITY;
    public int default_priority = Thread.MIN_PRIORITY;
    public static final int DEFAULT_PRIORITY = Thread.MIN_PRIORITY;
    protected final int startFiles = 5;
    protected Hashtable DataHash = new Hashtable(startFiles);
    protected Vector pending = new Vector(startFiles);

    protected Notifiable notify_object = null;

    protected Threadtable threads = new Threadtable("Data Manager Threads");
    private URL[] urls = null;
    private Object[] objects = null;

    public DataManager()
    {
        this(null, null, true, DefaultPriority);
    }

    public DataManager(Object[] url)
    {
        this(url, null, true, DefaultPriority);
    }

    public DataManager(Object[] url, Notifiable notify_please, int priority)
    {
        this(url, notify_please, true, priority);
    }
    public DataManager(Object[] url, Notifiable notify_please)
    {
        this(url, notify_please, DefaultPriority);
    }

    public DataManager(Object[] url, Notifiable notify_please, boolean start,
                        int priority)
    {
       default_priority = priority;
       notify_object = notify_please;
       setPriority(Thread.MAX_PRIORITY);
       objects = url;
       if(start == true)
       {
            try {start();}
            catch(Exception e) {}
       }
    }

    public void start()
    {
       loadObjects(objects);
       objects = null;
       super.start();  // so as to make sure we return!
                       // If we call run directly, this never returns.
    }


    /** Loads a list of objects <u>asynchronously</u>
      */
    public int loadObjects(Object[] objects)
    {
       int ctr = 0;
       if(objects != null)
       {
           for (int i = 0; i < objects.length; i++)
           {
               if(addDataSet(objects[i], default_priority) != null) ctr++;
           }
       }
       return(ctr);
    }

     /** Spin idly to keep this thread alive. Don't want to burn cycles so wait
         on a variable that will never be notified. We can use this to
         signal something.
       */

    Boolean dummy_object = new Boolean(false);

    public void run()
    {
         while(true)
         {
           synchronized(dummy_object)
           {
                try{ dummy_object.wait();} catch(Exception e) {}
                if(dummy_object.booleanValue())
                {
                    updateThreads();
                }
                if(kill_self)
                break;
           }
         }
    }


      /* Update the list of pending threads/objects by removing the specified
          one if it is in this table of pending objects
        */
    public int updateThreads(Object obj, boolean stop)
    {
        Thread tmp = null;
        int n = -1;
        synchronized(threads)
        {
            if(threads.containsKey(obj.toString()))
            {
                if(stop) tmp = (Thread)threads.get(obj.toString());
                threads.remove(obj.toString());
            }
            n = threads.size();
        }
         /* Now make sure the thread has stopped or note that it is not pending
            so committment to the hash table of data objects should not occur.
            If we try to stop this, no notification occurs for the waiting
            object.
          */
        if(stop)
        {
            if(tmp != null && tmp.isAlive())
            {
                tmp.stop();
            }
        }
        return(n);
    }

       /**
           Go through the elements in the Threadtable and
           check which have died and which are still alive.
           Remove the ones that are dead.
         */
    public int updateThreads()
    {
          synchronized (threads)
          {
             Thread tmp;
             String name;
             for(Enumeration e = threads.keys(); e.hasMoreElements(); )
             {
                name = (String)e.nextElement();
                if((tmp = (Thread)threads.get(name)) == null)
                {
                    threads.remove(name);
                    continue;
                }
                if(!tmp.isAlive())
                {
                    threads.remove(name);
                    continue;
                }
             }
             return(threads.size());
          }
    }

     /**
         Add an existing data set to the cache list.
         If one exists, remove it.
       */
    public Thread addDataSet(Object ds, Object name)
    {
        synchronized(DataHash)
        {
            DataHash.put(name.toString(), ds);
        }
        return((Thread)null);
    }

     /** Add an object to the manager asynchronously by specifying
         the obejct
       */
    public Thread addDataSet(Object u)
    {
        return addDataSet(u, default_priority);
    }

     /** Add an object to the manager asynchronously by specifying
         the object and a priority for the asynchronous thread that fetches
         the object.
       */
    public Thread addDataSet(Object u, int priority)
    {
        ManagerRunnable tmp;
        try
        {
            tmp = (ManagerRunnable) u;
        }
        catch(Exception e)
        {
            System.out.println("Error for " + u + "\n"+e);
            return null;
        }
        if(tmp == null)
        {
            return(null);
        }
        tmp.manager(this);
        Thread t = new Thread(tmp);
        t.setPriority(priority);
        t.start();
        addPending(u, t);
        return(t);
    }

/** Put the thread into our thread table */
    public boolean addPending(Object u, Object t)
    {
         synchronized(threads)
         {
            threads.put(u.toString(),t);
         }
         return(true);
    }

/** Returns true if the object is currently managed
         in the data table -  pending  doesn't count so false is true.
*/
    public boolean manages(Object s)
    {
        synchronized(DataHash)
        {
            return DataHash.containsKey(s.toString());
        }
    }

    public boolean isPending(Object s)
    {
        synchronized(threads)
        {
            return threads.containsKey(s);
        }
    }

    public Object getDataSet(Object s)
    {
        return getDataSet(s.toString(), default_priority);
    }

    public Object getDataSet(Object obj, int priority)
    {
        if (obj == null) return(null);
        String s = obj.toString();
        synchronized(DataHash)
        {
            if (DataHash.containsKey(s))
            {
                return  DataHash.get(s);
            }
        }

        if (isPending(s))
        {
            synchronized(threads)
            {
                Thread tmp = (Thread)threads.get(s);
                if(tmp != null)
                {
                    tmp.setPriority(priority);
                }
            }
            while (true)
            {
                synchronized(DataHash)
                {
                    try
                    {
                       DataHash.wait();
                    }
                    catch (Exception e)
                    {
                        System.out.println (" Exception " + e +
                                            " in getDataSet ");
                    }
                    if (DataHash.containsKey(s))
                    {
                        return  DataHash.get(s);
                    }
                }
            }
        }

        Thread t = null;
        t = addDataSet(obj, priority);
        if(t == null) return null;
        try
        {
            t.join();
            updateThreads(s, false);
        }
        catch (InterruptedException e)
        {
            System.out.println(" bombed in getDataSet ");
        }
        synchronized(DataHash)
        {
            return  DataHash.get(s);
        }

    }

    public boolean removeDataSet(Object obj)
    {
        boolean hadIt = false;
        String s = obj.toString();

        if(isPending(s))
        {
           /* Chance for some other thread to get in here,
              so we have to do the check again in updateThreads.
            */
             updateThreads(s,true);
        }

        synchronized(DataHash)
        {
            hadIt = DataHash.containsKey(s);
            if (hadIt)
            {
                DataHash.remove(s);
            }
        }

        return hadIt;
    }



    public int size()
    {
        synchronized(DataHash)
        {
            return DataHash.size();
        }
    }

    public String[] list()
    {
        String []names = null;
        synchronized(DataHash)
        {
            names = new String[DataHash.size()];
            int ctr = 0;
            for(Enumeration e=DataHash.keys(); e.hasMoreElements();)
            {
                names[ctr++] = (String) e.nextElement();
            }
        }
        return(names);
    }

    public boolean insertDataSet(Object d)
    {
        return insertDataSet(d, (Object) (d.toString()));
    }

    public boolean insertDataSet(Object d, Object u)
    {
        String name = u.toString();
        synchronized(DataHash)
        {
            DataHash.put(name, d);
            try
            {
                DataHash.notifyAll();
            }
            catch (IllegalMonitorStateException e)
            {
                System.out.println(e + " in InsertData for " + name);
            }
        }
        updateThreads(name, false);

        /* Notify any object waiting on this */
        if(notify_object != null)
        {
            try
            {
                notify_object.Notify(name, ADDED_OBJECT);
            }
            catch(IllegalMonitorStateException e) {}
        }
        return true;
    }



    public String statusMessage()
    {
        String str = "";
        synchronized(DataHash)
        {
            synchronized(threads)
            {
                str += "# pending: " + threads.size();
            }
            str += "  # cached: " + DataHash.size();
            for (Enumeration e = threads.keys(); e.hasMoreElements();)
            {
                str += " " + (String) e.nextElement();
            }
        }
        return(str);
   }
} // ends Class DataManager


