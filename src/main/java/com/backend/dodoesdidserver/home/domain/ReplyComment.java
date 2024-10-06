package com.backend.dodoesdidserver.home.domain;

import com.backend.dodoesdidserver.common.BaseEntity;
import com.backend.dodoesdidserver.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyComment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dazim_conmment_id")
    private DazimComment dazimComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public ReplyComment(User user, DazimComment dazimComment, String content){
        this.user = user;
        this.content = content;
        changeDazimComment(dazimComment);
    }

    private void changeDazimComment(DazimComment dazimComment){
        if(dazimComment != null){
            this.dazimComment = dazimComment;
            dazimComment.getReplies().add(this);
        }
    }

    public void changeReplyContent(String content){
        this.content = content;
    }
}
