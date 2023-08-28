package kotlinproj.weather.domain

import jakarta.persistence.*

@Entity
class DateInfo(baseDate:String, baseTime:String) {
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
}