package com.example.juegodememoria;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {


    public enum Sonido{PAR,NOPAR, AMBIENTAL_TRONOS, ALERTA24, FICHA , FANFARRIA}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1= findViewById(R.id.button);
        Button button2= findViewById(R.id.button2);
        Button button3= findViewById(R.id.button3);

        button1.setOnClickListener(NivelfacilOyente);
        button2.setOnClickListener(NivelintermedioOyente);
        button3.setOnClickListener(NiveldificilOyente);


    }//onCreate


    View.OnClickListener NivelfacilOyente= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i= new Intent(MainActivity.this, TableroFacilActivity.class);
            startActivity(i);
            finish();

        }//onClick
    };//NivelfacilOyente

    View.OnClickListener NivelintermedioOyente= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i= new Intent(MainActivity.this, TableroNormalActivity.class);
            startActivity(i);
            finish();

        }//onClick
    };//NivelIntermedioOyente

    View.OnClickListener NiveldificilOyente= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i= new Intent(MainActivity.this, TableroDificilActivity.class);
            startActivity(i);
            finish();

        }//onClick
    };//NiveldificilOyente
}//MainActivity

