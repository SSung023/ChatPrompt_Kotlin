package kotlinproj.Util.exception

import kotlinproj.Util.exception.constants.ErrorCode
import org.springframework.http.HttpStatus

class BusinessException : RuntimeException {

    var status: HttpStatus? = null

    constructor(errorCode: ErrorCode) : super(errorCode.message) {
        this.status = errorCode.status
    }

    constructor(status: HttpStatus, errorCode: ErrorCode) : super(errorCode.message) {
        this.status = status
    }

    constructor(message: String) : super(message) { }
    constructor(message: String, cause: Throwable): super(message, cause) { }
    constructor(cause: Throwable) : super(cause) { }
}

