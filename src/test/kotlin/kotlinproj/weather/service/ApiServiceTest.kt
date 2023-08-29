package kotlinproj.weather.service

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.LocalTime

@SpringBootTest
@Transactional
class ApiServiceTest {
    @Autowired private lateinit var apiService: ApiService

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
        val curTime = LocalDateTime.now().toString();
        val dateArr = curTime.split("-", "T");
        var expected = "";
        for (i in 0..2){
            expected += dateArr[i];
        }

        //when
        val formattedDate = apiService.getBaseDate();

        //then
        assertThat(formattedDate).isEqualTo(expected)
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
    @DisplayName("여러 시간의 데이터를 가지고 왔을 때 fcstTime이 변화하면 다른 DTO객체로 변환된다.")
    fun save_api_result() {
        //given
        

        //when

        //then
    }
}