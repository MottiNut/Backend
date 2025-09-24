package com.mottinut.auth.domain.entities;

import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "share_clicks")
@Getter
@Setter
@NoArgsConstructor
public class ShareClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "share_code_id", nullable = false)
    private Long shareCodeId;

    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "referrer", columnDefinition = "TEXT")
    private String referrer;

    @Column(name = "country", length = 2)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    public ShareClick(Long shareCodeId, String ipAddress, String userAgent, String referrer) {
        this.shareCodeId = shareCodeId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.referrer = referrer;
        this.clickedAt = LocalDateTime.now();
    }
}
