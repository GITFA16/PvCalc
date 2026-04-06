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
            val dni = ui.dniReal ?: 0.0
            val dhi = ui.dhiReal ?: 0.0
            val tAir = ui.tAir ?: 0.0
            val wind = ui.velocity ?: 0.0

            // ApiOutputDC wiederverwenden
            //in Package Data
            Data.ApiOutputDC(
                cloudCoverPercent = cloud,
                ghiClear = ghi,
                dniReal = dni,
                dhiReal = dhi,
                tAir = tAir,
                velocity = wind,
                apiHttpResponse = 0 // 0 = manuell
            )
        }

        val c = apiOutputs.cloudCoverPercent / 100.0
        val cmf = 1.0 - 0.75 * c.pow(3.4)

        //Benutzerwerte
        val ghiClear = apiOutputs.ghiClear
        val dniReal = apiOutputs.dniReal
        val dhiReal = apiOutputs.dhiReal
        val sum = dniReal + dhiReal

        // GHI_eff
        if (ghiClear <= 0.0 || sum <= 0.0) {
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
        val scale = ghiClear / sum

        // normierte Strahlung (klarer Himmel)
        val dniNorm = dniReal * scale
        val dhiNorm = dhiReal * scale

        // effektive Werte mit Bewölkung
        val ghiEff = ghiClear * cmf
        val dniEff = dniNorm * cmf
        val dhiEff = dhiNorm * cmf

        // Anteile (für Output / Debug)
        val fdir = dniNorm / ghiClear
        val fdif = dhiNorm / ghiClear

        // G_beta
        val betaRad = Math.toRadians(ui.beta)
        val gBeta = dhiEff + dniEff * cos(betaRad)

        // T_cell
        val tCell0 = apiOutputs.tAir + gBeta / 800.0 * (modul.noct - 20.0)
        val tCell = if (apiOutputs.velocity != null) {
            tCell0 - 0.05 * apiOutputs.velocity
        } else {
            tCell0
        }

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