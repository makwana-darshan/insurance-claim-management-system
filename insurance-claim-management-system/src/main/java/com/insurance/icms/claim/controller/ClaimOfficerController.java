package com.insurance.icms.claim.controller;

import com.insurance.icms.claim.enums.ClaimStatus;
import com.insurance.icms.claim.service.ClaimService;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/claim")
public class ClaimOfficerController {

    private final ClaimService claimService;
    private final UserRepository userRepository;

    public ClaimOfficerController(ClaimService claimService,
                                  UserRepository userRepository) {
        this.claimService = claimService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("claims",
                claimService.getClaimsByStatus(ClaimStatus.SUBMITTED));
        model.addAttribute("surveyors",
                userRepository.findByRole("SURVEYOR"));
        return "claim/dashboard";
    }

    @PostMapping("/assign/{claimId}")
    public String assignSurveyor(@PathVariable Long claimId,
                                 @RequestParam Long surveyorId,
                                 Authentication authentication) {

        User admin = (User) authentication.getPrincipal();

        User surveyor = userRepository.findById(surveyorId)
                .orElseThrow(() -> new RuntimeException("Surveyor not found"));

        claimService.assignSurveyor(claimId, admin, surveyor);

        return "redirect:/claim/dashboard";
    }
}
