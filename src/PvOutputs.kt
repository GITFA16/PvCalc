import kotlin.math.cos
import kotlin.math.pow

data class PvOutputs(
    private val cmf: Double,
    private val ghiEff: Double,
    private val fdir: Double,
    private val fdif: Double,
    private val dniEff: Double,
    private val dhiEff: Double,
    private val gBeta: Double,
    private val tCell: Double,
    private val eta: Double,
    val leistung: Double?)

/*
GHI	    Global Horizontal Irradiance	W/m²
DNI	    Direct Normal Irradiance	W/m²
DHI	    Diffuse Horizontal Irradiance	W/m²
CMF	    Cloud Modification Factor	dimensionslos (0…1)
C	        Bewölkung	%
T_air	    Lufttemperatur	°C
T_cell	    Modultemperatur	°C
NOCT	    Nominal Operating Cell Temperature	°C - Abhängig von dem Modul
η_ref	    eta Wirkungsgrad bei Standard Test Conditions (STC)	% oder dimensionslos (0–1)
γ 	        Gamma Temperaturkoeffizient	%/°C oder 1/°C
A 	        Modulfläche	m²
β	        Beta Dachneigung	° (Grad)
v	        Windgeschwindigkeit	m/s
P 	        PV-Leistung	W (Watt)
*/


