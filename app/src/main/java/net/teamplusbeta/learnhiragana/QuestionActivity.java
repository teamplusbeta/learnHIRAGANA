package net.teamplusbeta.learnhiragana;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class QuestionActivity extends Activity {

    private TextToSpeech tts;
    private TextView historyView[] = new TextView[5];
    private String historyText[] = new String[50];
    private int current_history_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.question_main);

        int size = (int) getWindowSize(this.getApplicationContext());
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(size, size);
        LinearLayout.LayoutParams Params2 = new LinearLayout.LayoutParams(size / 5, size / 5);


        TextView textView = (TextView) findViewById(R.id.questionText);
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        textView.setTextSize(400 * getScaleSize(this.getApplicationContext()));
        textView.setLayoutParams(Params1);

        int[] history_id = {
                R.id.history1,
                R.id.history2,
                R.id.history3,
                R.id.history4,
                R.id.history5,
        };

        for (int i = 0; i < 5; i++) {
            historyView[i] = (TextView) findViewById(history_id[i]);
            historyView[i].setTextSize(70 * getScaleSize(this.getApplicationContext()));
            historyView[i].setLayoutParams(Params2);
        }

        setText();
        addListenerForImageButton();

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.JAPAN);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            // TextToSpeechのリソースを解放する
            tts.stop();
            tts.shutdown();
        }
    }


    public void setText() {
        TextView textView = (TextView) findViewById(R.id.questionText);
        textView.setText(getRandomHIRAGANA());
    }

    public void speekText( String toSpeek ) {

        if (toSpeek.length() > 0) {
            if (tts.isSpeaking()) {
                // すでに読み上げ中
                tts.stop();
            }
        }

        if (toSpeek.equals("は")) { // workaround "わ" っていっちゃうから...
            toSpeek = "歯";
        } else if (toSpeek.equals("へ")) { // workaround "え" っていっちゃうから...
            toSpeek = "屁";
        }

        tts.speak(toSpeek, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void rewindHistory() {

//        Log.d("TEST", "current_history_index : " + current_history_index);

        if( current_history_index == 0 ) {
            return;
        }

        TextView textView = (TextView) findViewById(R.id.questionText);

        textView.setText(historyView[4].getText());

        for (int i = 4; i > 0; i--) {
            historyView[i].setText(historyView[i - 1].getText());
        }

        if( current_history_index < 6 ) {
            historyView[0].setText("");
        } else {
            historyView[0].setText( historyText[current_history_index-6]);
        }
        if( current_history_index >= 0 ) {
            current_history_index--;
        }

//        showHistory();
//        Log.d("TEST", "current_history_index : " + current_history_index);
    }

    public void addListenerForImageButton() {

        ImageButton imageButton;

        imageButton = (ImageButton) findViewById(R.id.prevButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewindHistory();
            }
        });

        imageButton = (ImageButton) findViewById(R.id.hearButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) findViewById(R.id.questionText);
                speekText(textView.getText().toString() );
            }
        });

        imageButton = (ImageButton) findViewById(R.id.nextButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddHistory();
                setText();
            }
        });

        for (int i = 0; i < 5; i++) {
            historyView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = (TextView)findViewById( v.getId() );
                    speekText( textView.getText().toString() );
                }
            });
        }
    }

    public String getRandomHIRAGANA() {
        Random r = new Random();
        int n = r.nextInt(46); // 0~(46-1)の範囲を生成

        return getHIRAGANA(n);
    }

    public String getHIRAGANA(int n) {
        String str = "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん"; // 46文字
        return str.substring(n, n + 1); // n～n+1文字(n+1文字目は含まない) の抜出し
    }

    public void AddHistory() {

        for (int i = 0; i < (5-1); i++) {
            historyView[i].setText(historyView[i + 1].getText());
        }

        TextView textView = (TextView) findViewById(R.id.questionText);
        historyView[4].setText(textView.getText());

        Log.d("TEST", "historyText.length : " + historyText.length + ", " + current_history_index );

        if( current_history_index >= historyText.length ) {
            for( int i=0;i<historyText.length-1;i++ ) {
                historyText[i] = historyText[i+1];
            }
            current_history_index--;
        }

        historyText[current_history_index] = textView.getText().toString();
        current_history_index++;

//        showHistory();

    }

    public static float getWindowSize(Context context) {

        //画面サイズ取得の準備
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();

        float width;

        // AndroidのAPIレベルによって画面サイズ取得方法が異なるので条件分岐
        if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 13) {
            width = disp.getWidth();
        } else {
            Point size = new Point();
            disp.getSize(size);
            width = size.x;
        }

        return width;
    }

    public void showHistory() {
        for( int i=0;i<historyText.length;i++ ) {
            Log.d( "TEST",  "history " + historyText[i] );
        }
    }

    /**
     * drawableにstone.png（480px×1px）を仕込ませて、幅サイズの基準値にして、
     * 画面サイズによって拡大縮小の調整をする。
     *
     * @param context コンテキスト
     * @return float Scale size
     */
    public static float getScaleSize(Context context) {

        //stone.pngを読み込んでBitmap型で扱う
        Bitmap _bm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.stone);

        float _scale = 0;

        _scale = (float) getWindowSize(context) / (float) _bm.getWidth();

        return _scale;
    }
}
