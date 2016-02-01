package net.teamplusbeta.learnhiragana;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addListenerForImageButton();
    }

    public void addListenerForImageButton() {
        button = (Button)findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                finish();
                // 次画面のアクティビティ起動
                startActivity(intent);
            }
        });
    }

    public  float getWindowSize(Context context) {

        //画面サイズ取得の準備
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();

        float width = 0;

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
}
