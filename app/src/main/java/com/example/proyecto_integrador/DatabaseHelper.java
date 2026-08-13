package com.example.proyecto_integrador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ConsultorioDental.db";
    private static final int DATABASE_VERSION = 7; // Versión 7 para aplicar la carga masiva de datos predefinidos

    // Nombres de tablas
    public static final String TABLE_USUARIOS = "Usuarios";
    public static final String TABLE_PACIENTES = "pacientes";
    public static final String TABLE_CITAS = "citas";
    public static final String TABLE_INVENTARIO = "inventario";
    public static final String TABLE_DIAGNOSTICOS = "diagnosticos";

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
                "edad INTEGER, " +
                "alergias TEXT, " +
                "tratamiento TEXT)");

        // 2. Tabla de Pacientes / Historial Clínico
        db.execSQL("CREATE TABLE " + TABLE_PACIENTES + " (" +
                "id_paciente INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "edad INTEGER, " +
                "telefono TEXT, " +
                "correo TEXT, " +
                "alergias TEXT, " +
                "tratamiento TEXT)");

        // 3. Tabla de Citas ACTUALIZADA (Agregamos la columna 'doctor')
        db.execSQL("CREATE TABLE " + TABLE_CITAS + " (" +
                "id_cita INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_paciente INTEGER, " +
                "doctor TEXT NOT NULL, " +
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

        // 5. Tabla de Diagnósticos e Historial Clínico Independiente
        db.execSQL("CREATE TABLE " + TABLE_DIAGNOSTICOS + " (" +
                "id_diagnostico INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre_paciente TEXT, " +
                "cita_info TEXT, " +
                "procedimiento TEXT, " +
                "medicamentos TEXT, " +
                "fecha TEXT)");

        // Insertar datos por defecto (Carga masiva)
        insertarDatosDePrueba(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACIENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIAGNOSTICOS);
        onCreate(db);
    }

    private void insertarDatosDePrueba(SQLiteDatabase db) {
        // ==========================================
        // USUARIOS PREDEFINIDOS
        // ==========================================
        ContentValues valuesUser = new ContentValues();
        valuesUser.put("nombre", "Doctor Admin");
        valuesUser.put("usuario", "admin_doctor");
        valuesUser.put("password", "12345");
        valuesUser.put("rol", "dentista");
        valuesUser.put("alergias", "Ninguna");
        db.insert(TABLE_USUARIOS, null, valuesUser);

        valuesUser.clear();
        valuesUser.put("nombre", "William Eduardo Antonio Marcelo");
        valuesUser.put("usuario", "william_p");
        valuesUser.put("password", "12345");
        valuesUser.put("rol", "paciente");
        valuesUser.put("edad", 19);
        valuesUser.put("alergias", "Ninguna");
        valuesUser.put("tratamiento", "📅 Cita: 20/07/2026 [16:00] - Ortodoncia\n🦷 Proc: Cambio de ligas y ajuste de arco.\n💊 Receta: Ninguno");
        db.insert(TABLE_USUARIOS, null, valuesUser);

        db.execSQL("INSERT INTO " + TABLE_PACIENTES + " (nombre, edad, telefono, correo, alergias, tratamiento) VALUES " +
                "('William Eduardo Antonio Marcelo', 19, '5551234567', 'william@test.com', 'Ninguna', 'Ortodoncia activa'), " +
                "('Andrés', 20, '5559876543', 'andres@test.com', 'Ninguna', 'Resina'), " +
                "('Sofi', 19, '5554567890', 'sofi@test.com', 'Penicilina', 'Revisión General');");

        // ==========================================
        // CITAS HISTÓRICAS PREDEFINIDAS
        // ==========================================
        db.execSQL("INSERT INTO " + TABLE_CITAS + " (id_paciente, doctor, fecha, hora, motivo, estado) VALUES " +
                "(1, 'Dra. López', '10/05/2026', '10:00', 'Limpieza dental (Profilaxis)', 'Completada'), " +
                "(1, 'Dr. Admin', '15/06/2026', '12:30', 'Dolor agudo / Urgencia', 'Completada'), " +
                "(1, 'Dr. Martínez', '20/07/2026', '16:00', 'Ortodoncia (Ajuste / Revision)', 'Completada'), " +
                "(2, 'Dr. Admin', '01/08/2026', '09:00', 'Revisión general', 'Completada');");

        // ==========================================
        // HISTORIAL CLÍNICO / DIAGNÓSTICOS PREDEFINIDOS
        // ==========================================
        db.execSQL("INSERT INTO " + TABLE_DIAGNOSTICOS + " (nombre_paciente, cita_info, procedimiento, medicamentos, fecha) VALUES " +
                "('William Eduardo Antonio Marcelo', 'Cita: 10/05/2026 [10:00] - Limpieza dental', 'Profilaxis ultrasónica y pulido coronal. Se detectó caries incipiente en molar 36.', 'Enjuague con clorhexidina al 0.12% por 5 días', '10/05/2026'), " +
                "('William Eduardo Antonio Marcelo', 'Cita: 15/06/2026 [12:30] - Dolor agudo / Urgencia', 'Eliminación de tejido cariado en pieza 36 y colocación de resina compuesta tono A2.', 'Ibuprofeno 400mg cada 8 hrs por 3 días', '15/06/2026'), " +
                "('William Eduardo Antonio Marcelo', 'Cita: 20/07/2026 [16:00] - Ortodoncia', 'Cambio de ligas y ajuste de arco superior e inferior. Evolución favorable sin inflamación gingival.', 'Ninguno', '20/07/2026'), " +
                "('Andrés', 'Cita: 01/08/2026 [09:00] - Revisión general', 'Revisión de rutina, toma de radiografía panorámica. Sin alteraciones visibles.', 'Ninguno', '01/08/2026');");

        // ==========================================
        // INVENTARIO AMPLIADO (Material, Medicina, Herramientas)
        // ==========================================
        db.execSQL("INSERT INTO " + TABLE_INVENTARIO + " (nombre, cantidad, categoria) VALUES " +
                "('Anestesia Local (Cárpules)', 150, 'Medicamentos'), " +
                "('Ibuprofeno 400mg (Cajas)', 20, 'Medicamentos'), " +
                "('Amoxicilina 500mg (Cajas)', 15, 'Medicamentos'), " +
                "('Clorhexidina al 0.12% (Frascos)', 10, 'Medicamentos'), " +
                "('Ketorolaco 10mg (Cajas)', 12, 'Medicamentos'), " +
                "('Resina Compuesta A2 (Jeringas)', 30, 'Materiales'), " +
                "('Resina Compuesta A3 (Jeringas)', 25, 'Materiales'), " +
                "('Ionómero de Vidrio (Kits)', 12, 'Materiales'), " +
                "('Ácido Grabador (Jeringas)', 18, 'Materiales'), " +
                "('Adhesivo Dental (Frascos)', 15, 'Materiales'), " +
                "('Cemento Quirúrgico', 8, 'Materiales'), " +
                "('Guantes de Látex (Cajas)', 40, 'Desechables'), " +
                "('Cubrebocas Tricapa (Cajas)', 50, 'Desechables'), " +
                "('Eyectores de Saliva (Bolsas)', 25, 'Desechables'), " +
                "('Algodón en Rollos (Bolsas)', 30, 'Desechables'), " +
                "('Agujas Dentales Cortas (Cajas)', 15, 'Desechables'), " +
                "('Baberos para Paciente (Paquetes)', 20, 'Desechables'), " +
                "('Espejos Bucales', 20, 'Instrumental'), " +
                "('Exploradores Dentales', 15, 'Instrumental'), " +
                "('Pinzas Algodoneras', 15, 'Instrumental'), " +
                "('Jeringas Carpule', 10, 'Instrumental'), " +
                "('Fórceps para Extracción', 8, 'Instrumental'), " +
                "('Curetas periodontales', 12, 'Instrumental');");
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

    public boolean insertarUsuarioCompleto(String nombre, String usuario, String password, String rol, int edad, String alergias, String tratamiento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("usuario", usuario);
        values.put("password", password);
        values.put("rol", rol);
        values.put("edad", edad);
        values.put("alergias", alergias);
        values.put("tratamiento", tratamiento);
        long resultado = db.insert(TABLE_USUARIOS, null, values);
        return resultado != -1;
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
    public boolean verificarDisponibilidadHorario(String fecha, String hora, String doctor) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CITAS + " WHERE fecha = ? AND hora = ? AND doctor = ?",
                new String[]{fecha, hora, doctor});

        boolean estaOcupado = (cursor.getCount() > 0);
        cursor.close();
        return estaOcupado;
    }

    public boolean insertarCita(String doctor, String fecha, String hora, String motivo) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("doctor", doctor);
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

    public boolean actualizarTratamientoPaciente(String nombre, String tratamiento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tratamiento", tratamiento);
        int resultado = db.update(TABLE_PACIENTES, values, "nombre = ?", new String[]{nombre});
        return resultado > 0;
    }

    // ==========================================
    // MÉTODOS DE DIAGNÓSTICOS (HISTORIAL CLÍNICO)
    // ==========================================
    public boolean insertarDiagnostico(String nombrePaciente, String citaInfo, String procedimiento, String medicamentos, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre_paciente", nombrePaciente);
        values.put("cita_info", citaInfo);
        values.put("procedimiento", procedimiento);
        values.put("medicamentos", medicamentos);
        values.put("fecha", fecha);
        long resultado = db.insert(TABLE_DIAGNOSTICOS, null, values);
        return resultado != -1;
    }

    public Cursor obtenerDiagnosticosPorPaciente(String nombrePaciente) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_DIAGNOSTICOS + " WHERE nombre_paciente = ?", new String[]{nombrePaciente});
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

    // ==========================================
    // FUNCIONES EXTRA
    // ==========================================
    public Cursor obtenerUsuarioPorUsername(String usuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USUARIOS + " WHERE usuario = ?", new String[]{usuario});
    }

    public Cursor obtenerDiagnosticosParaPaciente(String usuarioOIdentificador) {
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. Buscamos si el texto ingresado coincide con un usuario para obtener su nombre real
        String nombreReal = usuarioOIdentificador;
        Cursor cursorUser = db.rawQuery("SELECT nombre FROM Usuarios WHERE usuario = ?", new String[]{usuarioOIdentificador});
        if (cursorUser != null && cursorUser.moveToFirst()) {
            nombreReal = cursorUser.getString(0);
            cursorUser.close();
        } else if (cursorUser != null) {
            cursorUser.close();
        }

        // 2. Consultamos los diagnósticos buscando tanto por el nombre real como por el identificador original
        return db.rawQuery("SELECT * FROM " + TABLE_DIAGNOSTICOS + " WHERE nombre_paciente = ? OR nombre_paciente = ?",
                new String[]{nombreReal, usuarioOIdentificador});
    }
    // ==========================================
    // NUEVOS MÉTODOS DE INVENTARIO (EDITAR Y ELIMINAR)
    // ==========================================
    public boolean actualizarMaterial(int id, String nombre, int cantidad, String categoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues valores = new android.content.ContentValues();
        valores.put("nombre", nombre);
        valores.put("cantidad", cantidad);
        valores.put("categoria", categoria);
        int resultado = db.update(TABLE_INVENTARIO, valores, "id_material = ?", new String[]{String.valueOf(id)});
        return resultado > 0;
    }

    public boolean eliminarMaterial(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete(TABLE_INVENTARIO, "id_material = ?", new String[]{String.valueOf(id)});
        return resultado > 0;
    }
}