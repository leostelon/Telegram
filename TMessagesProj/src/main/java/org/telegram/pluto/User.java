package org.telegram.pluto;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.telegram.messenger.PlutoAuthTokensHelper;
import org.telegram.messenger.UserWalletConfig;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import retrofit2.Callback;
import retrofit2.Response;

public class User {
    private static PlutoAuthTokensHelper.Token token;
    private static final ApiService apiService = RetrofitClient.getClient("http://192.168.68.201:3000").create(ApiService.class);

    static class CreateWalletRequestType {
        private String telegramId;
    }

    static class CreateWalletResponseType {
        private String refreshToken;
        private String accessToken;
    }

    static class GetWalletResponseType {
        private String id;
        private String telegramId;
        private String walletAddress;
        private String createdAt;
    }

    private interface ApiService {
        @POST("/wallets")
        Call<CreateWalletResponseType> createWallet(@Body CreateWalletRequestType requestType);

        @GET("/wallets/me")
        Call<GetWalletResponseType> getWallet(@Header("Authorization") String accessToken);
    }

    public static void createWallet(String telegramId,int currentAccount) {
        CreateWalletRequestType req = new CreateWalletRequestType();
        req.telegramId = telegramId;

        Call<CreateWalletResponseType> call = apiService.createWallet(req);

        call.enqueue(new Callback<CreateWalletResponseType>() {
            @Override
            public void onResponse(@NonNull Call<CreateWalletResponseType> call, @NonNull Response<CreateWalletResponseType> response) {
                if(response.isSuccessful()) {
                    CreateWalletResponseType res = response.body();
                    if(res != null) {
                        token = new PlutoAuthTokensHelper.Token(res.refreshToken, res.accessToken);
                        PlutoAuthTokensHelper.saveLogInToken(token);
                        Log.d("Wallet Response", res.toString());
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
                        Log.d("My Wallet", res.toString());
                        UserWalletConfig.UserWallet userWallet = new UserWalletConfig.UserWallet(res.walletAddress);
                        UserWalletConfig.getInstance(currentAccount).setUserWallet(userWallet);
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
