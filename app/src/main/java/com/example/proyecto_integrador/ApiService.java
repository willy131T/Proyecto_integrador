package com.example.proyecto_integrador;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // 1. Autenticación
    @POST("api/auth/login")
    Call<Map<String, Object>> login(@Body Map<String, String> credenciales);

    @POST("api/auth/registro-paciente")
    Call<Map<String, Object>> registrarPaciente(@Body Map<String, Object> pacienteData);

    @GET("api/auth/doctores")
    Call<Map<String, Object>> obtenerDoctores();

    // 2. Citas
    @GET("api/citas")
    Call<Map<String, Object>> obtenerTodasLasCitas();

    @GET("api/citas/paciente/{nombre}")
    Call<Map<String, Object>> obtenerCitasPaciente(@Path("nombre") String nombre);

    @GET("api/citas/proxima/{nombre}")
    Call<Map<String, Object>> obtenerProximaCita(@Path("nombre") String nombre);

    @POST("api/citas")
    Call<Map<String, Object>> agendarCita(@Body Map<String, Object> citaData);

    @PUT("api/citas/{id}/completar")
    Call<Map<String, Object>> completarCita(@Path("id") int idCita);

    @DELETE("api/citas/{id}")
    Call<Map<String, Object>> cancelarCita(@Path("id") int idCita);

    // 3. Pacientes y Diagnósticos
    @GET("api/pacientes/{nombre}")
    Call<Map<String, Object>> obtenerDetallePaciente(@Path("nombre") String nombre);

    @GET("api/pacientes/{nombre}/diagnosticos")
    Call<Map<String, Object>> obtenerDiagnosticos(@Path("nombre") String nombre);

    @POST("api/pacientes/{nombre}/diagnosticos")
    Call<Map<String, Object>> registrarDiagnostico(@Path("nombre") String nombre, @Body Map<String, Object> diagData);

    // 4. Pagos y Saldos
    @GET("api/pagos/paciente/{nombre}")
    Call<Map<String, Object>> obtenerPagosPaciente(@Path("nombre") String nombre);
}