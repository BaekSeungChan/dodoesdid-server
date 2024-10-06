package com.backend.dodoesdidserver.home.domain;

import com.backend.dodoesdidserver.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DazimLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dazim_id")
    private Dazim dazim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int buttonNumber;
    private boolean isPressed;

    @Builder
    public DazimLike(Dazim dazim, User user, int buttonNumber, boolean isPressed) {
        changeDazime(dazim);
        this.user = user;
        this.buttonNumber = buttonNumber;
        this.isPressed = isPressed;
    }

    private void changeDazime(Dazim dazim) {
        if(dazim != null){
            this.dazim = dazim;
            dazim.getDazimLikes().add(this);
        }
    }
    public void changePressed(boolean isPressed){
        this.isPressed = isPressed;
    }

}
