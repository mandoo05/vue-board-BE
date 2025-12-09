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
@Size(min = 8, max = 15, message = "비밀번호는 8~15자여야 합니다.")
@Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#%^&*]).*$",
        message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.")
public @interface ValidPassword {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
