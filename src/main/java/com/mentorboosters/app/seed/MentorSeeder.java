package com.mentorboosters.app.seed;

import com.mentorboosters.app.enumUtil.AccountStatus;
import com.mentorboosters.app.enumUtil.ApprovalStatus;
import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.exceptionHandling.ResourceAlreadyExistsException;
import com.mentorboosters.app.model.FixedTimeSlotNew;
import com.mentorboosters.app.model.MentorProfile;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.repository.MenteeProfileRepository;
import com.mentorboosters.app.repository.MentorProfileRepository;
import com.mentorboosters.app.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.mentorboosters.app.util.Constant.ALREADY_REGISTERED_EMAIL;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorSeeder implements CommandLineRunner {

    private final MentorProfileRepository mentorProfileRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final MenteeProfileRepository menteeProfileRepository;

    @Override
    public void run(String... args) {

        List<MentorSeederDTO> mentorsToSeed = List.of(
                MentorSeederDTO.builder()
                        .mentorEmail("yskarthik.b@gmail.com")
                        .phone("9999999999")
                        .timezone("Canada/Central")
                        .password("karthik@123")
                        .name("Karthik Bairu")
                        .profileUrl("https://mentorbooster-dev.s3.eu-north-1.amazonaws.com/mentor-images/06a2505e-5bca-47bf-9ec6-d91e9015c45b-karthik.png")
                        .yearsOfExperience("15")
                        .categories(List.of("Entrepreneurship"))
                        .summary("Karthik Bairu is a distinguished startup mentor at T-Hub, India’s largest startup incubator, where he provides strategic guidance to over 15 early-stage and growth-stage startups. With a proven track record as a three-time entrepreneur and an accomplished angel investor, Karthik brings a unique blend of hands-on business experience and investment insight. He is a gold medalist in Artificial Intelligence, and his expertise spans across emerging technologies, product innovation, and go-to-market strategy. Karthik is highly regarded for his ability to help founders refine their business models, scale sustainably, and prepare for successful fundraising.")
                        .description("Mentor at T-Hub | 3X Entrepreneur | AI Gold Medalist | Angel Investor | Advisor to 15+ Startups")
                        .amount(2500.0)
                        .timeSlots(List.of("18:00", "15:00"))
                        .build(),

                MentorSeederDTO.builder()
                        .mentorEmail("govind@gowin.in")
                        .phone("8888888888")
                        .timezone("Canada/Central")
                        .password("govind@123")
                        .name("Govind Babu")
                        .profileUrl("https://mentorbooster-dev.s3.eu-north-1.amazonaws.com/mentor-images/dc5656d8-8df1-4829-9e7b-799598df17a8-govind.png")
                        .yearsOfExperience("18")
                        .categories(List.of("Marketing"))
                        .summary("Govind Babu is a seasoned sales and leadership expert with over 18 years of global experience across leading companies like Tech Mahindra, Sify, Birlasoft, Aditya Birla (USA), and Synechron. An engineer by background and MBA graduate from Symbiosis, Pune, he has trained over 15,000 professionals across 7 countries. He is the co-author of the book “Life is Fundamentally Management” and currently serves as Managing Partner at EMP GoWin Global (Dubai) and GoWin Search (USA). A 3-time President of BNI in India and Dubai, Govind is deeply passionate about mentoring startup founders, building high-impact sales strategies, and driving growth for SMEs and enterprises across India, the Middle East, and Asia-Pacific.")
                        .description("Mentor | Sales Leadership Expert | 3X Entrepreneur | Author | International Trainer | 18+ Years of Global Experience | Coached 15K+ Sales Professionals | Ex-President, BNI India & Dubai")
                        .amount(3000.0)
                        .timeSlots(List.of("17:00", "20:00"))
                        .build()

// resume yrl: https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-resumes/9434cebc-6df8-4ddc-a9c8-0b7ea0a7b93c-Satyendra Kumar Singh_One Pager.pdf
// linked in url of stayen: https://www.linkedin.com/in/satyendra-kumar-singh-business-mentor-career-strategist-55b2b97/
//                MentorSeederDTO.builder()
//                        .mentorEmail("satyen.trainer@gmail.com")
//                        .phone("917379285472")
//                        .timezone("Asia/Shanghai")
//                        .password("satyen@123")
//                        .name("Satyendra Kumar Singh")
//                        .profileUrl("https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-images/d50d5bb7-4e6e-47af-9b32-43a288191c6a-Passport Pic.jpg")
//                        .yearsOfExperience("25")
//                        .categories(List.of("Marketing", "Entrepreneurship"))
//                        .summary("Satyendra K. Singh is a dynamic mentor and advisor with extensive experience in guiding over 100 startups and businesses towards growth and success. Currently serving as a Mentor at MAARG (Govt. of India), he plays a pivotal role in nurturing entrepreneurial talent. Satyendra is deeply involved in empowering emerging entrepreneurs. Additionally, he serves as an Advisory Board Member for Innovation and Incubation Councils at educational institutions, further contributing to the growth of innovation ecosystems. He regularly conducts workshops through the Institution’s Innovation Council (IIC), equipping future innovators with practical insights and tools for success.")
//                        .description("Mentor | Mentoring 100+ Startups & Businesses | Career Strategist - Counselled 50000+ students | Academic Advisor @ Educational Institutes | Avid Writer - Published 3 Poetry Titles and still writing...")
//                        .amount(2500.0)
//                        .timeSlots(List.of("19:00"))
//                        .build()
        );

        for (MentorSeederDTO dto : mentorsToSeed) {
            if (mentorProfileRepository.existsByEmailOrPhone(dto.getMentorEmail(), dto.getPhone())) {
                log.warn("⚠️ Mentor with email {} or phone {} already exists. Skipping seeding.", dto.getMentorEmail(), dto.getPhone());
                continue;
            }
            if (menteeProfileRepository.existsByEmail(dto.getMentorEmail())){
                log.warn("⚠️ Mentor with email {} already registered as mentee. So skipped seeding.", dto.getMentorEmail());
                continue;
            }

            try {
                ZoneId zoneId = ZoneId.of(dto.getTimezone());
                LocalDate today = LocalDate.now(zoneId);
                String hashedPassword = passwordEncoder.encode(dto.getPassword());

                MentorProfile mentor = MentorProfile.builder()
                        .name(dto.getName())
                        .email(dto.getMentorEmail())
                        .phone(dto.getPhone())
                        .linkedinUrl("https://linkedin.com/in/sampleprofile") // 🔧 placeholder
                        .profileUrl(dto.getProfileUrl())
                        .resumeUrl("https://mentorbooster-resumes.s3.amazonaws.com/sample_resume.pdf")
                        .yearsOfExperience(dto.getYearsOfExperience())
                        .password(hashedPassword)
                        .categories(dto.getCategories())
                        .summary(dto.getSummary())
                        .description(dto.getDescription())
                        .amount(dto.getAmount())
                        .terms(true)
                        .termsAndConditions(true)
                        .timezone(dto.getTimezone())
                        .accountStatus(AccountStatus.ACTIVE)
                        .approvalStatus(ApprovalStatus.ACCEPTED)
                        .build();

                List<FixedTimeSlotNew> timeSlots = dto.getTimeSlots().stream().map(timeStr -> {
                    LocalTime localTime = LocalTime.parse(timeStr.trim());
                    ZonedDateTime zdt = ZonedDateTime.of(today, localTime, zoneId);
                    return FixedTimeSlotNew.builder()
                            .timeStart(zdt.toInstant())
                            .mentor(mentor)
                            .build();
                }).toList();

                mentor.setTimeSlots(timeSlots);
                mentorProfileRepository.save(mentor);

                Users user = Users.builder()
                        .emailId(dto.getMentorEmail())
                        .password(hashedPassword)
                        .role(Role.MENTOR)
                        .build();
                usersRepository.save(user);

                log.info("✅ Seeded mentor: {}", dto.getName());

            } catch (Exception e) {
                log.error("❌ Failed to seed mentor {}: {}", dto.getName(), e.getMessage(), e);
            }
        }
    }
}
