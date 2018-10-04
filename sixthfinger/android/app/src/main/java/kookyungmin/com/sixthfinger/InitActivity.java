package kookyungmin.com.sixthfinger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class InitActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        Button wifiButton = (Button)findViewById(R.id.wifiButton);

        wifiButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Intent intent = new Intent(v.getContext(), BlueToothActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
