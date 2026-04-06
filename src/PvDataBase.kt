
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


/*PvModels.kt
Basismodell: beschreibt das PV-Modul selbst

data class PvBase(
    val name: String,
    val noct: Double,
    val etaRef: Double,
    val gamma: Double
)

Erweiterung: zusätzliche Infos, nicht jedes Modul braucht sie

data class PvExtended(
    val base: PvBase,
    val neuerParameter: Int
)*/

/*object PvCatalog {

    val Trina_Vertex_S_400 = PvExtended(
        base = PvBase(
            name = "Trina Vertex S 400",
            noct = 43.0,
            etaRef = 0.208,
            gamma = -0.0034
        ),
        neuerParameter = 20
    )

    val JaSolar_400 = PvBase(
        name = "JA Solar JAM72S10 400",
        noct = 45.0,
        etaRef = 0.200,
        gamma = -0.0035
    )

    val longi_410 = PvBase(
        name = "LONGi Hi-MO 5m 410",
        noct = 45.0,
        etaRef = 0.210,
        gamma = -0.0034
    )
}*/













/*
data class PvBase(
    val name: String,
    val noct: Double,
    val etaRef: Double,
    val gamma: Double
)

{
    var neuerparameter: Int? = null
}


object PvCatalog {

    val Trina_Vertex_S_400 =  PvBase(
        name = "Trina Vertex S 400",
        noct = 43.0,
        etaRef = 0.208,
        gamma = -0.0034
    ).apply {
        neuerparameter = 20
    }

    val JaSolar_400 =  PvBase(
        name = "JA Solar JAM72S10 400",
        noct = 45.0,
        etaRef = 0.200,
        gamma = -0.0035
    )

    val longi_410 = PvBase(
        name = "LONGi Hi-MO 5m 410",
        etaRef = 0.210,    // 21.0 %
        gamma = -0.0034,
        noct = 45.0
    )


}

*/





