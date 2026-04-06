package Data

interface CatalogInterface {
    fun getModuleNames(): List<String>
    fun getModule(name:String): PvBaseDC
}