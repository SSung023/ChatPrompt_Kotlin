package kotlinproj.Util.response.dto

import org.springframework.http.HttpStatus

/**
 * @author HeeYeon
 * @description List를 포함한 응답 전달 시 사용
 */
class ListResponse<T>: CommonResponse {
    var dataList: List<T>

    constructor(dataList: List<T>) : super(HttpStatus.OK, "") {
        this.dataList = dataList
    }

    constructor(status: HttpStatus, message: String, dataList: List<T>) : super(status, message) {
        this.dataList = dataList
    }
}