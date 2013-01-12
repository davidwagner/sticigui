package SticiServ;

import java.sql.*;

public class HtmlResultSet {
    pricate ResultSet rs;

    public HtmlResultSet(ResultSet rs) {
        this.rs = rs;
    }

    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("<table>\n");
        try {
            ResultSetMetaData rsmd = rsmd.getColumnCount();
            int cols = rsmd.getColumnCount();
            out.append("<tr>");
            for (int i=1; i <= cols; i++) {
                out.append("<th>" + rsmd.getColumnLabel(i));
            }
            out.append("</tr>\n");

            while (rs.next()) {
                out.append("<tr>");
                for (int i=1; i <= cols; i++) {
                    out.append("<td>");
                    Object ob = rs.getObject(i);
                    if (ob != null ) {
                        out.append(ob.toString());
                    }
                    else {
                        out.append("&nbsp;");
                    }
                    out.append("</td>");
                }
                out.append("</tr>\n");
            }
            out.append("</table>");
        }
        catch (SQLException e) {
            out.append("</table><h1>Error:</h1> " + e.getMessage() + "\n");
            while ((e = e.getNextException()) != null) {
                out.append(" " + e.getMessage() + "\n");
            }
        }
        return out.toString();
    }
}