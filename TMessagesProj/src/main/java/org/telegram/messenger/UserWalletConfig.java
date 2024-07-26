package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;

public class UserWalletConfig extends BaseController {
    static Gson gson =  new Gson();
    public final static int MAX_ACCOUNT_COUNT = 4;
    public UserWallet userWallet;

    public UserWalletConfig(int num) {
        super(num);
    }

    public static class UserWallet {
        public String walletAddress;

        public UserWallet(String walletAddress) {
            this.walletAddress = walletAddress;
        }
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

    public void saveConfig(boolean newUser) {
        SharedPreferences.Editor editor = getPreferences().edit();

        try {
            if(newUser) {
                String string = gson.toJson(userWallet);
                editor.putString("userWallet", string);
            }
            editor.apply();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setUserWallet(UserWallet newUserWallet) {
        Log.d("New Wallet", newUserWallet.toString());
        userWallet = newUserWallet;
    }
}
