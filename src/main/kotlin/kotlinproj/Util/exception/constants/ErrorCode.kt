package kotlinproj.Util.exception.constants

import org.springframework.http.HttpStatus

enum class ErrorCode (val status: HttpStatus, val message: String){
    DATA_ERROR_NOT_FOUND
        (HttpStatus.NOT_FOUND, "해당 데이터를 찾을 수 없습니다."),

    // SLACK ERR
    SLACK_MESSAGE_DONT_SEND
        (HttpStatus.BAD_REQUEST, "Slack에 메시지가 전송되지 않았습니다."),
}