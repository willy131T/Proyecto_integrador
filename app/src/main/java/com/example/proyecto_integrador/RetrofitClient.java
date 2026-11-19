package com.example.proyecto_integrador;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // NOTA DE CONECTIVIDAD:
    // • Emulador de Android Studio: "http://10.0.2.2:8000/"
    // • Dispositivo físico conectado por WiFi: "http://[IP_DE_TU_PC]:8000/" (Ej: "http://192.168.1.50:8000/")
    // • Servidor en la nube (Render, Railway, etc.): "https://tu-api.onrender.com/"
    private static final String BASE_URL = "https://odontocloud-api.onrender.com/";

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    public static synchronized ApiService getApiService() {
        if (apiService == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(45, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}