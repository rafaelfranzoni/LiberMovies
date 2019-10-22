package br.com.libermovies.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import br.com.libermovies.R;
import br.com.libermovies.controller.LiberMovies;

public class MainActivity extends AppCompatActivity {

    private Button btnSearch, btnFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFavorite = findViewById(R.id.btn_Favorites_Movies);
        btnSearch = findViewById(R.id.btn_SearchMovies);

        LiberMovies.iniciar(MainActivity.this);

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, MovieDetailActivity.class);
                it.putExtra("ISFAVORITES", true);
                startActivity(it);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BuscaFilmeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
