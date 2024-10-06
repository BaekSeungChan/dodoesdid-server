package com.backend.dodoesdidserver.home.domain;

import com.backend.dodoesdidserver.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DazimImage extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dazim_iamge_id")
    private Long id;

    private String originalFileName;

    private String storedFileName;

    @Enumerated(EnumType.STRING)
    private DazimSucess dazimSucess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dazim_id")
    private Dazim dazim;

    @Builder
    public DazimImage(String originalFileName, String storedFileName, Dazim dazim, DazimSucess dazimSucess){
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.dazimSucess = dazimSucess;
        changeDazim(dazim);
    }

    public static DazimImage createDazimImage(String originalFileName, String storedFileName, Dazim dazim) {
        DazimImage dazimImage = DazimImage.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .dazimSucess(DazimSucess.SUCESS)
                .build();

        dazimImage.changeDazim(dazim);

        return dazimImage;
    }

    private void changeDazim(Dazim dazim){
        if(dazim != null){
            this.dazim = dazim;
            dazim.getDazimImages().add(this);
        }
    }

}
