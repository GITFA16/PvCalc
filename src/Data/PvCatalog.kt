package Data

object PvCatalog : CatalogInterface {

    private val moduleMap = mutableMapOf<String, PvBaseDC> ()

    init {

        add(PvBaseDC(
            name = "LONGi Hi-MO 5m 410",
            noct = 45.0,
            etaRef = 0.210,
            gamma = -0.0034
        ))

        add(PvBaseDC(
            name = "Trina Vertex S+ 440",
            noct = 43.0,
            etaRef = 0.218,
            gamma = -0.0030
        ))

        add(PvBaseDC(
            name = "JA Solar 410",
            noct = 45.0,
            etaRef = 0.210,
            gamma = -0.0035
        ))

        add(PvBaseDC(
            name = "Canadian Solar HiKu6 410",
            noct = 41.0,
            etaRef = 0.210,
            gamma = -0.0034
        ))

        add(PvBaseDC(
            name = "Qcells Q.PEAK DUO-BLK ML-G10+ 400",
            noct = 43.0,
            etaRef = 0.204,
            gamma = -0.0034
        ))

        add(PvBaseDC(
            name = "REC Alpha Pure 400",
            noct = 44.0,
            etaRef = 0.216,
            gamma = -0.0024
        ))

        add(PvBaseDC(
            name = "LG NeON R ACe 370",
            noct = 44.0,
            etaRef = 0.214,
            gamma = -0.0030
        ))

        add(PvBaseDC(
            name = "Risen Titan 450",
            noct = 44.0,
            etaRef = 0.217,
            gamma = -0.0034
        ))

        add(PvBaseDC(
            name = "Jinko Tiger Neo 430",
            noct = 45.0,
            etaRef = 0.2152,
            gamma = -0.0029
        ))

        add(PvBaseDC(
            name = "Panasonic EverVolt Black 410",
            noct = 44.0,
            etaRef = 0.222,
            gamma = -0.0026
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

