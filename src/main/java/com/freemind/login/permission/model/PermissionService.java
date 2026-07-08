package com.freemind.login.permission.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository repository;

    public Permission addPermission(Permission permission) {
        return repository.save(permission);
    }

    public Permission updatePermission(Permission permission) {
        return repository.save(permission);
    }

    public Permission getOnePermission(Integer permId) {
        return repository.findById(permId).orElse(null);
    }

    public List<Permission> getAll() {
        return repository.findAll();
    }

    public void deletePermission(Integer permId) {
        repository.deleteById(permId);
    }

}
