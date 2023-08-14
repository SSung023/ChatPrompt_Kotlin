package kotlinproj.Util.response.dto

import org.springframework.http.HttpStatus

/**
 * @author HeeYeon
 * @description 단일 데이터 반환 시 사용
 */
class SingleResponse<T>: CommonResponse {
    var data: T

    constructor(data: T) : super(HttpStatus.OK, "") {
        this.data = data
    }

    constructor(status: HttpStatus, message: String, data: T) : super(status, message) {
        this.data = data
    }
}
