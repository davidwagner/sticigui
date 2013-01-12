package DataManager;

import java.applet.*;
import java.awt.*;
import java.net.*;
import java.util.*;


public class inline extends Applet implements Notifiable {
   String BASE = "/accounts/grad/duncan/Java/DataManager";
   int num_frames = 0;
   boolean use_cursor = false;
   static Color []colors = {Color.blue, Color.red, Color.yellow, Color.green};
   int number_of_zoom_intervals = 9;

   int num_series = 3;
   DataManager dmgr = null;

   public inline() {
     super();
   }

   static public void main(String argv[]) {
     inline applet = new inline();
     applet.init();
   }

   URL []urls;
   Object []data_sets;
   int waiting = 0;
   AppFrame frame = null;

   public void init() {
     int num_sets = 5;
     urls = new URL[num_sets];
     data_sets = new Object[2*num_sets];

     for(int i=0;i<urls.length;i++) {
      try {
         urls[i] = new URL("file://" + BASE + "/Data/data"+i);
         data_sets[i] = (Object) new RunnableDataSet(urls[i]);
       } catch(Exception e) {
      }
     }

     for(int i=0;i<urls.length;i++) {
      try {
         urls[i] = new URL("file://" + BASE + "/Data/doc"+i+".html");
         data_sets[urls.length + i] = (Object) new RunnableDocument(urls[i]);
       } catch(Exception e) {
      }
     }

     waiting = 2*urls.length;


     boolean auto_start = false;

     dmgr =  new DataManager(data_sets,
                               (Notifiable)this, auto_start,Thread.MIN_PRIORITY);
     if(!auto_start) {
       dmgr.start();
     }

     frame = new AppFrame("Data Manager Display", this, urls.length);
     start();  // start this applet
   }


   public void start() {
            /* Blocks until data set is returned or error */
    DataSet set =  (DataSet)getDataSet("file://" + BASE + "/Data/dataset4", Thread.MIN_PRIORITY);
    System.out.println(set.shortDisplay());
     set = (DataSet)getDataSet("file://" + BASE + "/Data/data3", Thread.MIN_PRIORITY);
    System.out.println(set.shortDisplay());

          /* This is in the same thread so will be done synchronously.
              Want to test the two threads - one adding and one removing simultaneously
           */
      removeDataSet("file:" + BASE + "/Data/dataset4");
  }

  public boolean removeDataSet(Object obj) {
    String name = obj.toString();
    synchronized(Waiting) {
     if(dmgr.isPending(name)) {
      waiting--;
     }
    }
    dmgr.removeDataSet(new RunnableDataSet(name));
    return true;
  }

     /* Get data set from DataManager with a given priority.
        Note this returns Object since we are now handling inhomogeous
        classes in the DataManager.
      */
  public Object getDataSet(Object obj, int priority) {
    synchronized(Waiting) {
      waiting++;
    }
    try {
    DataSet set =  (DataSet)dmgr.getDataSet(new RunnableDataSet(obj.toString()), priority);
    return set;
    } catch(Exception e) {
       Document doc =  (Document)dmgr.getDataSet(new RunnableDocument(obj.toString()), priority);
    return doc;
    }
  }


   Integer  Waiting = new Integer(20);

   public void addDataSet(String name, boolean use_cursor) {
    if(frame != null) {
     if(frame.list != null)
      frame.list.addItem(name);

     if(frame.status != null)  {
       frame.status.Notify(name, -1);
     }

    synchronized(Waiting) {
     waiting--;

     if(waiting == 0) {
       if(use_cursor)
         frame.setCursor(Frame.DEFAULT_CURSOR);
      if(frame.waiting != null) {
        frame.waiting.setLabel("Done");
      }
     } else {
       if(use_cursor)
         frame.setCursor(Frame.WAIT_CURSOR);
      if(frame.waiting != null)
        frame.waiting.setLabel(Integer.toString(waiting));
     }
     }
    }
   }

   public void stop() {
    if(frame != null)
     frame.hide();
   }

    /* Method that is called when the DataManager adds an element to its cache */
  public void Notify(Object s, int msg) {
     addDataSet(s.toString(),false);
  }

  public void Notify(Object s) {
    Notify(s, -1);
  }
}
