package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;

    public SubscriberService(SubscriberRepository subscriberRepository,
            SkillRepository skillRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
    }

    public boolean isExistsByEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber create(Subscriber subscriber) {
        // check skill list
        if (subscriber.getSkills() != null) {
            List<Long> reqSkills = subscriber.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkill = this.skillRepository.findByIdIn(reqSkills);
            subscriber.setSkills(dbSkill);
        }

        return this.subscriberRepository.save(subscriber);
    }

    public Subscriber findById(long id) {
        Optional<Subscriber> s = this.subscriberRepository.findById(id);
        if (s.isPresent()) {
            return s.get();
        }

        return null;
    }

    public Subscriber update(Subscriber subsDB, Subscriber subsRequest) {
        // get List id skill
        if (subsRequest.getSkills() != null) {
            List<Long> reqSkills = subsRequest.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkill = this.skillRepository.findByIdIn(reqSkills);
            subsDB.setSkills(dbSkill);
        }

        return this.subscriberRepository.save(subsDB);
    }

}
