package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User u) {
        return this.userRepository.save(u);
    }

    public void deleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User getUser(long id) {
        Optional<User> u = this.userRepository.findById(id);
        if (u.isPresent()) {
            return u.get();
        }
        return null;
    }

    public List<User> getAllUser() {
        return this.userRepository.findAll();
    }

    public User updateUser(User u) {
        User currentUser = this.getUser(u.getId());
        if (currentUser != null) {
            currentUser.setName(u.getName());
            currentUser.setEmail(u.getEmail());
            currentUser.setPassword(u.getPassword());
            this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
