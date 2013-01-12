package PbsGui;
import java.awt.*;

public abstract class PbsPlot
{
    public static void drawOpenPolygon(Graphics g, Polygon p)
    {
       for (int i=0; i < p.npoints - 1; i++)
       {
            g.drawLine(p.xpoints[i], p.ypoints[i], p.xpoints[i+1],
                        p.ypoints[i+1]);
       }
    }

}