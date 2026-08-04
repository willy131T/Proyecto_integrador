package com.example.proyecto_integrador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Nombre y versión de la base de datos
    private static final String DATABASE_NAME = "ConsultorioDental.db";
    private static final int DATABASE_VERSION = 3; // Subimos versión para forzar la recreación con datos de prueba

    // ==========================================
    // 1. TABLA: PACIENTES (Req. 6)
    // ==========================================
    public static final String TABLE_PACIENTES = "pacientes";
    public static final String COL_PACIENTE_ID = "id_paciente";
    public static final String COL_PACIENTE_NOMBRE = "nombre";
    public static final String COL_PACIENTE_TELEFONO = "telefono";
    public static final String COL_PACIENTE_CORREO = "correo";

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
    public static final String COL_CITA_ESTADO = "estado";

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

    // ==========================================
    // 6. TABLA: USUARIOS (Adaptada para Login/Registro)
    // ==========================================
    public static final String TABLE_USUARIOS = "Usuarios";
    public static final String COLUMN_ID_USUARIO = "id_usuario";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_USUARIO = "usuario";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROL = "rol";
    public static final String COLUMN_ALERGIAS = "alergias";

    private static final String CREATE_TABLE_USUARIOS = "CREATE TABLE " + TABLE_USUARIOS + "("
            + COLUMN_ID_USUARIO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NOMBRE + " TEXT,"
            + COLUMN_USUARIO + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_ROL + " TEXT,"
            + COLUMN_ALERGIAS + " TEXT" + ")";

    // ==========================================
    // MÉTODOS DEL CICLO DE VIDA DE LA BD
    // ==========================================
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Creamos todas las tablas
        db.execSQL(CREATE_TABLE_PACIENTES);
        db.execSQL(CREATE_TABLE_CITAS);
        db.execSQL(CREATE_TABLE_HISTORIAL);
        db.execSQL(CREATE_TABLE_PAGOS);
        db.execSQL(CREATE_TABLE_INVENTARIO);
        // Crear tabla de Inventario
        String tablaInventario = "CREATE TABLE INVENTARIO (" +
                "ID_MATERIAL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NOMBRE TEXT, " +
                "CANTIDAD INTEGER, " +
                "CATEGORIA TEXT)";
        db.execSQL(tablaInventario);
        db.execSQL(CREATE_TABLE_USUARIOS);
        // Crear tabla de Historial Clínico / Pacientes
        String tablaPacientes = "CREATE TABLE PACIENTES (" +
                "ID_PACIENTE INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NOMBRE TEXT, " +
                "EDAD INTEGER, " +
                "ALERGIAS TEXT, " +
                "TRATAMIENTO TEXT)";
        db.execSQL(tablaPacientes);

        // 2. Insertamos Datos de Prueba (Seed Data)
        insertarDatosDePrueba(db);
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
    // MÉTODO PRIVADO PARA DATOS POR DEFECTO
    // ==========================================
    private void insertarDatosDePrueba(SQLiteDatabase db) {
        // --- USUARIOS POR DEFECTO ---
        // Dentista (Admin)
        ContentValues valuesUser = new ContentValues();
        valuesUser.put(COLUMN_NOMBRE, "Doctor Admin");
        valuesUser.put(COLUMN_USUARIO, "admin_doctor");
        valuesUser.put(COLUMN_PASSWORD, "12345");
        valuesUser.put(COLUMN_ROL, "dentista");
        valuesUser.put(COLUMN_ALERGIAS, "Ninguna");
        db.insert(TABLE_USUARIOS, null, valuesUser);

        // Paciente de prueba
        valuesUser.clear();
        valuesUser.put(COLUMN_NOMBRE, "William Eduardo Antonio Marcelo");
        valuesUser.put(COLUMN_USUARIO, "william_p");
        valuesUser.put(COLUMN_PASSWORD, "12345");
        valuesUser.put(COLUMN_ROL, "paciente");
        valuesUser.put(COLUMN_ALERGIAS, "Ninguna");
        db.insert(TABLE_USUARIOS, null, valuesUser);

        // --- PACIENTES EN LA TABLA CLINICA ---
        String insertPacientes = "INSERT INTO " + TABLE_PACIENTES + " (nombre, telefono, correo) VALUES " +
                "('William Eduardo Antonio Marcelo', '5551234567', 'william@test.com'), " +
                "('Andrés', '5559876543', 'andres@test.com'), " +
                "('Sofi', '5554567890', 'sofi@test.com'), " +
                "('Noah', '5551112233', 'noah@test.com'), " +
                "('Vale', '5554445566', 'vale@test.com');";
        db.execSQL(insertPacientes);

        // --- INVENTARIO Y MEDICAMENTOS ---
        String insertInventario = "INSERT INTO " + TABLE_INVENTARIO + " (nombre_material, cantidad) VALUES " +
                "('Anestesia Local (Cárpules)', 50), " +
                "('Resina Compuesta (Jeringas)', 30), " +
                "('Amalgama (Cápsulas)', 20), " +
                "('Guantes de Látex (Cajas)', 15), " +
                "('Ibuprofeno 400mg (Cajas)', 20), " +
                "('Amoxicilina 500mg (Cajas)', 15), " +
                "('Agujas Dentales', 100), " +
                "('Gasas Estériles (Paquetes)', 40);";
        db.execSQL(insertInventario);
    }

    // ==========================================
    // MÉTODOS DE AUTENTICACIÓN Y REGISTRO (USUARIOS)
    // ==========================================

    public boolean insertarUsuario(String nombre, String usuario, String password, String rol, String alergias) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_USUARIO, usuario);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROL, rol);
        values.put(COLUMN_ALERGIAS, alergias);

        long result = db.insert(TABLE_USUARIOS, null, values);
        return result != -1;
    }

    public boolean existeUsuario(String usuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS, new String[]{COLUMN_ID_USUARIO},
                COLUMN_USUARIO + "=?", new String[]{usuario},
                null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public String validarLogin(String usuario, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String rol = null;

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ROL + " FROM " + TABLE_USUARIOS +
                        " WHERE " + COLUMN_USUARIO + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{usuario, password});

        if (cursor != null && cursor.moveToFirst()) {
            rol = cursor.getString(0);
            cursor.close();
        }

        return rol;
    }

    // ==========================================
    // MÉTODOS CRUD (OPERACIONES FUNCIONALES DE LA CLÍNICA)
    // ==========================================

    public long insertarPaciente(String nombre, String telefono, String correo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_PACIENTE_NOMBRE, nombre);
        valores.put(COL_PACIENTE_TELEFONO, telefono);
        valores.put(COL_PACIENTE_CORREO, correo);
        return db.insert(TABLE_PACIENTES, null, valores);
    }

    public long agendarCita(int idPaciente, String fecha, String hora) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_CITA_ID_PACIENTE, idPaciente);
        valores.put(COL_CITA_FECHA, fecha);
        valores.put(COL_CITA_HORA, hora);
        valores.put(COL_CITA_ESTADO, "Pendiente");
        return db.insert(TABLE_CITAS, null, valores);
    }

    public boolean actualizarEstadoCita(int idCita, String nuevoEstado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_CITA_ESTADO, nuevoEstado);
        int filasAfectadas = db.update(TABLE_CITAS, valores, COL_CITA_ID + " = ?", new String[]{String.valueOf(idCita)});
        return filasAfectadas > 0;
    }

    public long registrarHistorial(int idPaciente, String diagnostico, String tratamiento, String datosOdontograma) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_HIST_ID_PACIENTE, idPaciente);
        valores.put(COL_HIST_DIAGNOSTICO, diagnostico);
        valores.put(COL_HIST_TRATAMIENTO, tratamiento);
        valores.put(COL_HIST_ODONTOGRAMA, datosOdontograma);
        return db.insert(TABLE_HISTORIAL, null, valores);
    }

    public long registrarPago(int idCita, double monto, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_PAGO_ID_CITA, idCita);
        valores.put(COL_PAGO_MONTO, monto);
        valores.put(COL_PAGO_FECHA, fecha);
        return db.insert(TABLE_PAGOS, null, valores);
    }

    public long insertarMaterial(String nombre, int cantidad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_INV_NOMBRE, nombre);
        valores.put(COL_INV_CANTIDAD, cantidad);
        return db.insert(TABLE_INVENTARIO, null, valores);
    }

    // Método para insertar una nueva cita
    public boolean insertarCita(String fecha, String hora, String motivo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // OJO: Ajusta estos nombres a las columnas reales de tu tabla CITAS
        values.put("FECHA", fecha);
        values.put("HORA", hora);
        values.put("MOTIVO", motivo);

        // Si tu tabla requiere el ID del paciente de forma obligatoria,
        // puedes quemar uno temporalmente agregando: values.put("ID_PACIENTE", 1);

        long resultado = db.insert("CITAS", null, values);

        // Si resultado es -1, hubo un error. Si es diferente, se guardó bien.
        return resultado != -1;
    }



    // Método para consultar todas las citas
    public Cursor obtenerCitas() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Hacemos un SELECT de todos los registros en la tabla CITAS
        return db.rawQuery("SELECT * FROM CITAS", null);
    }

    // Método para eliminar una cita por su ID
    public boolean eliminarCita(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // OJO: Cambia "ID_CITA" por el nombre real de tu columna de ID
        int resultado = db.delete("CITAS", "ID_CITA = ?", new String[]{String.valueOf(id)});
        return resultado > 0; // Si borró al menos una fila, devuelve true
    }

    // Método para guardar un nuevo historial
    public boolean agregarHistorial(String nombre, int edad, String alergias, String tratamiento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("NOMBRE", nombre);
        valores.put("EDAD", edad);
        valores.put("ALERGIAS", alergias);
        valores.put("TRATAMIENTO", tratamiento);

        long resultado = db.insert("PACIENTES", null, valores);
        return resultado != -1; // Retorna true si se guardó correctamente
    }

    // Método para obtener todos los registros de la tabla PACIENTES
    public Cursor obtenerPacientes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM PACIENTES", null);
    }

    // Método para insertar un material al inventario
    public boolean insertarMaterial(String nombre, int cantidad, String categoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("NOMBRE", nombre);
        valores.put("CANTIDAD", cantidad);
        valores.put("CATEGORIA", categoria);

        long resultado = db.insert("INVENTARIO", null, valores);
        return resultado != -1;
    }

    // Método para obtener todos los materiales del inventario
    public Cursor obtenerInventario() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM INVENTARIO", null);
    }

}