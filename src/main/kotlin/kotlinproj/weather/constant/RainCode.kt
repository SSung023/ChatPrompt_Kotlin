package kotlinproj.weather.constant

enum class RainCode(val dateType:Number, val description:String) {
    NONE(0, "없음"),
    RAIN(1, "비"),
    RAIN_AND_SNOW(2, "비/눈"),
    SNOW(3, "눈"),
    SHOWER(4, "소나기")
}