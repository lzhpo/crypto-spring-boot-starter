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

package com.lzhpo.crypto.serializer;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.crypto.CryptoStrategy;
import com.lzhpo.crypto.CryptoWrapper;
import com.lzhpo.crypto.annocation.Decrypt;
import com.lzhpo.crypto.annocation.Encrypt;
import com.lzhpo.crypto.annocation.IgnoreCrypto;
import com.lzhpo.crypto.util.CryptoUtils;
import com.lzhpo.cryptosensitive.core.resolve.HandlerMethodResolver;
import com.lzhpo.cryptosensitive.core.util.AnnotationUtils;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;

/**
 * Common method of {@code ContextValueFilter} for fastjson1 and fastjson2.
 *
 * @author lzhpo
 */
@Slf4j
public abstract class AbstractFastJsonCryptoValueFilter {

    protected Object process(Object object, String fieldName, Object fieldValue) {
        HandlerMethodResolver methodResolver = SpringUtil.getBean(HandlerMethodResolver.class);
        HandlerMethod handlerMethod = methodResolver.resolve();
        if (Objects.isNull(handlerMethod)
                || ObjectUtils.isEmpty(fieldValue)
                || !String.class.isAssignableFrom(fieldValue.getClass())) {
            return fieldValue;
        }

        Class<?> objectClass = object.getClass();
        Field field = ReflectUtil.getField(objectClass, fieldName);
        IgnoreCrypto ignCrypto = AnnotationUtils.getAnnotation(handlerMethod, IgnoreCrypto.class);
        Optional<IgnoreCrypto> ignCryptoOpt = Optional.ofNullable(ignCrypto);
        String[] ignFieldNames = ignCryptoOpt.map(IgnoreCrypto::value).orElse(new String[0]);
        if (ignCryptoOpt.isPresent() && ignCryptoOpt.map(IgnoreCrypto::value).isEmpty()) {
            return fieldValue;
        }

        Encrypt encrypt = field.getAnnotation(Encrypt.class);
        if (Objects.nonNull(encrypt) && Arrays.stream(ignFieldNames).noneMatch(name -> name.equals(fieldName))) {
            String[] arguments = encrypt.arguments();
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = (String) CryptoUtils.resolveEmbeddedValue(arguments[i]);
            }
            CryptoStrategy strategy = encrypt.strategy();
            return strategy.encrypt(new CryptoWrapper(object, fieldName, (String) fieldValue, arguments));
        }

        Decrypt decrypt = field.getAnnotation(Decrypt.class);
        if (Objects.nonNull(decrypt) && Arrays.stream(ignFieldNames).noneMatch(name -> name.equals(fieldName))) {
            String[] arguments = decrypt.arguments();
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = (String) CryptoUtils.resolveEmbeddedValue(arguments[i]);
            }
            CryptoStrategy strategy = decrypt.strategy();
            return strategy.decrypt(new CryptoWrapper(object, fieldName, (String) fieldValue, arguments));
        }

        return fieldValue;
    }
}
