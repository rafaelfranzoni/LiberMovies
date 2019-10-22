package br.com.libermovies.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.teleclinic.bulent.smartimageview.SmartImageViewLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import br.com.libermovies.R;
import br.com.libermovies.controller.LiberMovies;
import br.com.libermovies.controller.RequestAPI;
import br.com.libermovies.controller.ServidorListener;

@SuppressLint("NewApi")
public class MovieDetailActivity extends AppCompatActivity {

    private String idMovie, add;
    private int anterior, proximo, size;
    private boolean isFavorite;
    private HashMap<String, String> movie;
    private String[] arrayFavorites;

    private Button btnProxFavo, btnAnteFavo;
    private ScrollView sw;
    private TextView titleTv,timeSeasonsTv,languageTv,plotTv;
    private SmartImageViewLayout iv;
    private ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent it = getIntent();

        idMovie = it.getStringExtra("IdMovie");
        isFavorite = it.getBooleanExtra("ISFAVORITES", false);

        carregaFavorites();

        titleTv = findViewById(R.id.tv_TitleDetail);
        timeSeasonsTv = findViewById(R.id.tv_Time_seasons);
        languageTv = findViewById(R.id.tv_Language);
        plotTv = findViewById(R.id.tv_Plot);
        iv = findViewById(R.id.iv_Poster);
        toggleButton = findViewById(R.id.ib_Favorite);
        btnProxFavo = findViewById(R.id.btn_ProxFavo);
        btnAnteFavo = findViewById(R.id.btn_AnteFavo);
        sw = findViewById(R.id.sv_Detail);

        if (!isFavorite){
            btnProxFavo.setVisibility(View.INVISIBLE);
            btnAnteFavo.setVisibility(View.INVISIBLE);
            carrega(idMovie);
        }else {
            toggleButton.setVisibility(View.INVISIBLE);
            invisibleButton(btnAnteFavo);
            try {
                if (!add.equals(null)&&!add.equals("")){
                    arrayFavorites = add.split(",");
                    size = arrayFavorites.length;

                    if (size >=1){
                        btnProxFavo.setText("Próxima");
                        btnAnteFavo.setText("Anterior");
                        carrega(arrayFavorites[proximo]);
                        proximo++;
                    }else{
                        btnProxFavo.setVisibility(View.INVISIBLE);
                        btnAnteFavo.setVisibility(View.INVISIBLE);
                    }
                }else{
                    mostraAlerta("Ops!", "Não há nenhum filme ou série adicionados aos favoritos.");
                }
            }catch (Exception e){
                mostraAlerta("Ops!", "Não há nenhum filme ou série adicionados aos favoritos.");
            }
        }

        btnAnteFavo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (anterior<=size){
                        sw.scrollTo(0,0);

                        proximo = anterior+1;

                        carrega(arrayFavorites[anterior]);

                        visibleButton(btnProxFavo);

                        anterior--;
                    }else{
                        visibleButton(btnProxFavo);
                        invisibleButton(btnAnteFavo);
                    }
                }catch (Exception e){
                    invisibleButton(btnAnteFavo);
                }
            }
        });

        btnProxFavo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (proximo<=size){
                        sw.scrollTo(0,0);

                        anterior = proximo -1;

                        carrega(arrayFavorites[proximo]);

                        if (anterior>=0){
                            visibleButton(btnAnteFavo);
                        }else{
                            invisibleButton(btnAnteFavo);
                        }
                        proximo++;
                    }else{
                        invisibleButton(btnProxFavo);
                        visibleButton(btnAnteFavo);
                    }
                }catch (Exception e){
                    invisibleButton(btnProxFavo);
                }
            }
        });

        toggleButton.setChecked(false);
        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star));

        if (!add.equals("")){
            String[] favo = add.split(",");

            for (int i=0;i<=favo.length-1;i++){
                if (favo[i].equals(idMovie)){
                    toggleButton.setChecked(true);
                    toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_sucess));
                }
            }
        }

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.star_sucess));

                    carregaFavorites();

                    LiberMovies.getInstance().setSharedPreferences("ID_MOVIES_FAVORITES", add+idMovie+",", MovieDetailActivity.this);

                    mostraAlerta("Parabéns!", "Este título foi adicionadas aos seus favoritos.");

                }else{
                    toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star));

                    LiberMovies.getInstance().setSharedPreferences("ID_MOVIES_FAVORITES", String.join(",", removeElements(idMovie, MovieDetailActivity.this)), MovieDetailActivity.this);

                    mostraAlerta("Ops!", "Você removeu este título de seus favoritos.");
                }
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void invisibleButton(Button btns){
        btns.setBackgroundResource(R.color.colorNull);
        btns.setTextColor(00000000);
    }

    @SuppressLint("ResourceAsColor")
    private void visibleButton(Button btns){
        btns.setBackgroundResource(R.drawable.rounded_button);
        btns.setTextColor(R.color.colorAzul);
    }

    private void carrega(String id){
        RequestAPI.goGetMovieDetail(id, MovieDetailActivity.this, new ServidorListener() {
            @Override
            public void recebeuResposta(String msgErro, Boolean resposta) {
                if (resposta) {
                    movie = LiberMovies.getInstance().getMovieDetail();

                    titleTv.setText(movie.get("Title"));
                    if (movie.get("Seasons").equals("N/A")) {
                        timeSeasonsTv.setText(movie.get("Runtime"));
                    } else {
                        timeSeasonsTv.setText(movie.get("Seasons"));
                    }
                    iv.putImages(movie.get("Poster"));
                    languageTv.setText(movie.get("Language"));
                    plotTv.setText("Synopsis: " + movie.get("Plot"));
                }
            }
        });
    }

    private void carregaFavorites() {
        try {
            add = LiberMovies.getInstance().getSharedPreferences("ID_MOVIES_FAVORITES", MovieDetailActivity.this);

            if (add.equals(null)){
                add = "";
            }
        }catch (Exception e){
            add = "";
        }
    }

    public static String[] removeElements(String deleteMe, Context context) {
        String favorites = LiberMovies.getInstance().getSharedPreferences("ID_MOVIES_FAVORITES", context);
        String[] input = favorites.split(",");
        if (input != null) {
            List<String> list = new ArrayList<>(Arrays.asList(input));
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(deleteMe)) {
                    list.remove(i);
                }
            }
            return list.toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    /**
     * Usado para mostrar uma msg na tela.
     *
     * @param titulo Titulo em formato String.
     * @param msg Texto em formato de String.
     */
    public void mostraAlerta(String titulo, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailActivity.this);
        builder.setTitle(titulo);
        builder.setMessage(msg);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alerta = builder.create();
        alerta.show();
    }
}
