package br.com.libermovies.controller;

import java.util.ArrayList;
import java.util.HashMap;

public interface ListenerFavorites {

    void recebeuRespostaFavorites(ArrayList<HashMap<String, String>> resposta);
}
