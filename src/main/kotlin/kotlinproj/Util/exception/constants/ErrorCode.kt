package kotlinproj.Util.exception.constants

import org.springframework.http.HttpStatus

enum class ErrorCode (val status: HttpStatus, val message: String){
    DATA_ERROR_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 데이터를 찾을 수 없습니다."),
}