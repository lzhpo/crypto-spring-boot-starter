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
package com.lzhpo.crypto.strategy;

import static cn.hutool.crypto.asymmetric.KeyType.PrivateKey;
import static cn.hutool.crypto.asymmetric.KeyType.PublicKey;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.symmetric.SM4;
import com.lzhpo.crypto.annocation.DecryptHandler;
import com.lzhpo.crypto.annocation.EncryptHandler;
import com.lzhpo.crypto.codec.CryptoWrapper;
import com.lzhpo.crypto.util.CryptoUtils;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzhpo
 * @see <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyGenerator">KeyGenerator</a>
 */
@Slf4j
public enum CryptoStrategy {
    AES {
        @Override
        public String encrypt(CryptoWrapper cryptoWrapper) {
            String fieldValue = cryptoWrapper.getFieldValue();
            String[] arguments = cryptoWrapper.getArguments();

            String key = CryptoUtils.requireNonBlank(arguments[0], "AES key cannot be blank.");
            AES aes = SecureUtil.aes(key.getBytes());
            return CryptoStrategyExecutor.executeSafely(fieldValue, AES, () -> aes.encryptHex(fieldValue));
        }

        @Override
        public String decrypt(CryptoWrapper cryptoWrapper) {
            String fieldValue = cryptoWrapper.getFieldValue();
            String[] arguments = cryptoWrapper.getArguments();

            String key = CryptoUtils.requireNonBlank(arguments[0], "AES key cannot be blank.");
            AES aes = SecureUtil.aes(key.getBytes());
            return CryptoStrategyExecutor.executeSafely(fieldValue, AES, () -> aes.decryptStr(fieldValue));
        }
    },

    DES {
        @Override
        public String encrypt(CryptoWrapper cryptoWrapper) {
            String fieldValue = cryptoWrapper.getFieldValue();
            String[] arguments = cryptoWrapper.getArguments();

            String key = CryptoUtils.requireNonBlank(arguments[0], "DES key cannot be blank.");
            DES des = SecureUtil.des(key.getBytes());
            return CryptoStrategyExecutor.executeSafely(fieldValue, DES, () -> des.encryptHex(fieldValue));
        }

        @Override
        public String decrypt(CryptoWrapper cryptoWrapper) {
            String fieldValue = cryptoWrapper.getFieldValue();
            String[] arguments = cryptoWrapper.getArguments();

            String key = CryptoUtils.requireNonBlank(arguments[0], "DES key cannot be blank.");
            DES des = SecureUtil.des(key.getBytes());
            return CryptoStrategyExecutor.executeSafely(fieldValue, DES, () -> des.decryptStr(fieldValue));
        }
    },

    RSA {
        @Override
        public String encrypt(CryptoWrapper cryptoWrapper) {
            String fieldValue = cryptoWrapper.getFieldValue();
            String[] arguments = cryptoWrapper.getArguments();

            String privateKey = CryptoUtils.requireNonBlank(arguments[0], "RSA privateKey cannot be blank.");
            String publicKey = CryptoUtils.requireNonBlank(arguments[1], "RSA publicKey cannot be blank.");
            RSA rsa = SecureUtil.rsa(privateKey, publicKey);
            return CryptoStrategyExecutor.executeSafely(fieldValue, RSA, () -> rsa.encryptHex(fieldValue, PublicKey));
        }

        @Override
        public String decrypt(CryptoWrapper cryptoWrapper) {
            String fieldValue = cryptoWrapper.getFieldValue();
            String[] arguments = cryptoWrapper.getArguments();

            String privateKey = CryptoUtils.requireNonBlank(arguments[0], "RSA privateKey cannot be blank.");
            String publicKey = CryptoUtils.requireNonBlank(arguments[1], "RSA publicKey cannot be blank.");
            RSA rsa = SecureUtil.rsa(privateKey, publicKey);
            return CryptoStrategyExecutor.executeSafely(fieldValue, RSA, () -> rsa.decryptStr(fieldValue, PrivateKey));
        }
    },

    BASE64 {
        @Override
        public String encrypt(CryptoWrapper cryptoWrapper) {
            String fieldValue = cryptoWrapper.getFieldValue();
            return CryptoStrategyExecutor.executeSafely(fieldValue, BASE64, () -> Base64.encode(fieldValue));
        }

        @Override
        public String decrypt(CryptoWrapper cryptoWrapper) {
            String fieldValue = cryptoWrapper.getFieldValue();
            return CryptoStrategyExecutor.executeSafely(fieldValue, BASE64, () -> Base64.decodeStr(fieldValue));
        }
    },

    SM4() {
        @Override
        public String encrypt(CryptoWrapper cryptoWrapper) {
            String fieldValue = cryptoWrapper.getFieldValue();
            String[] arguments = cryptoWrapper.getArguments();

            String key = CryptoUtils.requireNonBlank(arguments[0], "SM4 key cannot be blank.");
            SM4 sm4 = SmUtil.sm4(key.getBytes(StandardCharsets.UTF_8));
            return CryptoStrategyExecutor.executeSafely(fieldValue, SM4, () -> sm4.encryptHex(fieldValue));
        }

        @Override
        public String decrypt(CryptoWrapper cryptoWrapper) {
            String fieldValue = cryptoWrapper.getFieldValue();
            String[] arguments = cryptoWrapper.getArguments();

            String key = CryptoUtils.requireNonBlank(arguments[0], "SM4 key cannot be blank.");
            SM4 sm4 = SmUtil.sm4(key.getBytes(StandardCharsets.UTF_8));
            return CryptoStrategyExecutor.executeSafely(fieldValue, SM4, () -> sm4.decryptStr(fieldValue));
        }
    },

    CUSTOMIZE_HANDLER {
        @Override
        public String encrypt(CryptoWrapper cryptoWrapper) {
            String fieldName = cryptoWrapper.getFieldName();
            Object object = cryptoWrapper.getObject();

            Field field = ReflectUtil.getField(object.getClass(), fieldName);
            EncryptHandler cryptoHandler = field.getAnnotation(EncryptHandler.class);

            Class<? extends CustomizeCryptoHandler> handler = cryptoHandler.value();
            CustomizeCryptoHandler customizeCryptoHandler = ReflectUtil.newInstance(handler);
            return customizeCryptoHandler.customize(cryptoWrapper);
        }

        @Override
        public String decrypt(CryptoWrapper cryptoWrapper) {
            String fieldName = cryptoWrapper.getFieldName();
            Object object = cryptoWrapper.getObject();

            Field field = ReflectUtil.getField(object.getClass(), fieldName);
            DecryptHandler decryptHandler = field.getAnnotation(DecryptHandler.class);

            Class<? extends CustomizeCryptoHandler> handler = decryptHandler.value();
            CustomizeCryptoHandler customizeCryptoHandler = ReflectUtil.newInstance(handler);
            return customizeCryptoHandler.customize(cryptoWrapper);
        }
    };

    /**
     * Encrypt with {@link CryptoWrapper}.
     *
     * @param cryptoWrapper {@link CryptoWrapper}
     * @return after encrypt field value
     */
    public abstract String encrypt(CryptoWrapper cryptoWrapper);

    /**
     * Decrypt with {@link CryptoWrapper}.
     *
     * @param cryptoWrapper {@link CryptoWrapper}
     * @return after decrypt field value
     */
    public abstract String decrypt(CryptoWrapper cryptoWrapper);
}
