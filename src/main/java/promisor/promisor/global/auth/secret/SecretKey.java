package promisor.promisor.global.auth.secret;

import lombok.Getter;
/**
 * KEY를 가져다 줄 클래스 SecretKey
 *
 * @author Sanha Ko
 */
@Getter
public class SecretKey {

    private final String kakaoApiKey;
    private final String jwtSecretKey;
    private final Long jwtValidityTime;
    private final Long refreshValidityTime;

    public SecretKey(String kakaoApiKey, String jwtSecretKey, Long jwtValidityTime, Long refreshValidityTime) {
        this.kakaoApiKey = kakaoApiKey;
        this.jwtSecretKey = jwtSecretKey;
        this.jwtValidityTime = jwtValidityTime;
        this.refreshValidityTime = refreshValidityTime;
    }
}
