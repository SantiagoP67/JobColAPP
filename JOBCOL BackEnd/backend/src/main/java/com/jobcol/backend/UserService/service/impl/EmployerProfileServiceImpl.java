package com.jobcol.backend.UserService.service.impl;
import com.jobcol.backend.shared.mappers.EmployerProfileMapper;
import com.jobcol.backend.UserService.model.EmployerProfile;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.EmployerProfileRepository;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.UserService.service.EmployerProfileService;
import com.jobcol.backend.shared.dto.EmployerProfileDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Service
@RequiredArgsConstructor
public class EmployerProfileServiceImpl implements EmployerProfileService {
    private final EmployerProfileRepository employerProfileRepository;
    private final UserRepository userRepository;

    public EmployerProfileDTO createProfile(EmployerProfileDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = extractEmail(auth);

        System.out.println("EMAIL DEL TOKEN: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (employerProfileRepository.existsByUser(user)) {
            throw new RuntimeException("El usuario ya tiene perfil");
        }
        System.out.println("DTO RECIBIDO: " + dto);
        EmployerProfile employerProfile = EmployerProfileMapper.toEntity(dto);

        employerProfile.setUser(user);
        if (employerProfile.getAverageRating() == null) {
            employerProfile.setAverageRating(0.0);
        }

        if (employerProfile.getTotalJobsPosted() == null) {
            employerProfile.setTotalJobsPosted(0);
        }
        return EmployerProfileMapper.toDTO(employerProfileRepository.save(employerProfile));

    }

    @Override
    public EmployerProfileDTO getByUserId(Long userId) {

        EmployerProfile profile = employerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado"));

        return EmployerProfileMapper.toDTO(profile);
    }

    @Override
    public EmployerProfileDTO updateProfile(Long userId, EmployerProfileDTO dto) {

        EmployerProfile profile = employerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado"));

        EmployerProfileMapper.updateEntityFromDTO(dto, profile);

        return EmployerProfileMapper.toDTO(
                employerProfileRepository.save(profile)
        );
    }

    private String extractEmail(Authentication auth) {

        Object principal = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {

            System.out.println("JWT COMPLETO: " + jwt.getClaims());

            return jwt.getClaim("email"); 
        }

        return auth.getName();
    }
}
