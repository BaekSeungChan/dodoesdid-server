package com.backend.dodoesdidserver.group.repository;

import com.backend.dodoesdidserver.group.domain.Group;
import com.backend.dodoesdidserver.group.domain.GroupMember;
import com.backend.dodoesdidserver.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    GroupMember findByUserAndGroup(User user, Group group);

    List<GroupMember> findAllByUser(User user);

    boolean existsByGroupAndUser(Group group, User user);

    List<GroupMember> findAllByGroup(Group group);
}
