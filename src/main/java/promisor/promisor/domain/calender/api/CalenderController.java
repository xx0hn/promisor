package promisor.promisor.domain.calender.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import promisor.promisor.domain.calender.dto.*;
import promisor.promisor.domain.calender.exception.DateEmptyException;
import promisor.promisor.domain.calender.service.CalenderService;
import promisor.promisor.global.auth.JwtAuth;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/calender")
@RequiredArgsConstructor
public class CalenderController {
    private final CalenderService calenderService;

    @Operation(summary = "register personal calender", description = "개인 일정 등록")
    @PostMapping("/personal")
    public ResponseEntity<RegisterPersonalCalenderResponse> registerPersonal(@JwtAuth String email,
                                                                             @RequestBody RegisterDateDto registerDateDto){
        RegisterPersonalCalenderResponse response = calenderService.registerPersonal(email, registerDateDto.getDate(),
                registerDateDto.getStatus());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "modify personal calender", description = "개인 캘린더 상태 변경")
    @PatchMapping
    public ResponseEntity<ModifyStatusResponse> modifyStatus(@JwtAuth String email,
                                                                          @RequestBody @Valid ModifyStatusDto request) {
        ModifyStatusResponse response = calenderService.modifyStatus(email, request.getDate(), request.getStatus());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "get team calendar detail", description = "팀 캘린더 날짜 별 세부 조회")
    @GetMapping("/team/{id}/detail/{date}")
    public ResponseEntity<List<GetTeamCalendarResponse>> getTeamCalendar(@JwtAuth String email,
                                                                         @PathVariable("id") Long teamId,
                                                                         @PathVariable("date") String date) {
        List<GetTeamCalendarResponse> response = calenderService.getTeamCalendarDetail(email, teamId, date);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "register personal schedule", description = "사적인 이유 등록")
    @PostMapping("/personal/reason")
    public ResponseEntity<RegisterPersonalScheduleResponse> registerPersonalSchedule(@JwtAuth String email,
                                                                                     @RequestBody @Valid RegisterReasonDto registerReasonDto){
        RegisterPersonalScheduleResponse response = calenderService.registerPersonalSchedule(email, registerReasonDto.getDate(),
                registerReasonDto.getReason());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "get personal calendar status", description = "개인 캘린더 일정 조회")
    @GetMapping("/personal")
    public ResponseEntity<List<GetPersonalCalendarResponse>> getPersonalCalendar(@JwtAuth String email) {
        List<GetPersonalCalendarResponse> response = calenderService.getPersonalCalendar(email);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "get personal calendar schedule", description = "개인 캘린더 일정에 대한 사유 조회")
    @GetMapping("/personal/reason/{date}")
    public ResponseEntity<GetPersonalScheduleResponse> getPersonalSchedule(@JwtAuth String email,
                                                                           @PathVariable("date") String date) {
        if (date == null){throw new DateEmptyException();}
        GetPersonalScheduleResponse response = calenderService.getPersonalSchedule(email, date);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "get team calendar status", description = "팀 캘린더의 전체 상태 조회")
    @GetMapping("/team/{id}/{yearMonth}")
    public ResponseEntity<List<GetTeamCalendarStatusResponse>> getTeamCalendarStatus(@JwtAuth String email,
                                                                               @PathVariable("id") Long id,
                                                                               @PathVariable("yearMonth") String yearMonth) {
        List<GetTeamCalendarStatusResponse> response = calenderService.getTeamCalendarStatus(email, id, yearMonth);
        return ResponseEntity.ok().body(response);
    }
}
