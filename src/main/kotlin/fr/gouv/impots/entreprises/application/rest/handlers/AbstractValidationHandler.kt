package fr.gouv.impots.entreprises.application.rest.handlers

import io.netty.handler.codec.DecoderException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono
import java.lang.reflect.ParameterizedType
import javax.validation.ValidationException

abstract class AbstractValidationHandler<T>(private val validator: Validator) : RequestHandler {

    companion object {
        val LOG = LoggerFactory.getLogger(AbstractValidationHandler::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    val bodyType = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>

    override fun handleRequest(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(bodyType)
            .flatMap { body ->
                val errors = BeanPropertyBindingResult(body, bodyType.name)
                validator.validate(body, errors)

                if (errors.allErrors.isNotEmpty()) {
                    throw ValidationException(formatErrors(errors))
                }

                processBody(body, request)
            }
            .doOnError {throwable -> LOG.error("Error while validating request of type {}", bodyType.name, throwable)}
            .onErrorResume(ValidationException::class.java) { throwable -> Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.message, throwable)) }
            .onErrorResume(ServerWebInputException::class.java) { throwable -> Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.message, throwable)) }
            .onErrorResume(DecoderException::class.java) { throwable -> Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.message, throwable)) }
    }

    private fun formatErrors(errors: Errors) : String =
        "${errors.fieldErrorCount} erreur(s) lors de la validation de ${errors.objectName} : ${errors.fieldErrors.map { "${it.field} ${it.defaultMessage}"}.reduce {acc, s -> "$acc - $s" }}"

    abstract fun processBody(body: T, request: ServerRequest): Mono<ServerResponse>
}
