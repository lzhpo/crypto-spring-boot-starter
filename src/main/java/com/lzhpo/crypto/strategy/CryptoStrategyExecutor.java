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
import java.util.Optional;
import java.util.concurrent.Callable;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzhpo
 */
@Slf4j
@UtilityClass
public class CryptoStrategyExecutor {

    private static final CryptoProperties CRYPTO_PROPERTIES;

    static {
        CRYPTO_PROPERTIES = SpringUtil.getBean(CryptoProperties.class);
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
            Optional<CryptoFallbackValue> fallbackValue = Optional.ofNullable(CRYPTO_PROPERTIES.getStrategy())
                    .map(configurationMap -> configurationMap.get(strategy))
                    .map(CryptoStrategyConfiguration::getFallbackValue);

            return fallbackValue.isPresent()
                    ? fallbackValue.get().getValue(originalValue, e)
                    : CRYPTO_PROPERTIES.getDefaultFallbackValue();
        }
    }
}
