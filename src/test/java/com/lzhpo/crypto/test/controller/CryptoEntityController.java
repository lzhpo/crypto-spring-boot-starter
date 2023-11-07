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

package com.lzhpo.crypto.test.controller;

import com.lzhpo.crypto.annocation.IgnoreCrypto;
import com.lzhpo.crypto.test.entity.CryptoEntity;
import com.lzhpo.crypto.test.entity.NestedCryptoEntity;
import com.lzhpo.crypto.test.entity.NoCryptoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzhpo
 */
@Slf4j
@RestController
@RequestMapping("/crypto")
public class CryptoEntityController {

    @PostMapping("/single")
    public ResponseEntity<CryptoEntity> singleCrypto(@RequestBody CryptoEntity cryptoEntity) {
        log.debug("cryptoEntity: {}", cryptoEntity);
        return ResponseEntity.ok(cryptoEntity);
    }

    @PostMapping("/nested")
    public ResponseEntity<NestedCryptoEntity> nestedCrypto(@RequestBody NestedCryptoEntity nestedCryptoEntity) {
        log.debug("nestedCryptoEntity: {}", nestedCryptoEntity);
        return ResponseEntity.ok(nestedCryptoEntity);
    }

    @PostMapping("/nothing")
    public ResponseEntity<NoCryptoEntity> noCrypto(@RequestBody NoCryptoEntity noCryptoEntity) {
        log.debug("noCryptoEntity: {}", noCryptoEntity);
        return ResponseEntity.ok(noCryptoEntity);
    }

    @IgnoreCrypto
    @PostMapping("/ignore")
    public ResponseEntity<CryptoEntity> ignoreCrypto(@RequestBody CryptoEntity cryptoEntity) {
        log.debug("cryptoEntity: {}", cryptoEntity);
        return ResponseEntity.ok(cryptoEntity);
    }

    @PostMapping("/ignore-fields")
    @IgnoreCrypto({"name1", "name2", "mobilePhone1", "mobilePhone2", "description1", "description2"})
    public ResponseEntity<CryptoEntity> ignoreFieldsCrypto(@RequestBody CryptoEntity cryptoEntity) {
        log.debug("cryptoEntity: {}", cryptoEntity);
        return ResponseEntity.ok(cryptoEntity);
    }
}
