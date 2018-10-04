package kookyungmin.com.sixthfinger;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = (Button)findViewById(R.id.startButton);
        ImageView Image = (ImageView)findViewById(R.id.imageView);
        Image.setImageResource(R.drawable.main);
        startButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Intent intent = new Intent(v.getContext(), InitActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
