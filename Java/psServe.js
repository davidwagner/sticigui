/**
@author P.B. Stark
@version 1.0  11 January 2013.
@copyright 2013, P.B. Stark. All rights reserved.
**/
function psServe(ddurl, surl) {
    this.defaultTimeServer = 'http://www.google.com';
    this.defaultDueDateURL =  './Courses/sets.json';
    this.defaultSidURL  = './Courses/sid.json';
    if (!ddurl) {
        this.dueDateURL = defaultDueDateURL;
    } else {
        this.dueDateURL = ddurl;
    }
    if (!surl) {
        this.sidURL = defaultSidURL;
    } else {
        this.sidURL = surl;
    }
    this.acceptAllKey   = 'accept_all';
    this.noPasswordKey  = 'no_passwords';
    this.crk            = "WhenTheSaintsComeMarchingIn";
    this.dateHash       = new Array();
    this.sidHash        = new Array();
    this.commentChar    = '#';
    this.slack          = 11;
    this.serverDate     = getServerDate(document.location.href);

    this.getDueDate = function(set) {
        var dueDate = null;
        if (this.dateHash.indexOf(set)) {
            dueDate = new Date(this.dateHash[set]);
        }
        return(dueDate);
    }

    this.isAssigned = function(set) {
        return(this.dateHash.indexOf(set));
    }

    this.pastDue = function(set) {
        var rv = false;
        if (this.isAssigned(set)) {
            if (this.getDueDate(set).getTime() < this.serverDate.getTime()) {
                rv = true;
            }
        }
        return(rv);
    }
    this.allowSubmit = function(set) {
        return(this.isAssigned(set) && !this.pastDue(set));
    }

    this.setBox = function(u) {
        getSidFile(sidURL);
    }

    boolean revealKey(String set) {
        var rv = false;
        if (dateHash.containsKey(set)) {
            rv = ((Boolean) (((Object[]) dateHash.get(set))[2])).booleanValue();
        }
        return(rv);
    }

    function setBox(String s) {
        setDueDateURL(s);
        return(setBox());
    }

    function setBox(){
        this.getDateFile(dueDateURL);
        this.setServerDate = getServerDate(window.location.href);
        this.dateArr = new Array();
        return(true);
    }

    function getDateFile(s) {
        dateHash = new Array();
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
            alert("\n\n****exception in PsServe.getDateFile() " + e + ", loading URL " + u + "\n\n");
            return(false);
        }
    }


    function getSidFileByString(s) {
        sidHash = new Array();
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

    boolean isEnrolled(String sid) {
        return(sidHash.containsKey(sid));
    }

    boolean acceptAll(String key) {
        if (key == null) {
            return(sidHash.containsKey(acceptAllKey));
        } else {
            return(sidHash.containsKey(key));
        }
    }

    boolean noPassword(String key) {
        if (key == null) {
            return(sidHash.containsKey(noPasswordKey));
        } else {
            return(sidHash.containsKey(key));
        }
    }


    boolean acceptSid(String sid) {
        return(acceptAll(acceptAllKey) || isEnrolled(sid));
    }

    boolean isOkPasswd(String sid, String passwd) {
        if (acceptAll(acceptAllKey) || noPassword(noPasswordKey)) {
            return(true);
        } else if (sidHash.containsKey(sid)) {
            Object[] obj = (Object[]) sidHash.get(sid);
            return(((String) obj[0]).trim().equals(passwd.trim()));
        } else {
            return(false);
        }
    }


    void setSidURL(String s) {
        sidURL = Format.stringToURL((String) s, getCodeBase());
    }


    void setDueDateURL(String s) {
        dueDateURL = Format.stringToURL((String) s, getCodeBase());
    }
