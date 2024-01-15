package no.krex.http4kopenapi.annotations

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import org.http4k.contract.contract
import org.http4k.contract.jsonschema.v3.FieldRetrieval
import org.http4k.contract.jsonschema.v3.JacksonFieldMetadataRetrievalStrategy
import org.http4k.contract.jsonschema.v3.PrimitivesFieldMetadataRetrievalStrategy
import org.http4k.contract.jsonschema.v3.SimpleLookup
import org.http4k.contract.jsonschema.v3.then
import org.http4k.contract.meta
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.ApiRenderer
import org.http4k.contract.openapi.OpenAPIJackson
import org.http4k.contract.openapi.OpenAPIJackson.auto
import org.http4k.contract.openapi.v3.Api
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.ConfigurableJackson
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import java.time.Instant
import java.time.LocalDate

fun main() {

    contract {
        descriptionPath = "/schema"

        val apiRenderer: ApiRenderer<Api<JsonNode>, JsonNode> = ApiRenderer.Auto(
            json = OpenAPIJackson,
            schema = LifligAutoJsonToJsonSchema(
                json = ConfigurableJackson(
                    OpenAPIJackson.mapper.setSerializationInclusion(
                        /* Lets the schema generator see fields that are `null` in the example. */
                        JsonInclude.Include.ALWAYS
                    )
                ),
                fieldRetrieval = ExamplesWithNullLookup(
                    metadataRetrievalStrategy =
                    JacksonFieldMetadataRetrievalStrategy
                        .then(PrimitivesFieldMetadataRetrievalStrategy)
                        .then(LifligOpenapiAnnotationMetadataRetrievalStrategy)
                ),
            )
        )
        renderer = OpenApi3(
            apiInfo = ApiInfo(title = "Test api", version = "v1.0"),
            json = OpenAPIJackson,
            apiRenderer = apiRenderer
        )

        routes += "/" meta {
            this.returning(
                status = Status.OK,
                body = Body.auto<MeasureDto>().toLens() to MeasureDto(
                    id = "someid",
                    title = "Gjøre endringer",
                    description = "Den er for dyr",
                    effect = "hello world",
                    owner = null,
                    uncertainties = listOf("uncertainty"),
                    objectETag = "eTag",
                    deadline = LocalDate.MIN,
                    completed = CompletedMeasureDto(
                        completedBy = "d73bf1ae-641e-4ca4-aa04-6ed5ea186c89",
                        completedOn = Instant.EPOCH
                    ),
                    myMap = mapOf(
                        "outerKey1" to mapOf("innerKey1" to 3.14, "innerKey12" to 3.14159),
                        "outerKey2" to mapOf("innerKey2" to 2.71)
                    ),
                    myMapWithAnything = mapOf("number" to 4, "bool" to true),
                )
            )
        } bindContract Method.GET to { req: Request ->
            Response(Status.OK).body("Hello")
        }

    }.withFilter(DebuggingFilters.PrintRequestAndResponse())
        .withFilter(ServerFilters.CatchAll())
        .asServer(SunHttp(port = 8080)).start()

    println("Server started on http://localhost:8080")
}

