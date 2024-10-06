package com.backend.dodoesdidserver.home.repository;

import com.backend.dodoesdidserver.home.domain.Dazim;
import com.backend.dodoesdidserver.home.domain.DazimComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DazimCommentRepository extends JpaRepository<DazimComment, Long> {
    List<DazimComment> findByDazim(Dazim dazim);

}
