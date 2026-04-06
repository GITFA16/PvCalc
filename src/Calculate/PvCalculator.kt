package Calculate


import Data.API.ApiInterface
import Data.CatalogInterface
import Data.PVOutDC
import Data.PvOutputsDC
import Userinterface.UserInputDC
import kotlin.math.cos
import kotlin.math.pow

class PvCalculator(
    val apiInterface: ApiInterface,
    val pvCatalog: CatalogInterface
) : CalcInterface {


    override fun calculation(
        ui: UserInputDC,
        moduleName: String

    ): PVOutDC {

        val modul = pvCatalog.getModule(moduleName)

        val apiOutputs = if (ui.useApi) {
            apiInterface.apiCollect(ui)

        } else {
            // Werte aus GUI (ui.*)
            val cloud = ui.cloudCoverPercent ?: 0.0
            val ghi = ui.ghiClear ?: 0.0

            //val dni = ui.dniReal ?: 0.0
            //val dhi = ui.dhiReal ?: 0.0


            val tAir = ui.tAir ?: 0.0
            val wind = ui.velocity ?: 0.0

            // ApiOutputDC wiederverwenden
            //in Package Data
            Data.ApiOutputDC(
                cloudCoverPercent = cloud,
                ghiClear = ghi,
                dniReal = 0.0,
                dhiReal = 0.0,
                tAir = tAir,
                velocity = wind,
                apiHttpResponse = 0 // 0 = manuell
            )
        }

        val cloud = apiOutputs.cloudCoverPercent
        val c = (cloud / 100.0).coerceIn(0.0, 1.0)
        val cmf = 1.0 - 0.75 * c.pow(3.4)


        //Benutzerwerte
        val ghiClear = apiOutputs.ghiClear

        // effektive Werte mit Bewölkung
        val ghiEff = ghiClear * cmf
        if (ghiEff <= 0.0) {
            return PVOutDC(
                apiOut = apiOutputs,
                pvOut = PvOutputsDC(
                    cmf = 0.0,
                    ghiEff = 0.0,
                    fdir = 0.0,
                    fdif = 0.0,
                    dniEff = 0.0,
                    dhiEff = 0.0,
                    gBeta = 0.0,
                    tCell = 0.0,
                    eta = 0.0,
                    power = 0.0
                )
            )
        }

        //Manual-Mode: nur DNI manuell, DHI = GHI - DNI (clamped)
        val dniReal: Double
        val dhiReal: Double

        if (ui.useApi) {
            // API-Modus: Werte wie geliefert
            dniReal = apiOutputs.dniReal
            dhiReal = apiOutputs.dhiReal
        } else {
            // Manuell-Modus: aus Bewölkungskategorie (C%) feste Direkt/Diffus-Anteile ableiten
            val (dniFrac, dhiFrac) = when (cloud) {
                0.0 -> Pair(0.85, 0.15)     // Klar Frac for fraction
                25.0 -> Pair(0.75,0.25)    // Leicht bewölkt
                50.0 -> Pair(0.55, 0.45)    // Teilweise bewölkt
                75.0 -> Pair(0.30, 0.70)    // Stark bewölkt
                100.0 -> Pair(0.10, 0.90)   // Bedeckt
                else -> Pair(0.55, 0.45)    // Default
            }

            dniReal = ghiClear * dniFrac
            dhiReal = (ghiClear - dniReal).coerceAtLeast(0.0)

            apiOutputs.dniReal = dniReal
            apiOutputs.dhiReal = dhiReal
            //C werte = 0 -> Bei Klarenhimmel DNI Werte hoch - Direktstrahl , wenige DHI diffuse strahl
        }

        // Anteile (für Output / Debug)
        val fdir = dniReal / ghiClear
        val fdif = dhiReal / ghiClear

        val dniEff = fdir * ghiEff
        val dhiEff = fdif * ghiEff


        // G_beta
        val betaRad = Math.toRadians(ui.beta)
//        val gBeta = dhiEff + dniEff * cos(betaRad) --> Diffuse mit maximale werte
        val diffuseTilt = dhiEff * (1.0 + cos(betaRad)) / 2.0 //--> Reduzierung der isotropes Diffusmodell
        val gBeta = diffuseTilt + dniEff * cos(betaRad)

        // T_cell
        val tCell0 = apiOutputs.tAir + gBeta / 800.0 * (modul.noct - 20.0)
        val tCell = tCell0 - 0.05 * apiOutputs.velocity

        // Temperaturabhängiger Wirkungsgrad
        val eta = modul.etaRef * (1.0 + modul.gamma * (tCell - 25.0))

        // PV-Leistung
        val rawLeistung = gBeta * ui.flaeche * eta
        val leistung = if (rawLeistung > 0.0) rawLeistung else null

        // alles in API.PvOutputs zurück informieren
        return PVOutDC(
            apiOut = apiOutputs,
            pvOut = PvOutputsDC(
                cmf = cmf,
                ghiEff = ghiEff,
                fdir = fdir,
                fdif = fdif,
                dniEff = dniEff,
                dhiEff = dhiEff,
                gBeta = gBeta,
                tCell = tCell,
                eta = eta,
                power = leistung
            )
        )
    }
}