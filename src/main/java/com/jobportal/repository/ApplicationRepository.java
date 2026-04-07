package com.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
	
	// All applications submitted by one candidate
	List<Application> findByCandidate(User candidate);
	
	//list out All applications for one job — highest score first
	List<Application> findByJobOrderByMatchScoreDesc(Job job);
	
	// Check if candidate already applied to this job or not
	 boolean existsByCandidateAndJob(User candidate, Job job);

}
