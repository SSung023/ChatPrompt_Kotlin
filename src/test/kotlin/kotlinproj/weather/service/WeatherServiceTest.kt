package kotlinproj.weather.service

import kotlinproj.Util.log.Logger
import kotlinproj.weather.constant.SkyCode
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
    @DisplayName("기상청 API를 통해 가지고 온 데이터 12개를 weatherInfoDTO로 변환할 수 있다.")
    fun convertTo_WeatherInfoDTO() {
        //given
        val weatherResponse:List<Item> = getItems();

        //when
        val weatherDto:WeatherInfoDto = weatherService.convertToWeatherDto(weatherResponse);

        //then
        assertThat(weatherDto.temp).isEqualTo("25.8");
        assertThat(weatherDto.humidity).isEqualTo("78");
        assertThat(weatherDto.rainPossibility).isEqualTo("0");
        assertThat(weatherDto.rainAmount).isEqualTo("강수없음");
        assertThat(weatherDto.sky).isEqualTo(SkyCode.SUNNY.description);
    }
    
    @Test
    @DisplayName("List<Item>을 전달했을 때, Weather 엔티티로 변환이 가능하다.")
    fun canConvertTo_WeatherEntity() {
        //given
        val dateInfo = getSavedDateInfo();

        val weatherResponse:List<Item> = getItems();
        
        //when
        val weather:Weather = weatherService.convertToWeatherEntity(weatherResponse, dateInfo);
        
        //then
        Logger.log.info { weather.toString() }
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
            DateInfo("20230829", "0200", 28, 21)
        );
    }

}