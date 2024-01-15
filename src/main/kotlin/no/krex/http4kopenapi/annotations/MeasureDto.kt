package no.krex.http4kopenapi.annotations

import org.http4k.contract.openapi.OpenAPIJackson.auto
import org.http4k.core.Body
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

data class MeasureDto(
    val id: String,
    val title: String,
    val description: String,
    val effect: String,
    @LifligApiDescription(description = "Pass null if you don't know the owner")
    val owner: String?,
    val uncertainties: List<String>,
    val objectETag: String,
    val deadline: LocalDate,
    val completed: CompletedMeasureDto?,
    @LifligApiDescription(required = false)
    val valgfritt: String = "Hei",
) {
    companion object {
        val bodyLens = Body.auto<MeasureDto>().toLens()
        val example =
            MeasureDto(
                id = "someid",
                title = "Gjøre endringer på infrastrukturen",
                description = "Den er for dyr og vi må være raskere",
                effect = "hello world",
                owner = "someowner",
                uncertainties = listOf("unceratinty"),
                objectETag = "eTag",
                deadline = LocalDate.parse("2023-03-24"),
                completed =
                CompletedMeasureDto(
                    "d73bf1ae-641e-4ca4-aa04-6ed5ea186c89",
                    LocalDateTime.parse("2024-01-01T00:00")
                        .atZone(ZoneId.of("Europe/Paris"))
                        .toInstant()),
            )
    }
}


class CompletedMeasureDto(val completedBy: String, val completedOn: Instant)
