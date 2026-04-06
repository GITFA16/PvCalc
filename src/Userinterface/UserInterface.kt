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

        stage.scene = Scene(root, 550.0, 750.0)
        stage.title = "PV Rechner"
        stage.isResizable = false
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

        val ghi   = userInput.cbGhi.value   ?: 450.0
        val dhi = userInput.cbDhi.value ?: 250.0
        val tAir  = userInput.cbTAir.value  ?: 10.0
        val wind  = userInput.cbWind.value  ?: 3.5


        // Werte ins UserInputDC schreiben (nur manuell)
        if (!useApi) {
            ui.ghiClear = ghi
            ui.dhiReal = dhi
            ui.tAir = tAir
            ui.velocity = wind

        }

        thread(isDaemon = true) {
            try {
                val pvOutData = calc.calculation(ui, modulName)

                Platform.runLater {
                    val mode = if (ui.useApi) "API" else "Manuell"
                    val powerText = "%.2f".format(pvOutData.pvOut.power)
                    val code = pvOutData.apiOut.apiHttpResponse


                   if (mode == "API") {
                        output.text = when (code) {

                            200 -> """
                                Mode: API
                                API-Aufruf gültig
                                Modul: $modulName
                                Leistung: $powerText W
                        
                                Cloud Cover: ${"%.1f".format(pvOutData.apiOut.cloudCoverPercent)} %
                                GHI: ${"%.3f".format(pvOutData.apiOut.ghiClear)} W/m²
                                DNI: ${"%.3f".format(pvOutData.apiOut.dniReal)} W/m²
                                DHI: ${"%.3f".format(pvOutData.apiOut.dhiReal)} W/m²
                                T_air: ${"%.2f".format(pvOutData.apiOut.tAir)} °C
                                Wind: ${"%.2f".format(pvOutData.apiOut.velocity)} m/s
                            """.trimIndent()

                            404, 0 -> "API-Aufruf nicht gültig: Daten konnten nicht abgeholt werden.\r\nBitte Prüfen ob PLZ gültig ist oder Wetterdienste Online sind:\r\nhttps://openweathermap.org\r\nhttps://open-meteo.com/"

                            else -> "Response $code: API-Abholung nicht erfolgreich, bitte Internetverbindung prüfen"
                        }
                    }

                    else if (mode == "Manuell") {
                       output.text = """
                                Mode: Manuell
                                API-Aufruf gültig
                                Modul: $modulName
                                Leistung: $powerText W
                        
                                Cloud Cover: ${"%.1f".format(pvOutData.apiOut.cloudCoverPercent)} %
                                GHI: ${"%.3f".format(pvOutData.apiOut.ghiClear)} W/m²
                                DNI: ${"%.3f".format(pvOutData.apiOut.dniReal)} W/m²
                                DHI: ${"%.3f".format(pvOutData.apiOut.dhiReal)} W/m²
                                T_air: ${"%.2f".format(pvOutData.apiOut.tAir)} °C
                                Wind: ${"%.2f".format(pvOutData.apiOut.velocity)} m/s
                            """.trimIndent()
                    }


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