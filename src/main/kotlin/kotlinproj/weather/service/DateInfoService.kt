package kotlinproj.weather.service

import kotlinproj.Util.exception.BusinessException
import kotlinproj.Util.exception.constants.ErrorCode
import kotlinproj.weather.domain.DateInfo
import kotlinproj.weather.repository.DateInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse

@Service
@Transactional(readOnly = true)
class DateInfoService (private val dateInfoRepository: DateInfoRepository){

    @Transactional
    fun saveOne(dateInfo: DateInfo): DateInfo{
        return dateInfoRepository.save(dateInfo);
    }

    fun findOne(id: Long) : DateInfo {
        return dateInfoRepository.findById(id)
            .getOrElse {
                throw BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND)
            };
    }
    fun getDateInfoEntity(dateInfo: DateInfo, maxTemp: Double, minTemp: Double): DateInfo {
        val dateInfo = DateInfo(dateInfo.fcstDate, dateInfo.baseTime, maxTemp, minTemp)
        return saveOne(dateInfo)
    }
}