package promisor.promisor.domain.bandate.dto;

import com.sun.istack.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class ModifyStatusDto {
    @NotBlank(message = "날짜를 입력하세요.")
    String date;
    @NotBlank(message = "날짜상태를 입력하세요.")
    String status;
}
