package com.example.registrodeestudiantes
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class BaseDatos(context: Context): SQLiteOpenHelper(
    context,
    "estudiantes.db",
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val crearTabla = """
            CREATE TABLE estudiantes(
            carne TEXT PRIMARY KEY,
            nombre TEXT NOT NULL,
            carrera TEXT NOT NULL,
            correo TEXT,
            telefono TEXT,
            jornada TEXT,
            idiomas TEXT
            )""".trimIndent()

        db?.execSQL(crearTabla)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL("DROP TABLE IF EXISTS estudiantes")
        onCreate(db)
    }
    fun insertarEstudiante(estudiante: Estudiante): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply{
            put("carne", estudiante.carne)
            put("nombre", estudiante.nombre)
            put("carrera", estudiante.carrera)
            put("correo", estudiante.correo)
            put("telefono", estudiante.telefono)
            put("jornada", estudiante.jornada)
            put("idiomas", estudiante.idiomas)
        }
        val resultado = db.insert(
            "estudiantes",
            null,
            valores
        )
        return resultado != -1L
    }

    fun obtenerEstudiantes(): List<Estudiante>{
        val lista = mutableListOf<Estudiante>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM estudiantes",
            null
        )

        if (cursor.moveToFirst()){
            do {
                val estudiante = Estudiante(
                    carne = cursor.getString(cursor.getColumnIndexOrThrow("carne")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    carrera = cursor.getString(cursor.getColumnIndexOrThrow("carrera")),
                    correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                    jornada = cursor.getString(cursor.getColumnIndexOrThrow("jornada")),
                    idiomas = cursor.getString(cursor.getColumnIndexOrThrow("idiomas"))
                )
                lista.add(estudiante)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun eliminarEstudiante (carne: String): Boolean{

        val db = writableDatabase

        val resultado = db.delete(
            "Estudiantes",
            "carne = ?",
            arrayOf(carne)
        )
        return resultado > 0
    }

}