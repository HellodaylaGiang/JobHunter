package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User postManUser) throws IdInvalidException {

        boolean isEmailExist = this.userService.isEmailExist(postManUser.getEmail());

        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email" + postManUser.getEmail() + "đã tồn tại, sử dụng email khác");
        }

        String hashPass = passwordEncoder.encode(postManUser.getPassword());

        postManUser.setPassword(hashPass);
        User u = this.userService.handleCreateUser(postManUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(u));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {

        User currentUser = this.userService.fetchUserById(id);

        if (currentUser == null) {
            throw new IdInvalidException("User với id = " + id + "không tồn tại");
        }

        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User u = this.userService.fetchUserById(id);
        if (u == null) {
            throw new IdInvalidException("User với id = " + id + "không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(u));
    }

    @GetMapping("/users")
    @ApiMessage("fetch all user")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        // String sCurrent = currentOptinal.isPresent() ? currentOptinal.get() : "";
        // String sPageSize = currentOptinal.isPresent() ? pageSizeOptional.get() : "";

        // int current = Integer.parseInt(sCurrent);
        // int pageSize = Integer.parseInt(sPageSize);

        // Pageable pageable = PageRequest.of(current - 1, pageSize);

        ResultPaginationDTO l = this.userService.fetchAllUser(spec, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(l);
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User postManUser) throws IdInvalidException {
        User u = this.userService.handleUpdateUser(postManUser);
        if (u == null) {
            throw new IdInvalidException("User với id =" + postManUser.getId() + "không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(u));
    }
}
