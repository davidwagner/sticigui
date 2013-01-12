package PbsGui;

import java.awt.*;
import java.applet.*;

public class TestCrypt extends Applet
{
    public void init()
    {
        String start = "abc1234";
        String key = "31415";
        String finish = Format.crypt(start, key);
        System.out.println("start, finish " + start + " " + finish);
        String redo = Format.crypt(finish,key);
        System.out.println("finish, redo " + finish + " " + redo);
    }
}