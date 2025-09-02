package com.example.biblioteca.exception;
import com.example.biblioteca.dto.ErrorResponse;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest req){
    var body = ErrorResponse.builder()
      .erro(ex.getMessage()).codigo(404).timestamp(Instant.now()).caminho(req.getRequestURI())
      .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req){
    var msg = ex.getBindingResult().getFieldErrors().stream()
      .map(f -> f.getField()+": "+f.getDefaultMessage()).findFirst().orElse("Dados inválidos");
    var body = ErrorResponse.builder().erro(msg).codigo(400).timestamp(Instant.now()).caminho(req.getRequestURI()).build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req){
    var body = ErrorResponse.builder().erro("Erro interno").codigo(500).timestamp(Instant.now()).caminho(req.getRequestURI()).build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
