package kotlinproj.Util.config

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * @author HeeYeon
 * @description
 * Hibernate5Module이 지연로딩 되는 객체의 프로퍼티 값이 비어져 있어도, 직렬화를 가능하게 해준다.
 */
@Configuration
class JacksonConfig {

    // 강제 지연 로딩 설정을 하기 위해서는 이와 같이 추가해주면 된다.
    @Bean
    fun hibernate5JakartaModule(): Hibernate5JakartaModule {
        val hibernate5JakartaModule = Hibernate5JakartaModule()
        hibernate5JakartaModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, true)
        return hibernate5JakartaModule
    }
}