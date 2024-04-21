package com.techwitz.matchymatch;

public class CustomPuzzle {
    private String name;

    private String backgroundImage;

//    private ArrayList<byte[]> playableImages;
    public CustomPuzzle(){}

    public CustomPuzzle(String name, String backgroundImage) {
        this.name = name;
        this.backgroundImage = backgroundImage;
//        this.playableImages = playableImages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

//    public ArrayList<byte[]> getPlayableImages() {
//        return playableImages;
//    }
//
//    public void setPlayableImages(ArrayList<byte[]> playableImages) {
//        this.playableImages = playableImages;
//    }
}
