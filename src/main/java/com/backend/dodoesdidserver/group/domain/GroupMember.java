package com.backend.dodoesdidserver.group.domain;

import com.backend.dodoesdidserver.home.domain.Dazim;
import com.backend.dodoesdidserver.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    private GroupRole role;

    @OneToMany(mappedBy = "groupMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dazim> dazims = new ArrayList<>();

    @Builder
    public GroupMember(User user, Group group, GroupRole role){
        this.role = role;
        changeUser(user);
        changeGroup(group);
    }

    private void changeUser(User user){
        if(user != null){
            this.user = user;
            user.getGroupMembers().add(this);
        }
    }

    private void changeGroup(Group group){
        if(group != null){
            this.group = group;
            group.getGroupMembers().add(this);
        }
    }

    public void changeRole(GroupRole role){
        this.role = role;
    }
}
