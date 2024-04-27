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

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.crypto.CryptoProperties;
import com.lzhpo.crypto.strategy.CryptoStrategy;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.method.HandlerMethod;
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

    // spotless:off
    private static final String EMBEDDED_LEFT = "${";
    private static final String EMBEDDED_RIGHT = "}";
    private static final Map<CryptoStrategy, Supplier<String[]>> STRATEGY_FUNCTION_MAP = new HashMap<>(CryptoStrategy.values().length);

    static {
        CryptoProperties cryptoProperties = SpringUtil.getBean(CryptoProperties.class);
        STRATEGY_FUNCTION_MAP.put(CryptoStrategy.AES, () -> new String[]{cryptoProperties.getAes().getKey()});
        STRATEGY_FUNCTION_MAP.put(CryptoStrategy.DES, () -> new String[]{cryptoProperties.getDes().getKey()});
        STRATEGY_FUNCTION_MAP.put(CryptoStrategy.RSA, () -> {
            CryptoProperties.CryptoRsa cryptoRsa = cryptoProperties.getRsa();
            return new String[]{cryptoRsa.getPrivateKey(), cryptoRsa.getPublicKey()};
        });
        STRATEGY_FUNCTION_MAP.put(CryptoStrategy.SM4, () -> new String[]{cryptoProperties.getSm4().getKey()});
    }
    // spotless:on

    /**
     * Resolve embedded value in {@code arguments}.
     *
     * <p>e.g: replace ${abc} and ${abc:1} to actually value.
     *
     * @param value value
     */
    public static Object resolveEmbeddedValue(String value) {
        if (!StringUtils.hasText(value) || (!value.startsWith(EMBEDDED_LEFT) && !value.endsWith(EMBEDDED_RIGHT))) {
            return value;
        }

        ConfigurableListableBeanFactory beanFactory = SpringUtil.getConfigurableBeanFactory();
        String placeholdersResolved = beanFactory.resolveEmbeddedValue(value);
        BeanExpressionResolver exprResolver = beanFactory.getBeanExpressionResolver();
        if (Objects.isNull(exprResolver)) {
            return value;
        }

        BeanExpressionContext expressionContext = new BeanExpressionContext(beanFactory, new RequestScope());
        return exprResolver.evaluate(placeholdersResolved, expressionContext);
    }

    /**
     * Resolve arguments with embedded value.
     *
     * @param arguments arguments
     * @return after resolved arguments
     */
    public static String[] resolveArguments(String[] arguments) {
        if (ObjectUtils.isEmpty(arguments)) {
            return arguments;
        }

        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = (String) resolveEmbeddedValue(arguments[i]);
        }
        return arguments;
    }

    /**
     * Resolve arguments with embedded value.
     *
     * @param strategy  strategy
     * @param arguments arguments
     * @return after resolved arguments
     */
    public static String[] resolveArguments(CryptoStrategy strategy, String[] arguments) {
        if (ObjectUtils.isEmpty(arguments)) {
            return Optional.ofNullable(STRATEGY_FUNCTION_MAP.get(strategy))
                    .map(Supplier::get)
                    .orElse(arguments);
        }
        return resolveArguments(arguments);
    }

    /**
     * According to {@code annotationType}, get the annotation from {@code handlerMethod}
     *
     * @param handlerMethod  {@link HandlerMethod}
     * @param annotationType annotationType
     * @return {@link Annotation}
     */
    public static <T extends Annotation> T getAnnotation(HandlerMethod handlerMethod, Class<T> annotationType) {
        Class<?> beanType = handlerMethod.getBeanType();
        // First get the annotation from the class of the method
        T annotation = AnnotationUtil.getAnnotation(beanType, annotationType);
        if (Objects.isNull(annotation)) {
            // If not get the annotation from this class, will get it from the current method
            annotation = handlerMethod.getMethodAnnotation(annotationType);
        }
        return annotation;
    }

    /**
     * Get not blank content.
     *
     * @param content the content
     * @param message the message
     * @return result
     */
    public static String requireNonBlank(String content, String message) {
        if (!StringUtils.hasText(content)) {
            throw new CryptoException(message);
        }
        return content;
    }
}
