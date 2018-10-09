package kookyungmin.com.sixthfinger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class SwitchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);

        Button lightOn = (Button)findViewById(R.id.lightOn);
        Button lightOff = (Button)findViewById(R.id.lightOff);

        final ImageView Image = (ImageView)findViewById(R.id.lightImage);
        Image.setImageResource(R.drawable.light_on);

        lightOn.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Image.setImageResource(R.drawable.light_on);
                        lightSwitch("on");
                    }
                }
        );
        lightOff.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Image.setImageResource(R.drawable.light_off);
                        lightSwitch("off");
                    }
                }
        );
    }

    protected void lightSwitch(String state){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.189.144.126/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LightService service = retrofit.create(LightService.class);
        service.lightSwitch(state).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Toast.makeText(SwitchActivity.this, "스위치 " + response.body().toString(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SwitchActivity.this, "와이파이 접속을 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(SwitchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private interface LightService {
        @GET("sixfinger/light/{state}")
        Call<String> lightSwitch(@Path("state") String state);
    }
}
