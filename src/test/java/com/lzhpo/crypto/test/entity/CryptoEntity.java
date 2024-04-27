/*
 * Copyright 2022 lzhpo
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

package com.lzhpo.crypto.test.entity;

import com.lzhpo.crypto.annocation.Decrypt;
import com.lzhpo.crypto.annocation.DecryptHandler;
import com.lzhpo.crypto.annocation.Encrypt;
import com.lzhpo.crypto.annocation.EncryptHandler;
import com.lzhpo.crypto.strategy.CryptoStrategy;
import com.lzhpo.crypto.test.handler.FaceCustomizeDecryptHandler;
import com.lzhpo.crypto.test.handler.FaceCustomizeEncryptHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lzhpo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoEntity {

    @Encrypt(strategy = CryptoStrategy.BASE64)
    private String name1;

    @Decrypt(strategy = CryptoStrategy.BASE64)
    private String name2;

    @Encrypt(strategy = CryptoStrategy.SM4, arguments = "${custom.sm4.key}")
    private String name3;

    @Decrypt(strategy = CryptoStrategy.SM4, arguments = "${custom.sm.key:1234567812345678}")
    private String name4;

    @Encrypt(strategy = CryptoStrategy.AES)
    private String address1;

    @Decrypt(strategy = CryptoStrategy.AES)
    private String address2;

    @Encrypt(strategy = CryptoStrategy.DES)
    private String address3;

    @Decrypt(strategy = CryptoStrategy.DES)
    private String address4;

    @Encrypt(strategy = CryptoStrategy.RSA)
    private String mobilePhone1;

    @Decrypt(strategy = CryptoStrategy.RSA)
    private String mobilePhone2;

    @Encrypt(strategy = CryptoStrategy.SM4)
    private String mobilePhone3;

    @Decrypt(strategy = CryptoStrategy.SM4)
    private String mobilePhone4;

    @EncryptHandler(FaceCustomizeEncryptHandler.class)
    @Encrypt(strategy = CryptoStrategy.CUSTOMIZE_HANDLER)
    private String description1;

    @DecryptHandler(FaceCustomizeDecryptHandler.class)
    @Decrypt(strategy = CryptoStrategy.CUSTOMIZE_HANDLER)
    private String description2;
}
