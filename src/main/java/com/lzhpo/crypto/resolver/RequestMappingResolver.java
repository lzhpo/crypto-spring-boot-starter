/*
 * Copyright lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzhpo.crypto.resolver;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class RequestMappingResolver implements HandlerMethodResolver {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public HandlerMethod resolve() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .map(this::getHandler)
                .map(HandlerExecutionChain::getHandler)
                .filter(HandlerMethod.class::isInstance)
                .map(HandlerMethod.class::cast)
                .orElse(null);
    }

    public final HandlerExecutionChain getHandler(HttpServletRequest request) {
        try {
            return requestMappingHandlerMapping.getHandler(request);
        } catch (Exception e) {
            log.error("Cannot get handler from current HttpServletRequest: {}", e.getMessage(), e);
            return null;
        }
    }
}
