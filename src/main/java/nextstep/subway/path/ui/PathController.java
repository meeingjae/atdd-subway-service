package nextstep.subway.path.ui;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "paths")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> getPath(
            @RequestParam("source") long sourceId,
            @RequestParam("target") long targetId) {
        return ResponseEntity.ok(pathService.getShortestPath(sourceId, targetId));
    }

    @ExceptionHandler({ IllegalArgumentException.class })
    public ResponseEntity handleIllegalArgsException(Exception e) {
        return ResponseEntity.badRequest().build();
    }
}
