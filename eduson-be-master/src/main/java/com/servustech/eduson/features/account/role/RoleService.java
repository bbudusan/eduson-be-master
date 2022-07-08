package com.servustech.eduson.features.account.role;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class RoleService {
    private final RoleRepository roleRepository;
    
    public Role findByRoleName(RoleName name){
        return roleRepository.findByName(name);
    }

    public Role getUserRole() {
        return roleRepository.findByName(RoleName.ROLE_USER);
    }

    public Role getAdminRole() {
        return roleRepository.findByName(RoleName.ROLE_ADMIN);
    }

    public Role getLectorRole() {
        return roleRepository.findByName(RoleName.ROLE_LECTOR);
    }
}
