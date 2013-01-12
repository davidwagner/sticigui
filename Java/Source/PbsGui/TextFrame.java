/** class TextFrame extends Frame
package PbsGui
@author P.B. Stark http://statistics.berkeley.edu/~stark
@version 0.9
@copyright 1997, 1998.  All rights reserved.

Last modified 14 August 1998.

A Frame with one or two TextAreas to display popup results


**/

package PbsGui;
import java.awt.*;
import java.applet.*;

public class TextFrame extends Frame
{
    public TextArea[] textArea;
    public Dimension minimumSize = new Dimension(200,400);
    public Dimension preferredSize = minimumSize;
    public final static int[] rows = {20};
    public final static int[] cols = {40};
    int[] r = {2};
    int[] c = {2};
    int[][] select;
    int nAreas;
    public int vSpace = 60;
    public int hSpace = 15;
    protected Applet myApplet = null;
    protected final static Font textFont = new Font("Courier",Font.PLAIN,12);

    public TextFrame()
    {
        this(null, rows, cols, null, textFont);
    }

    public TextFrame(String title)
    {
        this(title, rows, cols, null, textFont);
    }

    public TextFrame(String title, int row, int col)
    {
        this(title, row, col, null, textFont);
    }

    public TextFrame(String title, int row, int col, Font ft)
    {
        this(title, row, col, null, ft);
    }

    public TextFrame(String title, int row, int col, Applet ma)
    {
        this(title, row, col, ma, textFont);
    }

    public TextFrame(String title, int row, int col, Applet ma, Font ft)
    {
        super();
        myApplet = ma;
        this.setTitle(title);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        nAreas = 1;
        select = new int[1][2];
        textArea = new TextArea[1];
        textArea[0] = new TextArea(row, col);
        textArea[0].setFont(ft);
        add(textArea[0]);
        this.setResizable(true);
        this.resize(minimumSize);
    }

    public TextFrame(String title, int[] row, int[] col)
    {
        this(title, row, col, null, textFont);
    }

    public TextFrame(String title, int[] row, int[] col, Applet ma)
    {
        this(title, row, col, ma, textFont);
    }

    public TextFrame(String title, int[] row, int[] col, Font ft)
    {
        this(title, row, col, null, ft);
    }

    public TextFrame(String title, int[] row, int[] col, Applet ma, Font ft)
    {
        super();
        myApplet = ma;
        this.setTitle(title);
        this.nAreas = Math.min(row.length, col.length);
        select = new int[nAreas][2];
        setLayout(new FlowLayout(FlowLayout.LEFT));
        textArea = new TextArea[nAreas];
        for (int i=0; i < nAreas; i++)
        {
            textArea[i] = new TextArea(row[i], col[i]);
            textArea[i].setFont(ft);
            add(textArea[i]);
        }
        this.setResizable(true);
        this.resize(minimumSize);
        setEditable(false);
    }

    public void setEditable(boolean b)
    {
        for (int i = 0; i < textArea.length; i++)
        {
            textArea[i].setEditable(b);
        }
    }

    public void setEditable(boolean[] b)
    {
        for (int i = 0; i < Math.min(b.length, textArea.length); i++)
        {
            textArea[i].setEditable(b[i]);
        }
    }


    public void sizeToText()
    {
        Dimension d = new Dimension(0,0);
        Dimension di;
        for (int i=0; i < nAreas; i++)
        {
            di = textArea[i].size();
            d.height += di.height;
            d.width = Math.max(d.width,di.width);
        }
        this.resize(d.width + hSpace, d.height + vSpace);
    }

    public void setText(String text)
    {
        textArea[0].setText(text);
    }

    public void setText(String[] text)
    {
        for (int i=0; i < Math.min(nAreas, text.length); i++)
        {
            textArea[i].setText(text[i]);
        }
    }

    public String getText(int i)
    {
        return(textArea[i].getText());
    }

    public String[] getText()
    {
        String[] s = new String[nAreas];
        for (int i=0; i < nAreas; i++)
        {
            s[i] = textArea[i].getText();
        }
        return(s);
    }

    public String[] getText(int[] areas)
    {
        String[] s = new String[areas.length];
        for (int i=0; i < areas.length; i++)
        {
            s[i] = textArea[areas[i]].getText();
        }
        return(s);
    }

    public void appendText(String text)
    {
        textArea[0].appendText(text);
    }

    public void appendText(String[] text)
    {
        for (int i=0; i < nAreas; i++)
        {
            textArea[i] .appendText(text[i]);
        }
    }

    public void clearText()
    {
        for (int i=0; i < nAreas; i++)
        {
            textArea[i] .setText("");
        }
    }

    public void setFont(Font f)
    {
        for (int i = 0; i < nAreas; i++) textArea[i].setFont(f);
    }

    public void setFont(Font[] f)
    {
        for (int i = 0; i < Math.min(nAreas, f.length); i++)
        {
            textArea[i].setFont(f[i]);
        }
    }


    public boolean handleEvent(Event e)
    {
        if (e.id == Event.WINDOW_DESTROY) this.dispose();
        else if (myApplet != null )
        {
            if (e.id == Event.GOT_FOCUS)
            {
                for (int i = 0; i < nAreas; i++)
                {
                    textArea[i].select(select[i][0], select[i][1]);
                }
            }
            else if (e.id == Event.ACTION_EVENT)
            {
                for (int i = 0; i < nAreas; i++)
                {
                    if ( e.target == textArea[i] )
                    {
                        select[i][0] = textArea[i].getSelectionStart();
                        select[i][1] = textArea[i].getSelectionEnd();
                        Event evt = new Event(this, Event.LIST_SELECT, new Integer(i));
                        myApplet.postEvent(evt);
                    }
                }
            }
        }
        return(super.handleEvent(e));
    }

}
