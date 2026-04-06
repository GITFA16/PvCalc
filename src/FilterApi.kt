import java.time.ZoneId
import java.time.ZonedDateTime



class FilterApi (
        var rawWeather : String, // apiCollect.apiWeatherData
        var rawIrradiance : String) {

/*

        //This Function Acts as a Filter for the Response of the general API Call of the openewather.org
        //https://api.openweathermap.org/data/2.5/weather?lat=47.4891&lon=8.145&exclude=minutely,hourly,daily&appid=937bd0a5e1d6dfe38866bc7014b69d9e


        fun filterOpenweathermap (key: String): Double? {

            val allowDecimalComma: Boolean = true
            val lkey = "\"$key\""
            val iKey = rawWeather.indexOf(lkey)
            if (iKey < 0) return null

            var i = iKey + lkey.length

            // Skip whitespace until ':'
            while (i < rawWeather.length && rawWeather[i].isWhitespace()) i++
            if (i >= rawWeather.length || rawWeather[i] != ':') return null


            // Skip whitespace before the value
            while (i < rawWeather.length && rawWeather[i].isWhitespace()) i++

            // Optional quotes around value
            val quoted = i < rawWeather.length && rawWeather[i] == '"'
            if (quoted) i++

            val start = i
            while (i < rawWeather.length) {
                val c = rawWeather[i]
                if (!(c.isDigit() || c == '+' || c == '-' || c == '.' || c == 'e' || c == 'E')) break
                i++
            }
            if (i == start) return null

            val s = rawWeather.substring(start, i)
            return s.toDoubleOrNull()
        }
*/



    fun filterOpenMeteo (fieldName: String): Double {
            // 1) Find the "hourly" object start
            val hourlyKey = "\"hourly\":"
            val hourlyKeyIndex = rawIrradiance.indexOf(hourlyKey)
            if (hourlyKeyIndex == -1) error("hourly object not found")

            val hourlyObjStart = rawIrradiance.indexOf('{', hourlyKeyIndex)
            if (hourlyObjStart == -1) error("hourly object start '{' not found")

            // 2) Find the matching closing brace '}' for the hourly object
            var braceCount = 0
            var hourlyObjEnd = -1
            for (i in hourlyObjStart until rawIrradiance.length) {
                when (rawIrradiance[i]) {
                    '{' -> braceCount++
                    '}' -> {
                        braceCount--
                        if (braceCount == 0) {
                            hourlyObjEnd = i
                            break
                        }
                    }
                }
            }
            if (hourlyObjEnd == -1) error("hourly object end '}' not found")

            // Create a substring containing only the hourly object to avoid matching hourly_units keys
            val hourlyBlock = rawIrradiance.substring(hourlyObjStart, hourlyObjEnd + 1)

            // 3) Find the requested field inside the hourly block
            val key = "\"$fieldName\""
            val keyIndex = hourlyBlock.indexOf(key)
            if (keyIndex == -1) error("$fieldName not found inside hourly object")

            // 4) Extract the [ ... ] array after that key
            val arrayStart = hourlyBlock.indexOf('[', keyIndex)
            if (arrayStart == -1) error("Array start '[' for $fieldName not found")

            val arrayEnd = hourlyBlock.indexOf(']', arrayStart)
            if (arrayEnd == -1) error("Array end ']' for $fieldName not found")

            val content = hourlyBlock.substring(arrayStart + 1, arrayEnd).trim()
           // if (content.isEmpty()) return


            // 5) Parse doubles SIMPLER BUT with baisc Syntax
            // return content.split(',').map { it.trim().toDouble() }


            var list =  parseDoubleList(content)
            var value = valueAtCurrentSwissHour(list)
            //println(value)
            return value
    }



        //Convert from a String of Values from Time 0...23h into a List of doubles
        fun parseDoubleList(content: String): List<Double> {
            val parts = content.split(',')

            val result = mutableListOf<Double>()

            for (part in parts) {
                val trimmed = part.trim()
                val value = trimmed.toDouble()
                result.add(value)
            }

            return result
        }


        //Taking the actual hours and take the hour as index and returning the value of the currecnt hour
        fun valueAtCurrentSwissHour(values: List<Double>): Double {
            val SWISS_ZONE = ZoneId.of("Europe/Zurich")
            val hour = ZonedDateTime.now(SWISS_ZONE).hour
            //println(hour)
            require(values.size > hour) { "Expected at least ${hour + 1} values, got ${values.size}" }
            return values[hour]
        }




    }




