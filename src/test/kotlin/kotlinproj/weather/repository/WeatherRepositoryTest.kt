package kotlinproj.weather.repository

import kotlinproj.weather.domain.PublishDate
import kotlinproj.weather.domain.Weather
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataJpaTest(showSql = true)
@ExtendWith(SpringExtension::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WeatherRepositoryTest {
    @Autowired
    private lateinit var weatherRepository: WeatherRepository
    @Autowired
    private lateinit var dateInfoRepository: DateInfoRepository

    @BeforeEach
    fun setup(){
        //given
        val publishDate = PublishDate(
            baseDate = "20230828", baseTime = "0200")
        val weather = Weather(publishDate,
            forecastTime = "0300", temperature = 23, humidity = 60,
            rainPossible = 30, rainfall = 1, skyState = "1")

        //when
        dateInfoRepository.save(publishDate)
        weatherRepository.save(weather)
    }

    @Test
    @DisplayName("weather entity 저장 테스트")
    fun save_weather_entity_test() {
        //given
        val publishDate = PublishDate(
            baseDate = "20230828", baseTime = "0200")
        val weather = Weather(publishDate,
            forecastTime = "0300", temperature = 23, humidity = 60,
            rainPossible = 30, rainfall = 1, skyState = "1")

        //when
        dateInfoRepository.save(publishDate)
        val save = weatherRepository.save(weather)

        //then
        assertThat(save.id).isEqualTo(1L)
    }
}