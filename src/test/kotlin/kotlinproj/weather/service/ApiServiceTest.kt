package kotlinproj.weather.service

import kotlinproj.weather.domain.DateInfo
import kotlinproj.weather.domain.Weather
import kotlinproj.weather.dto.WeatherInfoDto
import kotlinproj.weather.dto.kma.Item
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@SpringBootTest
@Transactional
class ApiServiceTest {
    @Autowired private lateinit var apiService: ApiService
    @Autowired lateinit var weatherService: WeatherService

    @Test
    @DisplayName("callback url로 요청을 했을 때 성공 코드(00)이 와야 한다.")
    fun shouldNotError_when_Request() {
        //given

        //when
        val searchWeather = apiService.requestWeatherAPI(LocalTime.now(), 12);

        //then
        val resCode = searchWeather.response.header.resultCode;
        assertThat(resCode).isEqualTo("00");
        Assertions.assertThatNoException();
    }

    @Test
    @DisplayName("현재의 시간을 yyyymmdd 형태로 변환할 수 있다.")
    fun canConvert_To_CertainFormat() {
        //given
        val curDate = LocalDate.of(2023,9,1)
        val curTime = LocalDateTime.now().toString();
        val dateArr = curTime.split("-", "T");
        var expected = "";
        for (i in 0..2){
            expected += dateArr[i];
        }

        //when
        val formattedDate = apiService.getBaseDate(curDate);

        //then
        assertThat(formattedDate).isEqualTo("20230901")
    }

    @Test
    @DisplayName("시간에 맞춰서 base_time request param을 설정할 수 있다.")
    fun setParam_By_CurTime() {
        //given
        val curTime:LocalTime = LocalTime.of(10, 30, 9);

        //when
        val formattedTime = apiService.getBaseTime(curTime);

        //then
        assertThat(formattedTime).isEqualTo("0800");
    }

    @Test
    @DisplayName("fcstTime이 달라지면 다른 Weather 엔티티로 취급하여 List에 add한다.")
    fun should_differenntEntity_when_fcstChanged() {
        //given
        val itemList:List<Item> = getItems("0900",false) + getItems("1000", true);

        //when
        val weatherInfo = apiService.saveWeatherList(itemList)

        //then
        assertThat(weatherInfo.size).isEqualTo(2);
        assertThat(weatherInfo[0].forecastTime).isEqualTo("0900")
        assertThat(weatherInfo[1].forecastTime).isEqualTo("1000")
    }
    
    @Test
    @DisplayName("데이터 요청 시간이 23시 미만이면 요청 시간이 현재 시간대 + 1이어야 한다.")
    fun should_return_correct_timezone() {
        //given
        val curTime = LocalTime.of(0, 40)
        
        //when
        val fcstTime:String = apiService.getForecastTime(curTime)
        
        //then
        assertThat(fcstTime).isEqualTo("0100")
    }
    @Test
    @DisplayName("데이터 요청 시간에 23시 이상이면 요청 시간이 0000이어야 한다.")
    fun should_return_0000() {
        //given
        val curTime = LocalTime.of(23, 1)

        //when
        val fcstTime = apiService.getForecastTime(curTime)

        //then
        assertThat(fcstTime).isEqualTo("0000")
    }
    
    @Test
    @DisplayName("DB에 존재하는 날씨 데이터들 중, 요청 시간대 이후의 날씨 데이터들을 불러올 수 있다.")
    fun load_weatherInfo_after_requestTime() {
        //given
        saveWeather("0900");
        saveWeather("1000");
        saveWeather("1100");
        val time = LocalTime.of(8,5)
        
        //when
        val weatherList:List<WeatherInfoDto> = apiService.loadWeather(
            LocalDate.of(2023,9,1), time
        );
        
        //then
        assertThat(weatherList.size).isEqualTo(3)
    }
    
    
    
    





    // baseTime, fcstTime, fcstDate 필요
    private fun getItems(fcstTime:String, flag:Boolean): List<Item> {
        val weatherList:MutableList<Item> = mutableListOf();
        weatherList.also {
            it.add(getItem(fcstTime, "TMP", "25.8"))
            it.add(getItem(fcstTime,"POP", "0"))
            it.add(getItem(fcstTime,"PCP", "강수없음"))
            it.add(getItem(fcstTime,"PTY", "0"))
            it.add(getItem(fcstTime,"REH", "78"))
            it.add(getItem(fcstTime,"SKY", "1"))
            it.add(getItem(fcstTime,"SNO", "적설없음"))
        }
        if (flag){
            weatherList.also {
                it.add(getItem(fcstTime,"TMX", "29"))
                it.add(getItem(fcstTime,"TMN", "24"))
            }
        }
        return weatherList;
    }
    private fun getItem(fcstTime:String, category: String, fcstValue: String): Item {
        return Item(
            baseDate = "20230825", baseTime = "0800", category = category,
            fcstDate = "20230825", fcstTime = fcstTime, fcstValue = fcstValue,
            nx = 60, ny = 120
        );
    }

    private fun getItems(fcstDate: String, fcstTime:String, flag:Boolean): List<Item> {
        val weatherList:MutableList<Item> = mutableListOf();
        weatherList.also {
            it.add(getItem(fcstDate, fcstTime, "TMP", "25.8"))
            it.add(getItem(fcstDate, fcstTime,"POP", "0"))
            it.add(getItem(fcstDate, fcstTime,"PCP", "강수없음"))
            it.add(getItem(fcstDate, fcstTime,"PTY", "0"))
            it.add(getItem(fcstDate, fcstTime,"REH", "78"))
            it.add(getItem(fcstDate, fcstTime,"SKY", "1"))
            it.add(getItem(fcstDate, fcstTime,"SNO", "적설없음"))
        }
        if (flag){
            weatherList.also {
                it.add(getItem(fcstDate, fcstTime,"TMX", "29"))
                it.add(getItem(fcstDate, fcstTime,"TMN", "24"))
            }
        }
        return weatherList;
    }
    private fun getItem(fcstDate:String, fcstTime:String, category: String, fcstValue: String): Item {
        return Item(
            baseDate = "20230825", baseTime = "0800", category = category,
            fcstDate = fcstDate, fcstTime = fcstTime, fcstValue = fcstValue,
            nx = 60, ny = 120
        );
    }

    private fun saveWeather(forecastTime: String): Weather{
        val dateInfo = DateInfo(
            fcstDate = "20230901",
            baseTime = "20230901"
        )
        return weatherService.saveOne(
            Weather(
                dateInfo,
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