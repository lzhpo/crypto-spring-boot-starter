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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lzhpo.crypto.CryptoStrategy;
import com.lzhpo.crypto.CryptoWrapper;
import com.lzhpo.crypto.annocation.Decrypt;
import com.lzhpo.crypto.annocation.IgnoreCrypto;
import com.lzhpo.crypto.resolve.HandlerMethodResolver;
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
public class JacksonCryptoDeserializer extends JsonDeserializer<String> {

    @Override
    public Class<?> handledType() {
        return String.class;
    }

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String fieldValue = jsonParser.getValueAsString();
        if (Objects.isNull(fieldValue)) {
            return null;
        }

        HandlerMethodResolver methodResolver = SpringUtil.getBean(HandlerMethodResolver.class);
        HandlerMethod handlerMethod = methodResolver.resolve();
        if (ObjectUtils.isEmpty(handlerMethod)) {
            return fieldValue;
        }

        String fieldName = jsonParser.currentName();
        Object object = jsonParser.currentValue();
        IgnoreCrypto ignCrypto = CryptoUtils.getAnnotation(handlerMethod, IgnoreCrypto.class);
        Optional<IgnoreCrypto> ignCryptoOpt = Optional.ofNullable(ignCrypto);
        if (ignCryptoOpt.isEmpty() || ignCryptoOpt.map(IgnoreCrypto::value).isPresent()) {
            Class<?> objectClass = object.getClass();
            Field field = ReflectUtil.getField(objectClass, fieldName);
            Decrypt decrypt = field.getAnnotation(Decrypt.class);
            String[] ignFieldNames = ignCryptoOpt.map(IgnoreCrypto::value).orElse(new String[0]);

            if (Objects.nonNull(decrypt) && Arrays.stream(ignFieldNames).noneMatch(name -> name.equals(fieldName))) {
                String[] arguments = decrypt.arguments();
                for (int i = 0; i < arguments.length; i++) {
                    arguments[i] = (String) CryptoUtils.resolveEmbeddedValue(arguments[i]);
                }
                CryptoStrategy strategy = decrypt.strategy();
                return strategy.decrypt(new CryptoWrapper(object, fieldName, fieldValue, arguments));
            }
        }

        return fieldValue;
    }
}
