package com.example.Web.models;

import javax.persistence.*;


@Entity
public class UserRole {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;

    public UserRole(User user_id, Role role) {
        this.user = user_id;
        this.role = role;
    }

    public UserRole() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
