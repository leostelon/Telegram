package org.telegram.pluto;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.FileLog;
import org.telegram.pluto.types.Token;
import org.telegram.pluto.types.UserWallet;

public class UserWalletConfig extends BaseController {
    static Gson gson =  new Gson();
    public final static int MAX_ACCOUNT_COUNT = 4;
    public Token token;
    public UserWallet userWallet;

    private static class User extends UserWallet {}

    public UserWalletConfig(int num) {
        super(num);
    }

    public SharedPreferences getPreferences() {
        if (currentAccount == 0) {
            return ApplicationLoader.applicationContext.getSharedPreferences("userwalletconfing", Context.MODE_PRIVATE);
        } else {
            return ApplicationLoader.applicationContext.getSharedPreferences("userwalletconfig" + currentAccount, Context.MODE_PRIVATE);
        }
    }

    private static volatile UserWalletConfig[] Instance = new UserWalletConfig[UserWalletConfig.MAX_ACCOUNT_COUNT];
    public static UserWalletConfig getInstance(int num) {
        UserWalletConfig localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (UserWalletConfig.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new UserWalletConfig(num);
                }
            }
        }
        return localInstance;
    }

    public void loadConfig() {
        SharedPreferences preferences = getPreferences();

        String string = preferences.getString("userWallet", null);
        if (string != null) {
            userWallet = gson.fromJson(string, User.class);
        }

        string = preferences.getString("token", null);
        if (string != null) {
            token = gson.fromJson(string, Token.class);
        }
    }

    public void saveConfig(boolean newUser) {
        SharedPreferences.Editor editor = getPreferences().edit();

        try {
            if(newUser) {
                String userWalletString = gson.toJson(userWallet);
                editor.putString("userWallet", userWalletString);
            }
            String tokenString = gson.toJson(token);
            editor.putString("token", tokenString);
            editor.apply();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setUserWallet(UserWallet newUserWallet) {
        Log.d("New Wallet", newUserWallet.toString());
        userWallet = newUserWallet;
    }

    public void setToken(Token newToken) {
        Log.d("New Wallet Token", newToken.accessToken);
        token = newToken;
    }
}
