![](https://img.shields.io/badge/JDK-1.8+-success.svg)
![](https://maven-badges.herokuapp.com/maven-central/com.lzhpo/crypto-spring-boot-starter/badge.svg?color=blueviolet)
![](https://img.shields.io/:license-Apache2-orange.svg)
[![Style check](https://github.com/lzhpo/crypto-spring-boot-starter/actions/workflows/style-check.yml/badge.svg)](https://github.com/lzhpo/crypto-spring-boot-starter/actions/workflows/style-check.yml)

## 开源地址

- GitHub：[https://github.com/lzhpo/crypto-spring-boot-starter](https://github.com/lzhpo/crypto-spring-boot-starter)
- Gitee：[https://gitee.com/lzhpo/crypto-spring-boot-starter](https://gitee.com/lzhpo/crypto-spring-boot-starter)

## 如何使用？

*crypto-spring-boot-starter也支持SpringBoot3*

> 3.0.0及以上版本的crypto-spring-boot-starter只针对使用SpringBoot3用户，SpringBoot2用户请使用低于3.0.0版本的crypto-spring-boot-starter，两者功能不受影响，均会同步更新！

### 1.导入依赖

> 依赖已发布至Maven中央仓库，可直接引入依赖。

- Maven：
  ```xml
  <dependency>
    <groupId>com.lzhpo</groupId>
    <artifactId>crypto-spring-boot-starter</artifactId>
    <version>${latest-version}</version>
  </dependency>
  ```
- Gradle:
  ```groovy
  implementation 'com.lzhpo:crypto-spring-boot-starter:${latest-version}'
  ```

### 2.实体类字段上使用 `@Encrypt` 或 `@Decrypt` 注解配置加密解密策略

1. 支持多种加密解密策略，例如：BASE64、AES、DES、RSA、国密SM4...<br>
    ```java
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
    ```
2. 支持自定义加密解密策略。实现 `CustomizeCryptoHandler` 接口（加密解密都是此接口），使用 `@EncryptHandler` 或 `@DecryptHandler` 注解指向实现的策略即可。
    ```java
    @EncryptHandler(FaceCustomizeEncryptHandler.class)
    @Encrypt(strategy = CryptoStrategy.CUSTOMIZE_HANDLER)
    private String description1;

    @DecryptHandler(FaceCustomizeDecryptHandler.class)
    @Decrypt(strategy = CryptoStrategy.CUSTOMIZE_HANDLER)
    private String description2;
    ```
3. `@Encrypt` 或 `@Decrypt` 注解中的 `arguments` 支持从环境变量中读取以及设置默认值，同时也方便了自定义加密解密策略的时候灵活配置。
    ```java
    @Encrypt(strategy = CryptoStrategy.AES, arguments = {"${crypto.aes.key}"})
    private String address1;

    @Decrypt(strategy = CryptoStrategy.AES, arguments = {"${crypto.aes.key:1234567890123456}"})
    private String address2;
    ```
   同时，参数支持在 IDEA 中自动高亮显示。
   ![](docs/images/SpEL-IDEA.png)
4. 支持在配置文件中单独对加解密策略设置不同的参数以及回退策略，需要注意的是 `@Encrypt` 和 `@Decrypt` 注解中配置的 `arguments` 优先级大于此处的配置。
    ```yml
    crypto:
      default-fallback-value: "N/A"
      strategy:
        aes:
          fallback-value: empty_string
          arguments:
            # The arguments[0] is AES key
            - "1234567812345678"
        des:
          fallback-value: null_value
          arguments:
            # The arguments[0] is DES key
            - "12345678"
        sm4:
          fallback-value: original_value
          arguments:
            # The arguments[0] is SM4 key
            - "1234567812345678"
        rsa:
          fallback-value: empty_string
          arguments:
            # The arguments[0] is RSA private key
            - "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANyhI/9e5evQyzRLVsUmbQesdRl7fXu9ZAl6lZqVyL+ypf6FmQouH89OTEv/JjmybuAha9zsYwNAKSobSRATaqYSCvdwoPRgUFRFfq6ed61kpO5+D+T/X3v85JmXIngkieCe9n5b5KT3XNtHFBXVsZ3/onWEYZRhFMTsMsKkvijBAgMBAAECgYAKV2fEbC5vAp0JvRfKuym8ZLgi6wPHWWnfW154jdmIab9n2huBq4aMbSU8oS+pn+xcR1jC1NYxxG/BhGCk9yIGIzE/57tggjibNpiqC/uS12SiaJPz9oqOVJPI+l5uf9xqdytzvNJe6AGMViZdS+nnQRZfdDrs5cgghv7lx+kjiQJBAOQWmEJukHaIUXvW8ZWNekIgb8/Frq7gNvRaeqjqpZMqUIXXDj80eODGsNjIUwwEdlFX4//C7udmLfWfhyOq1bkCQQD3oOGP8rjIkouhbJldaILeuaN3ee3v3dtsmLM8epC9HH3EcFBD2O+l60wCa67uM/ArPn3XjL/lidqnVAJHPG9JAkEAumz1WicAkMFuyGew4enXKcFVYl9THcBJaoOhifrwBk8prZtPG74Jpr7/wNBLgKENDANoaZ2soxnTKtWPIUn6kQJAAmcxSTBV0rx5VmuzYVCuVHMAvxwTzwwcIQWqV5/o36zzG4Drhn0Idle+ORfKbs1aO1Ez72+SPSwFTzJlg0N24QJATQu2dlhbm87uGh0fUHpV6Nw6lf/mBMek1stC8PQXB0MtNPeYd+Ul45zfc+k5mIWUHwt47To5uAo2ywsCSdWBCw=="
            # The arguments[1] RSA public key
            - "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDcoSP/XuXr0Ms0S1bFJm0HrHUZe317vWQJepWalci/sqX+hZkKLh/PTkxL/yY5sm7gIWvc7GMDQCkqG0kQE2qmEgr3cKD0YFBURX6unnetZKTufg/k/197/OSZlyJ4JIngnvZ+W+Sk91zbRxQV1bGd/6J1hGGUYRTE7DLCpL4owQIDAQAB"
    ```
   配置参数解释：
   - `fallback-value`：当加解密出现异常的时候需要做的事情。
     - original_value：返回原始值。
     - null_value：返回 null 值。
     - empty_string：返回空字符串。
     - throw_exception：直接抛出异常。
   - `default-fallback-value`：当加密策略没有配置 `fallback-value` 的时候，将会直接返回此处配置的值。
   - `arguments`：一般来说统一在yml中配置加解密的key就可以了，如果有字段需要单独使用其它key进行加解密，可以在此配置。
    ```java
    @Encrypt(strategy = CryptoStrategy.SM4, arguments = "${custom.sm4.key}")
    private String name3;
    
    @Decrypt(strategy = CryptoStrategy.SM4, arguments = "${custom.sm.key:1234567812345678}")
    private String name4;
    ```

### 3. 支持在 controller 使用 `@IgnoreCrypto` 注解忽略加密解密，同时支持忽略指定的字段

```java
@IgnoreCrypto
@PostMapping("/ignore1")
public ResponseEntity<CryptoEntity> ignore1(@RequestBody CryptoEntity cryptoEntity) {
    return ResponseEntity.ok(cryptoEntity);
}

@PostMapping("/ignore2")
@IgnoreCrypto({"name2", "mobilePhone2", "description2"})
public ResponseEntity<CryptoEntity> ignore2(@RequestBody CryptoEntity cryptoEntity) {
    return ResponseEntity.ok(cryptoEntity);
}
```

### 4. 将默认的 Jackson 切换为 FastJson（不推荐）

1. 加入 FastJson 依赖（支持 FastJson1 和 FastJson2）：
    ```xml
    <dependency>
      <groupId>com.alibaba</groupId>
      <artifactId>fastjson</artifactId>
      <version>1.x.x/2.x.x</version>
    </dependency>
    ```
2. 将 `FastJsonHttpMessageConverter` 声明为 Bean 即可，`sensitive-spring-boot-starter` 会自动注入相关逻辑。
    ```java
    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
      return new FastJsonHttpMessageConverter();
    }
    ```

## 微信公众号

<img src="./docs/images/WeChat-MP.png" width="453" height="150" alt="会打篮球的程序猿">
