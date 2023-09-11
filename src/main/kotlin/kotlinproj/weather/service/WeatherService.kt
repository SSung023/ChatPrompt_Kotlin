package kotlinproj.weather.service

import kotlinproj.Util.exception.BusinessException
import kotlinproj.Util.exception.constants.ErrorCode
import kotlinproj.weather.constant.SkyCode
import kotlinproj.weather.constant.WeatherCode
import kotlinproj.weather.domain.DateInfo
import kotlinproj.weather.domain.Weather
import kotlinproj.weather.dto.WeatherInfoDto
import kotlinproj.weather.dto.kma.Item
import kotlinproj.weather.repository.WeatherRepository
import kotlinproj.weather.util.WeatherUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime


/**
 * @author HeeYeon
 * @description
 * 기상청 단기예보 API 사용해서 기상청으로부터 날씨 정보를 받아옴
 */
@Service
@Transactional(readOnly = true)
class WeatherService(
    private val weatherRepository: WeatherRepository,
    private val weatherUtil: WeatherUtil
){

    /**
     * Open API를 통해 불러온 기상청 정보를 DB에 저장
     */
    @Transactional
    fun saveAll(weatherList: List<Weather>): List<Weather>{
        return weatherRepository.saveAll(weatherList)
    }

    @Transactional
    fun saveOne(weather: Weather) : Weather {
        return weatherRepository.save(weather)
    }

    /**
     * 요청 시간대에 맞춰서 해당 날짜에 있는 날씨 데이터들을 전달
     */
    fun loadWeather(date: LocalDate, time: LocalTime): List<WeatherInfoDto> {
        val dateStr = weatherUtil.getBaseDate(date)
        val timeStr = weatherUtil.getBaseTime(time)

        val weatherList = weatherRepository.getWeatherAfterDateTime(dateStr, timeStr)
        return weatherList.map {
            convertToWeatherDto(it)
        }
    }


    /**
     * @param weather DTO로 변환하고자 하는 entity
     * 단일 weather 엔티티를 DTO로 변환
     */
    fun convertToWeatherDto(weather: Weather): WeatherInfoDto {
        return WeatherInfoDto(
            temp = weather.temperature,
            humidity = weather.humidity,
            rainPossibility = weather.rainPossibility,
            rainAmount = weather.rainAmt,
            sky = weather.skyState
        )
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
            temp = associated[WeatherCode.TMP.name]?.fcstValue?.toDouble(),
            humidity = associated[WeatherCode.REH.name]?.fcstValue?.toInt(),
            rainPossibility = associated[WeatherCode.POP.name]?.fcstValue?.toInt(),
            rainAmount = associated[WeatherCode.PCP.name]?.fcstValue,
            sky = getSkyState(skyCodeNum)
        );
    }


    /**
     * 1시간 동안의 날씨를 전달받아서 Weather 엔티티로 변환
     * @param itemList 1시간동안의 날씨 정보
     * @param dateInfo Weather 엔티티의 연관관계 설정을 위한 param
     */
    @Transactional
    fun convertToWeatherEntity(itemList: List<Item>, dateInfo: DateInfo): Weather {
        val associated = itemList.associateBy {
            it.category
        }
        val skyCode = associated[WeatherCode.SKY.name]?.fcstValue?.toInt()
            ?: 0;

        dateInfo.updateMaxTemp(associated[WeatherCode.TMX.name]?.fcstValue?.toDouble())
        dateInfo.updateMinTemp(associated[WeatherCode.TMN.name]?.fcstValue?.toDouble())

        return Weather(
            dateInfo = dateInfo,
            forecastTime = itemList[0].fcstTime,
            temperature = associated[WeatherCode.TMP.name]?.fcstValue?.toDouble() ?: 0.0,
            humidity =  associated[WeatherCode.REH.name]?.fcstValue?.toInt() ?: 0,
            rainPossible = associated[WeatherCode.POP.name]?.fcstValue?.toInt() ?: 0,
            rainAmt =  associated[WeatherCode.PCP.name]?.fcstValue ?: "0",
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