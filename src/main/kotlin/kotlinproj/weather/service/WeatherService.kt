package kotlinproj.weather.service

import kotlinproj.Util.exception.BusinessException
import kotlinproj.Util.exception.constants.ErrorCode
import kotlinproj.weather.constant.SkyCode
import kotlinproj.weather.constant.WeatherCode
import kotlinproj.weather.dto.WeatherInfoDto
import kotlinproj.weather.dto.kma.Item
import kotlinproj.weather.dto.kma.Response
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
     * 기상청 Open API를 통해 받은 정보를 바탕으로 특정 시간대의 날씨 정보를 받아옴
     * @param curTime 기상 정보를 받고 싶은 시간
     */
    fun getWeatherInfo(curTime: LocalTime): WeatherInfoDto {
        val itemList = requestWeatherAPI(curTime).response.body.items.item;
        return convertResToWeatherDto(itemList);
    }

    /**
     * 기상청 Open API를 통해 단기예보 데이터를 가지고 옴
     * url 변동 사항: base_date, base_time, nx, ny
     */
    fun requestWeatherAPI(curTime: LocalTime) : Response{
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
                    .queryParam("numOfRows", 12)
                    .queryParam("dataType", "JSON")
                    .queryParam("base_date", getBaseDate())
                    .queryParam("base_time", getBaseTime(curTime))
                    .queryParam("nx", 120)
                    .queryParam("ny", 60)
                    .build()
            }
            .retrieve()
            .bodyToMono(Response::class.java)
            .block();

        return requireNotNull(response) {
            throw BusinessException(ErrorCode.API_SEND_FAILURE)
        };
    }

    /**
     * @param resList numOfRows를 12로 설정하면 1시간동안의 날씨 정보를 배열로 받을 수 있음
     * 정보들을 모아서 WeatherInfoDto로 만들어서 반환
     */
    fun convertResToWeatherDto(resList: List<Item>): WeatherInfoDto {
        val associated = resList.associateBy {
            it.category
        }
        val skyCodeNum = associated[WeatherCode.SKY.name]?.fcstValue?.toInt()
            ?: 0;

        return WeatherInfoDto(
            temp = associated[WeatherCode.TMP.name]?.fcstValue,
            humidity = associated[WeatherCode.REH.name]?.fcstValue,
            rainPossibility = associated[WeatherCode.POP.name]?.fcstValue,
            rainAmount = associated[WeatherCode.PCP.name]?.fcstValue,
            sky = getSkyState(skyCodeNum)
        );
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

    /**
     * Int 형태로 오는 하늘상태 코드를 문자열로 변환
     * @param skyCode API를 통해 받은 하늘상태 코드
     */
    fun getSkyState(skyCode: Int): String {
        return SkyCode.values().firstOrNull{
            it.dayNumber == skyCode
        }?.description
            ?: throw BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND);
    }




}