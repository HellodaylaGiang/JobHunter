package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission p) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                p.getModule(),
                p.getApiPath(),
                p.getMethod());
    }

    public Permission create(Permission p) {
        return this.permissionRepository.save(p);
    }

    public Permission fetchById(long id) {
        Optional<Permission> p = this.permissionRepository.findById(id);
        if (p.isPresent()) {
            return p.get();
        }

        return null;
    }

    public Permission update(Permission p) {
        Permission permissionDB = this.fetchById(p.getId());
        if (permissionDB != null) {
            permissionDB.setName(p.getName());
            permissionDB.setApiPath(p.getApiPath());
            permissionDB.setMethod(p.getMethod());
            permissionDB.setModule(p.getModule());

            // update
            permissionDB = this.permissionRepository.save(permissionDB);

            return permissionDB;
        }
        return null;
    }

    public void delete(long id) {

        // delete permission_role
        Optional<Permission> p = this.permissionRepository.findById(id);
        Permission currentPermission = p.get();
        // xóa từng đối tượng trong List
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

        // delete permission
        this.permissionRepository.deleteById(id);
    }

    public ResultPaginationDTO getPermisions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> p = this.permissionRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(p.getTotalPages());
        mt.setTotal(p.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(p.getContent());

        return rs;
    }

    public boolean isSameName(Permission p) {
        Permission permissionDB = this.fetchById(p.getId());
        if (permissionDB != null) {
            if (permissionDB.getName().equals(p.getName())) {
                return true;
            }
        }
        return false;
    }
}
