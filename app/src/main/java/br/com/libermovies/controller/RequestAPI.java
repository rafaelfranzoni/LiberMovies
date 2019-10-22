package br.com.libermovies.controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestAPI {

    private static ArrayList<HashMap<String, String>> movies;

    public static void goGetMoviesSearch(String name, int page, final Context context, final ServidorListener listener)  {

        movies = new ArrayList<>();

        String aux = "";

        if (page>=2){
            aux = "&page="+page;
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        final String url = "http://www.omdbapi.com/?apikey=823b44c4&r=json&s="+name+aux;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    double paginas = Integer.parseInt(response.getString("totalResults"))/10.0;
                    paginas = Math.round(paginas);

                    LiberMovies.getInstance().setSharedPreferences("TOTAL_PAGES", String.valueOf(paginas), context);

                    JSONArray search = response.getJSONArray("Search");

                    // loop através de todos os contatos
                    for (int i = 0; i < search.length(); i++) {

                        JSONObject c = null;
                        HashMap<String, String> aux = new HashMap<>();
                        try {
                            c = search.getJSONObject(i);

                            aux.put("Title", c.getString("Title"));
                            aux.put("Year", c.getString("Year"));
                            aux.put("imdbID", c.getString("imdbID"));
                            aux.put("Type", c.getString("Type"));
                            aux.put("Poster", c.getString("Poster"));

                            movies.add(aux);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    LiberMovies.getInstance().setMovies(movies);

                    listener.recebeuResposta(null, true);

                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.recebeuResposta("Erro ao ler o arquivo JSON", false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String responseBody = null;
                String message = null;
                try {
                    responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    message = data.optString("erro");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listener.recebeuResposta("Status code: "+error.networkResponse.statusCode+
                        "\nErro: "+message, false);
            }
        });
        queue.add(jsonObjectRequest);
    }

    public static void goGetMovieDetail(String id, final Context context, final ServidorListener listener)  {

        final HashMap<String, String> movie = new HashMap<>();

        RequestQueue queue = Volley.newRequestQueue(context);

        final String url = "http://www.omdbapi.com/?apikey=823b44c4&r=json&i="+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    movie.put("Poster", response.getString("Poster"));
                    movie.put("Plot", response.getString("Plot"));
                    movie.put("Title", response.getString("Title"));
                    movie.put("Runtime", response.getString("Runtime"));
                    movie.put("Language", response.getString("Language"));
                    if (response.getString("Type").equals("series")){
                        movie.put("Seasons", response.getString("totalSeasons"));
                    }else {
                        movie.put("Seasons", "N/A");
                    }

                    LiberMovies.getInstance().setMovieDetail(movie);

                    listener.recebeuResposta(null, true);

                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.recebeuResposta("Erro ao ler o arquivo JSON", false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String responseBody = null;
                String message = null;
                try {
                    responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    message = data.optString("erro");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listener.recebeuResposta("Status code: "+error.networkResponse.statusCode+
                        "\nErro: "+message, false);
            }
        });
        queue.add(jsonObjectRequest);
    }
}
