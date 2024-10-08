/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.management.endpoint.routes.impl;

import io.micronaut.context.annotation.Requires;
import io.micronaut.inject.MethodExecutionHandle;
import io.micronaut.management.endpoint.routes.RouteData;
import io.micronaut.management.endpoint.routes.RoutesEndpoint;
import io.micronaut.web.router.UriRouteInfo;
import jakarta.inject.Singleton;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default route data implementation.
 *
 * @author James Kleeh
 * @since 1.0
 */
@Singleton
@Requires(beans = RoutesEndpoint.class)
public class DefaultRouteData implements RouteData<Map<String, String>> {

    @Override
    public Map<String, String> getData(UriRouteInfo<?, ?> routeInfo) {
        return Collections.singletonMap("method", getMethodString(routeInfo.getTargetMethod()));
    }

    /**
     * @param targetMethod The {@link MethodExecutionHandle}
     * @return A String with the target method
     */
    protected String getMethodString(MethodExecutionHandle<?, ?> targetMethod) {
        return targetMethod.getReturnType().asArgument().getTypeString(false) +
            " " +
            targetMethod.getDeclaringType().getName() +
            '.' +
            targetMethod.getMethodName() +
            "(" +
            Arrays.stream(targetMethod.getArguments())
                .map(argument -> argument.getType().getName() + " " + argument.getName())
                .collect(Collectors.joining(", ")) +
            ")";
    }
}
