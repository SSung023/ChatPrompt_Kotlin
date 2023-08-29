package kotlinproj.weather.repository

import kotlinproj.weather.domain.DateInfo
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DateInfoRepositoryTest {
    @Autowired
    private lateinit var dateInfoRepository: DateInfoRepository

    @Test
    @DisplayName("dateInfo 엔티티 저장 테스트")
    fun save_dateInfoEntity() {
        //given
        val dateInfo = DateInfo(
            baseDate = "20230828", baseTime = "0200");

        //when
        val savedDate = dateInfoRepository.save(dateInfo)

        //then
        Assertions.assertThat(savedDate.id).isEqualTo(1L);
        Assertions.assertThat(savedDate.baseDate).isEqualTo(dateInfo.baseDate)
        Assertions.assertThat(savedDate.baseTime).isEqualTo(dateInfo.baseTime)
    }
}