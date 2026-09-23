package com.example.registrodeestudiantes

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

val Supabase = createSupabaseClient(
    supabaseUrl = "https://khyhylcxukaotugddrsh.supabase.co",
    supabaseKey = "sb_publishable_PT3a8muDHxs2TEsZi-ATKg_wSV2kLQ7"
) {
    install(plugin = Postgrest)
}

suspend fun obtenerEstudiantes(): List<Estudiante>{
    return Supabase.postgrest
        .from("estudiantes")
        .select()
        .decodeList<Estudiante>()
}

suspend fun eliminarEstudiante(carne: String){
    Supabase.postgrest
        .from ("estudiantes")
        .delete{
            filter{
                eq("carne", carne)
            }
        }
}

suspend fun insertarEstudiante(estudiante: Estudiante){
    Supabase.postgrest
        .from("estudiantes")
        .insert(estudiante)
}

suspend fun actualizarEstudiante(estudiante: Estudiante) {
    Supabase.postgrest
        .from("estudiantes")
        .update(estudiante) {
            filter {
                eq("carne", estudiante.carne)
            }
        }
}