package util;

import java.io.*;
import java.util.StringTokenizer;

public class SearchParameter {
    private String classOfPerson;
    private String transportType;
    private String waterwayIncl;
    private String startPoint;
    private String endPoint;
    private String day;


    public SearchParameter(String filename) throws FileNotFoundException, IOException {
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
                            case "classofperson": this.classOfPerson = value; break;
                            case "transporttype": this.transportType = value; break;
                            case "waterwayincl": this.waterwayIncl = value; break;
                            case "startpoint": this.startPoint = value; break;
                            case "endpoint": this.endPoint = value; break;
                            case "day": this.day = value; break;
                        }
                    }
                }
            }
            // next line
            inLine = br.readLine();
        }
    }

    public String getClassOfPerson() {
        return classOfPerson;
    }
    public String getTransportType() {
        return transportType;
    }
    public String getWaterwayIncl() {
        return waterwayIncl;
    }
    public String getStartPoint() {
        return startPoint;
    }
    public String getEndPoint() {
        return endPoint;
    }
    public String getDay() {
        return day;
    }
}
