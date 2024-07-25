package org.telegram.pluto;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import retrofit2.Callback;
import retrofit2.Response;

public class User {
    static class CreateWalletRequestType {
        private String telegramId;
    }

    static class CreateWalletResponseType {
        private String refreshToken;
        private String accessToken;
    }

    private interface ApiService {
        @POST("/wallets")
        Call<CreateWalletResponseType> createWallet(@Body CreateWalletRequestType requestType);
    }

    public static void createWallet(String telegramId) {
        ApiService apiService = RetrofitClient.getClient("http://192.168.68.201:3000").create(ApiService.class);

        CreateWalletRequestType req = new CreateWalletRequestType();
        req.telegramId = telegramId;

        Call<CreateWalletResponseType> call = apiService.createWallet(req);

        call.enqueue(new Callback<CreateWalletResponseType>() {
            @Override
            public void onResponse(@NonNull Call<CreateWalletResponseType> call, @NonNull Response<CreateWalletResponseType> response) {
                if(response.isSuccessful()) {
                    CreateWalletResponseType res = response.body();
                    if(res != null) Log.d("Wallet Response", res.toString());
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
}
