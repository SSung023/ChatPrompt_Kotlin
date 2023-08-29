package kotlinproj.weather.repository

import kotlinproj.weather.domain.DateInfo
import org.springframework.data.jpa.repository.JpaRepository


interface DateInfoRepository : JpaRepository<DateInfo, Long>{
}