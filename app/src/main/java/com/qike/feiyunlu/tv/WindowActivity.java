package com.qike.feiyunlu.tv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.qike.feiyunlu.tv.presentation.presenter.Service.FloatService;

public class WindowActivity extends AppCompatActivity {


    
//ddd
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window);

        Button open = (Button)findViewById(R.id.open);
        Button close = (Button)findViewById(R.id.close);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService(new Intent(WindowActivity.this, FloatService.class));

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(WindowActivity.this, FloatService.class));
            }
        });


    }
}
