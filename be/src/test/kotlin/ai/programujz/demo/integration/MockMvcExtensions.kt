package ai.programujz.demo.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

fun MockMvc.performGet(
    url: String,
    headers: Map<String, String> = emptyMap()
): ResultActions {
    return perform(
        get(url)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .apply {
                headers.forEach { (key, value) -> header(key, value) }
            }
    )
}

fun MockMvc.performPost(
    url: String,
    requestBody: String,
    headers: Map<String, String> = emptyMap()
): ResultActions {
    return perform(
        post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .apply {
                headers.forEach { (key, value) -> header(key, value) }
            }
    )
}

fun MockMvc.performPut(
    url: String,
    requestBody: String,
    headers: Map<String, String> = emptyMap()
): ResultActions {
    return perform(
        put(url)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .apply {
                headers.forEach { (key, value) -> header(key, value) }
            }
    )
}

fun MockMvc.performPatch(
    url: String,
    requestBody: String,
    headers: Map<String, String> = emptyMap()
): ResultActions {
    return perform(
        patch(url)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .apply {
                headers.forEach { (key, value) -> header(key, value) }
            }
    )
}

fun MockMvc.performDelete(
    url: String,
    headers: Map<String, String> = emptyMap()
): ResultActions {
    return perform(
        delete(url)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .apply {
                headers.forEach { (key, value) -> header(key, value) }
            }
    )
}

// Extension functions for extracting fields from ResultActions

/**
 * Extract a field value from the JSON response as a String
 */
fun ResultActions.extractField(fieldName: String): String? {
    val objectMapper = jacksonObjectMapper()
    val responseBody = this.andReturn().response.contentAsString
    val responseMap: Map<String, Any> = objectMapper.readValue(responseBody)
    return responseMap[fieldName]?.toString()
}

/**
 * Extract a field value from the JSON response with type conversion
 */
inline fun <reified T> ResultActions.extractFieldAs(fieldName: String): T? {
    val objectMapper = jacksonObjectMapper()
    val responseBody = this.andReturn().response.contentAsString
    val responseMap: Map<String, Any> = objectMapper.readValue(responseBody)
    val value = responseMap[fieldName] ?: return null
    
    return when (T::class) {
        String::class -> value.toString() as T
        Int::class -> value.toString().toInt() as T
        Long::class -> value.toString().toLong() as T
        Boolean::class -> value.toString().toBoolean() as T
        else -> objectMapper.convertValue(value, T::class.java)
    }
}

/**
 * Extract the entire response body as a typed object
 */
inline fun <reified T> ResultActions.extractResponseAs(): T {
    val objectMapper = jacksonObjectMapper()
    val responseBody = this.andReturn().response.contentAsString
    return objectMapper.readValue(responseBody)
}

/**
 * Extract the entire response body as a Map
 */
fun ResultActions.extractResponseAsMap(): Map<String, Any> {
    val objectMapper = jacksonObjectMapper()
    val responseBody = this.andReturn().response.contentAsString
    return objectMapper.readValue(responseBody)
}