package no.krex.http4kopenapi.annotations

import no.krex.http4kopenapi.annotations.LifligApiDescription.Companion.toMap
import org.http4k.contract.jsonschema.v3.FieldMetadata
import org.http4k.contract.jsonschema.v3.FieldMetadataRetrievalStrategy
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Finds properties in a class.
 */
object LifligOpenapiAnnotationMetadataRetrievalStrategy : FieldMetadataRetrievalStrategy {
    override fun invoke(target: Any, fieldName: String): FieldMetadata =
        target.javaClass.findPropertyDescription(fieldName)
            ?.toMap()
            .let { FieldMetadata(it.orEmpty()) }

    private fun Class<Any>.findPropertyDescription(name: String): LifligApiDescription? =
        kotlin.memberProperties.asSequence()
            .filter { property -> property.name == name }
            .firstOrNull()
            ?.let { property -> Pair(property, property.findAnnotation<LifligApiDescription>()) }
            ?.also { (prop, annotation) ->
                // Some validation/sanity-checks

                if (annotation != null && !annotation.required) {
                    check(kotlin.constructors.any { it.parameters.any { p -> p.name == prop.name && p.isOptional } }) {
                        "An optional parameter should have a default value in a constructor"
                    }
                }
            }?.second
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class LifligApiDescription(val description: String = "", val required: Boolean = true) {
    companion object {
        fun LifligApiDescription.toMap(): Map<String, Any?> = buildMap {
            if (description != "") {
                put("description", description)
            }
            if (!required) {
                put("required", required)
            }
        }
    }
}