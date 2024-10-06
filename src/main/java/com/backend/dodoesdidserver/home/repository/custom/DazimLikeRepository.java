package com.backend.dodoesdidserver.home.repository.custom;

import com.backend.dodoesdidserver.home.domain.Dazim;
import com.backend.dodoesdidserver.home.domain.DazimLike;
import com.backend.dodoesdidserver.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DazimLikeRepository extends JpaRepository<DazimLike, Long> {

    Optional<DazimLike> findByDazimAndUserAndButtonNumber(Dazim dazim, User user, int buttonNumber);

}
