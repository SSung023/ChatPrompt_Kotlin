package kotlinproj.weather.service

import kotlinproj.Util.exception.BusinessException
import kotlinproj.Util.exception.constants.ErrorCode
import kotlinproj.weather.constant.Constants
import kotlinproj.weather.constant.WeatherCode
import kotlinproj.weather.domain.DateInfo
import kotlinproj.weather.domain.Weather
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

@Service
@Transactional(readOnly = true)
class ApiService (
    private val webBuilder: WebClient.Builder,
    private val weatherService: WeatherService,
    private val dateInfoService: DateInfoService,
) {
    @Value("\${kma.callback-url}")
    lateinit var BASE_URL:String;
    @Value("\${kma.service-key}")
    lateinit var SERVICE_KEY:String;



    /**
     * 기상청 Open API를 통해 받은 단기예보 데이터를 받음
     * url 변동 사항: base_date, base_time, nx, ny
     */
    fun requestWeatherAPI(curTime: LocalTime, numOfRaws: Number) : Response {
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
                    .queryParam("numOfRows", numOfRaws)
                    .queryParam("dataType", Constants.DATA_TYPE)
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
     * 기상청 Open API를 통해 받은 정보를 바탕으로 fcstTime 기준으로
     * Weather 엔티티 객체 생성 후, List에 저장하고 bulk 방식으로 DB에 저장
     *
     * 문제: fcstDate가 바뀌면 dateInfo 객체도 새로 save 해주어야 함
     */
    @Transactional
    fun saveWeatherList(weatherInfo: List<Item>) : List<Weather>{
        val weatherList:MutableList<Weather> = mutableListOf()

        var fcstTime = weatherInfo[0].fcstTime
        var fcstDate = weatherInfo[0].fcstDate
        val itemList = mutableListOf(weatherInfo[0])
        var dateInfo = dateInfoService.saveOne(
            DateInfo(weatherInfo[0].fcstDate, weatherInfo[0].baseTime))

        for (item in weatherInfo) {
            if (fcstTime != item.fcstTime) {
                weatherList.add(addRelationToWeather(itemList, dateInfo));

                fcstTime = item.fcstTime // 초기화
                itemList.clear()
            }
            if (fcstDate != item.fcstDate) {
                // 날짜가 바뀔 때 dateInfo 정보 변환
                fcstDate = item.fcstDate
                dateInfo = dateInfoService.saveOne(
                    DateInfo(item.fcstDate, item.baseTime)
                )
            }
            if (item.category == WeatherCode.TMX.name) {
                dateInfo.updateMaxTemp(item.fcstValue.toDouble())
            }
            else if (item.category == WeatherCode.TMN.name) {
                dateInfo.updateMinTemp(item.fcstValue.toDouble())
            }
            itemList.add(item)
        }
        weatherList.add(addRelationToWeather(itemList, dateInfo))
        weatherService.saveWeatherInfo(weatherList)
        return weatherList;
    }
    private fun addRelationToWeather(itemList: List<Item>, dateInfo: DateInfo): Weather {
        return weatherService.convertToWeatherEntity(itemList, dateInfo)
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