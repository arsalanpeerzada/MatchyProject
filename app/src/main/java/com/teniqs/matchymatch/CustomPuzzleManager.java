package com.teniqs.matchymatch;

import java.util.ArrayList;

public class CustomPuzzleManager {

    private ArrayList<CustomPuzzle> customPuzzles;

    private static CustomPuzzleManager customPuzzleManager;

    private CustomPuzzleManager(){

    }

    public static CustomPuzzleManager getInstance(){
        if(customPuzzleManager == null){
            customPuzzleManager = new CustomPuzzleManager();
        }
        return customPuzzleManager;
    }

    public void addCustomPuzzle(CustomPuzzle customPuzzle){
        customPuzzles.add(customPuzzle);
    }

    public ArrayList<CustomPuzzle> getCustomPuzzles(){
        return customPuzzles;
    }

    public void saveCustomPuzzles(){
        //serialize function
    }

    public void deserializeCustomPuzzles(){
        //deserialize function. Do this on start of the app every time.
    }
}
