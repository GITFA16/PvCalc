package Data.API

import Data.ApiOutputDC
import Userinterface.UserInputDC
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.ZoneId

    class ApiCollect : ApiInterface {

        // Deklarationen Data-Collect Bereich
        var httpZipResponse = ""
        var apiWeatherData = ""
        var rawWeatherdata = ""
        var lon = ""
        var lat = ""
        var apiUrl = ""
        var apiUrlZip = ""
        var apiUrlWeatherData = ""


        //Object http from Class API.Http
        private var http = Http(apiUrl)

        //Objects Data-Filter Bereich
        private val filterApi = FilterApi(apiWeatherData)
        private val apioutputs = ApiOutputDC(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0)




        override fun apiCollect(ui: UserInputDC): ApiOutputDC {

            apiUrlZip = ("https://api.openweathermap.org/geo/1.0/zip?zip=" + ui.zip.toString() + ",CH&appid=" + ui.appId) //https://api.openweathermap.org/data/3.0/onecall?lat={lat}&lon={lon}&exclude={part}&appid={API key}"
            http.apiUrl = apiUrlZip
            println(apiUrlZip)
            http.get()
            httpZipResponse = http.response.data
            apioutputs.apiHttpResponse = http.response.code
            println(httpZipResponse)


            if (!isHttpOk(http.response.code)) {
                return apioutputs   //  sofortiger Abbruch
            }


            lon = extractLon(httpZipResponse) ?: "0.0"
            lat = extractLat(httpZipResponse) ?: "0.0"
            println("lAT: " + lat + "  LON: " + lon)


            val SWISS_ZONE = ZoneId.of("Europe/Zurich")
            val today = LocalDate.now(SWISS_ZONE).toString() // YYYY-MM-DD
            val timezoneEncoded = URLEncoder.encode(
                SWISS_ZONE.id,
                StandardCharsets.UTF_8.toString()
            )


            // apiUrlWeatherData = ("https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&hourly=shortwave_radiation,direct_normal_irradiance,diffuse_radiation,cloud_cover,temperature_2m,wind_speed_10m&start_date=" + today + "&end_date=" + today + "&timezone=" + timezoneEncoded)
            apiUrlWeatherData = "https://api.open-meteo.com/v1/forecast?latitude="+lat+"&longitude="+lon+"&current=shortwave_radiation,direct_normal_irradiance,diffuse_radiation,cloud_cover,temperature_2m,wind_speed_10m&timezone="+timezoneEncoded
            println(apiUrlWeatherData)
            http.apiUrl = apiUrlWeatherData
            http.get()
            rawWeatherdata = http.response.data
            apioutputs.apiHttpResponse = http.response.code
            println(rawWeatherdata)


            if (!isHttpOk(http.response.code)) {
                return apioutputs    //  sofortiger Abbruch
            }

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
        filterApi.rawWeatherData = rawWeatherdata
        apioutputs.ghiClear = filterApi.filterOpenMeteo("shortwave_radiation") // G(h) / GHI
        apioutputs.dniReal = filterApi.filterOpenMeteo("direct_normal_irradiance") // Gb(n) / DNI
        apioutputs.dhiReal = filterApi.filterOpenMeteo("diffuse_radiation") // Gd(h) / DHI
        apioutputs.cloudCoverPercent = filterApi.filterOpenMeteo("cloud_cover") //
        apioutputs.tAir= filterApi.filterOpenMeteo("temperature_2m") //
        apioutputs.velocity= filterApi.filterOpenMeteo("wind_speed_10m") //
        println(apioutputs)
    }




        private fun isHttpOk(responseCode: Int): Boolean {
            return when (responseCode) {
                200 -> {
                    println("Response OK")
                    true
                }
                404 -> {
                    println("Response Invalid (404)")
                    false
                }
                else -> {
                    println("Response Error: $responseCode")
                    false
                }
            }
        }

}