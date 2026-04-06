fun main () {




    val myAppId = "937bd0a5e1d6dfe38866bc7014b69d9e"

    println("Berechnung der PV-Leistung")

    println("Dachneigung β in grad (°) eingeben:")
    var beta = readln().toDouble()

    println("Modulfläche (m²) eingeben:")
    var flaeche = readln().toDouble()


    println("Bitte geben Sie Ihre PLZ ein: ")
    var zip = readln().toInt()



    println("Wählen Sie ein PV-Modul:")
    println("1 = Trina Vertex S 400")
    println("2 = JA Solar 400")
    println("3 = LONGi Hi-MO 410")
    println("4 = REC Alpha 400")

    val auswahl = readln().toInt()

    val trina = PvCatalog.Trina_Vertex_S_400
    val jasolar = PvCatalog.JaSolar_400
    val longi = PvCatalog.longi_410


    val modul = when (auswahl) {
        1 -> trina
        2 -> jasolar
        3 -> longi
      //  4 -> trina
        else -> {
            println("Ungültige Auswahl, Standard: Trina 400")
            trina
        }
    }
    println("Sie haben " + modul.name+ "ausgewählt")




/*


    class Userinterface(
        private val calculator: CalcInterface
    ) {
        var zip: String = ""
        var myAppId: String = ""
        var beta: Double = 30.0
        var flaeche: Double = 10.0
        var auswahl: String = ""
        lateinit var modul: PvBase

        fun getoutputData(): PVOutData {
            val userInputs = UserInputDataClass(
                zip = zip,
                appId = myAppId,
                beta = beta,
                flaeche = flaeche,
                pvModul = auswahl
            )

            return calculator.berechnen(
                ui = userInputs,
                modul = modul
            )
        }
    }


*/




    //Dies soll dannn die Klasse User Interface mal vorübergehend ersetzten
        fun ui ( calculator: CalcInterface): PVOutData {
            val userInputs = UserInputDataClass(
                zip = zip,
                appId = myAppId,
                beta = beta,
                flaeche = flaeche,
                pvModul = auswahl
            )

            return calculator.calculation(
                ui = userInputs,
                modul = modul
            )
        }



    val apiCollect = ApiCollect()
    val pvCalc = PvCalculator(apiInterface = apiCollect )
    val ui = ui(pvCalc)

    var hmiOutput =ui.pvOut
    println(hmiOutput)




    /*

        fun getoutputData(): PVOutData {
            val calculator = PvCalculator()
            val userInputs = UserInputDataClass(
                zip = zip,
                appId = myAppId,
                beta = beta,
                flaeche = flaeche,
                pvModul = auswahl
            )

            val outputs = calculator.calculation(
                ui = userInputs,
                modul = modul
            )
           // return outputs.pvOut.leistung
            return outputs

        }*/



}



