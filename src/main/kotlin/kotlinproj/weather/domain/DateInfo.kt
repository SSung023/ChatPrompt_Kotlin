package kotlinproj.weather.domain

import jakarta.persistence.*

@Entity
class DateInfo(fcstDate:String, baseTime:String,
               maxTemp:Double? = 0.0, minTemp:Double? = 0.0) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dateInfo_id", nullable = false)
    val id:Long = 0;

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "dateInfo")
    private val mutableWeatherInfo: MutableList<Weather> = mutableListOf()
    val weatherInfo: List<Weather> get() = mutableWeatherInfo.toList();

    var fcstDate:String = fcstDate
        protected set
    var baseTime:String = baseTime
        protected set

    @Column(columnDefinition = "DOUBLE")
    var maxTemp:Double? = maxTemp
        protected set
    @Column(columnDefinition = "DOUBLE")
    var minTemp:Double? = minTemp
        protected set




    //=== 비지니스 코드 ===//
    fun updateMaxTemp(maxTemp: Double?) {
        this.maxTemp = maxTemp;
    }
    fun updateMinTemp(minTemp: Double?) {
        this.minTemp = minTemp;
    }


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
        return "DateInfo(id=$id, baseDate='$fcstDate', baseTime='$baseTime', maxTemp=$maxTemp, minTemp=$minTemp)"
    }


}