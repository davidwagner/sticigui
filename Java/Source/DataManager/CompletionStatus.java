package DataManager;
import java.awt.*;

public class CompletionStatus extends Canvas implements Notifiable {
 int remaining = 0;
 Integer Remaining = new Integer(0);
 int total;
 Color color = Color.red;
 CompletionStatus(int total) {
  setBackground(Color.yellow);
  synchronized(Remaining) {
      this.total = total;
      remaining = total;
   }
 }

 public Dimension preferredSize() {
  return new Dimension(100,16);
 }

 public Dimension mininumSize() {
  return preferredSize();
 }

 public void Notify(Object obj, int msg) {
  synchronized(Remaining) {
      remaining--;
   }
   repaint();
 }

 public void paint(Graphics g) {
  int inset = 3;
  Dimension sz = size();
  synchronized(Remaining) {
    g.setColor(color);
    double prop = Math.min(1.0, ((double) (total-remaining)) /(double)total);
    g.fillRect(inset,inset,(int) (prop*(double)(sz.width-2*inset)), sz.height-2*inset);
  }
 }

}


