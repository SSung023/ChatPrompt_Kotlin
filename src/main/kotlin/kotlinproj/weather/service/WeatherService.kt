package kotlinproj.weather.service

import kotlinproj.Util.exception.BusinessException
import kotlinproj.Util.exception.constants.ErrorCode
import kotlinproj.weather.constant.Constants.Companion.DATA_TYPE
import kotlinproj.weather.constant.SkyCode
import kotlinproj.weather.constant.WeatherCode
import kotlinproj.weather.domain.DateInfo
import kotlinproj.weather.domain.Weather
import kotlinproj.weather.dto.WeatherInfoDto
import kotlinproj.weather.dto.kma.Item
import kotlinproj.weather.dto.kma.Response
import kotlinproj.weather.repository.WeatherRepository
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
class WeatherService(
    private val weatherRepository: WeatherRepository
){


    /**
     * 기상청 Open API를 통해 받은 정보를 바탕으로 fcstTime 기준으로 WeatherInfoDto 생성
     */
    fun getWeatherInfo(weatherInfo: List<Item>) {
        val dateList:MutableList<DateInfo> = mutableListOf()
        val weatherList:MutableList<Weather> = mutableListOf()

        var fcstTime = weatherInfo[0].fcstTime;
        for (item in weatherInfo) {
            val itemList:MutableList<Item> = mutableListOf()
            var dateInfo = DateInfo(item.baseDate, item.baseTime);

            // fcstTime이 변하지 않았을 때: Item을 List에 계속 담아둠
            if (fcstTime != item.fcstTime) {
                itemList.add(item)
            }
            // fcstTime이 변했을 때: List<Item>을 Weather, DateInfo로 바꾸고
            // List에 담아
            else {
                fcstTime = item.fcstTime
            }
        }
    }

    /**
     * Open API를 통해 불러온 기상청 정보를 DB에 저장
     */
    @Transactional
    fun saveWeatherInfo(weatherList: List<WeatherInfoDto>){



    }





    /**
     * @param resList numOfRows를 12로 설정하면 1시간동안의 날씨 정보를 배열로 받을 수 있음
     * 정보들을 모아서 WeatherInfoDto로 만들어서 반환
     */
    fun convertToWeatherDto(resList: List<Item>): WeatherInfoDto {
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
     * 1시간 동안의 날씨를 전달받아서 Weather 엔티티로 변환
     * @param itemList 1시간동안의 날씨 정보
     * @param dateInfo Weather 엔티티의 연관관계 설정을 위한 param
     */
    fun convertToWeatherEntity(itemList: List<Item>, dateInfo: DateInfo): Weather {
        val associated = itemList.associateBy {
            it.category
        }
        val skyCode = associated[WeatherCode.SKY.name]?.fcstValue?.toInt()
            ?: 0;

        return Weather(
            dateInfo = dateInfo,
            forecastTime = itemList[0].fcstTime,
            temperature = associated[WeatherCode.TMP.name]?.fcstValue ?: "0",
            humidity =  associated[WeatherCode.REH.name]?.fcstValue?.toInt() ?: 0,
            rainPossible = associated[WeatherCode.POP.name]?.fcstValue?.toInt() ?: 0,
            rainfall =  associated[WeatherCode.PCP.name]?.fcstValue?: "0",
            skyState = getSkyState(skyCode)
        )
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