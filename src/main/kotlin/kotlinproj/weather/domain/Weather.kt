package kotlinproj.weather.domain

import jakarta.persistence.*

@Entity
class Weather(
    dateInfo: DateInfo,
    forecastTime: String, temperature: Double, humidity: Int,
    rainPossible: Int, rainAmt: String, skyState:String
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_id")
    val id:Long = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dateInfo_id")
    var dateInfo: DateInfo = dateInfo

    var forecastTime: String = forecastTime // 예보 시간
        protected set
    @Column(columnDefinition = "DOUBLE")
    var temperature: Double = temperature // 1시간동안의 기온
        protected set
    @Column(columnDefinition = "INTEGER")
    var humidity: Int = humidity // 습도
        protected set
    @Column(columnDefinition = "INTEGER")
    var rainPossibility: Int = rainPossible // 강수확률
        protected set
    var rainAmt:String = rainAmt // 강수량
        protected set
    var skyState:String = skyState // 하늘 상태
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
        return "Weather(id=$id, forecastTime='$forecastTime', temperature=$temperature, humidity=$humidity, rainPossibility=$rainPossibility, rainfall=$rainAmt, skyState='$skyState')"
    }


}