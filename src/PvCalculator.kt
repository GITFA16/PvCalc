

import kotlin.math.cos
import kotlin.math.pow




class PvCalculator (val apiInterface:ApiInterface) : CalcInterface  {

     override fun calculation(ui: UserInputDataClass, modul: PvBase): PVOutData {
        val apioutputs = apiInterface.apiCollect(ui = ui)
        val c = apioutputs.cloudCoverPercent / 100.0
        val cmf = 1.0 - 0.75 * c.pow(3.4)



        // GHI_eff
        val ghiEff = apioutputs.ghiClear * cmf
         if (ghiEff <= 0.0) {
             return PVOutData (
              apiOut = apioutputs,
              pvOut = PvOutputs(cmf = 0.0,
              ghiEff = ghiEff,
              fdir = 0.0,
              fdif = 0.0,
              dniEff = 0.0,
              dhiEff = 0.0,
              gBeta = 0.0,
              tCell = 0.0,
              eta = 0.0,
              leistung = 0.0
            ))

         }

        // fdir / fdif
        val fdir = apioutputs.dniReal / ghiEff
        val fdif = apioutputs.dhiReal / ghiEff

        // DNI_eff & DHI_eff
        val dniEff = fdir * ghiEff
        val dhiEff = fdif * ghiEff

        // G_beta
        val betaRad = Math.toRadians(ui.beta)
        val gBeta = dhiEff + dniEff * cos(betaRad)

        // T_cell
        val tCell0 = apioutputs.tAir + gBeta / 800.0 * (modul.noct - 20.0)
        val tCell = if (apioutputs.v != null) {
            tCell0 - 0.05 * apioutputs.v
        } else {
            tCell0
        }

        // Temperaturabhängiger Wirkungsgrad
        val eta = modul.etaRef * (1.0 + modul.gamma * (tCell - 25.0))

        // PV-Leistung
      val rawLeistung = gBeta * ui.flaeche * eta
      val leistung: Double? = if (rawLeistung > 0.0) rawLeistung
          else null



          // alles in PvOutputs zurück informieren
        return PVOutData(
            apiOut = apioutputs,
            pvOut = PvOutputs(
            cmf = cmf,
            ghiEff = ghiEff,
            fdir = fdir,
            fdif = fdif,
            dniEff = dniEff,
            dhiEff = dhiEff,
            gBeta = gBeta,
            tCell = tCell,
            eta = eta,
            leistung = leistung
        ))


    }
}

//GHI	    Global Horizontal Irradiance	W/m²
//DNI	    Direct Normal Irradiance	W/m²
//DHI	    Diffuse Horizontal Irradiance	W/m²
//CMF	    Cloud Modification Factor	dimensionslos (0…1)
//C	        Bewölkung	%
//T_air	    Lufttemperatur	°C
//T_cell	    Modultemperatur	°C
//NOCT	    Nominal Operating Cell Temperature	°C - Abhängig von dem Modul
//η_ref	    eta Wirkungsgrad bei Standard Test Conditions (STC)	% oder dimensionslos (0–1)
//γ 	        Gamma Temperaturkoeffizient	%/°C oder 1/°C
//A 	        Modulfläche	m²
//β	        Beta Dachneigung	° (Grad)
//v	        Windgeschwindigkeit	m/s
//P 	        PV-Leistung	W (Watt)
