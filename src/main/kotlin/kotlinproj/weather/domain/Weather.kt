package kotlinproj.weather.domain

import jakarta.persistence.*

@Entity
class Weather(
    dateInfo: DateInfo,
    forecastTime: String, temperature: Int, humidity: Int,
    rainPossible: Int, rainfall: Int, skyState:String
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_id")
    val id:Long = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dateInfo_id")
    var dateInfo: DateInfo = dateInfo

    var forecastTime: String = forecastTime
        protected set
    var temperature: Int = temperature
        protected set
    var humidity: Int = humidity
        protected set
    var rainPossibility: Int = rainPossible
        protected set
    var rainfall:Int = rainfall
        protected set
    var skyState:String = skyState
        protected set
}