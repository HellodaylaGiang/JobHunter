package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository,
            CompanyService companyService,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User u) {
        // check company
        if (u.getCompany() != null) {
            Optional<Company> comOptional = this.companyService.findById(u.getCompany().getId());
            u.setCompany(comOptional.isPresent() ? comOptional.get() : null);
        }

        // check role
        if (u.getRole() != null) {
            Role r = roleService.fetchById(u.getRole().getId());
            u.setRole(r != null ? r : null);
        }
        return this.userRepository.save(u);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        Optional<User> u = this.userRepository.findById(id);
        if (u.isPresent()) {
            return u.get();
        }
        return null;
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User u) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser companyUser = new ResCreateUserDTO.CompanyUser();

        if (u.getCompany() != null) {
            companyUser.setId(u.getCompany().getId());
            companyUser.setName(u.getCompany().getName());
            res.setCompanyUser(companyUser);
        }

        res.setId(u.getId());
        res.setEmail(u.getEmail());
        res.setName(u.getName());
        res.setAge(u.getAge());
        res.setCreatedAt(u.getCreatedAt());
        res.setAddress(u.getAddress());

        return res;
    }

    public ResUserDTO convertToResUserDTO(User u) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();

        if (u.getCompany() != null) {
            companyUser.setId(u.getCompany().getId());
            companyUser.setName(u.getCompany().getName());
            res.setCompanyUser(companyUser);
        }

        if (u.getRole() != null) {
            roleUser.setId(u.getRole().getId());
            roleUser.setName(u.getRole().getName());
            res.setRole(roleUser);
        }

        res.setId(u.getId());
        res.setEmail(u.getEmail());
        res.setName(u.getName());
        res.setAge(u.getAge());
        res.setUpdatedAt(u.getUpdatedAt());
        res.setCreatedAt(u.getCreatedAt());
        res.setGender(u.getGender());
        res.setAddress(u.getAddress());

        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User u) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser companyUser = new ResUpdateUserDTO.CompanyUser();
        if (u.getCompany() != null) {
            companyUser.setId(u.getCompany().getId());
            companyUser.setName(u.getCompany().getName());

            res.setCompanyUser(companyUser);
        }

        res.setId(u.getId());
        res.setName(u.getName());
        res.setAge(u.getAge());
        res.setUpdatedAt(u.getUpdatedAt());
        res.setGender(u.getGender());
        res.setAddress(u.getAddress());
        return res;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> page = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);

        List<ResUserDTO> listUser = page.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);
        return rs;
    }

    public User handleUpdateUser(User u) {
        User currentUser = this.fetchUserById(u.getId());
        if (currentUser != null) {
            currentUser.setAddress(u.getAddress());
            currentUser.setGender(u.getGender());
            currentUser.setAge(u.getAge());
            currentUser.setName(u.getName());

            // check company
            if (u.getCompany() != null) {
                Optional<Company> comOptional = this.companyService.findById(u.getCompany().getId());
                currentUser.setCompany(comOptional.isPresent() ? comOptional.get() : null);
            }

            // check role
            if (u.getRole() != null) {
                Role r = roleService.fetchById(u.getRole().getId());
                currentUser.setRole(r != null ? r : null);
            }

            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
