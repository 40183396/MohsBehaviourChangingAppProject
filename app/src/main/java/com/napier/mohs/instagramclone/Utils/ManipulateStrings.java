package com.napier.mohs.instagramclone.Utils;

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
}
