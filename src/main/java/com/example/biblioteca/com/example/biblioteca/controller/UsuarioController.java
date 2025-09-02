package com.example.biblioteca.controller;
import com.example.biblioteca.domain.Usuario;
import com.example.biblioteca.repository.UsuarioRepository;
import com.example.biblioteca.exception.NotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
  private final UsuarioRepository repo;
  public UsuarioController(UsuarioRepository repo){ this.repo = repo; }

  @GetMapping
  public ResponseEntity<CollectionModel<EntityModel<Usuario>>> listar(){
    var models = repo.findAll().stream().map(this::toModel).toList();
    var self = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).listar()).withSelfRel();
    return ResponseEntity.ok(CollectionModel.of(models, self));
  }

  @GetMapping("/{id}")
  public EntityModel<Usuario> obter(@PathVariable Long id){
    var u = repo.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    return toModel(u);
  }

  @PostMapping
  public ResponseEntity<EntityModel<Usuario>> criar(@Valid @RequestBody Usuario usuario){
    var salvo = repo.save(usuario);
    return ResponseEntity.status(HttpStatus.CREATED).body(toModel(salvo));
  }

  @PutMapping("/{id}")
  public EntityModel<Usuario> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario dto){
    var u = repo.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    u.setNome(dto.getNome()); u.setEmail(dto.getEmail());
    return toModel(repo.save(u));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> remover(@PathVariable Long id){
    if(!repo.existsById(id)) throw new NotFoundException("Usuário não encontrado");
    repo.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private EntityModel<Usuario> toModel(Usuario u){
    var self = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).obter(u.getId())).withSelfRel();
    var update = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).atualizar(u.getId(), u)).withRel("update");
    var delete = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).remover(u.getId())).withRel("delete");
    return EntityModel.of(u, self, update, delete);
  }
}
