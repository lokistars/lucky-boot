package com.lucky.platform.util;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
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

    //超时毫秒数（默认30分钟）
    public static final int EXPIRESSECOND = 1800000;

    //用于JWT加密的密匙 自定义
    public static final String DATAKEY = "888888";

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
    public static String generateJWT(String userId, String userName, String ...identities){
        //签名算法，选择SHA-256
        SignatureAlgorithm signature = SignatureAlgorithm.HS256;
        //获取当前系统时间
        long nowTimeMillis = System.currentTimeMillis();
        Date now = new Date(nowTimeMillis);
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
         * iat setExpiration() 到期时间
         * exp setNotBefore() 在此之前不可用
         * nbf setIssuedAt()  jwt的签发时间
         * jti：JWT setId() jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
         */
        JwtBuilder builder = Jwts.builder().setHeader(headMap)
                //加密后的客户编号
                .claim("userId", AESSecretUtil.encryptToStr(userId, DATAKEY))
                //客户名称
                .claim("userName", userName)
                //客户端浏览器信息
                .claim("userAgent", identities[0])
                .setId(createJTI())
                //Signature
                .signWith(signingKey,SignatureAlgorithm.HS256);
        //添加Token过期时间
        if (EXPIRESSECOND>-0){
            long expMillis = nowTimeMillis + EXPIRESSECOND;
            Date expDate = new Date(expMillis);
            builder.setExpiration(expDate).setNotBefore(now);
        }
        //生成JWT
        return builder.compact();
    }

    /**
     *  解析JWT
     * @param jsonWebToken
     * @return 返回Claims对象
     */
    public static Claims parseJWT(String jsonWebToken) {
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
    public static String validateLogin(String jsonWebToken) {
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
            retMap.put("freshToken", generateJWT(decryptUserId, (String)claims.get("userName"), (String)claims.get("userAgent"), (String)claims.get("domainName")));
        }else {
            log.warn("[JWTHelper]-JWT解析出claims为空");
        }
        return retMap!=null? JSONObject.toJSONString(retMap):null;
    }
    public void signingKey(){
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
        String user = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36";
        String admin = generateJWT("17", "admin", user);
        Claims claims = parseJWT(admin);
        System.out.println(AESSecretUtil.decryptToStr((String) claims.get("userId"), DATAKEY));


    }

}
