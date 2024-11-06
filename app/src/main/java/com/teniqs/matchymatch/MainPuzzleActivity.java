package com.teniqs.matchymatch;

import static com.teniqs.matchymatch.Utils.Utils.getUriToResource;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.teniqs.matchymatch.Utils.Common;
import com.teniqs.matchymatch.Utils.Constants;
import com.teniqs.matchymatch.Utils.Shaker;

import java.util.ArrayList;
import java.util.Random;

public class MainPuzzleActivity extends AppCompatActivity {

    CardView home_search, home_ad_card;


    TextView tv1;
    ArrayList<Integer> puzzleAssets, puzzleAssetsChange, puzzleAssetsSave;
    ArrayList<String> puzzleSounds;
    int score = 0;
    int changer = 0;
    private static final Integer GREY = Color.parseColor("#D2D2D2");
    ImageView imageOne, imageTwo, imageThree, imageFour, imageFive, imageSix, imageSeven, imageEight, imageNine, imageTen, DRAGOne, DRAGtwo;
    TextToSpeech tts;
    ConstraintLayout container;
    MediaPlayer winlevelsound;
    SimpleDraweeView ll;
    Random random;
    private String puzzle_name = "";
    private static final String LEN_PREFIX = "Count_";
    private static final String VAL_PREFIX = "IntValue_";
    //    private int value1, value2;
    ImageView homebutton;
    DraweeController controller;
    private boolean canShowFullscreenAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.main_puzzle);
        puzzleAssets = getIntent().getExtras().getIntegerArrayList("puzzle_assets");
        puzzleAssetsChange = getIntent().getExtras().getIntegerArrayList("puzzle_assets");
        puzzleAssetsSave = getIntent().getExtras().getIntegerArrayList("puzzle_assets");
        puzzleSounds = getIntent().getExtras().getStringArrayList("puzzle_sounds");
        puzzle_name = getIntent().getExtras().getString("puzzle_name");
        ConstraintLayout parent = findViewById(R.id.parent);
        Drawable background = getResources().getDrawable(getIntent().getExtras().getInt("background"));
        parent.setBackground(background);
        tv1 = findViewById(R.id.tv1);
        tv1.setVisibility(View.INVISIBLE);

        home_ad_card = findViewById(R.id.home_ad_card);

        for (int i = 0; i < puzzleAssets.size(); i++) {
            ImageView imageView = (ImageView) parent.getChildAt(i);
            imageView.setImageResource(puzzleAssets.get(i));
        }

        guideClass();


        random = new Random();
        winlevelsound = MediaPlayer.create(MainPuzzleActivity.this, R.raw.kidscheering);
        tts = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {

                tts.setLanguage(Constants.speechLocale);
                tts.setPitch((float) 1.1);
                tts.setSpeechRate((float) 0.6);
            }
        });
        ll = findViewById(R.id.ll);

        controller = Fresco.newDraweeControllerBuilder()
//                .setUri("android.resource://com.matchymatchproject.mirassociationdanny.matchymatch/drawable/puzzle_end_gif.gif")
                .setUri(getUriToResource(MainPuzzleActivity.this, R.drawable.puzzle_end_gif))
//                .setUri("https://media4.giphy.com/avatars/100soft/WahNEDdlGjRZ.gif")
                .setAutoPlayAnimations(true)
                .build();


        Handler forWait = new Handler();
        forWait.postDelayed(() -> getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE), 4000);


        imageOne = findViewById(R.id.image_one);
        imageTwo = findViewById(R.id.image_two);
        imageThree = findViewById(R.id.image_three);
        imageFour = findViewById(R.id.image_four);
        imageFive = findViewById(R.id.image_five);
        imageSix = findViewById(R.id.image_six);
        imageSeven = findViewById(R.id.image_seven);
        imageEight = findViewById(R.id.image_eight);
        imageNine = findViewById(R.id.image_nine);
        imageTen = findViewById(R.id.image_ten);
        DRAGOne = findViewById(R.id.image_puzzle_one);
        DRAGtwo = findViewById(R.id.image_puzzle_two);

        container = findViewById(R.id.parent);

        homebutton = findViewById(R.id.homebtn);

        homebutton.setOnClickListener(v -> {
            Intent LH = new Intent(MainPuzzleActivity.this, MainActivity.class);
            startActivity(LH);
        });


        SharedPreferences myScore = this.getSharedPreferences(puzzle_name + "_MyAwesomeAnimalScore", Context.MODE_PRIVATE);
        score = myScore.getInt("score", 0);

        if (score != 0) {
            puzzleAssetsChange = getFromPrefs("values", myScore);
            puzzleAssetsSave = getFromPrefs("values", myScore);

            for (int i = 0; i < puzzleAssets.size(); i++) {
                ImageView imageView = (ImageView) parent.getChildAt(i);
                boolean isMatched = false;
                Drawable puzzleAssetDrawable = getResources().getDrawable(puzzleAssets.get(i));
                for (int j = 0; j < puzzleAssetsChange.size(); j++) {
                    Drawable puzzleAssetChangeDrawable = getResources().getDrawable(puzzleAssetsChange.get(j));
                    if (areDrawablesIdentical(puzzleAssetDrawable, puzzleAssetChangeDrawable)) {
                        isMatched = true;
                        break;
                    } else {
                        isMatched = false;
                    }
                }
                if (!isMatched) {
                    imageView.setAlpha(0.5f);
                }
            }

        }

        if (score == 0) {

//            score+=1;

            myScore = getSharedPreferences(puzzle_name + "_MyAwesomeAnimalScore", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = myScore.edit();
            editor.putInt("score", score);
            editor.apply();
        }

        int num = random.nextInt(puzzleAssetsChange.size());
        DRAGOne.setImageResource(puzzleAssetsChange.get(num));
//        value1 = puzzleAssetsChange.get(num);
        puzzleAssetsChange.remove(num);
        if (puzzleAssetsChange.size() > 0) {
            num = random.nextInt(puzzleAssetsChange.size());
            DRAGtwo.setImageResource(puzzleAssetsChange.get(num));
//            value2 = puzzleAssetsChange.get(num);
            puzzleAssetsChange.remove(num);
        }

        imageOne.setOnDragListener(dragListener);
        imageTwo.setOnDragListener(dragListener);
        imageThree.setOnDragListener(dragListener);
        imageFour.setOnDragListener(dragListener);
        imageFive.setOnDragListener(dragListener);
        imageSix.setOnDragListener(dragListener);
        imageSeven.setOnDragListener(dragListener);
        imageEight.setOnDragListener(dragListener);
        imageNine.setOnDragListener(dragListener);
        imageTen.setOnDragListener(dragListener);

        //DRAGOne.setOnLongClickListener(longClickListener);
        //DRAGtwo.setOnLongClickListener(longClickListener);
        DRAGOne.setOnTouchListener(Common.touchListener);
        DRAGtwo.setOnTouchListener(Common.touchListener);
        winlevelsound = MediaPlayer.create(MainPuzzleActivity.this, R.raw.kidscheering);


        imageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(puzzleSounds.get(0), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        imageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(puzzleSounds.get(1), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        imageThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(puzzleSounds.get(2), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        imageFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(puzzleSounds.get(3), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        imageFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(puzzleSounds.get(4), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        imageSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(puzzleSounds.get(5), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        imageSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(puzzleSounds.get(6), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        imageEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(puzzleSounds.get(7), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        imageNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(puzzleSounds.get(8), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        imageTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(puzzleSounds.get(9), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        loadAdView();

    }


    public void storeValues(String name, ArrayList<Integer> array, SharedPreferences.Editor edit) {
        if (array != null) {
            edit.putInt(LEN_PREFIX + name, array.size());
            int count = 0;
            for (int i = 0; i < array.size(); i++) {
                edit.putInt(VAL_PREFIX + name + count++, array.get(i));
            }
        } else {
            edit.putInt(LEN_PREFIX + name, 0);
            edit.putInt(VAL_PREFIX + name + 0, 0);
        }
        edit.commit();
    }

    public ArrayList<Integer> getFromPrefs(String name, SharedPreferences prefs) {
        ArrayList<Integer> values = new ArrayList<>();

        int count = prefs.getInt(LEN_PREFIX + name, 0);
        for (int i = 0; i < count; i++) {
            values.add(prefs.getInt(VAL_PREFIX + name + i, i));
        }
        return values;
    }


    public static boolean areDrawablesIdentical(Drawable drawableA, Drawable drawableB) {
        Drawable.ConstantState stateA = drawableA.getConstantState();
        Drawable.ConstantState stateB = drawableB.getConstantState();
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true.
        return (stateA != null && stateB != null && stateA.equals(stateB))
                || getBitmap(drawableA).sameAs(getBitmap(drawableB));
    }

    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }

    View.OnDragListener dragListener = new View.OnDragListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int dragEvent = event.getAction();
            final View view = (View) event.getLocalState();

            switch (dragEvent) {

                case DragEvent.ACTION_DRAG_ENTERED:

                    break;

                case DragEvent.ACTION_DRAG_EXITED:

                    break;

                case DragEvent.ACTION_DROP:

                    if (((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap() != ((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap()) {

                        Shaker shake = new Shaker(v, 0, 15, GREY, Color.RED);
                        shake.shake();
                        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(100);
                        tts.speak(getString(R.string.try_again), TextToSpeech.QUEUE_FLUSH, null);

                    } else {

                        v.setAlpha(0.5f);


                        changer += 1;

                        score += 1;


                        if (view.getId() == R.id.image_puzzle_one) {
                            if (puzzleAssetsChange.size() > 0) {
                                int index = random.nextInt(puzzleAssetsChange.size());
                                int resource = puzzleAssetsChange.get(index);
                                Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(resource)).getBitmap();
                                if (DRAGtwo.getDrawable() != null && (bitmap != ((BitmapDrawable) DRAGtwo.getDrawable()).getBitmap())) {
                                    DRAGOne.setImageResource(resource);
                                    puzzleAssetsChange.remove(index);

                                } else {
                                    DRAGOne.setImageDrawable(null);
                                    DRAGOne.setOnTouchListener(null);
                                }
                            } else {
                                DRAGOne.setImageDrawable(null);
                                DRAGOne.setOnTouchListener(null);
                            }
                        } else if (view.getId() == R.id.image_puzzle_two) {

                            if (puzzleAssetsChange.size() > 0) {
                                int index = random.nextInt(puzzleAssetsChange.size());
                                int resource = puzzleAssetsChange.get(index);
                                Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(resource)).getBitmap();
                                if (DRAGOne.getDrawable() != null && (bitmap != ((BitmapDrawable) DRAGOne.getDrawable()).getBitmap())) {
                                    DRAGtwo.setImageResource(resource);

                                    puzzleAssetsChange.remove(index);
                                } else {
                                    DRAGtwo.setImageDrawable(null);
                                    DRAGtwo.setOnTouchListener(null);
                                }
                            } else {
                                DRAGtwo.setImageDrawable(null);
                                DRAGtwo.setOnTouchListener(null);
                            }
                        }


                        for (int i = 0; i < puzzleAssets.size(); i++) {
                            Drawable drawable = getResources().getDrawable(puzzleAssets.get(i));
                            if (((BitmapDrawable) drawable).getBitmap() == ((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap()) {
                                tts.speak(puzzleSounds.get(i), TextToSpeech.QUEUE_FLUSH, null);

                                for (int j = 0; j < puzzleAssetsSave.size(); j++) {
                                    int resourceSave = puzzleAssetsSave.get(j);
                                    Drawable drawableSave = getResources().getDrawable(resourceSave);
                                    if (areDrawablesIdentical(drawableSave, drawable)) {
                                        puzzleAssetsSave.remove(j);
                                        break;
                                    }
                                }
                                puzzleAssets.remove(i);
                                puzzleSounds.remove(i);
                            }
                        }

                        SharedPreferences myScore = getSharedPreferences(puzzle_name + "_MyAwesomeAnimalScore", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = myScore.edit();
                        editor.putInt("score", score);
                        storeValues("values", puzzleAssetsSave, editor);

                        if (score == 10) {

                            container.setAlpha(0.5f);
//                                    bgimage.setAlpha(0.5f);
                            winlevelsound.start();

//                                    ImageView ll = findViewById(R.id.ll);
                            ll.setController(controller);
                            ll.setVisibility(View.VISIBLE);
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            final Handler DuckBackNavigate = new Handler();
                            DuckBackNavigate.postDelayed(() -> {

                                Intent IDB = new Intent(MainPuzzleActivity.this, MainActivity.class);
//                                          IDB.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                IDB.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(IDB);
                            }, 3500);

                            score = 0;
                            SharedPreferences myScoree = getSharedPreferences(puzzle_name + "_MyAwesomeAnimalScore", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editorr = myScoree.edit();
                            editorr.putInt("score", score);
                            storeValues("values", null, editorr);
                            editorr.apply();
                         //   showFullscreenAd();
                            // textView.setText("Score : " + score);
                        }


                    }

                    break;
            }
            return true;
        }
    };

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

    private void guideClass() {


//        new MaterialIntroView.Builder(this)
//                .enableDotAnimation(true)
//                .enableIcon(false)
//                .setFocusGravity(FocusGravity.CENTER)
//                .setFocusType(Focus.MINIMUM)
//                .setDelayMillis(200)
//                .enableFadeAnimation(true)
//                .performClick(false)
//                .setInfoText("Match below Boxes to Upper boxes to complete the puzzle")
//                .setShape(ShapeType.CIRCLE)
//                .setTarget(tv1)
//                .setUsageId("Tv1") //THIS SHOULD BE UNIQUE ID
//                .show();
    }

    private void loadAdView() {
//        RelativeLayout adContainer = findViewById(R.id.home_adview);
//        AdRequest adRequest = new AdRequest.Builder()
//                .build();
//        AdView adView = new AdView(this);
//        adView.setAdSize(AdSize.SMART_BANNER);
//        adView.setAdUnitId(Config.AD_MOB_BANNER);
//        adContainer.addView(adView);
//        adView.loadAd(adRequest);
//
//        adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                home_ad_card.setVisibility(View.VISIBLE);
//            }
//        });

    }



    @Override
    protected void onPause() {
        super.onPause();
        canShowFullscreenAd = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        canShowFullscreenAd = true;
    }
}
