/*
 * Copyright 2023 lzhpo
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

package com.lzhpo.crypto.util;

import cn.hutool.extra.spring.SpringUtil;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;

/**
 * @author lzhpo
 * @see AbstractNamedValueMethodArgumentResolver
 * @see ExpressionValueMethodArgumentResolver
 */
@Slf4j
@UtilityClass
public class CryptoUtils {

    private static final String EMBEDDED_LEFT = "${";
    private static final String EMBEDDED_RIGHT = "}";

    /**
     * Resolve embedded value in {@code arguments}.
     *
     * <p>e.g: replace ${abc} and ${abc:1} to actually value.
     *
     * @param value value
     */
    public static Object resolveEmbeddedValue(String value) {
        if (!value.startsWith(EMBEDDED_LEFT) && !value.endsWith(EMBEDDED_RIGHT)) {
            return value;
        }

        log.debug("The {} is embedded value.", value);
        ConfigurableListableBeanFactory beanFactory = SpringUtil.getConfigurableBeanFactory();
        String placeholdersResolved = beanFactory.resolveEmbeddedValue(value);
        BeanExpressionResolver exprResolver = beanFactory.getBeanExpressionResolver();
        if (Objects.isNull(exprResolver)) {
            log.debug("Not found beanExpressionResolver in beanFactory.");
            return value;
        }

        BeanExpressionContext expressionContext = new BeanExpressionContext(beanFactory, new RequestScope());
        Object result = exprResolver.evaluate(placeholdersResolved, expressionContext);
        log.debug("Resolved {} to {}", value, result);
        return result;
    }
}
