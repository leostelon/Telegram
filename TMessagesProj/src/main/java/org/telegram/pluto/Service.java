package org.telegram.pluto;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.telegram.messenger.PlutoAuthTokensHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.pluto.types.Token;
import org.telegram.pluto.types.UserWallet;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import retrofit2.Callback;
import retrofit2.Response;

public class Service {
    private static Token token;
    private static final int currentAccount = UserConfig.selectedAccount;

    static class CreateWalletRequestType {
        private String telegramId;
    }

    static class CreateWalletResponseType extends Token {}

    static class GetWalletResponseType extends UserWallet {}

    private interface ApiService {
        @POST("/wallets")
        Call<CreateWalletResponseType> createWallet(@Body CreateWalletRequestType requestType);

        @GET("/wallets/me")
        Call<GetWalletResponseType> getWallet(@Header("Authorization") String accessToken);
    }

    private static final ApiService apiService = RetrofitClient.getPlutoClient().create(ApiService.class);

    public static void createWallet(String telegramId, int currentAccount) {
        CreateWalletRequestType req = new CreateWalletRequestType();
        req.telegramId = telegramId;

        Call<CreateWalletResponseType> call = apiService.createWallet(req);

        call.enqueue(new Callback<CreateWalletResponseType>() {
            @Override
            public void onResponse(@NonNull Call<CreateWalletResponseType> call, @NonNull Response<CreateWalletResponseType> response) {
                if(response.isSuccessful()) {
                    CreateWalletResponseType res = response.body();
                    if(res != null) {
                        PlutoAuthTokensHelper.saveLogInToken(res);
                        Log.d("Wallet Response", res.accessToken);
                        UserWalletConfig.getInstance(currentAccount).setToken(res);
                        UserWalletConfig.getInstance(currentAccount).saveConfig(false);
                        getWallet(res.accessToken, currentAccount);
                    };
                } else {
                    Log.d("Wallet Failed", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreateWalletResponseType> call, @Nullable Throwable t) {
                Log.d("Wallet Failed", String.valueOf(t));
            }
        });
    }

    private static void getWallet(String accessToken, int currentAccount) {
        Call<GetWalletResponseType> call = apiService.getWallet("Bearer "+ accessToken);

        call.enqueue(new Callback<GetWalletResponseType>() {
            @Override
            public void onResponse(@NonNull Call<GetWalletResponseType> call, @NonNull Response<GetWalletResponseType> response) {
                if(response.isSuccessful()) {
                    GetWalletResponseType res = response.body();
                    if(res != null) {
                        Log.d("My Wallet", res.walletAddress);
                        UserWalletConfig.getInstance(currentAccount).setUserWallet(res);
                        UserWalletConfig.getInstance(currentAccount).saveConfig(true);
                    };
                } else {
                    Log.d("My Wallet Failed", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetWalletResponseType> call, @Nullable Throwable t) {
                Log.d("My Wallet Failed", String.valueOf(t));
            }
        });
    }
}
