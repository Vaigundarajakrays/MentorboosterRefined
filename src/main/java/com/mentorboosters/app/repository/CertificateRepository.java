package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
}
