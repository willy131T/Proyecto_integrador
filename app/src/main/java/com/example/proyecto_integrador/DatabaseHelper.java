package com.example.proyecto_integrador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ConsultorioDental.db";
    private static final int DATABASE_VERSION = 4; // Versión actualizada para recrear limpio

    // Nombres de tablas
    public static final String TABLE_USUARIOS = "Usuarios";
    public static final String TABLE_PACIENTES = "pacientes";
    public static final String TABLE_CITAS = "citas";
    public static final String TABLE_INVENTARIO = "inventario";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tabla de Usuarios (Login y Registro)
        db.execSQL("CREATE TABLE " + TABLE_USUARIOS + " (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "usuario TEXT UNIQUE, " +
                "password TEXT, " +
                "rol TEXT, " +
                "alergias TEXT)");

        // 2. Tabla de Pacientes / Historial Clínico
        db.execSQL("CREATE TABLE " + TABLE_PACIENTES + " (" +
                "id_paciente INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "edad INTEGER, " +
                "telefono TEXT, " +
                "correo TEXT, " +
                "alergias TEXT, " +
                "tratamiento TEXT)");

        // 3. Tabla de Citas
        db.execSQL("CREATE TABLE " + TABLE_CITAS + " (" +
                "id_cita INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_paciente INTEGER, " +
                "fecha TEXT NOT NULL, " +
                "hora TEXT NOT NULL, " +
                "motivo TEXT, " +
                "estado TEXT NOT NULL)");

        // 4. Tabla de Inventario
        db.execSQL("CREATE TABLE " + TABLE_INVENTARIO + " (" +
                "id_material INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "cantidad INTEGER NOT NULL, " +
                "categoria TEXT)");

        // Insertar datos por defecto
        insertarDatosDePrueba(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACIENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTARIO);
        onCreate(db);
    }

    private void insertarDatosDePrueba(SQLiteDatabase db) {
        // --- USUARIO ADMINISTRADOR ---
        ContentValues valuesUser = new ContentValues();
        valuesUser.put("nombre", "Doctor Admin");
        valuesUser.put("usuario", "admin_doctor");
        valuesUser.put("password", "12345");
        valuesUser.put("rol", "dentista");
        valuesUser.put("alergias", "Ninguna");
        db.insert(TABLE_USUARIOS, null, valuesUser);

        // --- USUARIO PACIENTE DE PRUEBA ---
        valuesUser.clear();
        valuesUser.put("nombre", "William Eduardo Antonio Marcelo");
        valuesUser.put("usuario", "william_p");
        valuesUser.put("password", "12345");
        valuesUser.put("rol", "paciente");
        valuesUser.put("alergias", "Ninguna");
        db.insert(TABLE_USUARIOS, null, valuesUser);

        // --- PACIENTES INICIALES ---
        db.execSQL("INSERT INTO " + TABLE_PACIENTES + " (nombre, edad, telefono, correo, alergias, tratamiento) VALUES " +
                "('William Eduardo Antonio Marcelo', 19, '5551234567', 'william@test.com', 'Ninguna', 'Limpieza general'), " +
                "('Andrés', 20, '5559876543', 'andres@test.com', 'Ninguna', 'Resina'), " +
                "('Sofi', 19, '5554567890', 'sofi@test.com', 'Penicilina', 'Ortodoncia');");

        // --- INVENTARIO INICIAL ---
        db.execSQL("INSERT INTO " + TABLE_INVENTARIO + " (nombre, cantidad, categoria) VALUES " +
                "('Anestesia Local (Cárpules)', 50, 'Medicamentos'), " +
                "('Resina Compuesta (Jeringas)', 30, 'Materiales'), " +
                "('Guantes de Látex (Cajas)', 15, 'Desechables');");
    }

    // ==========================================
    // MÉTODOS DE AUTENTICACIÓN
    // ==========================================
    public boolean insertarUsuario(String nombre, String usuario, String password, String rol, String alergias) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("usuario", usuario);
        values.put("password", password);
        values.put("rol", rol);
        values.put("alergias", alergias);
        long result = db.insert(TABLE_USUARIOS, null, values);
        return result != -1;
    }

    public String validarLogin(String usuario, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String rol = null;
        Cursor cursor = db.rawQuery("SELECT rol FROM Usuarios WHERE usuario=? AND password=?",
                new String[]{usuario, password});
        if (cursor != null && cursor.moveToFirst()) {
            rol = cursor.getString(0);
            cursor.close();
        }
        return rol;
    }

    // ==========================================
    // MÉTODOS DE CITAS
    // ==========================================
    public boolean insertarCita(String fecha, String hora, String motivo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fecha", fecha);
        values.put("hora", hora);
        values.put("motivo", motivo);
        values.put("estado", "Pendiente");
        long resultado = db.insert(TABLE_CITAS, null, values);
        return resultado != -1;
    }

    public Cursor obtenerCitas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CITAS, null);
    }

    public boolean eliminarCita(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete(TABLE_CITAS, "id_cita = ?", new String[]{String.valueOf(id)});
        return resultado > 0;
    }

    // ==========================================
    // MÉTODOS DE PACIENTES / HISTORIAL
    // ==========================================
    public boolean agregarHistorial(String nombre, int edad, String alergias, String tratamiento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("edad", edad);
        valores.put("alergias", alergias);
        valores.put("tratamiento", tratamiento);
        long resultado = db.insert(TABLE_PACIENTES, null, valores);
        return resultado != -1;
    }

    public Cursor obtenerPacientes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PACIENTES, null);
    }

    // ==========================================
    // MÉTODOS DE INVENTARIO
    // ==========================================
    public boolean insertarMaterial(String nombre, int cantidad, String categoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("cantidad", cantidad);
        valores.put("categoria", categoria);
        long resultado = db.insert(TABLE_INVENTARIO, null, valores);
        return resultado != -1;
    }

    public Cursor obtenerInventario() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INVENTARIO, null);
    }
}