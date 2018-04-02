package com.napier.mohs.behaviourchangeapp.Utils;

/**
 * Created by Mohs on 17/03/2018.
 */

public class ManipulateStrings {

    public static String usernameRemovePeriod(String username){
        // removes the period from a username and replaces with a space
        return username.replace(".", " ");
    }

    public static String usernameRemoveSpace(String username){
        return username.replace(" " , ".");
    }

    public static String retrieveTags(String string){
        // checks if tags '#' are included in caption
        if(string.indexOf("#") > 0){
            StringBuilder stringBuilder = new StringBuilder();
            // converts string to character array
            char[] charArray = string.toCharArray();
            boolean wordFound = false;
            // iterates through character array looking for '#'
            for(char c : charArray){
                // if a character = '#' we've found a tag
                if(c == '#'){
                    wordFound = true;
                    stringBuilder.append(c);
                } else {
                    // keeps appending till space is found
                    if(wordFound){
                        stringBuilder.append(c);
                    }
                }
                // checks if character is blank space
                if(c == ' '){
                    wordFound = false;
                }
            }
            // replace space with no space, and '#' to ',#' so tags are seperated by commas
            String s = stringBuilder.toString().replace(" ", "").replace("#", ",#");
            // so input -> #t1 #t2 #t3 becomes output -> #t1, #t2, #t3
            return s.substring(1, s.length());
        }
        return string;
    }
}
