
- [Introducción](#introducción)
  - [1. **`App.kt` (Clase `App`)**](#1-appkt-clase-app)
    - [Propósito:](#propósito)
    - [Modificación de `AndroidManifest.xml`](#modificación-de-androidmanifestxml)
    - [¿Por qué es necesario?](#por-qué-es-necesario)
  - [2. **`LanguageViewModel.kt` (Clase `LanguageViewModel`)**](#2-languageviewmodelkt-clase-languageviewmodel)
    - [Propósito:](#propósito-1)
    - [¿Por qué es necesario?](#por-qué-es-necesario-1)
  - [3. **`MainActivity.kt` (Clase `MainActivity`)**](#3-mainactivitykt-clase-mainactivity)
    - [Propósito:](#propósito-2)
    - [¿Por qué es necesario?](#por-qué-es-necesario-2)
  - [4. **`SettingsActivity.kt` (Clase `SettingsActivity`)**](#4-settingsactivitykt-clase-settingsactivity)
    - [Propósito:](#propósito-3)
    - [¿Por qué es necesario?](#por-qué-es-necesario-3)
  - [5. **Detalles clave del Composable `SettingsScreen`**](#5-detalles-clave-del-composable-settingsscreen)


## Introducción
El desarrollo de aplicaciones multilingües requiere una estructura técnica que permita cambiar dinámicamente el idioma en tiempo de ejecución, mientras se asegura que la selección persista a lo largo del uso de la aplicación. En este proyecto, hemos implementado una solución que aborda esta necesidad utilizando varias herramientas de Android, como Application, SharedPreferences, ViewModel, y Jetpack Compose, para crear una aplicación que permite a los usuarios seleccionar su idioma preferido y aplicar el cambio de manera instantánea.

El reto principal fue garantizar que la interfaz de usuario se actualizara correctamente tras el cambio de idioma, sin tener que reiniciar la aplicación, y que la elección del usuario se mantuviera en futuras sesiones. Para lograrlo, hemos estructurado el código en varios componentes clave que permiten aplicar y persistir el idioma seleccionado, manejar el ciclo de vida de la aplicación correctamente, y actualizar la interfaz de manera reactiva cuando el idioma cambia.

En esta explicación, desglosaremos cada componente del proyecto y la razón detrás de su implementación, demostrando cómo se integran para ofrecer una solución eficiente al cambio dinámico de idiomas en una aplicación Android.


<img src="Screen_recording_20241017_134732.gif" alt="alt text" width="300" />

### 1. **`App.kt` (Clase `App`)**

Este archivo define una clase que extiende `Application`, el punto de entrada global de la aplicación.

#### Propósito:
La clase **`App`** se encarga de aplicar el idioma seleccionado cada vez que se inicia la aplicación o se crea una nueva actividad. Al heredar de `Application`, actúa como un contexto global que gestiona la configuración del idioma en toda la aplicación.

- **`onCreate()`**: Aplica el idioma guardado al iniciar la aplicación.
- **`applyLanguage(context)`**: Recupera el idioma desde **SharedPreferences** y lo aplica a través de `setLocale()`.
- **`setLocale(context, language)`**: Cambia el idioma utilizando `Locale` y la configuración del contexto de la aplicación, asegurando que todas las actividades, fragmentos y vistas utilicen el idioma correcto.

```kotlin
override fun onCreate() {
    super.onCreate()
    // Aplica el idioma guardado al iniciar la aplicación
    applyLanguage(this)
}
```

```kotlin
 fun applyLanguage(context: Context): Context {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val language = sharedPreferences.getString("language", "en") ?: "en"
    Log.d("App", language)

    return setLocale(context, language)
}
```
```kotlin
// Actualiza la configuración del idioma
fun setLocale(context: Context, language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)
    context.createConfigurationContext(config)
    return context
}
```
#### Modificación de `AndroidManifest.xml`

Es necesario añadir el nombre de la app como se ve abajo

```xml
 <application
        android:name=".App"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        ...
    >
```

#### ¿Por qué es necesario?
Esta clase es necesaria para garantizar que el idioma seleccionado se aplique de manera global y persista en toda la aplicación. Al centralizar la lógica de cambio de idioma en la clase `App`, puedes controlar el contexto del idioma de manera uniforme.

---

### 2. **`LanguageViewModel.kt` (Clase `LanguageViewModel`)**

Este archivo define un **ViewModel** que gestiona el estado del idioma seleccionado de manera reactiva.

#### Propósito:
El **`LanguageViewModel`** encapsula la lógica relacionada con el idioma en un modelo de vista independiente, permitiendo que las vistas observen el estado del idioma y reaccionen automáticamente a los cambios.

- **`_language` y `language`**: Utilizan `LiveData` para almacenar y observar el idioma seleccionado. `_language` es mutable y `language` es la versión inmutable observada por la UI.
- **`setLanguage(newLanguage)`**: Cambia el idioma almacenado y lo guarda en **SharedPreferences** para persistir la selección.
- **`getSavedLanguage()`**: Recupera el idioma guardado en las preferencias para inicializar el `ViewModel`.

#### ¿Por qué es necesario?
El **ViewModel** es crucial porque maneja el estado de forma desacoplada del ciclo de vida de la actividad o composición. De esta manera, el estado del idioma persiste incluso cuando la actividad se destruye y se vuelve a crear (por ejemplo, en una rotación de pantalla), garantizando una experiencia de usuario fluida.

---

### 3. **`MainActivity.kt` (Clase `MainActivity`)**

Define la actividad principal donde se muestra la pantalla de inicio de la aplicación. En este archivo, se configura la pantalla principal usando Jetpack Compose.

#### Propósito:
- **`onCreate()`**: Al iniciar la actividad, se aplica el idioma guardado mediante `App.applyLanguage(this)` antes de cargar la UI.
- **`MainScreen()`**: Es la composable que muestra el texto de bienvenida y un botón que lleva a la `SettingsActivity` para cambiar el idioma.

#### ¿Por qué es necesario?
Esta actividad es el punto de entrada de la UI de la aplicación. Al aplicar el idioma en `onCreate()`, te aseguras de que la interfaz esté siempre en el idioma correcto antes de que el usuario vea cualquier contenido.

```kotlin
  override fun onCreate(savedInstanceState: Bundle?) {

        // Aplicar el idioma guardado al iniciar la actividad
        App.applyLanguage(this)

        super.onCreate(savedInstanceState)

```

---

### 4. **`SettingsActivity.kt` (Clase `SettingsActivity`)**

Esta clase define la actividad donde el usuario puede cambiar el idioma de la aplicación.

#### Propósito:
- **`onCreate()`**: Aplica el idioma guardado mediante `App.applyLanguage(this)` y luego carga la pantalla de configuración. Inicializa el **LanguageViewModel** para que la UI observe el estado del idioma.
- **`attachBaseContext(newBase)`**: Sobreescribe el contexto de la actividad para asegurarse de que el idioma correcto se aplique.
- **`SettingsScreen(viewModel)`**: Es una composable que muestra una lista de idiomas disponibles y permite cambiar el idioma. La función `mapCode` mapea los códigos de idioma a los recursos `strings.xml` correspondientes para mostrar los textos localizados en la UI.

```kotlin
  override fun onCreate(savedInstanceState: Bundle?) {

        // Aplicar el idioma guardado al iniciar la actividad
        App.applyLanguage(this)

        super.onCreate(savedInstanceState)

        Log.i("SettingsActivity", "La actividad se ha reiniciado")

        setContent {
            val languageViewModel = LanguageViewModel(LocalContext.current)

            LanguageSwitcherAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    SettingsScreen(languageViewModel)
                }
            }
        }
    }
```
#### ¿Por qué es necesario?
Esta actividad permite al usuario seleccionar el idioma preferido y persiste su elección. El uso del **ViewModel** asegura que la selección del idioma se maneje de forma reactiva, actualizando la UI cuando el usuario selecciona un nuevo idioma.

---

### 5. **Detalles clave del Composable `SettingsScreen`**

Dentro de `SettingsScreen`, se muestran las opciones de idioma y se usa un `LazyColumn` para listar los idiomas disponibles.

- **Recreación de la actividad**: Cuando el usuario selecciona un idioma, se llama a `App.setLocale(context, language)` para aplicar el nuevo idioma y se recrea la actividad con `(context as? Activity)?.recreate()`. Esto asegura que la interfaz se recargue en el idioma correcto.

```kotlin
    .clickable {
        viewModel.setLanguage(language)
        // Aplicar el idioma y recrear la actividad
        App.setLocale(context, language)
        (context as? Activity)?.recreate() // Recrear la actividad
    }
```