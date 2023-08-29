package kotlinproj.weather.service

import kotlinproj.Util.log.Logger
import kotlinproj.weather.constant.SkyCode
import kotlinproj.weather.domain.DateInfo
import kotlinproj.weather.domain.Weather
import kotlinproj.weather.dto.WeatherInfoDto
import kotlinproj.weather.dto.kma.Item
import kotlinproj.weather.repository.DateInfoRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.LocalTime

@SpringBootTest
@Transactional
class WeatherServiceTest() {
    @Autowired lateinit var dateInfoRepository: DateInfoRepository
    @Autowired lateinit var weatherService: WeatherService;

    @Test
    @DisplayName("callback url로 요청을 했을 때 성공 코드(00)이 와야 한다.")
    fun shouldNotError_when_Request() {
        //given

        //when
        val searchWeather = weatherService.requestWeatherAPI(LocalTime.now(), 12);

        //then
        val resCode = searchWeather.response.header.resultCode;
        assertThat(resCode).isEqualTo("00");
        assertThatNoException();
    }

    @Test
    @DisplayName("현재의 시간을 yyyymmdd 형태로 변환할 수 있다.")
    fun canConvert_To_CertainFormat() {
        //given
        val curTime = LocalDateTime.now().toString();
        val dateArr = curTime.split("-", "T");
        var expected = "";
        for (i in 0..2){
            expected += dateArr[i];
        }

        //when
        val formattedDate = weatherService.getBaseDate();

        //then
        assertThat(formattedDate).isEqualTo(expected)
    }

    @Test
    @DisplayName("시간에 맞춰서 base_time request param을 설정할 수 있다.")
    fun setParam_By_CurTime() {
        //given
        val curTime:LocalTime = LocalTime.of(10, 30, 9);

        //when
        val formattedTime = weatherService.getBaseTime(curTime);

        //then
        assertThat(formattedTime).isEqualTo("0800");
    }
    
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

    @Test
    @DisplayName("여러 시간의 데이터를 가지고 왔을 때 fcstTime이 변화하면 다른 DTO객체로 변환된다.")
    fun save_api_result() {
        //given
        val weatherInfo = weatherService.requestWeatherAPI(LocalTime.now(), 24)
            .response.body.items.item

        //when
        val dtoList = weatherService.getWeatherInfo(weatherInfo)

        //then
        assertThat(dtoList.size).isEqualTo(2)
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