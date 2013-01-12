package DataManager;
import java.awt.*;

public class AppFrame extends Frame {

  List list = null;
  Button waiting = null;
  Button quit = null;
  inline applet = null;
  CompletionStatus status = null;
  TextArea msg = null;
  AppFrame(String name, inline app, int total) {
   super(name);
   applet = app;
   setLayout(new BorderLayout());
   add("Center",list = new List());
   Panel panel = new Panel();
    panel.setLayout(new FlowLayout());
    panel.add(waiting = new Button("Waiting"));
    panel.add(quit = new Button("Quit"));
   add("North",panel);

  Panel status_panel = new Panel();
    status_panel.setLayout(new BorderLayout());
   status_panel.add("Center", msg = new TextArea());
   status_panel.add("South",status = new CompletionStatus(total));
   add("South", status_panel);

   pack();
   resize(550,600);
   validate();
   show();
  }

 public boolean handleEvent(Event ev) {

  if(ev.id == Event.ACTION_EVENT) {
   if(ev.target.equals(quit)) {  // quit button
    System.exit(0);
   } else if(ev.target.equals(waiting)) { // waiting button
     msg.setText(applet.dmgr.statusMessage());
   }
   if(ev.target.equals(list)) { // list
    if(ev.arg != null && ev.arg instanceof String) {
       displayMessage((String)ev.arg);
    }
   }
  }

   return(false);
 }

 public void displayMessage(String obj) {
      Object set;
      String message = "";

      set = applet.getDataSet(obj, Thread.MAX_PRIORITY);

      if(set != null) {
       if(set instanceof RunnableDataSet)
        message = ((DataSet)set).shortDisplay();
       else if(set instanceof RunnableDocument) {
          message = ((Document)set).contents;
       }
        msg.setText(message);
      }
 }
}



