package com.mo16.eventstoreplayground;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Getter
@ToString
@RequiredArgsConstructor
@Entity
public class SubscriptionCheckpoint {
    @Id
    private String id;
    @Column(nullable = false)
    private String streamId;
    @Column(nullable = false)
    private long revision;

    public static SubscriptionCheckpoint get(){
        return new SubscriptionCheckpoint();
    }
    
    public SubscriptionCheckpoint id(String id){
        this.id = id;
        return this;
    }
    
    public SubscriptionCheckpoint streamId(String streamId){
        this.streamId = streamId;
        return this;
    }
    
    public SubscriptionCheckpoint revision(long revision){
        this.revision = revision;
        return this;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SubscriptionCheckpoint that = (SubscriptionCheckpoint) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
