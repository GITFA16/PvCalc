package Calculate

import Data.API.ApiInterface
import Data.CatalogInterface
import Data.PVOutDC
import Data.PvOutputsDC
import Userinterface.UserInputDC
import kotlin.math.cos

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
            val ghi = ui.ghiClear ?: 0.0
            val dhi = ui.dhiReal ?: 0.0
            val tAir = ui.tAir ?: 0.0
            val wind = ui.velocity ?: 0.0
            val dhiClamped = dhi.coerceIn(0.0, ghi)
            val dni = (ghi - dhiClamped).coerceAtLeast(0.0)

            // ApiOutputDC wiederverwenden (cloudCover wird nicht mehr genutzt)
            Data.ApiOutputDC(
                cloudCoverPercent = 0.0,
                ghiClear = ghi,
                dniReal = dni,     // wird im Manual-Mode aus GHI/DHI abgeleitet
                dhiReal = dhi,
                tAir = tAir,
                velocity = wind,
                apiHttpResponse = 0 // 0 = manuell
            )
        }

        // Kein Cloud-Faktor mehr
        val cmf = 1.0

        // Benutzerwerte
        val ghiClear = apiOutputs.ghiClear

        // Manual-Mode: User gibt GHI + DHI ein, BHI (= direkter horizontaler Anteil) = GHI - DHI
        val dniReal: Double   // hier als BHI zu verstehen
        val dhiReal: Double

        if (!ui.useApi) {
            val dhiInput = apiOutputs.dhiReal

            // DHI darf nicht negativ sein und nicht größer als GHI
            val dhiClamped = dhiInput.coerceIn(0.0, ghiClear)

            // Direkter horizontaler Anteil aus Bilanz
            val bhiAuto = (ghiClear - dhiClamped).coerceAtLeast(0.0)

            dhiReal = dhiClamped
            dniReal = bhiAuto
        } else {
            // API-Mode: nutze Werte wie geliefert
            dniReal = apiOutputs.dniReal
            dhiReal = apiOutputs.dhiReal
        }

        // effektive Werte (cmf=1)
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

        // Anteile (für Output / Debug)
        val fdir = if (ghiClear > 0.0) dniReal / ghiClear else 0.0
        val fdif = if (ghiClear > 0.0) dhiReal / ghiClear else 0.0

        // Bei cmf=1 sind die effektiven Werte identisch zu den "realen"
        val dniEff = dniReal * cmf
        val dhiEff = dhiReal * cmf

        // G_beta (gleiches vereinfachtes Modell wie bei dir)
        val betaRad = Math.toRadians(ui.beta)

        val cosB = cos(betaRad)
        val skyFactor = (1.0 + cosB) / 2.0

        val gBeta = dniEff * cosB + dhiEff * skyFactor

        // T_cell
        val tCell0 = apiOutputs.tAir + gBeta / 800.0 * (modul.noct - 20.0)
        val tCell = tCell0 - 0.05 * apiOutputs.velocity

        // Temperaturabhängiger Wirkungsgrad
        val eta = modul.etaRef * (1.0 + modul.gamma * (tCell - 25.0))

        // PV-Leistung
        val rawLeistung = gBeta * ui.flaeche * eta
        val leistung = if (rawLeistung > 0.0) rawLeistung else null

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