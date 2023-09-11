package kotlinproj.weather.dto

data class WeatherInfoDto(
    val temp:String? = "", // 1시간 동안의 기온
    val humidity:String? = "", // 1시간 동안의 습도 ex)
    val rainPossibility: String? = "", // 강수확률 POP ex) 30, 60
    val rainAmount:String? = "", // 1시간 강수량 PCP ex) 강수없음, 1.2
    val sky:String? = "", // 하늘 상태
)
