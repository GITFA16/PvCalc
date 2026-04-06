package Data

object PvCatalog : CatalogInterface {

    private val moduleMap = mutableMapOf<String, PvBaseDC> ()

    init {

        add(PvBaseDC(
            name = "Trina Vertex S 400",
            noct = 43.0,
            etaRef = 0.208,
            gamma = -0.0034,
            neuerparameter = 20
        ))

        add(PvBaseDC(
            name = "JA Solar JAM72S10 400",
            noct = 45.0,
            etaRef = 0.200,
            gamma = -0.0035
        ))

        add(PvBaseDC(
            name = "LONGi Hi-MO 5m 410",
            noct = 45.0,
            etaRef = 0.210,
            gamma = -0.0034
        ))

        add(PvBaseDC(
            name = "New4 Test PV-Type",
            noct = 45.0,
            etaRef = 0.210,
            gamma = -0.0034
        ))

    }


    private fun add(module: PvBaseDC) {
        moduleMap.put(module.name, module)
    }



    override fun getModule(name: String): PvBaseDC =
        moduleMap[name]
            ?: error("PV module not found: $name")



    override fun getModuleNames() = moduleMap.keys.toList()


}

