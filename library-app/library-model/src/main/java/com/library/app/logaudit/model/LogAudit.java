package com.library.app.logaudit.model;

import com.library.app.user.model.User;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "lib_log_audit")
public class LogAudit implements Serializable{
    private static final long serialVersionUID = -6737238567613975932L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    @NotNull
    private Date createdAt;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;
    
    public enum Action {
        ADD, UPDATE
    }
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private Action action;
    
    @NotNull
    private String element;

    public LogAudit(){
        this.createdAt = new Date();
    }

    public LogAudit(User user, Action action, String element) {
        this();
        this.user = user;
        this.action = action;
        this.element = element;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogAudit other = (LogAudit) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LogAudit{" + "id=" + id + ", createdAt=" + createdAt + ", user=" + user + ", action=" + action + ", element=" + element + '}';
    }
    
}
