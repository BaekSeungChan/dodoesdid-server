package com.backend.dodoesdidserver.group.domain;

import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dodoes_groups")
public class Group {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    private String groupName;

    private String groupUrl;

    private String groupNotice;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupImage> groupImages = new ArrayList<>();

    @Builder
    public Group(String groupName){
        if(groupName == null || groupName.isEmpty()) {
            throw new ApiException(ErrorCode.GROUP_NOT_FOUND);
        }
        this.groupName = groupName;
    }

    public static Group createGroup(String groupName){

        return Group.builder()
                .groupName(groupName)
                .build();
    }

    public void changeGroupUrl(String groupUrl){
        this.groupUrl = groupUrl;
    }

    public void updateGroupName(String groupName){
        this.groupName = groupName;
    }

    public void updateGroupNotice(String groupNotice){
        this.groupNotice = groupNotice;
    }

}
