package kookyungmin.com.sixthfinger;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WifiActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        Intent intent = new Intent(this.getIntent());
        BluetoothDevice selectedDevice = intent.getParcelableExtra("selectedDevice");
        
    }
}
