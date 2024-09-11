//package com.kjone.kjoneuserservice.domain.user;
//
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.sql.Timestamp;
//
//@Entity
//@Table(name = "user_files")
//@Setter
//@Getter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class UserFile {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long fileId;
//
//    @ManyToOne

//    @JoinColumn(name = "user_id", nullable = false)
//    private User user_id;
//
////    // 권한이라는 것에 외래키를 추가 함으로써
////    @ElementCollection(fetch = FetchType.EAGER)
////    @Enumerated(EnumType.STRING)
////    @CollectionTable(name = "authority", joinColumns = @JoinColumn(name = "user_id"))
////    @Column(name = "role")
////    private Set<FileType> roles = new HashSet<>();
//
//    @Column(name = "file_name", nullable = false)
//    private String fileName;
//
//    @Column(name = "file_path", nullable = false)
//    private String filePath;
//
//    @Column(name = "uploaded_at", nullable = false, updatable = false)
//    private Timestamp uploadedAt;
//
//    // Getters and Setters
//}