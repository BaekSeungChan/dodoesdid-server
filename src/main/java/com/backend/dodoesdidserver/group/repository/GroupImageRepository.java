package com.backend.dodoesdidserver.group.repository;

import com.backend.dodoesdidserver.group.domain.Group;
import com.backend.dodoesdidserver.group.domain.GroupImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupImageRepository extends JpaRepository<GroupImage, Long> {
    Optional<GroupImage> findByGroup(Group groupId);
    void deleteByGroup(Group group);
}
