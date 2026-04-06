package Data.API

import Userinterface.UserInputDC
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.ZoneId

    class ApiCollect : ApiInterface {

        // Deklarationen Data-Collect Bereich
        var httpZipResponse = ""
        var apiWeatherData = ""
        var apiSolarIrradiation = ""
        var lon = ""
        var lat = ""
        var apiUrl = ""
        var apiUrlZip = ""
        var apiUrlIrradiance = ""


        //Objects Data-Collect Bereich
        private var http = Http(apiUrl)  //Object http from Class API.Http

        //Objects Data-Filter Bereich
        private val filterApi = ApiFilter(apiWeatherData)
        private val apioutputs = ApiOutputDC(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0)



        override fun apiCollect(ui: UserInputDC): ApiOutputDC {


            apiUrlZip = ("https://api.openweathermap.org/geo/1.0/zip?zip=" + ui.zip.toString() + ",CH&appid=" + ui.appId) //https://api.openweathermap.org/data/3.0/onecall?lat={lat}&lon={lon}&exclude={part}&appid={API key}"
            http.apiUrl = apiUrlZip
            println(apiUrlZip) //("${http.apiUrl}")
            http.get()
            httpZipResponse = http.response.data
            apioutputs.apiHttpResponse = http.response.code
            println(httpZipResponse)


            //nominatim.openstreetmap.org/search?postalcode=5225&format=json


            lon = extractLon(httpZipResponse) ?: "0.0"
            lat = extractLat(httpZipResponse) ?: "0.0"
            println("lAT: " + lat + "  LON: " + lon)


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
            apiSolarIrradiation = http.response.data
            apioutputs.apiHttpResponse = http.response.code
            println(apiSolarIrradiation)


        when {
            http.response.code == 200 -> {
                filter()
                println("Response OK")
            }
            http.response.code == 404 -> {
                println("Response Invalid")
            }
        }


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
        filterApi.rawIrradiance = apiSolarIrradiation
        apioutputs.ghiClear = filterApi.filterOpenMeteo("shortwave_radiation") // G(h) / GHI
        apioutputs.dniReal = filterApi.filterOpenMeteo("direct_normal_irradiance") // Gb(n) / DNI
        apioutputs.dhiReal = filterApi.filterOpenMeteo("diffuse_radiation") // Gd(h) / DHI
        apioutputs.cloudCoverPercent = filterApi.filterOpenMeteo("cloud_cover") //
        apioutputs.tAir= filterApi.filterOpenMeteo("temperature_2m") //
        apioutputs.velocity= filterApi.filterOpenMeteo("wind_speed_10m") //

        println(apioutputs)
    }



}