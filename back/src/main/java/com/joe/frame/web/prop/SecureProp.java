package com.joe.frame.web.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 密匙
 * @author joe
 *
 */
@Data
@ConfigurationProperties(prefix = "com.joe.secure.key")
public class SecureProp {
	/*
	 * AES密钥
	 */
	private String aes = "joeAes123";
	/*
	 * RSA密钥（私钥）
	 */
	private String rsaPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALdgXgGcJf02sayrJWcuYbK274Yl6ck+a6wPE68C3p7mxubzbsKxaMF11VYngjq+yfWcKOfxKpDw2pSWTXdu1MYXInCzkFzRa9SoMhCYnmia9c/0JNybL62dJ3ntp1jUUHl2sZlqZD3F4SvCDj3oAQe+hT51hsDDYjJs2cIN9vtjAgMBAAECgYBwHY48Q8ECSN9mp5kNtdDr2GcnyKRgczaXkvxA42tDEewIJkvQ43Ed3zJZI3y++Ak4joqADPYueePaNu8UF4Iksu5esfUbJUIEXGfQNA2KFH4irzu+mmHNYl7juI1dPAusSyBDe5nNNlf/unZa6kr+xEtNJGReUsq8VYcUARQTaQJBAPvKJgxfAlC5b7/I4vZqNo8iVjDnq0wzmxe/SAcHd04BkhLAB3s14X6Ij3aczTv6Hih8Hz/RmJLOGbdv58VfM78CQQC6cVujqTQnW9UTktzJz5F3I+/zNGpKgN5ntZPhIsebuyZxzDqd8yYKrnIs9FKEq+UJB8pLu8wFdXrPSuhObpFdAkEAgS95kBVbIcHS5SqT0Gw0bAHpCRSEHgfIRkndEcYx3zMNGFOwQvYlKP+149yVrHUq7gipG9xVFNVr+mMPHoQ3DwJAdTJElIAMFKGZMxCiUQl1OxsP/iT4m5SeOTreliqypqXh6K6kOh1Z0GUPyEpchKE4+5A2DZ0jBltus1c25/aA0QJBAJWphC6PR7WznZcdKGdanl6bj8H8wit6ytoHWVYsg2k1DsKN8DZkv8SxVQZ7IJYqCH/bprRVvs7Z/LwE9Fg81us=";
	/*
	 * RSA公钥
	 */
	private String rsaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3YF4BnCX9NrGsqyVnLmGytu+GJenJPmusDxOvAt6e5sbm827CsWjBddVWJ4I6vsn1nCjn8SqQ8NqUlk13btTGFyJws5Bc0WvUqDIQmJ5omvXP9CTcmy+tnSd57adY1FB5drGZamQ9xeErwg496AEHvoU+dYbAw2IybNnCDfb7YwIDAQAB";
}
