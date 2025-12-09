package kr.co.board.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

    /** 기본 OpenAPI 설정 */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("/"));
    }

    /** PreAuthorize 기반 보안 표시 */
    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .packagesToScan("kr.co.board")   // ⬅ 여기가 가장 중요!
                .pathsToExclude("/docs/error/**")
                .addOperationCustomizer((operation, handlerMethod) -> {

                    // @PreAuthorize 분석
                    PreAuthorize methodAuth = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), PreAuthorize.class);
                    PreAuthorize classAuth = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), PreAuthorize.class);

                    // 보안 표시
                    if (isSecured(methodAuth) || isSecured(classAuth)) {
                        operation.addSecurityItem(
                                new io.swagger.v3.oas.models.security.SecurityRequirement()
                                        .addList("bearerAuth")
                        );
                    }

                    return operation;
                })
                .build();
    }

    /** 인증이 필요한지 체크 */
    private boolean isSecured(PreAuthorize preAuthorize) {
        if (preAuthorize == null) return false;

        String value = preAuthorize.value().trim();

        if (value.contains("permitAll") || value.contains("anonymous")) return false;

        return value.contains("isAuthenticated()")
                || value.contains("hasRole")
                || value.contains("hasAnyRole");
    }
}
