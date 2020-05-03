package util;

import java.io.*;
import java.util.StringTokenizer;

public class Parameter {
    private String servername;
    private String portnumber;
    private String username;
    private String pwd;
    private String dbname;
    private String schema;

    public Parameter(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);
        FileReader fr = new FileReader(file);

        BufferedReader br = new BufferedReader(fr);

        String inLine = br.readLine();
        boolean inComment = false;
        boolean skip = false;

        while(inLine != null) {
            skip = false;

            // ignore comments like //
            if(inLine.startsWith("//")) {
                skip = true;
            }

            if(!inComment) {
                if(inLine.startsWith("/*")) {
                    inComment = true;
                    skip = true;
                }
            } else { // in comment
                if(inLine.contains("*/")) {
                    inComment = false;
                }
                // in any case:
                skip = true;
            }

            if(!skip) {
                StringTokenizer st = new StringTokenizer(inLine, ":");
                if(st.hasMoreTokens()) {
                    String key, value;
                    key = st.nextToken();
                    if(st.hasMoreTokens()) {
                        value = st.nextToken();
                        value = value.trim();

                        // fill parameters
                        switch(key) {
                            case "servername": this.servername = value; break;
                            case "portnumber": this.portnumber = value; break;
                            case "username": this.username = value; break;
                            case "pwd": this.pwd = value; break;
                            case "dbname": this.dbname = value; break;
                            case "schema": this.schema = value; break;
                        }
                    }
                }
            }
            // next line
            inLine = br.readLine();
        }
    }
    public String getServername() {
        return servername;
    }

    public String getPortnumber() {
        return portnumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPwd() {
        return pwd;
    }

    public String getDbname() {
        return dbname;
    }

    public String getSchema() {
        return schema;
    }
}
