/**

public class PsServe extends Applet

@author P.B. Stark
@version 2.0 22 June 2010.
@copyright 1998-2010, P.B. Stark. All rights reserved.

Control Problem set access and due dates.

**/

import PbsGui.*;
import PbsStat.*;
import java.awt.*;
import java.applet.*;
import java.util.*;
import java.io.*;
import java.net.*;
import DataManager.*;

public class PsServe extends Applet {
    URL dueDateURL;
    URL sidURL;
    String defaultDateURL = "./Courses/sets.due";
    String defaultSidURL  = "./Courses/sid.txt";
    String acceptAllKey   = "accept_all";
    String noPasswordKey  = "no_passwords";
    String crk = "WhenTheSaintsComeMarchingIn";
    Hashtable dateHash    = new Hashtable(10);
    Hashtable sidHash     = new Hashtable(100);
    String dateFileName   = defaultDateURL;
    String sidFileName    = defaultSidURL;
//    TextArea ta = new TextArea();
    char commentChar = '#';
    public int slack = 11; // slack (minutes) in the due date for server congestion
//
    public void init() {
        super.init();
        setLayout(new BorderLayout());
//        ta.setEditable(false);
//        ta.setFont(new Font("Courier",Font.PLAIN,12));
//        add("Center",ta);
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
        dueDateURL = Format.stringToURL((String) dateFileName, cb);
        sidURL = Format.stringToURL((String) sidFileName, cb);
        setBox();
        getSidFile(sidURL);
    }

    public Date getDueDate(String set) {
        Date date = (Date) ((Object[]) (dateHash.get(set)))[0];
        return(date);
    }

    public boolean pastDue(String set) {
        Date dueDate;
        boolean rv;
        if (dateHash.containsKey(set)) {
            dueDate = (Date) (((Object[]) dateHash.get(set))[0]);
            Date today = new Date();
            rv = dueDate.before(today);
        } else {
            rv = false;
        }
        return(rv);
    }

    public boolean revealKey(String set) {
        boolean rv;
        if (dateHash.containsKey(set)) {
            rv = ((Boolean) (((Object[]) dateHash.get(set))[2])).booleanValue();
        } else {
            rv = false;
        }
        return(rv);
    }

    public boolean isAssigned(String set) {
        boolean ia;
        if (dateHash.containsKey(set)) {
            ia = ((Boolean) (((Object[]) dateHash.get(set))[1])).booleanValue();
        } else {
            ia = false;
        }
        return(ia);
    }

    public String allAssignments() {
        String ans = "";
        for (Enumeration e = dateHash.keys(); e.hasMoreElements();) {
            String s = (String) e.nextElement();
            Object[] val = (Object[]) dateHash.get(s);
            Date dueDate = (Date) val[0];
            ans += dueDate.toLocaleString() + '\t';
            ans += s + "\t";
            if (pastDue(s)) {
                ans +=" past the due date";
            } else if (isAssigned(s)) {
                ans += " still open";
            } else {
                ans += " wait";
            }
            if (((Boolean) (val[2])).booleanValue()) {
                ans += "; answers online";
            }
            ans += "\n";
         }
         return(ans);
    }

    public boolean allowSubmit(String set) {
        return(isAssigned(set) && !pastDue(set));
    }

    public boolean setBox(String s) {
        setDueDateURL(s);
        return(setBox());
    }

    public boolean setBox(){
        getDateFile(dueDateURL);
        Date today = new Date();
        Date[] dateArr = new Date[dateHash.size()];
/*
       ta.setText("");
        ta.appendText("Current as of " + today.toLocaleString() + "\n\n");
        ta.appendText("Date Due             \tAssignment\t Status\n");
        String[] strArr = new String[dateHash.size()];
        int j = 0;
        for (Enumeration e = dateHash.keys(); e.hasMoreElements();) {
            String s = (String) e.nextElement();
            Object[] val = (Object[]) dateHash.get(s);
            Date dueDate = (Date) val[0];
            dateArr[j] = dueDate;
            strArr[j] = dueDate.toLocaleString() + '\t';
            strArr[j] += "Set " + s.substring(11,s.length()) + "\t\t";
            if (pastDue(s)) {
                strArr[j] +=" past the due date";
            } else if (isAssigned(s)) {
                strArr[j] += " still open";
            } else {
                strArr[j] += " wait";
            }
            if (((Boolean) (val[2])).booleanValue()) {
                strArr[j] += "; answers online";
            }
            strArr[j++] += "\n";
        }
        int[] inx = PbsStat.sortIndex(dateArr);
        for (int k=0; k < strArr.length; k++) {
            ta.appendText(strArr[inx[k]]);
        }
        repaint();
*/
        return(true);
    }

    public boolean getDateFile() {
        return(getDateFile(dueDateURL));
    }

    public boolean getDateFile(String s) {
        return(getDateFile(Format.stringToURL(s, getCodeBase())));
    }

    public boolean getDateFile(URL u){
        dateHash = new Hashtable(10);
        DataInputStream inp;
        try {
            inp = new DataInputStream(u.openStream());
            String key;
            String line;
            if (inp != null) {
                Date today = new Date();
                while((line = inp.readLine()) != null && (line.length() >= 2)) {
                    line = Format.crypt(line,crk);
                    StringTokenizer st = new StringTokenizer(line,"\t\n,/: ",false);
                    if ( (key = st.nextToken()) != null) {
                        int[] values = new int[6];
                        int dum = 0;
                        for (int j=0; j < 6 && st.hasMoreTokens(); j++) {
                            dum = j;
                            values[j] = Format.strToInt(st.nextToken());
                        }
                        if (dum == 5) {
                            if (values[2] > 1900) {
                                values[2] -= 1900;
                            }
                            Date theDate = new Date(values[2],values[0]-1,values[1],values[3],
                                           values[4],values[5]);
                            theDate.setMinutes(theDate.getMinutes() - slack);
                            Object[] val = new Object[3];
                            val[0] = theDate;
                            try {
                                String assign = st.nextToken();
                                if (assign.equals("ready")) {
                                    val[1] = new Boolean(true);
                                } else {
                                    val[1] = new Boolean(false);
                                }
                            } catch (NoSuchElementException e) {
                                val[1] = new Boolean(false);
                            }
                            try {
                                String release = st.nextToken();
                                if (release.equals("show_answers") ||
                                    (release.equals("automatic") &&
                                               theDate.before(today))) {
                                    val[2] = new Boolean(true);
                                } else {
                                    val[2] = new Boolean(false);
                                }
                            } catch (NoSuchElementException e) {
                                val[2] = new Boolean(false);
                            }
                            dateHash.put(key,val);
                        }
                    }
                }
                return(true);
            } else {
                return(false);
            }
        } catch (Exception e) {
            System.out.println("\n\n****exception in PsServe.getDateFile() " + e + ", loading URL " + u + "\n\n");
            return(false);
        }
    }

    public boolean getSidFile() {
        return(getSidFile(sidURL));
    }

    public boolean getSidFileByString(String s) {
        return(getSidFile(Format.stringToURL(s, getCodeBase())));
    }

    public boolean getSidFile(URL u){
      sidHash = new Hashtable(100);
      DataInputStream inp;
      try {
        inp = new DataInputStream(u.openStream());
        String key;
        String line;
        if (inp != null) {
          while((line = inp.readLine()) != null) {
            line = Format.crypt(line,crk);
            Object[] val = new Object[2];
            StringTokenizer st = new StringTokenizer(line,"\t, ",false);
            try {
                key = st.nextToken();
                try {
                    val[0] = st.nextToken();
                } catch (NoSuchElementException e) {
                    val[0] = null;
                }
                try {
                    val[1] = st.nextToken();
                } catch (NoSuchElementException e) {
                    val[1] = null;
                }
                sidHash.put(key,val);
            } catch (NoSuchElementException e) { }
          }
        }
        return(true);
      } catch (Exception e) {
         System.out.println("\n\n****exception in PsServe.getSidFile() " + e + ", loading URL " + 
                            u.toString() + "\n\n");
         return(false);
      }
    }

    public boolean isEnrolled(String sid) {
        return(sidHash.containsKey(sid));
    }

    public boolean acceptAll(String key) {
        if (key == null) {
            return(sidHash.containsKey(acceptAllKey));
        } else {
            return(sidHash.containsKey(key));
        }
    }

    public boolean noPassword(String key) {
        if (key == null) {
            return(sidHash.containsKey(noPasswordKey));
        } else {
            return(sidHash.containsKey(key));
        }
    }


    public boolean acceptSid(String sid) {
        return(acceptAll(acceptAllKey) || isEnrolled(sid));
    }

    public void setAcceptAllKey(String key) {
        acceptAllKey = key;
    }

    public String getAcceptAllKey() {
        return(acceptAllKey);
    }

    public void setNoPasswordKey(String key) {
        noPasswordKey = key;
    }

    public String getNoPasswordKey() {
        return(noPasswordKey);
    }

    public boolean isOkPasswd(String sid, String passwd) {
        if (acceptAll(acceptAllKey) || noPassword(noPasswordKey)) {
            return(true);
        } else if (sidHash.containsKey(sid)) {
            Object[] obj = (Object[]) sidHash.get(sid);
            return(((String) obj[0]).trim().equals(passwd.trim()));
        } else {
            return(false);
        }
    }

    public String getEmail(String sid) {
        return( (String) ((Object[]) sidHash.get(sid))[1]);
    }

    public void setSidURL(String s) {
        sidURL = Format.stringToURL((String) s, getCodeBase());
    }

    public URL getSidURL() {
        return(sidURL);
    }

    public void setDueDateURL(String s) {
        dueDateURL = Format.stringToURL((String) s, getCodeBase());
    }

    public URL getDueDateURL() {
        return(dueDateURL);
    }

} // ends class PsServe.
