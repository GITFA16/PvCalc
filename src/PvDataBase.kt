
data class PvBase(
    val name: String,
    val noct: Double,
    val etaRef: Double,
    val gamma: Double,
    val neuerparameter: Int? = null   // <— ergänzt, optional
)

object PvCatalog {

    val Trina_Vertex_S_400 = PvBase(
        name = "Trina Vertex S 400",
        noct = 43.0,
        etaRef = 0.208,
        gamma = -0.0034,
        neuerparameter = 20
    )

    val JaSolar_400 = PvBase(
        name = "JA Solar JAM72S10 400",
        noct = 45.0,
        etaRef = 0.200,
        gamma = -0.0035
        // neuerparameter bleibt null
    )

    val longi_410 = PvBase(
        name = "LONGi Hi-MO 5m 410",
        noct = 45.0,
        etaRef = 0.210,
        gamma = -0.0034
    )
}




/*NOCT	    Nominal Operating Cell Temperature	°C - Abhängig von dem Modul
η_ref	    eta Wirkungsgrad bei Standard Test Conditions (STC)	% oder dimensionslos (0–1)
γ 	        Gamma Temperaturkoeffizient	%/°C oder 1/°C
*/
