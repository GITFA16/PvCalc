package Userinterface

import Calculate.CalcInterface
import Calculate.PvCalculator
import Data.API.ApiCollect
import Data.API.ApiInterface
import Data.CatalogInterface
import Data.PvCatalog
import UserInput
import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class UserInterface : Application() {

    val pvCatalog: CatalogInterface = PvCatalog
    val apiCollect: ApiInterface = ApiCollect()
    val calc: CalcInterface = PvCalculator(apiCollect, pvCatalog)

    val moduleNames = pvCatalog.getModuleNames()

    private val userInput = UserInput(moduleNames)
    private val button = Button("Berechnen")
    private val errorLabel = Label().apply { textFill = Color.RED }
    private val output = TextArea().apply { isEditable = false }

    private val API_KEY = "937bd0a5e1d6dfe38866bc7014b69d9e"

    override fun start(stage: Stage) {
        val root = VBox(10.0, userInput, errorLabel, button, output).apply {
            padding = Insets(15.0)
        }

        button.setOnAction { calculate() }

        stage.scene = Scene(root, 700.0, 800.0)
        stage.title = "PV Rechner"
        stage.show()
    }

    private fun calculate() {
        errorLabel.text = ""
        output.text = ""

        // ZIP prüfen; Modus API oder manuell
        val useApi = userInput.rbApi.isSelected
        val zipText = userInput.tfZip.text.trim()

        if (useApi && !zipText.matches(Regex("\\d{4}"))) {
            errorLabel.text = "PLZ muss aus genau 4 Ziffern bestehen (z.B. 5610)."
            return
        }

        val zip = if (useApi) zipText.toInt() else 0

        // --- Beta / Fläche ---
        val beta = userInput.tfBeta.text.trim().toDoubleOrNull()
        if (beta == null || beta !in 0.0..90.0) {
            errorLabel.text = "Dachneigung muss zwischen 0° und 90° liegen (z.B. 30)."
            return
        }

        val area = userInput.tfFlaeche.text.trim().toDoubleOrNull()
        if (area == null || area <= 0.0) {
            errorLabel.text = "Fläche muss größer als 0 sein (z.B. 10)."
            return
        }

        val modulName: String = userInput.cbModul.value

        button.isDisable = true
        output.text = "Berechnung läuft ..."

        val ui = UserInputDC(
            useApi = useApi,
            zip = zip,
            appId = API_KEY,
            beta = beta,
            flaeche = area,
            pvModul = userInput.cbModul.selectionModel.selectedIndex
        )

//        // 1) Cloud Cover (%)
//        val cloud = when (userInput.cbCloud.value) {
//            "0%  komplett klar"-> 0.0 "xy "
//            "10% fast klar"-> 10.0
//            "30% leicht bewölkt"-> 30.0
//            "50% halb bewölkt"-> 50.0
//            "70% stark bewölkt"-> 70.0
//            "90% sehr stark bewölkt"-> 90.0
//            "100% bedeckt"-> 100.0
//            else -> 50.0
//        }
//
//        // 2) GHI_clear (W/m²)
//        val ghi = when (userInput.cbGhi.value) {
//            "Winter (100–300)" -> 200.0
//            "Frühling/Herbst (400–700)" -> 550.0
//            "Sommer Mittag (800–1000)" -> 900.0
//            "Sommer Maximum (1050–1100)" -> 1075.0
//            else -> 850.0
//        }
//
//        // 3) DNI_real (W/m²)
//        val dni = when (userInput.cbDni.value) {
//            "Sommer klar (700–900)" -> 800.0
//            "Leicht bewölkt (200–600)" -> 400.0
//            "Stark bewölkt (0–100)" -> 50.0
//            "Winter klar (300–600)" -> 450.0
//            else -> 350.0
//        }
//
//        // 4) DHI_real (W/m²)
//        val dhi = when (userInput.cbDhi.value) {
//            "Klarer Himmel (50–150)" -> 100.0
//            "Leicht bewölkt (150–350)" -> 250.0
//            "Stark bewölkt (100–300)" -> 200.0
//            "Nebel/Bedeckt (50–250)" -> 150.0
//            else -> 250.0
//        }
//
//        // 5) Lufttemperatur (°C)
//        val tAir = when (userInput.cbTAir.value) {
//            "Winter (-10 bis +10) " -> 0.0
//            "Übergang (0 bis 20)" -> 10.0
//            "Sommer (15 bis 35)" -> 25.0
//            else -> 15.0
//        }
//
//        // 6) Windgeschwindigkeit (m/s)
//        val wind = when (userInput.cbWind.value) {
//            "ruhig (0–2)" -> 1.0
//            "leicht (2–5)" -> 3.5
//            "windig (5–10)" -> 7.5
//            "stark (10–20)" -> 15.0
//            else -> 5.0
//        }

        val cloud = userInput.cbCloud.value ?: 50.0
        val ghi   = userInput.cbGhi.value   ?: 550.0
        val dni   = userInput.cbDni.value   ?: 400.0
        val dhi   = userInput.cbDhi.value   ?: 250.0
        val tAir  = userInput.cbTAir.value  ?: 10.0
        val wind  = userInput.cbWind.value  ?: 3.5

        // Werte ins UserInputDC schreiben (nur manuell)
        if (!useApi) {
            ui.cloudCoverPercent = cloud
            ui.ghiClear = ghi
            ui.dniReal = dni
            ui.dhiReal = dhi
            ui.tAir = tAir
            ui.velocity = wind
        }

        thread(isDaemon = true) {
            try {
                val pvOutData = calc.calculation(ui, modulName)

                Platform.runLater {
                    val mode = if (ui.useApi) "API" else "Manuell"

                    val powerText = pvOutData.pvOut.power?.let {
                        "%.10f".format(it)
                    } ?: "0.000"

                    val code = pvOutData.apiOut.apiHttpResponse
                    val apiInfo = when (code) {
                        200 -> "API-Aufruf gültig"
                        404 -> "API-Aufruf nicht gültig: Daten konnten nicht abgeholt werden"
                        0 -> ""
                        else -> "Response $code: API-Abholung nicht erfolgreich"
                    }

                    output.text = """
                        Mode: $mode. $apiInfo
                        Modul: $modulName
                        Leistung: $powerText W
                        
                        Cloud Cover: ${"%.1f".format(pvOutData.apiOut.cloudCoverPercent)} %
                        GHI: ${"%.3f".format(pvOutData.apiOut.ghiClear)} W/m²
                        DNI: ${"%.3f".format(pvOutData.apiOut.dniReal)} W/m²
                        DHI: ${"%.3f".format(pvOutData.apiOut.dhiReal)} W/m²
                        T_air: ${"%.2f".format(pvOutData.apiOut.tAir)} °C
                        Wind: ${"%.2f".format(pvOutData.apiOut.velocity)} m/s
                    """.trimIndent()

                    button.isDisable = false
                }
            } catch (e: Exception) {
                Platform.runLater {
                    errorLabel.text = "Fehler bei Berechnung/API."
                    output.text = e.message ?: "Unbekannter Fehler"
                    button.isDisable = false
                }
            }
        }
    }

    override fun stop() = exitProcess(0)
}