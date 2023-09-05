package kotlinproj.weather.constant

enum class WeatherCode(val description: String) {
    TMP("1시간 기온"),
    TMX("최고 기온"),
    TMN("최저 기온"),
    POP("강수 확률"),
    PCP("1시간 강수량"),
    PTY("강수 형태"),
    SNO("1시간 적설량"),
    REH("습도"),
    SKY("하늘 상태")
}