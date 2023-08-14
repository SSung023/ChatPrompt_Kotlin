package kotlinproj.common.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.LocalDateTime

/**
 * @author HeeYeon
 * @description 생성일자, 수정일자를 자동으로 저장
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    protected val createdDate: LocalDateTime? = null

    @LastModifiedDate
    protected val modifiedDate: LocalDateTime? = null
}