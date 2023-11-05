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

package com.lzhpo.crypto.annocation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lzhpo.crypto.CryptoStrategy;
import com.lzhpo.crypto.serializer.JacksonCryptoDeserializer;
import com.lzhpo.crypto.serializer.JacksonCryptoSerializer;
import java.lang.annotation.*;

/**
 * @author lzhpo
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@JacksonAnnotationsInside
@JsonSerialize(using = JacksonCryptoSerializer.class)
@JsonDeserialize(using = JacksonCryptoDeserializer.class)
public @interface Decrypt {

    CryptoStrategy strategy();

    String[] arguments() default {};
}
