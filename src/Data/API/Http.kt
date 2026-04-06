package Data.API

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

class Http(
    var apiUrl: String
) {

    var response = HttpDC("", 0)

    fun get() {
        val url: URL = URI.create(apiUrl).toURL()
        val connection: HttpURLConnection =
            url.openConnection() as HttpURLConnection

        try {
            // Request method: GET
            connection.requestMethod = "GET"

            // Response code
            response.code = connection.responseCode
            println("Response Code: ${response.code}")

            if (response.code == HttpURLConnection.HTTP_OK) {
                // Read and collect the response data
                val reader = BufferedReader(
                    InputStreamReader(connection.inputStream)
                )

                val responseBuilder = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    responseBuilder.append(line)
                }

                reader.close()
                response.data = responseBuilder.toString()
            } else {
                response.data = "Error: Unable to fetch data from the API"
            }

            // Close the connection
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
