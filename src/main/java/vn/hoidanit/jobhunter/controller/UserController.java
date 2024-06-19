package vn.hoidanit.jobhunter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User postManUser) {
        String hashPass = passwordEncoder.encode(postManUser.getPassword());

        postManUser.setPassword(hashPass);
        this.userService.saveUser(postManUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(postManUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        if (id >= 1500) {
            throw new IdInvalidException("Id lon hon 1500");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("postmanuser");
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> fetchUserById(@PathVariable("id") long id) {
        User u = this.userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(u);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> fetchAllUser() {
        List<User> l = this.userService.getAllUser();
        return ResponseEntity.status(HttpStatus.OK).body(l);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User postManUser) {
        User u = this.userService.updateUser(postManUser);
        return ResponseEntity.ok(u);
    }
}
