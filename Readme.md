A proof-of-concept on modifying HTTP4k to allow null primitives in the Contract `example`, and not using nullability to decide required/optional properties.
Also adds a new annotation to set `description` and `required`.


TODO: not serialize every nullable field of the `SchemaNode`, while still providing schema for null fields in the example. You need to serialize nulls because the schema generator looks at the json first, class second. The example is serialized to json, then the keys are traversed.
However, the same serializer is applied to the `SchemaNode` hierarchy while generating the final schema, bloating the output.


Stuff are prefixed with `Liflig` because it's my current employer. I just needed to separate code from the default http4k classes. 

---

License: Apache 2.0. One file is copied from http4k with this license as well.

