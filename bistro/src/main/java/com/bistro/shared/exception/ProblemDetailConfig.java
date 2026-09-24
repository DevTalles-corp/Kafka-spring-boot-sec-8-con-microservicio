package com.bistro.shared.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProblemDetailConfig {

    public static final URI DEFAULT_TYPE = URI.create("about:blank");

    public static ProblemDetail validationProblem(List<Map<String, String>> errors) {
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setType(DEFAULT_TYPE);
        problem.setTitle("Solicitud inválida");
        problem.setDetail("Los datos de la solicitud no son válidos. Revise los errores indicados.");
        problem.setProperty("errors", errors);
        return problem;
    }

    public static ProblemDetail notFoundProblem(String detail) {
        ProblemDetail problem = ProblemDetail.forStatus(404);
        problem.setType(DEFAULT_TYPE);
        problem.setTitle("Recurso no encontrado");
        problem.setDetail(detail);
        return problem;
    }

    public static ProblemDetail genericProblem(int status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setType(DEFAULT_TYPE);
        problem.setTitle(title);
        problem.setDetail(detail);
        return problem;
    }
}
