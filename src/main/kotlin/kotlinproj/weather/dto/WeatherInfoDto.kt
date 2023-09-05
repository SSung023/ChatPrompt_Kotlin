package kotlinproj.weather.dto

data class WeatherInfoDto(
    val temp:String? = "", // 1시간 동안의 기온
    val humidity:String? = "", // 1시간 동안의 습도
    val rainPossibility: String? = "", // 강수확률 POP
    val rainAmount:String? = "", // 1시간 강수량 PCP
    val sky:String? = "", // 하늘 상태
)
