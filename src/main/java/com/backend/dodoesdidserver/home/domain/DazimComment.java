package com.backend.dodoesdidserver.home.domain;

import com.backend.dodoesdidserver.common.BaseEntity;
import com.backend.dodoesdidserver.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DazimComment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dazim_id")
    private Dazim dazim;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "dazimComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReplyComment> replies = new ArrayList<>();

    public DazimComment(Dazim dazim, User user, String content){
        this.content = content;
        this.user = user;
        changeDazim(dazim);
    }

    private void changeDazim(Dazim dazim){
        this.dazim = dazim;
        dazim.getDazimCommnets().add(this);
    }

    public void changeDazimContent(String content){
        this.content = content;
    }
}
