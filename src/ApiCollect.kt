
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.ZoneId




class ApiCollect : ApiInterface  {

    // Deklarationen Data-Collect Bereich
    var httpZipResponse = ""
    var apiWeatherData = ""
    var apiSolarIrradiation = ""
    var lon = ""
    var lat = ""
    var apiUrl = ""
    var apiUrlZip = ""
    var apiUrlWeather = ""
    var apiUrlIrradiance = ""

    //Objects Data-Collect Bereich
    private var http = Http(apiUrl)  //Object http from Class Http

    //Objects Data-Filter Bereich
    val filterApi = FilterApi(apiWeatherData, apiSolarIrradiation)
    val apioutputs = ApiOutputDataClass(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)







    override fun apiCollect(ui:UserInputDataClass): ApiOutputDataClass {

        apiUrlZip =
            ("https://api.openweathermap.org/geo/1.0/zip?zip=" + ui.zip.toString() + ",CH&appid=" + ui.appId) //https://api.openweathermap.org/data/3.0/onecall?lat={lat}&lon={lon}&exclude={part}&appid={API key}"
        http.apiUrl = apiUrlZip
        println(apiUrlZip) //("${http.apiUrl}")
        http.get()
        httpZipResponse = http.responseOut
        println(httpZipResponse)

        lon = extractLon(httpZipResponse) ?: "0.0"
        lat = extractLat(httpZipResponse) ?: "0.0"
        println("lAT: " + lat + "  LON: " + lon)



        apiUrlWeather =
            ("https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&exclude=minutely,hourly,daily&appid=" + ui.appId)
        http.apiUrl = apiUrlWeather
        println(apiUrlWeather) //("${http.apiUrl}")
        http.get()
        apiWeatherData = http.responseOut
        println(apiWeatherData)


        // "api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&hourly=shortwave_radiation,direct_normal_irradiance,diffuse_radiation&start_date=2025-12-17&end_date=2025-12-17&timezone=Europe/Zurich"

        val SWISS_ZONE = ZoneId.of("Europe/Zurich")
        val today = LocalDate.now(SWISS_ZONE).toString() // YYYY-MM-DD
        val timezoneEncoded = URLEncoder.encode(
            SWISS_ZONE.id,
            StandardCharsets.UTF_8.toString()
        )


        apiUrlIrradiance = ("https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&hourly=shortwave_radiation,direct_normal_irradiance,diffuse_radiation,cloud_cover,temperature_2m,wind_speed_10m&start_date=" + today + "&end_date=" + today + "&timezone=" + timezoneEncoded)
        println(apiUrlIrradiance) //("${http.apiUrl}")
        http.apiUrl = apiUrlIrradiance
        http.get()
        apiSolarIrradiation = http.responseOut
        println(apiSolarIrradiation)


        //-After Call of API Collect and Datas Available: Filter Data and prepare them for class ApiInput
        //Call of the ApiFilter, which calls then => ApiCollect => Http
        //vielleicht hier noch eine Kontrolle einfügen, dass gültige Daten da sind, bevor gefiltert wird!
        filter()

        return apioutputs
    }





    fun extractLat(json: String): String? {
        val regex = """"lat":\s*([0-9.]+)""".toRegex()
        return regex.find(json)?.groups?.get(1)?.value
    }


    fun extractLon(json: String): String? {
        val regex = """"lon":\s*([0-9.]+)""".toRegex()
        return regex.find(json)?.groups?.get(1)?.value
    }




    fun filter() {

        //überlegen ob hier der eigentlich API call Aufruf sein soll oder im Main, auf Jedenfall muss im Main dann sowieso diese methode aufgerufen werden



        //Example of 3 Calls:
        //println(rawWeather)
       // filterApi.rawWeather = apiWeatherData
      //  apioutputs.tAir = filterApi.filterOpenweathermap("temp")
       //     ?: 999.0 // throw IllegalArgumentException("Could not extract numeric value for key 'temp'.")
        //println(apiInputs.tAir)

        /*  key = "pressure" //get .... to be corrected
          apiInputs.dniReal =  filterFromText()  ?: 999.0 //throw IllegalArgumentException("Could not extract numeric value for key 'xy.")
          //println(apiInputs.dniReal)

          key = "humidity" //get .... to be corrected
          apiInputs.dhiReal =  filterFromText()  ?: 999.0 //throw IllegalArgumentException("Could not extract numeric value for key 'yx'.")
          //println(apiInputs.dhiReal)*/

        //println(rawIrradiance)
        filterApi.rawIrradiance = apiSolarIrradiation
        apioutputs.ghiClear = filterApi.filterOpenMeteo("shortwave_radiation") // G(h) / GHI
        apioutputs.dniReal = filterApi.filterOpenMeteo("direct_normal_irradiance") // Gb(n) / DNI
        apioutputs.dhiReal = filterApi.filterOpenMeteo("diffuse_radiation") // Gd(h) / DHI
        apioutputs.cloudCoverPercent = filterApi.filterOpenMeteo("cloud_cover") //
        apioutputs.tAir= filterApi.filterOpenMeteo("temperature_2m") //
        apioutputs.v= filterApi.filterOpenMeteo("wind_speed_10m") //

        //println(apiInputs.dhiReal)

        println(apioutputs)


    }



}









