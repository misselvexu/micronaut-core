package io.micronaut.inject.qualifiers

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.core.type.Argument
import jakarta.inject.Singleton
import spock.lang.AutoCleanup
import spock.lang.Specification

class MatchArgumentSpec extends Specification {

    @AutoCleanup
    ApplicationContext context = ApplicationContext.run()

    void "test match serialize specific argument"() {
        given:
            def argument = Argument.of(MySerializer, [Argument.of(List, [Argument.STRING] as Argument[])] as Argument[])
        when:
            def beanDefinitions = context.getBeanDefinitions(argument, MatchArgumentQualifier.ofArgument(argument))

        then:
            beanDefinitions.size() == 1
            beanDefinitions[0].getBeanType() == ListStringSerializer
    }

    void "test match serialize object argument"() {
        given:
            def argument = Argument.of(MySerializer, [Argument.of(List, [Argument.OBJECT_ARGUMENT] as Argument[])] as Argument[])
        when:
            def beanDefinitions = context.getBeanDefinitions(argument, MatchArgumentQualifier.ofArgument(argument))

        then:
            beanDefinitions.size() == 2
            beanDefinitions.collect { it.getBeanType() } as Set == [ListGenericSerializer, ListObjectSerializer] as Set
    }

    void "test match serialize non-specific argument"() {
        given:
            def argument = Argument.of(MySerializer, [Argument.of(List, [Argument.of(Boolean)] as Argument[])] as Argument[])
        when:
            def beanDefinitions = context.getBeanDefinitions(argument, MatchArgumentQualifier.ofArgument(argument))

        then:
            beanDefinitions.size() == 2
            beanDefinitions.collect { it.getBeanType() } as Set == [ListGenericSerializer, ListObjectSerializer] as Set
    }

    void "test match serialize Number argument"() {
        given:
            def argument = Argument.of(MySerializer, [Argument.of(List, [Argument.of(Number)] as Argument[])] as Argument[])
        when:
            def beanDefinitions = context.getBeanDefinitions(argument, MatchArgumentQualifier.ofArgument(argument))

        then:
            beanDefinitions.size() == 1
            beanDefinitions[0].getBeanType() == ListGenericNumberSerializer
    }

    void "test match serialize Long argument"() {
        given:
            def argument = Argument.of(MySerializer, [Argument.of(List, [Argument.of(Long)] as Argument[])] as Argument[])
        when:
            def beanDefinitions = context.getBeanDefinitions(argument, MatchArgumentQualifier.ofArgument(argument))

        then:
            beanDefinitions.size() == 1
            beanDefinitions[0].getBeanType() == ListLongSerializer
    }

    void "test match serialize Double argument"() {
        given:
            def argument = Argument.of(MySerializer, [Argument.of(List, [Argument.of(Double)] as Argument[])] as Argument[])
        when:
            def beanDefinitions = context.getBeanDefinitions(argument, MatchArgumentQualifier.ofArgument(argument))

        then:
            beanDefinitions.size() == 1
            beanDefinitions[0].getBeanType() == ListGenericNumberSerializer
    }

    void "test match serialize ArrayList Long argument"() {
        given:
            def argument = Argument.of(MySerializer, [Argument.of(ArrayList, [Argument.of(Long)] as Argument[])] as Argument[])
        when:
            def beanDefinitions = context.getBeanDefinitions(argument, MatchArgumentQualifier.ofArgument(argument))

        then:
            beanDefinitions.size() == 1
            beanDefinitions[0].getBeanType() == ListArrayListLongSerializer
    }

    void "test match serialize Collection String argument"() {
        when:
            def beanDefinitions = context.getBeanDefinitions(MySerializer,
                    MatchArgumentQualifier.contravariant(MySerializer, Argument.of(Collection, [Argument.of(String)] as Argument[]))
            )

        then:
            beanDefinitions.size() == 1
            beanDefinitions[0].getBeanType() == IterableSerializer
    }

    void "test match deserialize List String argument"() {
        def item = Argument.of(Collection, [Argument.of(String)] as Argument[])
        given:
            def argument = Argument.of(MyDeserializer, [item] as Argument[])
        when:
            def beanDefinitions = context.getBeanDefinitions(argument, MatchArgumentQualifier.covariant(MyDeserializer, item))

        then:
            beanDefinitions.size() == 1
            beanDefinitions[0].getBeanType() == ArrayListStringDeserializer
    }

    void "test match deserialize List Object argument"() {
        def item = Argument.of(Collection, [Argument.of(Object)] as Argument[])
        given:
            def argument = Argument.of(MyDeserializer, [item] as Argument[])
        when:
            def beanDefinitions = context.getBeanDefinitions(argument, MatchArgumentQualifier.covariant(MyDeserializer, item))

        then:
            beanDefinitions.size() == 1
            beanDefinitions[0].getBeanType() == ArrayListObjectDeserializer
    }

    void "test match deserialize Collection Number argument"() {
        def item = Argument.of(Collection, [Argument.of(Number)] as Argument[])
        given:
            def argument = Argument.of(MyDeserializer, [item] as Argument[])
        when:
            def beanDefinitions = context.getBeanDefinitions(argument, MatchArgumentQualifier.covariant(MyDeserializer, item))

        then:
            beanDefinitions.size() == 1
            beanDefinitions[0].getBeanType() == ArrayListNumberDeserializer
    }

    void "test match deserialize List Number argument"() {
        def item = Argument.of(List, [Argument.of(Number)] as Argument[])
        given:
            def argument = Argument.of(MyDeserializer, [item] as Argument[])
        when:
            def beanDefinitions = context.getBeanDefinitions(argument, MatchArgumentQualifier.covariant(MyDeserializer, item))

        then:
            beanDefinitions.size() == 1
            beanDefinitions[0].getBeanType() == ListNumberDeserializer
    }

    void "test match deserialize enum with interface argument"() {
        def item = Argument.of(MyEnum)
        given:
            def argument = Argument.of(MyDeserializer, [item] as Argument[])
        when:
            def beanDefinitions = context.getBeanDefinitions(argument, MatchArgumentQualifier.covariant(MyDeserializer, item))

        then:
            beanDefinitions.size() == 1
            beanDefinitions[0].getBeanType() == EnumDeserializer
    }

    void "test finding higher types type object finding all instances"() {
        def item = Argument.OBJECT_ARGUMENT
        when:
            def beanDefinitions = context.getBeanDefinitions(MyReader, MatchArgumentQualifier.covariant(MyReader, item))
            def beanAllDefinitions = context.getBeanDefinitions(Argument.of(MyReader))

        then:
            // The list reader is eliminated because array list is higher
            beanDefinitions.size() + 1 == beanAllDefinitions.size()
            beanDefinitions.size() == 3
    }

    void "test finding higher types type object finding all instances - object reader is a direct match and eliminating everything else"() {
        given:
            ApplicationContext context = ApplicationContext.run(["MatchArgumentSpec.enableObjectReader": "true"])
            def item = Argument.OBJECT_ARGUMENT
        when:
            def beanDefinitions = context.getBeanDefinitions(MyReader, MatchArgumentQualifier.covariant(MyReader, item))

        then:
            beanDefinitions.size() == 1
        cleanup:
            context.close()
    }

    void "test generic higher types reader"() {
        when:
            def stringReaders = context.getBeanDefinitions(MyReader2,
                    MatchArgumentQualifier.covariant(MyReader2, Argument.of(MyType3)))

        then:
            stringReaders.size() == 1
            stringReaders[0].getBeanType() == MyType2AndHigherReader
    }

    interface MySerializer<E> {}

    @Singleton
    static class ListObjectSerializer implements MySerializer<List<Object>> {}

    @Singleton
    static class ListGenericNumberSerializer<T extends Number> implements MySerializer<List<T>> {}

    @Singleton
    static class CollectionGenericNumberSerializer<T extends Number> implements MySerializer<Collection<T>> {}

    @Singleton
    static class ListLongSerializer implements MySerializer<List<Long>> {}

    @Singleton
    static class ListArrayListLongSerializer implements MySerializer<ArrayList<Long>> {}

    @Singleton
    static class ListStringSerializer implements MySerializer<List<String>> {}

    @Singleton
    static class ListCharSequenceSerializer implements MySerializer<List<CharSequence>> {}

    @Singleton
    static class ListGenericSerializer<T> implements MySerializer<List<T>> {}

    @Singleton
    static class IterableSerializer<T> implements MySerializer<Iterable<T>> {}

    interface MyDeserializer<E> {}

    @Singleton
    static class IterableObjectDeserializer<T> implements MyDeserializer<Iterable<T>> {}

    @Singleton
    static class ListObjectDeserializer<T> implements MyDeserializer<List<T>> {}

    @Singleton
    static class ListStringDeserializer implements MyDeserializer<List<String>> {}

    @Singleton
    static class ArrayListObjectDeserializer<T> implements MyDeserializer<ArrayList<T>> {}

    @Singleton
    static class ArrayListStringDeserializer implements MyDeserializer<ArrayList<String>> {}

    @Singleton
    static class ArrayListNumberDeserializer implements MyDeserializer<ArrayList<Number>> {}

    @Singleton
    static class ListNumberDeserializer implements MyDeserializer<List<Number>> {}

    @Singleton
    static class EnumDeserializer<E extends Enum<E>> implements MyDeserializer<E> {}

    @Singleton
    static class MyInterfaceDeserializer implements MyDeserializer<MyInterface> {}

    static interface MyInterface {}

    static enum MyEnum implements MyInterface {}

    interface MyReader<E> {}

    @Requires(property = "MatchArgumentSpec.enableObjectReader", value = "true")
    @Singleton
    static class ObjectMyReader implements MyReader<Object> {}

    @Singleton
    static class StringMyReader implements MyReader<CharSequence> {}

    @Singleton
    static class ListMyReader<T extends List<T>> implements MyReader<T> {}

    @Singleton
    static class ArrayListMyReader<T extends ArrayList<T>> implements MyReader<T> {}

    @Singleton
    static class EnumMyReader implements MyReader<MyEnum> {}

    static class MyType1 implements MyInterface {

    }

    static class MyType2 implements MyInterface {

    }

    static class MyType3 extends MyType2 {

    }
    static interface MyInterface2 extends MyInterface {

    }

    interface MyReader2<E> {}

    @Singleton
    static class MyType2AndHigherReader<T extends MyType2> implements MyReader2<T> {}

    @Singleton
    static class NumberReader<T extends Number> implements MyReader2<T> {}

}


