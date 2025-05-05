package com.ph4ntoms.authenticate;

import com.ph4ntoms.authenticate.model.Group;
import com.ph4ntoms.authenticate.model.Permission;
import com.ph4ntoms.authenticate.model.Role;
import com.ph4ntoms.authenticate.model.User;
import com.ph4ntoms.authenticate.repo.GroupRepository;
import com.ph4ntoms.authenticate.repo.PermissionRepository;
import com.ph4ntoms.authenticate.repo.RoleRepository;
import com.ph4ntoms.authenticate.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class InitDatabase implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GroupRepository groupRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Permission getPermission(String name, String code, String description) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    Permission permission = Permission.builder()
                            .name(name)
                            .code(code)
                            .enabled(true)
                            .description(description)
                            .build();
                    return permissionRepository.save(permission);
                });
    }

    @Transactional
    public Role getRole(String name, Set<Permission> permissions, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name(name)
                            .code(name)
                            .description(description)
                            .permissions(new HashSet<>(permissions))
                            .build();
                    return roleRepository.save(role);
                });
    }

    @Transactional
    public Group getGroup(String name, Set<Role> roles, String description) {
        return groupRepository.findByName(name)
                .orElseGet(() -> {
                    Group group = Group.builder()
                            .name(name)
                            .code(name)
                            .enabled(true)
                            .description(description)
                            .roles(new HashSet<>(roles))
                            .build();
                    return groupRepository.save(group);
                });
    }

    @Transactional
    public void getUser(
            String username,
            String email,
            String firstName,
            String lastName,
            Set<Group> groups
    ) {
        userRepository.findByUsername(username).orElseGet(() -> {
            User user = User.builder()
                    .username(username)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .enabled(true)
                    .groups(new HashSet<>(groups))
                    .balance(999L)
                    .password(passwordEncoder.encode("123456"))
                    .phone("84" + ThreadLocalRandom.current().nextInt(1_000_000_00, 10_000_000_00))
                    .build();
            return userRepository.save(user);
        });
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Create permissions
        Permission searchPermission = getPermission("SEARCH", "SEARCH", "Search permission");
        Permission createPermission = getPermission("CREATE", "CREATE", "Create permission");
        Permission updatePermission = getPermission("UPDATE", "UPDATE", "Update permission");
        Permission deletePermission = getPermission("DELETE", "DELETE", "Delete permission");

        // Create roles with permissions
        Set<Permission> adminPermissions = Set.of(searchPermission, createPermission, updatePermission, deletePermission);
        Set<Permission> userPermissions = Set.of(searchPermission);

        Role adminRole = getRole("ADMIN", adminPermissions, "Admin role");
        Role userRole = getRole("USER", userPermissions, "User role");
        Role managerRole = getRole("MANAGER", Set.of(searchPermission, updatePermission), "Manager role");

        // Create groups with roles
        Group adminGroup = getGroup("Admin Group", Set.of(adminRole), "Admin group");
        Group userGroup = getGroup("User Group", Set.of(userRole), "User group");
        Group managerGroup = getGroup("Manager Group", Set.of(managerRole, userRole), "Manager group");

        // Create users with groups
        getUser("admin", "admin@gmail.com", "Admin", "Person 1", Set.of(adminGroup));
        getUser("customer", "customer@gmail.com", "Customer", "Person 2", Set.of(userGroup));
        getUser("manager", "manager@gmail.com", "Manager", "Person 3", Set.of(managerGroup));
        getUser("employee", "employee@gmail.com", "Employee", "Person 4", Set.of(userGroup, managerGroup));

        // Add more sample users
        for (int i = 1; i <= 10; i++) {
            getUser(
                    "user" + i,
                    "user" + i + "@gmail.com",
                    "User" + i,
                    "LastName" + i,
                    Set.of(userGroup)
            );
        }
    }
}
