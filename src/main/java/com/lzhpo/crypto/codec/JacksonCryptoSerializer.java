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
package com.lzhpo.crypto.codec;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.lzhpo.crypto.annocation.Encrypt;
import com.lzhpo.crypto.annocation.IgnoreCrypto;
import com.lzhpo.crypto.resolver.HandlerMethodResolver;
import com.lzhpo.crypto.strategy.CryptoStrategy;
import com.lzhpo.crypto.util.CryptoUtils;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;

/**
 * @author lzhpo
 */
@Slf4j
public class JacksonCryptoSerializer extends JsonSerializer<String> {

    @Override
    public Class<String> handledType() {
        return String.class;
    }

    // spotless:off
    @Override
    public void serialize(String fieldValue, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (Objects.isNull(fieldValue)) {
            gen.writeNull();
            return;
        }

        HandlerMethodResolver handlerMethodResolver = SpringUtil.getBean(HandlerMethodResolver.class);
        HandlerMethod handlerMethod = handlerMethodResolver.resolve();
        if (ObjectUtils.isEmpty(handlerMethod)) {
            gen.writeString(fieldValue);
            return;
        }

        String fieldName = gen.getOutputContext().getCurrentName();
        IgnoreCrypto ignCrypto = CryptoUtils.getAnnotation(handlerMethod, IgnoreCrypto.class);
        Optional<IgnoreCrypto> ignCryptoOpt = Optional.ofNullable(ignCrypto);
        Optional<String[]> ignFieldNamesOpt = ignCryptoOpt.map(IgnoreCrypto::value);
        if ((ignCryptoOpt.isPresent() && ignFieldNamesOpt.filter(ArrayUtil::isNotEmpty).isEmpty())
                || ignFieldNamesOpt.filter(names -> Arrays.asList(names).contains(fieldName)).isPresent()) {
            gen.writeString(fieldValue);
            log.debug("Skip encrypt for {}, because @IgnoreCrypto is null or not contains {}", fieldName, fieldName);
            return;
        }

        Object object = gen.getCurrentValue();
        Class<?> objectClass = object.getClass();
        Field field = ReflectUtil.getField(objectClass, fieldName);
        Encrypt encrypt = field.getAnnotation(Encrypt.class);
        if (Objects.nonNull(encrypt)) {
            CryptoStrategy strategy = encrypt.strategy();
            String[] arguments = CryptoUtils.resolveArguments(strategy, encrypt.arguments());
            log.debug("Encrypt for {} with {} strategy, arguments={}", fieldName, strategy.name(), arguments);
            fieldValue = strategy.encrypt(new CryptoWrapper(object, fieldName, fieldValue, arguments));
        }

        gen.writeString(fieldValue);
    }
    // spotless:on
}
