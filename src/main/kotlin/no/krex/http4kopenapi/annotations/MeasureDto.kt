package no.krex.http4kopenapi.annotations

import java.time.Instant
import java.time.LocalDate

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
    val myMap: Map<String, Map<String, Double>>,
    val myMapWithAnything: Map<String, Any>,
)


class CompletedMeasureDto(val completedBy: String, val completedOn: Instant)
