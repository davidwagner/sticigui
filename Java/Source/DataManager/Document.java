/** Document.java
D. Temple Lang and P.B. Stark  stark AT stat.berkeley.edu
Copyright 1997, 1998.  All rights reserved.
Last modified 2/1/98 by P.B. Stark

*/

package DataManager;

import java.io.*;
import java.net.*;

public class Document
{
     public URL u = null;
     public String contents;

     public Document(String name)
     {
           try
           {
                u = new URL(name);
           }
           catch(MalformedURLException e)
           {
                System.out.println("[Document] " + e);
           }
     }

     public Document(URL u)
     {
            this.u = u;
     }

     public String load()
     {
           contents = "";
           try
           {
                 URLConnection con = u.openConnection();
                 BufferedInputStream in = null;
                 DataInputStream stream;
                 if(con != null)
                 {
                    in = new BufferedInputStream((InputStream) con.getContent());
                 }
                 stream = new DataInputStream(in);
                 if(stream != null)
                 {
                       String line;
                       while((line= stream.readLine()) != null)
                       {
                            contents += line + "\n";
                       }
                 }
           }
           catch(Exception e)
           {
                System.out.println("Document.load(): problem loading " + this + "\n"
                                    + "Exception " + e);
           }
           return contents;
     }

     public String toString()
     {
           return u.toString();
     }
}
