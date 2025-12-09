package kr.co.board.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Size(min = 5, max = 15, message = "username은 5자 이상 15자 이하이어야 합니다.")
@Pattern(regexp = "^[A-Za-z0-9]*$", message = "username은 영문자와 숫자만 사용할 수 있습니다.")
public @interface ValidUsername {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
