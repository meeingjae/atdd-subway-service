package nextstep.subway.auth.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.auth.dto.TokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.member.MemberAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AuthAcceptanceTest extends AcceptanceTest {

    /**
     * Feature: 로그인 기능
     *
     *   Scenario: 로그인을 시도한다.
     *     Given 회원 등록되어 있음
     *     When 로그인 요청
     *     Then 로그인 됨
     */
    @DisplayName("Bearer Auth")
    @Test
    void myInfoWithBearerAuth() {
        //Given
        회원_생성을_요청(EMAIL, PASSWORD, AGE);

        //When
        ExtractableResponse<Response> response = 로그인_요청(EMAIL, PASSWORD);

        //Then
        로그인_성공함(response);
    }

    /**
     * Feature: 로그인 기능
     *
     *   Scenario: 로그인을 시도한다.
     *     Given 회원 등록되어 있음
     *     When 잘못된 로그인 요청
     *     Then 로그인 실패
     */
    @DisplayName("Bearer Auth 로그인 실패")
    @Test
    void myInfoWithBadBearerAuth() {
        //Given
        회원_생성을_요청(EMAIL, PASSWORD, AGE);

        //When
        ExtractableResponse<Response> response = 로그인_요청(EMAIL, "WRONG_PASSWORD");

        //Then
        로그인_실패함(response);
    }

    /**
     * Feature: 회원정보 조회 기능
     *
     *   Scenario: 로그인을 시도한다.
     *     Given 회원 등록되어 있음
     *     When 유효한 토큰 사용하여 회원정보 조회
     *     Then 회원정보 조회 성공
     */
    @DisplayName("Bearer Auth 유효한 토큰")
    @Test
    void myInfoWithValidBearerAuth() {
        //Given
        회원_생성을_요청(EMAIL, PASSWORD, AGE);
        TokenResponse tokenResponse = 로그인_요청(EMAIL, PASSWORD).as(TokenResponse.class);

        //When
        ExtractableResponse<Response> response = 회원정보_조회_요청(tokenResponse.getAccessToken());

        //Then
        회원정보_조회_성공(response);
    }

    /**
     * Feature: 회원정보 조회 기능
     *
     *   Scenario: 로그인을 시도한다.
     *     Given 회원 등록되어 있음
     *     When 유효하지 않은 토큰 사용하여 회원정보 조회
     *     Then 회원정보 조회 실패
     */
    @DisplayName("Bearer Auth 유효하지 않은 토큰")
    @Test
    void myInfoWithWrongBearerAuth() {
        //Given
        회원_생성을_요청(EMAIL, PASSWORD, AGE);
        TokenResponse tokenResponse = 로그인_요청(EMAIL, PASSWORD).as(TokenResponse.class);

        //When
        ExtractableResponse<Response> response = 회원정보_조회_요청("WRONG_TOKEN");

        //Then
        회원정보_조회_실패(response);
    }

    private ExtractableResponse<Response> 로그인_요청(String email, String password) {
        TokenRequest tokenRequest = new TokenRequest(email, password);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tokenRequest)
                .when().post("/login/token")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 회원정보_조회_요청(String accessToken) {
        return RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/members/me")
                .then().log().all()
                .extract();
    }

    private void 로그인_성공함(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.as(TokenResponse.class).getAccessToken()).isNotBlank()
        );
    }

    private void 로그인_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private void 회원정보_조회_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 회원정보_조회_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
