package Userinterface

data class UserInputDC (
    var zip: Int,
    var appId: String,
    var beta : Double,
    var flaeche: Double,
    var pvModul : Int,
    var useApi: Boolean = true,

    //Manuelle Eingaben (nur relevant, wenn useApi=false)
    var cloudCoverPercent: Double? = 0.00, // C [%]
    var ghiClear: Double? = 0.0,          // GHI_clear [W/m²]
    var dniReal: Double? = 0.0,           // DNI [W/m²] Direct Normal Irradiance
    var dhiReal: Double? = 0.0,           // DHI [W/m²] Direct Horizontal Irradiance
    var tAir: Double? = 0.0,              // T_air [°C] Lufttemperatur
    var velocity: Double? = 0.0           // Wind [m/s] Windgeschwindigkeit
    )