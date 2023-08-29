package kotlinproj.weather.repository

import kotlinproj.weather.domain.Weather
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WeatherRepository: JpaRepository<Weather, Long> {
}