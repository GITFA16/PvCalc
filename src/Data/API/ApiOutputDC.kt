package Data.API

data class ApiOutputDC (
    var cloudCoverPercent : Double,  //val ist besser? - da die Aufbau des Codes nicht geändert wird
    var ghiClear : Double,
    var dniReal : Double,
    var dhiReal : Double,
    var tAir : Double,
    var velocity : Double,
    var apiHttpResponse: Int
)