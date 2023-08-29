package kotlinproj.weather.domain

import jakarta.persistence.*

@Entity
class DateInfo(baseDate:String, baseTime:String,
    maxTemp:Number? = 0, minTemp:Number? = 0) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dateInfo_id", nullable = false)
    val id:Long = 0;

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "dateInfo")
    private val mutableWeatherInfo: MutableList<Weather> = mutableListOf()
    val weatherInfo: List<Weather> get() = mutableWeatherInfo.toList();

    var baseDate:String = baseDate
        protected set
    var baseTime:String = baseTime
        protected set

    var maxTemp:Number? = maxTemp
        protected set
    var minTemp:Number? = minTemp
        protected set






    //=== 연관관계 편의 메서드 ===//
    fun addWeather(weather: Weather) {
        this.mutableWeatherInfo.add(weather);
    }


    //=== toString, equals, hashCode ===//
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DateInfo) return false

        if (id != other.id) return false

        return true
    }
    override fun hashCode(): Int {
        return id.hashCode()
    }
    override fun toString(): String {
        return "DateInfo(id=$id, baseDate='$baseDate', baseTime='$baseTime', maxTemp=$maxTemp, minTemp=$minTemp)"
    }


}