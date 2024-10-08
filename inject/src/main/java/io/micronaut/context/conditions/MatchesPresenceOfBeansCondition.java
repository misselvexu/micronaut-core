/*
 * Copyright 2017-2024 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.context.conditions;

import io.micronaut.context.BeanContext;
import io.micronaut.context.condition.Condition;
import io.micronaut.context.condition.ConditionContext;
import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.UsedByGeneratedCode;

import java.util.Arrays;
import java.util.Objects;

/**
 * Matches presence of beans condition.
 *
 * @param beans The beans
 * @author Denis Stepanov
 * @since 4.6
 */
@UsedByGeneratedCode
@Internal
public record MatchesPresenceOfBeansCondition(
    AnnotationClassValue<?>[] beans) implements Condition {

    @Override
    public boolean matches(ConditionContext context) {
        BeanContext beanContext = context.getBeanContext();
        for (AnnotationClassValue<?> bean : beans) {
            Class<?> type = bean.getType().orElse(null);
            if (type == null || !beanContext.containsBean(type)) {
                context.fail("No bean of type [" + bean.getName() + "] present within context");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MatchesPresenceOfBeansCondition that = (MatchesPresenceOfBeansCondition) o;
        return Objects.deepEquals(beans, that.beans);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(beans);
    }

    @Override
    public String toString() {
        return "MatchesPresenceOfBeansCondition{" +
            "beans=" + Arrays.toString(beans) +
            '}';
    }
}
