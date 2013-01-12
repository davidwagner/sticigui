package PbsStat;

import java.awt.*;
import java.applet.*;

public class TestPbsStat extends Applet
{
    public void init()
    {
        double x = 0;
        double dx = 0.25;
        for (int i = 0; i < 20; i++)
        {
            x += dx;
            System.out.println("x, Phi(x) " + x + " " +
                PbsStat.normCdf(x));
        }
        double p = 0.1;
        double dp = 0.025;
        for (int i = 0; i < 38; i++)
        {
            p += dp;
            System.out.println("p, Phi^-1(x), Phi(Phi^-1) " + p + " " +
                PbsStat.normInv(p) + " " +
                PbsStat.normCdf(PbsStat.normInv(p)));
        }
    }
}