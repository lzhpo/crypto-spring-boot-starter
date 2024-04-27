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

package com.lzhpo.crypto.codec;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.crypto.annocation.Decrypt;
import com.lzhpo.crypto.annocation.Encrypt;
import com.lzhpo.crypto.annocation.IgnoreCrypto;
import com.lzhpo.crypto.resolver.HandlerMethodResolver;
import com.lzhpo.crypto.strategy.CryptoStrategy;
import com.lzhpo.crypto.util.CryptoUtils;
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

    // spotless:off
    protected Object process(Object object, String fieldName, Object fieldValue) {
        HandlerMethodResolver methodResolver = SpringUtil.getBean(HandlerMethodResolver.class);
        HandlerMethod handlerMethod = methodResolver.resolve();
        if (Objects.isNull(handlerMethod) || ObjectUtils.isEmpty(fieldValue)
                || !String.class.isAssignableFrom(fieldValue.getClass())) {
            return fieldValue;
        }

        Optional<IgnoreCrypto> ignCryptoOpt = Optional.ofNullable(CryptoUtils.getAnnotation(handlerMethod, IgnoreCrypto.class));
        Optional<String[]> ignFieldNamesOpt = ignCryptoOpt.map(IgnoreCrypto::value);
        if ((ignCryptoOpt.isPresent() && !ignFieldNamesOpt.filter(ArrayUtil::isNotEmpty).isPresent())
                || ignFieldNamesOpt.filter(names -> Arrays.asList(names).contains(fieldName)).isPresent()) {
            log.debug("Skip encrypt or decrypt for {}, because @IgnoreCrypto is null or not contains it.", fieldName);
            return fieldValue;
        }

        Class<?> objectClass = object.getClass();
        Field field = ReflectUtil.getField(objectClass, fieldName);

        Encrypt encrypt = field.getAnnotation(Encrypt.class);
        if (Objects.nonNull(encrypt)) {
            CryptoStrategy strategy = encrypt.strategy();
            String[] arguments = CryptoUtils.resolveArguments(strategy, encrypt.arguments());
            log.debug("Encrypt for {} with {} strategy, arguments={}", fieldName, strategy.name(), arguments);
            return strategy.encrypt(new CryptoWrapper(object, fieldName, (String) fieldValue, arguments));
        }

        Decrypt decrypt = field.getAnnotation(Decrypt.class);
        if (Objects.nonNull(decrypt)) {
            CryptoStrategy strategy = decrypt.strategy();
            String[] arguments = CryptoUtils.resolveArguments(strategy, decrypt.arguments());
            log.debug("Decrypt for {} with {} strategy, arguments={}", fieldName, strategy.name(), arguments);
            return strategy.decrypt(new CryptoWrapper(object, fieldName, (String) fieldValue, arguments));
        }

        return fieldValue;
    }
    // spotless:on
}
