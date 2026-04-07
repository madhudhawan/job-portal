package com.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jobportal.model.Job;
import com.jobportal.model.User;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
	
	List<Job> findByRecruiter(User recruiter);

}
