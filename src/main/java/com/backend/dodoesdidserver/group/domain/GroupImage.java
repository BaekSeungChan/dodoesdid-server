package com.backend.dodoesdidserver.group.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class GroupImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;

    private String storedFileName;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    public GroupImage(String originalFileName, String storedFileName, Group group) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        changeGroup(group);
    }

    private void changeGroup(Group group) {
        if(group != null) {
            this.group = group;
            group.getGroupImages().add(this);
        }
    }

    public static GroupImage createGroupImage(Group group, String originalFileName, String storedFileName){
        return GroupImage.builder()
                .group(group)
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .build();
    }
}
