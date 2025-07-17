package com.mercadonosso.users_service.adapters.out.persistence.jpa;

import com.mercadonosso.users_service.core.domain.User;

public class UserMapper {
    public static UserJPAEntity toJPAEntity(User user) {
        var entity = new UserJPAEntity();
        entity.setId(user.getId());
        entity.setFullName(user.getFullName());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setCpf(user.getCpf());
        entity.setCnpj(user.getCnpj());
        entity.setTelephoneNumber(user.getTelephoneNumber());
        entity.setCep(user.getCep());
        entity.setSocialReason(user.getSocialReason());
        entity.setSeller(user.isSeller());
        entity.setProfilePictureUrl(user.getProfilePictureUrl());
        entity.setOrderSellingId(user.getOrderSellingId());
        entity.setOrderBoughtId(user.getOrderBoughtId());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        entity.setActive(user.isActive());
        return entity;
    }

    public static User toDomain(UserJPAEntity jpaEntity) {
        var domain = new User();
        domain.setId(jpaEntity.getId());
        domain.setFullName(jpaEntity.getFullName());
        domain.setEmail(jpaEntity.getEmail());
        domain.setPasswordHash(jpaEntity.getPasswordHash());
        domain.setCpf(jpaEntity.getCpf());
        domain.setCnpj(jpaEntity.getCnpj());
        domain.setTelephoneNumber(jpaEntity.getTelephoneNumber());
        domain.setCep(jpaEntity.getCep());
        domain.setSocialReason(jpaEntity.getSocialReason());
        domain.setSeller(jpaEntity.isSeller());
        domain.setProfilePictureUrl(jpaEntity.getProfilePictureUrl());
        domain.setOrderSellingId(jpaEntity.getOrderSellingId());
        domain.setOrderBoughtId(jpaEntity.getOrderBoughtId());
        domain.setCreatedAt(jpaEntity.getCreatedAt());
        domain.setUpdatedAt(jpaEntity.getUpdatedAt());
        domain.setActive(jpaEntity.isActive());
        return domain;
    }
}
