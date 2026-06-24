package se.lagesbild.intervention;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    List<Intervention> findByOrderByStartedAtDesc();
}
