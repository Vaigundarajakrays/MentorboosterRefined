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
                        .currency("CAD")
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
                        .currency("CAD")
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
                        .currency("CAD")
                        .timeSlots(List.of("19:00"))
                        .build(),

                MentorSeederDTO.builder()
                        .mentorEmail("alex.velazquez@gmail.com")
                        .phone("7777777777")
                        .timezone("America/Toronto")
                        .password("alex@123")
                        .name("Alex Velazquez")
                        .profileUrl("https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-images/c94b6501-f679-4d3d-9725-b7a52fbd74a2-alex.jpg")
                        .yearsOfExperience("12")
                        .categories(List.of("Marketing", "Entrepreneurship"))
                        .summary("Alex Velazquez is a seasoned software engineer and interview coach with over a decade of experience at Google. During his tenure, he led 100+ technical interviews, served on the Early Career Hiring Committee, and mentored students through workshops across North America. Alex is passionate about helping early career talent break into top tech companies like Google, Meta, Amazon, and IBM. He left Big Tech to focus on mentoring full-time and now runs 'Next Offer,' a coaching program designed to equip aspiring software engineers with the tools to stand out in the competitive job market. Through personalized coaching, resume revamps, mock interviews, and strategic job search guidance, Alex helps mentees build confidence, refine their skills, and secure offers at leading technology firms. His mission is simple: to bridge the gap between talent and opportunity by teaching mentees how to think like an interviewer, communicate effectively, and excel in the hiring process.")
                        .description("Software Engineer Mentor | Ex-Google | SWE Interview Coach")
                        .amount(150.0)
                        .currency("CAD")
                        .timeSlots(List.of("16:00", "21:00"))
                        .build(),

                MentorSeederDTO.builder()
                        .mentorEmail("roberta.basili@gmail.com")
                        .phone("6666666666")
                        .timezone("Europe/Amsterdam")
                        .password("roberta@123")
                        .name("Roberta Basili")
                        .profileUrl("https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-images/a4501a49-11d9-4161-96f5-40ab955c5ed2-basili.jpg")
                        .yearsOfExperience("10")
                        .categories(List.of("Marketing", "Entrepreneurship"))
                        .summary("Roberta Basili is an experienced Career & Life Coach specializing in guiding expats and international professionals to find fulfilling, high-income careers abroad. Based in Amsterdam and certified by Erickson Coaching International, Roberta combines her background as a senior recruiter with her expertise as a professional coach to help clients transition careers, industries, or countries with clarity and confidence. Having coached over 100 professionals from 24+ nationalities, Roberta understands the unique challenges faced by expats who feel stuck in unfulfilling roles. Her mission is to empower professionals to overcome limiting beliefs, navigate complex job markets, and create careers that are not only financially rewarding but also deeply meaningful.")
                        .description("The Expats’ Career Coach | Career & Life Coach | Former Recruiter")
                        .amount(160.0)
                        .currency("USD")
                        .timeSlots(List.of("15:00", "20:00"))
                        .build(),

                MentorSeederDTO.builder()
                        .mentorEmail("ctckohli@outlook.com")
                        .phone("918882279758")
                        .timezone("Asia/Kolkata")
                        .password("tarun@123")
                        .name("Tarun Kohli")
                        .profileUrl("https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-images/b9b4e112-1d8e-4592-ad12-c68f49ab4e62-TARUN_KOHLI_PHOTO_optimized_100 copy1.JPG")
                        .resumeUrl("https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-resumes/8f4514d3-9ef0-4c2a-be88-c64a65717144-T K Advisors Profile.pdf")
                        .linkedinUrl("https://www.linkedin.com/in/tkadvisors/")
                        .yearsOfExperience("32")
                        .categories(List.of("Marketing", "Entrepreneurship"))
                        .summary("Tarun Kohli is a senior management consultant and marketing leader with over 32 years of cross-industry experience. He has held leadership roles spanning marketing, international business development, channel establishment, strategic planning, operations management, and customer relationship management. As founder of T K ADVISORS and a seasoned fractional CMO, Tarun helps businesses—especially SMEs and startups—build go-to-market strategies, establish distribution channels, structure partnerships, and improve sales and marketing effectiveness. He combines analytical thinking with hands-on execution and is experienced in leadership assessment, gap analysis, and training programs.")
                        .description("Fractional CMO | Senior Management Consultant & Advisor | SME/Startup Coach")
                        .amount(100.0)
                        .currency("INR")
                        .timeSlots(List.of("01:00"))
                        .build(),

                MentorSeederDTO.builder()
                        .mentorEmail("aojekunle@yahoo.com")
                        .phone("999999000065")
                        .timezone("America/Toronto")
                        .password("azeezat@123")
                        .name("Azeezat Ojekunle")
                        .profileUrl("https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-images/716e3771-6dbc-4e4f-a131-ae476c83783a-Azeezat Ojekunle.jpg")
                        .yearsOfExperience("15+")
                        .categories(List.of("Finance"))
                        .summary("Azeezat Ojekunle is a finance professional and business strategist with over 15 years of experience guiding entrepreneurs and small business owners toward sustainable growth. As the Founder of Mentor Boosters, she has built a platform dedicated to connecting experienced mentors with business owners seeking clarity, structure, and profitability. Azeezat’s expertise lies in simplifying complex financial concepts and turning them into actionable business strategies. She has advised startups and SMEs on financial planning, budgeting, funding strategies, and leadership development. Known for her practical and empathetic mentoring approach, Azeezat helps entrepreneurs make informed decisions, improve cash flow, and align financial success with business vision. Her mentorship empowers business owners to gain confidence in managing finances, scaling operations, and building resilience in dynamic markets.")
                        .description("Founder of Mentor Boosters | Finance Leader | Business Strategist | Mentor for Entrepreneurs | Advocate for Financial Empowerment")
                        .amount(150.0)
                        .currency("CAD")
                        .timeSlots(List.of("16:00", "19:00"))
                        .build(),

                MentorSeederDTO.builder()
                        .mentorEmail("touchinfinitynow@gmail.com")
                        .phone("456357836483")
                        .timezone("America/New_York")
                        .password("avinash@123")
                        .name("Avinash Mahalingam")
                        .profileUrl("https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-images/1af23244-0a9c-4be8-b8dd-240d5ac8e6e3-avinash.jpg")
                        .yearsOfExperience("15")
                        .categories(List.of("Product Development", "Entrepreneurship"))
                        .summary("Avinash Mahalingam is a strategic product leader and mentor with deep expertise in AI Product Management, Business Strategy, and Leadership Development. With extensive experience across technology, innovation, and commercial growth, he helps professionals and founders bridge the gap between product vision and market success. As a mentor, Avinash focuses on empowering individuals to think strategically, build scalable products, and lead teams with clarity and impact. His approach combines technical excellence with human-centered leadership—helping mentees align their career goals, mindset, and performance to thrive in dynamic business environments. Avinash has guided numerous professionals and organizations in shaping product strategy, optimizing sales execution, and creating value-driven business models in AI and technology domains.")
                        .description("AI Product Leader | Business Strategist | Growth Advisor | Leadership Mentor")
                        .amount(200.0)
                        .currency("USD")
                        .timeSlots(List.of("13:00", "15:00"))
                        .linkedinUrl("https://www.linkedin.com/in/avinashmahalingam/")
                        .build(),

                MentorSeederDTO.builder()
                        .mentorEmail("shwetasapra98@gmail.com")
                        .phone("19059214552")
                        .timezone("America/Toronto") // EST timezone
                        .password("shweta@123")
                        .name("Shweta Sapra")
                        .profileUrl("https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-images/42074eb2-a947-44fe-af79-6b935b5c6800-shweta.jpg") // <-- replace with actual image link
                        .yearsOfExperience("8.5")
                        .categories(List.of("Marketing"))
                        .summary("Shweta Sapra is a career coach and mentor with over 8.5 years of experience in marketing, analytics, and higher education consulting. She helps professionals and students navigate career transitions, build impactful resumes, and prepare for MBA entrance exams. Her mentoring approach blends data-driven insights with empathy, helping mentees gain clarity, confidence, and career direction.")
                        .description("Career Coach | Marketing & Analytics Expert | MBA Preparation Mentor")
                        .amount(80.0)
                        .currency("CAD")
                        .timeSlots(List.of("01:00"))
                        .build(),

                MentorSeederDTO.builder()
                        .mentorEmail("gaurav@amgvp.com")
                        .phone("16477649590")
                        .timezone("America/New_York")
                        .password("gaurav@123")
                        .name("Gaurav Bansal")
                        .profileUrl("https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-images/f41800b8-8ca1-4d06-81f1-9e5c945195f9-Gaurav Bansal_Headshot.jpg")
                        .resumeUrl("https://mentorbooster-prod.s3.ca-central-1.amazonaws.com/mentor-resumes/e903aa8c-e19b-47fc-99ef-a35d0baad001-Gaurav Bansal_Resume.pdf")
                        .linkedinUrl("https://www.linkedin.com/in/gauravbansalventurecapital/")
                        .yearsOfExperience("13")
                        .categories(List.of("Finance", "Entrepreneurship"))
                        .summary("Gaurav is a tech investor and advisor with a portfolio of over 35+ VC investments across 10 countries and experience in US$2Bn+ worth of IPOs, M&A, and private equity deals. He is the founder & CEO at AMG Venture Partners where he manages his Family investment portfolio (50+ startups and member of 520+ global angel syndicates). Additionally, Gaurav serves as an Expert in Residence and Mentor across startup programs in North America, Europe, and LATAM, and specializes in investment readiness, AI/ML strategy, and global expansion. Gaurav is also an anonymous Evaluator for various Government grant programs in North America and Europe. Gaurav has held key roles at multiple venture capital funds and investment banks, bringing deep sector expertise in MedTech, Robotics, Fintech, B2B SaaS, AI/ML and Sustainability. Gaurav loves to travel (12+ countries), has worked across North America, Asia and Eu/UK markets and has a big network of investors, founders, and senior industry veterans.")
                        .description("Venture Capital & Startup Growth Advisor | Seed-Stage Investor | AI / SaaS Specialist")
                        .amount(300.0)
                        .currency("USD")
                        .timeSlots(List.of("13:00", "09:00"))
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
                        .currency(dto.getCurrency())
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
        if (dto.getMentorEmail().equals("alex.velazquez@gmail.com")) {
            return List.of(
                    new Skill("Software Engineering & Development",
                            List.of("Proven expertise in algorithms, data structures, and large-scale systems.")),
                    new Skill("Interview Preparation",
                            List.of("Conducted 100+ interviews at Google, with deep knowledge of technical hiring processes.")),
                    new Skill("Resume & LinkedIn Optimization",
                            List.of("Helps mentees craft standout profiles in competitive markets.")),
                    new Skill("Mock Interviews & Feedback",
                            List.of("Simulates real interview environments to improve performance.")),
                    new Skill("Career Strategy",
                            List.of("Guides mentees on job search approaches, referrals, and offer negotiation.")),
                    new Skill("Teaching & Mentorship",
                            List.of("Experienced coach at universities, bootcamps, and initiatives like Google CodeNext.")),
                    new Skill("Problem Solving & Algorithms",
                            List.of("Strong foundation in solving coding challenges and technical problems."))
            );
        }
        if (dto.getMentorEmail().equals("satyen.trainer@gmail.com")) {
            return List.of(
                    new Skill("Startup & Business Mentoring", List.of("Advises entrepreneurs and early-stage ventures on strategy, structure, and sustainable business models.")),
                    new Skill("Career Strategy & Planning", List.of("Helps students and professionals map meaningful career paths through structured, personalized planning.")),
                    new Skill("Academic Advising & Training", List.of("Collaborates with educational institutions to deliver training programs and workshops on skill development and goal setting.")),
                    new Skill("Motivational Counselling & Public Speaking", List.of("Inspires individuals through motivational sessions focused on mindset shifts, confidence building, and overcoming barriers.")),
                    new Skill("Content Creation & Creative Writing", List.of("Published poet and author, skilled in crafting impactful literature across Hindi and English, with a focus on personal growth and self-reflection.")),
                    new Skill("Skill Development & Capacity Building", List.of("Designs and delivers modules that focus on practical life skills, professional readiness, and personal effectiveness.")),
                    new Skill("Empathy-Driven Leadership", List.of("Mentorship approach rooted in empathy, lifelong learning, and a strong belief in the transformative power of clarity and inner drive."))
            );
        }
        if (dto.getMentorEmail().equals("roberta.basili@gmail.com")) {
            return List.of(
                    new Skill("Expat Career Coaching",
                            List.of("Supporting professionals in navigating global job markets.")),
                    new Skill("Career Change Consulting",
                            List.of("Helping clients pivot into new roles, industries, and countries.")),
                    new Skill("Resume & LinkedIn Optimization",
                            List.of("Crafting standout applications tailored for global recruiters.")),
                    new Skill("Interview Preparation",
                            List.of("Providing insider recruiter strategies and mock interviews.")),
                    new Skill("Leadership Development",
                            List.of("Guiding professionals to grow into leadership and management roles.")),
                    new Skill("Negotiation & Strategy",
                            List.of("Coaching on salary, benefits, and career advancement planning.")),
                    new Skill("Diversity, Equity & Inclusion Advocacy",
                            List.of("Championing inclusive hiring and workplace practices.")),
                    new Skill("Cross-Cultural Career Navigation",
                            List.of("Helping expats adapt professionally and culturally to new environments."))
            );
        }
        if (dto.getMentorEmail().equals("tarun.kohli@gmail.com")) {
            return List.of(
                    new Skill("International Business Development",
                            List.of("Led cross-border expansion projects across Asia, Europe, and Middle East.",
                                    "Expertise in building international partnerships and distribution channels.")),

                    new Skill("Marketing & Go-to-Market Strategy",
                            List.of("Designed and executed GTM strategies for SMEs and startups.",
                                    "Specialized in positioning, branding, and customer acquisition.")),

                    new Skill("Sales & Channel Development",
                            List.of("Established dealer and distributor networks across multiple industries.",
                                    "Developed high-performing sales teams with structured processes.")),

                    new Skill("Management Consulting",
                            List.of("Advised clients in automotive, logistics, telecom, and construction sectors.",
                                    "Focused on operational efficiency, growth strategy, and turnaround management.")),

                    new Skill("Leadership & Executive Coaching",
                            List.of("Guided senior leaders through personalized coaching programs.",
                                    "Helps executives strengthen decision-making and leadership presence.")),

                    new Skill("Gap Assessment & Leadership Assessment",
                            List.of("Conducts structured leadership gap analysis for SMEs and corporates.",
                                    "Identifies organizational skill gaps and provides development roadmaps.")),

                    new Skill("Strategic Planning & Operations Management",
                            List.of("Specialized in long-term business planning and execution frameworks.",
                                    "Improved operational processes for multinational and SME clients.")),

                    new Skill("Cross-cultural & Cross-functional Coordination",
                            List.of("Worked with global teams across Japan, India, Europe, and the Middle East.",
                                    "Strong experience in managing diverse cultural and functional teams.")),

                    new Skill("Market Research & Entry Strategy",
                            List.of("Designed market-entry strategies for international companies entering India.",
                                    "Expert in competitive analysis, market sizing, and feasibility studies.")),

                    new Skill("Training, Workshops & Guest Faculty",
                            List.of("Delivered 100+ workshops on business development, leadership, and marketing.",
                                    "Served as guest faculty at leading management institutes."))

            );
        }
        if (dto.getMentorEmail().equals("aojekunle@yahoo.com")) {
            return List.of(
                    new Skill("Financial Planning & Business Strategy",
                            List.of("Helping business owners design sustainable financial structures, budgets, and long-term growth plans.")),
                    new Skill("Entrepreneurial Leadership",
                            List.of("Guiding entrepreneurs to lead effectively and make strategic business decisions with confidence.")),
                    new Skill("SME Growth Consulting",
                            List.of("Providing tailored mentoring for small and medium businesses to improve profitability and scalability.")),
                    new Skill("Mentorship & Coaching",
                            List.of("Empowering professionals with actionable insights, accountability, and structured mentorship for career and business success.")),
                    new Skill("Financial Literacy & Advisory",
                            List.of("Simplifying complex financial topics into easy, practical guidance for better decision-making.")),
                    new Skill("Business Development & Expansion Strategy",
                            List.of("Advising founders on how to structure, fund, and scale their ventures responsibly.")),
                    new Skill("Strategic Problem-Solving",
                            List.of("Applying analytical and structured thinking to overcome financial and business challenges.")),
                    new Skill("Leadership & Team Development",
                            List.of("Building entrepreneurial confidence and leadership through focused mentoring and mindset transformation."))
            );
        }

        if (dto.getMentorEmail().equals("touchinfinitynow@gmail.com")) {
            return List.of(
                    new Skill("AI Product Management",
                            List.of("Guiding professionals to define, build, and scale AI-driven products that create measurable business impact.",
                                    "Helping mentees bridge the gap between product vision and practical execution using AI technologies.")),

                    new Skill("Product Strategy & Innovation",
                            List.of("Coaching mentees on crafting actionable product roadmaps and defining go-to-market strategies.",
                                    "Teaching how to align cross-functional teams and drive innovation through structured experimentation.")),

                    new Skill("Business Strategy & Growth",
                            List.of("Helping leaders and founders identify growth levers, create scalable frameworks, and make data-driven decisions.",
                                    "Providing mentorship on building sustainable business models and long-term value creation.")),

                    new Skill("Sales & Market Execution",
                            List.of("Mentoring professionals on B2B and enterprise sales strategy, customer success, and market expansion.",
                                    "Improving client relationship management and aligning sales processes with business goals.")),

                    new Skill("Leadership & Team Development",
                            List.of("Helping leaders inspire their teams, communicate vision clearly, and foster trust-based collaboration.",
                                    "Guiding professionals to develop the mindset and behaviors that drive performance and innovation.")),

                    new Skill("Self-Leadership & Personal Mastery",
                            List.of("Coaching individuals to cultivate emotional intelligence, resilience, and self-awareness for career success.",
                                    "Empowering mentees to align mindset, goals, and actions for long-term personal and professional growth."))
            );
        }

        if (dto.getMentorEmail().equals("shwetasapra98@gmail.com")) {
            return List.of(
                    new Skill("Career Transition & Resume Building",
                            List.of(
                                    "Helping professionals restructure their resumes to highlight achievements and align profiles with targeted roles.",
                                    "Guiding mentees through smooth career switches by identifying transferable skills and market-fit positioning."
                            )),

                    new Skill("Marketing & Analytics Strategy",
                            List.of(
                                    "Mentoring professionals on marketing concepts, campaign performance analysis, and analytics tools to enhance decision-making.",
                                    "Providing insights into how data-driven marketing strategies can drive measurable business growth."
                            )),

                    new Skill("Career Change & Personal Branding",
                            List.of(
                                    "Coaching professionals to rebrand themselves for new industries or roles using personalized strategies.",
                                    "Helping mentees build authentic personal brands that communicate their value and expertise effectively."
                            )),

                    new Skill("Higher Education (MBA Preparation)",
                            List.of(
                                    "Supporting mentees in crafting goal-oriented MBA preparation plans and optimizing their academic and professional profiles.",
                                    "Mentoring students on exam strategies, SOPs, and interview preparation for global MBA programs."
                            )),

                    new Skill("Professional Development & Interview Coaching",
                            List.of(
                                    "Training mentees in behavioral interview techniques, effective communication, and confidence building.",
                                    "Providing actionable feedback to improve articulation, clarity, and executive presence during interviews."
                            )),

                    new Skill("Pharma Industry Mentorship",
                            List.of(
                                    "Helping professionals from pharma backgrounds transition into marketing, analytics, or consulting roles.",
                                    "Offering career direction for pharma professionals seeking to expand into data-driven or strategic domains."
                            ))
            );
        }

        if (dto.getMentorEmail().equals("gaurav@amgvp.com")) {
            return List.of(
                    new Skill("Investment Readiness & Fundraising Strategy",
                            List.of("Advising startups on crafting pitch decks, financial models, and valuation strategies to attract investors.",
                                    "Guiding founders on building datarooms, managing investor CRM, and understanding how VCs and angels evaluate deals.")),

                    new Skill("Startup Growth & Scaling (AI / SaaS)",
                            List.of("Helping early-stage companies achieve product-market fit and accelerate customer acquisition.",
                                    "Designing sustainable growth roadmaps tailored for AI and SaaS-driven startups.")),

                    new Skill("Business Model & Go-to-Market Strategy",
                            List.of("Mentoring founders on refining their value proposition, monetization strategy, and pricing models.",
                                    "Advising startups on international expansion and go-to-market execution across multiple regions.")),

                    new Skill("Investor & Partner Network Access",
                            List.of("Providing access to global investor networks including angel syndicates, VC funds, and strategic partners.",
                                    "Leveraging extensive connections across North America, Europe, and Asia to open doors for fundraising and collaborations.")),

                    new Skill("Leadership & Founding Team Coaching",
                            List.of("Supporting founders in leadership transitions, team structuring, and scaling company culture.",
                                    "Helping leaders improve decision-making, communication, and operating efficiency in high-growth environments.")),

                    new Skill("M&A / Exit Strategy Consultation",
                            List.of("Advising on acquisition readiness, strategic partnerships, and long-term exit planning.",
                                    "Guiding founders through term sheet negotiation, legal documentation, and investor communication during M&A discussions."))
            );
        }






        return List.of(
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

}
