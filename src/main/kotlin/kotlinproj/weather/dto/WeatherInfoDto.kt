package kotlinproj.weather.dto

data class WeatherInfoDto(
    val temp:Double? = 0.0, // 1시간 동안의 기온
    val humidity:Int? = 0, // 1시간 동안의 습도 ex)
    val rainPossibility: Int? = 0, // 강수확률 POP ex) 30, 60
    val rainAmount:String? = "", // 1시간 강수량 PCP ex) 강수없음, 1.2
    val sky:String? = "", // 하늘 상태 ex) 맑음, 흐림
)
