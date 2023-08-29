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

    //=== 연관관계 편의 메서드 ===//
    fun addDateInfo(dateInfo: DateInfo) {
        this.dateInfo = dateInfo;
        dateInfo.addWeather(this);
    }


    //=== toString, equals, hashCode ===//
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Weather) return false

        if (id != other.id) return false

        return true
    }
    override fun hashCode(): Int {
        return id.hashCode()
    }
    override fun toString(): String {
        return "Weather(id=$id, forecastTime='$forecastTime', temperature=$temperature, humidity=$humidity, rainPossibility=$rainPossibility, rainfall=$rainfall, skyState='$skyState')"
    }


}