/*
 * Copyright 2024 lzhpo
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

package com.lzhpo.crypto.strategy;

import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.crypto.CryptoProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzhpo
 */
@Slf4j
@UtilityClass
public class CryptoStrategyExecutor {

    private static final String DEFAULT_FALLBACK_VALUE;
    private static final Map<CryptoStrategy, CryptoFallbackValue> FALLBACK_VALUE_MAP = new HashMap<>();

    static {
        CryptoProperties cryptoProperties = SpringUtil.getBean(CryptoProperties.class);
        DEFAULT_FALLBACK_VALUE = cryptoProperties.getDefaultFallbackValue();
        FALLBACK_VALUE_MAP.put(CryptoStrategy.AES, cryptoProperties.getAes().getFallbackValue());
        FALLBACK_VALUE_MAP.put(CryptoStrategy.DES, cryptoProperties.getDes().getFallbackValue());
        FALLBACK_VALUE_MAP.put(CryptoStrategy.RSA, cryptoProperties.getRsa().getFallbackValue());
        FALLBACK_VALUE_MAP.put(CryptoStrategy.SM4, cryptoProperties.getSm4().getFallbackValue());
    }

    /**
     * Safely execute strategy target method.
     *
     * @param originalValue the original value
     * @param strategy      the strategy
     * @param method        the target method
     * @return result
     */
    public static String executeSafely(String originalValue, CryptoStrategy strategy, Callable<String> method) {
        try {
            return method.call();
        } catch (Exception e) {
            log.error("Execute {} with value[{}] error: {}", strategy, originalValue, e.getMessage(), e);
            CryptoFallbackValue fallbackValue = FALLBACK_VALUE_MAP.get(strategy);
            return Objects.nonNull(fallbackValue) ? fallbackValue.getValue(originalValue, e) : DEFAULT_FALLBACK_VALUE;
        }
    }
}
