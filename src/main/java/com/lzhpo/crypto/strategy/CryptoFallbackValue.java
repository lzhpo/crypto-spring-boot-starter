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

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;

/**
 * Fallback value when crypto error.
 *
 * @author lzhpo
 */
public enum CryptoFallbackValue {
    ORIGINAL_VALUE() {
        @Override
        public String getValue(String originalValue, Exception e) {
            return originalValue;
        }
    },

    NULL_VALUE() {
        @Override
        public String getValue(String originalValue, Exception e) {
            return null;
        }
    },

    EMPTY_STRING() {
        @Override
        public String getValue(String originalValue, Exception e) {
            return StrUtil.EMPTY;
        }
    },

    THROW_EXCEPTION() {
        @Override
        public String getValue(String originalValue, Exception e) {
            throw new CryptoException(e.getMessage(), e);
        }
    };

    /**
     * Different fallback has different result.
     *
     * @param originalValue the original value
     * @param e             the Exception
     * @return result
     */
    public abstract String getValue(String originalValue, Exception e);
}
