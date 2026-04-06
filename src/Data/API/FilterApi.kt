package Data.API

class FilterApi(
    var rawWeatherData: String
) {


    fun filterOpenMeteo(field: String): Double {

        val currentBlock = Regex("\"current\"\\s*:\\s*\\{([\\s\\S]*?)\\}\\s*[,}]")
            .find(rawWeatherData)?.groupValues?.get(1)
            ?: error("current block not found")

        val value = Regex("\"${Regex.escape(field)}\"\\s*:\\s*([-+]?\\d+(?:\\.\\d+)?(?:[eE][-+]?\\d+)?)")
            .find(currentBlock)?.groupValues?.get(1)
            ?: error("current block not found")
        return value.toDouble()

    }


}


//18.2.2026: Für die Erweiterung und Ergänzungen des API Kollektors auf Tagesbasiert Datensätze welche
// auf Stundenauflösung aktuelle Werte zurückliefen, (Geeignet für einzelne Tagesverläufe)
//Können hier 3 Abhängigen Methoden verwendet werden, welche unter der Dokumentation "FutureOptions" liegen
