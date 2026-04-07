package com.jobportal.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.service.EmailService;
import com.jobportal.service.MatchingService;
import com.jobportal.service.ResumeParserService;
import com.jobportal.service.SkillExtractorService;
import com.jobportal.service.UserService;

@Controller
@RequestMapping("/candidate")
public class CandidateController {
	
	@Autowired
	private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ResumeParserService resumeParserService;

    @Autowired
    private SkillExtractorService skillExtractorService;

    @Autowired
    private MatchingService matchingService;

    // ==============================
    // Candidate Dashboard
    // URL : http://localhost:8080/candidate/dashboard
    // ==============================
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        // Get currently logged in candidate
        User candidate = userService.findByEmail(principal.getName());

        // Get all applications by this candidate
        List<Application> applications = 
            applicationRepository.findByCandidate(candidate);

        // Get all available jobs
        List<Job> allJobs = jobRepository.findAll();

        // Send data to HTML page
        model.addAttribute("candidate", candidate);
        model.addAttribute("applications", applications);
        model.addAttribute("allJobs", allJobs);

        return "candidate-dashboard";   // templates/candidate-dashboard.html
    }

    // ==============================
    // Show all available jobs
    // URL : http://localhost:8080/candidate/jobs
    // ==============================
    @GetMapping("/jobs")
    public String viewJobs(Model model, Principal principal) {

        User candidate = userService.findByEmail(principal.getName());
        List<Job> jobs = jobRepository.findAll();

        model.addAttribute("jobs", jobs);
        model.addAttribute("candidate", candidate);

        return "candidate-jobs";    // templates/candidate-jobs.html
    }

    // ==============================
    // Apply to a job with resume upload
    // URL : POST http://localhost:8080/candidate/apply/{jobId}
    // ==============================
    @PostMapping("/apply/{jobId}")
    public String applyToJob(
            @PathVariable Long jobId,
            @RequestParam("resumeFile") MultipartFile resumeFile,
            Principal principal,
            Model model) {

        try {
            // Get logged in candidate
            User candidate = userService.findByEmail(principal.getName());

            // Get job
            Job job = jobRepository.findById(jobId).orElse(null);

            if (job == null) {
                return "redirect:/candidate/jobs?error=jobNotFound";
            }

            // Check already applied
            boolean alreadyApplied = applicationRepository
                    .existsByCandidateAndJob(candidate, job);

            if (alreadyApplied) {
                return "redirect:/candidate/jobs?alreadyApplied=true";
            }

            // Validate file
            if (resumeFile.isEmpty()) {
                return "redirect:/candidate/jobs?error=emptyFile";
            }

            // Step 1: Save resume
            String resumePath = resumeParserService.saveResume(resumeFile);

            // Step 2: Extract text
            String resumeText = resumeParserService.extractTextFromPDF(resumePath);

            // Step 3: Extract skills
            String extractedSkills = skillExtractorService.extractSkills(resumeText);

            // Step 4: Calculate score
            double score = matchingService.calculateScore(
                    extractedSkills, job.getRequiredSkills()
            );

            // Step 5: Save application
            Application application = new Application();
            application.setCandidate(candidate);
            application.setJob(job);
            application.setResumePath(resumePath);
            application.setExtractedSkills(extractedSkills);
            application.setMatchScore(score);
            application.setStatus("PENDING");

            applicationRepository.save(application);

            // Step 6: Send email
            try {
                emailService.sendApplicationConfirmationEmail(
                        candidate.getEmail(),
                        candidate.getFullName(),
                        job.getTitle(),
                        job.getCompanyName(),
                        score
                );
                System.out.println("✅ Confirmation email sent");

            } catch (Exception e) {
                System.out.println("⚠️ Email failed: " + e.getMessage());
            }

            // Step 7: Redirect
            return "redirect:/candidate/dashboard?applied=true";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/candidate/jobs?error=true";
        }
    }
    @GetMapping("/job/{jobId}")
    public String viewJobDetail(
            @PathVariable Long jobId,
            Model model,
            Principal principal) {

        User candidate = userService.findByEmail(principal.getName());
        Job job = jobRepository.findById(jobId).orElse(null);

        boolean alreadyApplied = applicationRepository
            .existsByCandidateAndJob(candidate, job);

        model.addAttribute("job", job);
        model.addAttribute("alreadyApplied", alreadyApplied);
        model.addAttribute("candidate", candidate);

        return "job-detail";    // templates/job-detail.html
    }
}