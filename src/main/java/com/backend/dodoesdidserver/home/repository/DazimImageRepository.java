package com.backend.dodoesdidserver.home.repository;

import com.backend.dodoesdidserver.home.domain.Dazim;
import com.backend.dodoesdidserver.home.domain.DazimImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DazimImageRepository extends JpaRepository<DazimImage, Long> {
}
