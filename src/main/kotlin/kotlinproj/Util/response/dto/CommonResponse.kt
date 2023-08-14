package kotlinproj.Util.response.dto

import org.springframework.http.HttpStatus

/**
 * @author HeeYeon
 * @description 모든 Response에 대한 기본적인 구조
 */
open class CommonResponse(val status: HttpStatus?, val message: String?);
