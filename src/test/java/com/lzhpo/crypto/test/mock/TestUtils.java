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
package com.lzhpo.crypto.test.mock;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import java.security.KeyPair;
import org.junit.jupiter.api.Test;

/**
 * @author lzhpo
 */
class TestUtils {

    @Test
    void generateRsaKey() {
        KeyPair keyPair = SecureUtil.generateKeyPair("RSA");

        String privateKey = Base64.encode(keyPair.getPrivate().getEncoded());
        System.out.println("RSA privateKey: " + privateKey);

        String publicKey = Base64.encode(keyPair.getPublic().getEncoded());
        System.out.println("RSA publicKey: " + publicKey);
    }

    @Test
    void generateSm4Key() {
        String key = RandomUtil.randomString(16);
        System.out.println("SM4 key: " + key);
    }
}
