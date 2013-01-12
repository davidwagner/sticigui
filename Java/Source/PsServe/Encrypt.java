/**

public class Encrypt extends Applet

@author P.B. Stark
@version 1.0 10 November 1999.
@copyright 1998, 1999, P.B. Stark. All rights reserved.

Encrypts using xor

**/

import PbsGui.*;
import PbsStat.*;
import java.awt.*;
import java.applet.*;
import java.util.*;
import java.io.*;
import java.net.*;
import DataManager.*;

public class Encrypt extends Applet {
    URL dateURL;
    URL sidURL;
    final String defaultDateURL = "setsRaw.due";
    final String defaultSidURL = "sidRaw.txt";
    String crk = "WhenTheSaintsComeMarchingIn";
    String dateFileName;
    String sidFileName;
    TextArea[] ta = new TextArea[4];
    char commentChar = '#';
//
    public void init() {
      super.init();
      setLayout(new BorderLayout());
      Panel thePanel = new Panel();
      thePanel.setLayout(new GridLayout(2,2));
      for (int i=0; i < 4; i++) {
        ta[i] = new TextArea();
        ta[i].setEditable(false);
        ta[i].setFont(new Font("Courier",Font.PLAIN,12));
        ta[i].setText("");
        thePanel.add(ta[i]);
      }
      add("Center",thePanel);
      if (getParameter("files") != null) {
          StringTokenizer st = new StringTokenizer(getParameter("files"),"\n\t, ",false);
          dateFileName = (st.nextToken()); // first filename is for due dates
          sidFileName = (st.nextToken());  // second file name is for SID
      } else {
          dateFileName = defaultDateURL;
          sidFileName = defaultSidURL;
      }
      if (getParameter("cKey") != null) {
          crk = getParameter("cKey");
      }
      URL cb = getCodeBase();
      dateURL = Format.stringToURL((String) dateFileName, cb);
      sidURL = Format.stringToURL((String) sidFileName, cb);
      setBoxes(sidURL, dateURL);
    }

    public boolean setBoxes(URL u, URL v){
      DataInputStream inp;
      try {
        inp = new DataInputStream(u.openStream());
        String key;
        String line;
        if (inp != null) {
          while((line = inp.readLine()) != null) {
            ta[0].appendText(line + "\n");
            ta[1].appendText(Format.crypt(line,crk) + "\n");
          }
        }
      } catch (IOException e) {
         System.out.println(" exception in Encrypt.setBoxes() " + e + ", loading URL " + sidURL);
         repaint();
         return(false);
      }
      try {
        inp = new DataInputStream(v.openStream());
        String key;
        String line;
        if (inp != null) {
          while((line = inp.readLine()) != null) {
            ta[2].appendText(line + "\n");
            ta[3].appendText(Format.crypt(line,crk) + "\n");
          }
        }
      } catch (IOException e) {
         System.out.println(" exception in Encrypt.setBoxes() " + e + ", loading URL " + sidURL);
         repaint();
         return(false);
      }
      return(true);
    }


} // ends class PsServe.
