package com.mentorboosters.app.seed;

import com.mentorboosters.app.enumUtil.AccountStatus;
import com.mentorboosters.app.enumUtil.ApprovalStatus;
import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.exceptionHandling.ResourceAlreadyExistsException;
import com.mentorboosters.app.model.FixedTimeSlotNew;
import com.mentorboosters.app.model.MentorProfile;
import com.mentorboosters.app.model.Skill;
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
import java.util.Objects;

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
                        .build(),

                // Password of styen: $2a$10$QUB1hFb0W3oz2IvKjiQfsuhwSW6sHS73ZDI3JYSSUCAsAzZOaq2DG
                MentorSeederDTO.builder()
                        .mentorEmail("satyen.trainer@gmail.com")
                        .phone("917379285472")
                        .timezone("Asia/Shanghai")
                        .password("satyen@123")
                        .name("Satyendra Kumar Singh")
                        .profileUrl("https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-images/29f7b20c-e20c-4ff2-9bc6-3157b2a99ba5-Satyendra Kumar Singh.png")
                        .linkedinUrl("https://www.linkedin.com/in/satyendra-kumar-singh-business-mentor-career-strategist-55b2b97/")
                        .resumeUrl("https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-resumes/9434cebc-6df8-4ddc-a9c8-0b7ea0a7b93c-Satyendra Kumar Singh_One Pager.pdf")
                        .yearsOfExperience("25")
                        .categories(List.of("Marketing", "Entrepreneurship"))
                        .summary("Satyendra Kumar Singh is a seasoned business mentor and career strategist with over two decades of experience guiding students, professionals, and startups. With a strong background in academic advising, startup mentoring, motivational counselling, and skill development training, he has empowered countless individuals to achieve clarity in their personal and professional goals. His work spans institutions, startups, and government organizations, where he brings a structured, empathetic, and transformative approach. In addition to his mentoring work, Satyendra is a prolific author with three published poetry titles and ongoing projects in motivational writing and fiction. His mentorship is grounded in real-world insights and a passion for enabling growth through purpose-driven guidance.")
                        .description("Mentor | Mentoring 100+ Startups & Businesses | Career Strategist - Counselled 50000+ students | Academic Advisor @ Educational Institutes | Avid Writer - Published 3 Poetry Titles and still writing...")
                        .amount(2500.0)
                        .timeSlots(List.of("19:00"))
                        .build()
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

            List<Skill> skills = getSkills(dto);

            try {
                ZoneId zoneId = ZoneId.of(dto.getTimezone());
                LocalDate today = LocalDate.now(zoneId);
                String hashedPassword = passwordEncoder.encode(dto.getPassword());

                String defaultLinkedinUrl = "https://linkedin.com/in/sampleprofile";
                String defaultResumeUrl = "https://mentorbooster-resumes.s3.amazonaws.com/sample_resume.pdf";
                if(dto.getResumeUrl() != null && dto.getLinkedinUrl() != null){
                    defaultLinkedinUrl = dto.getLinkedinUrl();
                    defaultResumeUrl = dto.getResumeUrl();
                }

                MentorProfile mentor = MentorProfile.builder()
                        .name(dto.getName())
                        .email(dto.getMentorEmail())
                        .phone(dto.getPhone())
                        .linkedinUrl(defaultLinkedinUrl) // 🔧 placeholder
                        .profileUrl(dto.getProfileUrl())
                        .resumeUrl(defaultResumeUrl)
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
                        .skills(skills)
                        .build();

                // List is mutable, so even though we put .skills(skills) before, we later update those skills with mentor
                for (Skill skill : skills) {
                    skill.setMentorProfile(mentor);
                }


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

    private static List<Skill> getSkills(MentorSeederDTO dto) {
        List<Skill> skills = null;
        if(dto.getMentorEmail().equals("satyen.trainer@gmail.com")){
            skills = List.of(
                    new Skill("Startup & Business Mentoring", List.of("Advises entrepreneurs and early-stage ventures on strategy, structure, and sustainable business models.")),
                    new Skill("Career Strategy & Planning", List.of("Helps students and professionals map meaningful career paths through structured, personalized planning.")),
                    new Skill("Academic Advising & Training", List.of("Collaborates with educational institutions to deliver training programs and workshops on skill development and goal setting.")),
                    new Skill("Motivational Counselling & Public Speaking", List.of("Inspires individuals through motivational sessions focused on mindset shifts, confidence building, and overcoming barriers.")),
                    new Skill("Content Creation & Creative Writing", List.of("Published poet and author, skilled in crafting impactful literature across Hindi and English, with a focus on personal growth and self-reflection.")),
                    new Skill("Skill Development & Capacity Building", List.of("Designs and delivers modules that focus on practical life skills, professional readiness, and personal effectiveness.")),
                    new Skill("Empathy-Driven Leadership", List.of("Mentorship approach rooted in empathy, lifelong learning, and a strong belief in the transformative power of clarity and inner drive."))
            );
        } else {
            skills = List.of(
                    new Skill("Startup Mentorship & Advisory", List.of("Guiding early-stage and growth-stage startups on business strategy, product-market fit, and scaling.")),
                    new Skill("Artificial Intelligence & Machine Learning", List.of("Deep technical expertise in AI/ML applications, with academic excellence and practical implementation.")),
                    new Skill("Entrepreneurship", List.of("Built and exited three startups, with hands-on experience in founding, growing, and managing ventures.")),
                    new Skill("Fundraising & Investment Strategy", List.of("Experience in angel investing and supporting startups in preparing for venture capital and funding rounds.")),
                    new Skill("Go-to-Market Strategy", List.of("Expertise in market validation, positioning, and customer acquisition for technology products.")),
                    new Skill("Technology & Product Development", List.of("Strong technical foundation with the ability to guide product roadmaps, MVP design, and agile development.")),
                    new Skill("Leadership & Team Building", List.of("Proven ability to build high-performing teams, foster innovation, and cultivate entrepreneurial leadership.")),
                    new Skill("Startup Ecosystem Navigation", List.of("In-depth knowledge of incubator/accelerator programs, government schemes, and startup networks."))
            );
        }
        return skills;
    }
}
