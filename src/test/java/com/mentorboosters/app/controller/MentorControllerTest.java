//package com.mentorboosters.app.controller;
//
//import com.mentorboosters.app.dto.SearchMentorsDTO;
//import com.mentorboosters.app.dto.TopMentorsDTO;
//import com.mentorboosters.app.dto.TopRatedMentorsDTO;
//import com.mentorboosters.app.dto.VerifiedMentorDTO;
//import com.mentorboosters.app.enumUtil.Role;
//import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
//import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
//import com.mentorboosters.app.model.*;
//import com.mentorboosters.app.repository.*;
//import com.mentorboosters.app.response.CommonResponse;
//import com.mentorboosters.app.service.MentorService;
//import com.mentorboosters.app.util.CommonFiles;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.sql.Time;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static com.mentorboosters.app.util.Constant.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class MentorControllerTest {
//
//    @Mock
//    MentorRepository mentorRepository;
//
//    @Mock
//    UsersRepository usersRepository;
//
//    @Mock
//    CategoryRepository categoryRepository;
//
//    @Mock
//    CertificateRepository certificateRepository;
//
//    @Mock
//    ExperienceRepository experienceRepository;
//
//    @Mock
//    FixedTimeSlotRepository fixedTimeSlotRepository;
//
//    @Mock
//    CommonFiles commonFiles;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    MentorService mentorService;
//
//    private Mentor mentor1, mentor2, mentor3, updatedMentor, existingMentor;
//    private Users user1;
//    private Certificate existingCert;
//    private Experience existingExp;
//    private FixedTimeSlot existingSlot1, existingSlot2;
//    private Category updatedCat1, updatedCat2;
//
//    @BeforeEach
//    void setUp() {
//        Certificate cert = Certificate.builder()
//                .id(1L)
//                .name("AWS Certified")
//                .provideBy("Amazon")
//                .createDate("2023-01-01")
//                .imageUrl("https://example.com/certificate.jpg")
//                .build();
//        cert.setCreatedAt(LocalDateTime.now());
//        cert.setUpdatedAt(LocalDateTime.now());
//
//        Experience exp = Experience.builder()
//                .id(1L)
//                .role("Software Engineer")
//                .companyName("Tech Corp")
//                .startDate(LocalDate.of(2020, 1, 1))
//                .endDate(LocalDate.of(2023, 1, 1))
//                .description("Worked on cloud-based solutions.")
//                .build();
//        exp.setCreatedAt(LocalDateTime.now());
//        exp.setUpdatedAt(LocalDateTime.now());
//
//        FixedTimeSlot slot1 = FixedTimeSlot.builder()
//                .id(1L)
//                .timeStart(Time.valueOf("10:00:00").toLocalTime())
//                .timeEnd(Time.valueOf("11:00:00").toLocalTime())
//                .build();
//        slot1.setCreatedAt(LocalDateTime.now());
//        slot1.setUpdatedAt(LocalDateTime.now());
//
//        Category cat1 = Category.builder()
//                .id(1L)
//                .name("Design")
//                .icon("Awesome.code")
//                .build();
//        cat1.setCreatedAt(LocalDateTime.now());
//        cat1.setUpdatedAt(LocalDateTime.now());
//
//        mentor1 = Mentor.builder()
//                .id(1L)
//                .name("Joe1")
//                .email("joe1@gmail.com")
//                .gender("Male")
//                .avatarUrl("https://example.com/avatar.jpg")
//                .bio("Experienced mentor")
//                .role("Software Engineer")
//                .freePrice(50.0)
//                .freeUnit("hour")
//                .verified(true)
//                .rate(4.9)
//                .numberOfMentoree(20)
//                .certificates(List.of(cert))
//                .experiences(List.of(exp))
//                .timeSlots(List.of(slot1))
//                .categories(List.of(cat1))
//                .build();
//        mentor1.setCreatedAt(LocalDateTime.now());
//        mentor1.setUpdatedAt(LocalDateTime.now());
//
//        mentor2 = Mentor.builder()
//                .id(2L)
//                .name("Joe2")
//                .email("joe2@gmail.com")
//                .gender("Male")
//                .avatarUrl("https://example.com/avatar.jpg")
//                .bio("Experienced mentor")
//                .role("Software Engineer")
//                .freePrice(50.0)
//                .freeUnit("hour")
//                .verified(true)
//                .rate(4.9)
//                .numberOfMentoree(20)
//                .certificates(List.of(cert))
//                .experiences(List.of(exp))
//                .timeSlots(List.of(slot1))
//                .categories(List.of(cat1))
//                .build();
//        mentor2.setCreatedAt(LocalDateTime.now());
//        mentor2.setUpdatedAt(LocalDateTime.now());
//
//        Certificate cert3 = Certificate.builder()
//                .name("AWS Certified")
//                .provideBy("Amazon")
//                .createDate("2023-01-01")
//                .imageUrl("https://example.com/certificate.jpg")
//                .build();
//
//        Experience exp3 = Experience.builder()
//                .role("Software Engineer")
//                .companyName("Tech Corp")
//                .startDate(LocalDate.of(2020, 1, 1))
//                .endDate(LocalDate.of(2023, 1, 1))
//                .description("Worked on cloud-based solutions.")
//                .build();
//
//        FixedTimeSlot slot3 = FixedTimeSlot.builder()
//                .timeStart(LocalTime.of(10, 0))
//                .timeEnd(LocalTime.of(11, 0))
//                .build();
//
//        Category cat3 = Category.builder()
//                .name("Design")
//                .icon("Awesome.code")
//                .build();
//
//        Review review3 = Review.builder()
//                .message("You are good at teaching")
//                .rating(4L)
//                .createdById(1L)
//                .userName("Rekoj")
//                .build();
//
//        mentor3 = Mentor.builder()
//                .name("Joe1")
//                .email("joe1@gmail.com")
//                .gender("Male")
//                .avatarUrl("https://example.com/avatar.jpg")
//                .bio("Experienced mentor")
//                .role("Software Engineer")
//                .freePrice(50.0)
//                .freeUnit("hour")
//                .verified(true)
//                .rate(4.9)
//                .numberOfMentoree(20)
//                .certificates(List.of(cert3))
//                .experiences(List.of(exp3))
//                .timeSlots(List.of(slot3))
//                .reviews(List.of(review3))
//                .categories(List.of(cat3))
//                .build();
//
//        user1 = Users.builder()
//                .name("Joe1")
//                .emailId("joe1@gmail.com")
//                .userName("Joe123")
//                .role(Role.MENTOR)
//                .password("joe@123")
//                .build();
//
//        Certificate updatedCert = Certificate.builder()
//                .id(1L)
//                .name("AWS Certified")
//                .provideBy("Amazon")
//                .createDate("2023-01-01")
//                .imageUrl("https://example.com/certificate.jpg")
//                .build();
//
//        Experience updatedExp = Experience.builder()
//                .id(1L)
//                .role("Software Engineer")
//                .companyName("Tech Corp")
//                .startDate(LocalDate.parse("2020-01-01"))
//                .endDate(LocalDate.parse("2023-01-01"))
//                .description("Worked on cloud-based solutions.")
//                .build();
//
//        FixedTimeSlot updatedSlot1 = FixedTimeSlot.builder()
//                .id(1L)
//                .timeStart(LocalTime.parse("10:00:00"))
//                .timeEnd(LocalTime.parse("11:00:00"))
//                .build();
//
//        FixedTimeSlot updatedSlot2 = FixedTimeSlot.builder()
//                .id(2L)
//                .timeStart(LocalTime.parse("11:00:00"))
//                .timeEnd(LocalTime.parse("12:00:00"))
//                .build();
//
//        updatedCat1 = Category.builder().id(1L).name("Design").icon("Awesome.code").build();
//        updatedCat2 = Category.builder().id(2L).name("English").icon("Awesome.code").build();
//
//        updatedMentor = Mentor.builder()
//                .name("Joe1")
//                .email("joe1@gmail.com")
//                .gender("Male")
//                .avatarUrl("https://example.com/avatar.jpg")
//                .bio("Experienced mentor")
//                .role("Software Engineer")
//                .freePrice(50.0)
//                .freeUnit("hour")
//                .verified(true)
//                .rate(4.9)
//                .numberOfMentoree(20)
//                .certificates(List.of(updatedCert))
//                .experiences(List.of(updatedExp))
//                .timeSlots(List.of(updatedSlot1, updatedSlot2))
//                .categories(List.of(updatedCat1, updatedCat2))
//                .build();
//
//        existingCert = Certificate.builder()
//                .id(1L)
//                .name("Old Cert")
//                .provideBy("Old Provider")
//                .createDate("2022-01-01")
//                .imageUrl("https://old.com/cert.jpg")
//                .build();
//
//        existingExp = Experience.builder()
//                .id(1L)
//                .role("Old Role")
//                .companyName("Old Company")
//                .startDate(LocalDate.parse("2019-01-01"))
//                .endDate(LocalDate.parse("2020-01-01"))
//                .description("Old desc")
//                .build();
//
//        existingSlot1 = FixedTimeSlot.builder()
//                .id(1L)
//                .timeStart(LocalTime.parse("08:00:00"))
//                .timeEnd(LocalTime.parse("09:00:00"))
//                .build();
//
//        existingSlot2 = FixedTimeSlot.builder()
//                .id(2L)
//                .timeStart(LocalTime.parse("09:00:00"))
//                .timeEnd(LocalTime.parse("10:00:00"))
//                .build();
//
//        Category existingCat1 = Category.builder().id(1L).name("Design").icon("Old.icon").build();
//        Category existingCat2 = Category.builder().id(2L).name("English").icon("Old.icon").build();
//
//        existingMentor = Mentor.builder()
//                .id(1L)
//                .name("Old Name")
//                .email("old@mail.com")
//                .gender("Female")
//                .avatarUrl("https://old.com/avatar.jpg")
//                .bio("Old bio")
//                .role("Old Role")
//                .freePrice(10.0)
//                .freeUnit("session")
//                .verified(false)
//                .rate(3.5)
//                .numberOfMentoree(5)
//                .certificates(new ArrayList<>(List.of(existingCert)))
//                .experiences(new ArrayList<>(List.of(existingExp)))
//                .timeSlots(new ArrayList<>(List.of(existingSlot1, existingSlot2)))
//                .categories(new ArrayList<>(List.of(existingCat1, existingCat2)))
//                .build();
//
//
//    }
//
//    @Test
//    void getALlMentorsShouldReturnSuccessfully() throws UnexpectedServerException {
//        when(mentorRepository.findAll()).thenReturn(List.of(mentor1, mentor2));
//
//        CommonResponse<List<Mentor>> response = mentorService.findAllMentorsWithSlots();
//
//        //test
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals("Loaded all the Mentor Details.", response.getMessage());
//
//        List<Mentor> mentors = response.getData();
//        assertEquals(2, mentors.size());
//
//        for (Mentor mentor : mentors) {
//            assertNotNull(mentor.getId());
//            assertNotNull(mentor.getName());
//            assertNotNull(mentor.getEmail());
//            assertNotNull(mentor.getGender());
//            assertNotNull(mentor.getAvatarUrl());
//            assertNotNull(mentor.getBio());
//            assertNotNull(mentor.getRole());
//            assertEquals(50.0, mentor.getFreePrice());
//            assertEquals("hour", mentor.getFreeUnit());
//            assertTrue(mentor.getVerified());
//            assertEquals(4.9, mentor.getRate());
//            assertEquals(20, mentor.getNumberOfMentoree());
//
//            // Check certificates
//            assertNotNull(mentor.getCertificates());
//            assertFalse(mentor.getCertificates().isEmpty());
//            Certificate cert = mentor.getCertificates().get(0);
//            assertEquals("AWS Certified", cert.getName());
//            assertEquals("Amazon", cert.getProvideBy());
//            assertEquals("2023-01-01", cert.getCreateDate());
//            assertEquals("https://example.com/certificate.jpg", cert.getImageUrl());
//
//            // Check experiences
//            assertNotNull(mentor.getExperiences());
//            assertFalse(mentor.getExperiences().isEmpty());
//            Experience exp = mentor.getExperiences().get(0);
//            assertEquals("Software Engineer", exp.getRole());
//            assertEquals("Tech Corp", exp.getCompanyName());
//            assertEquals(LocalDate.of(2020, 1, 1), exp.getStartDate());
//            assertEquals(LocalDate.of(2023, 1, 1), exp.getEndDate());
//            assertEquals("Worked on cloud-based solutions.", exp.getDescription());
//
//            // Check time slots
//            assertNotNull(mentor.getTimeSlots());
//            assertFalse(mentor.getTimeSlots().isEmpty());
//            FixedTimeSlot slot = mentor.getTimeSlots().get(0);
//            assertEquals(LocalTime.of(10, 0), slot.getTimeStart());
//            assertEquals(LocalTime.of(11, 0), slot.getTimeEnd());
//
//            // Check categories
//            assertNotNull(mentor.getCategories());
//            assertFalse(mentor.getCategories().isEmpty());
//            Category cat = mentor.getCategories().get(0);
//            assertEquals("Design", cat.getName());
//            assertEquals("Awesome.code", cat.getIcon());
//
//        }
//
//        verify(mentorRepository, times(1)).findAll();
//    }
//
//
//    @Test
//    void getAllMentorsShouldReturnEmptyList() throws UnexpectedServerException {
//        when(mentorRepository.findAll()).thenReturn(List.of());
//
//        CommonResponse<List<Mentor>> response = mentorService.findAllMentorsWithSlots();
//
//        //test
//        assertNotNull(response);
//        assertTrue(response.getData().isEmpty());
//        assertFalse(response.getStatus());
//        assertEquals("No mentors available", response.getMessage());
//        assertEquals(200, response.getStatusCode());
//
//        verify(mentorRepository, times(1)).findAll();
//    }
//
//    @Test
//    void getAllMentorsShouldThrowException() {
//        when(mentorRepository.findAll()).thenThrow(new RuntimeException("DB failure"));
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            mentorService.findAllMentorsWithSlots();
//        });
//
//        //test
//        assertTrue(exception.getMessage().contains("Error while fetching mentors: "));
//        verify(mentorRepository, times(1)).findAll();
//    }
//
//    @Test
//    void saveMentorShouldSaveSuccessfully() throws UnexpectedServerException {
//
//        when(mentorRepository.existsByEmail(anyString())).thenReturn(false);
//        when(categoryRepository.findByName("Design")).thenReturn(Optional.of(Category.builder().id(1L).name("Design").icon("Awesome.code").build()));
//
//        when(commonFiles.generateAlphaPassword(6)).thenReturn("abc123").thenReturn("user123"); // password & username
//        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("hashedPass123");
//
//        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentor3);
//        when(usersRepository.save(any(Users.class))).thenReturn(user1);
//
//        CommonResponse<Mentor> response = mentorService.saveMentor(mentor3);
//
//        //test
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals("Successfully Added.", response.getMessage());
//        assertEquals(200, response.getStatusCode());
//
//        Mentor saved = response.getData();
//        assertNotNull(saved);
//        assertEquals("Joe1", saved.getName());
//        assertEquals("joe1@gmail.com", saved.getEmail());
//        assertEquals("Male", saved.getGender());
//        assertEquals("Experienced mentor", saved.getBio());
//        assertEquals("https://example.com/avatar.jpg", saved.getAvatarUrl());
//        assertEquals("Software Engineer", saved.getRole());
//        assertEquals(50.0, saved.getFreePrice());
//        assertEquals("hour", saved.getFreeUnit());
//        assertTrue(saved.getVerified());
//        assertEquals(4.9, saved.getRate());
//        assertEquals(20, saved.getNumberOfMentoree());
//
//        // Certificate
//        assertEquals(1, saved.getCertificates().size());
//        Certificate cert = saved.getCertificates().get(0);
//        assertEquals("AWS Certified", cert.getName());
//        assertEquals("Amazon", cert.getProvideBy());
//        assertEquals("2023-01-01", cert.getCreateDate());
//        assertEquals("https://example.com/certificate.jpg", cert.getImageUrl());
//
//        // Experience
//        assertEquals(1, saved.getExperiences().size());
//        Experience exp = saved.getExperiences().get(0);
//        assertEquals("Software Engineer", exp.getRole());
//        assertEquals("Tech Corp", exp.getCompanyName());
//        assertEquals(LocalDate.of(2020, 1, 1), exp.getStartDate());
//        assertEquals(LocalDate.of(2023, 1, 1), exp.getEndDate());
//        assertEquals("Worked on cloud-based solutions.", exp.getDescription());
//
//        // Time slot
//        assertEquals(1, saved.getTimeSlots().size());
//        FixedTimeSlot slot = saved.getTimeSlots().get(0);
//        assertEquals(LocalTime.of(10, 0), slot.getTimeStart());
//        assertEquals(LocalTime.of(11, 0), slot.getTimeEnd());
//
//        // Review
//        assertEquals(1, saved.getReviews().size());
//        Review review = saved.getReviews().get(0);
//        assertEquals("You are good at teaching", review.getMessage());
//        assertEquals(4L, review.getRating());
//        assertEquals(1L, review.getCreatedById());
//        assertEquals("Rekoj", review.getUserName());
//
//        // Category
//        assertEquals(1, saved.getCategories().size());
//        Category cat = saved.getCategories().get(0);
//        assertEquals("Design", cat.getName());
//        assertEquals("Awesome.code", cat.getIcon());
//
//        verify(mentorRepository, times(1)).existsByEmail("joe1@gmail.com");
//        verify(categoryRepository, times(1)).findByName("Design");
//        verify(mentorRepository, times(1)).save(any(Mentor.class));
//        verify(usersRepository, times(1)).save(any(Users.class));
//        verify(commonFiles, times(2)).generateAlphaPassword(6);
//        verify(passwordEncoder, times(1)).encode("abc123");
//        verify(commonFiles, times(1)).sendPasswordToMentor(any(), eq("abc123"));
//    }
//
//    @Test
//    void saveMentorShouldReturnEmailAlreadyExistsIfEmailExists() throws UnexpectedServerException {
//
//        when(mentorRepository.existsByEmail("joe1@gmail.com")).thenReturn(true);
//
//
//        CommonResponse<Mentor> response = mentorService.saveMentor(mentor3);
//
//        // test
//        assertNotNull(response);
//        assertFalse(response.getStatus());
//        assertEquals("Email Already Exists", response.getMessage());
//        assertEquals(403, response.getStatusCode());
//
//        verify(mentorRepository, times(1)).existsByEmail("joe1@gmail.com");
//        verify(mentorRepository, never()).save(any());
//        verify(usersRepository, never()).save(any());
//        verify(commonFiles, never()).sendPasswordToMentor(any(), anyString());
//    }
//
//    @Test
//    void saveMentorShouldThrowException() {
//
//        when(mentorRepository.existsByEmail("joe1@gmail.com")).thenReturn(false);
//        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(Category.builder().id(1L).name("Design").icon("Awesome.code").build()));
//        when(mentorRepository.save(any(Mentor.class))).thenThrow(new RuntimeException("DB error"));
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> mentorService.saveMentor(mentor3));
//
//        //test
//        assertTrue(exception.getMessage().contains("Error while adding mentors: DB error"));
//
//        verify(mentorRepository, times(1)).existsByEmail("joe1@gmail.com");
//        verify(mentorRepository, times(1)).save(any(Mentor.class));
//        verify(usersRepository, never()).save(any());
//        verify(commonFiles, never()).sendPasswordToMentor(any(), anyString());
//    }
//
//    @Test
//    void updateMentorShouldUpdateSuccessfully() throws Exception {
//        Long mentorId = 1L;
//
//        // Mocks
//        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(existingMentor));
//        when(certificateRepository.findById(1L)).thenReturn(Optional.of(existingCert));
//        when(experienceRepository.findById(1L)).thenReturn(Optional.of(existingExp));
//        when(fixedTimeSlotRepository.findById(1L)).thenReturn(Optional.of(existingSlot1));
//        when(fixedTimeSlotRepository.findById(2L)).thenReturn(Optional.of(existingSlot2));
//        when(categoryRepository.findByName("Design")).thenReturn(Optional.of(updatedCat1));
//        when(categoryRepository.findByName("English")).thenReturn(Optional.of(updatedCat2));
//        when(mentorRepository.save(any(Mentor.class))).thenAnswer(inv -> inv.getArgument(0));
//
//
//        CommonResponse<Mentor> response = mentorService.updateMentor(mentorId, updatedMentor);
//
//        // Verify
//        assertNotNull(response);
//        assertEquals("Mentor updated successfully", response.getMessage());
//        assertTrue(response.getStatus());
//        assertEquals(200,response.getStatusCode());
//
//        Mentor result = response.getData();
//
//        assertEquals("Joe1", result.getName());
//        assertEquals("joe1@gmail.com", result.getEmail());
//        assertEquals("Male", result.getGender());
//        assertEquals("https://example.com/avatar.jpg", result.getAvatarUrl());
//        assertEquals("Experienced mentor", result.getBio());
//        assertEquals("Software Engineer", result.getRole());
//        assertEquals(50.0, result.getFreePrice());
//        assertEquals("hour", result.getFreeUnit());
//        assertTrue(result.getVerified());
//        assertEquals(4.9, result.getRate());
//        assertEquals(20, result.getNumberOfMentoree());
//
//        // Certificates
//        assertEquals(1, result.getCertificates().size());
//        Certificate cert = result.getCertificates().get(0);
//        assertEquals(1L, cert.getId());
//        assertEquals("AWS Certified", cert.getName());
//        assertEquals("Amazon", cert.getProvideBy());
//        assertEquals("2023-01-01", cert.getCreateDate());
//        assertEquals("https://example.com/certificate.jpg", cert.getImageUrl());
//        assertEquals(result, cert.getMentor());
//
//        // Experiences
//        assertEquals(1, result.getExperiences().size());
//        Experience exp = result.getExperiences().get(0);
//        assertEquals(1L, exp.getId());
//        assertEquals("Software Engineer", exp.getRole());
//        assertEquals("Tech Corp", exp.getCompanyName());
//        assertEquals(LocalDate.parse("2020-01-01"), exp.getStartDate());
//        assertEquals(LocalDate.parse("2023-01-01"), exp.getEndDate());
//        assertEquals("Worked on cloud-based solutions.", exp.getDescription());
//        assertEquals(result, exp.getMentor());
//
//        // Time Slots
//        assertEquals(2, result.getTimeSlots().size());
//        FixedTimeSlot slot1 = result.getTimeSlots().get(0);
//        FixedTimeSlot slot2 = result.getTimeSlots().get(1);
//
//        assertEquals(1L, slot1.getId());
//        assertEquals(LocalTime.parse("10:00:00"), slot1.getTimeStart());
//        assertEquals(LocalTime.parse("11:00:00"), slot1.getTimeEnd());
//        assertEquals(result, slot1.getMentor());
//
//        assertEquals(2L, slot2.getId());
//        assertEquals(LocalTime.parse("11:00:00"), slot2.getTimeStart());
//        assertEquals(LocalTime.parse("12:00:00"), slot2.getTimeEnd());
//        assertEquals(result, slot2.getMentor());
//
//        // Categories
//        assertEquals(2, result.getCategories().size());
//        Category cat1 = result.getCategories().get(0);
//        Category cat2 = result.getCategories().get(1);
//
//        assertEquals(1L, cat1.getId());
//        assertEquals("Design", cat1.getName());
//        assertEquals("Awesome.code", cat1.getIcon());
//
//        assertEquals(2L, cat2.getId());
//        assertEquals("English", cat2.getName());
//        assertEquals("Awesome.code", cat2.getIcon());
//
//
//        verify(mentorRepository).findById(mentorId);
//        verify(mentorRepository).save(any(Mentor.class));
//        verify(certificateRepository).findById(1L);
//        verify(experienceRepository).findById(1L);
//        verify(fixedTimeSlotRepository).findById(1L);
//        verify(fixedTimeSlotRepository).findById(2L);
//        verify(categoryRepository).findByName("Design");
//        verify(categoryRepository).findByName("English");
//    }
//
//    @Test
//    void updateMentor_shouldThrowResourceNotFoundException_whenMentorNotFound() {
//        Long mentorId = 1L;
//
//        when(mentorRepository.findById(mentorId)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            mentorService.updateMentor(mentorId, updatedMentor);
//        });
//
//        verify(mentorRepository).findById(mentorId);
//    }
//
//    @Test
//    void updateMentor_shouldThrowResourceNotFoundException_whenCertificateNotFound() {
//
//        when(mentorRepository.findById(1L)).thenReturn(Optional.of(existingMentor));
//        when(certificateRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            mentorService.updateMentor(1L, updatedMentor);
//        });
//
//        verify(certificateRepository).findById(1L);
//    }
//
//    @Test
//    void updateMentor_shouldThrowResourceNotFoundException_whenExperienceNotFound() {
//
//        when(mentorRepository.findById(1L)).thenReturn(Optional.of(existingMentor));
//        when(certificateRepository.findById(1L)).thenReturn(Optional.of(existingCert));
//        when(experienceRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            mentorService.updateMentor(1L, updatedMentor);
//        });
//
//        verify(experienceRepository).findById(1L);
//    }
//
//    @Test
//    void updateMentor_shouldThrowResourceNotFoundException_whenTimeSlotNotFound() {
//
//        when(mentorRepository.findById(1L)).thenReturn(Optional.of(existingMentor));
//        when(certificateRepository.findById(1L)).thenReturn(Optional.of(existingCert));
//        when(experienceRepository.findById(1L)).thenReturn(Optional.of(existingExp));
//        when(fixedTimeSlotRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            mentorService.updateMentor(1L, updatedMentor);
//        });
//
//        verify(fixedTimeSlotRepository).findById(1L);
//    }
//
//    @Test
//    void updateMentor_shouldThrowUnexpectedServerException_whenUnexpectedErrorOccurs() {
//
//        when(mentorRepository.findById(1L)).thenReturn(Optional.of(existingMentor));
//        when(certificateRepository.findById(1L)).thenReturn(Optional.of(existingCert));
//        when(experienceRepository.findById(1L)).thenReturn(Optional.of(existingExp));
//        when(fixedTimeSlotRepository.findById(1L)).thenReturn(Optional.of(existingSlot1));
//        when(fixedTimeSlotRepository.findById(2L)).thenReturn(Optional.of(existingSlot2));
//        when(categoryRepository.findByName("Design")).thenReturn(Optional.of(updatedCat1));
//        when(categoryRepository.findByName("English")).thenReturn(Optional.of(updatedCat2));
//
//        when(mentorRepository.save(any(Mentor.class))).thenThrow(new NullPointerException("Unexpected null"));
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            mentorService.updateMentor(1L, updatedMentor);
//        });
//
//        assertTrue(exception.getMessage().contains("Error while updating mentors: "));
//    }
//
//    @Test
//    void deleteMentor_shouldDeleteSuccessfully_whenMentorExists() throws UnexpectedServerException, ResourceNotFoundException {
//
//        when(mentorRepository.findById(1L)).thenReturn(Optional.of(existingMentor));
//
//        // No need to stub deleteById or deleteByEmailId since they return void
//        CommonResponse<Mentor> response = mentorService.deleteMentor(1L);
//
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(MENTOR_DELETED_SUCCESSFULLY, response.getMessage());
//
//        verify(mentorRepository).findById(1L);
//        verify(mentorRepository).save(existingMentor);
//        verify(usersRepository).deleteByEmailId("old@mail.com");
//        verify(mentorRepository).deleteById(1L);
//    }
//
//    @Test
//    void deleteMentor_shouldThrowUnexpectedServerException_whenUnexpectedErrorOccurs() {
//        Long mentorId = 1L;
//
//        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(existingMentor));
//        when(mentorRepository.save(any(Mentor.class))).thenThrow(new NullPointerException("Unexpected null"));
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            mentorService.deleteMentor(mentorId);
//        });
//
//        assertTrue(exception.getMessage().contains(ERROR_DELETING_MENTORS));
//        verify(mentorRepository).findById(mentorId);
//    }
//
//    @Test
//    void deleteMentor_shouldThrowResourceNotFoundException_whenMentorNotFound() {
//        Long mentorId = 1L;
//
//        when(mentorRepository.findById(mentorId)).thenReturn(Optional.empty());
//
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            mentorService.deleteMentor(mentorId);
//        });
//
//        assertTrue(exception.getMessage().contains(MENTOR_NOT_FOUND_WITH_ID + mentorId));
//    }
//
//    @Test
//    void getVerifiedMentors_shouldReturnListOfMentors_whenDataExists() throws UnexpectedServerException {
//
//        VerifiedMentorDTO dto1 = mock(VerifiedMentorDTO.class);
//        lenient().when(dto1.getName()).thenReturn("Joe2");
//        lenient().when(dto1.getGender()).thenReturn("Male");
//        lenient().when(dto1.getAvatarUrl()).thenReturn("https://example.com/avatar.jpg");
//        lenient().when(dto1.getNumberOfMentoree()).thenReturn(20);
//        lenient().when(dto1.getCategoryNames()).thenReturn("Design, English");
//
//        List<VerifiedMentorDTO> mockList = List.of(dto1);
//
//        when(mentorRepository.findVerifiedMentors()).thenReturn(mockList);
//
//        CommonResponse<List<VerifiedMentorDTO>> response = mentorService.getVerifiedMentors();
//
//        //test
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(LOADED_ALL_VERIFIED_MENTOR_DETAILS, response.getMessage());
//        assertEquals(1, response.getData().size());
//
//        VerifiedMentorDTO returnedDto = response.getData().get(0);
//        assertEquals("Joe2", returnedDto.getName());
//        assertEquals("Male", returnedDto.getGender());
//        assertEquals("https://example.com/avatar.jpg", returnedDto.getAvatarUrl());
//        assertEquals(20, returnedDto.getNumberOfMentoree());
//        assertEquals("Design, English", returnedDto.getCategoryNames());
//
//        verify(mentorRepository).findVerifiedMentors();
//    }
//
//    @Test
//    void getVerifiedMentors_shouldReturnEmptyList_whenNoVerifiedMentorsExist() throws UnexpectedServerException {
//        when(mentorRepository.findVerifiedMentors()).thenReturn(List.of());
//
//        CommonResponse<List<VerifiedMentorDTO>> response = mentorService.getVerifiedMentors();
//
//        assertFalse(response.getStatus());
//        assertEquals(NO_VERIFIED_MENTORS_AVAILABLE, response.getMessage());
//        assertTrue(response.getData().isEmpty());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//
//        verify(mentorRepository).findVerifiedMentors();
//    }
//
//    @Test
//    void getVerifiedMentors_shouldThrowUnexpectedServerException_whenErrorOccurs() {
//        when(mentorRepository.findVerifiedMentors()).thenThrow(new RuntimeException("Database error"));
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            mentorService.getVerifiedMentors();
//        });
//
//        assertTrue(exception.getMessage().contains(ERROR_FETCHING_VERIFIED_MENTORS));
//    }
//
//    @Test
//    void getTopRatedMentors_shouldReturnListOfTopRatedMentors_whenDataExists() throws UnexpectedServerException {
//        TopRatedMentorsDTO dto1 = mock(TopRatedMentorsDTO.class);
//        lenient().when(dto1.getName()).thenReturn("Joe2");
//        lenient().when(dto1.getGender()).thenReturn("Male");
//        lenient().when(dto1.getAvatarUrl()).thenReturn("https://example.com/avatar.jpg");
//        lenient().when(dto1.getNumberOfMentoree()).thenReturn(20);
//        lenient().when(dto1.getCategoryNames()).thenReturn("Design, English");
//        lenient().when(dto1.getRate()).thenReturn(4.8);
//
//        List<TopRatedMentorsDTO> mockList = List.of(dto1);
//
//        when(mentorRepository.findTopRatedMentors()).thenReturn(mockList);
//
//        CommonResponse<List<TopRatedMentorsDTO>> response = mentorService.getTopRatedMentors();
//
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(LOADED_ALL_TOP_RATED_MENTORS_DETAILS, response.getMessage());
//        assertEquals(1, response.getData().size());
//
//        TopRatedMentorsDTO returnedDto = response.getData().get(0);
//        assertEquals("Joe2", returnedDto.getName());
//        assertEquals("Male", returnedDto.getGender());
//        assertEquals("https://example.com/avatar.jpg", returnedDto.getAvatarUrl());
//        assertEquals(20, returnedDto.getNumberOfMentoree());
//        assertEquals("Design, English", returnedDto.getCategoryNames());
//        assertEquals(4.8, returnedDto.getRate());
//
//        verify(mentorRepository).findTopRatedMentors();
//    }
//
//    @Test
//    void getTopRatedMentors_shouldReturnEmptyList_whenNoTopRatedMentorsExist() throws UnexpectedServerException {
//        when(mentorRepository.findTopRatedMentors()).thenReturn(List.of());
//
//        CommonResponse<List<TopRatedMentorsDTO>> response = mentorService.getTopRatedMentors();
//
//        assertFalse(response.getStatus());
//        assertEquals(NO_TOP_RATED_MENTORS_AVAILABLE, response.getMessage());
//        assertTrue(response.getData().isEmpty());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//
//        verify(mentorRepository).findTopRatedMentors();
//    }
//
//    @Test
//    void getTopRatedMentors_shouldThrowUnexpectedServerException_whenErrorOccurs() {
//        when(mentorRepository.findTopRatedMentors()).thenThrow(new RuntimeException("Database error"));
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            mentorService.getTopRatedMentors();
//        });
//
//        assertTrue(exception.getMessage().contains(ERROR_FETCHING_TOP_RATED_MENTORS));
//    }
//
//    @Test
//    void getTopMentors_shouldReturnListOfTopMentors_whenDataExists() throws UnexpectedServerException {
//        TopMentorsDTO dto1 = mock(TopMentorsDTO.class);
//        lenient().when(dto1.getName()).thenReturn("Joe2");
//        lenient().when(dto1.getGender()).thenReturn("Male");
//        lenient().when(dto1.getAvatarUrl()).thenReturn("https://example.com/avatar.jpg");
//        lenient().when(dto1.getNumberOfMentoree()).thenReturn(20);
//        lenient().when(dto1.getCategoryNames()).thenReturn("Design, English");
//        lenient().when(dto1.getId()).thenReturn(1L);
//        lenient().when(dto1.getRate()).thenReturn(4.8);
//
//        List<TopMentorsDTO> mockList = List.of(dto1);
//
//        when(mentorRepository.findTopMentors()).thenReturn(mockList);
//
//        CommonResponse<List<TopMentorsDTO>> response = mentorService.getTopMentors();
//
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(LOADED_ALL_TOP_MENTORS_DETAILS, response.getMessage());
//        assertEquals(1, response.getData().size());
//
//        TopMentorsDTO returnedDto = response.getData().get(0);
//        assertEquals("Joe2", returnedDto.getName());
//        assertEquals("Male", returnedDto.getGender());
//        assertEquals("https://example.com/avatar.jpg", returnedDto.getAvatarUrl());
//        assertEquals(20, returnedDto.getNumberOfMentoree());
//        assertEquals("Design, English", returnedDto.getCategoryNames());
//        assertEquals(4.8, returnedDto.getRate(), 0.01);
//        assertEquals(1L, returnedDto.getId());
//
//        verify(mentorRepository).findTopMentors();
//    }
//
//    @Test
//    void getTopMentors_shouldReturnEmptyList_whenNoTopMentorsExist() throws UnexpectedServerException {
//        when(mentorRepository.findTopMentors()).thenReturn(List.of());
//
//        CommonResponse<List<TopMentorsDTO>> response = mentorService.getTopMentors();
//
//        assertFalse(response.getStatus());
//        assertEquals(NO_TOP_MENTORS_AVAILABLE, response.getMessage());
//        assertTrue(response.getData().isEmpty());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//
//        verify(mentorRepository).findTopMentors();
//    }
//
//    @Test
//    void getTopMentors_shouldThrowUnexpectedServerException_whenErrorOccurs() {
//        when(mentorRepository.findTopMentors()).thenThrow(new RuntimeException("Database error"));
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            mentorService.getTopMentors();
//        });
//
//        assertTrue(exception.getMessage().contains(ERROR_FETCHING_TOP_MENTORS));
//    }
//
//    @Test
//    void searchMentors_shouldReturnListOfMentors_whenDataExists() throws UnexpectedServerException {
//        String keyword = "Design, Java";
//
//        SearchMentorsDTO dto1 = mock(SearchMentorsDTO.class);
//        lenient().when(dto1.getName()).thenReturn("Joe2");
//        lenient().when(dto1.getGender()).thenReturn("Male");
//        lenient().when(dto1.getAvatarUrl()).thenReturn("https://example.com/avatar.jpg");
//        lenient().when(dto1.getNumberOfMentoree()).thenReturn(20);
//        lenient().when(dto1.getCategoryNames()).thenReturn("Design, Java");
//        lenient().when(dto1.getId()).thenReturn(1L);
//        lenient().when(dto1.getRate()).thenReturn(4.8);
//        lenient().when(dto1.getRole()).thenReturn("Java Expert");
//
//        List<SearchMentorsDTO> mockList = List.of(dto1);
//
//        when(mentorRepository.searchMentors(any())).thenReturn(mockList);
//
//        CommonResponse<List<SearchMentorsDTO>> response = mentorService.searchMentors(keyword);
//
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(LOADED_ALL_MENTOR_DETAILS, response.getMessage());
//        assertEquals(1, response.getData().size());
//
//        SearchMentorsDTO returnedDto = response.getData().get(0);
//        assertEquals("Joe2", returnedDto.getName());
//        assertEquals("Male", returnedDto.getGender());
//        assertEquals("https://example.com/avatar.jpg", returnedDto.getAvatarUrl());
//        assertEquals(20, returnedDto.getNumberOfMentoree());
//        assertEquals("Design, Java", returnedDto.getCategoryNames());
//        assertEquals(4.8, returnedDto.getRate(), 0.01);
//        assertEquals(1L, returnedDto.getId());
//        assertEquals("Java Expert", returnedDto.getRole());
//
//        verify(mentorRepository).searchMentors(any());
//    }
//
//    @Test
//    void searchMentors_shouldReturnEmptyList_whenKeywordIsEmpty() throws UnexpectedServerException {
//        String keyword = "";
//
//        CommonResponse<List<SearchMentorsDTO>> response = mentorService.searchMentors(keyword);
//
//        assertFalse(response.getStatus());
//        assertEquals(NO_MENTORS_AVAILABLE, response.getMessage());
//        assertTrue(response.getData().isEmpty());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//    }
//
//    @Test
//    void searchMentors_shouldReturnEmptyList_whenKeywordIsNull() throws UnexpectedServerException {
//        String keyword = null;
//
//        CommonResponse<List<SearchMentorsDTO>> response = mentorService.searchMentors(keyword);
//
//        assertFalse(response.getStatus());
//        assertEquals(NO_MENTORS_AVAILABLE, response.getMessage());
//        assertTrue(response.getData().isEmpty());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//    }
//
//    @Test
//    void searchMentors_shouldReturnEmptyList_whenNoMentorsFound() throws UnexpectedServerException {
//        String keyword = "Design, Java";
//
//        when(mentorRepository.searchMentors(any())).thenReturn(List.of());
//
//        CommonResponse<List<SearchMentorsDTO>> response = mentorService.searchMentors(keyword);
//
//        assertFalse(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(NO_MENTORS_AVAILABLE, response.getMessage());
//        assertTrue(response.getData().isEmpty());
//
//        verify(mentorRepository).searchMentors(any());
//    }
//
//    @Test
//    void searchMentors_shouldThrowUnexpectedServerException_whenErrorOccurs() {
//        String keyword = "Design, Java";
//
//        when(mentorRepository.searchMentors(any())).thenThrow(new RuntimeException("Database error"));
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            mentorService.searchMentors(keyword);
//        });
//
//        assertTrue(exception.getMessage().contains(ERROR_FETCHING_MENTORS));
//    }
//
//
//}
