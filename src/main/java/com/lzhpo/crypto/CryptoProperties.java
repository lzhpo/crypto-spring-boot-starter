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
package com.lzhpo.crypto;

import com.lzhpo.crypto.strategy.CryptoFallbackValue;
import com.lzhpo.crypto.strategy.CryptoStrategy;
import com.lzhpo.crypto.strategy.CryptoStrategyConfiguration;
import java.util.EnumMap;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.ObjectUtils;

/**
 * @author lzhpo
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "crypto")
public class CryptoProperties implements InitializingBean {

    /**
     * Will use default fallback value if not configure {@link CryptoFallbackValue}
     */
    private String defaultFallbackValue = "N/A";

    /**
     * Crypto strategy configurations
     */
    private Map<CryptoStrategy, CryptoStrategyConfiguration> strategy = new EnumMap<>(CryptoStrategy.class);

    // spotless:off
    @Override
    public void afterPropertiesSet() {
        if (ObjectUtils.isEmpty(strategy)) {
            log.info("Not configure any crypto strategy, means you must provide strategy arguments manually in @Encrypt and @Decrypt.");
        }
    }
    // spotless:on
}
