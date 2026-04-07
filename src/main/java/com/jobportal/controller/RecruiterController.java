package com.jobportal.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.service.EmailService;
import com.jobportal.service.UserService;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    
    // Recruiter Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        User recruiter = userService.findByEmail(principal.getName());
        List<Job> myJobs = jobRepository.findByRecruiter(recruiter);

        model.addAttribute("recruiter", recruiter);
        model.addAttribute("myJobs", myJobs);

        return "recruiter-dashboard";
    }

    
    // Show Post Job Form
    @GetMapping("/post-job")
    public String showPostJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "post-job";
    }

    
    // Post Job
    @PostMapping("/post-job")
    public String postJob(@ModelAttribute("job") Job job, Principal principal) {

        User recruiter = userService.findByEmail(principal.getName());
        job.setRecruiter(recruiter);

        jobRepository.save(job);

        return "redirect:/recruiter/dashboard?jobPosted=true";
    }

    
    // View Applicants (Ranked)
    @GetMapping("/applicants/{jobId}")
    public String viewApplicants(@PathVariable Long jobId, Model model) {

        Job job = jobRepository.findById(jobId).orElse(null);

        if (job == null) {
            return "redirect:/recruiter/dashboard";
        }

        List<Application> applicants =
                applicationRepository.findByJobOrderByMatchScoreDesc(job);

        model.addAttribute("job", job);
        model.addAttribute("applicants", applicants);

        return "applicants";
    }

    
    // Update Status + Send Email
    @PostMapping("/update-status/{appId}")
    public String updateStatus(
            @PathVariable Long appId,
            @RequestParam("status") String status) {

        Application application =
                applicationRepository.findById(appId).orElse(null);

        if (application == null) {
            return "redirect:/recruiter/dashboard";
        }

        // Store jobId BEFORE update (safe navigation)
        Long jobId = application.getJob().getId();

        // Update status
        application.setStatus(status.toUpperCase());
        applicationRepository.save(application);

        // Candidate + Job details
        String candidateEmail = application.getCandidate().getEmail();
        String candidateName  = application.getCandidate().getFullName();
        String jobTitle       = application.getJob().getTitle();
        String companyName    = application.getJob().getCompanyName();

        // Send Email
        try {
            if ("ACCEPTED".equalsIgnoreCase(status)) {

                emailService.sendAcceptanceEmail(
                        candidateEmail,
                        candidateName,
                        jobTitle,
                        companyName
                );

                System.out.println("✅ Acceptance email sent to: " + candidateEmail);

            } else if ("REJECTED".equalsIgnoreCase(status)) {

                emailService.sendRejectionEmail(
                        candidateEmail,
                        candidateName,
                        jobTitle,
                        companyName
                );

                System.out.println("✅ Rejection email sent to: " + candidateEmail);
            }

        } catch (Exception e) {
            System.out.println("⚠️ Email failed: " + e.getMessage());
        }

        return "redirect:/recruiter/applicants/" + jobId;
    }
}