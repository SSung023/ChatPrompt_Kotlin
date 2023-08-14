package kotlinproj.Util.exception.constants

import org.springframework.http.HttpStatus

enum class SuccessCode (val status: HttpStatus, val key: String, val message: String) {
    SUCCESS(HttpStatus.OK, "OK", "정상적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "CREATED", "정상적으로 생성되었습니다.")
}