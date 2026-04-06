
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
import javafx.scene.transform.Rotate
import javafx.scene.text.Text
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class UserInput (moduleNames:List<String>): HBox(80.0)    {

    // UI Elemente - TextField : Inputs
    val tfZip = TextField("")
    val tfBeta = TextField("")
    val tfFlaeche = TextField("")
    val cbModul = ComboBox<String>() //Kotak Pilihan Dropdown; Privat untuk Gui Class

     //Umschalten
    val tgMode = ToggleGroup()
    val rbApi = RadioButton("Mit API").apply { toggleGroup = tgMode; isSelected = true }
    val rbManual = RadioButton("Ohne API (manuell)").apply { toggleGroup = tgMode }

    val cbCloud = ComboBox<Double>() // %
    val cbGhi   = ComboBox<Double>() // W/m²
    val cbDni   = ComboBox<Double>() // W/m²
    val cbDhi   = ComboBox<Double>() // W/m²
    val cbTAir  = ComboBox<Double>() // °C
    val cbWind  = ComboBox<Double>() // m/s

    init {
        padding = Insets(10.0)

        // 1) Winkelgrafik
        val anglePane = Pane().apply {
            prefWidth = 120.0
            prefHeight = 120.0
        }
        val centerX = 50.0
        val centerY = 100.0
        val radius = 50.0

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

        val angleText = Text(centerX - 20, 25.0, "β = 30°")
        anglePane.children.addAll(baseLine, angleArcGraphic, angleLine, angleText)

        fun updateBeta(beta: Double) {
            val b = beta.coerceIn(0.0, 90.0)
            val rad = b * PI / 180.0

            angleLine.endX = centerX + radius * cos(rad)
            angleLine.endY = centerY - radius * sin(rad)

            angleArcGraphic.length = b
            angleText.text = "β = ${b.toInt()}°"
        }

        // Startwerte
        tfBeta.text = "30"
        // TextField -> Slider + Grafik
        tfBeta.textProperty().addListener { _, _, newValue ->
            val b = newValue.replace(",", ".").toDoubleOrNull() ?: return@addListener
            val clamped = b.coerceIn(0.0, 90.0)
            updateBeta(clamped)
        }


        val betaBox = VBox(5.0, tfBeta)
        val betaRow = HBox(10.0, betaBox, anglePane)

        val comboWidth = 200.0
       listOf(cbCloud, cbGhi, cbDni, cbDhi, cbTAir, cbWind).forEach {
           it.prefWidth = comboWidth
       }

       val leftBox = VBox(
            10.0,
            Label("Modus"), rbApi, rbManual,
            Label("PLZ"), tfZip,
            Label("Dachneigung β (0–90°)"),betaRow,
            Label("Fläche"), tfFlaeche,
            Label("PV Modul"), cbModul
        )

        val rightBox = VBox(10.0).apply {
            children.addAll(
            Label("Manuelle Werte (Ohne API)            "),
                        Label("Wie bewölkt ist der Himmel ?         "), cbCloud, //Cloud Cover C (%)
                        Label("Sonnenstärke (Jahreszeit / Tageszeit)"), cbGhi, //GHI_clear (W/m²) Global Hor. Irra.
                        Label("Direkte Sonneneinstrahlung           "), cbDni, //DNI_real (W/m²) Direct Norm. Irra
                        Label("Gestreutes Licht (durch Wolken)      "), cbDhi, //DHI_real (W/m²) Diffuse Hor. Irra
                        Label("Außentemperatur                      "), cbTAir, // T_air (°C)
                        Label("Windstärke                           "), cbWind //Wind (m/s)
            )
        }

        children.addAll(leftBox, rightBox)

        cbModul.items.addAll(moduleNames)
        cbModul.selectionModel.selectFirst()


        cbCloud.items.addAll(0.0, 10.0, 30.0, 50.0, 70.0, 90.0, 100.0)
        cbCloud.selectionModel.select(50.0)
        cbCloud.setConverter(object : StringConverter<Double>() {
            override fun toString(value: Double?): String = when (value) {
                0.0 -> "Komplett klar"
                10.0 -> "Fast klar"
                30.0 -> "Leicht bewölkt"
                50.0 -> "Halb bewölkt"
                70.0 -> "Stark bewölkt"
                90.0 -> "Sehr stark bewölkt"
                100.0 -> "Bedeckt"
                else -> ""
            }
            override fun fromString(string: String?) = 50.0
        })
        

        cbGhi.items.addAll(200.0, 550.0, 900.0, 1075.0)
        cbGhi.selectionModel.select(550.0)
        cbGhi.setConverter(object : StringConverter<Double>() {
            override fun toString(value: Double?): String = when (value) {
                200.0 -> "Winter"
                550.0 -> "Frühling / Herbst"
                900.0 -> "Sommer Mittag"
                1075.0 -> "Sommer Maximum"
                else -> ""
            }
            override fun fromString(string: String?) = 550.0
        })

        cbDni.items.addAll (800.0, 400.0, 50.0, 450.0)
        cbDni.selectionModel.select(400.0)
        cbDni.setConverter(object : StringConverter<Double>() {
            override fun toString(value: Double?): String = when (value) {
                800.0 -> "Sommer klar"
                400.0 -> "Leicht bewölkt"
                50.0 -> "Stark bewölkt"
                450.0 -> "Winter klar"
                else -> ""
            }
            override fun fromString(string: String?) = 400.0
        })

        cbDhi.items.addAll(100.0, 250.0, 200.0, 150.0)
        cbDhi.selectionModel.select(250.0)
        cbDhi.setConverter(object : StringConverter<Double>() {
            override fun toString(value: Double?): String = when (value) {
                100.0 -> "Klarer Himmel"
                250.0 -> "Leicht bewölkt"
                200.0 -> "Stark bewölkt"
                150.0 -> "Nebel / Bedeckt"
                else -> ""
            }
            override fun fromString(string: String?) = 250.0
        })


        cbTAir.items.addAll(0.0, 10.0, 25.0)
        cbTAir.selectionModel.select(10.0)
        cbTAir.setConverter(object : StringConverter<Double>() {
            override fun toString(value: Double?): String = when (value) {
                0.0 -> "Winter"
                10.0 -> "Übergang"
                25.0 -> "Sommer"
                else -> ""
            }
            override fun fromString(string: String?) = 10.0
        })

        cbWind.items.addAll(1.0,3.5,7.5,15.0)
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

        setManual(false) //default mode API
        tgMode.selectedToggleProperty().addListener { _, _, _ ->
            setManual(rbManual.isSelected)
        }
    }

    fun setManual(manual: Boolean) {
        cbCloud.isDisable = !manual
        cbGhi.isDisable = !manual
        cbDni.isDisable = !manual
        cbDhi.isDisable = !manual
        cbTAir.isDisable = !manual
        cbWind.isDisable = !manual
        tfZip.isDisable = manual
        if (manual) tfZip.clear()
    }

}