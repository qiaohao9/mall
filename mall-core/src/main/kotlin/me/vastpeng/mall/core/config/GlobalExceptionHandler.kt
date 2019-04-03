package me.vastpeng.mall.core.config

import me.vastpeng.mall.core.util.ResponseUtil
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import java.lang.IllegalArgumentException
import org.hibernate.validator.internal.engine.path.PathImpl
import javax.validation.ConstraintViolationException
import javax.validation.ValidationException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException


@ControllerAdvice
@Order(value = Ordered.LOWEST_PRECEDENCE)
class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseBody
    fun badArgumentHandler(e: IllegalArgumentException): Any {
        e.printStackTrace()
        return ResponseUtil.badArgumentValue()
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseBody
    fun badArgumentHandler(e: MethodArgumentTypeMismatchException): Any {
        e.printStackTrace()
        return ResponseUtil.badArgumentValue()
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseBody
    fun badArgumentHandler(e: MissingServletRequestParameterException): Any {
        e.printStackTrace()
        return ResponseUtil.badArgumentValue()
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseBody
    fun badArgumentHandler(e: HttpMessageNotReadableException): Any {
        e.printStackTrace()
        return ResponseUtil.badArgumentValue()
    }

    @ExceptionHandler(ValidationException::class)
    @ResponseBody
    fun badArgumentHandler(e: ValidationException): Any {
        e.printStackTrace()
        if (e is ConstraintViolationException) {
            val violations = e.constraintViolations
            for (item in violations) {
                val message = (item.propertyPath as PathImpl).getLeafNode().getName() + item.message
                return ResponseUtil.fail(402, message)
            }
        }
        return ResponseUtil.badArgumentValue()
    }

    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun seriousHandler(e: Exception): Any {
        e.printStackTrace()
        return ResponseUtil.serious()
    }

}