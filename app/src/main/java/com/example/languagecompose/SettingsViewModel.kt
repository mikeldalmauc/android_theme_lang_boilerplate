package com.example.languagecompose

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel(private val context: Context) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)


    /*
            MODELO PARA EL IDIOMA


     --------------------------------------------------------------------------------
     */


    /*
       LiveData se utiliza en el ViewModel para permitir la observación reactiva de los datos por parte de la UI,
       asegurando que la interfaz se actualice automáticamente cuando cambian los datos. Al ser consciente del ciclo de vida,
       evita fugas de memoria y asegura que las actualizaciones solo se envíen a componentes activos. Además, facilita la
       separación de responsabilidades entre la lógica de datos y la lógica de presentación, permitiendo que el ViewModel
       persista ante cambios de configuración, como la rotación de pantalla.
    */
    private val _language = MutableLiveData<String>().apply {
        value = getSavedLanguage()
    }
    val language: LiveData<String> = _language

    fun getSavedLanguage(): String {
        return sharedPreferences.getString("language", "English") ?: "English"
    }

    /*
        La razón por la que se utiliza viewModelScope.launch en la función setLanguage es para asegurar
        que las operaciones que pueden ser potencialmente bloqueantes (como la edición de SharedPreferences)
        se realicen en un contexto de coroutine, permitiendo que la UI permanezca receptiva.
    */
    fun setLanguage(newLanguage: String) {
        _language.value = newLanguage // Actualiza el LiveData
        sharedPreferences.edit().putString("language", newLanguage).apply() // Actualiza el LiveData
    }

    /*
            MODELO PARA EL TEMA


     --------------------------------------------------------------------------------
     */

    private val _tema = MutableLiveData<Boolean>().apply {
        value = getSavedTheme()
    }
    val tema: LiveData<Boolean> = _tema

    fun getSavedTheme(): Boolean {
        return sharedPreferences.getBoolean("dark_theme", false) // Tema claro por defecto
    }

    fun setTheme(isDarkTheme: Boolean) {
        _tema.value = isDarkTheme // Actualiza el LiveData
        sharedPreferences.edit().putBoolean("dark_theme", isDarkTheme).apply() // Tema claro por defecto
    }


    init {
        Log.d( "LanguageViewModel", "_language: ${_language.value}")
        Log.d( "LanguageViewModel", "_tema: ${_tema.value}")
    }
}