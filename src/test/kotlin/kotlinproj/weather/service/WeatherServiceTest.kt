package kotlinproj.weather.service

import kotlinproj.weather.constant.SkyCode
import kotlinproj.weather.constant.WeatherCode
import kotlinproj.weather.domain.DateInfo
import kotlinproj.weather.domain.Weather
import kotlinproj.weather.dto.WeatherInfoDto
import kotlinproj.weather.dto.kma.Item
import kotlinproj.weather.repository.DateInfoRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@Transactional
class WeatherServiceTest() {
    @Autowired lateinit var dateInfoRepository: DateInfoRepository
    @Autowired lateinit var weatherService: WeatherService;


    @Test
    @DisplayName("SKY 코드표를 통해 하늘이 어떤 상태인지 확인할 수 있다.")
    fun canCheck_SkyCode() {
        //given
        val skyCode = 1;
        
        //when
        val skyState:String = weatherService.getSkyState(skyCode);

        //then
        assertThat(skyState).isNotNull();
        assertThat(skyState).isEqualTo(SkyCode.SUNNY.description);
    }

    @Test
    @DisplayName("기상청 API를 통해 가지고 온 데이터 12개를 바로 weatherInfoDTO로 변환할 수 있다.")
    fun convertTo_WeatherInfoDTO() {
        //given
        val weatherResponse:List<Item> = getItems();

        //when
        val weatherDto:WeatherInfoDto = weatherService.convertToWeatherDto(weatherResponse);

        //then
        assertThat(weatherDto.temp).isEqualTo(25.8);
        assertThat(weatherDto.humidity).isEqualTo(78);
        assertThat(weatherDto.rainPossibility).isEqualTo(0);
        assertThat(weatherDto.rainAmount).isEqualTo("강수없음");
        assertThat(weatherDto.sky).isEqualTo(SkyCode.SUNNY.description);
    }

    @Test
    @DisplayName("Weather 엔티티를 weatherInfoDto로 변환할 수 있다.")
    fun canConvert_Weather_To_WeatherInfoDto() {
        //given
        val weather = saveWeather("0900")

        //when
        val weatherDto = weatherService.convertToWeatherDto(weather)

        //then
        assertThat(weatherDto.temp).isEqualTo(23.4)
    }


    @Test
    @DisplayName("List<Item>을 전달했을 때, Weather 엔티티로 변환이 가능하다.")
    fun canConvertTo_WeatherEntity() {
        //given
        val dateInfo = getSavedDateInfo();

        val weatherResponse:List<Item> = getItems();
        val associated = weatherResponse.associateBy { it.category }

        //when
        val weather:Weather = weatherService.convertToWeatherEntity(weatherResponse, dateInfo);
        val skyState = weatherService.getSkyState(associated[WeatherCode.SKY.name]?.fcstValue?.toInt()!!)

        //then
        assertThat(weather.dateInfo).isEqualTo(dateInfo)
        assertThat(weather.forecastTime).isEqualTo(weatherResponse[0].fcstTime)
        assertThat(weather.humidity).isEqualTo(associated[WeatherCode.REH.name]?.fcstValue?.toInt())
        assertThat(weather.rainAmt).isEqualTo(associated[WeatherCode.PCP.name]?.fcstValue)
        assertThat(weather.rainPossibility).isEqualTo(associated[WeatherCode.POP.name]?.fcstValue?.toInt())
        assertThat(weather.temperature).isEqualTo(associated[WeatherCode.TMP.name]?.fcstValue?.toDouble())
        assertThat(weather.skyState).isEqualTo(skyState)
    }

    @Test
    @DisplayName("List<Item>에 TMX 혹은 TMN 값이 있다면 dateInfo에 할당해준다.")
    fun assignTempInfo_When_Exist() {
        //given
        val itemList = getItems()
        val dateInfo = DateInfo("20230829", "1400")
        val associated = itemList.associateBy {
            it.category
        }

        //when
        val weatherEntity = weatherService.convertToWeatherEntity(itemList, dateInfo)

        //then
        assertThat(weatherEntity.dateInfo.maxTemp).isEqualTo(associated["TMX"]?.fcstValue?.toDouble())
        assertThat(weatherEntity.dateInfo.minTemp).isEqualTo(associated["TMN"]?.fcstValue?.toDouble())
    }


    @Test
    @DisplayName("DB에 존재하는 날씨 데이터들 중, 요청 시간대 이후의 날씨 데이터들을 불러올 수 있다.")
    fun load_weatherInfo_after_requestTime() {
        //given
        saveWeather("0900");
        saveWeather("1000");
        saveWeather("1100");

        //when
        val weatherList = weatherService.loadWeather(
            LocalDate.of(2023,9,1),
            LocalTime.of(8,5)
        )

        //then
        assertThat(weatherList.size).isEqualTo(3)
    }















    private fun getItems(): List<Item> {
        val weatherList:MutableList<Item> = mutableListOf();
        weatherList.also {
                it.add(getItem("TMP", "25.8"))
                it.add(getItem("TMX", "29"))
                it.add(getItem("TMN", "24"))
                it.add(getItem("POP", "0"))
                it.add(getItem("PCP", "강수없음"))
                it.add(getItem("PTY", "0"))
                it.add(getItem("REH", "78"))
                it.add(getItem("SKY", "1"))
                it.add(getItem("SNO", "적설없음"))
            }
        return weatherList;
    }
    private fun getItem(category: String, fcstValue: String): Item {
        return Item(
            baseDate = "20230825", baseTime = "0800", category = category,
            fcstDate = "20230825", fcstTime = "0900", fcstValue = fcstValue,
            nx = 60, ny = 120
        );
    }

    private fun getSavedDateInfo(): DateInfo {
        return dateInfoRepository.save(
            DateInfo("20230829", "0200", 28.0, 21.0)
        );
    }

    private fun saveWeather(forecastTime: String): Weather{
        val dateInfo = DateInfo(
            fcstDate = "20230901",
            baseTime = "20230901"
        )
        val savedDate = dateInfoRepository.save(dateInfo)

        return weatherService.saveOne(
            Weather(
                savedDate,
                forecastTime = forecastTime,
                temperature = 23.4,
                humidity = 56,
                rainPossible = 30,
                rainAmt = "0",
                skyState = "맑음"
            )
        )
    }

}