package kotlinproj.weather.repository

import kotlinproj.weather.domain.Weather
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface WeatherRepository: JpaRepository<Weather, Long> {

    @Query("select w from Weather w where w.forecastTime >= :fcstTime and w.dateInfo.fcstDate = :fcstDate")
    fun getWeatherAfterDateTime(@Param("fcstDate") fcstDate: String,
                                @Param("fcstTime") fcstTime: String): List<Weather>
}