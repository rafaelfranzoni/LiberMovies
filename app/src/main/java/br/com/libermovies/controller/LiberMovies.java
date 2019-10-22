package br.com.libermovies.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

public class LiberMovies {

    private static LiberMovies instance;
    private Activity activity;
    private ArrayList<HashMap<String, String>> movies;
    private HashMap<String, String> movieDetail;

    public static LiberMovies getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Precisa inicializar o ToSalvo antes de chamar este método");
        }
        return instance;
    }

    public static LiberMovies iniciar(Activity acty) {
        instance = new LiberMovies(acty);
        return getInstance();
    }

    private LiberMovies(Activity acty) {
        this.activity = acty;
    }

    public ArrayList<HashMap<String, String>> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<HashMap<String, String>> movies) {
        this.movies = movies;
    }

    public HashMap<String, String> getMovieDetail() {
        return movieDetail;
    }

    public void setMovieDetail(HashMap<String, String> movieDetail) {
        this.movieDetail = movieDetail;
    }

    public void setSharedPreferences(String tag, String info, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("LiberMovies", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(tag, info);
        editor.commit();
    }

    public String getSharedPreferences(String tag, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("LiberMovies", 0);
        String descricao = prefs.getString(tag, null);
        return descricao;
    }

    public void clearSharedPreferences(String tag, Context context){
        SharedPreferences preferences = context.getSharedPreferences("LiberMovies", 0);
        preferences.edit().remove(tag).commit();
    }
}
