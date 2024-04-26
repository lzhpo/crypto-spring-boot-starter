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

package com.lzhpo.crypto;

import static com.lzhpo.crypto.strategy.CryptoFallbackValue.NULL_VALUE;

import com.lzhpo.crypto.strategy.CryptoFallbackValue;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author lzhpo
 */
@Data
@ConfigurationProperties(prefix = "crypto")
public class CryptoProperties {

    /**
     * Will use default fallback value if not configure {@link CryptoFallbackValue}
     */
    private String defaultFallbackValue = "N/A";

    /**
     * AES crypto configurations.
     */
    @NestedConfigurationProperty
    private CryptoAes aes = new CryptoAes();

    /**
     * DES crypto configurations.
     */
    @NestedConfigurationProperty
    private CryptoDes des = new CryptoDes();

    /**
     * RSA crypto configurations.
     */
    @NestedConfigurationProperty
    private CryptoRsa rsa = new CryptoRsa();

    @Data
    public static class CryptoAes {
        private String key;
        private CryptoFallbackValue fallbackValue = NULL_VALUE;
    }

    @Data
    public static class CryptoDes {
        private String key;
        private CryptoFallbackValue fallbackValue = NULL_VALUE;
    }

    @Data
    public static class CryptoRsa {
        private String privateKey;
        private String publicKey;
        private CryptoFallbackValue fallbackValue = NULL_VALUE;
    }
}
