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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.registrodeestudiantes.ui.theme.RegistroDeEstudiantesTheme

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
                var pantallaActual by remember{
                    mutableStateOf("Inicio")
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ){ innerPadding ->
                    when (pantallaActual){
                        "Inicio" ->{
                            pantallaInicio(
                                irARegistro = {
                                    pantallaActual = "registro"
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        "registro" -> {
                            PantallaRegistro(
                                modifier = Modifier.padding(innerPadding),
                                volver = {
                                    pantallaActual = "Inicio"
                                }
                            )
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
    semestre: String,
    correo: String,
    telefono: String,
    direccion: String,
    jornada: String,
    listaCarnes: List<String>
): String {
    if (carne.isBlank() || nombre.isBlank() || carrera.isBlank() ||
        semestre.isBlank() || correo.isBlank() || telefono.isBlank() ||
        direccion.isBlank() || jornada.isBlank()
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
fun PantallaRegistro(
    modifier: Modifier = Modifier,
    volver: () -> Unit
) {
    var carne by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var semestre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    var jornada by remember { mutableStateOf("") }

    var ingles by remember { mutableStateOf(false) }
    var frances by remember { mutableStateOf(false) }
    var aleman by remember { mutableStateOf(false) }

    var registrado by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }

    var carneRegistrado by remember { mutableStateOf("") }
    var nombreRegistrado by remember { mutableStateOf("") }
    var carreraRegistrado by remember { mutableStateOf("") }
    var semestreRegistrado by remember { mutableStateOf("") }
    var correoRegistrado by remember { mutableStateOf("") }
    var telefonoRegistrado by remember { mutableStateOf("") }
    var direccionRegistrado by remember { mutableStateOf("") }

    var jornadaRegistrada by remember { mutableStateOf("") }

    var inglesRegistrado by remember { mutableStateOf(false) }
    var francesRegistrado by remember { mutableStateOf(false) }
    var alemanRegistrado by remember { mutableStateOf(false) }

    val listaCarnes = remember { mutableStateListOf<String>() }

    val limpiarFormulario = {
        carne = ""
        nombre = ""
        carrera = ""
        semestre = ""
        correo = ""
        telefono = ""
        direccion = ""
        jornada = ""
        ingles = false
        frances = false
        aleman = false
        mensajeError = ""
    }


    var pantallaActual by remember {
        mutableStateOf("inicio")
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
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
            value = semestre,
            onValueChange = { semestre = it },
            label = { Text("Semestre") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
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

        // Aquí es donde residía el problema: esta Row organiza a ambos botones y llama a las validaciones.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { limpiarFormulario() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpiar")
            }

            Button(
                onClick = {
                    val validacion = validarFormulario(
                        carne, nombre, carrera, semestre, correo, telefono, direccion, jornada, listaCarnes
                    )

                    if (validacion.isEmpty()) {
                        listaCarnes.add(carne)
                        registrado = true

                        carneRegistrado = carne
                        nombreRegistrado = nombre
                        carreraRegistrado = carrera
                        semestreRegistrado = semestre
                        correoRegistrado = correo
                        telefonoRegistrado = telefono
                        direccionRegistrado = direccion
                        jornadaRegistrada = jornada
                        inglesRegistrado = ingles
                        francesRegistrado = frances
                        alemanRegistrado = aleman

                        limpiarFormulario()
                    } else {
                        mensajeError = validacion
                        registrado = false
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Registrar")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)

            ) {
                Text(
                    text = "Información del Estudiante",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (registrado) {
                    Text("Carné: $carneRegistrado")
                    Text("Nombre: $nombreRegistrado")
                    Text("Carrera: $carreraRegistrado")
                    Text("Semestre: $semestreRegistrado")
                    Text("Correo: $correoRegistrado")
                    Text("Teléfono: $telefonoRegistrado")
                    Text("Dirección: $direccionRegistrado")
                    Text("Jornada: $jornadaRegistrada")

                    Text("Idiomas:")
                    if (inglesRegistrado) { Text(" Inglés") }
                    if (francesRegistrado) { Text(" Francés") }
                    if (alemanRegistrado) { Text(" Alemán") }
                } else {
                    Text("No hay estudiantes registrados.")
                }

                Spacer (modifier = Modifier.height(12.dp))
                Button(
                    onClick = volver,
                    modifier = Modifier.fillMaxWidth()
                ){
                     Text("Volver")
                }
            }
        }
    }
}

@Composable
fun pantallaInicio(
    irARegistro: () -> Unit,
    modifier: Modifier = Modifier
){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
      Text(
          text = "Registro de Estudiantes",
          style = MaterialTheme.typography.headlineSmall
      )

    Spacer(modifier = Modifier.height(20.dp))

    Button(
        onClick = irARegistro,
        modifier = Modifier.fillMaxWidth()
    ){
        Text ("Registrar estudiante")
    }
    }
}