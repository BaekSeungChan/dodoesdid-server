package com.backend.dodoesdidserver.home.repository;

import com.backend.dodoesdidserver.group.domain.GroupMember;
import com.backend.dodoesdidserver.home.domain.Dazim;

import com.backend.dodoesdidserver.home.repository.custom.DazimCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DazimRepository extends JpaRepository<Dazim, Long>, DazimCustom {
    Dazim findByIdAndGroupMember(Long dazimId, GroupMember groupMember);
}
