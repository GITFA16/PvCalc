import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Arc
import javafx.scene.shape.ArcType
import javafx.scene.shape.Line
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class UserInput(moduleNames: List<String>) : HBox(80.0) {


    // UI Elemente - TextField : Inputs
    val tfZip = TextField("")
    val tfBeta = TextField("")
    val tfFlaeche = TextField("")
    val cbModul = ComboBox<String>() // Kotak Pilihan Dropdown; Privat untuk Gui Class


    // Umschalten
    val tgMode = ToggleGroup()
    val rbApi = RadioButton("Mit API").apply { toggleGroup = tgMode; isSelected = true }
    val rbManual = RadioButton("Ohne API (manuell)").apply { toggleGroup = tgMode }


    // Manuelle Eingaben
    val cbGhi = ComboBox<Double>()  // W/m²
    val cbCloud = ComboBox<Double>()  // W/m²
    val cbTAir = ComboBox<Double>() // °C
    val cbWind = ComboBox<Double>() // m/s

    init {

        // Allgemeines Layout
        padding = Insets(5.0)

        // 1) Winkelgrafik
        val anglePane = Pane().apply {
            prefWidth = 100.0
            prefHeight = 110.0
        }

        val centerX = 5.0
        val centerY = 100.0
        val radius = 75.0

        val baseLine = Line(centerX, centerY, centerX + radius, centerY).apply {
            strokeWidth = 3.0
        }

        val angleLine = Line(centerX, centerY, centerX + radius, centerY).apply {
            strokeWidth = 4.0
        }

        val angleArcGraphic = Arc().apply {
            this.centerX = centerX
            this.centerY = centerY
            radiusX = 28.0
            radiusY = 28.0
            startAngle = 0.0
            length = 0.0
            type = ArcType.OPEN
            stroke = Color.DODGERBLUE
            strokeWidth = 3.0
            fill = Color.TRANSPARENT
        }

        anglePane.children.addAll(baseLine, angleArcGraphic, angleLine) // angleText

        fun updateBeta(beta: Double) {
            val b = beta.coerceIn(0.0, 90.0)
            val rad = b * PI / 180.0

            angleLine.endX = centerX + radius * cos(rad)
            angleLine.endY = centerY - radius * sin(rad)
            angleArcGraphic.length = b
        }

        // TextField -> Slider + Grafik
        tfBeta.textProperty().addListener { _, _, newValue ->
            val b = newValue.replace(",", ".").toDoubleOrNull() ?: return@addListener
            val clamped = b.coerceIn(0.0, 90.0)
            updateBeta(clamped)
        }

        val betaBox = VBox(5.0, tfBeta, anglePane)


        // 2) ComboBox-Layout (Breiten)
        val comboWidth = 200.0
        listOf(cbGhi, cbCloud, cbTAir, cbWind).forEach {
            it.prefWidth = comboWidth
        }


        // 3) Linke / Rechte Seite (Layout)
        val leftBox = VBox(
            10.0,
            Label("Modus"), rbApi, rbManual,
            Label("PLZ"), tfZip,
            Label("Dachneigung β (0–90°)"),
            betaBox,
            Label("Fläche"), tfFlaeche,
            Label("PV Modul"), cbModul
        )

        val rightBox = VBox(14.0).apply {
            children.addAll(
                Label("Wert-Eingabe (Ohne API)"),
                Label("Sonnenstärke "), cbGhi,           // GHI_clear (W/m²) Global Hor. Irra.
                Label("Bewölkung "), cbCloud,    // Cloud Bewölkung
                Label("Außentemperatur (°C)"), cbTAir,       // T_air (°C)
                Label("Windstärke (m/s)"), cbWind            // Wind (m/s)
            )
        }

        children.addAll(leftBox, rightBox)

        // 4) PV Modul Auswahl
        cbModul.items.addAll(moduleNames)
        cbModul.selectionModel.selectFirst()

        // 5) GHI Setup
        cbGhi.items.addAll(
            50.0,
            200.0,
            450.0,
            700.0,
            900.0,
            1050.0,
            1125.0
        )

        cbGhi.selectionModel.select(450.0)
        cbGhi.setConverter(object : StringConverter<Double>() {
            override fun toString(value: Double?): String = when (value) {
                50.0 -> "Nacht/Dämmerung"
                200.0 -> "Wintertypisch/tiefer Sonnenstand"
                450.0 -> "Frühling/Herbst typisch"
                700.0 -> "Frühling/Herbst sonnig (Vormittag–Nachmittag)"
                900.0 -> "Sommer sonnig (Vormittag/Nachmittag)"
                1050.0 -> "Sommer sehr sonnig (nahe Mittag)"
                1125.0 -> "Sommer Mittag (Maximum)"
                else -> ""
            }
            override fun fromString(string: String?) = 450.0
        })


        // 6) Bewölkung C in % (Double Werte, Anzeige Text)
        cbCloud.items.addAll(
            0.0,     // Klar Frac for fraction
                        25.0,    // Leicht bewölkt
                        50.0,    // Teilweise bewölkt
                        75.0,    // Stark bewölkt
                        100.0    // Bedeckt
        )

        cbCloud.selectionModel.select(25.0)
        cbCloud.setConverter(object : StringConverter<Double>() {
            override fun toString(value: Double?): String = when (value) {
                0.0  -> "Klar"
                25.0 -> "Leicht bewölkt"
                50.0 -> "Teilweise bewölkt"
                75.0 -> "Stark bewölkt"
                100.0-> "Bedeckt"
                else -> ""
            }
            override fun fromString(string: String?) = 350.0
        })


        // 7) Temperatur Setup
        cbTAir.items.addAll(
            0.0,
            23.0,
            28.0,
            32.0
        )

        cbTAir.selectionModel.select(23.0)
        cbTAir.setConverter(object : StringConverter<Double>() {
            override fun toString(value: Double?): String = when (value) {
                0.0 -> "Kalt"
                23.0 -> "Angenehm"
                28.0 -> "Heiss"
                32.0 -> "Sehr heiss"
                else -> ""
            }
            override fun fromString(string: String?) = 25.0
        })


        // 8) Wind Setup
        cbWind.items.addAll(
            1.0,
            3.5,
            7.5,
            15.0
        )

        cbWind.selectionModel.select(3.5)
        cbWind.setConverter(object : StringConverter<Double>() {
            override fun toString(value: Double?): String = when (value) {
                1.0 -> "Ruhig"
                3.5 -> "Leicht"
                7.5 -> "Windig"
                15.0 -> "Stark"
                else -> ""
            }
            override fun fromString(string: String?) = 3.5
        })


        // 9) Mode / Enable-Disable Handling
        setManual(false) // default mode API
        tgMode.selectedToggleProperty().addListener { _, _, _ ->
            setManual(rbManual.isSelected)
        }
    }

    fun setManual(manual: Boolean) {
        cbGhi.isDisable = !manual
        cbCloud.isDisable = !manual
        cbTAir.isDisable = !manual
        cbWind.isDisable = !manual
        tfZip.isDisable = manual
        if (manual) tfZip.clear()
    }
}