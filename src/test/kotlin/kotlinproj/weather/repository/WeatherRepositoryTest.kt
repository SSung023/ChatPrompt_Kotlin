package kotlinproj.weather.repository

import kotlinproj.weather.constant.SkyCode
import kotlinproj.weather.domain.DateInfo
import kotlinproj.weather.domain.Weather
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@DataJpaTest(showSql = true)
@ExtendWith(SpringExtension::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class WeatherRepositoryTest {
    @Autowired
    private lateinit var weatherRepository: WeatherRepository
    @Autowired
    private lateinit var dateInfoRepository: DateInfoRepository


    @Test
    @DisplayName("weather entity 저장 테스트")
    fun save_weather_entity_test() {
        //given
        val publishDate = DateInfo(
            fcstDate = "20230828", baseTime = "0200")
        val weather = Weather(publishDate,
            forecastTime = "0300", temperature = 23.0, humidity = 60,
            rainPossible = 30, rainAmt = "1.2", skyState = "맑음")

        //when
        dateInfoRepository.save(publishDate)
        val save = weatherRepository.save(weather)

        //then
        assertThat(save.id).isNotEqualTo(0L)
        assertThat(save.dateInfo.fcstDate).isEqualTo("20230828")
        assertThat(save.dateInfo.baseTime).isEqualTo("0200")
        assertThat(save.forecastTime).isEqualTo("0300")
        assertThat(save.temperature).isEqualTo(23.0)
        assertThat(save.humidity).isEqualTo(60)
        assertThat(save.rainPossibility).isEqualTo(30)
        assertThat(save.rainAmt).isEqualTo("1.2")
        assertThat(save.skyState).isEqualTo(SkyCode.SUNNY.description)
    }

    @Test
    @DisplayName("date와 time을 전달하면, 같은 date이면서 현재 시간보다 이후 시간대의 데이터를 반환한다.")
    fun should_return() {
        //given
        saveWeather("20230901","0900")
        saveWeather("20230901","1000")
        saveWeather("20230901","1100")

        //when
        val weatherList:List<Weather> = weatherRepository.getWeatherAfterDateTime("20230901","1000")

        //then
        assertThat(weatherList.size).isEqualTo(2)
    }








    private fun saveWeather(fcstDate: String, fcstTime:String) : Weather{
        val publishDate = DateInfo(
            fcstDate = fcstDate, baseTime = "0200")
        val weather = Weather(publishDate,
            forecastTime = fcstTime, temperature = 23.0, humidity = 60,
            rainPossible = 30, rainAmt = "1.2", skyState = "1")

        dateInfoRepository.save(publishDate)
        return weatherRepository.save(weather)
    }
}