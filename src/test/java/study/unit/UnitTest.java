package study.unit;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("단위 테스트")
class UnitTest {
    @Test
    void update() {
        // given
        String newName = "구분당선";
        String newColor = "GREEN";

        Station upStation = Station.from("강남역");
        Station downStation = Station.from("광교역");
        Line line = new Line("신분당선", "RED", upStation, downStation, new Distance(10));

        // when
        line.update(newName, newColor);

        // then
        assertThat(line.getName()).isEqualTo(newName);
    }
}
