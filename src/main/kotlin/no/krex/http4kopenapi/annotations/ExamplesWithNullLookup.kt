package no.krex.http4kopenapi.annotations

import org.http4k.contract.jsonschema.v3.Field
import org.http4k.contract.jsonschema.v3.FieldMetadataRetrievalStrategy
import org.http4k.contract.jsonschema.v3.FieldRetrieval
import org.http4k.contract.jsonschema.v3.NoFieldFound
import org.http4k.contract.jsonschema.v3.NoOpFieldMetadataRetrievalStrategy
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

/**
 * Lets you use `null` as an example in the OpenApi schema and the field will still show up.
 */
class ExamplesWithNullLookup(
    private val renamingStrategy: (String) -> String = { it },
    private val metadataRetrievalStrategy: FieldMetadataRetrievalStrategy = NoOpFieldMetadataRetrievalStrategy()
) : FieldRetrieval {
    override fun invoke(schemaExample: Any, name: String): Field {
        val fields = try {
            schemaExample::class.memberProperties.associateBy { renamingStrategy(it.name) }
        } catch (e: Error) {
            emptyMap<String, KProperty1<out Any, Any?>>()
        }

        val property: KProperty1<out Any, Any?> = fields[name] ?: throw NoFieldFound(
            name,
            schemaExample
        )

        val exampleValue: Any?= try {
            property.javaGetter?.invoke(schemaExample)
                ?: property.javaField?.takeIf { it.trySetAccessible() }?.get(schemaExample)
        } catch (ex: IllegalArgumentException) {
           null
        }

        return Field(
            value = exampleValue ?: property.returnType.classifier as KClass<*>,
            isNullable = property.returnType.isMarkedNullable || exampleValue == null,
            metadata = metadataRetrievalStrategy(schemaExample, name)
        )
    }
}

public object NullField