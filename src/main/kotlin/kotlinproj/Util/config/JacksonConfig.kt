package kotlinproj.Util.config

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author HeeYeon
 * @description
 * Hibernate5Module이 지연로딩 되는 객체의 프로퍼티 값이 비어져 있어도, 직렬화를 가능하게 해준다.
 */
@Configuration
class JacksonConfig {
    @Bean
    fun hibernate5Module(): Hibernate5Module {
        return Hibernate5Module();
    }
}