package com.mentorboosters.app.seed;

import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.model.MenteeProfile;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.repository.MenteeProfileRepository;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenteeSeeder implements CommandLineRunner {

    private final MenteeProfileRepository menteeProfileRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        seedMentee("Raja", "vaigundaraja.krays@gmail.com", "tttttttttttt", "raja@123");

    }

    private void seedMentee(String name, String email, String phone, String rawPassword) {

        try {

            if (menteeProfileRepository.existsByEmailOrPhone(email, phone)) {
                log.warn("⚠️ Mentee with email {} already exists. Skipped seeding", email);
                return;
            }

            if (usersRepository.existsByEmailId(email)) {
                log.warn("⚠️ User with email {} already exists. Please delete existing data", email);
                return;
            }

            String encodedPassword = passwordEncoder.encode(rawPassword);

            MenteeProfile mentee = MenteeProfile.builder()
                    .name(name)
                    .email(email)
                    .password(encodedPassword)
                    .phone(phone)
                    .goals(List.of("Marketing"))
                    .timeZone("Asia/Calcutta")
                    .status(Constant.ACTIVE)
                    .build();

            menteeProfileRepository.save(mentee);

            Users user = Users.builder()
                    .emailId(email)
                    .password(encodedPassword)
                    .role(Role.USER)
                    .build();

            usersRepository.save(user);

            log.info("✅ Seeded mentee {}", email);

        } catch (Exception e){
            log.error("❌ Failed to seed mentee: {}: {}",email, e.getMessage());
        }
    }
}
