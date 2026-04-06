//import kotlin.math.cos
//import kotlin.math.pow
//
//
//
// --- HAUPTPROGRAMM --- //
//
//fun main() {
//
//    println("Bewölkung in % eingeben:")
//        0 %	komplett klarer Himmel
//        10 %	fast klar
//        30 %	leicht bewölkt
//        50 %	halb bewölkt
//        70 %	stark bewölkt
//        90 %	sehr stark bewölkt
//        100 %	komplett bedeckt / kein direkter Sonnenschein
//  val cloudCoverPercent = readln().toDouble()
//
//    println("GHI_clear in W/m² eingeben:")
// GHI Klar = theoritische Einsatrahlung bei klaren Himmel
//    Sonnenstand	        Typischer           GHI_clear	Einheit
//    Winter,               tiefstehende Sonne	100–300	    W/m²
//    Frühling/Herbst,      gute Sonne	        400–700	    W/m²
//    Sommer, Mittag,       klarer Himmel	    800–1000	W/m²
//    Sommer-Maximum	                        1050–1100	W/m²
//    val ghiKlar = readln().toDouble()
//
//    println("Dachneigung β in grad (°) eingeben:")
//    val beta = readln().toDouble()
//Dachneigung ist nicht abhängig von API - von USER
//
//    println("DHI (Diffuse Horrizontal Irradiance) werte in W/m² eingeben:")
//    val dhiReal = readln().toDouble()
//
//    println("DNI (Direct Normal Irradiance) werte in W/m² eingeben:")
//    val dniReal = readln().toDouble()
//    Wetterzustand	                    DHI (W/m²)	DNI (W/m²)
//    Klarer Himmel (Sommer, Mittag)	50–150      700–900
//    Leicht bewölkt	                150–350     200–600
//    Stark bewölkt	                    100–300	    0–50
//    Winter – klar	                    50–150	    300–600
//
//    println("Lufttemperatur (°C) eingeben:")
//    val tAir = readln().toDouble()
//    Zustand / Saison	    T_air(°C)
//    Winter (CH)	        –5 bis +5
//    Frühling	            5–15
//    Sommer	            20–35
//    Hochsommer (heiß)	    30–40
//    Bewölkt / Regen	    10–20
//    Jahresmittel Schweiz	10–12
//
//    println("NOCT (Nominal Operating Cell Temperature) eingeben:")
//    val noct = readln().toDouble()
//    PV-Technologie	                    Typischer NOCT (°C)
//    Monokristallin Standard	            44–48 °C
//    Polykristallin	                    45–48 °C
//    Hochleistungsmodule (TOPCon / HJT)	40–45 °C
//    Dünnschichtmodule	                    43–47 °C
//    Billigere Module	                    47–50 °C
//    Diese Daten sind nicht von API abhängig
//    println("Windgeschwindigkeit in (m/s) eingeben: ")
//    val v = readln().toDoubleOrNull()
//    val v: Double? = readln().toDoubleOrNull()
//
//
//    println("eta_ref/n_ref Werte -Wirkungsgrad bei 25°C, bei Standard Test Conditions eingeben (z.B. 0.20) oder in %:")
//
//
//    Modultyp	                            η_ref (typisch)
//    Monokristallin Standard	            18–21 %
//    Hochleistungsmono (TOPCon / HJT)	    21–23 %
//    Polykristallin	                    16–18 %
//    Dünnschicht	                        10–14 %
//    Diese Daten sind nicht von API abhängig
//    val etaRef = readln().toDouble()        //η_ref
//
//
//    println("Temperaturkoeffizient - gamma Werte (Y) eingeben (z.B. -0.004):")
//    Modultyp	                         γ (pro °C)
//    Monokristallin Standard	        −0.35 %/°C (−0.0035/°C)
//    Hochleistungsmono (TOPCon)	    −0.30 %/°C (−0.0030/°C)
//    HJT/IBC (Premium)	                −0.25 %/°C (−0.0025/°C)
//    Polykristallin	                −0.40 %/°C (−0.0040/°C)
//    Dünnschicht	                    −0.20 %/°C (−0.0020/°C)
//    Diese Daten sind nicht von API abhängig
//    val gamma = readln().toDouble()
//
//    println("Modulfläche (m²) eingeben:")
//    val flaeche = readln().toDouble()
//    von User
//
//
//
//Berechnung
//
//    val cmf = cmf(cloudCoverPercent)
//    val ghiEff = ghiEff(ghiKlar, cmf)
//    val fdir = fdir(dniReal, ghiEff)
//    val fdif = fdif(dhiReal, ghiEff)
//    val dniEff = dniEff(fdir, ghiEff)
//    val dhiEff = dhiEff(fdif, ghiEff)
//    val gBeta = gBeta(ghiEff, beta, dhiEff,dniEff )
//    val tCell = tCell(tAir, gBeta, noct, v)
//    val eta = eta(etaRef, gamma, tCell)
//    val leistung = leistung(gBeta, flaeche, eta)
//
//
//Output
//
//    println("\n--- Ergebnisse ---")
//    println("CMF: $cmf")
//    println("GHI_eff: $ghiEff W/m²")
//    println("DNI_eff: $dniEff W/m²")
//    println("DHI_eff: $dhiEff W/m²")
//    println("G_beta: $gBeta W/m²")
//    println("Modultemperatur: $tCell °C")
//    println("eta: $eta")
//    println("PV-Leistung: $leistung W")
//}
//
//Berechnung
//
//fun cmf (cloudCoverPercent: Double): Double {
//    val c = cloudCoverPercent / 100.0
//    return 1.0 - 0.75 * c.pow(3.4)
//}
//
//fun ghiEff(ghiKlar: Double, cmf: Double): Double =
//    ghiKlar * cmf
//
//fun fdir (dni: Double, ghiEff: Double): Double =
//    dni / ghiEff
//
//fun fdif (dhi: Double, ghiEff: Double): Double =
//    dhi / ghiEff
//
//fun dniEff (fdir: Double, ghiEff: Double): Double =
//    fdir * ghiEff
//
//fun dhiEff(fdif: Double, ghiEff: Double): Double =
//    fdif * ghiEff
//
//fun gBeta(ghiEff: Double, beta: Double, dhiEff: Double, dniEff: Double): Double {
//    val betaRad = Math.toRadians(beta)
//    return ghiEff * (dhiEff + dniEff * cos(betaRad))
//}
//
//fun tCell(tAir: Double, gBeta: Double, noct: Double, v: Double?): Double {
//    val tCell0 = tAir + gBeta / 800.0 * (noct - 20.0)
//    return if (v!= null) tCell0 - 0.05 * v
//    else tCell0
//}
//
//fun eta(etaRef: Double, gamma: Double, tCell: Double): Double =
//    etaRef * (1.0 + gamma * (tCell - 25.0))
//
//fun leistung(gBeta: Double, flaeche: Double, eta: Double): Double =
//    gBeta * flaeche * eta
//
