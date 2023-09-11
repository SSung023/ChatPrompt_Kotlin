package kotlinproj.weather.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@SpringBootTest
class WeatherUtilTest {
    @Autowired lateinit var weatherUtil: WeatherUtil


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
        val formattedDate = weatherUtil.getBaseDate(curDate);

        //then
        assertThat(formattedDate).isEqualTo("20230901")
    }

    @Test
    @DisplayName("시간에 맞춰서 base_time request param을 설정할 수 있다.")
    fun setParam_By_CurTime() {
        //given
        val curTime: LocalTime = LocalTime.of(10, 30, 9);

        //when
        val formattedTime = weatherUtil.getBaseTime(curTime);

        //then
        assertThat(formattedTime).isEqualTo("0800");
    }
}