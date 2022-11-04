package com.example.juegodememoria;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.sql.Time;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TableroDificilActivity extends AppCompatActivity {

    //Constantes para las llaves de los datos del juego
    //atributo común a todos los objetos de la clase

    private static final String TABLERO="com.example.juegodememoria.TABLERO";
    private static String DATOS="com.example.juegodememoria.DATOS";

    private  final Random rng=new Random(); //SecureRandom

    private enum Sonido{PAR,NOPAR, AMBIENTAL_TRONOS, ALERTA24, FICHA , FANFARRIA}

    private final int M=5;
    private final int N=4;
    private final int MINUTO=50;
    private final int DOSMINUTOS= 100;
    private final int MOVEMENTS=18;

    private int[][] tablero={{-1,-1, -2,-2},//representa el tablero de fichas
            {-3,-3, -4,-4},
            {-5,-5, -6,-6},
            {-7,-7, -8,-8},
            {-9,-9, -10,-10}};

    private int contadorClics;
    private int filaPrevia; //guarda la fila del primer clic
    private int columnaPrevia;//guarda la columna del primer clic

    private int dificultad;
    private int tiempo;//Va a empezar en cero
    private int movimientos; //Se considera un movimiento cada dos clics

    //Animacion Fichas
    private Animation animacion_par_encontrado;
    private Animation animacion_sin_par;
    private Animation animacion_ganar;

    private Animation ImageViewPos1;
    private Animation ImageViewPos2;


    //El arreglo de ImageViews esta en funcion al tamaño del tablero
    private ImageView[][] tableroImageViews= new ImageView[tablero.length][tablero[0].length];
    private TextView tiempoTextView;
    private TextView movimientosTextView;

    Timer reloj; //un reloj que se ejecuta cada segundo
    TimerTask tareaReloj;  //objeto que contiene la tarea que realiza el reloj

    MediaPlayer reproductor;//permite reproducir sonidos o musica
    MediaPlayer sonidoJuego;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sonidoJuego=MediaPlayer.create(this, R.raw.ambiental_tronos);


        animacion_par_encontrado=AnimationUtils.loadAnimation(this, R.anim.animacion);
        animacion_sin_par=AnimationUtils.loadAnimation(this, R.anim.par_encontrado);
        animacion_ganar=AnimationUtils.loadAnimation(this, R.anim.animacion_ganar);


        //Se obtiene una referencia al GridLayout que contiene los ImageButtons
        ViewGroup tableroGridLayout = findViewById(R.id.tableroGridLayout);

        //getChildCount devuelve el numero de hijos de una vista
        for (int i = 0; i < tableroGridLayout.getChildCount(); i++) {

            tableroImageViews[i / M][i % N] = (ImageView) tableroGridLayout.getChildAt(i);
            //getChildAt(i) contiene el hijo de una vista en el indice especificado

        }

        for(int f=0; f<M; f++){
            for(int c=0; c<N; c++){

                tableroImageViews[f][c].setOnClickListener(oyenteImageView);
            }
        }

        tiempoTextView= findViewById(R.id.tiempotextView);
        movimientosTextView= findViewById(R.id.movimientostextView);

        if(dificultad==1){

        }else if(dificultad ==2){
            reiniciarJuego(0,0,1);
            reiniciarJuego(DOSMINUTOS,0,-1);

        }else if(dificultad ==3){
            reiniciarJuego(MINUTO, MOVEMENTS, -1);
        }
        animacionGanador = AnimationUtils.loadAnimation(this, R.anim.animacion_ganar);
        animacionGanador.setAnimationListener(animacionGanadorOyente);

        reiniciarJuego();
    }//onCreate



    public void AnimacionGanar(){
    }

    Animation.AnimationListener animacionGanadorOyente=new Animation.AnimationListener() {
        @Override
        //Se llama cuando empieza la animacion
        public void onAnimationStart(Animation animation) {

        }//OnAnimationStart

        //Se llama cuando termina la animacion
        @Override
        public void onAnimationEnd(Animation animation) {

            actualizarGui();
        }//onAnimationEnd

        //Se llama cuando se repite la animacion
        @Override
        public void onAnimationRepeat(Animation animation) {

        }//onAnimationRepeat
    };

    Animation.AnimationListener AnimacionParOyente=new Animation.AnimationListener() {
        @Override
        //Se llama cuando empieza la animacion
        public void onAnimationStart(Animation animation) {

        }//OnAnimationStart

        //Se llama cuando termina la animacion
        @Override
        public void onAnimationEnd(Animation animation) {

            actualizarGui();
        }//onAnimationEnd

        //Se llama cuando se repite la animacion
        @Override
        public void onAnimationRepeat(Animation animation) {

        }//onAnimationRepeat
    };

    Animation.AnimationListener AnimacionSinParOyente=new Animation.AnimationListener() {
        @Override
        //Se llama cuando empieza la animacion
        public void onAnimationStart(Animation animation) {

        }//OnAnimationStart

        //Se llama cuando termina la animacion
        @Override
        public void onAnimationEnd(Animation animation) {
            getView();
        }//onAnimationEnd

        //Se llama cuando se repite la animacion
        @Override
        public void onAnimationRepeat(Animation animation) {

        }//onAnimationRepeat
    };

    //Adaptador para mover GridView
    public GridAdapter(Activity act, int row, List[ContactBean] items){
        super(act, row, items);

        this.activity=act;
        this.row=row;
        this.items=items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.activity_tablero_dificil, null);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        ViewFlipper flipper = (viewFlipper) holder.findViewById(R.id.tableroGridLayout);

        for(int f=0; f<M; f++) {
            for (int c = 0; c < N; c++) {

                    tableroImageViews[f][c].startAnimation(animacion_ganar);

            }
        }


    }


    View.OnClickListener oyenteImageView=new View.OnClickListener() {
        @Override
        public void onClick(View v) { //El objeto v tiene una referencia a la vista sobre la que se hizo clic
            if (contadorClics < 2) { //Para el primer y segundo clic

                //La fila y columna de la ficha sobre la que se hace click
                int fila = 0;
                int columna = 0;

                //Se recorre  toda la matriz de ImageViews  hasta encontrar sobre la que se hizo clic
                for (int f = 0; f < M; f++) {
                    for (int c = 0; c < N; c++) {
                        if (v == tableroImageViews[f][c]) {
                            fila = f;
                            columna = c;
                        }
                    }
                }


                tablero[fila][columna] *= -1;
                contadorClics++;
                actualizarGui();
                reproducirSonido(MainActivity.Sonido.FICHA);


                if (contadorClics == 1) {//Si es el primer clic, guardar fila y columna

                    filaPrevia = fila;
                    columnaPrevia = columna;
                } else if (contadorClics == 2) {//Si es el segundo clic
                    //Si las fichas con diferentes
                    if (tablero[filaPrevia][columnaPrevia] != tablero[fila][columna]) {
                        tablero[filaPrevia][columnaPrevia] *= -1; //Voltear la primera ficha
                        tablero[fila][columna] *= -1; //Voltear la segunda ficha

                    }

                    movimientos++;//Se completo un movimiento


                    Handler manejador = new Handler();

                    if (esGameOver()) {
                        reproducirSonido(MainActivity.Sonido.FANFARRIA);
                        reloj.cancel();
                        //Si termino el juego
                        manejador.postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                reiniciarJuego();

                            }
                        }, 5000); //el juego termina despues de 5 seg
                    } else {   //El juego continua
                        manejador.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                contadorClics = 0;
                                actualizarGui();
                            }
                        }, 1000);


                    }


                }
            }
        }

    }; //OyenteImageView

    private boolean esGameOver(){
        for (int f=0; f<M; f++){
            for(int  c=0; c<N; c++){
                if (tablero[f][c]<0)  //Si hay un valor negativo
                    return false;     //El juego todavia no termino

            }
        }

        return true;
    }// esGameOver()



    private void actualizarGui(){
        for(int f=0; f<M; f++){
            for(int c=0; c<N; c++){
                switch(tablero[f][c]){
                    case 1:
                        tableroImageViews[f][c].setImageResource(R.drawable.dh);//Si la ficha esta volteada evitar mas clics
                        tableroImageViews[f][c].setEnabled(false);
                        break;
                    case 2:
                        tableroImageViews[f][c].setImageResource(R.drawable.dk);
                        tableroImageViews[f][c].setEnabled(false);
                        break;
                    case 3:
                        tableroImageViews[f][c].setImageResource(R.drawable.druid);
                        tableroImageViews[f][c].setEnabled(false);
                        break;
                    case 4:
                        tableroImageViews[f][c].setImageResource(R.drawable.monk);
                        tableroImageViews[f][c].setEnabled(false);
                        break;
                    case 5:
                        tableroImageViews[f][c].setImageResource(R.drawable.paladin);
                        tableroImageViews[f][c].setEnabled(false);
                        break;
                    case 6:
                        tableroImageViews[f][c].setImageResource(R.drawable.shaman);
                        tableroImageViews[f][c].setEnabled(false);
                        break;
                    case 7:
                        tableroImageViews[f][c].setImageResource(R.drawable.warlock);
                        tableroImageViews[f][c].setEnabled(false);
                        break;
                    case 8:
                        tableroImageViews[f][c].setImageResource(R.drawable.warrior);
                        tableroImageViews[f][c].setEnabled(false);
                        break;
                    case 9:
                        tableroImageViews[f][c].setImageResource(R.drawable.belf);
                        tableroImageViews[f][c].setEnabled(false);
                        break;
                    case 10:
                        tableroImageViews[f][c].setImageResource(R.drawable.alian);
                        tableroImageViews[f][c].setEnabled(false);
                        break;
                    default:

                        //Si el ImageView no esta habilitado, quiere decir que es una ficha que ha sido
                        //volteada, y tiene que hacer un sonido al desvoltearse

                        if(tableroImageViews[f][c].isEnabled()){
                            reproducirSonido(MainActivity.Sonido.FICHA);

                        }

                        tableroImageViews[f][c].setImageResource(R.drawable.reverso);
                        tableroImageViews[f][c].setEnabled(true);//permitir click si se muestra el reverso

                }
            }
        }

        movimientosTextView.setText(getString(R.string.movimientos, movimientos));

    }//actualizarGui

    private void reiniciarTablero(){
        for(int f=0;f<M;f++){
            for(int c=0; c<N; c++){
                if(tablero[f][c]>0){
                    tablero[f][c] *= -1;
                }
            }
        }
    }// reiniciarTablero()

    private void reiniciarJuego(){
        contadorClics=0;
        tiempo=0;
        movimientos=0;
        reiniciarTablero();
        mezclarTablero();
        actualizarGui();

        // Inicializamos los objetos de tiempo
        reloj = new Timer();
        tareaReloj = new TimerTask() { // La tera del reloj es un hilo
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tiempoTextView.setText(getString(R.string.tiempo,tiempo/60, tiempo%60));
                        tiempo += 1; // ha transcurrido un segundo más
                    } // run()
                });

                Handler manejador= new Handler();

                if(dificultad !=1){



                }


            } // run()
        };


        reloj.schedule(tareaReloj,0,1000);// La tarea empieza sin retraso y se repite cada segundo

        if (sonidoJuego != null){
            sonidoJuego.release();
        }

        sonidoJuego = MediaPlayer.create(this, R.raw.ambiental_tronos);
        sonidoJuego.setLooping(true);
        sonidoJuego.start();

    }//reiniciarJuego

    private void mezclarTablero() {
        for (int f = 0; f < M; f++) {
            for (int c = 0; c < N; c++) {
                int x=rng.nextInt(M);
                int y=rng.nextInt(N);

                //Se intercambian aleatoriamente dos fichas
                int temp=tablero[f][c];
                tablero[f][c]=tablero[x][y];
                tablero[x][y]=temp;


            }
        }
    }//mezclarTablero()


    //Permite guardar temporalmente en la memoria datos de la aplicacion, con el objeto Bundle
    //El objeto PersistableBundle permite guardar datos permanentemente en almacenamiento secundario


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {


        int [] tableroLineal= new int [M*N];
        int [] datos={contadorClics, filaPrevia, columnaPrevia, tiempo, movimientos};

        for(int f=0;f<M; f++){
            for(int c=0; c<N; c++){
                tableroLineal[M*f+c]= tablero[f][c];
                /*
                0,0=0;
                0,1=1;
                0,2=2;
                0,3=3;
                1,0=4;
                1,1=5;
                1,2=6;
                1,3=7;
                2,0=8;
                */

            }
        }

        outState.putIntArray(TABLERO, tableroLineal);
        outState.putIntArray(DATOS, datos);
        //Lo ultimo que se tiene que hacer despues de guardar los datos es llamar a la version del
        //metodo de la superclase
        super.onSaveInstanceState(outState);
    }//onSaveInstanceState()




    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int [] tableroLineal= savedInstanceState.getIntArray(TABLERO);
        int [] datos= savedInstanceState.getIntArray(DATOS);

        for(int i=0;i<tableroLineal.length;i++){
            tablero[i/M][i%N]= tableroLineal[i];
        }

        contadorClics=datos[0];
        filaPrevia=datos[1];
        columnaPrevia=datos[2];
        tiempo=datos[3];
        movimientos=datos[4];

        actualizarGui();
        movimientosTextView.setText("Entró");
    }//onRestoreInstanceState

    private void reproducirSonido(MainActivity.Sonido sonido) {
        //si el objeto ya esta en uso, liberarlo
        if (reproductor != null) {
            reproductor.release();
        }

        switch (sonido) {

            case FICHA:
                reproductor = MediaPlayer.create(this, R.raw.ficha);
                break;

            case FANFARRIA:
                //getBaseContext obtiene el contexto de la app, independientemente de donde se la
                //Llame(p.e. dentro de una clase anterior)
                reproductor = MediaPlayer.create(getBaseContext(), R.raw.fanfarria);
                break;
        }
        reproductor.start();

        //si el objeto ya esta en uso, liberarlo


    }//reproducirSonido()

    //Se libera el objeto reproductor cuando la app entra en pausa
    @Override
    protected void onPause() {
        super.onPause();
        sonidoJuego.release();
        reproductor.release();


    }//onPause

    //Se vuelve a crear el objeto reproductor cuando la aplicacion resume

    @Override
    protected void onResume() {
        super.onResume();

        reproductor= new MediaPlayer();
    }//onResume

}
