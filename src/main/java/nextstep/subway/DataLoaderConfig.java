package nextstep.subway;

import com.google.common.collect.Lists;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.member.domain.Member;
import nextstep.subway.member.domain.MemberRepository;
import nextstep.subway.station.domain.Station;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DataLoaderConfig implements CommandLineRunner {
    private LineRepository lineRepository;
    private MemberRepository memberRepository;

    public DataLoaderConfig(LineRepository lineRepository, MemberRepository memberRepository) {
        this.lineRepository = lineRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Station 강남역 = Station.from("강남역");
        Station 교대역 = Station.from("교대역");
        Station 양재역 = Station.from("양재역");
        Station 남부터미널역 = Station.from("남부터미널역");
        Distance 거리 = new Distance(10);

        Line 신분당선 = new Line("신분당선", "red lighten-1", 강남역, 양재역, 거리);
        Line 이호선 = new Line("2호선", "green lighten-1", 교대역, 강남역, 거리);
        Line 삼호선 = new Line("3호선", "orange darken-1", 교대역, 양재역, 거리);

        lineRepository.saveAll(Lists.newArrayList(신분당선, 이호선, 삼호선));

        memberRepository.save(new Member("probitanima11@gmail.com", "11", 10));
    }
}