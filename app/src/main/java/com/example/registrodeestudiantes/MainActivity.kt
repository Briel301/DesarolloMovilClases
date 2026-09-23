package com.example.registrodeestudiantes

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.registrodeestudiantes.ui.theme.RegistroDeEstudiantesTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import kotlinx.serialization.Serializable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch

import android.util.Log

@Serializable
data class Estudiante(
    val carne: String,
    val nombre: String,
    val carrera: String,
    val correo: String,
    val telefono: String,
    val jornada: String,
    val idiomas: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistroDeEstudiantesTheme {
                var pantallaActual by remember {
                    mutableStateOf("inicio")
                }
                var estudianteAEditar by remember { mutableStateOf<Estudiante?>(null) }
                val listaEstudiantes = remember {
                    mutableStateListOf<Estudiante>()
                }

                LaunchedEffect(Unit) {
                    try {
                        Log.d("SUPABASE_DEBUG", "Iniciando carga de estudiantes...")
                        val datos = obtenerEstudiantes()
                        Log.d("SUPABASE_DEBUG", "Estudiantes recibidos: ${datos.size}")
                        listaEstudiantes.clear()
                        listaEstudiantes.addAll(datos)
                    } catch (e: Exception) {
                        Log.e("SUPABASE_ERROR", "Error al cargar desde Supabase: ${e.message}", e)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    when (pantallaActual) {
                        "inicio" -> {
                            PantallaInicio(
                                irARegistro = {
                                    pantallaActual = "registro"
                                },
                                irALista = {
                                    pantallaActual = "lista"
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        "registro" -> {
                            PantallaRegistro(
                                listaEstudiantes = listaEstudiantes,
                                modifier = Modifier.padding(innerPadding),
                                volver = {
                                    pantallaActual = "inicio"
                                }
                            )
                        }
                        "lista" -> {
                            PantallaListaEstudiantes(
                                listaEstudiantes = listaEstudiantes,
                                onEditarEstudiante = { estudianteSeleccionado ->
                                    estudianteAEditar = estudianteSeleccionado
                                    pantallaActual = "editar"
                                },
                                volver = {
                                    pantallaActual = "inicio"
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        "editar" -> {
                            estudianteAEditar?.let { estudiante ->
                                PantallaEditarEstudiante(
                                    estudiante = estudiante,
                                    listaEstudiantes = listaEstudiantes,
                                    volver = {
                                        pantallaActual = "lista"
                                    },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun validarFormulario(
    carne: String,
    nombre: String,
    carrera: String,
    correo: String,
    telefono: String,
    jornada: String,
    listaCarnes: List<String>
): String {
    if (carne.isBlank() || nombre.isBlank() || carrera.isBlank() ||
        correo.isBlank() || telefono.isBlank() || jornada.isBlank()
    ) {
        return "Todos los campos y la jornada son obligatorios."
    }

    if (carne in listaCarnes) {
        return "El carné ya pertenece a un estudiante registrado."
    }

    if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
        return "Por favor, ingresa un correo electrónico válido."
    }

    if (telefono.length != 8) {
        return "El teléfono debe tener 8 dígitos"
    }

    return ""
}

@Composable
fun PantallaInicio(
    irARegistro: () -> Unit,
    irALista: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Registro de Estudiantes",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = irARegistro,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar estudiante")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = irALista,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lista de estudiantes")
        }
    }
}

@Composable
fun PantallaRegistro(
    listaEstudiantes: MutableList<Estudiante>,
    modifier: Modifier = Modifier,
    volver: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var carne by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var jornada by remember { mutableStateOf("") }

    var ingles by remember { mutableStateOf(false) }
    var frances by remember { mutableStateOf(false) }
    var aleman by remember { mutableStateOf(false) }

    var mensajeError by remember { mutableStateOf("") }
    var registrando by remember { mutableStateOf(false) }

    val limpiarFormulario = {
        carne = ""
        nombre = ""
        carrera = ""
        correo = ""
        telefono = ""
        jornada = ""
        ingles = false
        frances = false
        aleman = false
        mensajeError = ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Registro de Estudiantes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = carne,
            onValueChange = { entrada ->
                if (entrada.all { it.isDigit() }) carne = entrada
            },
            label = { Text("Carné") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { entrada ->
                if (entrada.all { it.isLetter() || it.isWhitespace() }) nombre = entrada
            },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = carrera,
            onValueChange = { carrera = it },
            label = { Text("Carrera") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { entrada ->
                if (entrada.all { it.isDigit() } && entrada.length <= 8) telefono = entrada
            },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Seleccione la jornada",
            style = MaterialTheme.typography.titleMedium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = jornada == "Matutina",
                onClick = { jornada = "Matutina" }
            )
            Text(text = "Matutina", modifier = Modifier.padding(top = 12.dp))

            RadioButton(
                selected = jornada == "Vespertina",
                onClick = { jornada = "Vespertina" }
            )
            Text(text = "Vespertina", modifier = Modifier.padding(top = 12.dp))

            RadioButton(
                selected = jornada == "Nocturna",
                onClick = { jornada = "Nocturna" }
            )
            Text(text = "Nocturna", modifier = Modifier.padding(top = 12.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Idiomas que maneja",
            style = MaterialTheme.typography.titleMedium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = ingles, onCheckedChange = { ingles = it })
            Text(text = "Inglés", modifier = Modifier.padding(top = 12.dp))

            Checkbox(checked = frances, onCheckedChange = { frances = it })
            Text(text = "Francés", modifier = Modifier.padding(top = 12.dp))

            Checkbox(checked = aleman, onCheckedChange = { aleman = it })
            Text(text = "Alemán", modifier = Modifier.padding(top = 12.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { limpiarFormulario() },
                modifier = Modifier.weight(1f),
                enabled = !registrando
            ) {
                Text("Limpiar")
            }

            Button(
                onClick = {
                    val listaCarnesRegistrados = listaEstudiantes.map { it.carne }
                    val validacion = validarFormulario(
                        carne,
                        nombre,
                        carrera,
                        correo,
                        telefono,
                        jornada,
                        listaCarnesRegistrados
                    )

                    if (validacion.isEmpty()) {
                        registrando = true
                        val idiomasSeleccionados = buildString {
                            if (ingles) append("Inglés ")
                            if (frances) append("Francés ")
                            if (aleman) append("Alemán ")
                        }.trim()

                        val nuevoEstudiante = Estudiante(
                            carne = carne,
                            nombre = nombre,
                            carrera = carrera,
                            correo = correo,
                            telefono = telefono,
                            jornada = jornada,
                            idiomas = idiomasSeleccionados
                        )

                        scope.launch {
                            try {
                                insertarEstudiante(nuevoEstudiante)
                                listaEstudiantes.add(nuevoEstudiante)
                                limpiarFormulario()
                                mensajeError = "Estudiante registrado correctamente."
                            } catch (e: Exception) {
                                mensajeError = "Error al conectar con Supabase: ${e.message}"
                            } finally {
                                registrando = false
                            }
                        }
                    } else {
                        mensajeError = validacion
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !registrando
            ) {
                if (registrando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Registrar")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = volver,
            modifier = Modifier.fillMaxWidth(),
            enabled = !registrando
        ) {
            Text("Volver")
        }
    }
}

@Composable
fun PantallaListaEstudiantes(
    listaEstudiantes: MutableList<Estudiante>,
    onEditarEstudiante: (Estudiante) -> Unit,
    volver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Lista de Estudiantes",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Total de estudiantes: ${listaEstudiantes.size}"
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            items(listaEstudiantes) { estudiante ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = estudiante.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Carné: ${estudiante.carne}")
                        Text("Carrera: ${estudiante.carrera}")
                        Text("Correo: ${estudiante.correo}")
                        Text("Teléfono: ${estudiante.telefono}")
                        Text("Jornada: ${estudiante.jornada}")
                        Text("Idiomas: ${estudiante.idiomas}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        try {
                                            eliminarEstudiante(estudiante.carne)
                                            listaEstudiantes.remove(estudiante)
                                        } catch (e: Exception) {
                                            // Error handling could be added here (e.g. Snackbar)
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Eliminar")
                            }
                            Button(
                                onClick = {
                                    onEditarEstudiante(estudiante)
                                }
                            ) {
                                Text("Editar")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = volver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

@Composable
fun PantallaEditarEstudiante(
    estudiante: Estudiante,
    listaEstudiantes: MutableList<Estudiante>,
    volver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val carne by remember { mutableStateOf(estudiante.carne) }
    var nombre by remember { mutableStateOf(estudiante.nombre) }
    var carrera by remember { mutableStateOf(estudiante.carrera) }
    var correo by remember { mutableStateOf(estudiante.correo) }
    var telefono by remember { mutableStateOf(estudiante.telefono) }
    var jornada by remember { mutableStateOf(estudiante.jornada) }

    var ingles by remember { mutableStateOf(estudiante.idiomas.contains("Inglés")) }
    var frances by remember { mutableStateOf(estudiante.idiomas.contains("Francés")) }
    var aleman by remember { mutableStateOf(estudiante.idiomas.contains("Alemán")) }

    var mensajeError by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Editar Estudiante",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = carne,
            onValueChange = {},
            label = { Text("Carné (No editable)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { entrada ->
                if (entrada.all { it.isLetter() || it.isWhitespace() }) nombre = entrada
            },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = carrera,
            onValueChange = { carrera = it },
            label = { Text("Carrera") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { entrada ->
                if (entrada.all { it.isDigit() } && entrada.length <= 8) telefono = entrada
            },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Seleccione la jornada",
            style = MaterialTheme.typography.titleMedium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = jornada == "Matutina",
                onClick = { jornada = "Matutina" }
            )
            Text(text = "Matutina", modifier = Modifier.padding(top = 12.dp))

            RadioButton(
                selected = jornada == "Vespertina",
                onClick = { jornada = "Vespertina" }
            )
            Text(text = "Vespertina", modifier = Modifier.padding(top = 12.dp))

            RadioButton(
                selected = jornada == "Nocturna",
                onClick = { jornada = "Nocturna" }
            )
            Text(text = "Nocturna", modifier = Modifier.padding(top = 12.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Idiomas que maneja",
            style = MaterialTheme.typography.titleMedium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = ingles, onCheckedChange = { ingles = it })
            Text(text = "Inglés", modifier = Modifier.padding(top = 12.dp))

            Checkbox(checked = frances, onCheckedChange = { frances = it })
            Text(text = "Francés", modifier = Modifier.padding(top = 12.dp))

            Checkbox(checked = aleman, onCheckedChange = { aleman = it })
            Text(text = "Alemán", modifier = Modifier.padding(top = 12.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = volver,
                modifier = Modifier.weight(1f),
                enabled = !guardando
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    if (nombre.isBlank() || carrera.isBlank() || correo.isBlank() ||
                        telefono.isBlank() || jornada.isBlank()
                    ) {
                        mensajeError = "Todos los campos y la jornada son obligatorios."
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                        mensajeError = "Por favor, ingresa un correo electrónico válido."
                    } else if (telefono.length != 8) {
                        mensajeError = "El teléfono debe tener 8 dígitos."
                    } else {
                        guardando = true
                        val idiomasSeleccionados = buildString {
                            if (ingles) append("Inglés ")
                            if (frances) append("Francés ")
                            if (aleman) append("Alemán ")
                        }.trim()

                        val estudianteActualizado = Estudiante(
                            carne = carne,
                            nombre = nombre,
                            carrera = carrera,
                            correo = correo,
                            telefono = telefono,
                            jornada = jornada,
                            idiomas = idiomasSeleccionados
                        )

                        scope.launch {
                            try {
                                actualizarEstudiante(estudianteActualizado)
                                val index = listaEstudiantes.indexOfFirst { it.carne == carne }
                                if (index != -1) {
                                    listaEstudiantes[index] = estudianteActualizado
                                }
                                volver()
                            } catch (e: Exception) {
                                mensajeError = "Error al actualizar en Supabase: ${e.message}"
                            } finally {
                                guardando = false
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !guardando
            ) {
                if (guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}