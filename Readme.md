A proof-of-concept on modifying HTTP4k to allow null primitives in the Contract `example`, and not using nullability to decide required/optional properties.
Also adds a new annotation to set `description` and `required`.

Fixes:
1. `null` primitives in the Contract example: `example = MyDto(firstName = null)`.
2. Properties are `required` by default, opt-in for optional: 
   - `data class MyDto(firstName: String?, lastName: String?)` -> `"required": ["firstName", "lastName"]` .
   - `data class MyDto(@LifligOpenapiAnnotation(required = false) val firstName: String = "Michael", val lastName: String)` -> `"required": ["lastName"]` .
   - Validation for optionals: fails when missing default value in `class MyDto(@LifligOpenapiAnnotation(required = false) val firstName: String /* = "Michael" removed default*/)` -> Exception .
3. Non-jackson way to set description: `@LifligOpenapiAnnotation(description = "Hello world") val myField` .
4. Correct schema for map-of-maps: `mapOf("key" to mapOf("keyInner" to 5))` -> `{ "additionalProperties": { "additionalProperties": { "type": "number} } }` .
5. Correct schema for map-of-various: `mapOf("key" to 1, "key2" to true)` -> `"additionalProperties": true` .


**TODO**: 
1. Not serialize every nullable field of the `SchemaNode`, while still providing schema for null fields in the example. You need to serialize nulls because the schema generator looks at the json first, class second. The example is serialized to json, then the keys are traversed.
However, the same serializer is applied to the `SchemaNode` hierarchy while generating the final schema, bloating the output.
2. Missing `format` on primitive `additionalProperties`


Stuff is prefixed with `Liflig` because it's my current employer. I just needed to separate code from the default http4k classes. 

---

License: Apache 2.0. One file is copied from http4k with this license as well.

