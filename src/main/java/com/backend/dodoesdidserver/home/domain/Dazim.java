package com.backend.dodoesdidserver.home.domain;

import com.backend.dodoesdidserver.common.BaseEntity;
import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.group.domain.GroupMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Dazim extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dazim_id")
    private Long id;

    private String dazimContent;

    private int dodoesCount1;
    private int dodoesCount2;
    private int dodoesCount3;
    private int dodoesCount4;
    private int dodoesCount5;


    @OneToMany(mappedBy = "dazim", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DazimImage> dazimImages = new ArrayList<>();

    @OneToMany(mappedBy = "dazim", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DazimLike> dazimLikes = new ArrayList<>();

    @OneToMany(mappedBy = "dazim", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DazimComment> dazimCommnets = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_id")
    private GroupMember groupMember;

    @Builder
    public Dazim(String dazimContent, GroupMember groupMember){
        this.dazimContent = dazimContent;
        changeDazim(groupMember);
    }

    private void changeDazim(GroupMember groupMember){
        if(groupMember != null){
            this.groupMember = groupMember;
            groupMember.getDazims().add(this);
        }
    }

    public void increaseButtonCount(int buttonNum){
        switch(buttonNum){
            case 1: dodoesCount1++; break;
            case 2: dodoesCount2++; break;
            case 3: dodoesCount3++;break;
            case 4: dodoesCount4++; break;
            case 5: dodoesCount5++; break;
            default:
                throw new ApiException(ErrorCode.INVALID_BUTTON_NUMBER);
        }
    }

    public void decreaseButtonCount(int buttonNum) {
        switch (buttonNum){
            case 1: if(buttonNum > 0) dodoesCount1--; break;
            case 2: if(buttonNum > 0) dodoesCount2--; break;
            case 3: if(buttonNum > 0) dodoesCount3--; break;
            case 4: if(buttonNum > 0) dodoesCount4--; break;
            case 5: if(buttonNum > 0) dodoesCount5--; break;
        }
    }

}
