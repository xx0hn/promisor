package promisor.promisor.domain.calender.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetTeamCalendarStatusResponse {

    private final LocalDate date;
    private final String dateStatus;

    public GetTeamCalendarStatusResponse(LocalDate date, String dateStatus) {
        this.date = date;
        this.dateStatus = dateStatus;
    }
}
