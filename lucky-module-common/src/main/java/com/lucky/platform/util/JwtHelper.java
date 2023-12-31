package com.lucky.platform.util;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * ClassName:    JwtHelper
 * Description:
 * Datetime:    2020/4/29   16:17
 * @author Nuany
 */
public class JwtHelper {
    private static final Logger log = LoggerFactory.getLogger(JwtHelper.class);
    //签名秘钥 自定义
    public static final String BASE64SECRET = "D4DwPLRlPyjoCibPnIEjXfIqOTFN9Iv4jNdWq4s9Mpc=";

    //用于JWT加密的密匙 自定义
    public static final String DATAKEY = "888888";

    public static final String USERID = "userId";
    public static final String USERNAME = "userName";
    public static final String USERAGENT = "userAgent";

    /**
     *  生成 Token
     * @param info
     * @param PrivateKey 私钥
     * @param expire  设置失效时间 秒
     * @return token
     * @throws Exception
     */
    public static String generateToken(JWTInfo info,String PrivateKey, int expire) throws Exception{
        //添加构成JWT的参数
        Map<String, Object> headMap = new HashMap<>(2);
        headMap.put("alg", SignatureAlgorithm.RS256.getValue());
        headMap.put("typ", "JWT");
        LocalDateTime now = LocalDateTime.now();
        JwtBuilder builder = Jwts.builder()
                .claim(USERID,info.getUserId())
                .claim(USERNAME,info.getUsername())
                .claim(USERAGENT,info.getName())
                .setHeader(headMap)
                .setId(createJTI())
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(now.plusMinutes(expire).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(RsaUtil.getPrivateKey(PrivateKey.getBytes()),SignatureAlgorithm.RS256);
        return builder.compact();
    }

    /**
     *  通过公钥解析 Token
     * @param token  Token
     * @param pubKey  公钥
     * @return  Claims
     * @throws Exception
     */
    private static Jws<Claims> parserToken(String token,String pubKey) throws  Exception{
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(RsaUtil.getPublicKey(pubKey.getBytes()))
                .build().parseClaimsJws(token);
                return claims;
    }

    /**
     * 获取Token中的用户信息
     * @param token
     * @param pubKey
     * @return
     * @throws Exception
     */
    public static String getToken(String token,String pubKey) throws  Exception{
        if(token.startsWith("Bearer")){
            token = token.replace("Bearer ","");
        }else {
            throw new RuntimeException("Token 格式不正确");
        }
        Jws<Claims> claimsJws = parserToken(token, pubKey);
        Claims body = claimsJws.getBody();
        return  body.get(USERID)+"";
    }



    /**
     * @Author: Helon
     * 格式：A.B.C
     * A-header头信息
     * B-payload 有效负荷
     * C-signature 签名信息 是将header和payload进行加密生成的
     * 生成JWT字符串
     * @param userId  - 用户编号
     * @param userName - 用户名
     * @param identities - 客户端信息（变长参数），目前包含浏览器信息，用于客户端拦截器校验，防止跨域非法访问
     * @return
     */
    private static String generateJWT(String userId, String userName ,int expire, String ...identities){
        //签名算法，选择SHA-256
        SignatureAlgorithm signature = SignatureAlgorithm.HS256;
        //获取当前系统时间
        LocalDateTime now = LocalDateTime.now();
        //将BASE64SECRET常量字符串使用base64解码成字节数组
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(BASE64SECRET);
        //使用HmacSHA256签名算法生成一个HS256的签名秘钥Key
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signature.getJcaName());
        String key = Base64.getEncoder().encodeToString(signingKey.getEncoded());
        log.info("生成一个HS256的签名秘钥"+key);
        //添加构成JWT的参数
        Map<String, Object> headMap = new HashMap<>(2);
        headMap.put("alg", SignatureAlgorithm.HS256.getValue());
        headMap.put("typ", "JWT");
        /**
         * 生成token
         * Jwts.builder() 返回了一个 JwtBuilder()
         * Header header; //头部
         * Claims claims; //声明
         * String payload; //载荷
         * SignatureAlgorithm algorithm; //签名算法
         * Key key; //签名key
         * byte[] keyBytes; //签名key的字节数组
         * CompressionCodec compressionCodec; //压缩算法
         *
         * iss setIssuer()  发行人
         * sub setSubject() 主题
         * aud setAudience() 接收方 用户
         * iat setExpiration() 到期时间,失效时间
         * exp setNotBefore() 在此之前不可用,生效时间
         * nbf setIssuedAt()  jwt的签发时间
         * jti：JWT setId() jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
         */
        JwtBuilder builder = Jwts.builder().setHeader(headMap)
                //加密后的客户编号
                .claim(USERID, AESSecretUtil.encryptToStr(userId, DATAKEY))
                //客户名称
                .claim(USERNAME, userName)
                //客户端浏览器信息
                .claim(USERAGENT, identities[0])
                .setId(createJTI())
                //Signature 进行签名 , 加密算法
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .setExpiration(Date.from(now.plusMinutes(expire).atZone(ZoneId.systemDefault()).toInstant()))
                .setNotBefore(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
        //生成JWT
        return builder.compact();
    }

    /**
     *  解析JWT
     * @param jsonWebToken
     * @return 返回Claims对象
     */
    private static Claims parseJWT(String jsonWebToken) {
        Claims claims = null;
        try {
            if (StringUtils.isNotBlank(jsonWebToken)) {
                claims = Jwts.parser().setSigningKey(DatatypeConverter
                        //签名秘钥
                        .parseBase64Binary(BASE64SECRET))
                        //解析jwt
                        .parseClaimsJws(jsonWebToken).getBody();
            }else {
                log.warn("[JWTHelper]-json web token 为空");
            }
        } catch (Exception e) {
            log.error("[JWTHelper]-JWT解析异常：可能因为token已经超时或非法token");
        }
        return claims;
    }

    /**
     * 校验jwt是否有效
     * @param jsonWebToken
     * @return
     */
    private static String validateLogin(String jsonWebToken) {
        Map<String, Object> retMap = null;
        Claims claims = parseJWT(jsonWebToken);
        if (claims != null) {
            //解密客户编号
            String decryptUserId = AESSecretUtil.decryptToStr((String)claims.get("userId"), DATAKEY);
            retMap = new HashMap<>();
            //加密后的客户编号
            retMap.put("userId", decryptUserId);
            //客户名称
            retMap.put("userName", claims.get("userName"));
            //客户端浏览器信息
            retMap.put("userAgent", claims.get("userAgent"));
            //刷新JWT
            retMap.put("freshToken", generateJWT(decryptUserId, (String)claims.get("userName"),10, (String)claims.get("userAgent"), (String)claims.get("domainName")));
        }else {
            log.warn("[JWTHelper]-JWT解析出claims为空");
        }
        return retMap!=null? JSONObject.toJSONString(retMap):null;
    }

    private static void signingKey(){
        SecretKey signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String key = Base64.getEncoder().encodeToString(signingKey.getEncoded());
        log.info("生成一个HS256的签名秘钥"+key);
        String token = Jwts.builder().setSubject("ce").signWith(signingKey).compact();
        log.info("token:{}"+token);
        String body = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token)
                .getBody().getSubject();
        log.info("body:{} " +body);
    }

    private static String createJTI() {
        return new String(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes()));
    }

    public static void main(String[] args) throws Exception {
        /*String user = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36";
        String admin = generateJWT("17", "admin", user);
        Claims claims = parseJWT(admin);
        System.out.println(AESSecretUtil.decryptToStr((String) claims.get("userId"), DATAKEY));*/
        String pub = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0fbzdKTjixnmHqfaPhSo253rjhd4sFqBdeu//bPUZpTvxKCpyYWXifqHgBcnbFRr3uYwT3FV6BET7tvbFG/P7vqrkULjJRzoaoNGOkdF/w5pUr4JTKQZBFIpyny3J5LftIkc+VZq0fL819QvZv2NQ+YzTDYLt5X9zMJOLbIeLsaZ2ZKso9fB8Dt8L1r1O4bHr37vWmiRgGdv9wy2p+Xw7z02eqoQpByCtnmxPndwqz55AK0qEZrLchswzHsDVsXeKWelaU2VEYavmNkNeK9RBxqFXa/Vti+cIEI1OgdQE0LgLk4h8J395XLapRPKyrkK8g3K4hZecFuqOpOgjmkzBwIDAQAB";
        String pri = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDR9vN0pOOLGeYep9o+FKjbneuOF3iwWoF167/9s9RmlO/EoKnJhZeJ+oeAFydsVGve5jBPcVXoERPu29sUb8/u+quRQuMlHOhqg0Y6R0X/DmlSvglMpBkEUinKfLcnkt+0iRz5VmrR8vzX1C9m/Y1D5jNMNgu3lf3Mwk4tsh4uxpnZkqyj18HwO3wvWvU7hsevfu9aaJGAZ2/3DLan5fDvPTZ6qhCkHIK2ebE+d3CrPnkArSoRmstyGzDMewNWxd4pZ6VpTZURhq+Y2Q14r1EHGoVdr9W2L5wgQjU6B1ATQuAuTiHwnf3lctqlE8rKuQryDcriFl5wW6o6k6COaTMHAgMBAAECggEAMdwuBjYd3uuAjSkZF4L6cmS3CmAiheaINcONLScmOlIF+jCZAUUbLmXzBYT6FKybjSwmrsbgOqLLk/5bsSe4Pi0CzxUOMcMCokCYTcCdK6t1cXO1a6DQXAlS6UXijKBBRXMGEWPnLAJC7tHxi8iMYmJpLiDreWZCBfziadGRf1O1tb3TtwKibYcrNTNsptrqBgb17QAHPT7PApl+9nk13IcfzIPv+rpYeqPxYzlqEx5QO/BSTWTJLySEC6qbP/l8hyI/4MQlD1eQw8VK/NlvQLxVX6BKemtdCaj7AOtq9Th/Poa0c1+ibqGACwFrftVFpXZU6Crm/+0SQ0AnNE2ZGQKBgQD8SliXuoXEtlU+4kY7/N6FAhvWeZRAorBSz1XkM7kmAwXRhlAvc/yprWZ0WNFrfmGmnNCXsVr7UXdlSaPG35xheekNHTrXhE5DxCJqZicv6wfFYZCGGQdGd1JDBfxg0G/RRyDuLoNnMIQ0pBHmuzqk8gf/cCOwI7qzwK1f6qJyKwKBgQDVDUkGzPS7JZMgKdHcGJ4DuhYQ5qc1QnuUbA1zfh+598OWC4hOy4Pr+AKj3eNBhNOvywnrH92JGckQ3+qdZDeCkGztnoh0S4c2Iozov8j0SFhUHHeTeM6pgF+CtjcejwKOWgKAEKoHj5O45VUEaX3UzobyxGDfLEWZtqPGBPZAlQKBgEKn1d5Ijn0Ze05A9DN3LMdRdk9lEdE96n4Gf3fqXnl3hPsFlHvlFhUyVgZZTQMsgdpGRio91n7d2ECvgxDRFXz/e55WYax9LTxkPn+D+8iCPSxeCZQUWsK4VciVdUeS6mgNddrX+SmzPMq3qm8eenEH7SsE0rj/x50kQ+o6kK6/AoGADIkLkurcO2aqkWnkfx6zrWR1h+kKTYfr3X++Ct2Q4FBDocBr9LbkvC2LejjD9kbqbZ10p/DoB/kMa4HBPCF5YqU8w35gaPdIrVa7E7a1DCp1h/1ky7h/8FktRXOTKqkfbWC+ZOqWFnvMJUTZC7lpPewo5H/Jc1gXgEPOvwfrtUECgYEA9GfeIxyy9y54OB+i8FoF45Y0lT82CtTtj8sF8WWA53Zr36WzF80H5YG2lGhR/jOxQ/O2R9uzBlmYvYpVda28LjIP+0yzKtUH1hOpF3EIBhOKZwY6dw6PTk7ZyoxDtkiIi0BuFihzDhYET7e5biq8HVKJ9xCvEdjzn820vtWjnvU=";
        JWTInfo info = new JWTInfo();
        info.setUserId("12");
        info.setUsername("张三");
        info.setName("zhangsan");
        String s = generateToken(info, pri, 10);
        s="Bearer "+s;
        System.out.println(s);
    }

}
