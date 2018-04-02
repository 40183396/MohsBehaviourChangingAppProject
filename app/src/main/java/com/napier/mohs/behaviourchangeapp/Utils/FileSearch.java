package com.napier.mohs.behaviourchangeapp.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Mohs on 18/03/2018.
 */

// class to searching directories and getting list of everything in directories
public class FileSearch {

    public static ArrayList<String> retrieveDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] filelist = file.listFiles(); // gets list of directories from whatever directory we chose
        for(int i = 0; i < filelist.length; i++){
            if(filelist[i].isDirectory()){
                pathArray.add(filelist[i].getAbsolutePath());
            }
        }
        return pathArray; // returns list of directories inside directory
    }

    public static ArrayList<String> retrieveFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] filelist = file.listFiles(); // gets list of files from whatever directory we chose
        for(int i = 0; i < filelist.length; i++){
            if(filelist[i].isFile()){
                pathArray.add(filelist[i].getAbsolutePath());
            }
        }
        return pathArray; // returns list of files inside directory
    }
}
