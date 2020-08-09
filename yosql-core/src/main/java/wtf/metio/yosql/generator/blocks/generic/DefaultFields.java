package wtf.metio.yosql.generator.blocks.generic;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import wtf.metio.yosql.generator.api.AnnotationGenerator;
import wtf.metio.yosql.generator.blocks.api.Fields;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;

final class DefaultFields implements Fields {

    private final AnnotationGenerator annotations;

    DefaultFields(final AnnotationGenerator annotations) {
        this.annotations = annotations;
    }

    @Override
    public FieldSpec field(final Type type, final String name) {
        return field(TypeName.get(type), name);
    }

    @Override
    public FieldSpec field(final TypeName type, final String name) {
        return prepareField(type, name).build();
    }

    private FieldSpec.Builder prepareField(final TypeName type, final String name) {
        // TODO: configure modifiers?
        return builder(type, name).addModifiers(Modifier.PRIVATE, Modifier.FINAL);
    }

    @Override
    public FieldSpec.Builder prepareConstant(final Type type, final String name) {
        return prepareConstant(TypeName.get(type), name);
    }

    @Override
    public FieldSpec.Builder prepareConstant(final TypeName type, final String name) {
        // TODO: configure modifiers?
        return builder(type, name).addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
    }

    @Override
    // TODO: do we need "field" & "privateField"?
    public FieldSpec privateField(final TypeName type, final String name) {
        return builder(type, name)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
    }

    private FieldSpec.Builder builder(final TypeName type, final String name) {
        return FieldSpec.builder(type, name)
                .addAnnotations(annotations.generatedField());
    }

}
