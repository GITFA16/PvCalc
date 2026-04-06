package Data

data class PvOutputsDC(
    val cmf: Double,
    val ghiEff: Double,
    val fdir: Double,
    val fdif: Double,
    val dniEff: Double,
    val dhiEff: Double,
    val gBeta: Double,
    val tCell: Double,
    val eta: Double,
    val power: Double?
)