package Data

data class PvOutputsDC(
    private val cmf: Double,
    private val ghiEff: Double,
    private val fdir: Double,
    private val fdif: Double,
    private val dniEff: Double,
    private val dhiEff: Double,
    private val gBeta: Double,
    private val tCell: Double,
    private val eta: Double,
    val power: Double?)