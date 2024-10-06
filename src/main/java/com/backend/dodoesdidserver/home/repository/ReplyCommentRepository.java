package com.backend.dodoesdidserver.home.repository;

import com.backend.dodoesdidserver.home.domain.DazimComment;
import com.backend.dodoesdidserver.home.domain.ReplyComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyCommentRepository extends JpaRepository<ReplyComment, Long> {
    List<ReplyComment> findByDazimComment(DazimComment parentComment);
}
