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


inputs
GHI_clear           [W/m²]
cloudCoverPercent   C [%]
tAir                T_air [°C]
noct                NOCT [°C]
etaRef              eta η_ref bei STC [0..1]
gamma               gamma γ Temperaturkoeff. [1/°C], z.B. -0.004
area                Modulfläche A [m²]
betaDeg             beta Dachneigung β [°]
windSpeed           v [m/s], optional
a = 0.7             Parameter a für Gβ DHI
b = 0.3             Prameter b für Gβ DNI

outputs
cmf = 0       Cloud Modification Factor
ghiEff = 0    GHI_eff
gBeta = 0     Einstrahlung auf geneigte Fläche
tCell = 0     Modultemperatur
eta = 0       Temperaturabhängiger Wirkungsgrad
powerW = 0    Leistung P [W]

init { }      einmaliger Aufruf pro Objekt --Konstruktor Anwendung

PlantUmlFile
| Symbol | Bedeutung        | Sichtbarkeit in Kotlin | Bedeutung                |
| ------ | ---------------- | ---------------------- | ------------------------ |
| +      | public           | `public` (Standard)    | Von überall sichtbar     |
| -      | private          | `private`              | Nur innerhalb der Klasse |
| #      | protected        | `protected`            | Klasse + Subklassen      |
| ~      | package/internal | `internal`             | innerhalb des Moduls     |

Class - Eigentschaften und Methode







