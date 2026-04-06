import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

// HttpGet Methode um zuerst die gewünschten LAT/LON aus der PLZ zu holen, danach eine Get für den API weather call anhand den LAT/LON

class Http (var apiUrl : String) {

        var responseOut = ""
    //Wichtig: Wenn hier die deklaration ist, dann wird die URL generiert ohne gültigen API-Key, und ZIP, da diese übergaben erst beim call dern funktion get geschiedt, desshalb wurde diese Deklaration auch dorthin verschoben
   /*     var url : URL  = URI.create(apiUrl).toURL()
        private var connection : HttpURLConnection = url.openConnection() as HttpURLConnection
*/
        init {
            responseOut = ""
        }




    fun get ()  {

        //var responseOut = ""
        var url : URL  = URI.create(apiUrl).toURL()
        var connection : HttpURLConnection = url.openConnection() as HttpURLConnection


        try {

            //Request method: GET
            connection.requestMethod = "GET"

            // Response code
            val responseCode: Int = connection.responseCode
            println("Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read and print the response data
                val reader: BufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String?
                val response = StringBuilder()

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                reader.close()
                //println("Response Data: $response")
                responseOut = response.toString()

            } else {
                responseOut = "Error: Unable to fetch data from the API"
               // println("Error: Unable to fetch data from the API")

            }

            // Close the connection
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}


