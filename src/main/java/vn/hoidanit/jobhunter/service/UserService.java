package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User u) {
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
        Meta mt = new Meta();

        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(page.getContent());

        List<ResUserDTO> listUser = page.getContent()
                .stream().map(item -> new ResUserDTO(
                        item.getId(),
                        item.getEmail(),
                        item.getName(),
                        item.getGender(),
                        item.getAddress(),
                        item.getAge(),
                        item.getUpdatedAt(),
                        item.getCreatedAt()))
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

            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
