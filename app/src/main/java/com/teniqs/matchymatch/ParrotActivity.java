package com.teniqs.matchymatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.teniqs.matchymatch.Utils.AlarmSoundService;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ParrotActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 7777;
    private static final int CAM_REQUEST = 1313;

    ImageView invisible_imageView;

    ImageView image_one,image_two,image_three,image_four,image_five,image_six;
    TextView TV_ONE,TV_TWO,TV_THREE,TV_FOUR,TV_FIVE,TV_SIX;

    ImageView homebtn;

    private ArrayList<CustomPuzzle> customPuzzles;
    private static final Type TYPE = new TypeToken<ArrayList<CustomPuzzle>>() {}.getType();
    SharedPreferences myPuzzle, myCustomPuzzle;
    SharedPreferences.Editor myPuzzleEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_parrot);

        //isi me add karo aur isi ko serialize/deserialize karo.
//        customPuzzles = CustomPuzzleManager.getInstance().getCustomPuzzles();

        Toast.makeText(ParrotActivity.this, "Please create puzzle from Parent's section", Toast.LENGTH_LONG).show();

        myPuzzle = this.getSharedPreferences("MyPuzzle", Context.MODE_PRIVATE);

        customPuzzles = loadData();

        homebtn = findViewById(R.id.homebtn);

        homebtn.setOnClickListener(v -> {
            Intent CM = new Intent(ParrotActivity.this , MainActivity.class);
            CM.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(CM);
        });

        invisible_imageView  = findViewById(R.id.save_image_uri);


        // declear images
        image_one   = findViewById(R.id.image_one);
        image_two   = findViewById(R.id.image_two);
        image_three = findViewById(R.id.image_three);
        image_four  = findViewById(R.id.image_four);
        image_five  = findViewById(R.id.image_five);
        image_six   = findViewById(R.id.image_six);

        //declear textview
        TV_ONE   = findViewById(R.id.puzzle_name_one);
        TV_TWO   = findViewById(R.id.puzzle_name_two);
        TV_THREE = findViewById(R.id.puzzle_name_three);
        TV_FOUR  = findViewById(R.id.puzzle_name_four);
        TV_FIVE  = findViewById(R.id.puzzle_name_five);
        TV_SIX   = findViewById(R.id.puzzle_name_six);


        //-------------------------------------this code for changing puzzle name start here....................

        //change puzzle name in first textView

        ArrayList<String> puzzleNames = new ArrayList<>();
        puzzleNames.add("Yellow");
        puzzleNames.add("LightBlue");
        puzzleNames.add("Green");
        puzzleNames.add("Pink");
        puzzleNames.add("Orange");
        puzzleNames.add("Purple");

        if(customPuzzles != null) {

            setText(puzzleNames);

        }
        setImage(puzzleNames);

        //-------------------------------------this code for changing puzzle name end here....................

        //-------------------------------------this code for fetching puzzle images start here....................


        //----------------------------------------end for fetching------------------------------------------

        image_one.setOnClickListener(v -> {

            startActivity(R.drawable.yellowbackground, "Yellow", image_one);

        });

        image_two.setOnClickListener(v -> {

            startActivity(R.drawable.bluebackground, "LightBlue", image_two);

        });

        image_three.setOnClickListener(v -> {

            startActivity(R.drawable.greenbackground, "Green", image_three);

        });

        image_four.setOnClickListener(v -> {

            startActivity(R.drawable.pinkbackground, "Pink", image_four);
        });

        image_five.setOnClickListener(v -> {

            startActivity(R.drawable.orangebackground, "Orange", image_five);
        });

        image_six.setOnClickListener(v -> {

            startActivity(R.drawable.purplebackground, "Purple", image_six);

        });

    }


    public ArrayList<CustomPuzzle> loadData() {
        customPuzzles = new Gson().fromJson(myPuzzle.getString("customPuzzles", null), TYPE);

        return customPuzzles;
    }

    //---------------------------create puzzles popup end here....................................

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null)
        {
            Uri pickedimage = data.getData();
            invisible_imageView.setImageURI(pickedimage);


        }if (requestCode == CAM_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            invisible_imageView.setImageBitmap(bitmap);

        }
    }

    private void startActivity(int bg, String puzzle, ImageView imageView)
    {

        if (imageView.getDrawable() == null || MainPuzzleActivity.areDrawablesIdentical(imageView.getDrawable() ,getResources().getDrawable(R.drawable.image_placeholder))) {
            imageView.setImageDrawable(getDrawable(R.drawable.image_placeholder));
            Toast.makeText(ParrotActivity.this, "Please create puzzle from Parent's section", Toast.LENGTH_LONG).show();
        }else {

            Intent intent = new Intent(ParrotActivity.this, CustomMainPuzzlePlayActivity.class);
            intent.putExtra("bgColor", bg);
            intent.putExtra("puzzleName", puzzle);

            startActivity(intent);
        }
    }

    private void setImage(ArrayList<String> puzzleNames)
    {

        ArrayList<ImageView> imageViews = new ArrayList<>();
        imageViews.add(image_one);
        imageViews.add(image_two);
        imageViews.add(image_three);
        imageViews.add(image_four);
        imageViews.add(image_five);
        imageViews.add(image_six);

        for(int i = 0; i < imageViews.size(); i++) {

            try {
//                if (imageViews.get(i).getDrawable() == null) {

//                byte[] data = dbHelper.GetBitmapByName("firstimage");
                    Bitmap bitmap = null;
                    myCustomPuzzle = this.getSharedPreferences(puzzleNames.get(i)+"_MyAwesomePuzzle", Context.MODE_PRIVATE);
                    if (myCustomPuzzle.getBoolean("isComplete", false) && customPuzzles != null && customPuzzles.get(i) != null && !customPuzzles.get(i).getBackgroundImage().equalsIgnoreCase("")) {
//                        byte[] b = Base64.decode(customPuzzles.get(i).getBackgroundImage(), Base64.DEFAULT);
//                        bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                        Picasso.get()
                                .load(new File(customPuzzles.get(i).getBackgroundImage()))
                                .into(imageViews.get(i));
                    } else {
                        imageViews.get(i).setImageDrawable(getDrawable(R.drawable.image_placeholder));
                    }

//                    if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                        imageViews.get(i).setImageBitmap(bitmap);
//                    } else {
//                        imageViews.get(i).setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                    }

//                } else {
//
//                }
            } catch (Exception e) {
                e.printStackTrace();
                imageViews.get(i).setImageDrawable(getDrawable(R.drawable.image_placeholder));
            }
        }
    }

    private void setText(ArrayList<String> puzzleNames)
    {
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(TV_ONE);
        textViews.add(TV_TWO);
        textViews.add(TV_THREE);
        textViews.add(TV_FOUR);
        textViews.add(TV_FIVE);
        textViews.add(TV_SIX);

            for (int i = 0; i < customPuzzles.size(); i++) {
                try {

                    myCustomPuzzle = this.getSharedPreferences(puzzleNames.get(i)+"_MyAwesomePuzzle", Context.MODE_PRIVATE);
                    if (customPuzzles.get(i) != null && myCustomPuzzle.getBoolean("isComplete", false)) {
                        textViews.get(i).setText(customPuzzles.get(i).getName());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


    }

    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
        //Stop the Media Player Service to stop sound
        stopService(new Intent(ParrotActivity.this, AlarmSoundService.class));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
