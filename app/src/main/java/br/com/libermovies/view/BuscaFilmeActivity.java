package br.com.libermovies.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.teleclinic.bulent.smartimageview.SmartImageViewLayout;

import java.util.HashMap;

import br.com.libermovies.R;
import br.com.libermovies.controller.LiberMovies;
import br.com.libermovies.controller.RequestAPI;
import br.com.libermovies.controller.ServidorListener;

public class BuscaFilmeActivity extends AppCompatActivity {

    private int anterior, proximo;
    private Button btn, btnAnterior, btnFind;
    private EditText edtFind;
    private double paginas;

    private int numberButtons[] = {R.id.btn_1, R.id.btn_2, R.id.btn_3,
            R.id.btn_4, R.id.btn_5, R.id.btn_6,
            R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_10};

    private int numberImage[] = {R.id.iv_Poster1, R.id.iv_Poster2, R.id.iv_Poster3,
            R.id.iv_Poster4, R.id.iv_Poster5, R.id.iv_Poster6,
            R.id.iv_Poster7, R.id.iv_Poster8, R.id.iv_Poster9, R.id.iv_Poster10};

    private int numberName[] = {R.id.tv_Name1, R.id.tv_Name2, R.id.tv_Name3,
            R.id.tv_Name4, R.id.tv_Name5, R.id.tv_Name6,
            R.id.tv_Name7, R.id.tv_Name8, R.id.tv_Name9, R.id.tv_Name10};

    private int numberYear[] = {R.id.tv_Year1, R.id.tv_Year2, R.id.tv_Year3,
            R.id.tv_Year4, R.id.tv_Year5, R.id.tv_Year6,
            R.id.tv_Year7, R.id.tv_Year8, R.id.tv_Year9, R.id.tv_Year10};

    private int numberType[] = {R.id.tv_Type1, R.id.tv_Type2, R.id.tv_Type3,
            R.id.tv_Type4, R.id.tv_Type5, R.id.tv_Type6,
            R.id.tv_Type7, R.id.tv_Type8, R.id.tv_Type9, R.id.tv_Type10};

    private int numberRelatLay[] = {R.id.rl_1, R.id.rl_2, R.id.rl_3,
            R.id.rl_4, R.id.rl_5, R.id.rl_6,
            R.id.rl_7, R.id.rl_8, R.id.rl_9, R.id.rl_10};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_filme);

        btn = findViewById(R.id.btn_Pagina);
        btnAnterior = findViewById(R.id.btn_Anterior);
        btnFind = findViewById(R.id.btn_Find);
        edtFind = findViewById(R.id.et_Search);

        invisibleList();
        invisibleButton(btn);
        invisibleButton(btnAnterior);

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtFind.getText().toString().equals("")){
                    visibleList();
                    RequestAPI.goGetMoviesSearch(edtFind.getText().toString(), 0, BuscaFilmeActivity.this, new ServidorListener() {
                        @Override
                        public void recebeuResposta(String msgErro, Boolean resposta) {
                            if (resposta){
                                paginas = Double.parseDouble(LiberMovies.getInstance().getSharedPreferences("TOTAL_PAGES", BuscaFilmeActivity.this));
                                carrega();
                            }
                        }
                    });
                }
            }
        });

        for(int id:numberButtons) {
            View v = findViewById(id);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text=(String)((Button)v).getText();

                    Intent it = new Intent(BuscaFilmeActivity.this, MovieDetailActivity.class);
                    it.putExtra("IdMovie", text);
                    it.putExtra("ISFAVORITES", false);
                    startActivity(it);
                }
            });
        }

        proximo = 2;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (proximo<=paginas){
                    clearList();
                    anterior = proximo-1;
                    RequestAPI.goGetMoviesSearch(edtFind.getText().toString(), proximo, BuscaFilmeActivity.this, new ServidorListener() {
                        @Override
                        public void recebeuResposta(String msgErro, Boolean resposta) {
                            if (resposta){
                                proximo++;

                                if (anterior>=0){
                                    visibleButton(btnAnterior);
                                }else{
                                    invisibleButton(btnAnterior);
                                }

                                carrega();
                            }
                        }
                    });
                }else{
                    invisibleButton(btn);
                }
            }
        });

        btnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (anterior<=paginas){
                    clearList();
                    proximo = anterior+1;
                    RequestAPI.goGetMoviesSearch(edtFind.getText().toString(), anterior, BuscaFilmeActivity.this, new ServidorListener() {
                        @Override
                        public void recebeuResposta(String msgErro, Boolean resposta) {
                            if (resposta){
                                anterior--;

                                if (proximo>=0){
                                    visibleButton(btn);
                                }else{
                                    invisibleButton(btn);
                                }

                                carrega();
                            }
                        }
                    });
                }else{
                    invisibleButton(btnAnterior);
                }
            }
        });
    }

    private void carrega(){
        int z = LiberMovies.getInstance().getMovies().size() - 1;

        for(int i=0; i<=z; i++){
            TextView name = findViewById(numberName[i]);
            TextView year = findViewById(numberYear[i]);
            TextView type = findViewById(numberType[i]);
            SmartImageViewLayout iv = findViewById(numberImage[i]);
            Button btn = findViewById(numberButtons[i]);

            HashMap<String, String> movie = LiberMovies.getInstance().getMovies().get(i);

            name.setText(movie.get("Title"));
            year.setText(movie.get("Year"));
            type.setText(movie.get("Type").toUpperCase());

            iv.putImages(movie.get("Poster"));

            btn.setText(movie.get("imdbID"));

        }

        int rly = 1;
        for (int a=0; a<=9; a++){
            RelativeLayout rl = findViewById(numberRelatLay[a]);

            if (rly > z+1){
                rl.setBackgroundResource(R.color.colorNull);
                invisibleButton(btn);
            }
            rly++;
        }

        ScrollView sw = findViewById(R.id.sv_Search);
        sw.scrollTo(0,0);

        if (paginas >= 1){
            btn.setText("Próxima");
            btnAnterior.setText("Anterior");
            visibleButton(btn);
        }else{
            invisibleButton(btn);
            invisibleButton(btnAnterior);
        }
    }

    private void clearList(){
        visibleList();

        for(int i=0; i<=9; i++){
            TextView name = findViewById(numberName[i]);
            TextView year = findViewById(numberYear[i]);
            TextView type = findViewById(numberType[i]);
            SmartImageViewLayout iv = findViewById(numberImage[i]);
            Button btn = findViewById(numberButtons[i]);

            name.setText("");
            year.setText("");
            type.setText("");
            iv.putImages("");
            btn.setText("");

        }
    }

    @SuppressLint("ResourceAsColor")
    private void invisibleList(){

        for(int i=0; i<=9; i++){
            RelativeLayout rl = findViewById(numberRelatLay[i]);
            SmartImageViewLayout iv = findViewById(numberImage[i]);
            TextView name = findViewById(numberName[i]);
            TextView year = findViewById(numberYear[i]);
            TextView type = findViewById(numberType[i]);

            rl.setBackgroundResource(R.color.colorNull);
            iv.setVisibility(View.INVISIBLE);
            name.setBackgroundResource(R.color.colorNull);
            year.setBackgroundResource(R.color.colorNull);
            type.setBackgroundResource(R.color.colorNull);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void visibleList(){

        for(int i=0; i<=9; i++){
            RelativeLayout rl = findViewById(numberRelatLay[i]);
            SmartImageViewLayout iv = findViewById(numberImage[i]);

            rl.setBackgroundResource(R.drawable.rounded_relative_layout);
            iv.setVisibility(View.VISIBLE);
        }
    }

    private void invisibleButton(Button btns){
        btns.setBackgroundResource(R.color.colorNull);
        btns.setTextColor(00000000);
    }

    private void visibleButton(Button btns){
        btns.setBackgroundResource(R.drawable.rounded_button);
        btns.setTextColor(Color.BLUE);
    }

    /**
     * Usado para mostrar uma msg na tela.
     *
     * @param titulo Titulo em formato String.
     * @param msg Texto em formato de String.
     */
    public void mostraAlerta(String titulo, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(BuscaFilmeActivity.this);
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
