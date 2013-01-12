/** class ListFrame extends Frame
package PbsGui
@author P.B. Stark http://statistics.berkeley.edu/~stark
@version 0.9
@copyright 1997, 1998.  All rights reserved.

Last modified 14 July 1998.

A Frame with one or two Lists to display popup results


**/

package PbsGui;
import java.awt.*;
import java.applet.*;

public class ListFrame extends Frame
{
    public final static int[] rows = {20};
    public final static int[] cols = {40};
    public List[] list;
    public Dimension minimumSize = new Dimension(200,400);
    public Dimension preferredSize = minimumSize;
    int[] r = {2};
    int[] c = {2};
    int nLists;
    public int vSpace = 60;
    public int hSpace = 20;
    protected Applet myApplet = null;
    protected int[][] selectedIndices;
    protected static final Font textFont = new Font("Courier",Font.PLAIN,10);
    FontMetrics fm;
    Panel panel;

    public ListFrame()
    {
        this(null, rows, cols, null, true, textFont);
    }

    public ListFrame(String title)
    {
        this(title, rows, cols, null, true, textFont);
    }

    public ListFrame(String title, int row, int col)
    {
        this(title, row, col, null, true, textFont);
    }

    public ListFrame(String title, int row, int col, Applet ma)
    {
        this(title, row, col, ma, true, textFont);
    }

    public ListFrame(String title, int row, int col, Applet ma, boolean multiple)
    {
        this(title, row, col, ma, multiple, textFont);
    }

    public ListFrame(String title, int row, int col, Applet ma, boolean multiple, Font ft)
    {
        super();
        myApplet = ma;
        this.setTitle(title);
        setLayout(new BorderLayout());
        panel = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        nLists = 1;
        list = new List[1];
        list[0] = new List(row, multiple);
        list[0].setFont(ft);
        panel.add(list[0]);
        add("Center",panel);
        this.setResizable(true);
        this.resize(minimumSize);
        selectedIndices = new int[1][];
    }

    public ListFrame(String title, int[] row, int[] col)
    {
        this(title, row, col, null, true, textFont);
    }

    public ListFrame(String title, int[] row, int[] col, Applet ma)
    {
        this(title, row, col, ma, true, textFont);
    }

    public ListFrame(String title, int[] row, int[] col, Applet ma, boolean multiple)
    {
        this(title, row, col, ma, multiple, textFont);
    }

    public ListFrame(String title, int[] row, int[] col, Applet ma, boolean multiple, Font ft)
    {
        super();
        myApplet = ma;
        this.setTitle(title);
        this.nLists = Math.min(row.length, col.length);
        setLayout(new BorderLayout());
        panel = new Panel();
        panel.setLayout(new BorderLayout());
        list = new List[nLists];
        for (int i=0; i < nLists; i++)
        {
            list[i] = new List(row[i], multiple);
            list[i].setFont(ft);
        }
        add("North",list[0]);
        panel.add("Center",list[1]);
        add("Center",panel);
        this.setResizable(true);
        this.resize(minimumSize);
        selectedIndices = new int[nLists][];
    }

    public void setMultipleSelections(boolean b)
    {
        for (int i = 0; i < list.length; i++)
        {
            list[i].setMultipleSelections(b);
        }
    }

    public void setMultipleSelections(boolean[] b)
    {
        for (int i = 0; i < Math.min(b.length, list.length); i++)
        {
            list[i].setMultipleSelections(b[i]);
        }
    }

    public void sizeToText()
    {
        Dimension d = new Dimension(0,0);
        Dimension di;
        for (int i=0; i < nLists; i++)
        {
            fm = getFontMetrics(list[i].getFont());
            di = list[i].preferredSize();
            d.height += di.height;
            for (int j=0; j < list[i].countItems(); j++)
            {
                d.width = Math.max(d.width, fm.stringWidth(list[i].getItem(j)+"  "));
            }
        }
        for (int i=0; i< nLists; i++)
        {
            list[i].resize(d.width + hSpace/3, list[i].preferredSize().height);
        }
        panel.resize(d.width + hSpace/3, list[1].preferredSize().height);
        this.resize(d.width + hSpace, d.height + vSpace);
        panel.invalidate();
        validate();
    }

    public void setList(String[] text)
    {
        list[0].clear();
        for (int i=0; i< text.length; i++)
        {
            list[0].addItem(text[i]);
        }
    }

    public void clear()
    {
        for (int i=0; i < nLists; i++)
        {
            list[i].clear();
        }
    }

    public void setList(String[][] text)
    {
        for (int i=0; i < Math.min(nLists, text.length); i++)
        {
            list[i].clear();
            for (int j=0; j < text[i].length; j++)
            {
                list[i].addItem(text[i][j]);
            }
        }
    }


    public void addItem(String text)
    {
        list[0].addItem(text);
    }

    public void addItems(String[] text)
    {
        for (int i=0; i < text.length; i++)
        {
            list[0].addItem(text[i]);
        }
    }

    public void addItems(String[][] text)
    {
        for (int i=0; i < nLists; i++)
        {
            for (int j=0; j < text[i].length; j++)
            {
                list[i].addItem(text[i][j]);
            }
        }
    }

    public void setFont(Font f)
    {
        for (int i = 0; i < nLists; i++) list[i].setFont(f);
    }

    public void setFont(Font[] f)
    {
        for (int i = 0; i < Math.min(nLists, f.length); i++)
        {
            list[i].setFont(f[i]);
        }
    }

    public boolean handleEvent(Event e)
    {
        if (e.id == Event.WINDOW_DESTROY) this.dispose();
        else if (myApplet != null)
        {
            if ( // e.id == Event.GOT_FOCUS ||
                    e.id == Event.WINDOW_EXPOSE ||
                    e.id == Event.WINDOW_DEICONIFY)
            {
                for (int i=1; i < nLists; i++)
                {
                    for (int j=0; j < selectedIndices[i].length; j++)
                    {
                        list[i].select(selectedIndices[i][j]);
                    }
                }
            }
            else if (e.id == Event.LIST_SELECT || e.id == Event.LIST_DESELECT)
            {
                for (int i=0; i < nLists; i++)
                {
                    selectedIndices[i] = list[i].getSelectedIndexes();
                }
                for (int i = 0; i < nLists; i++)
                {
                    if ( e.target == list[i] )
                    {
                        Event evt = new Event(this, e.id, new Integer(i));
                        myApplet.postEvent(evt);
                    }
                }
            }
        }
        return(super.handleEvent(e));
    }

}
