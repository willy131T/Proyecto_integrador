package com.example.proyecto_integrador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ConsultorioDental.db";
    private static final int DATABASE_VERSION = 1;

    // ==========================================
    // 1. TABLA: PACIENTES (Req. 6)
    // ==========================================
    public static final String TABLE_PACIENTES = "pacientes";
    public static final String COL_PACIENTE_ID = "id_paciente";
    public static final String COL_PACIENTE_NOMBRE = "nombre";
    public static final String COL_PACIENTE_TELEFONO = "telefono";
    public static final String COL_PACIENTE_CORREO = "correo"; // Útil para el Req. 5 (Recordatorios)

    private static final String CREATE_TABLE_PACIENTES = "CREATE TABLE " + TABLE_PACIENTES + " (" +
            COL_PACIENTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_PACIENTE_NOMBRE + " TEXT NOT NULL, " +
            COL_PACIENTE_TELEFONO + " TEXT, " +
            COL_PACIENTE_CORREO + " TEXT);";

    // ==========================================
    // 2. TABLA: CITAS (Req. 1, 4, 9)
    // ==========================================
    public static final String TABLE_CITAS = "citas";
    public static final String COL_CITA_ID = "id_cita";
    public static final String COL_CITA_ID_PACIENTE = "id_paciente";
    public static final String COL_CITA_FECHA = "fecha";
    public static final String COL_CITA_HORA = "hora";
    public static final String COL_CITA_ESTADO = "estado"; // "Pendiente", "Atendida", "Cancelada"

    private static final String CREATE_TABLE_CITAS = "CREATE TABLE " + TABLE_CITAS + " (" +
            COL_CITA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_CITA_ID_PACIENTE + " INTEGER, " +
            COL_CITA_FECHA + " TEXT NOT NULL, " +
            COL_CITA_HORA + " TEXT NOT NULL, " +
            COL_CITA_ESTADO + " TEXT NOT NULL);";

    // ==========================================
    // 3. TABLA: HISTORIAL CLÍNICO (Req. 2, 3, 7)
    // ==========================================
    public static final String TABLE_HISTORIAL = "historial_clinico";
    public static final String COL_HIST_ID = "id_historial";
    public static final String COL_HIST_ID_PACIENTE = "id_paciente";
    public static final String COL_HIST_DIAGNOSTICO = "diagnostico";
    public static final String COL_HIST_TRATAMIENTO = "tratamiento";
    public static final String COL_HIST_ODONTOGRAMA = "datos_odontograma";

    private static final String CREATE_TABLE_HISTORIAL = "CREATE TABLE " + TABLE_HISTORIAL + " (" +
            COL_HIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_HIST_ID_PACIENTE + " INTEGER, " +
            COL_HIST_DIAGNOSTICO + " TEXT, " +
            COL_HIST_TRATAMIENTO + " TEXT, " +
            COL_HIST_ODONTOGRAMA + " TEXT);";

    // ==========================================
    // 4. TABLA: PAGOS (Req. 8)
    // ==========================================
    public static final String TABLE_PAGOS = "pagos";
    public static final String COL_PAGO_ID = "id_pago";
    public static final String COL_PAGO_ID_CITA = "id_cita";
    public static final String COL_PAGO_MONTO = "monto";
    public static final String COL_PAGO_FECHA = "fecha_pago";

    private static final String CREATE_TABLE_PAGOS = "CREATE TABLE " + TABLE_PAGOS + " (" +
            COL_PAGO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_PAGO_ID_CITA + " INTEGER, " +
            COL_PAGO_MONTO + " REAL, " +
            COL_PAGO_FECHA + " TEXT);";

    // ==========================================
    // 5. TABLA: INVENTARIO (Req. 10)
    // ==========================================
    public static final String TABLE_INVENTARIO = "inventario";
    public static final String COL_INV_ID = "id_material";
    public static final String COL_INV_NOMBRE = "nombre_material";
    public static final String COL_INV_CANTIDAD = "cantidad";

    private static final String CREATE_TABLE_INVENTARIO = "CREATE TABLE " + TABLE_INVENTARIO + " (" +
            COL_INV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_INV_NOMBRE + " TEXT NOT NULL, " +
            COL_INV_CANTIDAD + " INTEGER NOT NULL);";
    //
    // ==========================================
    // 5. Tabla Usuarios
    // ==========================================
    public static final String TABLE_USUARIOS = "Usuarios";
    public static final String COLUMN_ID_USUARIO = "id_usuario";
    public static final String COLUMN_USUARIO = "usuario";
    public static final String COLUMN_PASSWORD = "password"; // En un entorno real debería ir encriptada
    public static final String COLUMN_ROL = "rol";


    String CREATE_TABLE_USUARIOS = "CREATE TABLE " + TABLE_USUARIOS + "("
            + COLUMN_ID_USUARIO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USUARIO + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_ROL + " TEXT" + ")";

    // ==========================================
    // MÉTODOS DEL CICLO DE VIDA DE LA BD
    // ==========================================
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creamos todas las tablas estructuradas
        db.execSQL(CREATE_TABLE_PACIENTES);
        db.execSQL(CREATE_TABLE_CITAS);
        db.execSQL(CREATE_TABLE_HISTORIAL);
        db.execSQL(CREATE_TABLE_PAGOS);
        db.execSQL(CREATE_TABLE_INVENTARIO);
        db.execSQL(CREATE_TABLE_USUARIOS);

        // Insertar el primer superusuario (Doctor) por defecto
        ContentValues values = new ContentValues();
        values.put(COLUMN_USUARIO, "admin_doctor");
        values.put(COLUMN_PASSWORD, "12345"); // Contraseña temporal
        values.put(COLUMN_ROL, "superusuario");

        db.insert(TABLE_USUARIOS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACIENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORIAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);

    }

    // ==========================================
    // MÉTODOS CRUD (OPERACIONES FUNCIONALES)
    // ==========================================

    // Req. 6: Registrar paciente
    public long insertarPaciente(String nombre, String telefono, String correo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_PACIENTE_NOMBRE, nombre);
        valores.put(COL_PACIENTE_TELEFONO, telefono);
        valores.put(COL_PACIENTE_CORREO, correo);
        return db.insert(TABLE_PACIENTES, null, valores);
    }

    // Req. 1 y 4: Agendar Cita
    public long agendarCita(int idPaciente, String fecha, String hora) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_CITA_ID_PACIENTE, idPaciente);
        valores.put(COL_CITA_FECHA, fecha);
        valores.put(COL_CITA_HORA, hora);
        valores.put(COL_CITA_ESTADO, "Pendiente"); // Estado por defecto
        return db.insert(TABLE_CITAS, null, valores);
    }

    // Req. 1: Modificar / Cancelar Cita (Actualizando el estado)
    public boolean actualizarEstadoCita(int idCita, String nuevoEstado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_CITA_ESTADO, nuevoEstado); // "Cancelada" o "Atendida"
        int filasAfectadas = db.update(TABLE_CITAS, valores, COL_CITA_ID + " = ?", new String[]{String.valueOf(idCita)});
        return filasAfectadas > 0;
    }

    // Req. 3 y 7: Registrar Diagnóstico, Tratamiento y Odontograma
    public long registrarHistorial(int idPaciente, String diagnostico, String tratamiento, String datosOdontograma) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_HIST_ID_PACIENTE, idPaciente);
        valores.put(COL_HIST_DIAGNOSTICO, diagnostico);
        valores.put(COL_HIST_TRATAMIENTO, tratamiento);
        valores.put(COL_HIST_ODONTOGRAMA, datosOdontograma);
        return db.insert(TABLE_HISTORIAL, null, valores);
    }

    // Req. 8: Registrar un pago para generar comprobante
    public long registrarPago(int idCita, double monto, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_PAGO_ID_CITA, idCita);
        valores.put(COL_PAGO_MONTO, monto);
        valores.put(COL_PAGO_FECHA, fecha);
        return db.insert(TABLE_PAGOS, null, valores);
    }

    // Req. 10: Registrar materiales
    public long insertarMaterial(String nombre, int cantidad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_INV_NOMBRE, nombre);
        valores.put(COL_INV_CANTIDAD, cantidad);
        return db.insert(TABLE_INVENTARIO, null, valores);
    }

    //Validacion usuarios
    public String validarLogin(String usuario, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String rol = null;

        // Consultar el usuario
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ROL + " FROM " + TABLE_USUARIOS +
                        " WHERE " + COLUMN_USUARIO + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{usuario, password});

        if (cursor != null && cursor.moveToFirst()) {
            rol = cursor.getString(0); // Obtiene el rol (ej. "superusuario")
            cursor.close();
        }

        return rol; // Si retorna null, el usuario o contraseña son incorrectos
    }

}