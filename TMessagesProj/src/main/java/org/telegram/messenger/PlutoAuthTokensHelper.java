package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.telegram.pluto.types.Token;

import java.util.ArrayList;

public class PlutoAuthTokensHelper {
    static Gson gson =  new Gson();

    public static ArrayList<Token> getSavedLogInTokens() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("saved_pluto_tokens", Context.MODE_PRIVATE);
        int count = preferences.getInt("count", 0);

        if (count == 0) {
            return null;
        }

        ArrayList<Token> tokens = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String value = preferences.getString("log_in_token_" + i, "");
            try {
                Token token = gson.fromJson(value, Token.class);
                if (token != null) {
                    tokens.add((Token) token);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        return tokens;
    }

    public static void saveLogInToken(Token token) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("savePlutoAuthToken " + gson.toJson(token));
        }
        ArrayList<Token> tokens = getSavedLogInTokens();
        if (tokens == null) {
            tokens = new ArrayList<>();
        }
        tokens.add(0, token);
        saveLogInTokens(tokens);
    }

    private static void saveLogInTokens(@NonNull ArrayList<Token> tokens) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("saved_pluto_tokens", Context.MODE_PRIVATE);
        ArrayList<Token> activeTokens = new ArrayList<>();
        preferences.edit().clear().apply();
        for (int i = 0; i < Math.min(20, tokens.size()); i++) {
            activeTokens.add(tokens.get(i));
        }
        if (!activeTokens.isEmpty()) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("count", activeTokens.size());
            for (int i = 0; i < activeTokens.size(); i++) {
                editor.putString("log_in_token_" + i, gson.toJson(activeTokens.get(i)));
            }
            editor.apply();
            BackupAgent.requestBackup(ApplicationLoader.applicationContext);
        }
    }

    public static void clearLogInTokens() {
        ApplicationLoader.applicationContext.getSharedPreferences("saved_pluto_tokens", Context.MODE_PRIVATE).edit().clear().apply();
    }
}
