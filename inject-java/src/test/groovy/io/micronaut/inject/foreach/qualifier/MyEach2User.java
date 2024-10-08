package io.micronaut.inject.foreach.qualifier;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

import java.util.List;

@Requires(property = "spec", value = "EachBeanQualifierSpec")
@Singleton
public class MyEach2User {

    private final List<MyEach2> myEach2s;

    public MyEach2User(List<MyEach2> myEach2s) {
        this.myEach2s = myEach2s;
    }

    public List<MyEach2> getAll() {
        return myEach2s;
    }
}
