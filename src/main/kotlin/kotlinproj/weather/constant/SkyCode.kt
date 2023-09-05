package kotlinproj.weather.constant

enum class SkyCode(val dayNumber: Number, val description: String) {
    SUNNY(1, "맑음"),
    LITTLE_CLOUDY(3, "구름 많음"),
    CLOUDY(4, "흐림"),
}