package kotlinproj.Util.exception

import kotlinproj.Util.response.dto.CommonResponse
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class BusinessExceptionHandler {
    val log = KotlinLogging.logger {}

    @ExceptionHandler(BusinessException::class)
    fun globalBusinessExceptionHandler(e: BusinessException): ResponseEntity<CommonResponse> {
        log.error("[Error]" + e.message);
        log.error(e.stackTraceToString());

        return ResponseEntity.badRequest()
            .body(CommonResponse(e.status, e.message));
    }
}