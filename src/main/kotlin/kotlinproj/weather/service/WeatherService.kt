package kotlinproj.weather.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriBuilder
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


/**
 * @author HeeYeon
 * @description
 * 기상청 단기예보 API 사용해서 기상청으로부터 날씨 정보를 받아옴
 */
@Service
@Transactional(readOnly = true)
class WeatherService(private val webBuilder: WebClient.Builder){
    @Value("\${kma.callback-url}")
    lateinit var BASE_URL:String;
    @Value("\${kma.service-key}")
    lateinit var SERVICE_KEY:String;


    /**
     * 기상청
     * url 변동 사항: base_date, base_time, nx, ny
     */
    fun getWeatherInfo(): String {
        val factory = DefaultUriBuilderFactory(BASE_URL)
            .apply {
                this.encodingMode = DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY
            };

        val webClient = webBuilder
            .uriBuilderFactory(factory)
            .baseUrl(BASE_URL)
            .build();

        val response = webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .queryParam("serviceKey", SERVICE_KEY)
                    .queryParam("dataType", "JSON")
                    .queryParam("base_date", getBaseDate())
                    .queryParam("base_time", getBaseTime(LocalTime.now()))
                    .queryParam("nx", 120)
                    .queryParam("ny", 60)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block();

        return response.toString();
    }




    /**
     * request url에 들어갈 base_date를 구함
     * -> 현재의 날짜를 yyyyMMdd 형식으로 바꾸는 코드
     */
    fun getBaseDate(): String {
        val curTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return curTime.format(formatter);
    }

    /**
     * request url에 들어갈 base_time을 구함
     * -> 현재 시간에서 30분을 뺀 후, 범위에 맞는 base_time을 구한 후 반환
     */
    fun getBaseTime(curTime:LocalTime): String {
        val hours = curTime.hour;
        val minutes = curTime.minute;
        var convertedHour = "";

        if (minutes < 30) {
            convertedHour = (hours - 1).toString() + "00";
        }else{
            convertedHour = hours.toString() + "00";
        }

        return when(convertedHour){
            "200", "300", "400" -> "0200";
            "500", "600", "700" -> "0500";
            "800", "900", "1000" -> "0800";
            "1100", "1200", "1300" -> "1100";
            "1400", "1500", "1600" -> "1400";
            "1700", "1800", "1900" -> "1700";
            "2000", "2100", "2200"-> "2000";
            "2300", "2400", "000", "0000", "100"-> "2300";

            else -> {"0000"}
        }
    }



}