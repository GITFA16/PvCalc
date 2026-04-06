package Data

data class ApiOutputDC (
    var cloudCoverPercent : Double,
    var ghiClear : Double,
    var dniReal : Double,
    var dhiReal : Double,
    var tAir : Double,
    var velocity : Double,
    var apiHttpResponse: Int
)