package nextstep.subway.favorite.application;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.acceptance.LineAcceptanceTest;
import nextstep.subway.line.acceptance.LineSectionAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nextstep.subway.auth.acceptance.AuthAcceptanceTest.로그인_됨;
import static nextstep.subway.auth.acceptance.AuthAcceptanceTest.로그인_요청;
import static nextstep.subway.favorite.acceptance.FavoriteAcceptanceTest.*;
import static nextstep.subway.member.MemberAcceptanceTest.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FavoriteServiceTest  extends AcceptanceTest {

    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;

    private String accessToken;

    @BeforeEach
    void init() {
        //지하철역 등록되어 있음
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = StationAcceptanceTest.지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = StationAcceptanceTest.지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

        //지하철 노선 등록되어 있음
        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음(new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10)).as(LineResponse.class);
        이호선 = LineAcceptanceTest.지하철_노선_등록되어_있음(new LineRequest("이호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 7)).as(LineResponse.class);
        삼호선 = LineAcceptanceTest.지하철_노선_등록되어_있음(new LineRequest("삼호선", "bg-red-600", 교대역.getId(), 양재역.getId(), 5)).as(LineResponse.class);
        //지하철 노선에 지하철역 등록되어 있음
        LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청(삼호선, 교대역, 남부터미널역, 3);

        //회원 등록되어 있음
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        회원_생성됨(createResponse);

        //로그인 되어있음
        ExtractableResponse<Response> loginResponse = 로그인_요청(EMAIL, PASSWORD);
        로그인_됨(loginResponse);

        accessToken = loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    void 즐겨찾기_생성() {
        //when
        ExtractableResponse<Response> createFavorite = 즐겨찾기_생성_요청(accessToken, 교대역.getId(), 양재역.getId());
        //then
        즐겨찾기_생성_됨(createFavorite);
    }

    @Test
    void 출발역과_도착역이_같은_경우_즐겨찾기_생성시_에러() {
        //when
        ExtractableResponse<Response> createFavorite = 즐겨찾기_생성_요청(accessToken, 교대역.getId(), 교대역.getId());
        //then
        즐겨찾기_등록_400_실패(createFavorite);
    }

    @Test
    void 유효하지_않은_토큰으로_즐겨찾기_생성시_에러() {
        //given
        String wrongAccessToken = "wrongToken";
        //when
        ExtractableResponse<Response> createFavorite = 즐겨찾기_생성_요청(wrongAccessToken, 교대역.getId(), 양재역.getId());
        //then
        즐겨찾기_등록_401_실패(createFavorite);
    }

    @Test
    void 존재하지_않은_역_즐겨찾기_생성시_에러() {
        //when
        ExtractableResponse<Response> createFavorite = 즐겨찾기_생성_요청(accessToken, 교대역.getId(), 1234L);

        //then
        즐겨찾기_등록_404_실패(createFavorite);
    }

}
