package kotlinproj.weather.service

import kotlinproj.weather.domain.DateInfo
import kotlinproj.weather.dto.kma.Item
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class DateInfoServiceTest {
    @Autowired lateinit var dateInfoService: DateInfoService


    @Test
    @DisplayName("dateInfo 객체를 저장할 수 있다.")
    fun canSaveDateInfo() {
        //given
        val dateInfo = DateInfo("20230829", "0200", 28.9, 23.2);

        //when
        val saveOne = dateInfoService.saveOne(dateInfo)

        //then
        assertThat(saveOne.id).isNotEqualTo(0L)
        assertThat(saveOne.fcstDate).isEqualTo("20230829")
        assertThat(saveOne.baseTime).isEqualTo("0200")
        assertThat(saveOne.maxTemp).isEqualTo(28.9)
        assertThat(saveOne.minTemp).isEqualTo(23.2)
    }

    @Test
    @DisplayName("특정 조건에 기존의 정보를 통해 DateInfo 엔티티를 만든다.")
    fun converToEntity_when_fcstTime_changed() {
        //given

        //when
        val dateInfo = DateInfo("20230829", "0200");
        val saved:DateInfo = dateInfoService.getDateInfoEntity(dateInfo, 27.3, 21.6);

        //then
        assertThat(saved.id).isNotEqualTo(0L);
        assertThat(saved.fcstDate).isEqualTo("20230829")
        assertThat(saved.baseTime).isEqualTo("0200")
        assertThat(saved.maxTemp).isEqualTo(27.3)
        assertThat(saved.minTemp).isEqualTo(21.6)
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
}