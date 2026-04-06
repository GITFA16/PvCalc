package Userinterface

import Data.API.ApiCollect
import Data.PvBase
import Data.PvCatalog
import Calculate.PvCalculator
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class UserInterface : Application() {


    // API Key unsichtbar im UI (Benutzer sieht kein Feld dafür)
    private val API_KEY = "937bd0a5e1d6dfe38866bc7014b69d9e"

    // --- UI Elemente ---TextField : Inputs
    private val tfZip = TextField("")
    private val tfBeta = TextField("")
    private val tfFlaeche = TextField("")

    private val cbModul = ComboBox<String>() //Kotak Pilihan Doprdown; Privat untuk Gui Class
    private val button = Button("Berechnen") //Tombol
    private val titleLabel = Label("PV Leistungsabschätzung")

    // Rückmeldung bei falschen Eingaben
    private val errorLabel = Label("").apply {
        style = "-fx-text-fill: red;" //text yang di apply berwarna merah
    }

    private val output = TextArea().apply {
        isEditable = false  //tidak bisa diubah - read Only
        prefRowCount = 14 // 14 baris text - lebih dari itu scroll kebawah
        isWrapText = true //teks otomatis pindah baris
    }

    private val box = VBox(10.0) // von oben nach unten / Vertikal - Jarak 10 pixel

    override fun start(stage: Stage) { //frame aplikasi




//        membuat layout (VBox, HBox, dll)
//        membuat komponen (Button, Label, TextField)
//        membuat Scene
//        memasang Scene ke Stage
//        menampilkan window (show())
        //stage Rumah . Start Ruangan

        cbModul.items.addAll(
            "Trina Vertex S 400",
            "JA Solar JAM72S10 400",
            "LONGi Hi-MO 5m 410"
        )

//        cbModul.setCellFactory {
//            object : ListCell<API.PvBase>() {
//                override fun updateItem(item: API.PvBase?, empty: Boolean) {
//                    super.updateItem(item, empty)
//                    text = if (empty || item == null) "" else item.name
//                }
//            }
//        }
//        cbModul.buttonCell = object : ListCell<API.PvBase>() {
//            override fun updateItem(item: API.PvBase?, empty: Boolean) {
//                super.updateItem(item, empty)
//                text = if (empty || item == null) "" else item.name
//            }
//        }


        cbModul.selectionModel.selectFirst() //Pilihan pertama sebagai default // Erste Wahl als default :-)

        box.children.addAll(
            titleLabel,

            Label("PLZ (4 Ziffern)"),
            tfZip,

            Label("Dachneigung β (0..90°)"),
            tfBeta,

            Label("Fläche (m²)"),
            tfFlaeche,

            Label("PV Modul"),
            cbModul,

            errorLabel,
            button,
            output
        )

        button.setOnAction { makeCalc() } //klick button  - Fungsi Berechnen soll starten

        with(stage) {  //stage rumahnya
            scene = Scene(box, 520.0, 650.0)  //ukurannya
            title = "PV Rechner" //judul
            isResizable = false
            setOnCloseRequest { exit() }
            show()
        }
    }









    private fun makeCalc() {

        // alte Meldungen löschen
        errorLabel.text = ""
        output.text = ""



        // PLZ prüfen (String -> Int)
        val zipText = tfZip.text.trim() //trim hapus spasi depan blakang
        if (!zipText.matches(Regex("\\d{4}"))) {
            errorLabel.text = "PLZ muss aus genau 4 Ziffern bestehen (z.B. 5610)."
            return
        }
        val zip = zipText.toInt()

        // Dachneigung prüfen (String -> Double)
        val betaText = tfBeta.text.trim()
        val beta = betaText.toDoubleOrNull()
        if (beta == null) { //kein wert und nicht gleich wie "0"
            errorLabel.text = "Dachneigung muss eine Zahl sein (z.B. 30)."
            return
        }
        if (beta !in 0.0..90.0) {
            errorLabel.text = "Dachneigung muss zwischen 0° und 90° liegen."
            return
        }

        // --- Fläche prüfen (String -> Double) ---
        val flaecheText = tfFlaeche.text.trim()
        val flaeche = flaecheText.toDoubleOrNull()
        if (flaeche == null) {
            errorLabel.text = "Fläche muss eine Zahl sein (z.B. 10)."
            return
        }
        if (flaeche <= 0.0) {
            errorLabel.text = "Fläche muss größer als 0 sein."
            return
        }

        // --- Modul auswählen ---
//        val modul = when (cbModul.selectionModel.selectedIndex) {
//            0 -> API.PvCatalog.Trina_Vertex_S_400
//            1 -> API.PvCatalog.JaSolar_400
//            2 -> API.PvCatalog.longi_410
//            else -> API.PvCatalog.Trina_Vertex_S_400
//        }

        val modul: PvBase = when (cbModul.value) {
            "Trina Vertex S 400" -> PvCatalog.Trina_Vertex_S_400
            "JA Solar JAM72S10 400" -> PvCatalog.JaSolar_400
            "LONGi Hi-MO 5m 410" -> PvCatalog.longi_410
            else -> PvCatalog.Trina_Vertex_S_400
        }

        // --- Inputs bauen ---
        val ui = UserInputDC(
            zip = zip,
            appId = API_KEY, //privat siehe oben
            beta = beta,
            flaeche = flaeche,
            pvModul = cbModul.selectionModel.selectedIndex
        )

        // UI sperren + Status
        button.isDisable = true  //user  tidak bisa klick berkali2 saat proses berjalan
        output.text = "Berechnung läuft ..."

        // Hintergrund-Thread, damit UI nicht einfriert
//        | Thread biasa                 | Daemon thread          |
//        | Menahan aplikasi tetap hidup | Tidak menahan aplikasi |
//        | Harus selesai dulu           | Bisa diputus otomatis  |

        thread(isDaemon = true) {
            try {
                val apiCollect = ApiCollect()
                val calc = PvCalculator(apiCollect)
                val PVOutData = calc.calculation(ui, modul)



                Platform.runLater { // führt Code im JavaFX-UI-Thread aus und die Warteschlange des UI-Threads gelegt
                    val leistungText =
                        PVOutData.pvOut.power?.let { "%.3f".format(it) } ?: "0.000"

                    output.text = """
                        Ergebnis
                        Leistung: $leistungText W
                        
                        🌦️ API-Werte (aktuelle Stunde)
                        Cloud Cover: ${"%.1f".format(PVOutData.apiOut.cloudCoverPercent)} %
                        GHI: ${"%.3f".format(PVOutData.apiOut.ghiClear)} W/m²
                        DNI: ${"%.3f".format(PVOutData.apiOut.dniReal)} W/m²
                        DHI: ${"%.3f".format(PVOutData.apiOut.dhiReal)} W/m²
                        T_air: ${"%.2f".format(PVOutData.apiOut.tAir)} °C
                        Wind: ${"%.2f".format(PVOutData.apiOut.velocity)} m/s
                        
                        Responsecode: ${PVOutData.apiOut.apiHttpResponse} 
                        Modul: ${modul.name}
                    """.trimIndent() //merapikan kalau lebih dari 1 baris

                    button.isDisable = false //setelah proses selesai bisa di klik kembali
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

    fun exit() {  // setOnCloseRequest { exit() } --> Saubere Outcome
        exitProcess(0)
    }
}