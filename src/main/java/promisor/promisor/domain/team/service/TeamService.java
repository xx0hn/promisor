package promisor.promisor.domain.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import promisor.promisor.domain.member.dao.MemberRepository;
import promisor.promisor.domain.member.domain.Member;
import promisor.promisor.domain.member.exception.MemberEmailNotFound;
import promisor.promisor.domain.member.exception.MemberNotFoundException;
import promisor.promisor.domain.promise.exception.MemberNotBelongsToTeam;
import promisor.promisor.domain.team.dao.InviteRepository;
import promisor.promisor.domain.team.dao.TeamMemberRepository;
import promisor.promisor.domain.team.dao.TeamRepository;
import promisor.promisor.domain.team.domain.Invite;
import promisor.promisor.domain.team.domain.Team;
import promisor.promisor.domain.team.domain.TeamMember;
import promisor.promisor.domain.team.dto.*;
import promisor.promisor.domain.team.exception.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InviteRepository inviteRepository;

    /*
     *   그룹 생성 API
     *   @Param: 생성자 이메일, 그룹 이름
     *   @author: Sanha Ko
     */
    @Transactional
    public Long createGroup(String email, CreateTeamDto request) {

        Member member =getMemberInfo(email);
        Team team = teamRepository.save(new Team(member, request.getGroupName()));
        teamMemberRepository.save(new TeamMember(member, team));
        return team.getId();
    }

    private Member getMemberInfo(String email){
        return memberRepository.findByEmail(email).orElseThrow(MemberEmailNotFound::new);
    }

    private Team getGroup(Long id) {
        return teamRepository.findById(id).orElseThrow(TeamIdNotFoundException::new);
    }

    @Transactional
    public ChangeTeamNameResponse editGroup(String email, EditTeamDto request) {

        Member member = getMemberInfo(email);
        Team team = getGroup(request.getGroupId());
        Member leader = team.getMember();
        if (leader.getId() != member.getId()){
            throw new NoRightsException();
        }
        team.changeGroupName(request.getGroupName());
        return new ChangeTeamNameResponse(team.getGroupName());
    }

    @Transactional
    public LeaveTeamResponse leaveGroup(String email, Long groupId) {

        Member member = getMemberInfo(email);
        Team team = getGroup(groupId);
        Member leader = team.getMember();

        if(leader.getId() == member.getId()){
            throw new LeaderLeaveException();
        }
        teamMemberRepository.leaveGroup(member, team);
        return new LeaveTeamResponse(
                member.getId(),
                groupId
        );
    }

    @Transactional
    public InviteTeamResponse inviteGroup(String email, InviteTeamDto request){

        Member inviting = getMemberInfo(email);
        Team team = getGroup(request.getGroupId());

        if(!Objects.equals(team.getMember().getId(), inviting.getId())){
            throw new NoRightToInviteException();
        }

        Member[] invited = new Member[request.getMemberId().length];
        for(int i=0;i<request.getMemberId().length;i++) {
            invited[i] = memberRepository.findById(request.getMemberId()[i]).orElseThrow(MemberNotFoundException::new);
            teamMemberRepository.save(new TeamMember(invited[i], team));
        }
        return new InviteTeamResponse(
                team.getId()
        );
    }

    @Transactional
    public DelegateLeaderResponse delegateLeader(String email, DelegateLeaderDto request) {

        Member oldLeader = getMemberInfo(email);
        Member newLeader = memberRepository.findById(request.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Team team = getGroup(request.getGroupId());
        if(!Objects.equals(team.getMember().getId(), oldLeader.getId())){
            throw new NoRightToDelegateException();
        }
        team.changeLeader(newLeader);
        return new DelegateLeaderResponse(request.getMemberId(), request.getGroupId());
    }

    /*
     *   그룹 조회 API
     *   @Param: 조회 요청자 이메일
     *   @Return: 조회 그룹 정보
     *   @author: Sanha Ko
     */
    public List<SearchGroupResponse> searchGroup(String email) {

        System.out.println(email);
        Member member = getMemberInfo(email);
        List<SearchGroupResponse> result = new ArrayList<>();
        List<Team> team = teamRepository.findGroupInfoWithMembers(member.getId());
        System.out.println(team);
        for (int i = 0; i<team.size(); i++) {
            List<TeamMember> teamMembers = teamMemberRepository.findMembersByTeamId(team.get(i).getId());
            List<String> membersName = new ArrayList<>();
            System.out.println(teamMembers);
            for (int j = 0; j<teamMembers.size(); j++) {
                membersName.add(teamMembers.get(j).getMember().getName());
            }
            System.out.println(membersName);
            System.out.println(new SearchGroupResponse(team.get(i).getId(), team.get(i).getGroupName(), team.get(i).getImageUrl(), membersName));
            result.add(new SearchGroupResponse(team.get(i).getId(), team.get(i).getGroupName(), team.get(i).getImageUrl(), membersName));
        }
        System.out.println(result);
        return result;
    }

    @Transactional
    public EditMyLocationResponse editMyLocation(String email, EditMyLocationDto request) {

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Member member = optionalMember.orElseThrow(MemberNotFoundException::new);
        TeamMember teamMember = teamMemberRepository.findMemberByMemberIdAndTeamId(member.getId(), request.getTeamId());
        teamMember.editMyLocation(request.getLatitude(), request.getLongitude());
        return new EditMyLocationResponse(request.getLatitude(), request.getLongitude());
    }

    public GetMidPointResponse getMidPoint(String email, Long teamId) {

        if (checkMemberInTeam(email, teamId)) {
            throw new MemberNotBelongsToTeam();
        }
        List<TeamMember> teamMemberList = teamMemberRepository.findMembersByTeamId(teamId);

        float avgLatitude=0;
        float avgLongitude=0;
        for (int i=0; i<teamMemberList.size(); i++) {
            avgLatitude=avgLatitude+teamMemberList.get(i).getLatitude();
            avgLongitude=avgLongitude+teamMemberList.get(i).getLongitude();
        }
        avgLatitude=avgLatitude/teamMemberList.size();
        avgLongitude=avgLongitude/teamMemberList.size();
        return new GetMidPointResponse(teamId, avgLatitude, avgLongitude);
    }

    public boolean checkMemberInTeam(String email, Long teamId) {

        List<TeamMember> foundMembers = teamMemberRepository.findMembersByTeamId(teamId);
        Member member = getMember(email);

        for (TeamMember foundMember : foundMembers) {
            if (foundMember.getMember() == member) {
                return false;
            }
        }
        return true;
    }

    public Member getMember(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.orElseThrow(MemberEmailNotFound::new);
    }

    public List<GetTeamMembersLocationResponse> getTeamMembersLocation(String email, Long teamId) {
        if (checkMemberInTeam(email, teamId)) {
            throw new MemberNotBelongsToTeam();
        }
        List<TeamMember> teamMemberList = teamMemberRepository.findMembersByTeamId(teamId);
        List<GetTeamMembersLocationResponse> result = teamMemberList.stream()
                .map(m -> new GetTeamMembersLocationResponse(m.getTeam().getId(), m.getMember().getId(), m.getMember().getName(), m.getLatitude(), m.getLongitude()))
                .collect(toList());
        return result;
    }
}
