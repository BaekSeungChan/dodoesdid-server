package com.backend.dodoesdidserver.group.repository;

import com.backend.dodoesdidserver.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Boolean existsByGroupName(String groupName);
    Group findByGroupName(String groupName);
}
