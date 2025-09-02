package com.example.biblioteca.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.biblioteca.domain.Emprestimo;
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {}
