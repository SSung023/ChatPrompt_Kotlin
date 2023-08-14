package kotlinproj.Util.response.dto

import org.springframework.data.domain.Slice
import org.springframework.http.HttpStatus

/**
 * @author HeeYeon
 * @description paging 한 결과를 반환할 때 사용
 */
class PagingResponse<T> : CommonResponse {
    var data: Slice<T>

    constructor(data: Slice<T>) : super(HttpStatus.OK, "") {
        this.data = data
    }
    constructor(status: HttpStatus, message: String, data: Slice<T>) : super(status, message) {
        this.data = data;
    }
}