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

1. 支持多种加密解密策略，例如：BASE64、AES、DES、RSA...
   _`@Encrypt` 或 `@Decrypt` 注解中的 `arguments` 支持从环境变量中读取以及设置默认值，同时也方便了自定义加密解密策略的时候灵活配置。_
    ```java
    @Encrypt(strategy = CryptoStrategy.BASE64)
    private String name1;

    @Decrypt(strategy = CryptoStrategy.BASE64)
    private String name2;

    @Encrypt(strategy = CryptoStrategy.AES, arguments = {"${crypto.aes.key}"})
    private String address1;

    @Decrypt(strategy = CryptoStrategy.AES, arguments = {"${crypto.aes.key:1234567890123456}"})
    private String address2;

    @Encrypt(strategy = CryptoStrategy.DES, arguments = {"${crypto.des.key}"})
    private String address3;

    @Decrypt(strategy = CryptoStrategy.DES, arguments = {"${crypto.des.key:12345678}"})
    private String address4;

    @Encrypt(strategy = CryptoStrategy.RSA, arguments = {"${crypto.rsa.private-key}", "${crypto.rsa.public-key}"})
    private String mobilePhone1;

    @Decrypt(strategy = CryptoStrategy.RSA, arguments = {"${crypto.rsa.private-key}", "${crypto.rsa.public-key}"})
    private String mobilePhone2;
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

## 公众号

|         微信          |            公众号             |
|:-------------------:|:--------------------------:|
| ![](./docs/images/微信.jpg) | ![](./docs/images/公众号.jpg) |
