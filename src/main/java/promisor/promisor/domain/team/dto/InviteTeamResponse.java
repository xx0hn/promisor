package promisor.promisor.domain.team.dto;

import lombok.Getter;

@Getter
public class InviteTeamResponse {
    private final Long memberId;
    private final Long groupId;

    public InviteTeamResponse(Long memberId, Long groupId) {
        this.memberId = memberId;
        this.groupId = groupId;
    }
}