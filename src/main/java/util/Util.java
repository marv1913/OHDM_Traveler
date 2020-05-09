package util;

import java.util.HashMap;

public class Util {

    public static HashMap<String, String> parametersToMap(String args[], String[] requiredArguments, String helpMessage) {


        HashMap<String, String> argumentMap = new HashMap<>();

        int i = 0;
        while(i < args.length) {
            // key is followed by value. Key starts with -
            if(!args[i].startsWith("-")) {
                /* found parameter that does not start with '-'
                maybe shell parameters. Leave it alone. We are done here
                */
                break;
            }

            // value can be empty
            if(args.length > i+1 && !args[i+1].startsWith("-")) {
                // it is a value
                argumentMap.put(args[i], args[i+1]);
                i += 2;
            } else {
                // no value - next parameter
                argumentMap.put(args[i], null);
                i += 1;
            }
        }
        for(int k=0; k<requiredArguments.length; k++){
            if(!argumentMap.containsKey(requiredArguments[k])){
                System.err.println("required argument '" + requiredArguments[k] + "' was not passed");
                System.out.println(helpMessage);
            }
        }
        return argumentMap;
    }
}