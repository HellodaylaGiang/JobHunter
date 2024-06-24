package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company c) {
        return this.companyRepository.save(c);
    }

    public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> p = this.companyRepository.findAll(spec, pageable);

        ResultPaginationDTO r = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(p.getNumber() + 1);
        mt.setPageSize(p.getSize());

        mt.setPages(p.getTotalPages());
        mt.setTotal(p.getTotalElements());

        r.setMeta(mt);
        r.setResult(p.getContent());
        return r;
    }

    public Company handleUpdateCompany(Company c) {
        Optional<Company> company = this.companyRepository.findById(c.getId());
        if (company.isPresent()) {
            Company currentC = company.get();
            currentC.setLogo(c.getLogo());
            currentC.setName(c.getName());
            currentC.setDescription(c.getDescription());
            currentC.setAddress(c.getAddress());
            return this.companyRepository.save(currentC);
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        this.companyRepository.deleteById(id);
    }
}
